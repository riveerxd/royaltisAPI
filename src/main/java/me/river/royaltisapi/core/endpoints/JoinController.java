package me.river.royaltisapi.core.endpoints;

import com.google.gson.Gson;
import me.river.royaltisapi.core.data.records.GameId;
import me.river.royaltisapi.core.data.records.LobbyCode;
import me.river.royaltisapi.core.managers.LobbyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JoinController {
    @Autowired
    private LobbyManager lobbyManager;

    @PostMapping("/join")
    public ResponseEntity join(
            @RequestBody String body
    ){
        try{
            LobbyCode lobbyCode = gson.fromJson(body, LobbyCode.class);
            if (!lobbyManager.doesLobbyExist(lobbyCode)){
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid lobby code");
            }
            GameId wantedGameId = lobbyManager.getGameIdByLobbyCode(lobbyCode);
            return ResponseEntity.status(HttpStatus.OK).body(wantedGameId.gameId());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}
