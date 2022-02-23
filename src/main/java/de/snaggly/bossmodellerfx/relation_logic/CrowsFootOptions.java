package de.snaggly.bossmodellerfx.relation_logic;

/**
 * Lists all possible option for the CrowsFoot-Notation.
 * Can/Must, One/Many.
 *
 * @author Omar Emshani
 */
public class CrowsFootOptions {
    public enum Cardinality {
        ONE,
        MANY
    }

    public enum Obligation {
        CAN,
        MUST
    }
}
