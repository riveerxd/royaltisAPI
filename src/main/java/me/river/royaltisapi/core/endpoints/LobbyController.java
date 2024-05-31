package me.river.royaltisapi.core.endpoints;

import com.google.gson.Gson;
import me.river.royaltisapi.core.data.records.GameId;
import me.river.royaltisapi.core.data.records.LobbyCode;
import me.river.royaltisapi.core.db.DbUtils;
import me.river.royaltisapi.core.db.LoginCheck;
import me.river.royaltisapi.core.managers.LobbyManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * This controller handles the creation and removal of game lobbies.
 */
@RestController
@DependsOn("lobbyManager")
public class LobbyController {
    private static final Logger logger = LoggerFactory.getLogger(LobbyController.class);
    private final LobbyManager lobbyManager;
    private final Gson gson = new Gson();

    @Autowired
    public LobbyController(LobbyManager lobbyManager) {
        this.lobbyManager = lobbyManager;
    }

    /**
     * Creates a new lobby for the specified game.
     *
     * @param authorizationHeader The authorization header for user authentication.
     * @param body The request body containing the GameId for which to create a lobby.
     * @return A ResponseEntity with the newly created lobby code on success (HTTP 201),
     *         or an error response with a relevant status code (HTTP 401 or 500).
     */
    @PostMapping("/createlobby")
    public ResponseEntity<String> createLobby(
            @RequestHeader(value = "Authorization") String authorizationHeader,
            @RequestBody String body
    ) {
        logger.info("Received create lobby request with authorization header: {}", authorizationHeader);
        try {
            if (LoginCheck.checkLoginToken(authorizationHeader)) {
                logger.info("Authorization token validated successfully.");
                try {
                    GameId gameId = gson.fromJson(body, GameId.class);
                    logger.info("Game ID extracted from request body: {}", gameId.gameId());
                    if (DbUtils.doesGameExist(gameId)) {
                        String lobbyCode = lobbyManager.createLobby(gameId);
                        logger.info("Lobby created successfully with code: {}", lobbyCode);
                        return ResponseEntity.status(HttpStatus.CREATED).body(lobbyCode);
                    } else {
                        logger.warn("Game with ID {} does not exist.", gameId.gameId());
                        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Game id does not exist");
                    }
                } catch (Exception e) {
                    logger.error("Error while creating lobby: {}", e.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while creating lobby");
                }
            } else {
                logger.warn("Invalid authorization token provided.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }
        } catch (Exception e) {
            logger.error("Error during authentication for lobby creation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Removes a lobby with the specified lobby code.
     *
     * @param authorizationHeader The authorization header for user authentication.
     * @param body The request body containing the LobbyCode of the lobby to remove.
     * @return A ResponseEntity with a success message on success (HTTP 200),
     *         or an error response with a relevant status code (HTTP 401 or 500).
     */
    @DeleteMapping("/removelobby")
    public ResponseEntity<String> removeLobby(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody String body
    ) {
        logger.info("Received remove lobby request with authorization header: {}", authorizationHeader);
        try {
            if (LoginCheck.checkLoginToken(authorizationHeader)) {
                logger.info("Authorization token validated successfully.");
                try {
                    LobbyCode lobbyCode = gson.fromJson(body, LobbyCode.class);
                    logger.info("Lobby code extracted from request body: {}", lobbyCode.lobbyCode());
                    lobbyManager.removeLobby(lobbyCode);
                    logger.info("Lobby with code {} removed successfully.", lobbyCode.lobbyCode());
                    return ResponseEntity.status(HttpStatus.OK).body("Lobby removed");
                } catch (Exception e) {
                    logger.error("Error while removing lobby: {}", e.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while removing lobby");
                }
            } else {
                logger.warn("Invalid authorization token provided.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }
        } catch (Exception e) {
            logger.error("Error during authentication for lobby removal: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
