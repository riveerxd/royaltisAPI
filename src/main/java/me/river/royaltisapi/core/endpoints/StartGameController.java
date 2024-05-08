package me.river.royaltisapi.core.endpoints;

import com.corundumstudio.socketio.BroadcastOperations;
import com.corundumstudio.socketio.SocketIOServer;
import com.google.gson.Gson;
import me.river.royaltisapi.core.Lobby;
import me.river.royaltisapi.core.User;
import me.river.royaltisapi.core.data.*;
import me.river.royaltisapi.core.db.DataRetriever;
import me.river.royaltisapi.core.managers.LobbyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static me.river.royaltisapi.core.game.Game.moveBordersTowardsMiddle;

@RestController
@DependsOn("socketIOServer")
public class StartGameController {
    private final SocketIOServer server;
    private final LobbyManager lobbyManager;
    @Autowired
    public StartGameController(SocketIOServer server, LobbyManager lobbyManager) {
        this.server = server;
        this.lobbyManager = lobbyManager;
    }
    @GetMapping("/start")
    public String startBroadcast(
            @RequestBody String body
    ) {
        System.out.println("recieved "+body);

        /* get req pattern
        {
            "gameId": 23,
            "count":  1000,
            "interval": 50,
            "lobbyCode":164895,
        }
         */
        Gson gson = new Gson();
        try{
            GameProps gameProps = gson.fromJson(body, GameProps.class);

            BroadcastOperations broadcastOperations = server.getBroadcastOperations();
            DataRetriever retriever = new DataRetriever();
            GameData data = retriever.retrieveGameData(gameProps.getGameId());
            Lobby curerntLobby = lobbyManager.getLobbyByLobbyCode(gameProps.getLobbyCode());
            System.out.println("Retrieved data: "+data);
            if (data == null){
                throw new RuntimeException("Data is null");
            }
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                int count = gameProps.getCount();
                ArrayList<Border> lastBorder = data.getBorders();
                @Override
                public void run() {
                    ArrayList<Border> updatedBorders = moveBordersTowardsMiddle(lastBorder, data.getMiddlePoint(), count);
                    lastBorder = updatedBorders;
                    for (User user : curerntLobby.getOnlineUsers()){
                        user.getClient().sendEvent("borders", gson.toJson(updatedBorders));
                        System.out.println("sending "+gson.toJson(updatedBorders)+" to "+user.getSocketSessionId());
                    }
                    count--;
                    if (count == 0){
                        timer.cancel();
                    }
                }
            }, 0, gameProps.getInterval());

        }catch (Exception e){
            e.printStackTrace();
        }
        return "Broadcast started!";
    }
}
