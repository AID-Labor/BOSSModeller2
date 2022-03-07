package de.snaggly.bossmodellerfx.model.view;

import de.snaggly.bossmodellerfx.model.abstraction.EntityAbstraction;
import de.snaggly.bossmodellerfx.model.subdata.Attribute;
import de.snaggly.bossmodellerfx.model.subdata.Relation;
import de.snaggly.bossmodellerfx.model.subdata.UniqueCombination;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Model for an EntityView.
 *
 * @author Omar Emshani
 */
public class Entity extends EntityAbstraction {
    private ArrayList<Attribute> attributes;
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
        super(name, xCoordinate, yCoordinate, isWeakType);
        this.uniqueCombination = uniqueCombination;
        this.attributes = attributes;
    }

    public UniqueCombination getUniqueCombination() {
        return uniqueCombination;
    }

    public void setUniqueCombination(UniqueCombination uniqueCombination) {
        this.uniqueCombination = uniqueCombination;
    }

    public ArrayList<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(ArrayList<Attribute> attributes) {
        this.attributes = attributes;
    }

    public void addAttribute(Attribute attribute) {
        attributes.add(attribute);
    }

    public void addAttribute(int index, Attribute attribute) {
        attributes.add(index, attribute);
    }

    public void removeAttribute(Attribute attribute) {
        attributes.remove(attribute);
    }

    public void removeAttribute(int index) {
        attributes.remove(index);
    }

    public LinkedList<Attribute> getPrimaryKeys() {
        var result = new LinkedList<Attribute>();
        for (var attribute : attributes) {
            if (attribute.isPrimary()) {
                result.add(attribute);
            }
        }
        return result;
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
}
