package nptr;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.TreeMap;

import nptr.utils.Debug;

/**
 * @author julien
 */
public class RepeatBuilder {

	/**
	 * @uml.property name="aGraines"
	 */
	private ArrayList<Sstring> aSstrings;
	private LinkedList<String> copies;
	private ArrayList<AlignCopies> runs;
	private ArrayList<Repeat> BDrepeats;
	private TreeMap<Integer, Position> orderedSstrings;
	private SeqRepeat sequence;
	/**
	 * @uml.property name="longueur"
	 */
	private int longueur;

	public RepeatBuilder(int longueur) {
		this.aSstrings = new ArrayList<Sstring>();
		this.copies = new LinkedList<String>();
		this.BDrepeats = new ArrayList<Repeat>();
		this.longueur = longueur;
		this.runs = new ArrayList<AlignCopies>();
	}

	/**
	 * @return
	 * @uml.property name="aGraines"
	 */
	public ArrayList<Sstring> getAGraines() {
		return aSstrings;
	}

	/**
	 * @return
	 * @uml.property name="longueur"
	 */
	public int getLongueur() {
		return longueur;
	}

	/**
	 * @return
	 * @uml.property name="copies"
	 */
	public LinkedList<String> getCopies() {
		return copies;
	}

	/**
	 * @param copies
	 * @uml.property name="copies"
	 */
	public void setCopies(LinkedList<String> copies) {
		this.copies = copies;
	}

	public void addGraine(Sstring gr) {
		this.aSstrings.add(gr);
	}

	/**
	 * Search the repeats that are equal or close to clusterized length
	 * 
	 * @param s:
	 *            Sequence to analyze
	 */
	public void search(SeqRepeat s) {
		sequence = s;
		// Debug.set(true);
		// orderedSstrings=this.getSstrings();
		// this.buildRuns(this.orderedSstrings);

		orderedSstrings = getSstringsV2();
		// building of runs from the list of sstrings close to the clusterized
		// length
		this.buildRunsV2(this.orderedSstrings);
		// this.buildRunsV3(this.orderedSstrings);
	}

	/**
	 * get the sstrings form the cluster and reorder them by position in the
	 * sequence
	 */
	private TreeMap<Integer, Position> getSstrings() {

		Map<Integer, Position> runSeeds = new HashMap<Integer, Position>();

		/*
		 * on r�cup�re pour chaque position possible le nom de la graine et sa
		 * distance
		 */
		for (int i = 0; i < aSstrings.size(); i++) {
			for (int p = 0; p < aSstrings.get(i).getPositions().size(); p++) {
				aSstrings.get(i).getPositions().get(p).setGraine(aSstrings.get(i).getName());
				// reconstitution partielle de la s�quence en associant � chaque
				// indice son objet position
				runSeeds.put(aSstrings.get(i).getPositions().get(p).getPos(), aSstrings.get(i).getPositions().get(p));
			}
		}
		// ordering Sstrings by their position via a TreeMap
		return new TreeMap<Integer, Position>(runSeeds);
	}

	private TreeMap<Integer, Position> getSstringsV2() {
		TreeMap<Integer, Position> runSeeds = new TreeMap<Integer, Position>();
		// rebuilding of list of short strings with a seed length equal to 1
		int[] lengthList = this.sequence.getLengthList();
		/*
		 * if(longueur<4) {
		 * 
		 * SequenceSeeder seqSeeder=new SequenceSeeder(this.sequence);
		 * seqSeeder.scanSequence(1);
		 * lengthList=this.sequence.getLengthListOneRes(); }
		 */

		// System.out.println("#########LONGUEUR="+longueur+"############");
		for (int i = 0; i < lengthList.length; i++) {
			if (Position.nearDist(lengthList[i], longueur)) {
				Debug.print(i + " - length:" + lengthList[i]);
				Position sStringPos = new Position(i);
				sStringPos.setDist(lengthList[i]);
				sStringPos.setGraine(sequence.getSequence().substring(i, i + Parameters.seedLength));
				runSeeds.put(i, sStringPos);
			} else
				Debug.print("****" + i + " - length:" + lengthList[i]);
		}

		return runSeeds;
	}

