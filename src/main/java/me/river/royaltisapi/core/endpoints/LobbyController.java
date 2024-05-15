package me.river.royaltisapi.core.endpoints;

import com.google.gson.Gson;
import me.river.royaltisapi.core.data.records.GameId;
import me.river.royaltisapi.core.data.records.LobbyCode;
import me.river.royaltisapi.core.db.DbUtils;
import me.river.royaltisapi.core.db.LoginCheck;
import me.river.royaltisapi.core.managers.LobbyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for joining a game.
 */
@RestController
@DependsOn("lobbyManager")
public class LobbyController {
    /**
     * The lobby manager.
     */
    private LobbyManager lobbyManager;

    /**
     * Instantiates a new Lobby controller.
     *
     * @param lobbyManager the lobby manager
     */
    @Autowired
    public LobbyController(LobbyManager lobbyManager) {
        this.lobbyManager = lobbyManager;
    }

    /**
     * The Gson instance.
     */
    private Gson gson = new Gson();
    /**
     * Tries to create a lobby, returns the lobby code of the created lobby.
     *
     * @param authorizationHeader the authorization header
     * @param body the body
     * @return the response entity
     */
    @PostMapping("/createlobby")
    public ResponseEntity<String> createLobby(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody String body
    ){
        if (LoginCheck.checkLoginToken(authorizationHeader)){
            try{
                GameId gameId = gson.fromJson(body, GameId.class);
                if (DbUtils.doesGameExist(gameId)){
                    String lobbyCode = lobbyManager.createLobby(gameId);
                    return ResponseEntity.status(HttpStatus.CREATED).body(lobbyCode);
                }else{
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Game id does not exist");
                }
            }catch (Exception e){
                e.printStackTrace();
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while creating lobby");
            }
        }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

    /**
     * Removes a lobby.
     *
     * @param authorizationHeader the authorization header
     * @param body the body
     * @return the response entity
     */
    @DeleteMapping("/removelobby")
    public ResponseEntity<String> removeLobby(
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader,
            @RequestBody String body
    ){
        if (LoginCheck.checkLoginToken(authorizationHeader)){
            try{
                LobbyCode lobbyCode = gson.fromJson(body, LobbyCode.class);
                lobbyManager.removeLobby(lobbyCode);
                return ResponseEntity.status(HttpStatus.OK).body("Lobby removed");
            }catch (Exception e){
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error while removing lobby");
            }
        }else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid token");
        }
    }

}
