package de.snaggly.bossmodellerfx.model.serializable;

import de.snaggly.bossmodellerfx.model.abstraction.UniqueCombinationAbstraction;

import java.util.ArrayList;

public class SerializableUniqueCombination extends UniqueCombinationAbstraction {
    public ArrayList<SerializableAttributeCombination> attributeCombination = new ArrayList<>();
}
