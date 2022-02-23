package de.snaggly.bossmodellerfx.model.subdata;

import de.snaggly.bossmodellerfx.model.abstraction.UniqueCombinationAbstraction;

import java.util.ArrayList;

/**
 * Model for UniqueCombination. This class holds the AttributeCombinations. Used in Entity.
 *
 * @author Omar Emshani
 */
public class UniqueCombination extends UniqueCombinationAbstraction {
    private ArrayList<AttributeCombination> combinations = new ArrayList<>();

    public UniqueCombination() { }

    public UniqueCombination(ArrayList<AttributeCombination> combinations) {
        this.combinations = combinations;
    }

    public ArrayList<AttributeCombination> getCombinations() {
        return combinations;
    }

    public void setCombinations(ArrayList<AttributeCombination> combinations) {
        this.combinations = combinations;
    }

    public void addCombination(AttributeCombination combination) {
        combinations.add(combination);
    }
}
