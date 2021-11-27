package de.snaggly.bossmodellerfx.model;

import java.util.ArrayList;

public class Attribute extends DataModel {
    private String type;
    private boolean isPrimary;
    private boolean isNonNull;
    private boolean isUnique;
    private String checkName;
    private String defaultName;
    private Attribute fkTableColumn;

    public Attribute(){
        this(null, "", false, false, false, "", "", null);
    }

    public Attribute(String name, String type, boolean isPrimary, boolean isNonNull, boolean isUnique, String checkName, String defaultName, Attribute fkTableColumn) {
        this(name, 0.0, 0.0, type, isPrimary, isNonNull, isUnique, checkName, defaultName, fkTableColumn);
    }

    public Attribute(String name, double xCoordinate, double yCoordinate, String type, boolean isPrimary, boolean isNonNull, boolean isUnique, String checkName, String defaultName, Attribute fkTableColumn) {
        super(name, xCoordinate, yCoordinate);
        this.type = type;
        this.isPrimary = isPrimary;
        this.isNonNull = isNonNull;
        this.isUnique = isUnique;
        this.checkName = checkName;
        this.defaultName = defaultName;
        this.fkTableColumn = fkTableColumn;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isPrimary() {
        return isPrimary;
    }

    public void setPrimary(boolean primary) {
        isPrimary = primary;
    }

    public boolean isNonNull() {
        return isNonNull;
    }

    public void setNonNull(boolean nonNull) {
        isNonNull = nonNull;
    }

    public boolean isUnique() {
        return isUnique;
    }

    public void setUnique(boolean unique) {
        isUnique = unique;
    }

    public String getCheckName() {
        return checkName;
    }

    public void setCheckName(String checkName) {
        this.checkName = checkName;
    }

    public String getDefaultName() {
        return defaultName;
    }

    public void setDefaultName(String defaultName) {
        this.defaultName = defaultName;
    }

    public Attribute getFkTableColumn() {
        return fkTableColumn;
    }

    public void setFkTableColumn(Attribute fkTableColumn) {
        this.fkTableColumn = fkTableColumn;
    }

    public static class UniqueCombination {
        private ArrayList<Attribute> uniqueCombinationList;

        public ArrayList<Attribute> getUniqueCombinationList() {
            return uniqueCombinationList;
        }

        public void setUniqueCombinationList(ArrayList<Attribute> uniqueCombinationList) {
            this.uniqueCombinationList = uniqueCombinationList;
        }

        public UniqueCombination(ArrayList<Attribute> uniqueCombinationList) {
            this.uniqueCombinationList = uniqueCombinationList;
        }
    }
}
