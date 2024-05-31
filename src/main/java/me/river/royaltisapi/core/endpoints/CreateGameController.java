package me.river.royaltisapi.core.endpoints;

import me.river.royaltisapi.core.data.GameData;
import me.river.royaltisapi.core.data.json.JsonParser;
import me.river.royaltisapi.core.db.transfer.DataUploader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller handles the creation of new games.
 */
@RestController
public class CreateGameController {
    private static final Logger logger = LoggerFactory.getLogger(CreateGameController.class);

    /**
     * Creates a new game based on the provided JSON data.
     *
     * @param authorizationHeader The authorization header for authentication (optional).
     * @param json               The JSON string containing game data.
     * @return A ResponseEntity with the ID of the created game on success, or an error status.
     */
    @PostMapping("/creategame")
    public ResponseEntity<String> createMap(
            @RequestHeader(value = "Authorization") String authorizationHeader,
            @RequestBody String json
    ) {
        if (authorizationHeader != null && !authorizationHeader.isEmpty()) {
            logger.info("Received create game request with authorization header.");
            try {
                logger.debug("Parsing game data from JSON.");
                GameData gameData = JsonParser.parseJsonToGameData(json);
                DataUploader uploader = new DataUploader();
                int gameId = uploader.uploadGameData(gameData);
                logger.info("Game uploaded successfully with ID: {}", gameId);
                return ResponseEntity.status(HttpStatus.CREATED).body(String.valueOf(gameId));
            } catch (Exception e) {
                logger.error("Error creating game: {}", e.getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
            }
        } else {
            logger.warn("Create game request rejected due to missing authorization header.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization header is missing");
        }
    }
}