	private void buildRunsV3(Map<Integer, Position> sortedSeeds) {
		// StringBuffer seq=new StringBuffer(sequence.getSequence());
		Object[] keys = sortedSeeds.keySet().toArray();
		for (int i = 0; i < keys.length; i++) {
			// current analyzed position of the sstring with length equal or
			// close
			int cursorPosition = (Integer) keys[i];
			Debug.print("cursorPosition " + cursorPosition);
			int j = cursorPosition;
			int countLengths = 1;
			int cursorIndex = i;
			// setting limit for acceptable gaps between sstrings
			double limit = longueur * Parameters.threshold;
			/*
			 * if(longueur<4) { limit=longueur; }else limit=longueur/2;
			 */
			// browsing from the cursor position to the position located at
			// "length" residues the number of sstrings
			do {
				if (j != cursorPosition)
					countLengths++;
				cursorIndex++;
				if (cursorIndex < keys.length)
					j = (Integer) keys[cursorIndex];
			} while ((j - (Integer) keys[cursorIndex - 1]) <= limit && cursorIndex < keys.length);
			j = (Integer) keys[cursorIndex - 1];
			Debug.print("countLengths " + countLengths + " cursorPosition " + cursorPosition + " and j: " + j);
			// if there are more than 50% Sstrings equal to length in this
			// region
			if (countLengths >= (j - cursorPosition) / 5) {
				int firstResidue = cursorPosition;
				int lastResidue = j;

				int longLength = lastResidue - firstResidue;
				double nbOfCopies = Math.ceil(longLength / longueur);
				nbOfCopies++;
				StringBuffer realRepeat = new StringBuffer("");
				int first = firstResidue;
				while (realRepeat.length() < longLength && first < sequence.getSequence().length() - 1) {
					int last = first + longueur;
					if (longueur + first > sequence.getSequence().length()) {
						last = sequence.getSequence().length();
					}
					realRepeat.append(sequence.getSequence().substring(first, last));
					first = last;
				}

				for (int k = firstResidue; k < firstResidue + longueur * (Math.round(nbOfCopies / 3)); k += longueur) {
					StringBuffer idealRepeat = new StringBuffer("");

					int borderoflastcop = k + longueur;
					if (borderoflastcop > sequence.getSequence().length())
						borderoflastcop = sequence.getSequence().length();
					for (int n = 0; n < (nbOfCopies); n++) {
						idealRepeat.append(sequence.getSequence().substring(k, borderoflastcop));
					}
					AlignCopies aCopies = new AlignCopies();
					// set the first copy as copy of reference
					aCopies.add(new Copy(idealRepeat.toString()));

					aCopies.add(new Copy(realRepeat.toString(), cursorPosition, j));

					AlignCopies alignCopies = this.align(aCopies, Aligner.muscle);
					Debug.print("alignment center-star");
					Debug.print(alignCopies.toString());
					PSim perf = new PSim(alignCopies);
					perf.compute();
					if (perf.getSimilarity() >= Parameters.threshold && nbOfCopies >= 2
							&& longLength >= Parameters.totalLength) {
						Debug.print(alignCopies.toString());

						for (int c = 0; c < aCopies.size(); c++) {
							if (aCopies.get(c).getLength() < 2)
								aCopies.remove(c);
						}
						this.appendResult(aCopies, perf.getSimilarity(), perf.getConsensus());
						Debug.print("i " + i + " j " + j + " cursorIndex " + cursorIndex);

						alignCopies.clear();
						break;
					}
				}
				i = cursorIndex;
			}
		}
	}

