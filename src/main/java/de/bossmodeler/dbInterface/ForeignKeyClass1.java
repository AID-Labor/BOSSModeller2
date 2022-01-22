package de.bossmodeler.dbInterface;

import java.util.LinkedList;

/**
 * ForeignKeyClass1 is a service class for creating foreign keys from database input.
 * 
 * @author Nils Wagner, Serdar Nurgün
 * @version 1.0.2
 * 			<p>
 * 			Since 1.0.0 Added javadoc annotations. NW
 * 			Since 1.0.1	Moved to dbInterface. SH
 * 			
 */

public class ForeignKeyClass1 {
	
	String tablename = "";
	LinkedList<ForeignKeyClass2> list = new LinkedList<ForeignKeyClass2>();
	
	public ForeignKeyClass1(String tablename){
		this.tablename = tablename;
	}


	/**
	 * Gets the tablename.
	 *
	 * @return the tablename
	 */
	public String getTablename() {
		return tablename;
	}


	/**
	 * Sets the tablename.
	 *
	 * @param tablename the tablename
	 */
	public void setTablename(String tablename) {
		this.tablename = tablename;
	}



	public LinkedList<ForeignKeyClass2> getList() {
		return list;
	}


	/**
	 * Sets the list.
	 *
	 * @param list the list
	 * @see ForeignKeyClass2
	 */
	public void setList(LinkedList<ForeignKeyClass2> list) {
		this.list = list;
	}



	
}


