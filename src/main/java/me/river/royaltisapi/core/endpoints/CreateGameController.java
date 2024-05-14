package me.river.royaltisapi.core.endpoints;

import me.river.royaltisapi.core.data.GameData;
import me.river.royaltisapi.core.data.json.JsonParser;
import me.river.royaltisapi.core.db.DataUploader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for creating a game.
 */
@RestController
public class CreateGameController {
    /**
     * Creates a new game.
     *
     * @param authorizationHeader the authorization header
     * @param json the json data
     * @return the response entity
     */
    @PostMapping("/creategame")
    public ResponseEntity<String> createMap(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody String json
    ){
        if (authorizationHeader != null && !authorizationHeader.isEmpty()){

            try {
                System.out.println(json);
                GameData gameData = JsonParser.parseJsonToGameData(json);
                DataUploader uploader = new DataUploader();
                int gameId = uploader.uploadGameData(gameData);
                System.out.println("Game uploaded: "+gameId);
                return ResponseEntity.status(HttpStatus.CREATED).body(String.valueOf(gameId));
            }catch (Exception e){
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal server error");
            }
        }else{
            System.out.println("Missing auth header");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authorization header is missing");
        }

    }
}
