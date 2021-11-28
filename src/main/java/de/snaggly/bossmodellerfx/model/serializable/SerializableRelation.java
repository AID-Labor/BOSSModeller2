package de.snaggly.bossmodellerfx.model.serializable;

import de.snaggly.bossmodellerfx.model.abstraction.RelationAbstraction;
import de.snaggly.bossmodellerfx.model.subdata.Relation;
import de.snaggly.bossmodellerfx.model.view.Entity;

import java.util.ArrayList;

public class SerializableRelation extends RelationAbstraction {
    public int tableAIndex;
    public int tableBIndex;

    public static SerializableRelation serializableRelation(Relation relation, ArrayList<Entity> entities) {
        var serRelation = new SerializableRelation();
        serRelation.orientation = relation.orientation;
        serRelation.setTableA_Cardinality(relation.getTableA_Cardinality());
        serRelation.setTableB_Cardinality(relation.getTableB_Cardinality());
        serRelation.setTableA_Obligation(relation.getTableA_Obligation());
        serRelation.setTableB_Obligation(relation.getTableB_Obligation());
        serRelation.tableAIndex = entities.indexOf(relation.getTableA());
        serRelation.tableBIndex = entities.indexOf(relation.getTableB());

        return serRelation;
    }

    public static Relation deserializableRelation(SerializableRelation serRelation, ArrayList<Entity> entities) {
        var relation = new Relation(
                entities.get(serRelation.tableAIndex),
                entities.get(serRelation.tableBIndex),
                serRelation.getTableA_Cardinality(),
                serRelation.getTableB_Cardinality(),
                serRelation.getTableA_Obligation(),
                serRelation.getTableB_Obligation()
        );
        relation.orientation = serRelation.orientation;

        return relation;
    }
}
