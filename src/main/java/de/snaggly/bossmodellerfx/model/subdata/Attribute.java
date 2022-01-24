package de.snaggly.bossmodellerfx.model.subdata;

import de.snaggly.bossmodellerfx.model.abstraction.AttributeAbstraction;
import de.snaggly.bossmodellerfx.model.view.Entity;

import java.util.ArrayList;

public class Attribute extends AttributeAbstraction {
    private Attribute fkTableColumn;

    public Attribute(){
        this(null, "", false, false, false, "", "", null);
    }

    public Attribute(String name, String type, boolean isPrimary, boolean isNonNull, boolean isUnique, String checkName, String defaultName, Attribute fkTableColumn) {
        super(name, type, isPrimary, isNonNull, isUnique, checkName, defaultName);
        this.fkTableColumn = fkTableColumn;
    }

    public Attribute getFkTableColumn() {
        return fkTableColumn;
    }

    public void setFkTableColumn(Attribute fkTableColumn) {
        this.fkTableColumn = fkTableColumn;
    }

    public Entity getEntityOfFkColumn(ArrayList<Entity> entityList) {
        if (fkTableColumn == null)
            return null;
        for (var entity : entityList) {
            if (entity.getPrimaryKey().equals(fkTableColumn))
                return entity;
        }
        return null;
    }
}
