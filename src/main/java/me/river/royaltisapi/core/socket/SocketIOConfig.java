package me.river.royaltisapi.core.socket;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import io.netty.handler.codec.http.HttpHeaders;
import jakarta.annotation.PreDestroy;
import me.river.royaltisapi.core.UserManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin
@org.springframework.context.annotation.Configuration
public class SocketIOConfig {

    private String SOCKETHOST = "0.0.0.0";
    private int SOCKETPORT = 9090;
    private final Gson gson = new Gson(); // Initialize Gson instance
    private SocketIOServer server;

    @Bean
    public SocketIOServer socketIOServer() {
        Configuration config = new Configuration();
        config.setHostname(SOCKETHOST);
        config.setPort(SOCKETPORT);
        server = new SocketIOServer(config);
        server.start();

        server.addConnectListener(new ConnectListener() {
            @Override
            public void onConnect(SocketIOClient client) {
                System.out.println("new user connected with socket " + client.getSessionId());
            }
        });

        server.addDisconnectListener(new DisconnectListener() {
            @Override
            public void onDisconnect(SocketIOClient client) {
                client.getNamespace().getAllClients().stream().forEach(data -> {
                    System.out.println("user disconnected " + data.getSessionId().toString());
                });
            }
        });

        // Add DataListener with content type check
        server.addEventListener("message", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String data, AckRequest ackRequest) throws Exception {
                HttpHeaders headers = client.getHandshakeData().getHttpHeaders();
                String contentType = headers.get("Content-Type");
                String messageType = headers.get("Message-Type");
                 if (messageType != null && messageType.equalsIgnoreCase("location-update")) {
                    UserManager userManager = new UserManager();
                    try {
                        // Parse JSON using Gson and log it
                        JsonObject messageObject = gson.fromJson(data, JsonObject.class);
                        System.out.println("Client " + client.getSessionId() + " sent message: " + messageObject);
                        userManager.handleLocationUpdate(client, messageObject);
                    } catch (Exception e) {
                        System.err.println("Error parsing JSON message: " + e.getMessage());
                    }
                } else {
                    System.out.println("Client " + client.getSessionId() + " sent message (not JSON): " + data);
                }
            }
        });

        return server;
    }

    @PreDestroy
    public void stopSocketIOServer() {
        this.server.stop();
    }
}