	private void buildRunsV2(Map<Integer, Position> sortedSeeds) {
		StringBuffer seq = new StringBuffer(sequence.getSequence());
		Object[] keys = sortedSeeds.keySet().toArray();
		// System.out.println(aSstrings.toString());
		for (int i = 0; i < keys.length; i++) {
			// current analyzed position of the sstring with length equal or
			// close
			int cursorPosition = (Integer) keys[i];
			Debug.print("cursorPosition " + cursorPosition);
			int j = cursorPosition;
			int countLengths = 1;
			int cursorIndex = i;
			// browsing from the cursor position to the position located at
			// "length" residues the number of sstrings
			while (j <= cursorPosition + sortedSeeds.get((Integer) cursorPosition).getDist()
					&& cursorIndex < keys.length) {
				if (j != cursorPosition)
					countLengths++;
				j = (Integer) keys[cursorIndex];
				cursorIndex++;
			}
			Debug.print("countLengths " + countLengths);
			// if there are more than 50% Sstrings equal to length in this
			// region
			if (countLengths >= longueur / 2) {
				int firstResidue = sortedSeeds.get(keys[i]).getPos();
				int lastResidue;
				try {
					lastResidue = (Integer) keys[cursorIndex - 2];
				} catch (java.lang.ArrayIndexOutOfBoundsException ex) {
					lastResidue = (Integer) keys[0];
				}
				AlignCopies aCopies = new AlignCopies();
				// rebuild the copies
				int iCopy = firstResidue;
				int lastResOfCopy = iCopy + sortedSeeds.get(iCopy).getDist();
				Debug.print("first res " + firstResidue + " last res " + lastResidue);
				boolean previousCopyChecked = false;
				while (iCopy < seq.length() && lastResOfCopy != iCopy) {
					Debug.print("iCopy " + iCopy);

					if (sortedSeeds.containsKey(iCopy)) {
						aCopies.add(new Copy(seq.substring(iCopy, lastResOfCopy), iCopy, lastResOfCopy - 1));
						Debug.print("copy added, lastResofCopy is " + lastResOfCopy);
						if (!previousCopyChecked) {
							// case of two first copies with the second highly
							// degenerated and is not detected, we add the
							// earlier copies
							Sstring sstringOfCopy;
							sstringOfCopy = getSstring(seq.substring(iCopy, iCopy + Parameters.seedLength));
							Position previousPos = sstringOfCopy.getPreviousPosition(iCopy);
							if (previousPos != null) {
								int previous = previousPos.getPos();
								Debug.print("previous position " + previous);
								// we count the two earlier copies
								if (Position.nearDist(iCopy - previous, longueur * 2)) {
									aCopies.add(aCopies.size() - 1,
											new Copy(seq.substring(previous, previous + longueur), previous,
													previous + longueur - 1));
									aCopies.add(aCopies.size() - 1, new Copy(seq.substring(previous + longueur, iCopy),
											previous + longueur, iCopy - 1));
								}
							}
							previousCopyChecked = true;
						}
					} else if (iCopy <= lastResidue) {
						if (lastResOfCopy > lastResidue + sortedSeeds.get(lastResidue).getDist()) {
							lastResOfCopy = lastResidue + sortedSeeds.get(lastResidue).getDist();
							Debug.print("last residue is out the area");
						} else {
							if (sortedSeeds.containsKey(lastResOfCopy)) {
								lastResOfCopy = iCopy + sortedSeeds.get(iCopy).getDist();
								Debug.print("lastResofCopy exist at " + lastResOfCopy);
							} else {
								lastResOfCopy = findClosestPositionFrom(keys, iCopy + sortedSeeds.get(iCopy).getDist());
								Debug.print("lastResofCopy approximated at " + lastResOfCopy);
							}
						}
						aCopies.add(new Copy(seq.substring(iCopy, lastResOfCopy), iCopy, lastResOfCopy - 1));
					} else {// last but one copy or last copy
						int lastRes = seq.length();
						if (iCopy + longueur < seq.length()) {
							lastRes = iCopy + longueur;
							// complete extra residues of this copy
							/*
							 * boolean extension=true; for(int
							 * l=0;l<aCopies.getLast().getLength()-longueur &&
							 * extension;l++) {
							 * if(aCopies.getLast().getSequence().charAt(
							 * longueur+l)==sequence.getSequence().charAt(
							 * lastRes+l)) { lastRes++; }else extension=false; }
							 */
						}

						aCopies.add(new Copy(seq.substring(iCopy, lastRes), iCopy, lastRes - 1));
						Debug.print("lastResofCopy of possible lastbutOneCopy at " + lastRes);

						// case of bridging two regions, add the copy if it's
						// inserted between two hifi copies
						// can be the last copy too
						// TODO maybe treat 2 and 3-residues long copies
						// specifically
						if (lastRes + Parameters.seedLength < seq.length()) {
							// Sstring
							// clusterizedSstring=getSstring(seq.substring(lastRes,lastRes+Parameters.seedLength));
							// Debug.print(clusterizedSstring.toString());
							if (lastRes + longueur < seq.length())
								lastResOfCopy = lastRes + longueur;
							else
								lastResOfCopy = seq.length();
							int next = this.getNextPosition(sortedSeeds, lastRes);
							Debug.print("next position for next possible last copy" + next);
							if (sortedSeeds.containsKey(lastResOfCopy)) {
								iCopy = lastRes;
								aCopies.add(new Copy(seq.substring(iCopy, lastResOfCopy), iCopy, lastResOfCopy - 1));
								Debug.print("lastResofCopy of bridging/last copy(perfect one) at " + lastResOfCopy);
							} else if (Position.nearDist(next - lastRes, longueur) || next == lastRes) {
								if (next == lastRes) {
									aCopies.add(new Copy(seq.substring(lastRes, lastResOfCopy), lastRes,
											lastResOfCopy - 1));

									break;
								}
								iCopy = lastRes;
								lastResOfCopy = next;
								aCopies.add(new Copy(seq.substring(iCopy, lastResOfCopy), iCopy, lastResOfCopy - 1));
								Debug.print("lastResofCopy of bridging/last copy at " + lastResOfCopy);

								/*
								 * }else
								 * if(Position.nearDist(clusterizedSstring.
								 * getDist(),longueur)) { iCopy=lastRes;
								 * lastResOfCopy=iCopy+longueur; if
								 * (lastResOfCopy>seq.length())
								 * lastResOfCopy=seq.length(); aCopies.add(new
								 * Copy(seq.substring(iCopy,lastResOfCopy),iCopy
								 * ,lastResOfCopy-1)); break;
								 */
							} else {
								int lev = Levenshtein.getLevenshteinDistance(aCopies.getLast().getSequence(),
										seq.substring(lastRes, lastResOfCopy));
								Debug.print("levenshtein" + lev);
								if (lev <= longueur / 2) {
									aCopies.add(new Copy(seq.substring(lastRes, lastResOfCopy), lastRes,
											lastResOfCopy - 1));
								}
								break;
							}
						} else
							break;
					}
					iCopy = lastResOfCopy;
					if (sortedSeeds.containsKey(iCopy))
						lastResOfCopy = iCopy + sortedSeeds.get(iCopy).getDist();
					else
						lastResOfCopy = iCopy + longueur;
				}
				Debug.print(aCopies.toString());
				if (aCopies.size() >= 2 && aCopies.hasValidLength()) {
					// this.runs.add(aCopies);
					// TODO: restablish this line by testing if the run is a
					// repeat or not
					if (this.alignAndTrim(aCopies))
						i = cursorIndex;
				}

			}
		}
	}

