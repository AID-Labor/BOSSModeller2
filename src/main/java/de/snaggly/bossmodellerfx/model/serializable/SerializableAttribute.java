package de.snaggly.bossmodellerfx.model.serializable;

import de.snaggly.bossmodellerfx.model.abstraction.AttributeAbstraction;
import de.snaggly.bossmodellerfx.model.view.Entity;

import java.util.ArrayList;

public class SerializableAttribute extends AttributeAbstraction {
    public boolean isFk = false;
    public int entityIndex = -1;
    public int attributeIndex = -1;

    public SerializableAttribute(String name, String type, boolean isPrimary, boolean isNonNull, boolean isUnique, String checkName, String defaultName) {
        super(name, type, isPrimary, isNonNull, isUnique, checkName, defaultName);
    }

    public static void adjustFkOnSerialize(ArrayList<Entity> entities, ArrayList<SerializableEntity> serEntities) {
        for (int i = 0; i < entities.size(); i++) {
            for (int j = 0; j < entities.get(i).getAttributes().size(); j++) {
                var attr = entities.get(i).getAttributes().get(j);
                if (attr.getFkTableColumn() == null)
                    continue;
                var serAttr = serEntities.get(i).attributes.get(j);
                serAttr.isFk = true;
                serAttr.entityIndex = entities.indexOf(attr.getEntityOfFkColumn(entities));
                serAttr.attributeIndex = entities.get(serAttr.entityIndex).getAttributes().indexOf(attr.getFkTableColumn());
            }
        }
    }

    public static void adjustFkOnDeserialize(ArrayList<SerializableEntity> serEntities, ArrayList<Entity> entities) {
        for (int i = 0; i < entities.size(); i++) {
            for (int j = 0; j < serEntities.get(i).attributes.size(); j++) {
                var serAttr = serEntities.get(i).attributes.get(j);
                if (!serAttr.isFk)
                    continue;
                var fkAttr = entities.get(i).getAttributes().get(j);
                fkAttr.setFkTableColumn(entities.get(serAttr.entityIndex).getAttributes().get(serAttr.attributeIndex));
            }
        }
    }
}
