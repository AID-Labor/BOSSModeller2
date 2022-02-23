package de.snaggly.bossmodellerfx.model.adapter;

import de.bossmodeler.logicalLayer.elements.DBLogicalAdministration;
import de.snaggly.bossmodellerfx.model.BOSSModel;

/**
 * Model for structuring DBLogicalAdministration. To be used for DBInterface.
 *
 * @author Omar Emshani
 */
public class DBLAHolder implements BOSSModel {
    private DBLogicalAdministration dbla;
    private String host, port, db, user, pass, schema;
    private SQLLanguage language;

    public DBLAHolder(DBLogicalAdministration dbla, SQLLanguage language, String host, String port, String db, String user, String pass, String schema) {
        this.dbla = dbla;
        this.host = host;
        this.port = port;
        this.db = db;
        this.user = user;
        this.pass = pass;
        this.schema = schema;
        this.language = language;
    }

    public DBLogicalAdministration getDbla() {
        return dbla;
    }

    public void setDbla(DBLogicalAdministration dbla) {
        this.dbla = dbla;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public String getDb() {
        return this.db;
    }

    public void setDb(String db) {
        this.db = db;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }

    public SQLLanguage getLanguage() {
        return language;
    }

    public void setLanguage(SQLLanguage language) {
        this.language = language;
    }
}