	private Integer findClosestPositionFrom(Object[] positions, int pos) {
		int i = 0;
		while (i < positions.length && (Integer) positions[i] < pos) {
			i++;
		}
		if (i >= positions.length)
			i = positions.length - 1;
		if (Math.abs(pos - (Integer) positions[i - 1]) < Math.abs(pos - (Integer) positions[i]))
			return (Integer) positions[i - 1];
		else
			return (Integer) positions[i];
	}

	private void buildRuns(Map<Integer, Position> sortedSeeds) {
		StringBuffer seq = new StringBuffer(sequence.getSequence());
		Object[] cles = sortedSeeds.keySet().toArray();

		/**
		 * browsing of the map we stop to the first dist close to the wanted
		 * length and start searching runs
		 **/

		// int tandemLength=0;
		for (int i = 0; i < cles.length; i++) {
			int intPos = (Integer) cles[i];
			/* Position de la graine courante */
			Position currentPos = sortedSeeds.get(intPos);
			if (currentPos.nearDist(longueur))
				Debug.print(" sstring " + currentPos.getGraine());
		}
		// AlignCopies aCopies;
		for (int i = 0; i < cles.length; i++) {
			boolean runFound = false;
			/* liste des copies du pattern en cours */
			AlignCopies aCopies = new AlignCopies();
			int intPosCurrent = (Integer) cles[i];
			/* Position de la graine courante */
			Position currentPos = sortedSeeds.get(intPosCurrent);
			int posfin = i;
			// if(longueur==11) {
			do {
				// indice de la position
				intPosCurrent = currentPos.getPos();

				Debug.print(currentPos.getGraine() + " pos:" + intPosCurrent + "  dist:" + currentPos.getDist());
				if (currentPos.nearDist(longueur)) {
					Debug.print(currentPos.getGraine() + " pos:" + intPosCurrent + "  dist:" + currentPos.getDist());
					runFound = true;
					// on stocke le pattern
					int posEstime = intPosCurrent + currentPos.getDist();

					// on cherche la position de la premi�re graine de la
					// prochaine copie
					posfin = findLastPosForCopy(cles, posEstime);

					/*
					 * on calcule le nombre graines pr�sentes dans ce pattern
					 * qui peuvent faire partie du run
					 */
					double cpt = 0;
					for (int c = 0; c < cles.length; c++) {
						if ((Integer) cles[c] >= intPosCurrent && (Integer) cles[c] <= posEstime) {
							if (sortedSeeds.get(cles[c]).nearDist(longueur)) {
								cpt++;
							}
						}
					}
					/* si on trouve dans la copie estim�e une autre graine */
					/*
					 * cpt >1 d'habitude mais si longueur=2, marche mieux si
					 * cpt>0 , � tester dans le temps
					 */
					if (cpt > 0 && posEstime - intPosCurrent > 1 && posfin != intPosCurrent) {
						if (!seq.substring(intPosCurrent, posEstime).equals("")) {
							Debug.print("perfect: " + seq.substring(intPosCurrent, posEstime) + " " + intPosCurrent
									+ " " + posEstime);
							Copy estimatedCopy = new Copy(seq.substring(intPosCurrent, posEstime), intPosCurrent,
									posEstime - 1);
							aCopies.add(estimatedCopy);
						}
						// tandemLength+=posEstime-intPosCurrent;
					}

					// mise � jour de la graine courante
					currentPos = sortedSeeds.get(cles[posfin]);
					Debug.print("next detected after perfect: " + currentPos.getPos());
				} else {
					// on regarde si dans ce pattern on ne trouve pas des copies
					int intPosEnd;
					if (intPosCurrent + longueur < seq.length())
						intPosEnd = intPosCurrent + longueur;
					else {
						intPosEnd = (Integer) cles[cles.length - 1];
						Debug.print("exit0");
						runFound = false;
					}

					String subTemp = seq.substring(intPosCurrent, intPosEnd);
					posfin = findLastPosForCopy(cles, intPosEnd);
					Debug.print(subTemp + " pos:" + intPosCurrent + " dist:" + longueur + " NotNeardist");

					// si on trouve une graine de la meme longueur dans cette
					// copie possible
					if ((this.catchBetweenRunCopy(intPosCurrent, intPosEnd)
							|| (longueur == 2 && this.catchTwoResCopy(currentPos, subTemp)
									|| (longueur == 3 && this.catchThreeResCopy(currentPos, subTemp)))
									&& sortedSeeds.get(cles[posfin]).nearDist(longueur))
							&& !subTemp.equals("")) {
						Debug.print("\t intposcurrent " + intPosCurrent + " " + intPosEnd);
						Debug.print("subtemp:" + subTemp);
						aCopies.add(new Copy(subTemp, intPosCurrent, intPosEnd - 1));
						if (sortedSeeds.containsKey(intPosEnd)) {
							if (longueur == sortedSeeds.get(intPosEnd).getDist()) {
								currentPos = sortedSeeds.get(intPosEnd);
								// continue;
								runFound = true;
							}
						}

						// tandemLength+=longueur;
						// on cherche la prochaine position de la graine trait�e
						Sstring GraineTreat = this.getSstring(currentPos.getGraine());
						Position nextPos = GraineTreat.getNextPosition(intPosCurrent);
						Debug.print("next seed estimated after tempcopy: " + GraineTreat.getName());
						// on stocke la graine de d�but de copie pr la r�cuperer
						// une fois les gaps pass�s
						if (nextPos != null && nextPos.getPos() >= intPosCurrent + longueur) {
							// if(nextPos!=null) {
							if (nextPos.getPos() == intPosCurrent + longueur) {
								currentPos = nextPos;
								Debug.print("\t next Pos with length equal " + currentPos.getPos());
							} else {
								Debug.print("\t next Pos " + nextPos.getPos());
								// on g�n�re une position fictive de cette
								// graine
								Position fictivePos = new Position(intPosEnd);
								fictivePos.setGraine(
										sequence.getSequence().substring(intPosEnd, intPosEnd + Parameters.seedLength));
								fictivePos.setDist(nextPos.getPos() - intPosEnd);
								currentPos = fictivePos;
								Debug.print("\t fictive pos " + currentPos.getPos() + " " + currentPos.getGraine()
										+ " dist: " + currentPos.getDist());
								runFound = true;
							}
							/*
							 * cas o� on a la derni�re copie qui est d�g�n�r�e
							 */
						} else {
							int lastIntPosEnd = intPosEnd + longueur;
							if (lastIntPosEnd > seq.length())
								lastIntPosEnd = seq.length();
							if (this.catchBetweenRunCopy(intPosEnd, lastIntPosEnd)
									|| (longueur == 2 && this.catchTwoResCopy(currentPos,
											seq.substring(intPosEnd, lastIntPosEnd)))
									|| (longueur == 3 && this.catchThreeResCopy(currentPos,
											seq.substring(intPosEnd, lastIntPosEnd)))) {
								aCopies.add(new Copy(seq.substring(intPosEnd, lastIntPosEnd), intPosEnd,
										lastIntPosEnd - 1));
								// tandemLength+=lastIntPosEnd-intPosEnd;
								if (nextPos != null && Position.nearDist(lastIntPosEnd - intPosEnd,
										nextPos.getPos() - lastIntPosEnd)) {
									currentPos = nextPos;
								} else {
									Debug.print("last degenerated copy " + seq.substring(intPosEnd, lastIntPosEnd));
									Debug.print("exit1");
									runFound = false;
								}

							} else {
								Debug.print("exit2");
								runFound = false;
							}
						}
					} else if (this.catchCopy(intPosCurrent, intPosEnd)) {
						posfin = findLastPosForCopy(cles, intPosEnd);
						Debug.print("estimated: " + subTemp + " current " + intPosCurrent + " posfin "
								+ (Integer) cles[posfin]);
						if (intPosCurrent < (Integer) cles[posfin] && (Integer) cles[posfin] - intPosCurrent > 1) {

							String potentialCopy = seq.substring(intPosCurrent, (Integer) cles[posfin]);
							// si ;la longueur de la copie potentielle est
							// porche de longueur ducluster ou si la copie est
							// plus courte car tronqu�e.
							if (Position.nearDist(potentialCopy.length(), longueur)
									|| potentialCopy.length() < longueur) {
								aCopies.add(new Copy(potentialCopy, intPosCurrent, ((Integer) cles[posfin]) - 1));
							}
							/* TODO OO */
							// tandemLength+=(Integer)cles[posfin]-intPosCurrent;
						}
						// si on ne trouve pas de pattern de fin de copie, on
						// ajoute juste la graine dans le cas o� distance=2
						else if ((Integer) cles[posfin] == intPosCurrent && longueur == 2 && !subTemp.equals("")) {
							aCopies.add(new Copy(subTemp, intPosCurrent, intPosEnd - 1));
							// tandemLength+=2;
						}
						if (posfin < cles.length - 1 && intPosCurrent != (Integer) cles[posfin]) {
							runFound = true;
						} else {
							Debug.print("exit3");
							runFound = false;
						}

						currentPos = sortedSeeds.get(cles[posfin]);

					} else {
						Debug.print("exit4");
						runFound = false;
					}
				}
			} while (runFound);
			Debug.print("------------------------------------");
			// }
			// aCopies.setRegionLength(tandemLength);
			if (aCopies.size() >= 2 && aCopies.hasValidLength()) {

				runs.add(aCopies);

				if (Position.nearDist(aCopies.get(0).getSequence().length(), longueur)) {
					if (posfin != 0)
						i = posfin;
					else
						i = cles.length;
				}
			}
		}

	}

