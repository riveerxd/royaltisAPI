package me.river.royaltisapi.core.endpoints;

import com.google.gson.Gson;
import me.river.royaltisapi.core.data.GameData;
import me.river.royaltisapi.core.data.records.GameId;
import me.river.royaltisapi.core.db.transfer.DataRetriever;
import me.river.royaltisapi.core.db.LoginCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * This controller handles requests to get initial game data for a specific game.
 */
@RestController
public class InitialDataController {
    private static final Logger logger = LoggerFactory.getLogger(InitialDataController.class);
    private final Gson gson = new Gson();

    /**
     * Handles POST requests to "/getinitialdata" to retrieve the initial data for a specific game.
     * Requires a valid authorization token in the header and a JSON body containing the GameId.
     *
     * @param authHeader The authorization token provided in the request header.
     * @param body       The JSON string containing the GameId of the requested game.
     * @return A ResponseEntity with the game data as a JSON object on success (HTTP 200),
     *         or an error response with a relevant status code (HTTP 401, 404, or 500).
     */
    @PostMapping("/getinitialdata")
    public ResponseEntity<String> getInitialData(
            @RequestHeader(value = "Authorization") String authHeader,
            @RequestBody String body
    ) {
        try {
            logger.info("Received request for initial game data with authorization header: {}", authHeader);

            // Validate authorization token
            if (LoginCheck.checkLoginToken(authHeader)) {
                logger.info("Authorization token validated successfully.");

                // Extract GameId from request body
                GameId gameId = gson.fromJson(body, GameId.class);
                logger.info("Game ID extracted from request body: {}", gameId.gameId());

                // Retrieve game data from the database
                DataRetriever retriever = new DataRetriever();
                GameData gameData = retriever.retrieveGameData(gameId.gameId());

                if (gameData != null) {
                    logger.info("Retrieved game data for game ID: {}", gameId.gameId());
                    return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(gameData));
                } else {
                    logger.warn("No game data found for game ID: {}", gameId.gameId());
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No game data found for the provided ID.");
                }
            } else {
                logger.warn("Invalid authorization token provided.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }
        } catch (Exception e) {
            logger.error("Error retrieving initial game data: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
