package de.snaggly.bossmodellerfx.model.serializable;

import de.snaggly.bossmodellerfx.model.abstraction.EntityAbstraction;
import de.snaggly.bossmodellerfx.model.subdata.Attribute;
import de.snaggly.bossmodellerfx.model.subdata.AttributeCombination;
import de.snaggly.bossmodellerfx.model.subdata.UniqueCombination;
import de.snaggly.bossmodellerfx.model.view.Entity;

import java.util.ArrayList;

public class SerializableEntity extends EntityAbstraction {
    public SerializableUniqueCombination uniqueCombination = new SerializableUniqueCombination();

    public static SerializableEntity serializableEntity(Entity entity) {
        var serEntity = new SerializableEntity();
        serEntity.setName(entity.getName());
        serEntity.setAttributes(entity.getAttributes());
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

    public static Entity deserializableEntity(SerializableEntity serEntity) {
        var entity = new Entity(
                serEntity.getName(),
                serEntity.getXCoordinate(),
                serEntity.getYCoordinate(),
                serEntity.getAttributes(),
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
