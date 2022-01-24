package de.snaggly.bossmodellerfx.model.serializable;

import de.snaggly.bossmodellerfx.model.abstraction.EntityAbstraction;
import de.snaggly.bossmodellerfx.model.subdata.Attribute;
import de.snaggly.bossmodellerfx.model.subdata.AttributeCombination;
import de.snaggly.bossmodellerfx.model.subdata.UniqueCombination;
import de.snaggly.bossmodellerfx.model.view.Entity;

import java.util.ArrayList;

public class SerializableEntity extends EntityAbstraction implements SerializableModel<Entity> {
    public ArrayList<SerializableAttribute> attributes = new ArrayList<>();
    public SerializableUniqueCombination uniqueCombination = new SerializableUniqueCombination();

    public static SerializableEntity serializableEntity(Entity entity) {
        return singletonInstance.serialize(entity);
    }

    public static Entity deserializableEntity(SerializableEntity serEntity) {
        return singletonInstance.deserialize(serEntity);
    }

    private final static SerializableEntity singletonInstance = new SerializableEntity();

    @Override
    public SerializableEntity serialize(Entity entity) {
        var serEntity = new SerializableEntity();
        serEntity.setName(entity.getName());
        for (var attribute : entity.getAttributes()){
            var serAttribute = new SerializableAttribute(
                    attribute.getName(),
                    attribute.getType(),
                    attribute.isPrimary(),
                    attribute.isNonNull(),
                    attribute.isUnique(),
                    attribute.getCheckName(),
                    attribute.getDefaultName()
            );
            serEntity.attributes.add(serAttribute);
        }
        serEntity.setWeakType(entity.isWeakType());
        serEntity.setXCoordinate(entity.getXCoordinate());
        serEntity.setYCoordinate(entity.getYCoordinate());
        for (var uniqueComb : entity.getUniqueCombination().getCombinations()) {
            var serAttrCombination = new SerializableAttributeCombination();
            serAttrCombination.setCombinationName(uniqueComb.getCombinationName());
            for (var attrEntry : uniqueComb.getAttributes()) {
                serAttrCombination.attributeCombinations.add(
                        entity.getAttributes().indexOf(attrEntry)
                );
            }
            serEntity.uniqueCombination.attributeCombination.add(serAttrCombination);
        }
        return serEntity;
    }

    @Override
    public Entity deserialize(SerializableModel<Entity> serializableModel) {
        if (!(serializableModel instanceof SerializableEntity))
            return null;
        var serEntity = (SerializableEntity)serializableModel;
        var attributes = new ArrayList<Attribute>();
        for (var serAttribute : serEntity.attributes) {
            attributes.add(new Attribute(
                    serAttribute.getName(),
                    serAttribute.getType(),
                    serAttribute.isPrimary(),
                    serAttribute.isNonNull(),
                    serAttribute.isUnique(),
                    serAttribute.getCheckName(),
                    serAttribute.getDefaultName(),
                    null
            ));
        }
        var entity = new Entity(
                serEntity.getName(),
                serEntity.getXCoordinate(),
                serEntity.getYCoordinate(),
                attributes,
                serEntity.isWeakType()
        );

        var attrCombinations = new ArrayList<AttributeCombination>();
        for (var serAttrCombination : serEntity.uniqueCombination.attributeCombination) {
            var attrCombination = new AttributeCombination();
            attrCombination.setCombinationName(serAttrCombination.getCombinationName());
            for (var serAttrCombinationEntry : serAttrCombination.attributeCombinations) {
                attrCombination.addAttribute(entity.getAttributes().get(serAttrCombinationEntry));
            }
            attrCombinations.add(attrCombination);
        }

        var uniqueCombo = new UniqueCombination();
        uniqueCombo.setCombinations(attrCombinations);
        entity.setUniqueCombination(uniqueCombo);

        return entity;
    }
}
