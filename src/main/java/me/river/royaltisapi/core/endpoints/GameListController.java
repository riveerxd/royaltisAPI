package me.river.royaltisapi.core.endpoints;

import com.google.gson.Gson;
import me.river.royaltisapi.core.db.transfer.GamePreviewRetriever;
import me.river.royaltisapi.core.db.LoginCheck;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller handles requests to get a list of available games.
 */
@RestController
public class GameListController {
    private static final Logger logger = LoggerFactory.getLogger(GameListController.class);
    private final Gson gson = new Gson();

    /**
     * Handles GET requests to "/gamelist". It retrieves a list of available games,
     * including their details, borders, lootboxes, and middle points.
     *
     * @param authHeader The authorization token provided in the request header.
     * @return A ResponseEntity with the game list as a JSON object on success (HTTP 200),
     *         or an error response with a relevant status code.
     */
    @GetMapping("/gamelist")
    public ResponseEntity<String> gameList(@RequestHeader(value = "Authorization") String authHeader) {
        try {
            logger.info("Received game list request with authorization header: {}", authHeader);

            // Validate the authorization token
            if (LoginCheck.checkLoginToken(authHeader)) {
                logger.info("Token validated successfully.");

                // Retrieve game preview data from the database
                GamePreviewRetriever gpr = new GamePreviewRetriever();
                Object[] gameData = gpr.retrievePreview();

                logger.info("Retrieved game previews successfully.");
                return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(gameData));
            } else {
                logger.warn("Invalid authorization token provided.");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }
        } catch (Exception e) {
            logger.error("Error retrieving game list: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
