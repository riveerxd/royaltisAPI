package me.river.royaltisapi.core.endpoints;

import com.google.gson.Gson;
import me.river.royaltisapi.core.game.User;
import me.river.royaltisapi.core.data.records.UserLogin;
import me.river.royaltisapi.core.db.LoginCheck;
import me.river.royaltisapi.core.managers.TokenManager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for logging in.
 */
@RestController
public class LoginController {
    /**
     * Logs in a user.
     *
     * @param json the json
     * @return the response entity
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody String json
    ){
        try {
            Gson gson = new Gson();
            UserLogin login = gson.fromJson(json, UserLogin.class);
            User user = new User(login.username(), login.password());
            if (LoginCheck.checkLogin(user)){
                String token = TokenManager.getUserToken(user);
                HttpHeaders respHeaders = new HttpHeaders();
                respHeaders.add("X-Token", token);
                return new ResponseEntity<>(respHeaders, HttpStatus.OK);
            }else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        }catch (IllegalArgumentException iae){
            iae.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }catch (Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to parse json");
        }
    }
}
