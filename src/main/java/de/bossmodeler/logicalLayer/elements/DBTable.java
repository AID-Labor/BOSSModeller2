package de.bossmodeler.logicalLayer.elements;

import java.util.LinkedList;


/**
 * DBTable represents a table, its properties and contains columns (objects of DBColumn).
 *
 * @author Stefan Hufschmidt
 * @version 1.0.1
 *
 * 	Since 1.0.0
 *  Edited descriptions. NW
 */


public class DBTable {

    /** dBTName name of the table. */
    private String dBTName;

    /** A list of columns (<code>DBColumns</code>) in this table. @see DBColumn */
    private LinkedList<DBColumn> dBTColumns;

    /** Shows if this table is a weak entity.<br>
     * true = table is a weak entity.<br>
     * false = table is not a weak entity. */
    private boolean dBTWeakEntity;

    /**dBTUniqueList is a List of single unique columns. */
    private LinkedList<DBColumn> dBTUniqueList;

    /**uniqueCombinations is a List of unique columnarrays as a Unique Combination. */
    private LinkedList<UniqueCombination> uniqueCombinations;

    /**dBTPKeyList is a List of Columns which are the primary key of this table.*/
    private LinkedList<DBColumn> dBTPKeyList;

    private LinkedList<DBColumn> dBTFKeyList;

    /** This constructor creates a new table with name and columns (<code>DBTable</code>).<br>
     * The dBTWeakEntity attribute will be set to false.<br>
     * The other attributes will be created with the new operator.
     * @param dBTName the name of this table (<code>DBTable</code>).
     * @param dBTColumns the list of columns (<code>DBColumns</code>) of this table (<code>DBTable</code>).
     * @see dBTName
     * @see dBTColumns */
    public DBTable(String dBTName, LinkedList<DBColumn> dBTColumns){
        this.dBTName = dBTName;
        this.dBTColumns = dBTColumns;
        this.dBTWeakEntity = false;
        this.dBTPKeyList = new LinkedList<DBColumn>();
        this.dBTUniqueList = new LinkedList<DBColumn>();
        this.uniqueCombinations = new LinkedList<UniqueCombination>();
        this.dBTFKeyList = new LinkedList<DBColumn>();
    }

    /** This constructor creates a new table with a name and without columns.<br>
     * The dBTWeakEntity attribute will be set to false.<br>
     * The other attributes will be created with the new operator.
     * @param dBTName the name of this table (<code>DBTable</code>).
     * @see dBTName  */
    public DBTable(String dBTName){
        this.dBTName = dBTName;
        this.dBTColumns = new LinkedList<DBColumn>();
        this.dBTWeakEntity = false;
        this.dBTPKeyList = new LinkedList<DBColumn>();
        this.dBTUniqueList = new LinkedList<DBColumn>();
        this.uniqueCombinations = new LinkedList<UniqueCombination>();
        this.dBTFKeyList = new LinkedList<DBColumn>();
    }

    /** @return The name of this table (dBTName).
     * @see dBTName */
    public String getdBTName() {
        return dBTName;
    }

    /** Sets the table name (dBTName).
     * @param dBTName the dBTName to set
     * @see dBTName */
    public void setdBTName(String dBTName) {
        this.dBTName = dBTName;
    }

    /** @return The list of columns of this table.
     * @see dBTColumns */
    public LinkedList<DBColumn> getdBTColumns() {
        return dBTColumns;
    }

    /** Sets the list of columns (dBTColumns).
     * @param dBTColumns the List of Columns which will bet set
     * @see dBTColumns */
    public void setdBTColumns(LinkedList<DBColumn> dBTColumns) {
        this.dBTColumns = dBTColumns;
    }

    /** Weak entity flag. <br> "true" if table is a weak entity, else "false".
     *@return if this Table is a weak Entity
     *@see dBTWeakEntity */
    public boolean isdBTWeakEntity() {
        return dBTWeakEntity;
    }

    /** Sets weak entity flag.<br> Set "true" if table is a weak entity, else "false".
     * @param dBTWeakEntity the weak entity flag (dBTWeakEntity).
     * @see dBTWeakEntity */
    public void setdBTWeakEntity(boolean dBTWeakEntity) {
        this.dBTWeakEntity = dBTWeakEntity;
    }

    /** @return A list of arrays(DBColumn) containing all unique combinations of columns (dBTPKeyList).
     * @see dBTPUniqueList */
    public LinkedList<DBColumn> getdBTUniqueList() {
        return dBTUniqueList;
    }

    /**
     * @return the uniqueCombinations
     */
    public LinkedList<UniqueCombination> getuniqueCombinations() {
        return uniqueCombinations;
    }

    /**
     * @param uniqueCombinations the uniqueCombinations to set
     */
    public void setuniqueCombinations(LinkedList<UniqueCombination> uniqueCombinations) {
        this.uniqueCombinations = uniqueCombinations;
    }

    /** @return A list of primary keys (dBTPKeyList).
     * @see dBTPKeyList */
    public LinkedList<DBColumn> getdBTPKeyList() {
        return dBTPKeyList;
    }

    /** Adds a column to this table.
     * @param columnToAdd the column that will be added to this table.
     * @see dBTColumns */
    public void addColumn(DBColumn columnToAdd){
        this.dBTColumns.addLast(columnToAdd);
    }

