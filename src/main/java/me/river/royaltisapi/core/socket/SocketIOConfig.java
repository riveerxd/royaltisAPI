package me.river.royaltisapi.core.socket;

import com.corundumstudio.socketio.*;
import jakarta.annotation.PreDestroy;
import me.river.royaltisapi.core.managers.LobbyManager;
import me.river.royaltisapi.core.managers.UserManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SocketIOConfig {
    private String socketHost = "0.0.0.0";
    private int socketPort = 9090;

    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(socketHost);
        config.setPort(socketPort);

        SocketIOServer server = new SocketIOServer(config);

        server.addEventListener("message", String.class, (client, data, ackRequest) -> {
            System.out.println("Message: "+data);
        });
        server.addConnectListener(client -> userManager().handleClientConnect(client));

        server.addDisconnectListener(client -> userManager().handleClientDisconnect(client));
        server.start();

        return server;
    }

    public static void broadcastMessage(Object data, SocketIOServer server) {
        System.out.println("Broadcasting message: " + data);
        server.getBroadcastOperations().sendEvent("broadcastMessage", data);
    }

    @Bean
    public UserManager userManager() {
        return new UserManager();
    }

    @Bean
    public LobbyManager lobbyManager(){
        return new LobbyManager();
    }

    @Bean
    public SocketIOServerShutdown socketIOServerShutdown(SocketIOServer server) {
        return new SocketIOServerShutdown(server);
    }

    static class SocketIOServerShutdown {

        private final SocketIOServer server;

        public SocketIOServerShutdown(SocketIOServer server) {
            this.server = server;
        }

        @PreDestroy
        public void stopSocketIOServer() {
            if (server != null) {
                server.stop();
            }
        }
    }
}
