package de.snaggly.bossmodellerfx.model.serializable;

import de.snaggly.bossmodellerfx.model.abstraction.RelationAbstraction;
import de.snaggly.bossmodellerfx.model.subdata.Attribute;
import de.snaggly.bossmodellerfx.model.subdata.Relation;
import de.snaggly.bossmodellerfx.model.view.Entity;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Model for a serializable Relation.
 *
 * @author Omar Emshani
 */
public class SerializableRelation extends RelationAbstraction implements SerializableModel<Relation> {
    public int tableAIndex;
    public int tableBIndex;
    private ArrayList<Entity> entities;
    private final ArrayList<Integer> foreignKeys_TableA = new ArrayList<>();
    private final ArrayList<Integer> foreignKeys_TableB = new ArrayList<>();

    public static SerializableRelation serializableRelation(Relation relation, ArrayList<Entity> entities) {
        singletonInstance.entities = entities;
        return singletonInstance.serialize(relation);
    }

    public static Relation deserializableRelation(SerializableRelation serRelation, ArrayList<Entity> entities) {
        singletonInstance.entities = entities;
        return singletonInstance.deserialize(serRelation);
    }

    private final static SerializableRelation singletonInstance = new SerializableRelation();

    @Override
    public SerializableRelation serialize(Relation relation) {
        var serRelation = new SerializableRelation();
        serRelation.orientation = relation.orientation;
        serRelation.setTableA_Cardinality(relation.getTableA_Cardinality());
        serRelation.setTableB_Cardinality(relation.getTableB_Cardinality());
        serRelation.setTableA_Obligation(relation.getTableA_Obligation());
        serRelation.setTableB_Obligation(relation.getTableB_Obligation());
        serRelation.tableAIndex = entities.indexOf(relation.getTableA());
        serRelation.tableBIndex = entities.indexOf(relation.getTableB());
        serRelation.setStrongRelation(relation.isStrongRelation());

        for (var attribute : relation.getFkAttributesA()) {
            serRelation.foreignKeys_TableA.add(relation.getTableA().getAttributes().indexOf(attribute));
        }
        for (var attribute : relation.getFkAttributesB()) {
            serRelation.foreignKeys_TableB.add(relation.getTableB().getAttributes().indexOf(attribute));
        }

        return serRelation;
    }

    @Override
    public Relation deserialize(SerializableModel<Relation> serializedModel) {
        var serRelation = (SerializableRelation)serializedModel;
        var relation = new Relation(
                entities.get(serRelation.tableAIndex),
                entities.get(serRelation.tableBIndex),
                serRelation.getTableA_Cardinality(),
                serRelation.getTableB_Cardinality(),
                serRelation.getTableA_Obligation(),
                serRelation.getTableB_Obligation()
        );
        var fKeysA = new LinkedList<Attribute>();
        for (var index : serRelation.foreignKeys_TableA) {
            fKeysA.add(relation.getTableA().getAttributes().get(index));
        }
        var fKeysB = new LinkedList<Attribute>();
        for (var index : serRelation.foreignKeys_TableB) {
            fKeysB.add(relation.getTableB().getAttributes().get(index));
        }
        relation.setFkAttributesA(fKeysA);
        relation.setFkAttributesB(fKeysB);
        relation.orientation = serRelation.orientation;
        relation.setStrongRelation(serRelation.isStrongRelation());

        return relation;
    }
}