	// take the run, align it, and trim the wrong copies
	public boolean alignAndTrim(AlignCopies aCopies) {
		
		boolean repeatAdded = false;
		
		// test si on a plusieurs copies du pattern et si les r�petitions en
		// tandem s'�tendent au dela de 9 r�sidus
		if (Position.nearDist(aCopies.get(0).getSequence().length(), longueur)) {
			
			if (aCopies.size() >= 2) {
				/*
				 * ALIGNEMENT
				 */

				AlignCopies alignCopies = new AlignCopies();

				/**
				 * PERFECTION calcul de similarit� , �limination de copies de
				 * l'alignement si elle ne passe pas le seuil
				 **/

				PSim perf = new PSim();

				// Alignment with MUSCLE
				if (Parameters.musclePath != "") {
					
					if (!repeatAdded) {
						
						alignCopies = this.align(aCopies, Aligner.muscle);

						perf = new PSim(alignCopies);
						perf.compute();
						
						if (perf.getSimilarity() >= Parameters.threshold && alignCopies.hasValidLength()) {
							
							this.appendResult(alignCopies, perf.getSimilarity(), perf.getConsensus());
							repeatAdded = true;
							
							/**
							 * on �limine des copies de l'alignement qui font
							 * chuter la similarit�
							 **/
						} else if (alignCopies.size() > 2) {
							repeatAdded = this.trimCopies(perf);
						} else {
							// skip the further alignments and trimmings by
							// setting repeatAdded to true
							if (perf.getSimilarity() < Parameters.threshold - 0.15)
								repeatAdded = true;
						}
					}
				}

				// Alignment with CLUSTAL
				if (Parameters.clustalPath != "") {
					alignCopies = this.align(aCopies, Aligner.clustal);
					// System.out.println("alignment de clustal");
					// System.out.println(alignCopies.toString());
					perf = new PSim(alignCopies);
					perf.compute();
					if (perf.getSimilarity() >= Parameters.threshold && alignCopies.hasValidLength()) {
						this.appendResult(alignCopies, perf.getSimilarity(), perf.getConsensus());
						repeatAdded = true;
					} else if (alignCopies.size() > 2) {
						repeatAdded = this.trimCopies(perf);
					}
				}
			}
		}
		return repeatAdded;
	}

