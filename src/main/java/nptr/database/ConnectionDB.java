package nptr.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Properties;

import nptr.database.Tuple;

public class ConnectionDB {
	
	private static Connection conn;
	public static String server="localhost";
	public ConnectionDB() {
		
	}
	public static Connection getInstance() {
		if(conn==null) {
			try {
				//System.out.println(System.getProperty("user.name"));
				Class.forName("org.postgresql.Driver");
				String url = "jdbc:postgresql://"+server+"/postgres";
				Properties props = new Properties();
				props.setProperty("user","postgres");
				props.setProperty("password","kajavacRbM");
				//props.setProperty("","");
				//props.setProperty("ssl","false");
				conn = DriverManager.getConnection(url, props);
				//on enlève l'autocommit pour gagner en vitesse lors de multiples insertions
				conn.setAutoCommit(false);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				System.out.println("Could not connect to this server: "+server);
				e.printStackTrace();
			}
		}
		return conn;
	}
	
	/******************** SELECT *****************************/
	/*********************************************************/
	/*********************************************************/
	
	/** exécute un SELECT sur un tuple spécifique
	 * @param oneQuery chaine de la requête SQL
	 * @return Hashtable contenant tous les champs du tuple considéré
	 **/
	public static HashMap getOneRow(String oneQuery) {
		HashMap ret=new HashMap();
		Statement st;
		ResultSet rs;
		try {
			ConnectionDB.getInstance();
			st = conn.createStatement();	
			rs = st.executeQuery(oneQuery);
			if(rs.next()) {
				ret=ConnectionDB.getOneRow(rs);
			}
			rs.close();
			st.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}
	/** exécute un SELECT sur un tuple spécifique
	 * @param rs Resulset après éxecution d'une requête 
	 * @return Hashtable contenant tous les champs du tuple considéré
	 **/
	public static HashMap getOneRow(ResultSet rs){
	
		HashMap<Object,Object> ret = new HashMap<Object,Object> ();
		Object temp;
	
		try {
	
			String cName;
	
			ResultSetMetaData rsMd = rs.getMetaData();
			
			int numberOfColumns = rsMd.getColumnCount();
			for (int i = 1; i <= numberOfColumns; i++) {
	
				cName = "unknown";
				String cType = rsMd.getColumnTypeName(i);
				try {
					cName = rsMd.getColumnName(i);
					//String cType = rsMd.getColumnTypeName(i);
					
	
					//types and rs methods are:
					/*
					INTEGER		getInt
					DOUBLE		getDouble
					CHAR		getString
					LONGCHAR	getString
					DATETIME	getDate
					*/
					//finish here by checking the cType
					//and then using the right get method above
					//to put the value into the hash.
					if (cType == "int4"){
						ret.put(cName, new Integer(rs.getInt(i)));
					}
					else {
						temp = rs.getObject(i);
						if(temp.getClass().getName() == "java.lang.String"){
							ret.put(cName,temp.toString().trim());
						}
						else {
							ret.put(cName,temp);
						}
					}
				}
				catch (Exception e){
					ret.put(cName, "");
					System.out.println("couldn't retrieve the value for the field "+cName+" of type "+cType+" ");
					e.printStackTrace();
				}
	
				//if (cType == "INTEGER"||cType == "COUNTER"){
				//	ret.put(cName, new Integer(rs.getInt(i)));
				//}
				//else if (cType == "Double"){
				//	ret.put(cName, new Double(rs.getDouble(i)));
				//}
				//else if (cType == "DATETIME"){
				//	ret.put(cName,rs.getDate(i));
				//}
				//else {
				//	ret.put(cName, rs.getString(i));
				//}
			}
		}
		catch (Exception e){
			System.out.println("couldn't retrieve this tuple"+e);
		}
		return ret;
	
	}
	/**
	 * Permet d'exécuter une requête SQL qui agit sur plusieurs tuples et renvoie son résultat
	 * @param multipleQuery requête SQL
	 * @return Arraylist contenant des Hashtables associées à chaque tuples
	 */
	public static Tuple getMultipleRow(String multipleQuery){

		Tuple ret = new Tuple();

		try {
			ConnectionDB.getInstance();
			Statement st;
			ResultSet rs;
			st = conn.createStatement();	
			rs = st.executeQuery(multipleQuery);
			//ResultSetMetaData rsMd = rs.getMetaData();
			while (rs.next()){
				ret.add(ConnectionDB.getOneRow(rs));
			}
			st.close();
			rs.close();
			return ret;

		}
		catch (Exception e){
			System.out.println("problème de requete:");
			e.printStackTrace();
			return null;
		}

	}
	/******************* INSERT,UPDATE,DELETE ****************/
	/*********************************************************/
	/*********************************************************/

	public static boolean setRow(PreparedStatement st) {
		try {
	
			int res = st.executeUpdate();
			
			st.close();
			if(res>0) return true;
			else return false;

		}
		catch (Exception e){
			System.out.println("couldn't set this row "+e);
			return false;
		}
	}
}
