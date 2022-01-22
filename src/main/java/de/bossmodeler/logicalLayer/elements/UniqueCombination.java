package de.bossmodeler.logicalLayer.elements;

import java.util.LinkedList;

public class UniqueCombination {

	private LinkedList<DBColumn> columns;
	private String shortForm;
	
	public UniqueCombination(LinkedList<DBColumn> columns, String shortForm){
		this.columns = columns;
		this.shortForm = shortForm;
	}

	/**
	 * @return the columns
	 */
	public LinkedList<DBColumn> getColumns() {
		return columns;
	}

	/**
	 * @param columns the columns to set
	 */
	public void setColumns(LinkedList<DBColumn> columns) {
		this.columns = columns;
	}

	/**
	 * @return the shortForm
	 */
	public String getShortForm() {
		return shortForm;
	}

	/**
	 * @param shortForm the shortForm to set
	 */
	public void setShortForm(String shortForm) {
		this.shortForm = shortForm;
	}
	
	public boolean equals(UniqueCombination uc){
		
		if(shortForm.equals(uc.getShortForm())){
			return true;
		}
		return false;
	}
	
	
}
