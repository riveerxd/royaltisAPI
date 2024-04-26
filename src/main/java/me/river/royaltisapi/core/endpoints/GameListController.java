package me.river.royaltisapi.core.endpoints;

import com.google.gson.Gson;
import me.river.royaltisapi.core.db.GamePreviewRetriever;
import me.river.royaltisapi.core.db.LoginCheck;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameListController {
    private Gson gson = new Gson();
    @GetMapping("/gamelist")
    public ResponseEntity gameList(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ){
        if (LoginCheck.checkLoginToken(authHeader)){
            GamePreviewRetriever gpr = new GamePreviewRetriever();
            Object[] gd = gpr.retrievePreview();
            return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(gd));
        }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }
}
