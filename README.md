T-REKS high performance version
===============================

T-REKS is a k-means based algorithm for the identification of tandem repeats in protein and nucleotide sequences [1].

This repository contains a modified version of the T-REKS method with improved performance for large scale analyses. Below is a list of the modifications and improvements.

The original Java source code of the T-REKS algorithm and web-server version from the authors can be found here: https://bioinfo.crbm.cnrs.fr/?route=tools&tool=3.

### Changes from original version

- Command line options
	- Use `commons.cli` library to handle command line argument parsing
	- Allow specifying only one external program (muscle or clustal)
	- Set the SEED and minimum repeat region lengths from the CLI, to allow speedup when near-perfect repeats are searched

- Input
	- Disable the GUI and database options - the only input available is a single FASTA file.

- Output
	- Table output as tab separated and alignment output in stockholm format (easy to `grep` alignments)
	- Print a long log message with every sequence processed and parameters used to standard out

- Paralellization
	- Use `Java parallel streams` to process multiple files concurrently
	- Process sequences in each file concurrently

- Project organization
	- Mavenize the project to handle dependencies and build an executable JAR file automatically.
	- Code versioning with `git`.
	- Upgrade to `muscle 3.8`, `clustalw 2.1` and up to `Java 11`,
	- Provide Conda environment file to easily install dependencies.

- Bugfixes
	- `IndexOutOfBoundsException` in `RepeatBuilder.align()` method when repeats could not be aligned by the external program.
	- Temporal files written to the path of external alignment executables did not allow running two T-REKS instances concurrently. Create temporal files with unique hash codes in the working directory instead.
	- Fix the bug of writing the results to the output file specified in the `-outfile` option
	- There was a bug with very large files not being split because the size of the file was saved in an `integer` - now changed to `long`.

- Algorithmic
	- Reduce the number of alignments needed to speedup repeat detection.
	- Only alignment through external software allowed.


### Usage

Download the latest JAR file from the GitHub releases page. Here an example on how to run it against SwissProt database:

```
java -Xmx1G -jar T-ReksHPC_X.X.jar -f uniprot_sprot.fasta -t uniprot_sprot_treks.tsv -a uniprot_sprot_treks.aln -m muscle -S 10 -L 30 -s 0.9 > uniprot_sprot.log
```

This command takes about 5 minutes on a 4-core Intel processor.

### Installation

Install dependencies with Conda:

```
conda env install -f environment.yml
```

To build the project, clone the source code and use Maven inside the repository directory:

```
mvn install
```

The Jar file should be in the `target/` folder.

### Original publication

>[1] Julien Jorda, Andrey V. Kajava; T-REKS: identification of Tandem REpeats in sequences with a K-meanS based algorithm, Bioinformatics, Volume 25, Issue 20, 15 October 2009, Pages 2632–2638, https://doi.org/10.1093/bioinformatics/btp482

