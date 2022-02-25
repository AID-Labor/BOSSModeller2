package de.bossmodeler.logicalLayer.elements;

//import de.bossmodeler.GUI.buttons.XmlClass; *Changed to utilise a new language loader
import de.bossmodeler.dbInterface.PostgreSQLSchnittstelle;
import de.bossmodeler.dbInterface.Schnittstelle;
import de.snaggly.bossmodellerfx.BOSS_Strings;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 This source code was modified to utilise a different Language loader.
 The changed lines have been highlighted with the appropriate annotation.
 */

/**
 * DBLogicalAdministration is a service class to create and handle DBTables and corresponding DBRelations and provide further methods for handling
 * database connection.
 * 
 * @author Nils Wagner, Serdar Nurgün
 * @version 1.0.4
 * 			<p>
 * 			Since 1.0.3
 * 			<br>	- Added attribute notInitializedTables. SH
 * 			<p>
 * 			Since 1.0.2  
 * 			<br>	- changed inter to Schnittstelle. SH
 * 			<br>	- improved save as SQL file system. SH
 * 			<br>	- exported some methods to Schnittstelle. SH
 * 			<p>
 * 			Since 1.0.1 Added javadoc annotations. NW
 * 			<p>
 *          Since 1.0.0 -implemented initializeTables -Added support for weak
 *          entities -implemented setting of dBTPKeyList (List of all Primary
 *          Keys of a DBTable) 
 *          
 *          
 */

public class DBLogicalAdministration {

	/** A list of all tables (<code>DBTable</code>).
	 *  @see 	DBTable */
	private LinkedList<DBTable> tables = new LinkedList<DBTable>();
	
	/** A list of tables which are not selected to initialize (<code>DBTable</code>)
	 * @see 	DBTable */
	private LinkedList<DBTable> notInitializedTables;

	/** A list of all relations (<code>DBRelation</code>). 
	 * @see 	DBRelation */
	private LinkedList<DBRelation> relations = new LinkedList<DBRelation>();
	
	/** The save sql location. */
	public static File saveSQLLocation = new File("");
	
	
	
	/**
	 * DBInterfaceCommunication (<code>DBInterfaceCommunication</code>).
	 *  @see 	DBInterfaceCommunication 
	 */
	DBInterfaceCommunication interCom;
	
	/** Saving path */
	private static File saveAsPath;
	
	/** 
	 * Constructor with DBInterfaceCommunication object.
	 * 
	 * @param interCom object of DBInterfaceCommunication 
	 * @see		DBInterfaceCommunication
	 */
	public DBLogicalAdministration(DBInterfaceCommunication interCom)throws SQLException {
		this.interCom = interCom;
		this.notInitializedTables = new LinkedList<DBTable>();
		//this.inter = new PostgreSQLSchnittstelle();
		//this.meta = interCom.getDatabaseMetaData();
	}
	
	/** 
	 * Constructor which creates new DBInterfaceCommunication.
	 * 
	 * @param host host address
	 * @param port port
	 * @param db database
	 * @param user user name
	 * @param pass password
	 * @param schema schema 
	 */
	/*public DBLogicalAdministration(String host, String port, String db,
			String user, String pass, String schema) throws SQLException {
		Schnittstelle inter = new PostgreSQLSchnittstelle(host,port,db,user,pass,schema);
		this.interCom = new DBInterfaceCommunication(inter);
		//this.meta = interCom.getDatabaseMetaData();
	}*/
	
	
	
	/** 
	 * Creates connection to database and sets meta data.
	 * 
	 * @param host host address
	 * @param port port
	 * @param db database
	 * @param user user name
	 * @param pass password
	 * @param schema schema 
	 */
	public void setConnection(String host, String port, String db,
			String user, String pass, String schema)throws SQLException{
		Schnittstelle inter = new PostgreSQLSchnittstelle(host,port,db,user,pass,schema);
		this.interCom = new DBInterfaceCommunication(inter);
		//this.meta = interCom.getDatabaseMetaData();
	}
	
