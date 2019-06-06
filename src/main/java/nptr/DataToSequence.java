package nptr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import nptr.database.ConnectionDB;
import nptr.database.Tuple;

public class DataToSequence {
	private LinkedList<SeqRepeat> sequences;

	public DataToSequence() {
		sequences = new LinkedList<SeqRepeat>();
		/* flux d'entr�e et sortie */
	}

	public LinkedList<SeqRepeat> fromFastaCMD(File fastaFile) throws FileNotFoundException {
		String ligne;
		try {

			FileReader lectFic = new FileReader(fastaFile);
			BufferedReader bufRead = new BufferedReader(lectFic);

			/** on r�cup�re le nombre de lignes */

			int l = 0;
			/* on initilaise une variable de la s�quence courante */
			SeqRepeat seqFasta = new SeqRepeat();
			StringBuffer stringSeq = new StringBuffer();
			/*
			 * on instancie l'arraylist acccueillant toutes les sequences fasta
			 */
			sequences = new LinkedList<SeqRepeat>();

			while ((ligne = bufRead.readLine()) != null) {
				// prise en charge du format fasta
				if (ligne.startsWith(">")) {
					if (l != 0) {
						seqFasta.setSequence(stringSeq.toString());
						sequences.add(seqFasta);
						seqFasta = new SeqRepeat();
						seqFasta.setDesc(ligne);
						stringSeq = new StringBuffer();
					} else {
						seqFasta.setDesc(ligne);
					}
				} else
					stringSeq.append(ligne);
				l++;
			}
			/* on veut prendre la derni�re s�quence */
			seqFasta.setSequence(stringSeq.toString());
			sequences.add(seqFasta);
			bufRead.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return sequences;
	}

	public Object[] fromFasta(File fastaFile) throws FileNotFoundException {

		String ligne;
		try {

			FileReader lectFic = new FileReader(fastaFile);
			BufferedReader bufRead = new BufferedReader(lectFic);

			/** on r�cup�re le nombre de lignes */
			int l = 0;
			int countseq = 0;
			/* on initialise une variable de la s�quence courante */
			StringBuffer stringSeq = new StringBuffer();
			/*
			 * on instancie l'arraylist acccueillant toutes les sequences fasta
			 */
			ArrayList<String> seqFasta = new ArrayList<String>();

			while ((ligne = bufRead.readLine()) != null) {
				// prise en charge du format fasta
				if (ligne.startsWith(">")) {
					if (l != 0) {
						seqFasta.add(stringSeq.toString());
						countseq++;
						stringSeq = new StringBuffer();
					} else {
					}
				} else
					stringSeq.append(ligne);

				l++;
			}
			/* on veut prendre la derni�re s�quence */
			seqFasta.add(stringSeq.toString());
			bufRead.close();
			return seqFasta.toArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * gets a specific databank from the database, gets only the sequences which
	 * have been imported more recently than the last exploration on this
	 * databank
	 * 
	 * @param iDB
	 *            : index of the databank to explore
	 * @param offset:
	 *            offset for the starting entry
	 * @param limit
	 *            : number of sequences to get
	 * @return Sequence ArrayList
	 */
	public LinkedList<SeqRepeat> fromDB(int iDB, int offset, int limit) {

		ConnectionDB.getInstance();
		String query = "BEGIN;";
		ConnectionDB.getOneRow(query);
		// only get sequences which have been added after the last exploration
		// by T-REKS in this databank
		query = "DECLARE curs1 cursor for  SELECT * FROM sequence WHERE id_db=" + iDB
				+ " and last_update>(SELECT search_date from history where id_db=" + iDB
				+ " order by search_date desc limit 1) ORDER BY sequence_pkey;";
		ConnectionDB.getOneRow(query);
		query = "move forward " + offset + " in curs1;";
		ConnectionDB.getOneRow(query);
		if (limit > 0)
			query = "fetch forward " + limit + " from curs1;";
		else
			query = "fetch forward all from curs1;";
		Tuple seqList = ConnectionDB.getMultipleRow(query);
		for (int s = 0; s < seqList.size(); s++) {
			HashMap seqTuple = seqList.get(s);
			SeqRepeat sequence = new SeqRepeat(seqTuple.get("description").toString(),
					seqTuple.get("sequence").toString());
			sequence.setId(Integer.parseInt(seqTuple.get("sequence_pkey").toString()));
			sequence.setGi(seqTuple.get("gi").toString());
			sequence.setDB(new DB(iDB));
			sequences.add(sequence);
		}
		query = "COMMIT;";
		ConnectionDB.getOneRow(query);
		return sequences;
	}

	public LinkedList<SeqRepeat> fromTuples(Tuple seqList) {

		for (int s = 0; s < seqList.size(); s++) {
			HashMap seqTuple = seqList.get(s);
			SeqRepeat sequence = new SeqRepeat(seqTuple.get("description").toString(),
					seqTuple.get("sequence").toString());
			sequence.setId(Integer.parseInt(seqTuple.get("sequence_pkey").toString()));
			sequence.setGi(seqTuple.get("gi").toString());
			sequence.setDB(new DB(Integer.parseInt(seqTuple.get("id_db").toString())));
			sequences.add(sequence);
		}
		return sequences;
	}

}
