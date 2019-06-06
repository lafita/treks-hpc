package nptr;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import nptr.database.ConnectionDB;
import nptr.database.ObjectToDB;

public class DB implements ObjectToDB{
	private int id;
	private String label;
	
	public DB() {
		id=0;
		label="unknown";
	}
	public DB(String l) {
		label=l;
				
	}
	public DB(int i) {
		this.id=i;
	}
	public DB(int i,String l) {
		this.id=i;
		this.label=l;
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
	public void delete() {
		PreparedStatement dbStatement;
		try {
			dbStatement = ConnectionDB.getInstance().prepareStatement(
					"DELETE FROM db WHERE DB_PKEY=?");
			dbStatement.setInt(1,this.id);
			if(ConnectionDB.setRow(dbStatement)) {
				System.out.println("the database has been successfully deleted");
			}else {
				System.out.println("the database couldn't be deleted");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void insert() {
		PreparedStatement dbStatement;
		try {
			dbStatement = ConnectionDB.getInstance().prepareStatement(
					"INSERT INTO db VALUES( default,? );SELECT currval('db_db_pkey_seq')");
			dbStatement.setString(1,this.label);
			
			if(!ConnectionDB.setRow(dbStatement)) {
				System.out.println("the database couldn't be inserted");
			}
			int nInserted = dbStatement.getUpdateCount();
			if (nInserted == 1 && dbStatement.getMoreResults()) {
			   ResultSet rs = dbStatement.getResultSet();
			   if (rs.next())
				   this.id=rs.getInt(1);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	public void update() {		
	}
	
}

