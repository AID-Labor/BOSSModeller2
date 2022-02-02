package de.snaggly.bossmodellerfx.model.subdata;

import de.snaggly.bossmodellerfx.guiLogic.Project;
import de.snaggly.bossmodellerfx.model.abstraction.AttributeAbstraction;
import de.snaggly.bossmodellerfx.model.view.Entity;

import java.util.ArrayList;

public class Attribute extends AttributeAbstraction {
    private Attribute fkTableColumn;
    private Entity fkTable;

    public Attribute(){
        this(null, "", false, false, false, "", "", null, null);
    }

    public Attribute(String name, String type, boolean isPrimary, boolean isNonNull, boolean isUnique, String checkName, String defaultName) {
        this(name, type, isPrimary, isNonNull, isUnique, checkName, defaultName, null, null);
    }

    public Attribute(String name, String type, boolean isPrimary, boolean isNonNull, boolean isUnique, String checkName, String defaultName, Attribute fkTableColumn) {
        this(name, type, isPrimary, isNonNull, isUnique, checkName, defaultName, fkTableColumn, null);
        this.fkTable = getEntityOfFkColumn(Project.getCurrentProject().getEntities());
    }

    public Attribute(String name, String type, boolean isPrimary, boolean isNonNull, boolean isUnique, String checkName, String defaultName, Attribute fkTableColumn, Entity fkTable) {
        super(name, type, isPrimary, isNonNull, isUnique, checkName, defaultName);
        this.fkTableColumn = fkTableColumn;
        this.fkTable = fkTable;
    }

    public Attribute getFkTableColumn() {
        return fkTableColumn;
    }

    public void setFkTableColumn(Attribute fkTableColumn) {
        this.fkTableColumn = fkTableColumn;
    }

    public Entity getFkTable() {
        return fkTable;
    }

    public void setFkTable(Entity fkTable) {
        this.fkTable = fkTable;
    }

    public Entity getEntityOfFkColumn(ArrayList<Entity> entityList) {
        if (fkTableColumn == null)
            return null;
        for (var entity : entityList) {
            if (entity.getPrimaryKey().equals(fkTableColumn)) {
                fkTable = entity;
                return fkTable;
            }
        }
        return null;
    }
}
