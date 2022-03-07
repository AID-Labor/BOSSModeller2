package de.bossmodeler.dbInterface;

/**
 * This abstract class models a backbone for different database languages.
 * It allows to create a connection to the database and to work with it.
 *
 * @author Serdar Nurgün
 * @version 1.0.3
 * <p>
 * Since 1.0.0 updated javadoc annotations. NW
 * Since 1.0.1 added abstract getMetaData. SH
 * Since 1.0.2 added some methods from LogicalAdministration. SH
 */

import java.sql.*;
import java.util.LinkedList;

import de.bossmodeler.logicalLayer.elements.DBTable;

/**
 * The Class Schnittstelle.
 */
public abstract class Schnittstelle {

    /**
     *  connection object of Connection.
     *
     * @see Connection
     */
    protected Connection connection = null;

    /**  host address. */
    private String host;

    /**  port. */
    private String port;

    /**  database. */
    private String db;

    /**  user name. */
    private String user;

    /**  password. */
    private String pass;

    /**  database schema. */
    private String schema;


    /**
     * Attempts to establish a connection to the given database URL.
     *
     *
     * @throws SQLException the SQL exception
     */
    public abstract void openConnection() throws SQLException;

    /**
     * Attempts to close a existing connection to the given database URL.
     * @throws SQLException
     */
    public void closeConnection() throws SQLException{
        this.connection.close();
    }

    /**
     * Loads JDBC driver.
     */
    public abstract void loadJdbcDriver();

    /**
     * Executes the given SQL statement, which returns a single ResultSet object.
     *
     * @param cmd SQL statement
     * @return ResultSet object
     * @see		ResultSet
     */
    public abstract ResultSet query(String cmd) throws SQLException;

    /**
     * Sets connection.
     *
     * @param connection the new connection
     * @see 	Connection
     */
    abstract void setConnection(Connection connection);

    /**
     * Returns object of <code>Connection</code>.
     *
     * @return object of Connection
     * @see		Connection
     */
    abstract Connection getConnection();

    /**
     * Executes a command on the database.
     *
     * @param cmd the cmd
     * @return boolean command executed successfully
     * @throws SQLException the SQL exception
     */
    abstract public void execute(String cmd) throws SQLException;

    /**
     * Returns Metadata of Database.
     *
     * @return DatabaseMetaData
     * @throws SQLException the SQL exception
     */
    public abstract DatabaseMetaData getMetaData() throws SQLException;

    /**
     * Returns SQL-Code for given Tables.
     *
     * @param tables the tables
     * @param caseSensitive the case sensitive
     * @param createSchema the create schema
     * @param schemaName the schema name
     * @return String SQL-Code from given Tables
     */
    public abstract String generateSQLCodeFromTables(LinkedList<DBTable> tables, boolean caseSensitive, boolean createSchema, String schemaName);

    /**
     * Initialize tables.
     *
     * @return the linked list
     * @throws SQLException the SQL exception
     */
    public abstract LinkedList<DBTable> initializeTables() throws SQLException;

    /**
     * Gets the host.
     *
     * @return the host
     */
    protected String getHost() {
        return host;
    }

    /**
     * Gets the port.
     *
     * @return the port
     */
    protected String getPort() {
        return port;
    }

    /**
     * Gets the db.
     *
     * @return the db
     */
    protected String getDb() {
        return db;
    }

    /**
     * Gets the user.
     *
     * @return the user
     */
    protected String getUser() {
        return user;
    }

    /**
     * Gets the pass.
     *
     * @return the pass
     */
    protected String getPass() {
        return pass;
    }

    /**
     * Gets the schema.
     *
     * @return the schema
     */
    protected String getSchema() {
        return schema;
    }

    /**
     * Sets the host.
     *
     * @param host the host to set
     */
    public void setHost(String host) {
        this.host = host;
    }

    /**
     * Sets the port.
     *
     * @param port the port to set
     */
    public void setPort(String port) {
        this.port = port;
    }

    /**
     * Sets the db.
     *
     * @param db the db to set
     */
    public void setDb(String db) {
        this.db = db;
    }

    /**
     * Sets the user.
     *
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * Sets the pass.
     *
     * @param pass the pass to set
     */
    public void setPass(String pass) {
        this.pass = pass;
    }

    /**
     * Sets the schema.
     *
     * @param schema the schema to set
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * Reestablish connection.
     *
     * @throws SQLException the SQL exception
     */
    public  void reestablishConnection() throws SQLException{
        if (!getUser().equals("") && !getHost().equals("")
                && !getPort().equals("") && !getDb().equals("")) {
            openConnection();
        } else
            throw new SQLException("Server data not set!");
    }

    /**
     * Gets the table names.
     *
     * @return the table names
     * @throws SQLException the SQL exception
     */
    public abstract LinkedList<String> getTableNames() throws SQLException;

    /**
     * Gets the database.
     *
     * @return the database
     * @throws SQLException the SQL exception
     */
    public abstract LinkedList<String> getDatabase() throws SQLException;

    /**
     * Returns all Databases of Connection
     * @return LinkedList with all database names
     * @throws SQLException if connection failes
     */
    public abstract LinkedList<String> getAllDatabase() throws SQLException;

    /**
     * Returns all schemata of database
     * @return LinkedList with all schemata names
     * @throws SQLException if connection failes
     */
    public abstract LinkedList<String> getDBSchemata() throws SQLException;

    /**
     * Returns all schemata of database
     * @param db database name
     * @return LinkedList with all schemata names
     * @throws SQLException if connection failes
     */
    public abstract LinkedList<String> getDBSchemata(String db)throws SQLException;

    /**
     * Returns if given schema is empty
     *
     * @param schema String with schema to test
     * @return boolean true if schema is empty, else false
     */
    public abstract boolean schemaEmpty(String schematest) throws SQLException;

    /**
     * returns all schemata from given db
     * @param db
     * @return LinkedList with schemata names
     * @throws SQLException if connection fails
     */
    public abstract LinkedList<String> getAllDBSchemata(String db) throws SQLException;

    /**
     * Writes given tables to database.
     * @param tables the tables to be written
     * @param database the database where the tables will be created in
     * @param schema2 the schema where the tables will be created in
     * @param css if case sensitiv or not
     * @param dbExists if the aimed database already exists
     * @param newSchema if a new schema has to be created or not
     * @throws SQLException if connection fails
     */
    public abstract void writeTablesToDB(LinkedList<DBTable> tables,String database, String schema2, boolean css,
                                         boolean dbExists, boolean newSchema) throws SQLException;

    /**
     * switches the database to the new given one and reestablishes the connection
     * @param sdb the database to be switched to
     * @throws SQLException if connection fails
     */
    public void switchDB(String sdb) throws SQLException {
        setDb(sdb);
        reestablishConnection();
    }

    /**
     *  Sets new given database connection properties and reestablishes connection
     * @param user username
     * @param pass password
     * @param host host
     * @param port port
     * @param sdb database to be switched to
     * @param schema schema
     * @throws SQLException if connection fails
     */
    public void switchDB(String user, String pass, String host, String port,
                         String sdb, String schema) throws SQLException {
        setHost(host);
        setPort(port);
        setDb(sdb);
        setUser(user);
        setPass(pass);
        setSchema(schema);
        reestablishConnection();
    }



}
