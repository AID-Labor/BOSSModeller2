/**
 * 
 */
package de.bossmodeler.dbInterface;

import de.bossmodeler.logicalLayer.elements.DBColumn;
import de.bossmodeler.logicalLayer.elements.DBTable;
import de.bossmodeler.logicalLayer.elements.UniqueCombination;
import de.snaggly.bossmodellerfx.BOSS_Strings;
/**
 This source code was modified to utilise a different Language loader.
 The changed lines have been highlighted with the appropriate annotation.
 */
//import de.bossmodeler.GUI.buttons.XmlClass; *Changed to utilise a new language loader

import javax.swing.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Objects;

/**
 * The MySQLSchnittstelle is the interface for MySQL database systems.
 * 
 * @author Stefan Hufschmidt
 * @version 1.0.0
 * 
 */

/**
 This source code was also modified to offer a method to return a full list of usable schemas.
 The changed lines have been highlighted with the appropriate annotation.
 */
public class MySQLSchnittstelle extends Schnittstelle {


	/**
	 * Instantiates a new mysql schnittstelle and loads the JDBC driver
	 *
	 * @param host the host
	 * @param port the port
	 * @param db the db
	 * @param user the user
	 * @param pass the pass
	 * @param schema the schema
	 * @see {@link MySQLSchnittstelle#loadJdbcDriver() loadJdbcDriver}
	 */
	public MySQLSchnittstelle(String host, String port, String db,
			String user, String pass, String schema){
		super();
		setHost(host);
		setPort(port);
		setDb(db);
		setUser(user);
		setPass(pass);
		setSchema(schema);
		loadJdbcDriver();
	}
	
	/**
	 * Constructor which calls {@link MySQLSchnittstelle#loadJdbcDriver() loadJdbcDriver} 
	 * to load the JDBC driver.
	 */
	public MySQLSchnittstelle(){
		super();
		loadJdbcDriver();
	}
	
	
	/**
	 * Attempts to establish a connection to the given database URL.
	 */
	@Override
	public void openConnection() throws SQLException
	{
		String url = "jdbc:mysql://"+getHost()+":"+getPort()+"/"+getDb()+"?"+
					"user=" + getUser() + "&password=" + getPass() + "&allowMultiQueries=true";
//		//Properties props = new Properties();
//		//props.setProperty("user", getUser());
//		//props.setProperty("password", getPass());
//		//props.setProperty("charset", "UTF-8");
		//connection = DriverManager.getConnection(url,props);
		//connection = DriverManager.getConnection(url,getUser(),getPass());
		connection = DriverManager.getConnection(url);
		//System.out.println(XmlClass.getTag("connectionToDatabaseSuccessful"));		
	}
	
	/**
	 * Executes the given SQL statement, which returns a single ResultSet object.
	 * 
	 * @param cmd SQL statement
	 * @return ResultSet object
	 * @see		ResultSet
	 */	
	@Override
	public ResultSet query(String cmd) throws SQLException{
		Statement stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = stmt.executeQuery(cmd);
		return rs;
	}
	
	/**
	 * Sets connection.
	 * 
	 * @see		Connection
	 */
	@Override
	void setConnection(Connection connection) {
		this.connection = connection;
	}
	
	/**
	 * Returns object of <code>Connection</code>.
	 * 
	 * @return object of Connection
	 * @see		Connection
	 */
	@Override
	public Connection getConnection() {
		return connection;
	}
	
