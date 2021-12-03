package de.snaggly.bossmodellerfx.model.abstraction;

import de.snaggly.bossmodellerfx.struct.relations.ConnectingOrientation;
import de.snaggly.bossmodellerfx.struct.relations.CrowsFootOptions;

public abstract class RelationAbstraction implements AbstractedModel {
    private CrowsFootOptions.Cardinality tableA_Cardinality;
    private CrowsFootOptions.Cardinality tableB_Cardinality;
    private CrowsFootOptions.Obligation tableA_Obligation;
    private CrowsFootOptions.Obligation tableB_Obligation;
    public ConnectingOrientation orientation;

    public RelationAbstraction() {}

    public RelationAbstraction(CrowsFootOptions.Cardinality tableA_Cardinality, CrowsFootOptions.Cardinality tableB_Cardinality, CrowsFootOptions.Obligation tableA_Obligation, CrowsFootOptions.Obligation tableB_Obligation) {
        this.tableA_Cardinality = tableA_Cardinality;
        this.tableB_Cardinality = tableB_Cardinality;
        this.tableA_Obligation = tableA_Obligation;
        this.tableB_Obligation = tableB_Obligation;
    }

    public CrowsFootOptions.Cardinality getTableA_Cardinality() {
        return tableA_Cardinality;
    }

    public void setTableA_Cardinality(CrowsFootOptions.Cardinality tableA_Cardinality) {
        this.tableA_Cardinality = tableA_Cardinality;
    }

    public CrowsFootOptions.Cardinality getTableB_Cardinality() {
        return tableB_Cardinality;
    }

    public void setTableB_Cardinality(CrowsFootOptions.Cardinality tableB_Cardinality) {
        this.tableB_Cardinality = tableB_Cardinality;
    }

    public CrowsFootOptions.Obligation getTableA_Obligation() {
        return tableA_Obligation;
    }

    public void setTableA_Obligation(CrowsFootOptions.Obligation tableA_Obligation) {
        this.tableA_Obligation = tableA_Obligation;
    }

    public CrowsFootOptions.Obligation getTableB_Obligation() {
        return tableB_Obligation;
    }

    public void setTableB_Obligation(CrowsFootOptions.Obligation tableB_Obligation) {
        this.tableB_Obligation = tableB_Obligation;
    }
}
