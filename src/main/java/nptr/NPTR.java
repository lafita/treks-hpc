package nptr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.LinkedList;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * The major class of T-Reks, it processes the sequences given by the user. It
 * coordinates all the user parameters and writes the results.
 * 
 * @author julien
 * 
 */
public class NPTR {

	// Files
	private String infile = "";
	private String align = "<standard output>";
	private String table = "<standard output>";
	
	private static PrintWriter tableWriter;
	private static PrintWriter alignWriter;
	

	// Counter for number of repeats
	private static int countRep = 0;

	/**
	 * Constructor from the command line.
	 * 
	 * @param args:
	 *            command line parameters
	 */
	public NPTR(String[] args) throws Exception {

		// ####################################
		// Parse the command line options

		// Specify all the options
		Options options = getOptions();
		CommandLineParser parser = new DefaultParser();

		String header = "\nFind tandem repeats in protein and nucleotide sequences.\n\n";
		String footer = "\nPlease report issues at http://github.com/lafita/treks-hpc/issues";
		String usage = "java -jar T-ReksHPC";
		
		HelpFormatter help = new HelpFormatter();

		final CommandLine cli;
		try {
			cli = parser.parse(options, args, false);
		} catch (ParseException e) {
			System.out.println("ERROR - " + e.getMessage());
			help.printHelp(usage, header, options, footer, true);
			System.exit(1);
			return;
		}

		args = cli.getArgs();
		
		Parameters.setParamDefault("20");
		
		// Help
		if (cli.hasOption("help")) {
			help.printHelp(usage, header, options, footer, true);
			System.exit(0);
			return;
		}
		
		// Input file
		if (cli.hasOption("infile")) {
			infile = cli.getOptionValue("infile");
			File inFile = new File(infile);
			if (!inFile.isFile()) {
				System.out.println("ERROR - the input file does not exist!");
				help.printHelp(usage, header, options, footer, true);
				System.exit(1);
				return;
			}
		} else {
			System.out.println("ERROR - required infile option (input file) missing!");
			help.printHelp(usage, header, options, footer, true);
			System.exit(1);
			return;
		}
		
		// Output files
		if (cli.hasOption("table")) {
			table = cli.getOptionValue("table");
			try {
				// If the file exists delete it first to override results
				File tableFile = new File(table);
				if (tableFile.isFile()) tableFile.delete();
				tableWriter = new PrintWriter(new FileOutputStream(table, true));
			} catch (FileNotFoundException e) {
				System.out.println("ERROR - " + e.getMessage());
				help.printHelp(usage, header, options, footer, true);
				System.exit(1);
				return;
			}
		} else {
			tableWriter = new PrintWriter(System.out);
		}
		if (cli.hasOption("align")) {
			align = cli.getOptionValue("align");
			try {
				// If the file exists delete it first to override results
				File alignFile = new File(align);
				if (alignFile.isFile()) alignFile.delete();
				alignWriter = new PrintWriter(new FileOutputStream(align, true));
			} catch (FileNotFoundException e) {
				System.out.println("ERROR - " + e.getMessage());
				help.printHelp(usage, header, options, footer, true);
				System.exit(1);
				return;
			}
		} else {
			alignWriter = new PrintWriter(System.out);
		}
		
		// External alignment tools
		if (cli.hasOption("clustal")) {
			String clustString = cli.getOptionValue("clustal");
			Parameters.clustalPath = clustString;
			if (clustString.contains("/")) {
				File clustFile = new File(clustString);
				if (!clustFile.isFile()) {
					System.out.println("ERROR - the clustal path does not exist!");
					help.printHelp(usage, header, options, footer, true);
					System.exit(1);
					return;
				}
			}
		}
		if (cli.hasOption("muscle")) {
			String muscleString = cli.getOptionValue("muscle");
			Parameters.musclePath = muscleString;
			if (muscleString.contains("/")) {
				File muscleFile = new File(muscleString);
				if (!muscleFile.isFile()) {
					System.out.println("ERROR - the muscle path does not exist!");
					help.printHelp(usage, header, options, footer, true);
					System.exit(1);
					return;
				}
			}
		}
		// Complain if both or none muscle and clustal are provided
		if (Parameters.musclePath == "" && Parameters.clustalPath == "") {
			System.out.println("ERROR - please provide ONE external alignment program (clustal or muscle).");
			help.printHelp(usage, header, options, footer, true);
			System.exit(1);
			return;
		} else if (Parameters.musclePath != "" && Parameters.clustalPath != "") {
			System.out.println("ERROR - please provide only ONE external alignment program (clustal or muscle).");
			help.printHelp(usage, header, options, footer, true);
			System.exit(1);
			return;
		}
		
		// Parameters
		if (cli.hasOption("similarity")) {
			Parameters.threshold = Double.parseDouble(cli.getOptionValue("similarity"));
		}
		if (cli.hasOption("overlapfilter")) {
			Parameters.overlapActivated = true;
		}
		if (cli.hasOption("kmeans")) {
			Parameters.nbCluster = Integer.parseInt(cli.getOptionValue("kmeans"));
		}
		if (cli.hasOption("seed")) {
			Parameters.seedLength = Integer.parseInt(cli.getOptionValue("seed"));
		}
		if (cli.hasOption("length")) {
			Parameters.totalLength = Integer.parseInt(cli.getOptionValue("length"));
		}
		if (cli.hasOption("varIndels")) {
			String varIndels = cli.getOptionValue("varIndels");
			String[] paramBlocs = varIndels.split("#");
			for (int j = 0; j < paramBlocs.length; j++) {
				String[] paramVals = paramBlocs[j].split(":");
				// System.out.println(paramVals[0]+" "+paramVals[1]);
				if (paramVals[0].equalsIgnoreCase("default")) {
					try {
						Integer.parseInt(paramVals[1]);
						Parameters.setParamDefault(paramVals[1]);
					} catch (NumberFormatException ex) {
						System.out.println("ERROR - incorrect format of varIndels parameter!");
						help.printHelp(usage, header, options, footer, true);
						System.exit(1);
						return;
					}

				} else {
					try {
						Integer.parseInt(paramVals[0]);

						Parameters.setParam(paramVals[0], paramVals[1]);
					} catch (NumberFormatException ex) {
						System.out.println("ERROR - incorrect format of varIndels parameter!");
						help.printHelp(usage, header, options, footer, true);
						System.exit(1);
						return;
					}
				}
			}
		}
		
		// Print the parameters used
		System.out.println("########## Parameters ##################");
		System.out.println("# Sequences file: " + infile);
		System.out.println("# Output table file: " + table);
		System.out.println("# Output alignment file: " + align);
		System.out.println("# Threshold of similarity percentage: " + Parameters.threshold);
		System.out.println("# Variability percent for lengths: " + Parameters.aParam);
		System.out.println("# Number of clusters for the K-means: " + Parameters.nbCluster);
		System.out.println("# Minimum repeat region length: " + Parameters.totalLength);
		System.out.println("# Repeats seed length: " + Parameters.seedLength);
		System.out.println("# Overlap Filtering: " + Parameters.overlapActivated);
		System.out.println("# Clustal Path: " + Parameters.clustalPath);
		System.out.println("# Muscle Path: " + Parameters.musclePath);
		System.out.println("# Number of available cores: " + Runtime.getRuntime().availableProcessors());
		System.out.println("#########################################\n");

		// Print header to table file
		tableWriter.println("seqid\trepnumber\treplength\tstart\tend\tpsim\ttotlength");
		
		// We split the file if it is needed
		String[] files = FileSplitter.split(infile);
		
		// Run files in parallel
		Arrays.asList(files).parallelStream().forEach(file -> runFile(file, files.length > 1));
		
		System.out.println("# Finished successfully: " + countRep + " sequences have been detected as tandem repeats containing.");
		
		// close the output files
		alignWriter.close();
		tableWriter.close();

	}
	