	/**
	 * appelle le programme externe clustal et r�cup�re l'alignement g�n�r�
	 * 
	 * @param mots:LinkledList
	 *            contenant les motifs � aligner
	 * @param typeAlign:
	 *            type d'alignement Muscle, clustalw...
	 */
	public AlignCopies align(AlignCopies mots, int typeAlign) {

		LinkedList<String> retMots = Aligner.compute(mots, typeAlign);

		/*
		 * on �crase les copies non align�es par celles align�es par le
		 * programme
		 */
		AlignCopies retCopies = new AlignCopies();

		for (int i = 0; i < mots.size(); i++) {
			retCopies.add(
					new Copy(mots.get(i).getSequence(), mots.get(i).getBeginPosition(), mots.get(i).getEndPosition()));
			retCopies.get(i).setSequence(retMots.get(i));
		}
		retCopies.setRegionLength(mots.getRegionLength());

		return retCopies;
	}

	public boolean trimCopies(PSim perf) {
		AlignCopies alignCopies = (AlignCopies) perf
				.getAlignment()/* .clone() */;
		double similarity = perf.getSimilarity();
		// System.out.println("trimming ran");
		boolean calculate = false;
		/*
		 * System.out.println("avant"); System.out.println(alignCopies);
		 */
		while (similarity < Parameters.threshold && alignCopies.size() > 2 && alignCopies.hasValidLength()) {
			HammingDistance upHD = new HammingDistance(perf.getConsensus(), alignCopies.getFirst().getSequence());
			HammingDistance lowHD = new HammingDistance(perf.getConsensus(), alignCopies.getLast().getSequence());
			if (upHD.getDistance() > lowHD.getDistance()) {
				// remove the first copy
				alignCopies.removeFirst();
			} else {
				// remove the last copy
				alignCopies.removeLast();
			}
			perf = new PSim(alignCopies);
			perf.compute();
			similarity = perf.getSimilarity();

			calculate = true;
		}
		// cleaning up the empty columns due to alignments
		perf.clean();
		// on shunte le processus d'alignment et de trimming si la similarit�
		// est excessivement trop basse
		// if(similarity<Parameters.threshold-0.15) return true;
		Debug.print("After trimming");
		Debug.print(alignCopies.toString());
		if (similarity >= Parameters.threshold && calculate && alignCopies.hasValidLength()) {
			this.appendResult(alignCopies, similarity, perf.getConsensus());
			return true;
		} else
			return false;
	}

