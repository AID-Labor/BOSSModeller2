package de.snaggly.bossmodellerfx.model.abstraction;

public abstract class AttributeCombinationAbstraction implements AbstractedModel {
    private String combinationName;
    public String getCombinationName() {
        return combinationName;
    }
    public void setCombinationName(String combinationName) {
        this.combinationName = combinationName;
    }
}
