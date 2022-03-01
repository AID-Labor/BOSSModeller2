package de.snaggly.bossmodellerfx.model.adapter;

import de.bossmodeler.dbInterface.MSSQLServerSchnittstelle;
import de.bossmodeler.dbInterface.MySQLSchnittstelle;
import de.bossmodeler.dbInterface.PostgreSQLSchnittstelle;
import de.bossmodeler.dbInterface.Schnittstelle;

/**
 * This class creates the SQLInterfaces given by the legacy DBInterface model.
 * Expand this class if you plan on introducing more supported DBMS.
 * @author Omar Emshani
 */
public class SQLInterface {
    private boolean isSchemaCompatible;
    private String defaultDBName;
    private String defaultPort;
    private final SQLLanguage language;

    private SQLInterface(SQLLanguage selectedLanguage) {
        this.language = selectedLanguage;

        switch (language) {
            case PostgreSQL:
                isSchemaCompatible = true;
                defaultPort = "5432";
                defaultDBName = "postgres";
                break;
            case Microsoft_SQL:
                isSchemaCompatible = true;
                defaultPort = "1433";
                defaultDBName = "";
                break;
            case MySQL:
                isSchemaCompatible = false;
                defaultPort = "3306";
                defaultDBName = "";
                break;
        }
    }

    public static Schnittstelle getDbDriverInterface(SQLLanguage language) {
        return getDbDriverInterface(language, null, null, null, null, null, null);
    }

    public static Schnittstelle getDbDriverInterface(SQLLanguage language, String host, String port, String dbName, String username, String password, String schema) {
        Schnittstelle dbInterface = null;
        switch (language) {
            case PostgreSQL:
                dbInterface = new PostgreSQLSchnittstelle(
                        host,
                        port,
                        dbName,
                        username,
                        password,
                        schema);
                break;
            case Microsoft_SQL:
                dbInterface = new MSSQLServerSchnittstelle(
                        host,
                        port,
                        dbName,
                        username,
                        password,
                        schema);
                break;
            case MySQL:
                dbInterface = new MySQLSchnittstelle(
                        host,
                        port,
                        dbName,
                        username,
                        password,
                        "");
                break;
        }

        return dbInterface;
    }

    public boolean isSchemaCompatible() {
        return isSchemaCompatible;
    }

    public String getDefaultDBName() {
        return defaultDBName;
    }

    public String getDefaultPort() {
        return defaultPort;
    }

    public SQLLanguage getLanguage() {
        return language;
    }

    public static SQLInterface getSQLInterfaceDescriptor(SQLLanguage selectedLanguage) {
        return new SQLInterface(selectedLanguage);
    }
}
