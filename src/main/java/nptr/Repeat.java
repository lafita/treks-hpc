package nptr;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedList;

import nptr.database.ConnectionDB;
import nptr.database.ObjectToDB;

public class Repeat implements ObjectToDB {
	
	private int length, seqLength;
	private double similarity;
	private AlignCopies copies;
	private String pattern;
	private int beginPosition, endPosition;
	private int seqId, id_db, id_org, id_func, source;

	public Repeat() {
		this.seqId = 0;
		this.similarity = 0;
		this.length = 0;
		this.seqLength = 0;
		this.copies = new AlignCopies();
		this.pattern = "";
		this.beginPosition = 0;
		this.endPosition = 0;
		this.id_db = 0;
		this.id_org = 0;
		this.id_func = 0;
		this.source = 0;
	}

	public Repeat(double sim, int l, AlignCopies aligncop) {
		this.seqId = 0;
		this.similarity = sim;
		this.length = l;
		this.seqLength = 0;
		this.copies = aligncop;
		this.pattern = "";
		this.beginPosition = aligncop.getFirst().getBeginPosition();
		this.endPosition = aligncop.getLast().getEndPosition();
		this.id_db = 0;
		this.id_org = 0;
		this.id_func = 0;
		this.source = 0;
	}

	public void delete() {

	}

	public void insert() {
		PreparedStatement dbStatement;
		try {
			dbStatement = ConnectionDB.getInstance()
					.prepareStatement("INSERT INTO repeats VALUES(default,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
			dbStatement.setDouble(1, this.similarity);
			dbStatement.setInt(2, copies.size());
			dbStatement.setInt(3, length);
			dbStatement.setString(4, pattern);

			String alignment = "";
			for (int i = 0; i < copies.size(); i++) {
				alignment += copies.get(i).getSequence() + "\\n";
			}
			dbStatement.setString(5, alignment);
			dbStatement.setInt(6, beginPosition);
			dbStatement.setInt(7, endPosition);
			dbStatement.setInt(8, this.id_db);
			dbStatement.setInt(9, this.id_org);
			dbStatement.setInt(10, this.seqLength);
			dbStatement.setNull(11, 0);// struct
			dbStatement.setInt(12, (endPosition - beginPosition) + 1);
			dbStatement.setInt(13, this.id_func);
			dbStatement.setInt(14, this.seqId);
			dbStatement.setInt(15, this.source);

			if (!ConnectionDB.setRow(dbStatement)) {
				System.out.println(dbStatement.toString());
				System.out.println("the repeat couldn't be inserted");
				System.exit(0);
			} else {
				System.out.println("repeat in " + seqId + " has been inserted");
				ConnectionDB.getInstance().commit();
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void update() {

	}

	/**
	 * @return renvoie le nombre de copies pr�sentes dans l'alignement du repeat
	 */
	public int getNumber() {
		return this.copies.size();
	}

	/**
	 * v�rifi� si le repeat n'est pas un artefact "XX"
	 * 
	 * @return
	 */
	public boolean isReal() {
		for (int i = 0; i < pattern.length(); i++) {
			if (pattern.charAt(i) != 'X' && pattern.charAt(i) != 'x') {
				return true;
			}
		}
		return false;
	}

	/**
	 * override toString()
	 * 
	 */
	public String toString() {
		String repString = "";
		repString += "Length: " + this.length + " residues - nb: " + this.getNumber() + "  from  "
				+ (this.beginPosition + 1) + " to " + (this.endPosition + 1) + " - Psim:" + this.similarity
				+ " region Length:" + this.getRegionLength() + " \n";
		for (int a = 0; a < this.copies.size(); a++) {
			repString += copies.get(a).getSequence() + "\n";
		}
		repString += "**********************";
		return repString;
	}

	public String getPattern() {
		return pattern;
	}

	public void setPattern(String pattern) {
		this.pattern = pattern;
	}

	public int getSeqId() {
		return seqId;
	}

	public void setSeqId(int id) {
		this.seqId = id;
	}

	public LinkedList<Copy> getCopies() {
		return copies;
	}

	public void setDB(int db) {
		this.id_db = db;
	}

	public int getDB() {
		return id_db;
	}

	public int getBeginPosition() {
		return beginPosition;
	}

	public void setBeginPosition(int beginPosition) {
		this.beginPosition = beginPosition;
	}

	public int getEndPosition() {
		return endPosition;
	}

	public void setEndPosition(int endPosition) {
		this.endPosition = endPosition;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getSeqLength() {
		return length;
	}

	public void setSeqLength(int slength) {
		this.seqLength = slength;
	}

	public void setCopies(AlignCopies copies) {
		this.copies = copies;
	}

	public double getSimilarity() {
		return similarity;
	}

	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}

	public void recalculatePsim() {
		PSim ps = new PSim(this.copies);
		ps.compute();
		this.pattern = ps.getConsensus();
		this.similarity = ps.getSimilarity();
	}

	public int getRegionLength() {
		return this.endPosition - this.beginPosition + 1;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

	public void perfectTrimming() {
		// left trimming
		while (!this.copies.getFirst().getSequence().equals(this.pattern) && this.copies.size() > 2) {
			this.copies.removeFirst();
		}
		// right trimming
		while (!this.copies.getLast().getSequence().equals(this.pattern) && this.copies.size() > 2) {
			this.copies.removeLast();
		}

	}
}
