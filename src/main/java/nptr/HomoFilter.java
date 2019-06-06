package nptr;

import java.util.LinkedList;
/**
 * HomoFilter permits to detect all homorepeats (i.e repeats of one single residue) and 
 * add them as result. The regions associated won't be considered in the SString generation.
 *  
 * @author julien
 *
 */
public class HomoFilter {
	private LinkedList<Repeat> homoRepeats;
	private SeqRepeat sequence;
	private int totalLength;
	
	public HomoFilter() {
		this.homoRepeats=new LinkedList<Repeat>();
		this.totalLength=4*Parameters.seedLength+1;
	}
	public HomoFilter(SeqRepeat seq) {
		this.sequence=seq;
		this.homoRepeats=new LinkedList<Repeat>();
		this.totalLength=4*Parameters.seedLength+1;
	}
	/**
	 * Detect all the homorepeats in the sequence with a length >= total length 
	 * defined in Parameters, and add them to the results 
	 *
	 */
	public void searchHomoRepeats() {
		String seq=sequence.getSequence();
		
		for (int i=0;i<seq.length()-(totalLength-1);i++) {
			boolean isHomo=true;
			int res=i;
			res++;
			while(res<seq.length() && isHomo) {
				if (seq.charAt(res)!=seq.charAt(i)) {
					isHomo=false;
				}else {
					res++;
				}
			}
			if(res-i>=totalLength) {
				//System.out.println("res "+res+" i "+i);
				/**System.out.println(seq.substring(i,res));**/
				//Consider it's a homorepeat
				AlignCopies homoCopies=new AlignCopies();
				for (int h=i;h<res;h++) {
					Copy oneRes=new Copy(seq.substring(h,h+1).toUpperCase(),h,h);
					homoCopies.add(oneRes);
				}
				RepeatBuilder homoPattern=new RepeatBuilder(1);
				homoPattern.setSequence(sequence);
				homoPattern.appendResult(homoCopies,1.0,new String(""+seq.charAt(i)).toUpperCase());
				sequence.addTandemRepeat(homoPattern);
				//instanciate a repeat to define the regions to skip in short string generation
				Repeat hRep=new Repeat(1,1,homoCopies);
				homoRepeats.add(hRep);
			}
			i=res-1;
		}
	}
	/**
	 * Check if a position is located inside a homorepeat region
	 * @param posi the position to verify
	 * @return boolean
	 */
	public boolean isInHomo (int posi) {
		for (Repeat hr:homoRepeats) {
			if(posi>=hr.getBeginPosition()&&posi<=hr.getEndPosition()) return true;
		}		
		return false;
	}
}
