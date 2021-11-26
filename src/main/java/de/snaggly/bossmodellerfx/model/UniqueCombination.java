package de.snaggly.bossmodellerfx.model;

import java.util.ArrayList;

public class UniqueCombination extends DataModel {
    private ArrayList<AttributeCombination> combinations = new ArrayList<>();

    public UniqueCombination() {
        super("simple", 0.0, 0.0);
    }

    public UniqueCombination(String name, ArrayList<AttributeCombination> combinations) {
        super(name, 0, 0);
        this.combinations = combinations;
    }

    public ArrayList<AttributeCombination> getCombinations() {
        return combinations;
    }

    public void setCombinations(ArrayList<AttributeCombination> combinations) {
        this.combinations = combinations;
    }

    public static class AttributeCombination {
        private String combinationName;
        private ArrayList<Attribute> attributes = new ArrayList<>();

        public String getCombinationName() {
            return combinationName;
        }

        public void setCombinationName(String combinationName) {
            this.combinationName = combinationName;
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
    }
}
