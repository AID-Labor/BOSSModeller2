package de.snaggly.bossmodellerfx.model.adapter;

import de.bossmodeler.logicalLayer.elements.DBRelation;
import de.bossmodeler.logicalLayer.elements.DBTable;
import de.snaggly.bossmodellerfx.model.BOSSModel;

import java.util.LinkedList;

/**
 * Structure for legacy data model. To be used for DBInterface.
 *
 * @author Omar Emshani
 */
public class DBProjectHolder implements BOSSModel {
    private LinkedList<DBTable> dbTables;
    private LinkedList<DBRelation> dbRelations;

    public DBProjectHolder(LinkedList<DBTable> dbTables, LinkedList<DBRelation> dbRelations) {
        this.dbTables = dbTables;
        this.dbRelations = dbRelations;
    }

    public DBProjectHolder() {
        dbTables = new LinkedList<>();
        dbRelations = new LinkedList<>();
    }

    public LinkedList<DBTable> getDbTables() {
        return dbTables;
    }

    public void setDbTables(LinkedList<DBTable> dbTables) {
        this.dbTables = dbTables;
    }

    public LinkedList<DBRelation> getDbRelations() {
        return dbRelations;
    }

    public void setDbRelations(LinkedList<DBRelation> dbRelations) {
        this.dbRelations = dbRelations;
    }

    public void addDbTable(DBTable dbTable) {
        this.dbTables.add(dbTable);
    }

    public void addDbRelation(DBRelation dbRelation) {
        this.dbRelations.add(dbRelation);
    }
}