	private int findLastPosForCopy(Object[] cles, int p) {
		int endingPos = (Integer) cles[cles.length - 1];
		boolean ending = false;
		int k = 0;
		/* cas o� la position d�passe la derni�re position existante */
		if (p >= endingPos) {
			return cles.length - 1;
		} else {
			while (!ending && k < cles.length) {
				if (Math.abs((Integer) cles[k] - p) < endingPos) {
					endingPos = Math.abs((Integer) cles[k] - p);
					k++;
				} else {
					ending = true;
				}
			}
			if (k > 0)
				k--;
			return k;
		}
	}

	private Integer getNextPosition(Map<Integer, Position> map, int pos) {
		Object[] keys = map.keySet().toArray();
		for (int i = 0; i < keys.length; i++) {
			if ((Integer) keys[i] == pos) {
				return (Integer) keys[i++];
			} else if ((Integer) keys[i] > pos) {
				return (Integer) keys[i];
			}
		}
		return 0;
	}

	private Integer getPreviousPosition(Map<Integer, Position> map, int pos) {
		Object[] keys = map.keySet().toArray();
		for (int i = 0; i < keys.length; i++) {
			if ((Integer) keys[i] > pos) {
				return (Integer) keys[i - 2];
			} else if ((Integer) keys[i] == pos) {
				try {
					return (Integer) keys[i - 1];
				} catch (java.lang.ArrayIndexOutOfBoundsException ex) {
					return (Integer) keys[i];
				}
			}
		}
		return 0;
	}

