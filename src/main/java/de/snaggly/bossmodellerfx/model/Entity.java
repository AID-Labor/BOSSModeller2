package de.snaggly.bossmodellerfx.model;

import java.util.ArrayList;

public class Entity extends DataModel {
    private ArrayList<Attribute> attributes;
    private UniqueCombination uniqueCombination;
    private boolean isWeakType;

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
        super(name, xCoordinate, yCoordinate);
        this.attributes = attributes;
        this.uniqueCombination = uniqueCombination;
        this.isWeakType = isWeakType;
    }

    public Attribute getPrimaryKey() {
        return attributes.stream().filter(Attribute::isPrimary).findFirst().orElse(null);
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

    public ArrayList<Attribute> getAttributes() {
        return attributes;
    }

    public void setAttributes(ArrayList<Attribute> attributes) {
        this.attributes = attributes;
    }

    public void addAttribute(Attribute attribute) {
        attributes.add(attribute);
    }

    public void removeAttribute(Attribute attribute) {
        attributes.remove(attribute);
    }

    public void removeAttribute(int index) {
        attributes.remove(index);
    }

    public UniqueCombination getUniqueCombination() {
        return uniqueCombination;
    }

    public void setUniqueCombination(UniqueCombination uniqueCombination) {
        this.uniqueCombination = uniqueCombination;
    }

    public boolean isWeakType() {
        return isWeakType;
    }

    public void setWeakType(boolean weakType) {
        isWeakType = weakType;
    }
}
