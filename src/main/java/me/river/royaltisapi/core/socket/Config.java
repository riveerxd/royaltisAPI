package me.river.royaltisapi.core.socket;

import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PreDestroy;
import me.river.royaltisapi.core.db.DBConnector;
import me.river.royaltisapi.core.exceptions.NullEnvironmentVariableException;
import me.river.royaltisapi.core.managers.LobbyManager;
import me.river.royaltisapi.core.managers.UserManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.SQLException;

@Configuration
public class Config {
    private String socketHost = "0.0.0.0";
    private int socketPort = 9090;

    @Bean
    public UserManager userManager() {
        return new UserManager();
    }

    @Bean
    public LobbyManager lobbyManager() {
        return new LobbyManager();
    }

    @Bean
    public SocketIOServerShutdown socketIOServerShutdown(SocketIOServer server) {
        return new SocketIOServerShutdown(server);
    }


    @Bean
    public SocketIOServer socketIOServer() {
        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(socketHost);
        config.setPort(socketPort);

        checkDb();
        SocketIOServer server = new SocketIOServer(config);

        server.addEventListener("item_delete", Object.class, ((socketIOClient, data, ackRequest) -> {
            userManager().handleItemRemove(socketIOClient, data);
        }));

        server.addEventListener("location_update", Object.class, ((socketIOClient, data, ackRequest) -> {
            userManager().handleLocationUpdate(socketIOClient, data);
        }));
        server.addConnectListener(client -> userManager().handleClientConnect(client));

        server.addDisconnectListener(client -> userManager().handleClientDisconnect(client));
        server.start();

        return server;
    }

    public void checkDb() {
        try {
            Connection connection = DBConnector.getConnection();
            if (connection == null) throw new RuntimeException("Couldnt connect to database");
        } catch (SQLException e) {
            System.err.println("SQL error: " + e.getMessage());
            System.exit(500);
        } catch (ClassNotFoundException e) {
            System.err.println("Driver error: " + e.getMessage());
            System.exit(501);
        } catch (RuntimeException e) {
            System.err.println("Database error: " + e.getMessage());
            System.exit(502);
        } catch (NullEnvironmentVariableException e) {
            System.err.println("Environment variable error: "+ e.getMessage());
            System.exit(503);
        }
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