	private Sstring getSstring(String gr) {
		int i = 0;
		Sstring seed = new Sstring();
		boolean found = false;
		while (i < aSstrings.size() && !found) {
			if (aSstrings.get(i).getName().equals(gr)) {
				seed = aSstrings.get(i);
				found = true;
			}
			i++;
		}
		return seed;
	}

	private Sstring getSstring(int first) {
		String gr = this.sequence.getSequence().substring(first, first + Parameters.seedLength);
		return getSstring(gr);
	}

	private boolean SstringAtPosition(int pos) {
		int i = 0;
		boolean found = false;
		while (i < aSstrings.size() && !found) {
			found = aSstrings.get(i).isPosition(pos);
		}
		return found;
	}

	private boolean catchBetweenRunCopy(int deb, int fin) {

		double cpt = 0;
		Object[] cles = orderedSstrings.keySet().toArray();
		for (int sub = 0; sub < cles.length; sub++) {
			int pInt = (Integer) cles[sub];
			if (pInt >= deb && pInt <= fin) {
				if (orderedSstrings.containsKey(pInt)) {
					cpt++;
				}
			}
		}
		if (cpt / (double) (longueur - 1) >= 0.5)
			return true;
		else
			return false;
	}

	/**
	 * si une copie existe par rapport au nombre de graines valides qu'elle
	 * contient, renvoie un bool�en
	 * 
	 * @param deb
	 *            : position de d�but
	 * @param fin
	 *            : position de fin
	 * @return bool�en
	 */
	private boolean catchCopy(int deb, int fin) {

		double cpt = 0;
		Object[] cles = orderedSstrings.keySet().toArray();
		boolean[] CatchedResidues = new boolean[fin - deb];
		for (int sub = 0; sub < cles.length; sub++) {
			int pInt = (Integer) cles[sub];
			if (pInt >= deb && pInt < fin - Parameters.seedLength) {
				if (orderedSstrings.containsKey(pInt)) {
					Position hypotheticSeed = orderedSstrings.get(pInt);
					Sstring hypSstring = this.getSstring(hypotheticSeed.getGraine());
					if (hypotheticSeed.nearDist(longueur) || (Position.nearDist(longueur, hypSstring.getDist()))) {
						for (int cIndex = 0; cIndex < Parameters.seedLength; cIndex++) {
							CatchedResidues[pInt - deb + cIndex] = true;
						}
					}
				}
			}
		}
		for (int cIndex = 0; cIndex < CatchedResidues.length; cIndex++) {
			if (CatchedResidues[cIndex])
				cpt++;
		}
		// Debug.print("catchCOpy "+cpt/(double)(longueur-1));
		if (cpt / (double) (longueur - 1) > 0.4)
			return true;
		else
			return false;
	}

	// specific catchCopy for copy with only one Sstring
	public boolean catchTwoResCopy(Position cp, String substring) {
		String ss = cp.getGraine();
		for (int i = 0; i < ss.length(); i++) {
			for (int j = 0; j < substring.length(); j++) {
				if (ss.charAt(i) == substring.charAt(j))
					return true;
			}
		}
		return false;
	}

	// specific catchCopy for copy with two Sstring overlapping
	public boolean catchThreeResCopy(Position cp, String substring) {
		String ss = cp.getGraine();
		int cpt = 0;
		for (int i = 0; i < ss.length(); i++) {
			for (int j = 0; j < substring.length(); j++) {
				if (ss.charAt(i) == substring.charAt(j))
					cpt++;
			}
		}
		if (cpt >= 2)
			return true;
		return false;
	}

	/**
	 * ajoute � copies le r�sultat mis en forme pour le GUI
	 * 
	 * @param alignCopies
	 *            alignement des repeats d�tect�s
	 * @param sim
	 *            :pourcentage de similarit�
	 */
	public void appendResult(AlignCopies alignCopies, double sim, String consensus) {
		Repeat repet = new Repeat(sim, longueur, alignCopies);
		// System.out.println("appendresult "+alignCopies.getRegionLength());
		repet.setSeqId(sequence.getId());
		repet.setPattern(consensus);
		repet.setDB(sequence.getDB().getId());
		repet.setSeqLength(sequence.size());
		BDrepeats.add(repet);
	}

	public void clear() {
		this.copies.clear();
	}

	public ArrayList<Repeat> getBDrepeats() {
		return BDrepeats;
	}

	public void setBDrepeats(ArrayList<Repeat> drepeats) {
		BDrepeats = drepeats;
	}

	public SeqRepeat getSequence() {
		return sequence;
	}

	public void setSequence(SeqRepeat sequence) {
		this.sequence = sequence;
	}

}
