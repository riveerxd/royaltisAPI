package me.river.royaltisapi.core;

import com.corundumstudio.socketio.SocketIOClient;
import com.google.gson.JsonObject;
import io.netty.handler.codec.http.HttpHeaders;

import java.util.HashMap;

public class UserManager {
    private HashMap<String, Location> userLocations = new HashMap<>();

    public void handleLocationUpdate(SocketIOClient client, JsonObject jsonData){
        try{
            HttpHeaders httpHeaders = client.getHandshakeData().getHttpHeaders();
            String token = httpHeaders.get("User-Token");
            String latitude = jsonData.get("latitude").getAsString();
            String longitude = jsonData.get("longitude").getAsString();
            Location location = new Location(latitude, longitude);
            userLocations.put(token, location);
            System.out.println("Successfully updated location for user "+client.getSessionId());
            System.out.println(userLocations);
        }catch (Exception e){
            System.err.println("error updating user location: "+e.getMessage());
        }
    }
}
