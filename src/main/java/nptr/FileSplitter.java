package nptr;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import nptr.utils.Utils;

/**
 * Utility function to split a big FASTA file into smaller ones.
 */
public class FileSplitter {

	public static String[] split(String file) throws IOException {

		// array contenant les chemins vers les fichiers issus du split
		String[] paths = { file };

		File fic = new File(file);
		BufferedReader in = new BufferedReader(new FileReader(fic));

		long size = fic.length();
		
		/*
		 * on calcule en combien de fichiers de 10 mo on peut splitter le
		 * fichier d'origine: 1024*1024*10 octets
		 */
		final int numOutputFiles = (int) Math.ceil((double) size / 10485760);

		if (numOutputFiles > 1) {

			int loopCounter = 0;

			/* on r�affecte une nouvelle instance � paths */
			paths = new String[numOutputFiles];
			PrintWriter[] fileHandles = new PrintWriter[numOutputFiles];

			for (int i = 0; i < numOutputFiles; i++) {
				
				String parent = "";
				if (fic.getParentFile() != null) {
					parent = fic.getParentFile().toString() + File.separator;
				}

				// Create new file nptrFinder
				String path = parent + Utils.getName(fic.getName()) + "_" + i + ".nf";
				
				paths[i] = path;
				fileHandles[i] = new PrintWriter(new FileOutputStream(path));
			}

			String s = "";
			int l = 0;
			StringBuffer stringSeq = new StringBuffer();

			while ((s = in.readLine()) != null) {

				if (loopCounter == numOutputFiles)
					loopCounter = 0;

				// prise en charge du format fasta
				if (s.startsWith(">")) {
					if (l != 0) {
						fileHandles[loopCounter].print(stringSeq.toString());
						/*
						 * on r�instancie le stringbuffer pr chaque s�quence
						 */
						stringSeq = new StringBuffer();
						stringSeq.append(s + "\n");
						loopCounter++;
					} else
						stringSeq.append(s + "\n");
				} else
					stringSeq.append(s + "\n");

				l++;
			}

			fileHandles[0].println(stringSeq);

			for (int i = 0; i < numOutputFiles; i++) {
				fileHandles[i].close();
			}
		}

		in.close();

		return paths;
	}

}
