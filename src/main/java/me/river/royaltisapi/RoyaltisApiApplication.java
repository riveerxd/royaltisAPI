package me.river.royaltisapi;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * The main entry point for the Royaltis API application.
 */
@SpringBootApplication
public class RoyaltisApiApplication {
    private static final Logger logger = LogManager.getLogger(RoyaltisApiApplication.class);

    /**
     * The main method that starts the Spring Boot application.
     *
     * @param args The command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        logger.info("Starting Royaltis API application...");
        ConfigurableApplicationContext context = SpringApplication.run(RoyaltisApiApplication.class, args);
        logger.info("Server port: {}", context.getEnvironment().getProperty("server.port"));
        logger.info("Royaltis API application started successfully.");
    }
}
