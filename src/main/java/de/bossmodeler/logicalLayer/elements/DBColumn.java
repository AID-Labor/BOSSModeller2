package de.bossmodeler.logicalLayer.elements;



/**
 * DBColumn represents a column and contains its properties.
 *
 * @author Stefan Hufschmidt
 * @version 1.0.1
 *
 *   Since 1.0.0
 *   Edited descriptions. NW
 */

public class DBColumn {

    /** dBCName: The name of this column */
    private String dBCName;

    /** dBCType: The data type of this column.<br>
     * e.g.: Integer */
    private String dBCType;

    /** dBCCheck states whether this column has a check constraint.<br>
     * null = no check constraint on the column.<br>
     * Else this string shows the check operation of this column. */
    private String dBCCheck;



    /** dBCFKRefTableName contains the referenced table of the foreign key.
     * "" if column is not a foreign key. */
    private String dBCFKRefTableName;


    /** dBCFKRefName contains the name of the referenced column.
     * "" if column is not a foreign key. */
    private String dBCFKRefName;

    /**  dBCFKConstraintName contains the constraint name of the foreingn key constraint
     * "" if column is not a foreign key.*/
    private String dBCFKConstraintName;

    /** dBCNotNull shows if this column has a "not null"-constraint.<br>
     * true = this column has a "not null" constraint.<br>
     * false = this column has not a "not null" constraint. */
    private boolean dBCNotNull;

    /** dBCDefault describes whether this column has a default value. <br>
     * true = this column has a default value.<br>
     * false = this Column has not a default value. */
    private String dBCDefault;


    /**
     * This constructor creates a new column (DBcolumn).
     *
     * @param dBCName name of the new column
     * @param dBCType data type of the new column
     * @param dBCCheck check constraint of the new column. Set "null" if no check constraint is wanted
     * @param dBCFKey set "true" if this column is a foreign key - else set "false"
     * @param dBCNotNull set "true" when you want a "not null" constraint on this column and false if not
     * @param dBCDefault set "true" when your want a "default" constraint on this column and false if not
     */
    public DBColumn(String dBCName,String dBCType,String dBCCheck,boolean dBCNotNull,String dBCDefault, String dBCFKRefTableName,String dBCFKRefName, String dBCFKConstraintName){
        this.dBCName = dBCName;
        this.dBCType = dBCType;
        this.dBCCheck = dBCCheck;
        this.dBCNotNull = dBCNotNull;
        this.dBCDefault = dBCDefault;
        this.dBCFKRefTableName = dBCFKRefTableName;
        this.dBCFKRefName = dBCFKRefName;
        this.dBCFKConstraintName = dBCFKConstraintName;

    }

    /**
     * This constructor creates a new column (DBcolumn).
     *
     * @param dBCName name of the new column
     * @param dBCType data type of the new column
     */
    public DBColumn(String dBCName,String dBCType){
        this.dBCName = dBCName;
        this.dBCType = dBCType;
        this.dBCCheck = "";
        this.dBCNotNull = false;
        this.dBCDefault = "";
        this.dBCFKRefTableName = "";
        this.dBCFKRefName = "";
        this.dBCFKConstraintName = "";

    }

    /** @return Name of the column (dBCName).
     * @see dBCName */
    public String getdBCName() {
        return dBCName;
    }

    /**
     * Sets the column name.
     * @param dBCName the dBCName to set
     * @see dBCName */
    public void setdBCName(String dBCName) {
        this.dBCName = dBCName;
    }

    /** @return the data type of the column (dBCType).
     * @see dBCType */
    public String getdBCType() {
        return dBCType;
    }

    /**
     * Sets the data type of the column.
     *
     *  @param dBCType the data type of the column (String).
     *  @see dBCType
     */
    public void setdBCType(String dBCType) {
        this.dBCType = dBCType;
    }

    /** @return the check constraint of the column (dBCCheck).<br> "null" if no check constraint is set.
     * @see dBCCheck */
    public String getdBCCheck() {
        return dBCCheck;
    }

    /** Sets the column check constraint.
     * @param dBCCheck String of the check constraint to be set.<br> Set "null" if no check constraint needs to be set.
     * @see dBCCheck*/
    public void setdBCCheck(String dBCCheck) {
        this.dBCCheck = dBCCheck;
    }


    /** @return Not null constraint flag (dBCNotNull).<br> "true" if column has a not null constraint, else "false"
     * @see dBCNotNull*/
    public boolean isdBCNotNull() {
        return dBCNotNull;
    }

    /**
     * Sets the not null constraint flag.
     *
     *  @param dBCNotNull the dBCNotNull to set
     *  @see {@link DBColumn#dBCNotNull dBCNotNull}
     */
    public void setdBCNotNull(boolean dBCNotNull) {
        this.dBCNotNull = dBCNotNull;
    }

    /** @return the dBCDefault
     * @see dBCDefault */
    public String isdBCDefault() {
        return dBCDefault;
    }

    /**
     * Sets default value flag (dBCDefault).
     *
     * @param dBCDefault the dBCDefault to set <br> Set "true" if column has a default value, else "false"
     * @see {@link DBColumn#dBCDefault dBCDefault}
     */
    public void setdBCDefault(String dBCDefault) {
        this.dBCDefault = dBCDefault;
    }

    /**
     * Returns foreign keys referenced table name
     *
     * @return String dBCFKRefTableName
     * @see {@link DBColumn#dBCFKRefTableName dBCFKRefTableName}
     */
    public String getdBCFKRefTableName() {
        return dBCFKRefTableName;
    }

    /**
     * Sets foreign keys referenced table name
     *
     * @param dBCFKRefTableName String containing by foreign key referenced table name
     * @see {@link DBColumn#dBCFKRefTableName dBCFKRefTableName}
     */
    public void setdBCFKRefTableName(String dBCFKRefTableName) {
        this.dBCFKRefTableName = dBCFKRefTableName;
    }

    /**
     * Returns foreign keys referenced column name
     *
     * @return String dBCFKRefName
     * @see {@link DBColumn#dBCFKRefName dBCFKRefName}
     */
    public String getdBCFKRefName() {
        return dBCFKRefName;
    }

    /**
     * Sets foreign keys referenced column name
     *
     * @param dBCFKRefTableName String containing by foreign key referenced column name
     * @see {@link DBColumn#dBCFKRefName dBCFKRefName}
     */
    public void setdBCFKRefName(String dBCFKRefName) {
        this.dBCFKRefName = dBCFKRefName;
    }

    @Override
    public boolean equals(Object obj) {
        return ((DBColumn)obj).getdBCName().equals(getdBCName());
    }

    /**
     * @return the dBCFKConstraintName
     */
    public String getdBCFKConstraintName() {
        return dBCFKConstraintName;
    }

    /**
     * @param dBCFKConstraintName the dBCFKConstraintName to set
     */
    public void setdBCFKConstraintName(String dBCFKConstraintName) {
        this.dBCFKConstraintName = dBCFKConstraintName;
    }

    /**
     * @return the dBCDefault
     */
    public String getdBCDefault() {
        return dBCDefault;
    }

    @Override
    public String toString() {
        return this.dBCName;
    }

}
