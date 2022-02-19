package de.snaggly.bossmodellerfx.model.subdata;

import de.snaggly.bossmodellerfx.model.abstraction.RelationAbstraction;
import de.snaggly.bossmodellerfx.model.view.Entity;
import de.snaggly.bossmodellerfx.relation_logic.CrowsFootOptions;

import java.util.ArrayList;
import java.util.LinkedList;

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

    public LinkedList<Attribute> getFkAttributeA() {
        return getFkAttribute(tableA.getAttributes(), tableB.getPrimaryKey());
    }

    public LinkedList<Attribute> getFkAttributeB() {
        return getFkAttribute(tableB.getAttributes(), tableA.getPrimaryKey());
    }

    public LinkedList<Attribute> getFkAttributeA(LinkedList<Attribute> foreignPrimaryKeys) {
        return getFkAttribute(tableA.getAttributes(), foreignPrimaryKeys);
    }

    public LinkedList<Attribute> getFkAttributeB(LinkedList<Attribute> foreignPrimaryKeys) {
        return getFkAttribute(tableB.getAttributes(), foreignPrimaryKeys);
    }

    private LinkedList<Attribute> getFkAttribute(ArrayList<Attribute> attributes, LinkedList<Attribute> primaryKey) {
        var result = new LinkedList<Attribute>();
        for (var attribute : attributes) {
            var fKey = attribute.getFkTableColumn();
            if (fKey != null && primaryKey.contains(fKey)) {
                result.add(attribute);
            }
        }
        return result;
    }
}
