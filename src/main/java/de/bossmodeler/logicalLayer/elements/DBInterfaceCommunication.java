package de.bossmodeler.logicalLayer.elements;

import de.bossmodeler.dbInterface.PostgreSQLSchnittstelle;
import de.bossmodeler.dbInterface.Schnittstelle;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * @author Serdar Nurgün
 * @version 1.0.3
 * <p>
 * Since 1.0.0 updated javadoc annotations. NW
 * Since 1.0.1 changed DataBaseMetaData to Schnittstelle. SH
 * Since 1.0.2 changed constructor. SH
 * Since 1.0.3 put some methods to Schnittstelle and deleted the MetaData attribute. SH
 */

public class DBInterfaceCommunication  {
	
	/** Object of Schnittstelle */
	private Schnittstelle inter;
	
	/**
	 * Calls openConnection of PostgreSQLSchnittstelle inter to open database connection and fetches meta data.
	 * <p>
	 * This method establishes a database connection with given parameters and the given PostgreSQLSchnittstelle inter.
	 * 
	 * @param inter object of PostgreSQLSchnittstelle
	 */
	public DBInterfaceCommunication(Schnittstelle inter) throws SQLException
	{
		this.inter = inter;
		inter.openConnection();
	}
	
	/**
	 * Returns DatabaseMetaData meta
	 * 
	 * @return meta object of DatabaseMetaData
	 * @throws SQLException 
	 * @see		DatabaseMetaData
	 */
	public DatabaseMetaData getDatabaseMetaData() throws SQLException{
		return inter.getMetaData();
	}
	
	/**
	 * Returns SQL script for db/schema creation on db.
	 * 
	 * @param tables tables to create
	 * @param schema schema if needed
	 * @param newSchema boolean indicates if new schema needs to be created
	 * @return String containing SQL script 
	 * @throws DBLanguageNotFoundException 
	 */
	public static String getSQLToDB(LinkedList<DBTable> tables, String schemaName, boolean newSchema) throws DBLanguageNotFoundException {
		return getSQLCode(tables, newSchema, schemaName, new PostgreSQLSchnittstelle());
	}
	
	/**
	 * Returns SQL script for table creation on db.
	 *
	 * @param tables tables to create
	 * @param caseSensitive the case sensitive
	 * @param schemaName the schema name
	 * @param sqlGen the sql gen
	 * @return String containing SQL script
	 * @throws DBLanguageNotFoundException the DB language not found exception
	 */
	public static String getSQLCode(LinkedList<DBTable> tables, boolean caseSensitive, String schemaName, Schnittstelle sqlGen) throws DBLanguageNotFoundException{
		/*boolean caseSensitive = false;
		if (JOptionPane.showConfirmDialog(null, XmlClass.getTag("maintainCaseSensitiveNames"), "WARNING",
		        JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
		    caseSensitive = true;
		}*/
		boolean createSchema = !schemaName.equals("");
		
		return sqlGen.generateSQLCodeFromTables(tables, caseSensitive, createSchema, schemaName);
		
	}
	
	/**
	 * Returns SQL script for db/schema creation on db.
	 * 
	 * @param tables tables to create
	 * @return String containing SQL script 
	 * @throws DBLanguageNotFoundException 
	 */
	public static String writeTables (LinkedList<DBTable> tables, Schnittstelle sqlGen, boolean casesensitive, String schemaname) throws DBLanguageNotFoundException{
		return getSQLCode(tables, casesensitive, schemaname, sqlGen);
	}

	/**
	 * Reestablish connection.
	 *
	 * @throws SQLException the SQL exception
	 */
	public void reestablishConnection() throws SQLException{
		this.inter.reestablishConnection();
	}

	/**
	 * Close connection.
	 *
	 * @throws SQLException the SQL exception
	 */
	public void closeConnection() throws SQLException {
		inter.closeConnection();
	}

	/**
	 * Gets the table names.
	 *
	 * @return the table names
	 * @throws SQLException the SQL exception
	 */
	public LinkedList<String> getTableNames() throws SQLException {
		return this.inter.getTableNames();
	}

	/**
	 * Initialize tables.
	 *
	 * @return the linked list
	 * @throws SQLException the SQL exception
	 */
	public LinkedList<DBTable> initializeTables() throws SQLException {
		return this.inter.initializeTables();
	}

	/**
	 * Gets the database.
	 *
	 * @return the database
	 * @throws SQLException the SQL exception
	 */
	public LinkedList<String> getDatabase() throws SQLException{
		return this.inter.getDatabase();
	}

	/**
	 * Returns all present databases.
	 * 
	 * @return LinkedList of String which contains all present databases.
	 */	
	public LinkedList<String> getAllDatabase() throws SQLException{
		return this.inter.getAllDatabase();
	}

	/**
	 * Returns all present schemata for current database.
	 * 
	 * @return LinkedList of String which contains all present schemata of current database.
	 */	
	public LinkedList<String> getDBSchemata() throws SQLException{
		return this.inter.getDBSchemata();
	}

	/**
	 * Returns all empty schemata for given database.
	 * 
	 * @return LinkedList of String which contains all empty schemata of current database.
	 */	
	public LinkedList<String> getDBSchemata(String db) throws SQLException{
		return this.inter.getDBSchemata(db);
	}

	/**
	 * Returns all present schemata for given database.
	 * 
	 * @return LinkedList of String which contains all present schemata of current database.
	 */	
	public LinkedList<String> getAllDBSchemata(String db) throws SQLException {
		return this.inter.getAllDBSchemata(db);
	}

	/**
	 * Writes current model to database.
	 * <p>
	 * This method converts the current model in a SQL script and executes it on the database via query.
	 * Whether to create a new database and/or schema can be chosen.
	 * 
	 * @param database name of the database
	 * @param schema name of the schema
	 * @param css boolean which indicates if case sensitivity was chosen (true = yes; false = no)
	 * @param dbExists boolean which indicates if a new database has to be created  (true = yes; false = no)
	 * @param newSchema boolean which indicates if a new schema has to be created  (true = yes; false = no)	 * 
	 * @throws DBLanguageNotFoundException 
	 */
	public void writeTablesToDB(LinkedList<DBTable> tables,String database, String schema, boolean css,
			boolean dbExists, boolean newSchema) throws SQLException{
		this.inter.writeTablesToDB(tables,database, schema, css, dbExists, newSchema);
	}
	

}
	

	

