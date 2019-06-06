package nptr;

import java.util.ArrayList;
/**
 * Scans the sequence and generates a SString library indexing all positions
 * of the occurences for a given SString.
 * @author julien
 *
 */
public class SequenceSeeder {
	private SeqRepeat sequence;
	private ArrayList <Sstring> seeds;
	private HomoFilter hFilter;
	private int frameLength;
	
	
	public SequenceSeeder() {
		this.sequence=new SeqRepeat();
		seeds=new ArrayList <Sstring>();
		hFilter=new HomoFilter();
	}
	public SequenceSeeder(SeqRepeat sequence) {
		this.sequence=sequence;
		seeds=new ArrayList <Sstring>();
		
	}
	/**
	 * Preliminary step is to locate eventual homorepeats, save them as a result
	 * and skip the associated regions in SString library generation
	 *
	 */
	public void scanSequence(int frameLength) {
		this.frameLength=frameLength;
		//search for homorepeats
		hFilter=new HomoFilter(sequence);
		hFilter.searchHomoRepeats();
		
		
		
		StringBuffer seq=new StringBuffer(sequence.getSequence());
		// generate substrings of length = seedLengh 
		int countCompress=0;
		for(int fram=0;fram<seq.length()-(this.frameLength-1);fram++) {
					
			//skip the homorepeats that could have been found
			if(!hFilter.isInHomo(fram) && !hFilter.isInHomo(fram+this.frameLength)) {
				String graine=seq.substring(fram,fram+this.frameLength);
				this.addSeed(graine,fram);
			}
		}
		int countTemp=0;
		for(int i=0;i<sequence.getLengthList().length-1;i++) {
		//	System.out.println(i+" "+sequence.getLengthList()[i]);
			if(Position.nearDist(sequence.getLengthList()[i+1],sequence.getLengthList()[i]) && sequence.getLengthList()[i+1]!=0) {
				countTemp++;
			}else {
				if(countTemp>=2) {
					countCompress+=countTemp;
				}
				countTemp=0;
			}			
		}
		sequence.setCompress(countCompress);
	}
	/** add an occurence of a short string to the library
	 *@param pat: occurence
	 *@param pos: position of the occurence
	**/
	public void addSeed(String pat,int pos) {
		boolean contain=false;
		int iGraine=seeds.size();
		for (int i=0;i<seeds.size();i++) {
			if(seeds.get(i).getName().equals(pat)) {
				contain=true;
				iGraine=i;
			}
		}
		if(contain) {
			if(seeds.get(iGraine).getPositions().getLast().getPos()+1<pos) {
				if(this.frameLength==1) {
					sequence.getLengthListOneRes()[seeds.get(iGraine).getPositions().getLast().getPos()]=pos-seeds.get(iGraine).getPositions().getLast().getPos();
				}else {
					sequence.getLengthList()[seeds.get(iGraine).getPositions().getLast().getPos()]=pos-seeds.get(iGraine).getPositions().getLast().getPos();
					sequence.getLengthList()[pos]=0;
				}
				
				seeds.get(iGraine).addPosition(pos);				
			}
				
		}else {
			Sstring gr = new Sstring(pat);
			gr.addPosition(pos);
			seeds.add(gr);
			//System.out.println(sequence.getLengthListOneRes().length+" pos "+pos);
			if(this.frameLength==1) {
				sequence.getLengthListOneRes()[pos]=0;
			}else sequence.getLengthList()[pos]=0;
		}
	}
	public ArrayList<Sstring> getSeeds() {
		return seeds;
	}
	public void setSeeds(ArrayList<Sstring> seeds) {
		this.seeds = seeds;
	}
	public SeqRepeat getSequence() {
		return sequence;
	}
	public void setSequence(SeqRepeat sequence) {
		this.sequence = sequence;
	}
	public String toString() {
		String ret="";
		for (Sstring seed:seeds) {
			ret+=seed.toString()+"\n";
		}
		return ret;
	}
}
