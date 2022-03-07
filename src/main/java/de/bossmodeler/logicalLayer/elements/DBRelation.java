package de.bossmodeler.logicalLayer.elements;

import javax.swing.*;
import java.util.LinkedList;

public class DBRelation {
	public static final int CARDINALITYONE = 1;
	public static final int CARDINALITYMANY = 0;
	public static final int RELATIONTYPECAN = 1;
	public static final int RELATIONTYPEMUST = 0;
	
	private DBTable tableA;
	private DBTable tableB;
	private DBTable strongTable;
	private int cardinalityA;
	private int cardinalityB;
	private int relationTypeA;
	private int relationTypeB;
	
	private DBTable fkTable;
	private LinkedList<DBColumn> fkColumn;
	
	public DBRelation(DBTable tableA, DBTable tableB){
		this.tableA = tableA;
		this.tableB = tableB;
	}
	
	public DBRelation(DBTable tableA, DBTable tableB, DBTable strongTable, int cA, int cB, int rA, int rB, DBTable fkTable, LinkedList<DBColumn> fkColumn){
		this.tableA = tableA;
		this.tableB = tableB;
		this.cardinalityA = cA;
		this.cardinalityB = cB;
		this.strongTable = strongTable;
		this.relationTypeA = rA;
		this.relationTypeB = rB;
		this.fkTable = fkTable;
		this.fkColumn = fkColumn;
		if(fkTable == null || fkColumn == null){
			//should never happen
			JOptionPane.showMessageDialog(null, "fkTable or fkColumn == null!!!", "ERROR", JOptionPane.ERROR_MESSAGE);
		}
	}
	/**
	 * @return the tableA
	 */
	public DBTable getTableA() {
		return tableA;
	}
	/**
	 * @param tableA the tableA to set
	 */
	public void setTableA(DBTable tableA) {
		this.tableA = tableA;
	}
	/**
	 * @return the tableB
	 */
	public DBTable getTableB() {
		return tableB;
	}
	/**
	 * @param tableB the tableB to set
	 */
	public void setTableB(DBTable tableB) {
		this.tableB = tableB;
	}
	/**
	 * @return the strongTable
	 */
	public DBTable getStrongTable() {
		return strongTable;
	}
	/**
	 * @param strongTable the strongTable to set
	 */
	public void setStrongTable(DBTable strongTable) {
		this.strongTable = strongTable;
	}
	/**
	 * @return the cardinalityA
	 */
	public int getCardinalityA() {
		return cardinalityA;
	}
	/**
	 * @param cardinalityA the cardinalityA to set
	 */
	public void setCardinalityA(int cardinalityA) {
		this.cardinalityA = cardinalityA;
	}
	/**
	 * @return the cardinalityB
	 */
	public int getCardinalityB() {
		return cardinalityB;
	}
	/**
	 * @param cardinalityB the cardinalityB to set
	 */
	public void setCardinalityB(int cardinalityB) {
		this.cardinalityB = cardinalityB;
	}
	/**
	 * @return the relationTypeA
	 */
	public int getRelationTypeA() {
		return relationTypeA;
	}
	/**
	 * @param relationTypeA the relationTypeA to set
	 */
	public void setRelationTypeA(int relationTypeA) {
		this.relationTypeA = relationTypeA;
	}
	/**
	 * @return the relationTypeB
	 */
	public int getRelationTypeB() {
		return relationTypeB;
	}
	/**
	 * @param relationTypeB the relationTypeB to set
	 */
	public void setRelationTypeB(int relationTypeB) {
		this.relationTypeB = relationTypeB;
	}

	public LinkedList<DBColumn> getForeignKey() {
		return this.fkColumn;
	}

	public DBTable getForeignKeyTable() {
		return this.fkTable;
	}

	/**
	 * @return the fkTable
	 */
	public DBTable getFkTable() {
		return fkTable;
	}

	/**
	 * @param fkTable the fkTable to set
	 */
	public void setFkTable(DBTable fkTable) {
		this.fkTable = fkTable;
	}

	/**
	 * @return the fkColumn
	 */
	public LinkedList<DBColumn> getFkColumn() {
		return fkColumn;
	}

	/**
	 * @param fkColumn the fkColumn to set
	 */
	public void setFkColumn(LinkedList<DBColumn> fkColumn) {
		this.fkColumn = fkColumn;
	}
	
	/**
	 * Builds the foreign key constraint name for this relation.
	 * @return the foreign key constraint name
	 */
	public String buildConstraintName() {
		String newConstraintName = getForeignKeyTable().getdBTName() + "_";
		if(getTableA().getdBTName().equals(getForeignKeyTable().getdBTName())){
			for(int i=0;i<getTableA().getdBTFKeyList().size();i++){
				if(getForeignKey().contains(getTableA().getdBTFKeyList().get(i))){
					newConstraintName += getTableA().getdBTFKeyList().get(i).getdBCName() + "_";
				}
			}
		} else {
			for(int i=0;i<getTableB().getdBTFKeyList().size();i++){
				if(getForeignKey().contains(getTableB().getdBTFKeyList().get(i))){
					newConstraintName += getTableB().getdBTFKeyList().get(i).getdBCName() + "_";
				}
			}
		}
		
		newConstraintName += "fkey";
		return newConstraintName;
	}
	
	
}
