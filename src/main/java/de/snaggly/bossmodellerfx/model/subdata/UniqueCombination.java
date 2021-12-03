package de.snaggly.bossmodellerfx.model.subdata;

import de.snaggly.bossmodellerfx.model.abstraction.UniqueCombinationAbstraction;

import java.util.ArrayList;

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

}
