package me.river.royaltisapi.core.endpoints;

import com.corundumstudio.socketio.BroadcastOperations;
import com.corundumstudio.socketio.SocketIOServer;
import com.google.gson.Gson;
import me.river.royaltisapi.core.data.records.Border;
import me.river.royaltisapi.core.data.records.GameProps;
import me.river.royaltisapi.core.game.Lobby;
import me.river.royaltisapi.core.game.User;
import me.river.royaltisapi.core.data.*;
import me.river.royaltisapi.core.db.DataRetriever;
import me.river.royaltisapi.core.managers.LobbyManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static me.river.royaltisapi.core.game.Game.moveBordersTowardsMiddle;

/**
 * Controller for starting a game.
 */
@RestController
@DependsOn("socketIOServer")
public class StartGameController {
    /**
     * The server.
     */
    private final SocketIOServer server;

    /**
     * The lobby manager.
     */
    private final LobbyManager lobbyManager;

    /**
     * Instantiates a new Start game controller.
     *
     * @param server the server
     * @param lobbyManager the lobby manager
     */
    @Autowired
    public StartGameController(SocketIOServer server, LobbyManager lobbyManager) {
        this.server = server;
        this.lobbyManager = lobbyManager;
    }

    /**
     * The Gson instance.
     */
    private Gson gson = new Gson();

    /**
     * Starts a game.
     *
     * @param body the body
     * @return the string
     */
    @PostMapping("/start")
    public String startBroadcast(
            @RequestBody String body
    ) {
        try{
            GameProps gameProps = gson.fromJson(body, GameProps.class);

            BroadcastOperations broadcastOperations = server.getBroadcastOperations();
            DataRetriever retriever = new DataRetriever();
            GameData data = retriever.retrieveGameData(gameProps.gameId());
            Lobby curerntLobby = lobbyManager.getLobbyByLobbyCode(gameProps.getLobbyCode());
            System.out.println("Retrieved data: "+data);
            if (data == null){
                throw new RuntimeException("Data is null");
            }
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                int count = gameProps.count();
                ArrayList<Border> lastBorder = data.getBorders();
                @Override
                public void run() {
                    ArrayList<Border> updatedBorders = moveBordersTowardsMiddle(lastBorder, data.getMiddlePoint(), count);
                    lastBorder = updatedBorders;
                    for (User user : curerntLobby.getOnlineUsers()){
                        user.getClient().sendEvent("borders", gson.toJson(updatedBorders));
                    }
                    count--;
                    if (count == 0){
                        timer.cancel();
                    }
                }
            }, 0, gameProps.interval());

        }catch (Exception e){
            e.printStackTrace();
        }
        return "Game started";
    }
}
