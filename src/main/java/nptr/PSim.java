package nptr;


import java.util.LinkedList;

import nptr.utils.Hash;


public class PSim {

	private AlignCopies alignment;
	private String consensus;
	private double similarity;
	
	public PSim() {
		alignment=new AlignCopies();
	}
	public PSim(AlignCopies al) {
		alignment=al;
	}
	public void setData(AlignCopies al) {
		alignment=al;
	}
	public AlignCopies getData() {
		return alignment;
	}
	
	
	public void compute() throws java.lang.StringIndexOutOfBoundsException{
		String pattern="";
		Hash column;
		consensus="";
		int countEmptyCol=0;
		if (alignment.size()>0) {
			double nbMatchs=0;
			try {
				//we  go over the columns of the alignment
				for (int p=0;p<alignment.get(0).getSequence().length();p++) {
					column=new Hash();
					for (int i=0;i<alignment.size();i++) {
						pattern=alignment.get(i).getSequence();
						int nb=1;
							try {
								Character car=new Character(pattern.charAt(p));
								if(column.containsKey(car)) nb=Integer.parseInt(column.get(car).toString())+1;
								column.put(car,nb);
							}catch (Exception e) {
								System.out.println( "could not align:");
								System.out.println(alignment);
							}
						
					}
					column=column.sort(false);
					//in  case of the residue is in the majority, or if there is ex aequo between several residues but in nb >1
					int nbResCons=Integer.parseInt(column.getFirstValue().toString());
					if(nbResCons>1) {
							if(!column.getFirst().equals(new Character('-'))) {
								//the major residue is added to consensus
								consensus+=column.getFirst();
								//count the nb of matches
								nbMatchs+=nbResCons;
							}else if(nbResCons!=alignment.size()) {
//								the major residue is added to consensus
								consensus+=column.getFirst();
								//count the nb of matches
								nbMatchs+=nbResCons;
							}else {
								countEmptyCol++;
								consensus+="-";
							}
					}
					//otherwise an X is added to consensus 
					else {
						consensus+="X";
						//if(alignment.size()>2) nbMatchs+=1;
						//else nbMatchs+=0.25;
						/**test, � remettre en place code au desuss si marche pas  **/
						nbMatchs+=1;
					}
					
				}
			}catch(java.lang.StringIndexOutOfBoundsException ex) {
				//System.out.println(alignment.toString());
				//System.out.println("msa mode: "+Parameters.msaMode);
				ex.printStackTrace();
				System.exit(1);
			}
			//System.out.println("nbMatchs "+nbMatchs+" total "+alignment.size()*(alignment.get(0).getSequence().length()-countEmptyCol));
			similarity=nbMatchs/(double)(alignment.size()*(alignment.get(0).getSequence().length()-countEmptyCol));
		}
	}
	
	public void clean () {
		String pattern="";
		LinkedList<Integer> emptyColumns=new LinkedList<Integer>();
		//System.out.println(consensus);
		//System.out.println(alignment);
		try {
			for (int p=0;p<consensus.length();p++) {
				int nbResCons=0;
				char car=consensus.charAt(p);
				if(car=='-') {
					for (int i=0;i<alignment.size();i++) {
						pattern=alignment.get(i).getSequence();					
							if(car==pattern.charAt(p))nbResCons++;
					}
					//in  case of the residue is in the majority, or if there is ex aequo between several residues but in nb >1
					if(nbResCons==alignment.size()) {
						//add this column as an empty one
						emptyColumns.add(p);
					}
				}
			}
			//		 cleaning up the empty columns
			for(int i=emptyColumns.size()-1;i>=0;i--) {
				for(int j=0;j<alignment.size();j++) {
					StringBuffer sb =new StringBuffer(alignment.get(j).getSequence());
					sb.deleteCharAt(emptyColumns.get(i));
					alignment.get(j).setSequence(sb.toString());
				}
				StringBuffer consb=new StringBuffer(consensus);
				consb.deleteCharAt(emptyColumns.get(i));
				consensus=consb.toString();
			}
		}catch(java.lang.StringIndexOutOfBoundsException ex) {
			ex.printStackTrace();
			System.exit(1);
		}
	}
	
	
	public AlignCopies getAlignment() {
		return alignment;
	}
	public void setAlignment(AlignCopies alignment) {
		this.alignment = alignment;
	}
	public String getConsensus() {
		return consensus;
	}
	public void setConsensus(String consensus) {
		this.consensus = consensus;
	}
	public double getSimilarity() {
		return similarity;
	}
	public void setSimilarity(double similarity) {
		this.similarity = similarity;
	}
}
