package me.river.royaltisapi.core.data;

/**
 * Contains the access keys for the database.
 */
public class AccessKeys {
    /**
     * The driver class for the database.
     */
    public String getDRIVER_CLASS() {
        String driver = System.getenv("royaltis_db_driver");
        return driver != null ? driver : "com.mysql.cj.jdbc.Driver";
    }
    /**
     * The URL of the database.
     */
    public String getDATABASE_URL() {
        String url = System.getenv("royaltis_db_url");
        return url != null ? url : "jdbc:mysql://localhost:3306/royaltis";

    }
    /**
     * The username for the database.
     */
    public String getUSERNAME() {
        return System.getenv("royaltis_db_user");
    }

    /**
     * The password for the database.
     */
    public String getPASSWORD() {
        return System.getenv("royaltis_db_pass");
    }
}
