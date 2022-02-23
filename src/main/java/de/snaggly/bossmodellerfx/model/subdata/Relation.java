package de.snaggly.bossmodellerfx.model.subdata;

import de.snaggly.bossmodellerfx.model.abstraction.RelationAbstraction;
import de.snaggly.bossmodellerfx.model.view.Entity;
import de.snaggly.bossmodellerfx.relation_logic.CrowsFootOptions;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Model for a Relation between two Entities.
 *
 * @author Omar Emshani
 */
public class Relation extends RelationAbstraction {
    private Entity tableA;
    private Entity tableB;

    public Relation(Entity tableA, Entity tableB, CrowsFootOptions.Cardinality tableA_Cardinality, CrowsFootOptions.Cardinality tableB_Cardinality, CrowsFootOptions.Obligation tableA_Obligation, CrowsFootOptions.Obligation tableB_Obligation) {
        super(tableA_Cardinality, tableB_Cardinality, tableA_Obligation, tableB_Obligation);
        this.tableA = tableA;
        this.tableB = tableB;
    }

    public Entity getTableA() {
        return tableA;
    }

    public void setTableA(Entity tableA) {
        this.tableA = tableA;
    }

    public Entity getTableB() {
        return tableB;
    }

    public void setTableB(Entity tableB) {
        this.tableB = tableB;
    }

    /**
     * Gets the ForeignKeys of TableA linked in TableB
     */
    public LinkedList<Attribute> getFkAttributesA() {
        return getFkAttributes(tableA.getAttributes(), tableB.getPrimaryKey());
    }

    /**
     * Gets the ForeignKeys of TableB linked in TableA
     */
    public LinkedList<Attribute> getFkAttributesB() {
        return getFkAttributes(tableB.getAttributes(), tableA.getPrimaryKey());
    }

    /**
     * Gets the ForeignKeys in attributes linked in primaryKey,
     * by iterating over all attributes and checking if FkTableColumn exist in primaryKeys list.
     */
    private LinkedList<Attribute> getFkAttributes(ArrayList<Attribute> attributes, LinkedList<Attribute> primaryKeys) {
        var result = new LinkedList<Attribute>();
        for (var attribute : attributes) {
            var fKey = attribute.getFkTableColumn();
            if (fKey != null && primaryKeys.contains(fKey)) {
                result.add(attribute);
            }
        }
        return result;
    }
}
