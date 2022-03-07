package de.snaggly.bossmodellerfx.model.abstraction;

import de.snaggly.bossmodellerfx.model.BOSSModel;

/**
 * Main purpose of this interface is to only cut out attributes from the Model which cannot be directly used in a serializable Model.
 * An abstracted Model like AttributeAbstraction contains all attributes which defines this Model apart from the ForeignKey.
 * The ForeignKey in an Attribute references another Attribute on a different object. When serializing, this value needs to be converted
 * to an index to allow re-referencing when deserializing.
 *
 * Main purpose is to not reuse existing Datatypes.
 *
 * @author Omar Emshani
 */
public interface AbstractedModel extends BOSSModel {
}
