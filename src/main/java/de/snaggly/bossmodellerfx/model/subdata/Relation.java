package de.snaggly.bossmodellerfx.model.subdata;

import de.snaggly.bossmodellerfx.model.abstraction.RelationAbstraction;
import de.snaggly.bossmodellerfx.model.view.Entity;
import de.snaggly.bossmodellerfx.relation_logic.CrowsFootOptions;

import java.util.ArrayList;

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

    public Attribute getFkAttributeA() {
        return getFkAttribute(tableA.getAttributes(), tableB.getPrimaryKey());
    }

    public Attribute getFkAttributeB() {
        return getFkAttribute(tableB.getAttributes(), tableA.getPrimaryKey());
    }

    public Attribute getFkAttributeA(Attribute foreignPrimaryKey) {
        return getFkAttribute(tableA.getAttributes(), foreignPrimaryKey);
    }

    public Attribute getFkAttributeB(Attribute foreignPrimaryKey) {
        return getFkAttribute(tableB.getAttributes(), foreignPrimaryKey);
    }

    private Attribute getFkAttribute(ArrayList<Attribute> attributes, Attribute primaryKey) {
        for (var attribute : attributes) {
            if (attribute.getFkTableColumn() != null && attribute.getFkTableColumn().equals(primaryKey))
                return attribute;
        }
        return null;
    }
}
