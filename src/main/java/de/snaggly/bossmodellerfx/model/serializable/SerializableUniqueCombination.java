package de.snaggly.bossmodellerfx.model.serializable;

import de.snaggly.bossmodellerfx.model.abstraction.UniqueCombinationAbstraction;

import java.util.ArrayList;

/**
 * Model for a serializable UniqueCombination.
 *
 * @author Omar Emshani
 */
public class SerializableUniqueCombination extends UniqueCombinationAbstraction {
    public ArrayList<SerializableAttributeCombination> attributeCombination = new ArrayList<>();
}
