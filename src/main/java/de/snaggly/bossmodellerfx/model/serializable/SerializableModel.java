package de.snaggly.bossmodellerfx.model.serializable;

import de.snaggly.bossmodellerfx.model.abstraction.AbstractedModel;

/**
 * Parent of all Serializable models.
 * Interface declares methods to convert a normal model to a serializable Model and vice versa.
 * Implementing Classes need to be able to full translate their models both ways.
 *
 * @author Omar Emshani
 */
public interface SerializableModel<T extends AbstractedModel> {
    SerializableModel<T> serialize(T model);
    T deserialize(SerializableModel<T> serializedModel);
}
