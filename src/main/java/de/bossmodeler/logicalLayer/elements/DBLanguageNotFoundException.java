/**
 * 
 */
package de.bossmodeler.logicalLayer.elements;

/**
 * @author Stefan
 *
 */
public class DBLanguageNotFoundException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6567852499444815589L;

	public DBLanguageNotFoundException(String language){
		super(language);
	}
}
