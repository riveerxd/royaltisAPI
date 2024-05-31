package me.river.royaltisapi.core.data;

import me.river.royaltisapi.core.exceptions.NullEnvironmentVariableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages access keys and connection details for the database, primarily by retrieving
 * them from environment variables. Provides default values if environment variables
 * are not set.
 */
public class AccessKeys {
    private static final Logger logger = LoggerFactory.getLogger(AccessKeys.class);
    private static final String DEFAULT_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DEFAULT_URL = "jdbc:mysql://localhost:3306/royaltis";

    /**
     * Retrieves the database driver class from the environment variable "ROYALTIS_DB_DRIVER".
     * If the variable is not set, returns the default driver class.
     *
     * @return the database driver class
     */
    public String getDriverClass() {
        String driver = System.getenv("ROYALTIS_DB_DRIVER");
        logger.info("Database driver class requested, using: {}", driver != null ? driver : DEFAULT_DRIVER);
        return driver != null ? driver : DEFAULT_DRIVER;
    }

    /**
     * Retrieves the database URL from the environment variable "ROYALTIS_DB_URL".
     * If the variable is not set, returns the default database URL.
     *
     * @return the database URL
     */
    public String getDatabaseURL() {
        String url = System.getenv("ROYALTIS_DB_URL");
        logger.info("Database URL requested, using: {}", url != null ? url : DEFAULT_URL);
        return url != null ? url : DEFAULT_URL;
    }

    /**
     * Retrieves the database username from the environment variable "ROYALTIS_DB_USER".
     *
     * @return the database username
     * @throws NullEnvironmentVariableException if the environment variable is not set
     */
    public String getUsername() throws NullEnvironmentVariableException {
        String user =  System.getenv("ROYALTIS_DB_USER");
        if (user == null){
            logger.error("Environment variable ROYALTIS_DB_USER is null");
            throw new NullEnvironmentVariableException("Environment variable ROYALTIS_DB_USER is null");
        }else{
            logger.info("Database username retrieved successfully.");
            return user;
        }
    }

    /**
     * Retrieves the database password from the environment variable "ROYALTIS_DB_PASS".
     *
     * @return the database password
     * @throws NullEnvironmentVariableException if the environment variable is not set
     */
    public String getPassword() throws NullEnvironmentVariableException {
        String pass =  System.getenv("ROYALTIS_DB_PASS");
        if (pass == null){
            logger.error("Environment variable ROYALTIS_DB_PASS is null");
            throw new NullEnvironmentVariableException("Environment variable ROYALTIS_DB_PASS is null");
        }else{
            logger.info("Database password retrieved successfully (value masked for security).");
            return pass;
        }
    }
}
