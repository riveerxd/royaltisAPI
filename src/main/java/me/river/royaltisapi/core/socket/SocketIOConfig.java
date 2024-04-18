package me.river.royaltisapi.core.socket;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.Configuration;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.corundumstudio.socketio.listener.DataListener;
import com.corundumstudio.socketio.listener.DisconnectListener;
import jakarta.annotation.PreDestroy;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.CrossOrigin;

@CrossOrigin(origins = "*")
@org.springframework.context.annotation.Configuration
public class SocketIOConfig {

    private String SOCKETHOST = "0.0.0.0";
    private int SOCKETPORT = 9090;
    public static SocketIOServer server;

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

        server.addEventListener("message", String.class, new DataListener<String>() {
            @Override
            public void onData(SocketIOClient client, String data, AckRequest ackRequest) throws Exception {
                return;
            }
        });

        return server;
    }

    public static void broadcastMessage(Object data) {
        System.out.println("message sent: "+data);
        server.getBroadcastOperations().sendEvent("broadcastMessage", data);
    }

    @PreDestroy
    public void stopSocketIOServer() {
        this.server.stop();
    }
}
