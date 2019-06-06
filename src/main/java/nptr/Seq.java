package nptr;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;

import nptr.database.ConnectionDB;
import nptr.database.ObjectToDB;

/**
 * @author  julien
 */
public class Seq implements ObjectToDB{

	/**
	 * @uml.property  name="desc"
	 */
	private String desc;
	protected String seq;
	protected int[]lengthList;
	protected int[] lengthListOneRes;
	/**
	 * @uml.property  name="nom"
	 */
	private int id;
	private String gi;
	private int loca;
	private Organism org;
	private DB base;
	
	public Seq() {
		this.desc="";
		this.seq="s";
		this.id=0;
		this.gi="0";
		this.org=new Organism();
		this.base=new DB();
		this.loca=0;
		lengthList=new int[this.seq.length()];
		lengthListOneRes=new int[this.seq.length()];
	}
	public Seq(String desc, String s) {
		this.desc=desc;
		this.seq=s.toUpperCase();
		this.id=0;
		this.gi="0";
		this.org=new Organism();
		this.base=new DB();
		this.loca=0;
		lengthList=new int[this.seq.length()];
		for(int i=0;i<lengthList.length;i++) {
			lengthList[i]=-1;
		}
		lengthListOneRes=new int[this.seq.length()];
	}
	
	public Seq(int i) {
		this.id=i;
		String query="SELECT * FROM sequence WHERE  SEQUENCE_PKEY= '"+this.id+"'";
		HashMap hashSeq=ConnectionDB.getOneRow(query);
		if (hashSeq.size()>0) {
			//this.org=new SimpleObject(Integer.parseInt(hashSeq.get("ID_ORG").toString()));
			this.base=new DB(Integer.parseInt(hashSeq.get("ID_DB").toString()));
			this.desc=hashSeq.get("DESCRIPTION").toString();
			this.seq=hashSeq.get("SEQUENCE").toString();
			this.gi=hashSeq.get("GI").toString();
		}
		
	}
	/**
	 * @param n
	 * @uml.property  name="desc"
	 */
	public void setDesc (String n) {
		if(n.startsWith(">")) desc=n;
		else desc=">"+n;
	}
	/**
	 * @return
	 * @uml.property  name="desc"
	 */
	public String getDesc () {
		return desc;
	}
	public void setSequence (String s) {
		
		if(s.startsWith(">")) {
			String sTemp="";
			String[] lignes=s.split("\\n");
			this.setDesc(lignes[0]);
			for(int i=1;i<lignes.length;i++) {
				sTemp+=lignes[i];
			}
			seq=sTemp;
		}else seq=s;
		seq=seq.toUpperCase();
		lengthList=new int[this.seq.length()];
		for(int i=0;i<lengthList.length;i++) {
			lengthList[i]=-1;
		}
		lengthListOneRes=new int[this.seq.length()];
	}
	public String getSequence () {
		return seq;
	}
	public int size() {
		return seq.length();
	}
	/* mise en String buffer du contenu du fichier */
	public void createFromFile(String f) {
		try {
			InputStream ips=new FileInputStream(f);
		   InputStreamReader ipsr=new InputStreamReader(ips);
		   BufferedReader br=new BufferedReader(ipsr);
		   String ligne;
		   StringBuffer stringSeq = new StringBuffer();
		   while ((ligne=br.readLine())!=null)
			   //prise en charge du format fasta
			   if(ligne.startsWith(">")) this.setDesc(ligne);
			   else stringSeq.append(ligne);
		   /* fermeture du fichier */
		   br.close();
		   this.setSequence(stringSeq.toString());
		}catch (Exception e) {
			System.out.println("le fichier n'a pu �tre ouvert: "+e);
		}
	}
	/**
	 * export the sequence in FASTA format in a file
	 *@param xpPath : file path
	 *@ param append, tells  whether or not the data has to be appended to an existing file 
	 */
	public void exportToFile(String xpPath, boolean append) {
		BufferedWriter bw = null;

		try {
			bw = new BufferedWriter(new FileWriter(xpPath, append));
			if(this.id!=0) 
				bw.write(">gi|"+this.gi+"|id|"+this.id+"\n");
			else {
				bw.write(this.desc+"\n");
			}
			int l;
			for(l=0;l<Math.floor(this.seq.length()/60);l++) {
				bw.write(this.seq.substring(l*60,(l+1)*60)+"\n");
			}
			if(Math.floor(this.seq.length())%60!=0) bw.write(this.seq.substring(l*60,this.seq.length())+"\n");
			bw.newLine();
			bw.flush();
			bw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * @return
	 * @uml.property  name="nom"
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param nom
	 * @uml.property  name="nom"
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	public String getGi() {
		return gi;
	}
	/**
	 * @param nom
	 * @uml.property  name="nom"
	 */
	public void setGi(String gi) {
		this.gi = gi;
	}
	
	public Organism getOrg() {
		return org;
	}
	public void setOrg(Organism org) {
		this.org = org;
	}
	public void setOrg(String o) {
		this.org = new Organism(o);
	}
	public DB getDB() {
		return base;
	}
	public void setDB(DB d) {
		this.base = d;
	}
	public void setDB(String d) {
		this.base = new DB(d);
	}
	public void delete() {
		PreparedStatement dbStatement;
		try {
			dbStatement = ConnectionDB.getInstance().prepareStatement(
					"DELETE FROM sequence WHERE SEQUENCE_PKEY=?");
			dbStatement.setInt(1,this.id);
			if(ConnectionDB.setRow(dbStatement)) {
				System.out.println("the sequence has been successfully deleted");
			}else {
				System.out.println("the sequence couldn't be deleted");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}
	public void insert() {
		PreparedStatement dbStatement;
		try {
			dbStatement = ConnectionDB.getInstance().prepareStatement(
			"INSERT INTO sequence VALUES(default,?,?,?,?,?,?,?,?);");
			dbStatement.setString(1,desc);
			dbStatement.setInt(2,org.getId());
			dbStatement.setString(3,seq);
			dbStatement.setInt(4,base.getId());
			dbStatement.setString(5,gi);
			dbStatement.setInt(6,seq.length());
			dbStatement.setTimestamp(7, new Timestamp(System.currentTimeMillis()));
			dbStatement.setInt(8,loca);
			if(!ConnectionDB.setRow(dbStatement)) {
				System.out.println(dbStatement.toString());
				System.out.println("the sequence couldn't be inserted");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
		
	}
	public void update() {
		PreparedStatement dbStatement;
		try {
			dbStatement = ConnectionDB.getInstance().prepareStatement(
			"UPDATE sequence SET(DESCRIPTION=?,SEQUENCE=? ) WHERE SEQUENCE_PKEY=?;");
			dbStatement.setString(1,desc);
			dbStatement.setString(2,seq);
			dbStatement.setInt(3,this.id);
			
			if(ConnectionDB.setRow(dbStatement)) {
				System.out.println("the sequence has been successfully updated");
			}else {
				System.out.println("the sequence couldn't be updated");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	public int[] getLengthList() {
		return lengthList;
	}
	public void setLengthList(int[] lengthList) {
		this.lengthList = lengthList;
	}
	public int[] getLengthListOneRes() {
		return lengthListOneRes;
	}
	public void setLengthListOneRes(int[] lengthListOneRes) {
		this.lengthListOneRes = lengthListOneRes;
	}
		
}
