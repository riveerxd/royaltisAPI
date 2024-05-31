package me.river.royaltisapi.core.endpoints;

import com.google.gson.Gson;
import me.river.royaltisapi.core.data.records.GameId;
import me.river.royaltisapi.core.data.records.LobbyCode;
import me.river.royaltisapi.core.exceptions.LobbyNotFoundException;
import me.river.royaltisapi.core.managers.LobbyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller handles requests to join a game lobby.
 */
@RestController
public class JoinController {
    private static final Logger logger = LoggerFactory.getLogger(JoinController.class);

    @Autowired
    private LobbyManager lobbyManager;

    private final Gson gson = new Gson();

    /**
     * Handles POST requests to "/join". It attempts to join a game lobby using the provided lobby code.
     * If the lobby exists and the join is successful, it returns the GameId of the lobby.
     *
     * @param body The JSON string containing the lobby code.
     * @return A ResponseEntity with the GameId on success (HTTP 200),
     *         or an error response with a relevant status code (HTTP 403 or 500).
     */
    @PostMapping("/join")
    public ResponseEntity join(@RequestBody String body) {
        try {
            LobbyCode lobbyCode = gson.fromJson(body, LobbyCode.class);
            logger.info("Received join request with lobby code: {}", lobbyCode.lobbyCode());

            if (lobbyManager.doesLobbyExist(lobbyCode)) {
                logger.info("Lobby with code {} exists.", lobbyCode.lobbyCode());
                GameId wantedGameId = lobbyManager.getGameIdByLobbyCode(lobbyCode);
                logger.info("Returning game ID {} for lobby code {}.", wantedGameId.gameId(), lobbyCode.lobbyCode());
                return ResponseEntity.status(HttpStatus.OK).body(wantedGameId.gameId());
            } else {
                logger.warn("Lobby with code {} not found.", lobbyCode.lobbyCode());
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid lobby code");
            }
        } catch (LobbyNotFoundException e) {
            logger.error("Error retrieving game ID for lobby: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        } catch (Exception e) { // Catching general exceptions for unexpected errors
            logger.error("Unexpected error processing join request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
        }
    }
}
