/**
 * @author julien JORDA
 * This class permits to build an organism
 * related in the database.
 */
package nptr;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import nptr.database.ConnectionDB;
import nptr.database.ObjectToDB;

public class Organism implements ObjectToDB{
	private int id,taxon;
	private String label;
	
	public Organism() {
		id=0;
		label="unknown";
	}
	
	public Organism(int i) {
		this.id=i;
	}
	
	/** specific to organism */
	public Organism(String l) {
		label=l;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
	public int getTaxon() {
		return taxon;
	}

	public void setTaxon(int taxon) {
		this.taxon=taxon;
	}
	
	/** interactions with database
	 * 
	 */
	
	public void delete() {
		PreparedStatement dbStatement;
		try {
			dbStatement = ConnectionDB.getInstance().prepareStatement(
					"DELETE FROM organism WHERE organism_PKEY=?;");
			dbStatement.setInt(1,this.id);
			if(ConnectionDB.setRow(dbStatement)) {
				System.out.println("the organism has been successfully deleted");
			}else {
				System.out.println("the organism couldn't be deleted");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void insert() { 
		PreparedStatement dbStatement;
		try {
				
			dbStatement = ConnectionDB.getInstance().prepareStatement(
					"INSERT INTO organism VALUES( default,?,? );SELECT currval('organism_organism_pkey_seq');");
			dbStatement.setString(1,this.label);
			dbStatement.setInt(2,this.taxon);
			if(!ConnectionDB.setRow(dbStatement)) {
				System.out.println("the organism couldn't be inserted");
			}
			int nInserted = dbStatement.getUpdateCount();
			if (nInserted == 1 && dbStatement.getMoreResults()) {
			   ResultSet rs = dbStatement.getResultSet();
			   if (rs.next())
				   this.id=rs.getInt(1);
			}
			ConnectionDB.getInstance().commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void update() {
		
	}
	
}
