package nptr.database;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;

import nptr.database.Tuple;


public class QueryDB {
	private static String query;
	public static Tuple allOrganism=null;
	public static Tuple allTaxa=null;
	public static Tuple allDB=null;
	public static Tuple allLocalz=null;
	public static Tuple allFunction=null;
	public static Tuple allDefinition=null;
	public static Tuple allKeyword=null;
	public static Tuple allpfam=null;
	public QueryDB() {
		ConnectionDB.getInstance();
		query="SELECT * FROM sequence";
	}
	public static HashMap getOrganism(int id) {
		query=" SELECT * FROM organism WHERE organism_pkey="+id;
		HashMap ret =ConnectionDB.getOneRow(query);
		return ret;
	}
	public static Tuple getAllOrganism() {
		if (allOrganism==null) {
			query=" SELECT * FROM organism ORDER BY organism_pkey";
			Tuple ret=ConnectionDB.getMultipleRow(query);
			allOrganism=ret;
		}
		return allOrganism;
	}
	
	public static Tuple getAllTaxa() {
		if (allTaxa==null) {
			query=" SELECT * FROM taxon ORDER BY taxon_pkey";
			Tuple ret=ConnectionDB.getMultipleRow(query);
			allTaxa=ret;
		}
		return allTaxa;
	}
	
	public static HashMap getDB(int id) {
		query=" SELECT * FROM db WHERE db_pkey="+id;
		HashMap ret =ConnectionDB.getOneRow(query);
		return ret;
	}
	public static Tuple getAllDB() {
		if (allDB==null) {
			query=" SELECT * FROM db ORDER BY db_pkey";
			Tuple ret=ConnectionDB.getMultipleRow(query);
			allDB=ret;
		}
		return allDB;
	}
	public static Tuple getAllDomains() {
		if (allpfam==null) {
			query=" SELECT * FROM pfam ORDER BY pfam_pkey";
			Tuple ret=ConnectionDB.getMultipleRow(query);
			allpfam=ret;
		}
		return allpfam;
	}
	public static Tuple getAllLocalizations() {
		if (allLocalz==null) {
			query=" SELECT * FROM localization ORDER BY localization_pkey";
			Tuple ret=ConnectionDB.getMultipleRow(query);
			allLocalz=ret;
		}
		return allLocalz;
	}
	public static Tuple getAllFunctions() {
		if (allFunction==null) {
			query=" SELECT * FROM function ORDER BY function_pkey";
			Tuple ret=ConnectionDB.getMultipleRow(query);
			allFunction=ret;
		}
		return allFunction;
	}
	public static Tuple getAllDefinitions() {
		if (allDefinition==null) {
			query=" SELECT * FROM definition ORDER BY definition_pkey";
			Tuple ret=ConnectionDB.getMultipleRow(query);
			allDefinition=ret;
		}
		return allDefinition;
	}
	public static Tuple getAllKeyWords() {
		if (allKeyword==null) {
			query=" SELECT * FROM keyword ORDER BY keyword_pkey";
			Tuple ret=ConnectionDB.getMultipleRow(query);
			allKeyword=ret;
		}
		return allKeyword;
	}
	public static int countSequencesFromDB(int i) {
		query="SELECT count(*) as nb FROM sequence where id_db="+i+" and last_update>(SELECT search_date from history where id_db="+i+" order by search_date DESC limit 1);";
		HashMap ret=ConnectionDB.getOneRow(query);
		//System.out.println("number "+ret.get("nb").toString());
		return Integer.parseInt(ret.get("nb").toString());
	}
	
	public static boolean insertHistory (int i,int nb) {
		PreparedStatement dbStatement;
		try {
			
			dbStatement = ConnectionDB.getInstance().prepareStatement(
					"INSERT INTO history VALUES (default,current_timestamp,?,?)"
			);
			dbStatement.setInt(1, i);
			dbStatement.setInt(2, nb);
			if (ConnectionDB.setRow(dbStatement)) {
				System.out.println("history updated.");
				ConnectionDB.getInstance().commit();
				return true;
			}else return false;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			return false;
		}
	}
	
	
	/**
	 * delete all the sequences in the database wich belong to the dbank of index i
	 * @param i: index of the databank to delete
	 * @return
	 */
	public static boolean deleteSequencesFromDB(int i) {
		PreparedStatement dbStatement;
			try {
				
				dbStatement = ConnectionDB.getInstance().prepareStatement(
						"DELETE FROM sequence WHERE id_db=?"
				);
				dbStatement.setInt(1, i);
				return ConnectionDB.setRow(dbStatement);
			} catch (SQLException e) {
				return false;
			}
		
	}
	/**
	 * delete all the sequences in the database wich belong to the dbank of index i
	 * @param i: index of the databank to delete
	 * @return
	 */
	public static boolean deleteRepeatsFromDB(int i) {
		PreparedStatement dbStatement;
		try {
			
			dbStatement = ConnectionDB.getInstance().prepareStatement(
					"DELETE FROM repeats WHERE id_db=?"
			);
			dbStatement.setInt(1, i);
			return ConnectionDB.setRow(dbStatement);
		} catch (SQLException e) {
			return false;
		}
		
	}
}
