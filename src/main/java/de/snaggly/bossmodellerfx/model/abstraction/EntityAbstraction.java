package de.snaggly.bossmodellerfx.model.abstraction;

import de.snaggly.bossmodellerfx.model.subdata.Attribute;
import de.snaggly.bossmodellerfx.model.view.ViewModel;

import java.util.ArrayList;

public class EntityAbstraction extends ViewModel implements AbstractedModel {
    private String name;
    private ArrayList<Attribute> attributes;
    private boolean isWeakType;

    public EntityAbstraction() {
        this(null, new ArrayList<>(), false);
    }

    public EntityAbstraction(String name, ArrayList<Attribute> attributes, boolean isWeakType) {
        this(name, 0.0, 0.0, attributes, isWeakType);
    }

    public EntityAbstraction(String name, double xCoordinate, double yCoordinate, ArrayList<Attribute> attributes, boolean isWeakType) {
        super(xCoordinate, yCoordinate);
        this.name = name;
        this.attributes = attributes;
        this.isWeakType = isWeakType;
    }

    public Attribute getPrimaryKey() {
        return attributes.stream().filter(Attribute::isPrimary).findFirst().orElse(null);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public boolean isWeakType() {
        return isWeakType;
    }

    public void setWeakType(boolean weakType) {
        isWeakType = weakType;
    }
}
