package me.river.royaltisapi.core.endpoints;

import com.corundumstudio.socketio.BroadcastOperations;
import com.corundumstudio.socketio.SocketIOServer;
import com.google.gson.Gson;
import me.river.royaltisapi.core.data.*;
import me.river.royaltisapi.core.db.DataRetriever;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static me.river.royaltisapi.core.game.Game.moveBordersTowardsMiddle;

@RestController
public class StartGame {
    @Autowired
    private SocketIOServer server;
    @GetMapping("/start")
    public String startBroadcast(
            @RequestBody String body
    ) {
        /* get req pattern
        {
            "gameId": 23,
            "count":  1000,
            "interval": 50
        }
         */
        Gson gson = new Gson();
        try{
            GameProps gameProps = gson.fromJson(body, GameProps.class);

            BroadcastOperations broadcastOperations = server.getBroadcastOperations();
            DataRetriever retriever = new DataRetriever();
            GameData data = retriever.retrieveGameData(gameProps.getGameId());

            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                int count = gameProps.getCount();
                ArrayList<Border> lastBorder = data.getBorders();
                @Override
                public void run() {
                    ArrayList<Border> updatedBorders = moveBordersTowardsMiddle(lastBorder, new MiddlePoint((new Coordinates(50.07305079677018, 14.425703412853181))), count);
                    lastBorder = updatedBorders;
                    broadcastOperations.sendEvent("borders", gson.toJson(updatedBorders));
                    count--;
                    if (count == 0){
                        timer.cancel();
                    }
                }
            }, 0, gameProps.getInterval());

        }catch (Exception e){
            System.err.println(e.getMessage());
        }
        return "Broadcast started!";
    }
}
