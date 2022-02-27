package de.snaggly.bossmodellerfx.model.subdata;

import de.snaggly.bossmodellerfx.model.abstraction.RelationAbstraction;
import de.snaggly.bossmodellerfx.model.view.Entity;
import de.snaggly.bossmodellerfx.relation_logic.CrowsFootOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Model for a Relation between two Entities.
 *
 * @author Omar Emshani
 */
public class Relation extends RelationAbstraction {
    private Entity tableA;
    private Entity tableB;
    private final HashMap<Entity,LinkedList<Attribute>> foreignKeys = new HashMap<>();

    public Relation(Entity tableA, Entity tableB, CrowsFootOptions.Cardinality tableA_Cardinality, CrowsFootOptions.Cardinality tableB_Cardinality, CrowsFootOptions.Obligation tableA_Obligation, CrowsFootOptions.Obligation tableB_Obligation) {
        super(tableA_Cardinality, tableB_Cardinality, tableA_Obligation, tableB_Obligation);
        this.tableA = tableA;
        this.tableB = tableB;
        foreignKeys.put(tableA, new LinkedList<>());
        foreignKeys.put(tableB, new LinkedList<>());
    }

    public Entity getTableA() {
        return tableA;
    }

    /**
     * Sets a new Table.
     * Warning, pre stashed ForeignKeys will be cleared!
     */
    public void setTableA(Entity tableA) {
        foreignKeys.remove(this.tableA);
        this.tableA = tableA;
        foreignKeys.computeIfAbsent(tableA, k -> new LinkedList<>());
    }

    public Entity getTableB() {
        return tableB;
    }

    /**
     * Sets a new Table.
     * Warning, pre stashed ForeignKeys will be cleared!
     */
    public void setTableB(Entity tableB) {
        foreignKeys.remove(this.tableB);
        this.tableB = tableB;
        foreignKeys.computeIfAbsent(tableB, k -> new LinkedList<>());
    }

    /**
     * Gets the ForeignKeys of TableA linked in TableB
     */
    public LinkedList<Attribute> getFkAttributesA() {
        return getFkAttributes(tableA);
    }

    /**
     * Gets the ForeignKeys of TableB linked in TableA
     */
    public LinkedList<Attribute> getFkAttributesB() {
        return getFkAttributes(tableB);
    }

    /**
     * Sets the ForeignKeys of TableA
     */
    public void setFkAttributesA(LinkedList<Attribute> fKeys) {
        foreignKeys.get(tableA).addAll(fKeys);
    }

    /**
     * Sets the ForeignKeys of TableB
     */
    public void setFkAttributesB(LinkedList<Attribute> fKeys) {
        foreignKeys.get(tableB).addAll(fKeys);
    }

    /**
     * Gets the ForeignKeys in attributes linked in primaryKey,
     * by iterating over all attributes and checking if FkTableColumn exist in primaryKeys list.
     */
    public LinkedList<Attribute> getFkAttributes(Entity table) {
        return foreignKeys.get(table);
    }
}
