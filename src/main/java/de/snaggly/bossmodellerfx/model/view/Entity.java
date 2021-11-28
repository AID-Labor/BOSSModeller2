package de.snaggly.bossmodellerfx.model.view;

import de.snaggly.bossmodellerfx.model.abstraction.EntityAbstraction;
import de.snaggly.bossmodellerfx.model.subdata.Attribute;
import de.snaggly.bossmodellerfx.model.subdata.Relation;
import de.snaggly.bossmodellerfx.model.subdata.UniqueCombination;

import java.util.ArrayList;

public class Entity extends EntityAbstraction {
    private UniqueCombination uniqueCombination;

    public Entity() {
        this(null, new ArrayList<>(), false);
    }

    public Entity(String name, ArrayList<Attribute> attributes, boolean isWeakType) {
        this(name, 0.0, 0.0, attributes, isWeakType);
    }

    public Entity(String name, double xCoordinate, double yCoordinate, ArrayList<Attribute> attributes, boolean isWeakType) {
        this(name, xCoordinate, yCoordinate, attributes, new UniqueCombination(), isWeakType);
    }

    public Entity(String name, double xCoordinate, double yCoordinate, ArrayList<Attribute> attributes, UniqueCombination uniqueCombination, boolean isWeakType) {
        super(name, xCoordinate, yCoordinate, attributes, isWeakType);
        this.uniqueCombination = uniqueCombination;
    }

    public ArrayList<Relation> getInvolvedRelations(ArrayList<Relation> relations) {
        var result = new ArrayList<Relation>();
        for (var relation : relations) {
            if (relation.getTableA() == this || relation.getTableB() == this) {
                result.add(relation);
            }
        }
        return result;
    }

    public UniqueCombination getUniqueCombination() {
        return uniqueCombination;
    }

    public void setUniqueCombination(UniqueCombination uniqueCombination) {
        this.uniqueCombination = uniqueCombination;
    }
}
