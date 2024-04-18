package me.river.royaltisapi.core.endpoints;

import com.google.gson.Gson;
import me.river.royaltisapi.core.User;
import me.river.royaltisapi.core.db.LoginCheck;
import me.river.royaltisapi.core.managers.TokenManager;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {
    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody String json
    ){
        try {
            Gson gson = new Gson();
            User user = gson.fromJson(json, User.class);
            if (LoginCheck.checkLogin(user)){
                String token = TokenManager.getUserToken(user);
                HttpHeaders respHeaders = new HttpHeaders();
                respHeaders.add("X-Token", token);
                return new ResponseEntity<>(respHeaders, HttpStatus.OK);
            }else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to parse json");
        }
    }
}