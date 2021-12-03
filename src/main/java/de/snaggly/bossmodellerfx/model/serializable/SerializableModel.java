package de.snaggly.bossmodellerfx.model.serializable;

import de.snaggly.bossmodellerfx.model.abstraction.AbstractedModel;

public interface SerializableModel<T extends AbstractedModel> {
    SerializableModel<T> serialize(T model);
    T deserialize(SerializableModel<T> serializedModel);
}
