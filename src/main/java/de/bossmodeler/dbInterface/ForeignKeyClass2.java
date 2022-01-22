package de.bossmodeler.dbInterface;

import de.bossmodeler.logicalLayer.elements.DBColumn;

import java.util.LinkedList;

/**
 * ForeignKeyClass2 is a service class for creating foreign keys from database input.
 * 
 * @author Nils Wagner, Serdar Nurgün
 * @version 1.0.1
 * 			<p>
 * 			Since 1.0.1 Added javadoc annotations. NW
 * 			Since 1.0.1	Moved to dbInterface. SH
 * 			
 */
public class ForeignKeyClass2 {
	
	/** The column. */
	DBColumn column;
	
	/** The list of DBColumn ls */
	LinkedList<DBColumn> ls = new LinkedList<DBColumn>();
	
    /**
	 * Instantiates a new foreign key class2.
	 *
	 * @param column
	 *            the column
	 */
    ForeignKeyClass2(DBColumn column){
		this.column = column;
	}

	/**
	 * Gets the column.
	 *
	 * @return the column
	 * @see DBColumn
	 */
	public DBColumn getColumn() {
		return column;
	}

	/**
	 * Sets the column.
	 *
	 * @param column the new column
	 */
	public void setColumn(DBColumn column) {
		this.column = column;
	}

	/**
	 * Gets the list of DBColumn ls.
	 *
	 * @return the ls
	 */
	public LinkedList<DBColumn> getLs() {
		return ls;
	}

	/**
	 * Sets the ls.
	 *
	 * @param ls
	 *            the new ls
	 */
	public void setLs(LinkedList<DBColumn> ls) {
		this.ls = ls;
	}

}
