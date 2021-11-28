package de.snaggly.bossmodellerfx.model.subdata;

import de.snaggly.bossmodellerfx.model.abstraction.AttributeCombinationAbstraction;

import java.util.ArrayList;

public class AttributeCombination extends AttributeCombinationAbstraction {
    private ArrayList<Attribute> attributes = new ArrayList<>();

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
}
