package me.river.royaltisapi.core.socket;

import com.corundumstudio.socketio.SocketIOServer;
import jakarta.annotation.PreDestroy;
import me.river.royaltisapi.core.db.DBConnector;
import me.river.royaltisapi.core.exceptions.NullEnvironmentVariableException;
import me.river.royaltisapi.core.managers.LobbyManager;
import me.river.royaltisapi.core.managers.UserManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * This class provides the configuration for the SocketIO server and manages related beans.
 */
@Configuration
public class Config {
    private static final Logger logger = LoggerFactory.getLogger(Config.class);
    private final String socketHost = "0.0.0.0";
    private final int socketPort = 9090;

    /**
     * Creates a bean for the UserManager.
     *
     * @return A new instance of UserManager.
     */
    @Bean
    public UserManager userManager() {
        return new UserManager();
    }

    /**
     * Creates a bean for the LobbyManager.
     *
     * @return A new instance of LobbyManager.
     */
    @Bean
    public LobbyManager lobbyManager() {
        return new LobbyManager();
    }

    /**
     * Creates a bean for the SocketIOServerShutdown.
     *
     * @param server The SocketIOServer instance to manage.
     * @return A new instance of SocketIOServerShutdown.
     */
    @Bean
    public SocketIOServerShutdown socketIOServerShutdown(SocketIOServer server) {
        return new SocketIOServerShutdown(server);
    }

    /**
     * Creates and configures the SocketIOServer bean.
     * It sets up the host, port, and adds event listeners for client connections,
     * disconnections, item removal, and location updates.
     *
     * @return The configured SocketIOServer instance.
     */
    @Bean
    public SocketIOServer socketIOServer() {
        logger.info("Configuring SocketIO server on {}:{}", socketHost, socketPort);

        com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
        config.setHostname(socketHost);
        config.setPort(socketPort);

        logger.info("Checking database connection...");
        checkDb();
        logger.info("Database connection successful.");

        SocketIOServer server = new SocketIOServer(config);

        server.addEventListener("item_delete", Object.class, (socketIOClient, data, ackRequest) -> {
            logger.info("Received item_delete event from client: {}", socketIOClient.getSessionId());
            userManager().handleItemRemove(socketIOClient, data);
        });

        server.addEventListener("location_update", Object.class, (socketIOClient, data, ackRequest) -> {
            logger.info("Received location_update event from client: {}", socketIOClient.getSessionId());
            userManager().handleLocationUpdate(socketIOClient, data);
        });

        server.addConnectListener(client -> {
            logger.info("New client connected: {}", client.getSessionId());
            userManager().handleClientConnect(client);
        });

        server.addDisconnectListener(client -> {
            logger.info("Client disconnected: {}", client.getSessionId());
            userManager().handleClientDisconnect(client);
        });

        server.start();
        logger.info("SocketIO server started.");

        return server;
    }

    /**
     * Checks the database connection. If the connection fails, logs an error and exits the application.
     */
    private void checkDb() {
        try (Connection connection = DBConnector.getConnection()) {
            if (connection == null) {
                logger.error("Failed to establish database connection.");
                throw new RuntimeException("Could not connect to database");
            }
        } catch (SQLException e) {
            logger.error("SQL error during database check: {}", e.getMessage());
            System.exit(500);
        } catch (ClassNotFoundException e) {
            logger.error("Driver error during database check: {}", e.getMessage());
            System.exit(501);
        } catch (RuntimeException e) {
            logger.error("Database error during check: {}", e.getMessage());
            System.exit(502);
        } catch (NullEnvironmentVariableException e) {
            logger.error("Environment variable error during database check: {}", e.getMessage());
            System.exit(503);
        }
    }

    /**
     * This static inner class is responsible for stopping the SocketIO server when the application shuts down.
     */
    static class SocketIOServerShutdown {
        private final SocketIOServer server;

        public SocketIOServerShutdown(SocketIOServer server) {
            this.server = server;
        }

        @PreDestroy
        public void stopSocketIOServer() {
            if (server != null) {
                logger.info("Stopping SocketIO server.");
                server.stop();
            }
        }
    }
}
