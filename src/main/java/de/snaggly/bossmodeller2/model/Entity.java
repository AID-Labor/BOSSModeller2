package de.snaggly.bossmodeller2.model;

import java.util.ArrayList;

public class Entity extends DataModel {
    private ArrayList<Attribute> attributes;
    private boolean isWeakType;

    public Entity() {

    }

    public Entity(String name, ArrayList<Attribute> attributes, boolean isWeakType) {
        super(name, 0.0, 0.0);
        this.attributes = attributes;
        this.isWeakType = isWeakType;
    }

    public Entity(String name, double xCoordinate, double yCoordinate, ArrayList<Attribute> attributes, boolean isWeakType) {
        super(name, xCoordinate, yCoordinate);
        this.attributes = attributes;
        this.isWeakType = isWeakType;
    }

    public ArrayList<Attribute> getAttributes() {
        return attributes;
    }

    public Attribute getPrimaryKey() {
        return attributes.stream().filter(Attribute::isPrimary).findFirst().orElse(null);
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

    public boolean isWeakType() {
        return isWeakType;
    }

    public void setWeakType(boolean weakType) {
        isWeakType = weakType;
    }
}
