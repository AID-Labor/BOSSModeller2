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
    public static Schnittstelle getDBInterface(SQLLanguage selectedLanguage) {
        return getDBInterface(selectedLanguage, null, null, null, null, null, null);
    }

    public static Schnittstelle getDBInterface(SQLLanguage selectedLanguage, String host, String port, String dbName, String username, String password, String schema) {
        Schnittstelle result = null;
        switch (selectedLanguage) {
            case PostgreSQL:
                result = new PostgreSQLSchnittstelle(
                        host,
                        port,
                        dbName,
                        username,
                        password,
                        schema);
                break;
            case MSSQL:
                result = new MSSQLServerSchnittstelle(
                        host,
                        port,
                        dbName,
                        username,
                        password,
                        schema);
                break;
            case MySQL:
                result = new MySQLSchnittstelle(
                        host,
                        port,
                        dbName,
                        username,
                        password,
                        "");
                break;
        }
        return result;
    }
}
