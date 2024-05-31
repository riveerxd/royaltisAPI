package me.river.royaltisapi.core.endpoints;

import com.google.gson.Gson;
import me.river.royaltisapi.core.game.User;
import me.river.royaltisapi.core.data.records.UserLogin;
import me.river.royaltisapi.core.db.LoginCheck;
import me.river.royaltisapi.core.managers.TokenManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller handles user login requests.
 */
@RestController
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    /**
     * Handles POST requests to "/login" for user authentication.
     *
     * @param json The JSON string containing user credentials (username and password).
     * @return A ResponseEntity with an HTTP status code and a token header on success (HTTP 200),
     *         or an error response with a relevant status code (HTTP 401 or 500).
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody String json) {
        try {
            logger.info("Received login request.");

            Gson gson = new Gson();
            UserLogin login = gson.fromJson(json, UserLogin.class);
            User user = new User(login.username(), login.password());

            // Check user credentials
            if (LoginCheck.checkLogin(user)) {
                String token = TokenManager.getUserToken(user);
                HttpHeaders respHeaders = new HttpHeaders();
                respHeaders.add("X-Token", token);
                logger.info("Login successful for user: {}", user.getUsername());
                return new ResponseEntity<>(respHeaders, HttpStatus.OK);
            } else {
                logger.warn("Invalid credentials for user: {}", user.getUsername());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        } catch (IllegalArgumentException iae) {
            logger.error("Illegal argument during login: {}", iae.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        } catch (Exception e) {
            logger.error("Error processing login request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to parse JSON");
        }
    }
}
