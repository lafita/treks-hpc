package nptr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

public class Aligner {

	public static int clustal = 0;
	public static int muscle = 1;

	// Disable instantiating this class - should be static
	private Aligner() {}

	public static synchronized LinkedList<String> compute(AlignCopies mots, int prog) {

		int program = prog;
		LinkedList<String> alignMots = new LinkedList<String>();

		// Where to store temp files? Working directory better
		String path = System.getProperty("user.dir");
		String parent = path + File.separator;
		
		// Name of the tmp file has to be unique - use hashing and randomness
		int hash = mots.toString().hashCode();
		int random = (int) (Math.random() * 1000 + 1);
		String inPath = parent + ".tmp" + hash + "_" + random + ".fa";
		String outPath = parent + ".tmp" + hash + "_" + random + ".out";
		
		File tmpfile = new File(inPath);
		PrintWriter tempFasta;

		try {
			tempFasta = new PrintWriter(new FileOutputStream(tmpfile));
			for (int i = 0; i < mots.size(); i++) {
				tempFasta.println(">temp" + i);
				tempFasta.println(mots.get(i).getSequence());
			}
			tempFasta.close();
		} catch (FileNotFoundException e) {
			System.out.println("ERROR - " + e.getMessage());
			System.exit(1);
		}

		String commands = "";
		
		switch (program) {

		case 0:
			commands = Parameters.clustalPath + " /infile=" + inPath + " /outfile=" + outPath
					+ " /output=fasta /outorder=input /align /gapopen=5";
			//System.out.println("aligning on clustal: " + commands);
			break;
		case 1:
			commands = Parameters.musclePath + " -in " + inPath + " -out " + outPath
					+ " -quiet ";
			//System.out.println("aligning on muscle: " + commands);
			break;
		}

		try {
			Process p = Runtime.getRuntime().exec(commands);
			
			/*// This is used to get the STDOUT of the alignment command execution
			InputStream stdin = p.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(stdin));
			
			while ((br.readLine()) != null) {
				// on ne fait rien avec le flux de sortie,est seulement
				// r�cuper� pr que le process puisse fermer
				System.out.println(br.readLine());
			}*/
			
			int exitVal = p.waitFor();
			if (exitVal == 0) {
				
				/* l'alignement est g�ner� dans le fichier out.fasta */
				File outputFile = new File(outPath);

				BufferedReader input = new BufferedReader(new FileReader(outputFile));
				// on r�cup�re l'indice du motif si on a un > devant sinon
				// on le stocke dans la linkedList
				int k = 0;
				String line;
				String ligne = "";
				while ((line = input.readLine()) != null) {
					if (!line.startsWith(">")) {
						ligne += line;
					} else if (k != 0) {
						alignMots.add(ligne);
						// System.out.println(ligne);
						ligne = "";
					}
					k++;
				}
				// System.out.println(ligne);
				// ajout du dernier motif
				alignMots.add(ligne);

				input.close();
				outputFile.delete();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// Remove the temporary FASTA file with mots
		tmpfile.delete();

		return alignMots;
	}
}
