package com.javaquery.spring.liquibase;

/**
 * TenantDataSource interface defines the necessary methods to retrieve database connection details for a tenant.
 *
 * @author vicky.thakor
 * @since 1.0.0
 */
public interface TenantDataSource {
    /**
     * Gets the unique identifier for the tenant. This is for connection pool naming and logging purposes.
     * @return the owner ID as a String
     */
    String getTenantName();

    /**
     * Gets the host of the tenant's database.
     * @return the host as a String
     */
    String getHost();
    /**
     * Gets the port of the tenant's database.
     * @return the port as an Integer
     */
    Integer getPort();
    /**
     * Gets the schema name of the tenant's database.
     * @return the schema name as a String
     */
    String getSchemaName();
    /**
     * Gets the username for the tenant's database. Recommended to be stored securely/encrypted.
     * @return the username as a String
     */
    String getUsername();
    /**
     * Gets the password for the tenant's database. Recommended to be stored securely/encrypted.
     * @return the password as a String
     */
    String getPassword();
    /**
     * Gets the JDBC connection URL for the tenant's database.
     * @return the connection URL as a String
     */
    String getDriver();
    /**
     * Gets the database dialect for the tenant's database.
     * @return the dialect as a String
     */
    String getDialect();

    default String connectionURL() {
        if (getDriver() == null) {
            return null;
        }
        String host = getHost();
        Integer port = getPort();
        String schemaName = getSchemaName();

        if (getDriver().toLowerCase().contains("mysql")) {
            return "jdbc:mysql://" + host + ":" + port + "/" + schemaName + "?createDatabaseIfNotExist=true";
        } else if (getDriver().toLowerCase().contains("postgresql")) {
            return "jdbc:postgresql://" + host + ":" + port + "/" + schemaName + "?createDatabaseIfNotExist=true";
        } else if (getDriver().toLowerCase().contains("sqlserver")) {
            return "jdbc:sqlserver://" + host + ":" + port + ";databaseName=" + schemaName
                    + "?createDatabaseIfNotExist=true";
        } else if (getDriver().toLowerCase().contains("oracle")) {
            return "jdbc:oracle:thin:@" + host + ":" + port + ":" + schemaName + "?createDatabaseIfNotExist=true";
        }
        return null;
    }
}
