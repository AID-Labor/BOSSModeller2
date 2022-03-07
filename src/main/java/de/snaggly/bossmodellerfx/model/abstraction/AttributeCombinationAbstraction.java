package de.snaggly.bossmodellerfx.model.abstraction;

/**
 * Abstracting around AttributeCombination. Here Attributes have been abstracted.
 *
 * @author Omar Emshani
 */
public abstract class AttributeCombinationAbstraction implements AbstractedModel {
    private String combinationName;
    private boolean isPrimaryCombination; //Combination of all PrimaryKeys
    public String getCombinationName() {
        return combinationName;
    }
    public void setCombinationName(String combinationName) {
        this.combinationName = combinationName;
    }
    public boolean isPrimaryCombination() {
        return isPrimaryCombination;
    }
    public void setPrimaryCombination(boolean primaryCombination) {
        isPrimaryCombination = primaryCombination;
    }
}