	/** 
	 * Reestablishes connection with saved credentials, if set. 
	 */
	public void reestablishConnection()throws SQLException{
		this.interCom.reestablishConnection();
		//this.meta = this.interCom.getDatabaseMetaData();
	}
	
	
	/** 
	 * Adds an object of <code>DBTable</code> to list of tables <code>tables</code>.
	 * 
	 * @param table : DBTable object to add to the list
	 * @see 	DBTable
	 **/
	public void addTable(DBTable table) {
		tables.add(table);
	}
	
	/** 
	 * Removes an object of <code>DBTable</code> from list of tables <code>tables</code>.
	 * 
	 * @param table : DBTable object to remove from the list
	 * @see 	DBTable
	 */
	public void removeTable(DBTable table) {
		tables.remove(table);
	}
	
	/** 
	 * Returns list of tables <code>tables</code>. 
	 * 
	 * @return tables LinkedList of DBTable (<code>tables</code>)
	 * @see 	DBTable
	 */
	public LinkedList<DBTable> getTables() {
		return tables;
	}
	
	
	/**
	 * Searches for duplicate relation in LinkedList <code>relations</code>.
	 * <p>
	 * This method returns true if a relation between DBTable a and b already exists in exact or crossed order. (b,a or a,b).
	 *
	 * @return boolean true when relation already exists (b,a or a,b)
	 * @see 		DBTable
	 * @see			DBRelation	 
	 */
	public boolean searchDuplicateRelation(DBRelation relation) {
		DBTable a,b;
		a= relation.getTableA();
		b= relation.getTableB();
		if (a == null || b == null)
			return true;
		for (int i = 0; i < relations.size(); i++) {
			if (relations.get(i).getTableA().getdBTName()
					.equals(a.getdBTName())
					&& relations.get(i).getTableB().getdBTName()
							.equals(b.getdBTName())
					&& relations.get(i).getForeignKey()
							.equals(relation.getForeignKey())
					&& relations.get(i).getForeignKeyTable().getdBTName()
							.equals(relation.getForeignKeyTable().getdBTName())) {
				return true;
			}
			if (relations.get(i).getTableB().getdBTName()
					.equals(a.getdBTName())
					&& relations.get(i).getTableA().getdBTName()
							.equals(b.getdBTName())
					&& relations.get(i).getForeignKey()
							.equals(relation.getForeignKey())
					&& relations.get(i).getForeignKeyTable().getdBTName()
							.equals(relation.getForeignKeyTable().getdBTName())) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Removes a relation <code>DBRelation</code> from list of relations.
	 * 
	 * @param DBRelation relations
	 * @see		DBRelation
	 */
	public void removeRelation(DBRelation relation) {
		relations.remove(relation);
	}
	
	/** 
	 * Returns list of relations <code>relations</code>. 
	 * 
	 * @return relations LinkedList of DBRelation (<code>relations</code>)
	 * @see 	DBRelation
	 */
	public LinkedList<DBRelation> getRelations() {
		return relations;
	}
	
	/**
	 * This method calls initializeTables and initializeRelations.
	 * 
	 * @see 	{@link #initializeTables() initializeTables}
	 * @see 	{@link #initializeRelations() initializeRelations}
	 */
	public void initializeAll() throws SQLException {
		initializeTables();
		initializeRelations();
		this.interCom.closeConnection();
	}
	
	/**
	 * Creates all tables and throws out all tables not in given LinkedList.
	 * 
	 * @param tables LinkedList String tables which remain in list <code>tables</code>.
	 */
	public void initializeSome(LinkedList<String> tables) throws SQLException {
		initializeTables();
		LinkedList<DBTable> hilftable = new LinkedList<DBTable>();
		// Adding tables to a help list and set the original list to the help list
		for (int i = 0; i < tables.size(); i++) {
			for (int j = 0; j < this.tables.size(); j++) {
				if (tables.get(i).equals(this.tables.get(j).getdBTName())) {
					hilftable.add(this.tables.get(j));
					break;
				}
			}
		}
		
		// add not initialized tables to the notInitializedTables list
		for(int i=0;i<this.tables.size();i++){
			if(!hilftable.contains(this.tables.get(i))){
				this.notInitializedTables.add(this.tables.get(i));
			}
		}
		
		this.tables = hilftable;
	}
	
	/**
	 * Returns all table names fetched from database in a LinkedList of String.
	 * 
	 * @return LinkedList of String containing table names of database.
	 */
	public LinkedList<String> getTableNames() throws SQLException {
		return this.interCom.getTableNames();
	}
	
	/**
	 * Sets all tables <code>DBTable</code> and corresponding columns <code>DBColumn</code> to <code>tables</code>.
	 * <p>
	 * This method creates all tables, then all corresponding columns with data type and constraints. 
	 * All information derives from the meta data of the database.
	 * 
	 * @see 	DBTable
	 * @see 	DBColumn
	 */
	public void initializeTables() throws SQLException {
		this.tables = this.interCom.initializeTables();
	}
	
	/**
	 * Sets all relations <code>DBRelation</code> with corresponding tables <code>DBTable</code> to <code>relations</code>.
	 * <p>
	 * This method creates all relations with cardinalities, relation types and strong table. 
	 * All information derives from the NOT NULL and UNIQUE constrains of the tables foreign keys.
	 * 
	 * @see 	DBTable
	 * @see 	DBRelation
	 */
	public void initializeRelations() throws SQLException {

		for (int i = 0; i < tables.size(); i++) {
			for (int j = 0; j < tables.size(); j++) {
				if (i == j) {
					continue;
				}
				for (int f = 0; f < tables.get(i).getdBTFKeyList().size(); f++) {
					DBTable tableA = tables.get(i);
					DBColumn columnA = tables.get(i).getdBTFKeyList().get(f);

					// Finding referenced table of fk tableB
					DBTable tableB = getTable(tables.get(i).getdBTFKeyList()
							.get(f).getdBCFKRefTableName());

					// Distinguishing relation with NOT NULL and UNIQUE-Constraint

					DBColumn uniqueConstraint = tableA.getUnique(columnA
							.getdBCName());
					int cA;
					int cB;
					int rA;
					int rB;
					DBTable strongTable = null;
					if(tableA != null && tableB != null)
						if (uniqueConstraint != null
								&& (tableA.getdBTPKeyList().size() < 2)) {
							// Case 1:1
							cA = 1;
							cB = 1;
							if (columnA.isdBCNotNull()) {
								// CASE 1:1 MUST:MUST
								rA = 0;
								rB = 0;
								
								addRelation(tableA, tableB,
										strongTable, cA, cB, rA, rB, tableA, columnA);
							} else {
								// CASE 1:1 CAN:MUST
								rA = 1;
								rB = 0;
								addRelation(tableA, tableB,
										strongTable, cA, cB, rA, rB, tableA, columnA);
							}
						}
						// Case 1:N
						else {
							cA = 0;
							cB = 1;
							if (columnA.isdBCNotNull()) {
								// Case 1:N must...can
								rA = 1;
								rB = 0;
								addRelation(tableA, tableB,
										strongTable, cA, cB, rA, rB, tableA, columnA);
							} else {
								// Case 1:N can...can
								rA = 1;
								rB = 1;
								addRelation(tableA, tableB,
										strongTable, cA, cB, rA, rB, tableA, columnA);
							}
						}
				}

			}

		}
		// Setting strong table
		for (int i = 0; i < relations.size(); i++) {
			for (int j = 0; j < relations.get(i).getTableA().getdBTPKeyList()
					.size(); j++) {
				if (relations.get(i).getTableA().getdBTPKeyList().get(j)
						.getdBCFKRefTableName()
						.equals(relations.get(i).getTableB().getdBTName())) {
					relations.get(i).setStrongTable(
							relations.get(i).getTableA());
				}
			}
			for (int j = 0; j < relations.get(i).getTableB().getdBTPKeyList()
					.size(); j++) {
				if (relations.get(i).getTableB().getdBTPKeyList().get(j)
						.getdBCFKRefTableName()
						.equals(relations.get(i).getTableA().getdBTName())) {
					relations.get(i).setStrongTable(
							relations.get(i).getTableB());
				}
			}
		}

	}
	
	/**
	 * Adds the relation. If a Relation already exists the fkColumn will be added to the existing one.
	 *
	 * @param tableA the table a
	 * @param tableB the table b
	 * @param strongTable the strong table
	 * @param cA the c a
	 * @param cB the c b
	 * @param rA the r a
	 * @param rB the r b
	 * @param fkTable the fk table
	 * @param fkColumn the fk column
	 */
	private void addRelation(DBTable tableA, DBTable tableB,
			DBTable strongTable, int cA, int cB, int rA, int rB,
			DBTable fkTable, DBColumn fkColumn) {
		DBRelation relation = checkForExistingRelation(tableA, tableB, fkTable, fkColumn);
		if(relation != null){
			relation.getForeignKey().add(fkColumn);
		} else {
			LinkedList<DBColumn> newFKList = new LinkedList<DBColumn>();
			newFKList.add(fkColumn);
			relation = new DBRelation(tableA, tableB, strongTable, cA, cB, rA, rB, fkTable, newFKList);
			if (!searchDuplicateRelation(relation))
				relations.add(relation);
		}
	}

	/**
	 * Check for existing relation. Returns null if Relation doesn't exist.
	 *
	 * @param tableA the table a
	 * @param tableB the table b
	 * @param fkTable the fk table
	 * @param fkColumn the fk column
	 * @return the DB relation
	 */
	private DBRelation checkForExistingRelation(DBTable tableA, DBTable tableB,
			DBTable fkTable, DBColumn fkColumn) {
		for(int i=0;i<relations.size();i++){
			if(relations.get(i).getTableA().getdBTName().equals(tableA.getdBTName())
					&& relations.get(i).getTableB().getdBTName().equals(tableB.getdBTName())
					&& relations.get(i).getForeignKeyTable().getdBTName().equals(fkTable.getdBTName())
					&& relations.get(i).getForeignKey() != null
					&& relations.get(i).getForeignKey().size() != 0
					&& relations.get(i).getForeignKey().getFirst().getdBCFKConstraintName().equals(fkColumn.getdBCFKConstraintName())){
				return relations.get(i);
			}
		}
		return null;
	}

	/**
	 * Saves current model as a SQL script. Data extension ".sql".
	 * <p>
	 * The method gets all tables and calls DBInterfaceCommunication.writeTables() for the SQL script. 
	 * Afterwards the user has to choose the save path of the data.
	 *
	 * @param parentframe Frame in which the saving dialog is shown
	 * @param tables All tables which are processed
	 * @param projectName Used as the default name of the SQL script
	 * @param sqlGen the sql gen
	 * @param casesensitive the casesensitive
	 * @param schemaname the schemaname
	 * @throws DBLanguageNotFoundException the DB language not found exception
	 * @see 	{@link DBInterfaceCommunication#writeTables(LinkedList) DBInterfaceCommunication.writeTables}
	 */
	public static void saveProjectAsSQL(Container parentframe,
			LinkedList<DBTable> tables, String projectName, Schnittstelle sqlGen,
			boolean casesensitive, String schemaname) throws DBLanguageNotFoundException {
		String output = "";
		output = DBInterfaceCommunication.writeTables(tables, sqlGen, casesensitive, schemaname);

		JFileChooser fileChooser = new JFileChooser(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 2847384256559660529L;

			@Override
		    public void approveSelection(){
		        File f = getSelectedFile();
		        if(f.exists() && getDialogType() == SAVE_DIALOG){
					//int result = JOptionPane.showConfirmDialog(this,XmlClass.getTag("dataalreadyexists"),XmlClass.getTag("dataconflict"),JOptionPane.YES_NO_OPTION);
		            // Changed to utilise new language loader
					int result = JOptionPane.showConfirmDialog(this, BOSS_Strings.LEGACY_PROMPT_OVERRIDING_EXISTING_FILE, BOSS_Strings.LEGACY_FILE_CONFLICT,JOptionPane.YES_NO_OPTION);
		            switch(result){
		                case JOptionPane.YES_OPTION:
		                    super.approveSelection();
		                    return;
		                case JOptionPane.NO_OPTION:
		                    return;
		                case JOptionPane.CLOSED_OPTION:
		                    return;
		            }
		        }
		        super.approveSelection();
		    }     
		};
		
		fileChooser.setSelectedFile(new File(saveSQLLocation.getPath().substring(0, saveSQLLocation.getPath().indexOf(saveSQLLocation.getName())) + projectName + ".sql"));
		
   		
   		fileChooser.setFileFilter(new ExtensionFileFilter("SQL",new String[] { "SQL" }));
		//Changed to utilise new language loader
  		//fileChooser.setDialogTitle(XmlClass.getTag("save2") + "\""+ projectName + "\"" + XmlClass.getTag("asSQL"));
		fileChooser.setDialogTitle("" + "\""+ projectName + "\"" + " als .sql speichern.");
   		int userSelection = fileChooser.showSaveDialog(parentframe);
  		if (userSelection == JFileChooser.APPROVE_OPTION) 
   		{
  			File fileToSave = fileChooser.getSelectedFile();
	    	saveAsPath = fileToSave;
	    	if (saveAsPath != null && !saveAsPath.getPath().equals("")){
		   		try {
		   			BufferedWriter out = new BufferedWriter(new FileWriter(saveAsPath));
		   			out.write(output);
		   			out.flush();
		   			out.close();
		   			
		   			saveSQLLocation = saveAsPath;
					//Changed to utilise new language loader
		    		//JOptionPane.showMessageDialog(parentframe,XmlClass.getTag("sqlCreatedSuccessfully"),XmlClass.getTag("success"),
		    		//JOptionPane.INFORMATION_MESSAGE);
					JOptionPane.showMessageDialog(parentframe, BOSS_Strings.LEGACY_SQL_FILE_CREATION_SUCCESS, BOSS_Strings.LEGACY_SUCCESS,
							JOptionPane.INFORMATION_MESSAGE);
		   		} catch (IOException e) {
					//Changed to utilise new language loader
		   			//JOptionPane.showMessageDialog(parentframe,XmlClass.getTag("sqlCouldNotBeSaved"),"Error",JOptionPane.ERROR_MESSAGE);
					JOptionPane.showMessageDialog(parentframe, BOSS_Strings.LEGACY_SQLCOILDNOTBESAVED, BOSS_Strings.LEGACY_ERROR,JOptionPane.ERROR_MESSAGE);
		    	}
			} else{
				//Changed to utilise new language loader
				//JOptionPane.showMessageDialog(parentframe,XmlClass.getTag("sqlCouldNotBeSaved"),"Error",JOptionPane.ERROR_MESSAGE);
				JOptionPane.showMessageDialog(parentframe, BOSS_Strings.LEGACY_SQLCOILDNOTBESAVED, BOSS_Strings.LEGACY_ERROR,JOptionPane.ERROR_MESSAGE);
			}
	   	} else {
			//Changed to utilise new language loader
	   		//JOptionPane.showMessageDialog(parentframe,XmlClass.getTag("sqlHasNotBeenSaved"),"Error",JOptionPane.ERROR_MESSAGE);
			JOptionPane.showMessageDialog(parentframe, BOSS_Strings.LEGACY_SQLCOILDNOTBESAVED, BOSS_Strings.LEGACY_ERROR,JOptionPane.ERROR_MESSAGE);
	   	}
	   	
	}
	
	/**
	 * Returns all present databases without system intern ones.
	 * 
	 * @return LinkedList of String which contains all present databases without system intern ones.
	 */	
	public LinkedList<String> getDatabase() throws SQLException {
		return this.interCom.getDatabase();
	}
	
	/**
	 * Returns all present databases.
	 * 
	 * @return LinkedList of String which contains all present databases.
	 */	
	public LinkedList<String> getAllDatabase() throws SQLException {
		return this.interCom.getAllDatabase();
	}
	/**
	 * Returns all present schemata for current database.
	 * 
	 * @return LinkedList of String which contains all present schemata of current database.
	 */	
	public LinkedList<String> getDBSchemata() throws SQLException {
		return this.interCom.getDBSchemata();
	}
	
	/**
	 * Returns all empty schemata for given database.
	 * 
	 * @return LinkedList of String which contains all empty schemata of current database.
	 */	
	public LinkedList<String> getDBSchemata(String db) throws SQLException {
		return this.interCom.getDBSchemata(db);	
	}
	
	/**
	 * Returns all present schemata for given database.
	 * 
	 * @return LinkedList of String which contains all present schemata of current database.
	 */	
	public LinkedList<String> getAllDBSchemata(String db) throws SQLException {
		return this.interCom.getAllDBSchemata(db);	
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
	 * @see 	DBInterfaceCommunication
	 * @see 	{@link DBInterfaceCommunication#getSQLToDB(LinkedList, String, String, boolean, boolean) DBInterfaceCommunication.getSQLToDB}
	 */
	public void writeTablesToDB(String database, String schema,boolean css, boolean dbExists, boolean newSchema)throws SQLException, DBLanguageNotFoundException {
		this.interCom.writeTablesToDB(tables,database, schema, css, dbExists, newSchema);
	}
	
	/**
	 * Searches a DBTable object in tables and returns it.
	 * <p>
	 * This method searches a table with the given table name in the list <code>tables</code> 
	 * 
	 * @param table table name to search for
	 * @return returns object of DBTable or null if no table was found
	 */
	public DBTable getTable(String table) {
		for (int i = 0; i < tables.size(); i++) {
			if (table.equals(tables.get(i).getdBTName())) {
				return tables.get(i);
			}
		}
		return null;
	}
	
	/**
	 * Returns interCom <code>DBInterfaceCommunication</code>.
	 * 
	 * @returns object of DBInterfaceCommunication
	 */
	public DBInterfaceCommunication getInterCom() {
		return interCom;
	}
	
	/**
	 * Sets interCom <code>DBInterfaceCommunication</code>.
	 * 
	 * @param interCom object of DBInterfaceCommunication which will be set
	 */
	public void setInterCom(DBInterfaceCommunication interCom) {
		this.interCom = interCom;
	}

	/**
	 * @return the notInitializedTables
	 */
	public LinkedList<DBTable> getNotInitializedTables() {
		return notInitializedTables;
	}

	/**
	 * @param notInitializedTables the notInitializedTables to set
	 */
	public void setNotInitializedTables(LinkedList<DBTable> notInitializedTables) {
		this.notInitializedTables = notInitializedTables;
	}

	/**
	 * @param tables the tables to set
	 */
	public void setTables(LinkedList<DBTable> tables) {
		this.tables = tables;
	}

	/**
	 * @param relations the relations to set
	 */
	public void setRelations(LinkedList<DBRelation> relations) {
		this.relations = relations;
	}
}
