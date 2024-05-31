package me.river.royaltisapi.core.endpoints;

import com.corundumstudio.socketio.SocketIOServer;
import com.google.gson.Gson;
import me.river.royaltisapi.core.data.GameData;
import me.river.royaltisapi.core.data.records.Border;
import me.river.royaltisapi.core.data.records.GameProps;
import me.river.royaltisapi.core.game.Lobby;
import me.river.royaltisapi.core.game.User;
import me.river.royaltisapi.core.db.transfer.DataRetriever;
import me.river.royaltisapi.core.managers.LobbyManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static me.river.royaltisapi.core.game.Game.moveBordersTowardsMiddle;

/**
 * This controller handles requests to start a game.
 */
@RestController
@DependsOn("socketIOServer")
public class StartGameController {
    private static final Logger logger = LoggerFactory.getLogger(StartGameController.class);

    private final SocketIOServer server;
    private final LobbyManager lobbyManager;
    private final Gson gson = new Gson();

    @Autowired
    public StartGameController(SocketIOServer server, LobbyManager lobbyManager) {
        this.server = server;
        this.lobbyManager = lobbyManager;
    }

    /**
     * Handles POST requests to "/start". Starts a game broadcast by initiating a timer
     * that periodically sends updated border information to connected clients.
     *
     * @param authorizationHeader The authorization header for user authentication.
     * @param body The request body containing game properties (GameProps).
     * @return A ResponseEntity with a success message on successful game start (HTTP 200),
     *         or an error response with a relevant status code (HTTP 401 or 500).
     */
    @PostMapping("/start")
    public ResponseEntity<String> startBroadcast(
            @RequestHeader(value = "Authorization") String authorizationHeader,
            @RequestBody String body
    ) {
        if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
            logger.info("Received start game request with authorization header.");
            try {
                GameProps gameProps = gson.fromJson(body, GameProps.class);
                logger.debug("Game properties: {}", gameProps);

                // Retrieve game data from database
                DataRetriever retriever = new DataRetriever();
                GameData data = retriever.retrieveGameData(gameProps.gameId());
                logger.info("Retrieved game data for game ID: {}", gameProps.gameId());

                // Retrieve lobby information
                Lobby currentLobby = lobbyManager.getLobbyByLobbyCode(gameProps.getLobbyCode());
                logger.info("Retrieved lobby with code: {}", gameProps.getLobbyCode());

                if (data == null) {
                    logger.error("Game data not found for ID: {}", gameProps.gameId());
                    throw new RuntimeException("Data is null");
                }

                // Start the game loop timer
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    int count = gameProps.count();
                    ArrayList<Border> lastBorder = data.getBorders();

                    @Override
                    public void run() {
                        logger.debug("Moving borders towards middle...");
                        ArrayList<Border> updatedBorders = moveBordersTowardsMiddle(lastBorder, data.getMiddlePoint(), count);
                        lastBorder = updatedBorders;

                        // Send updated borders to each user in the lobby
                        for (User user : currentLobby.getOnlineUsers()) {
                            logger.debug("Sending updated borders to user: {}", user.getUsername());
                            user.getClient().sendEvent("borders", gson.toJson(updatedBorders));
                        }

                        count--;
                        if (count == 0) {
                            logger.info("Border shrinking complete.");
                            timer.cancel();
                        }
                    }
                }, 0, gameProps.interval());

                logger.info("Game started successfully.");
                return ResponseEntity.status(HttpStatus.OK).body("Game started");
            } catch (Exception e) {
                logger.error("Error starting game: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        } else {
            logger.warn("Start game request rejected due to missing authorization header.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization header is missing");
        }
    }
}