    /** Deletes a column from this table
     * @param columnName the name of the column that will be deleted.
     * @see dBTColumns */
    public void deleteColumn(String columnName){
        for(int i=0;i<this.dBTColumns.size();i++){
            if(this.dBTColumns.get(i).getdBCName().equals(columnName)){
                this.dBTColumns.remove(i);
            }
        }
    }

    /** Adds an array(DBColumn) with a unique combination to this tables unique list (dBTUniqueList).
     * @param listToAdd the array(DbColumn) that will be added to this tables unique list
     * @see dBTUniqueList*/
    public void addUniqueList(DBColumn column){
        boolean exists=false;
        for(int i=1; i< this.dBTUniqueList.size();i++){
            if (this.dBTUniqueList.get(i).getdBCName().equals(column.getdBCName())){
                exists=true;
            }
        }
        if (!exists) {
            this.dBTUniqueList.add(column);
        }
    }

    /** Removes a unique combination from this table.
     * @param listToRemove the array of DBColumns that will be deleted from this tables unique list (dBTUniqueList).
     * @see dBTUniqueList */
    public void removeUniqueList(DBColumn column){
        this.dBTUniqueList.remove(column);
    }

    /** Adds a column to this tables primary key list (dBTPKeyList).
     * @param pk the column that will be added to the primary key list (dBTPKeyList).
     * @see dBTPKeyList */
    public void addPKey(DBColumn pk){
        this.dBTPKeyList.add(pk);
    }

    /** Removes a primary key from this tables primary key list (dBTPKeyList).
     * @param pk the primary key that will be removed
     * @see dBTPKeyList */
    public void removePKey(DBColumn pk){
        this.dBTPKeyList.remove(pk);
    }

    /**
     * Gets the d btf key list.
     *
     * @return the d btf key list
     */
    public LinkedList<DBColumn> getdBTFKeyList() {
        return dBTFKeyList;
    }

    public void setdBTFKeyList(LinkedList<DBColumn> dBTFKeyList) {
        this.dBTFKeyList = dBTFKeyList;
    }

    public void addFKey(DBColumn fk){
        this.dBTFKeyList.add(fk);
    }

    public void removeFKey(DBColumn fk){
        this.dBTFKeyList.remove(fk);
    }


    public void setdBTPKeyList(LinkedList<DBColumn> dBTPKeyList) {
        this.dBTPKeyList = dBTPKeyList;
    }

    public DBColumn getColumn(String column){
        for (int i=0;i<dBTColumns.size();i++){
            if (column.equals(dBTColumns.get(i).getdBCName())){
                return dBTColumns.get(i);
            }
        }
        return null;
    }

    public DBColumn getFkey(String fk){
        for (int i=0;i<dBTFKeyList.size();i++){
            if (fk.equals(dBTFKeyList.get(i).getdBCFKRefTableName())){
                return dBTFKeyList.get(i);
            }
        }
        return null;
    }

    public DBColumn getUnique(String uq){
        for (int i=0;i<dBTUniqueList.size();i++){
            if (uq.equals(dBTUniqueList.get(i).getdBCName())){
                return dBTUniqueList.get(i);
            }
        }
        return null;
    }

    /**
     * Adds a unique combination to the DBTable
     * @param uniqueCombination the unique combination to be added
     * @see DBColumn
     * @see java.util.LinkedList
     */
    public void addUniqueCombination(UniqueCombination uniqueCombination){
        if(this.uniqueCombinations == null){
            this.uniqueCombinations = new LinkedList<UniqueCombination>();
        }
        boolean checkDuplicate = false;
        for(int i=0;i<this.uniqueCombinations.size();i++){
            if(this.uniqueCombinations.get(i).equals(uniqueCombination)){
                checkDuplicate = true;
                break;
            }
        }
        if(!checkDuplicate)
            this.uniqueCombinations.add(uniqueCombination);
    }

    /**
     * Sets the new column type in all lists.
     *
     * @param columnName the column name
     * @param newColumnType the new column type
     * @see #dBTColumns
     * @see #dBTFKeyList
     * @see #dBTPKeyList
     * @see #dBTUniqueList
     * @see #uniqueCombinations
     */
    public void setNewColumnTypeInAllLists(String columnName, String newColumnType) {
        //Columns
        for(int i=0;i<dBTColumns.size();i++){
            if(dBTColumns.get(i).getdBCName().equals(columnName)){
                dBTColumns.get(i).setdBCType(newColumnType);
            }
        }
        //PKs
        for (int i = 0; i < dBTPKeyList.size(); i++) {
            if (dBTPKeyList.get(i).getdBCName().equals(columnName)) {
                dBTPKeyList.get(i).setdBCType(newColumnType);
            }
        }
        //FKs
        for (int i = 0; i < dBTFKeyList.size(); i++) {
            if (dBTFKeyList.get(i).getdBCName().equals(columnName)) {
                dBTFKeyList.get(i).setdBCType(newColumnType);
            }
        }
        //single Uniques
        for (int i = 0; i < dBTUniqueList.size(); i++) {
            if (dBTUniqueList.get(i).getdBCName().equals(columnName)) {
                dBTUniqueList.get(i).setdBCType(newColumnType);
            }
        }
        //unique Combinations
        for (int i = 0; i < uniqueCombinations.size(); i++) {
            for(int j=0;j<uniqueCombinations.get(i).getColumns().size();j++){
                if (uniqueCombinations.get(i).getColumns().get(j).getdBCName().equals(columnName)) {
                    uniqueCombinations.get(i).getColumns().get(j).setdBCType(newColumnType);
                }
            }
        }
    }

}