	private static void runFile(String file, boolean delete) {

		File currentFile = new File(file);
		
		// cas o� le fichier est au format fasta
		DataToSequence data = new DataToSequence();
		
		LinkedList<SeqRepeat> sequences = new LinkedList<SeqRepeat>();

		try {
			sequences = data.fromFastaCMD(currentFile);
		} catch (FileNotFoundException e) {
			System.out.println("The file " + currentFile + " couldn't be opened.");
		}

		// exploration of repeats also in parallel
		sequences.parallelStream().forEach(seq -> findRepeats(seq));

		// clear the list of sequences (reduce memory?) and flush the writers
		sequences.clear();
		tableWriter.flush();
		alignWriter.flush();

		// delete the file if it was split
		if (delete) 
			currentFile.delete();
		
	}

	private static void findRepeats(SeqRepeat seqRep) {

		String seqId = seqRep.getDesc().split(" ")[0].substring(1);

		// Tandem repeat exploration
		TRExplorer Trex = new TRExplorer(seqRep);
		Trex.run();

		OverlapManager aCopies = Trex.getACopies();

		if (aCopies.size() > 0) {

			System.out.println("# Repeats found in sequence " + seqId);
			countRep++;
			
			for (int i = 0; i < aCopies.size(); i++) {
				
				Repeat rep = aCopies.get(i);
				
				// Test that the repeat is not an artifact from X residues
				if (rep.isReal()) {
					
					// Print the alignment
					String aString = "";
					for (int a = 0; a < rep.getCopies().size(); a++) {
						Copy copy = rep.getCopies().get(a);

						// Create the ID and sequence range
						String repId = String.format("%s/%d-%d\t", seqId, copy.getBeginPosition() + 1,
								copy.getEndPosition() + 1);

						aString += repId + copy.getSequence() + "\n";
					}
					aString += "//";
					alignWriter.println(aString);
					
					//Print table output
					String tString = String.format("%s\t%d\t%d\t%d\t%d\t%.2f\t%d",
							seqId,
							rep.getNumber(),
							rep.getLength(),
							rep.getBeginPosition() + 1,
							rep.getEndPosition() + 1,
							rep.getSimilarity(),
							rep.getRegionLength());
					
					tableWriter.println(tString);
					
				}
			}
			seqRep.clear();
			aCopies.clear();

		} else
			System.out.println("# Repeat NOT found in sequence " + seqId);
		
	}

	private static Options getOptions() {

		Options options = new Options();

		// Help
		options.addOption("h", "help", false, "Print usage information");

		// Input and output files
		options.addOption("f", "infile", true,
				"Path to file containing one or multiple sequences in FASTA format (required).");
		options.addOption("t", "table", true, "File path to save repeats in tabular format.");
		options.addOption("a", "align", true, "File path to save repeat alignments.");

		// External alignment tools
		options.addOption("m", "muscle", true, "Path to muscle executable (clustal or muscle required).");
		options.addOption("c", "clustal", true, "Path to clustalw executable (clustal or muscle required).");

		// Repeats detection parameters
		options.addOption("s", "similarity", true, "Minimum percentage of sequence similarity between repeats [default=0.7].");
		options.addOption("v", "varIndels", true, "Percentage of variability length due to indels.");
		options.addOption("o", "overlapfilter", false, "Filter overlapping repeats found in the same sequence [default=true].");
		options.addOption("k", "kmeans", true, "Number of clusters for the K-means [default=12].");
		options.addOption("S", "seed", true,
				"Length of the seed (number of residues) for repeat detection [default=2].");
		options.addOption("L", "length", true,
				"Minimum length of a repeating region [default=14].");

		return options;
	}
}
