package me.river.royaltisapi.core.endpoints;

import com.google.gson.Gson;
import me.river.royaltisapi.core.data.GameData;
import me.river.royaltisapi.core.data.records.GameId;
import me.river.royaltisapi.core.db.DataRetriever;
import me.river.royaltisapi.core.db.LoginCheck;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for getting initial data.
 */
@RestController
public class InitialDataController {
    /**
     * The Gson instance.
     */
    Gson gson = new Gson();

    /**
     * Gets the initial data.
     *
     * @param authHeader the authorization header
     * @param body the body
     * @return the response entity
     */
    @PostMapping("/getinitialdata")
    public ResponseEntity getInitialData(
            @RequestHeader(value = "Authorization") String authHeader,
            @RequestBody String body
    ){
        try{
            if (LoginCheck.checkLoginToken(authHeader)){
                GameId gameId = gson.fromJson(body, GameId.class);
                DataRetriever retriever = new DataRetriever();
                GameData gameData = retriever.retrieveGameData(gameId.gameId());
                return ResponseEntity.status(HttpStatus.OK).body(gson.toJson(gameData));
            }else{
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
            }
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
    }
}
