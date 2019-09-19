T-REKS high performance version
===============================

T-REKS is a k-means based algorithm for the identification of tandem repeats in protein and nucleotide sequences [1].

This repository contains a modified version of the T-REKS method with improved performance for large scale analyses in HPC clusters.
Below is a list of the modifications.

The original Java source code, JAR file and T-REKS web-server are available here: https://bioinfo.crbm.cnrs.fr/?route=tools&tool=3.

I would like to thank the T-REKS authors ([Dr. Andrey Kajava's team at the CRBM](http://www.crbm.cnrs.fr/en/team/structural-biocomputing-and-molecular-modelling/)) for providing the source code and their support running T-REKS. 
All credit should be given to them citing the T-REKS publication [1].

### Changes from original version

- Command line options
	- Use `commons.cli` library to handle command line argument parsing.
	- Make a requirement to specify one external alignment program (muscle or clustal), but not both (only one is used). Only external alignment is available so the `msaMode` in the origianl T-REKS version is always set to external.
	- Add options to set the SEED and minimum repeat region lengths from the CLI, to allow faster analysis when near-perfect repeats (high sequence identity) and/or long repeats are searched.

- Input
	- Disable the database option - the only input available is a multi FASTA file of any length.
	- Disable the GUI - only the command line is available. This was needed due to interferences between the command line and the GUI modes of the software that needed X11 forwarding and caused unpredictable errors.

- Output
	- Results stored in a table output as tab separated rows (one for each repeat region) and an alignment output in Stockholm format (easy to `grep` alignments).
	- Print a log message to stdout with every sequence processed and whether repeats were found or not, to be able to check that all sequences were processed.
	- Print parameters used and final count of sequences with repeats in the log as a further check.

- Project organization
	- Use [Apache Maven](https://maven.apache.org/) to organize the project - this allows to handle dependencies and build an executable JAR file automatically.
	- Start source code versioning with `git`.
	- Upgrade to `muscle 3.8`, `clustalw 2.1` and make compatible up to `Java 11`.
	- Provide Conda environment file to easily install dependencies.

- Paralellization
	- Use `Java parallel streams` to process the multiple files (in case of large files that need splitting) and sequences in each file concurrently.
	- This version uses all cores available, and the speedup is almost perfect for up to 12 cores (tested). One caveat is that the more cores used the more memory is required - 12 cores would require about 10GB of RAM.
	
- Algorithmic
	- Reduce the number of alignments needed to speedup repeat detection - the original version was re-trying the alignment with clustal by default after aligning with muscle (feature or bug?).
	- Only alignment through external software - the source code for the custom alignment implemented in T-REKS was missing so the only option was to use an external alignment (muscle or clustal).
	
- Bugfixes
	- `IndexOutOfBoundsException` thrown in `RepeatBuilder.align()` method when repeats could not be aligned by the external program: error fixed and ensure that if it happens again an `Exeption` will be thrown.
	- Temporal files written to the path of external alignment executables did not allow running two T-REKS instances concurrently. Create temporal files with unique hash codes in the working directory instead.
	- Fix the bug of writing the results to the output file specified in the `-outfile` option - the file was rewritten for every repeat and always ended up being blank.
	- There was a bug with very large files not being split because the size of the file was saved in an `integer` - now changed to `long`.


### Usage

Download the latest JAR file from the GitHub releases page. Here an example on how to run it against SwissProt database:

```
java -Xmx1G -jar T-ReksHPC_X.X.jar -f uniprot_sprot.fasta -t uniprot_sprot_treks.tsv -a uniprot_sprot_treks.aln -m muscle -S 10 -L 30 -s 0.9 > uniprot_sprot.log
```

This command takes about 5 minutes on a 4-core Intel processor.

### Installation

Install dependencies with Conda:

```
conda env create -f environment.yml
```

To build the project from source, use Maven (inside this repository directory):

```
mvn install
```

The packaged JAR file should be in the `target/` folder.

### Original publication

>[1] Julien Jorda, Andrey V. Kajava; T-REKS: identification of Tandem REpeats in sequences with a K-meanS based algorithm, Bioinformatics, Volume 25, Issue 20, 15 October 2009, Pages 2632–2638, https://doi.org/10.1093/bioinformatics/btp482

