package de.snaggly.bossmodellerfx.model.serializable;

import de.snaggly.bossmodellerfx.model.abstraction.AttributeCombinationAbstraction;

import java.util.ArrayList;

/**
 * Model for a serializable AttributeCombination.
 *
 * Holds index' of Attribute to retain connecting reference on serialization.
 *
 * @author Omar Emshani
 */
public class SerializableAttributeCombination extends AttributeCombinationAbstraction {
    public ArrayList<Integer> attributeCombinations = new ArrayList<>();
}