	/**
	 * Loads JDBC driver.
	 */
	@Override
	public void loadJdbcDriver(){
		try{
			Class.forName("com.mysql.jdbc.Driver");
			
			//System.out.println(XmlClass.getTag("loadedDriver"));
		}catch(ClassNotFoundException e){
			// *Change to utilise a new language loader
			//JOptionPane.showMessageDialog(null, XmlClass.getTag("errorLoadingDriver")+e.getMessage(), XmlClass.getTag("errorLoadingDriver"), JOptionPane.ERROR_MESSAGE);
			JOptionPane.showMessageDialog(null, BOSS_Strings.LEGACY_ERROR_LOADING_DRIVER +e.getMessage(), BOSS_Strings.LEGACY_ERROR_LOADING_DRIVER, JOptionPane.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Returns meta data of connection object.
	 * 
	 * @return DatabaseMetaData containing meta data of database
	 * @see 	DatabaseMetaData
	 */
	@Override
	public DatabaseMetaData getMetaData() throws SQLException {
		return connection.getMetaData();
	}
	

	/**
	 * Executes SQL command on Database
	 */
	@Override
	public void execute(String cmd) throws SQLException {
		Statement stmt;
		stmt = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
		stmt.execute(cmd);
	}

	/**
	 * Generated SQL-Code for given tables 
	 */
	@Override
	public String generateSQLCodeFromTables(LinkedList<DBTable> tables, boolean caseSensitive, boolean createSchema, String schemaName) {
		String cs = "";
		if(caseSensitive){
			cs = "`";
		}
		
		//check if check-constraints exists
		/**
		 * Check constraints are now supported after Version 8
		 * @author Omar Emshani
		 */
		boolean checkConstraintsExists = false;
		for(int i=0;i<tables.size();i++){
			for(int j=0;j<tables.get(i).getdBTColumns().size();j++){
				if(tables.get(i).getdBTColumns().get(j).getdBCCheck() != null
						&& !tables.get(i).getdBTColumns().get(j).getdBCCheck().equals("")
						&& !tables.get(i).getdBTColumns().get(j).getdBCCheck().equals("()")){
					checkConstraintsExists = true;
					break;
				}
			}
			if(checkConstraintsExists)
				break;
		}
		
		//show warning if check constraint exists
		/**
		 * Changed to utilise a new language loader
		 */
		if(!caseSensitive && checkConstraintsExists)
			JOptionPane.showMessageDialog(null, BOSS_Strings.LEGACY_MYSQL_CHECK_CONSTRAINT_WARNING, BOSS_Strings.LEGACY_WARNING, JOptionPane.WARNING_MESSAGE);
		else if(caseSensitive && checkConstraintsExists)
			JOptionPane.showMessageDialog(null, BOSS_Strings.LEGACY_MYSQL_CHECK_CONSTRAINT_WARNING + "\n\n" + BOSS_Strings.LEGACY_CHECK_SERVER_CASESENSITIVE, BOSS_Strings.LEGACY_WARNING, JOptionPane.WARNING_MESSAGE);
		else if(caseSensitive)
			JOptionPane.showMessageDialog(null, BOSS_Strings.LEGACY_CHECK_SERVER_CASESENSITIVE, BOSS_Strings.LEGACY_WARNING, JOptionPane.WARNING_MESSAGE);
		/*
		String schema = "";
		if(schemaName == null){
			schema = (String)JOptionPane.showInputDialog(null, XmlClass.getTag("insertSchema"),XmlClass.getTag("insertSchema"), JOptionPane.QUESTION_MESSAGE,null,null,"public");
		} else {
			schema = schemaName;
		}*/
		String sql = "";
		/*if(createSchema){
			sql+="CREATE SCHEMA "+"\""+schema+"\""+";\n\n";
		}
		sql+="SET search_path TO '"+schema+"';\n\n";
		
		for (int i=0; i<tables.size(); i++){
			sql+="DROP TABLE IF EXISTS "+cs+tables.get(i).getdBTName()+cs+" CASCADE; \n";
		}
		sql+="\n";	
		*/	
		for (int i=0; i<tables.size(); i++){
			sql+="CREATE TABLE "+cs+tables.get(i).getdBTName()+cs+" \n( \n";
			for (int c=0; c<tables.get(i).getdBTColumns().size();c++){
				sql+="\t"+cs+tables.get(i).getdBTColumns().get(c).getdBCName()+cs+" "+tables.get(i).getdBTColumns().get(c).getdBCType();
				if(!tables.get(i).getdBTColumns().get(c).isdBCDefault().equals("")){
					sql+=" DEFAULT "+tables.get(i).getdBTColumns().get(c).isdBCDefault();
				}
				//Check constraints in MySQL are noted in own line!, Edit by OE
				/*if(!tables.get(i).getdBTColumns().get(c).getdBCCheck().equals("")){
					sql+=" CHECK"+tables.get(i).getdBTColumns().get(c).getdBCCheck();
				}*/
				if (tables.get(i).getdBTColumns().get(c).isdBCNotNull()){
					sql+=" NOT NULL";	
				}
				if(tables.get(i).getUnique(tables.get(i).getdBTColumns().get(c).getdBCName()) != null
						&& !tables.get(i).getdBTPKeyList().contains(tables.get(i).getUnique(tables.get(i).getdBTColumns().get(c).getdBCName()))){
					sql+=" UNIQUE";
				}
				//Edited by OE
				if(!tables.get(i).getdBTColumns().get(c).getdBCCheck().equals("")){
					sql+=",\n\tCHECK "+tables.get(i).getdBTColumns().get(c).getdBCCheck();
				}
				if (c < tables.get(i).getdBTColumns().size() - 1) {
					sql += ",\n";				
				} else {
					sql += "\n";
				}
			
			}
			
			sql+="); \n\n";
		}
		for(int i=0;i<tables.size();i++){
			if(tables.get(i).getdBTPKeyList().size()==0){
				continue;
			}
			sql+="ALTER TABLE "+cs+tables.get(i).getdBTName()+cs+" ADD PRIMARY KEY "+"("+cs;
			for (int pk=0; pk<tables.get(i).getdBTPKeyList().size();pk++){
				if (pk+1<tables.get(i).getdBTPKeyList().size()){
					sql+=tables.get(i).getdBTPKeyList().get(pk).getdBCName()+cs+", "+cs;
				}
				else {
					sql+=tables.get(i).getdBTPKeyList().get(pk).getdBCName()+cs+");\n";
				}
					
			}
		}
		sql+="\n";
		
		
		for(int i=0;i<tables.size();i++){
			LinkedList<String> constraints = new LinkedList<String>();
			for(int j=0;j<tables.get(i).getdBTFKeyList().size();j++){
				if(!constraints.contains(tables.get(i).getdBTFKeyList().get(j).getdBCFKConstraintName())){
					constraints.add(tables.get(i).getdBTFKeyList().get(j).getdBCFKConstraintName());
				}
			}
			
			for(int j=0;j<constraints.size();j++){
				LinkedList<String> fkColumnNames = new LinkedList<String>();
				LinkedList<String> fkRefColNames = new LinkedList<String>();
				String refTableName = "";
				for(int x=0;x<tables.get(i).getdBTFKeyList().size();x++){
					if(tables.get(i).getdBTFKeyList().get(x).getdBCFKConstraintName().equals(constraints.get(j))){
						fkColumnNames.add(tables.get(i).getdBTFKeyList().get(x).getdBCName());
						fkRefColNames.add(tables.get(i).getdBTFKeyList().get(x).getdBCFKRefName());
						refTableName = tables.get(i).getdBTFKeyList().get(x).getdBCFKRefTableName();
					}
				}
				String hilfsSql = "";
				hilfsSql += "ALTER TABLE " + cs + tables.get(i).getdBTName() + cs + 
						" ADD CONSTRAINT " + cs + constraints.get(j) + cs +
						" FOREIGN KEY (";
				for(int x=0;x<fkColumnNames.size();x++){
					if(x+1 == fkColumnNames.size()){
						hilfsSql += cs + fkColumnNames.get(x) + cs;
					} else {
						hilfsSql += cs + fkColumnNames.get(x) + cs + ",";
					}
				}
				hilfsSql += ") REFERENCES "+ cs + refTableName + cs + "(";
				for(int x=0;x<fkRefColNames.size();x++){
					if(x+1 == fkRefColNames.size()){
						hilfsSql += cs + fkRefColNames.get(x) + cs;
					} else {
						hilfsSql += cs + fkRefColNames.get(x) + cs + ",";
					}
				}
				hilfsSql += ");\n";
				sql += hilfsSql;
				hilfsSql = "";
			}
		}
		/*LinkedList<ForeignKeyClass1> fklist = new LinkedList<ForeignKeyClass1>();
		boolean abfrage = false;
		
		for(int h=0;h<tables.size();h++){
			fklist.add(new ForeignKeyClass1(tables.get(h).getdBTName()));
			for(int j=0;j<tables.get(h).getdBTFKeyList().size();j++){
				abfrage = false;
				for(int k=0;k<fklist.get(h).list.size();k++){
					if(fklist.get(h).list.get(k).getColumn().getdBCFKRefTableName().equals(tables.get(h).getdBTFKeyList().get(j).getdBCFKRefTableName())){
						//Set table name
						fklist.get(h).list.get(k).ls.add(tables.get(h).getdBTFKeyList().get(j));
						abfrage = true;
						break;
					}
				}
				if(abfrage==false){
					fklist.get(h).list.add(new ForeignKeyClass2(tables.get(h).getdBTFKeyList().get(j)));					
					fklist.get(h).list.getLast().ls.add(tables.get(h).getdBTFKeyList().get(j));
				}
			}
			
		}
		
		String hilfsql = "";
		
		for(int i=0;i<fklist.size();i++){
			if(fklist.get(i).list.size()!=0){
				for(int o=0;o<fklist.get(i).list.size();o++){
					sql+="ALTER TABLE "+cs+fklist.get(i).getTablename()+cs+" ADD FOREIGN KEY (";
					for(int p=0;p<fklist.get(i).list.get(o).ls.size();p++){
						sql+=cs+fklist.get(i).list.get(o).ls.get(p).getdBCName()+cs+",";
						hilfsql+=cs+fklist.get(i).list.get(o).ls.get(p).getdBCFKRefName()+cs+",";
					}
					sql=sql.substring(0, sql.length()-1);
					hilfsql=hilfsql.substring(0, hilfsql.length()-1);
					sql+=") REFERENCES "+cs+fklist.get(i).list.get(o).getColumn().getdBCFKRefTableName()+cs+"("+hilfsql+");\n";
					hilfsql = "";
				}
			}
		}*/
		for (int i=0; i<tables.size(); i++){
			if (tables.get(i).getuniqueCombinations().size() != 0) {				
				for (int u = 0; u < tables.get(i).getuniqueCombinations().size(); u++) {
					if (tables.get(i).getuniqueCombinations().get(u).getShortForm() != null){
						sql += "ALTER TABLE "+cs+tables.get(i).getdBTName()+cs+" ADD CONSTRAINT "
							+cs+tables.get(i).getuniqueCombinations().get(u).getShortForm()+cs+" UNIQUE (";
					}else{
						continue;
					}
					for (int fu = 0; fu < tables.get(i).getuniqueCombinations().get(u).getColumns().size(); fu++) {
						if (fu + 1 < tables.get(i).getuniqueCombinations().get(u).getColumns().size()) {
							sql += cs+tables.get(i).getuniqueCombinations().get(u).getColumns().get(fu).getdBCName()+cs+",";
						} else {
							sql += cs+tables.get(i).getuniqueCombinations().get(u).getColumns().get(fu).getdBCName()+cs+");\n";
						}
					}
				
				}
				
			}
		}
		return sql;
	}

	@Override
	public LinkedList<DBTable> initializeTables() throws SQLException {

		// Initialize tables
		LinkedList<DBTable> tables = new LinkedList<DBTable>();

		// Adding all tables to the list
		ResultSet rs = getMetaData().getTables(null, getDb(), null,
				new String[] { "TABLE" });
		while (rs.next())
			tables.add(new DBTable(rs.getString("TABLE_NAME")));

		// Creating all columns in every table
		for (int i = 0; i < tables.size(); i++) {
			ResultSet rstemp = getMetaData().getColumns(null, getDb(), tables.get(i)
					.getdBTName(), null);

			while (rstemp.next()) {
				String columnName = rstemp.getString("COLUMN_NAME");
				String columnType = null;
				if (rstemp.getString("TYPE_NAME").equals("int2")) {
					columnType = "smallint";
				} else if (rstemp.getString("TYPE_NAME").equals("int4")) {
					columnType = "integer";
				} else if (rstemp.getString("TYPE_NAME").equals("int8")) {
					columnType = "bigint";
				} else if (rstemp.getString("TYPE_NAME").equals("float4")) {
					columnType = "float";
				} else if (rstemp.getString("TYPE_NAME").equals("VARCHAR")) { //Has to be varchar(255), otherwise SQL error, Edited by OE
					columnType = "varchar(255)";
				} else {
					columnType = rstemp.getString("TYPE_NAME").toLowerCase(); //Edited by OE
				}

				String columnCheck = "";
				String columnRefTableName = "";
				String columnRefName = "";
				String columnFKConstraintName = "";

				// Setting foreign key-infos
				ResultSet rsFK = getMetaData().getImportedKeys(null, getDb(),
						tables.get(i).getdBTName());
				while (rsFK.next()) {
					if (columnName.equals(rsFK.getString("FKCOLUMN_NAME"))) {
						columnFKConstraintName = rsFK.getString("FK_NAME");
						columnRefTableName = rsFK.getString("PKTABLE_NAME");
						columnRefName = rsFK.getString("PKCOLUMN_NAME");
						break;
					}
				}
				
				
				// Setting dbCNotNull (NOT NULL Constraint)
				boolean columnNotNull = true;
				if (rstemp.getInt("NULLABLE") == 1)
					columnNotNull = false;

				// Setting default value
				String columnDefault = rstemp.getString("COLUMN_DEF");
				if (columnDefault == null)
					columnDefault = "";
				tables.get(i).addColumn(
						new DBColumn(columnName, columnType, columnCheck,
								columnNotNull, columnDefault,
								columnRefTableName, columnRefName, columnFKConstraintName));
			}

			// Setting dBTPKeyList (Contains all primary keys (object of DBColumn) of the table)
			ResultSet rsPK = getMetaData().getPrimaryKeys(null, getDb(), tables.get(i)
					.getdBTName());
			while (rsPK.next()) {
				for (int j = 0; j < tables.get(i).getdBTColumns().size(); j++)
					if (tables.get(i).getdBTColumns().get(j).getdBCName()
							.equals(rsPK.getString("COLUMN_NAME"))) {
						tables.get(i).addPKey(
								tables.get(i).getdBTColumns().get(j));						
					}
			}
			
			// Add single primary key to single unique list if not present
			if (tables.get(i).getdBTPKeyList().size() == 1){
				boolean duplicate=false;
				DBColumn pk2= tables.get(i).getdBTPKeyList().getFirst();
				for(DBColumn pk1: tables.get(i).getdBTUniqueList()){
					if (pk1.getdBCName().equals(pk2.getdBCName())){
						duplicate=true;
						break;
					}
				}
				if(!duplicate){
					tables.get(i).addUniqueList(pk2);
				}
			}
	

			// Setting dBTFKeyList (Contains all foreign keys (object of DBColumn) of the table)
			for (int j = 0; j < tables.get(i).getdBTColumns().size(); j++) {
				if (!tables.get(i).getdBTColumns().get(j).getdBCFKRefName()
						.equals("")) {
					tables.get(i).addFKey(tables.get(i).getdBTColumns().get(j));
				}
			}
		}
		
		// Setting weak entity flag
		for (int i = 0; i < tables.size(); i++) {
			for (int m = 0; m < tables.get(i).getdBTPKeyList().size(); m++) {
				for (int o = 0; o < tables.get(i).getdBTFKeyList().size(); o++) {
					if (tables.get(i).getdBTPKeyList().get(m).getdBCName().equals(tables.get(i).getdBTFKeyList().get(o).getdBCName()))
					{
						tables.get(i).setdBTWeakEntity(true);
						break;
					}

				}
			}
		}

		// Setting CHECK-Constraint
		// CHECK CONSTRAINTS ARE NOT SUPPORTED BY MYSQL
		/**
		 * MySQL support check constraints. Since 8.0.16
		 * Edited to retrieve Check Constraints
		 * @author Omar Emshani
		 */
		ResultSet cc = query(
				"select cc.CHECK_CLAUSE, tc.TABLE_NAME " +
				"from  information_schema.TABLE_CONSTRAINTS tc " +
				"inner join information_schema.CHECK_CONSTRAINTS cc " +
				"on tc.CONSTRAINT_NAME = cc.CONSTRAINT_NAME " +
				"where tc.TABLE_SCHEMA  = '"+getDb()+"';");
		while (cc.next()) {
			String checkConstraint = cc.getString("CHECK_CLAUSE");
			int columnNameIndex = checkConstraint.indexOf("`")+1;
			String dbColumnName =  checkConstraint.substring(columnNameIndex, checkConstraint.indexOf("`", columnNameIndex));
			DBColumn dbColumn = Objects.requireNonNull(getTable(tables, cc.getString("TABLE_NAME"))).getColumn(dbColumnName);
			if (dbColumn!=null) {
				dbColumn.setdBCCheck(checkConstraint.replace("`", ""));
			}
		}

		// Setting UNIQUE-Constraints

		ResultSet uc = query("SELECT a.constraint_name,a.column_name,a.table_name "
				+ "FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE a, INFORMATION_SCHEMA.TABLE_CONSTRAINTS b "
				+ "WHERE a.constraint_name = b.constraint_name "
				+ "AND a.table_schema = b.table_schema "
				+ "AND a.table_name = b.table_name "
				+ "AND b.constraint_type = 'UNIQUE' "
				+ "AND a.table_schema = '"+getDb()+"';");
		ArrayList<String[]> aResult = new ArrayList<String[]>();
		while(uc.next()){
			String[] a = new String[3];
			a[0] = uc.getString("table_name");
			a[1] = uc.getString("constraint_name");
			a[2] = uc.getString("column_name");
			
			aResult.add(a);	
		}
		LinkedList<String> tableList = new LinkedList<String>();

		for (String[] o: aResult){
			boolean checktablename = false;
			for(int i=0;i<tableList.size();i++){
				if (o[0].equals(tableList.get(i))) {
					checktablename = true;
					break;
				}
			}if (!checktablename) {
				tableList.add(o[0]);
			}
		}
		
		for(String o: tableList){
			LinkedList<String> constraintList = new LinkedList<String>();
			for(String[] b: aResult){
				boolean checkconstraintname = false;
				if(b[0].equals(o)){
					for(int i=0;i<constraintList.size();i++){
						checkconstraintname = false;
						if(b[1].equals(constraintList.get(i))){
							checkconstraintname = true;
							break;
						}
					}if(!checkconstraintname){
						constraintList.add(b[1]);
					}
				}
			}
			for(String a: constraintList){
				LinkedList<String> columnList = new LinkedList<String>();
				for(String[] c: aResult){
					if(c[1].equals(a)){
						columnList.add(c[2]);
					}
				}
				if(columnList.size()<2){
					getTable(tables,o).addUniqueList(getTable(tables,o).getColumn(columnList.get(0)));
				}else{
					LinkedList<DBColumn> ucc = new LinkedList<DBColumn>();
					for(int n=0;n<columnList.size();n++){
						ucc.add(getTable(tables,o).getColumn(columnList.get(n)));						
					}
					getTable(tables,o).addUniqueCombination(new UniqueCombination(ucc,a));
				}
			}
			
			
		}
		// Setting uniqueCombination for PK
		LinkedList<DBColumn> ucPk = new LinkedList<DBColumn>();
		for (DBTable table : tables) {
			if (table.getdBTPKeyList().size() > 1) {
				for (DBColumn col : table.getdBTPKeyList()) {
					ucPk.add(col);
				}
				table.getuniqueCombinations().add(new UniqueCombination(ucPk, null));
			}
		}
		// Deleting default values of data type "serial"
		for (DBTable table : tables) {
			for (DBColumn col : table.getdBTColumns()) {
				if(col.getdBCType().equals("serial")){
					col.setdBCDefault("");
				}
			}
		}
		
		return tables;
	}
	
	private DBTable getTable(LinkedList<DBTable> tables, String tableName){
		for(DBTable t: tables){
			if(t.getdBTName().equals(tableName)){
				return t;
			}
		}
		return null;
	}

	//Edited to only show tables of set DB, Fixed by OE
	@Override
	public LinkedList<String> getTableNames() throws SQLException{
		LinkedList<String> helpList = new LinkedList<String>();
		ResultSet rs = getMetaData().getTables(getDb(), "", null,
				new String[] { "TABLE" });
		while (rs.next())
			helpList.add(rs.getString("TABLE_NAME"));
		return helpList;
	}

	//Edited to use SHOW DATABSES query instead. Fixed by OE
	@Override
	public LinkedList<String> getDatabase() throws SQLException {
		if(connection.isClosed())
			reestablishConnection();
		LinkedList<String> s = new LinkedList<String>();
		ResultSet dbc = connection.createStatement().executeQuery("SHOW DATABASES");
		while (dbc.next()) {
			s.add(dbc.getString(1));
		}
		
		LinkedList<String> helplist = new LinkedList<String>();
		for (String dbName : s) {
			if (!dbName.equals("template0")
					&& !dbName.equals("template1")
					&& !dbName.equals("information_schema")
					&& !dbName.equals("mysql")
					&& !dbName.equals("performance_schema")
					&& !dbName.equals("sys")
					&& schemaEmpty(dbName)) {
				helplist.add(dbName);
			}
		}	
		closeConnection();
		return helplist;
	}

	//Method modified to also ignore system DBs. This change was necessary,
	// as this class did not offer a method to show filled DBs too.
	//System DBs are not in use.
	//Edited to use SHOW DATABSES query instead. Fixed by OE
	@Override
	public LinkedList<String> getAllDatabase() throws SQLException {
		if(connection.isClosed())
			reestablishConnection();
		LinkedList<String> s = new LinkedList<String>();
		ResultSet dbc = connection.createStatement().executeQuery("SHOW DATABASES");
		while (dbc.next()) {
			s.add(dbc.getString(1));
		}

		LinkedList<String> helplist = new LinkedList<String>();
		for (int i=0; i<s.size();i++){
			if(!s.get(i).equals("template0")
					&& !s.get(i).equals("template1")
					&& !s.get(i).equals("information_schema")
					&& !s.get(i).equals("mysql")
					&& !s.get(i).equals("performance_schema")
					&& !s.get(i).equals("sys")) {
				helplist.add(s.get(i));
			}
		}
		closeConnection();
		return helplist;
	}

	@Override
	public LinkedList<String> getDBSchemata() throws SQLException {
		if(connection.isClosed())
			reestablishConnection();
		LinkedList<String> s = new LinkedList<String>();
		ResultSet dbc = query("SELECT schema_name FROM information_schema.schemata");
		while (dbc.next()) {
			s.add(dbc.getString(1));
		}
		closeConnection();
		return s;
	}

	@Override
	public LinkedList<String> getDBSchemata(String db) throws SQLException {
		if(connection.isClosed())
			reestablishConnection();
		LinkedList<String> s = new LinkedList<String>();
		
		switchDB(db);
		ResultSet dbc = query("SELECT schema_name FROM information_schema.schemata");
		while (dbc.next()) {
			s.add(dbc.getString(1));
		}
		LinkedList<String> helplist = new LinkedList<String>();
		for (int i=0; i<s.size();i++){
			if(!s.get(i).equals("mysql")
					&& !s.get(i).equals("performance_schema")
					&& !s.get(i).equals("information_schema")){
				helplist.add(s.get(i));
			}
		}
		LinkedList<String> helplist2 = new LinkedList<String>();
		for (int i=0; i<helplist.size();i++){
			if(schemaEmpty(helplist.get(i))){
				helplist2.add(helplist.get(i));
			}
		}			
		closeConnection();
		return helplist2;	
	}

	@Override
	public boolean schemaEmpty(String schematest) throws SQLException {
		// Initialize tables
		LinkedList<DBTable> tables2 = new LinkedList<DBTable>();

		// Adding all tables to the list
		ResultSet rs = query("SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES WHERE TABLE_SCHEMA = '" + schematest + "';");
		while (rs.next())
			tables2.add(new DBTable(rs.getString("TABLE_NAME")));
		// if no tables are in current schema return true, else false
		if (tables2.size() < 1) {
			return true;
		} else {
			return false;
		}
	}

	//Method modified to also ignore system schemas. This change was necessary,
	// as this class did not offer a method to show filled schemas too.
	//System Schemas are not in use.
	@Override
	public LinkedList<String> getAllDBSchemata(String db) throws SQLException {
		if(connection.isClosed())
			reestablishConnection();
		LinkedList<String> s = new LinkedList<String>();

		switchDB(db);
		ResultSet dbc = query("SELECT schema_name FROM information_schema.schemata");
		while (dbc.next()) {
			s.add(dbc.getString(1));
		}
		//Ignoring system schemas
		LinkedList<String> helplist = new LinkedList<String>();
		for (String value : s) {
			if (!value.equals("mysql")
					&& !value.equals("performance_schema")
					&& !value.equals("information_schema")) {
				helplist.add(value);
			}
		}
		closeConnection();
		return helplist;
	}

	@Override
	public void writeTablesToDB(LinkedList<DBTable> tables,String database, String schema, boolean css,
			boolean dbExists, boolean newSchema) throws SQLException {
		if(connection.isClosed())
			reestablishConnection();
		String sqlDB = "";
		if (!dbExists) {
			if(css)
				sqlDB += "CREATE DATABASE " +"`"+ database +"`"+ ";";
			else
				sqlDB += "CREATE DATABASE " + database + ";";
			execute(sqlDB);
			closeConnection();
			switchDB(database);
		}else {
			switchDB(database);
		}if (newSchema){
			String sql = generateSQLCodeFromTables(tables, css, newSchema, schema);
					//DBInterfaceCommunication.getSQLToDB(tables,schema, true);
			execute(sql);

		}else{
			String sql = generateSQLCodeFromTables(tables, css, newSchema, schema);
					//DBInterfaceCommunication.getSQLToDB(tables, schema, false);
			execute(sql);
		}
		closeConnection();
	}
	


}
