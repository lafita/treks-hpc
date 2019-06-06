package nptr;

import java.util.LinkedList;

public class TRExplorer {
	
	private SeqRepeat seqRep;
	private OverlapManager aCopies;
	
	public TRExplorer (SeqRepeat seq) {
		seqRep=seq;
		aCopies=new OverlapManager();
	}
	
	public void run() {
		
		//splitting the sequence if it is too long
		if(seqRep.size()>FastaSplitter.lengthToCut) {
			FastaSplitter fsplitter=new FastaSplitter(seqRep);
			LinkedList<SeqRepeat> seqParts=fsplitter.split();
			OverlapManager aPartCopies=new OverlapManager();
			int count=0;
			for(SeqRepeat seqPart:seqParts) {
				seqPart.scanSeed();
				seqPart.buildFromSeeds();
				aPartCopies=seqPart.getPatterns();
				aPartCopies.setActivated(Parameters.overlapActivated);
				aPartCopies.deleteOverlaps();
				for(Repeat repPart:aPartCopies) {
					repPart.setBeginPosition(repPart.getBeginPosition()+(FastaSplitter.lengthToCut*count));
					repPart.setEndPosition(repPart.getEndPosition()+(FastaSplitter.lengthToCut*count));
					repPart.setSeqLength(seqRep.size());
				}
				aCopies.addAll(aPartCopies);
				count++;
			}
		} else { //if the sequence has an acceptable length
			
			seqRep.scanSeed();
			seqRep.buildFromSeeds();
			aCopies=seqRep.getPatterns();
			aCopies.setActivated(Parameters.overlapActivated);
			aCopies.deleteOverlaps();
			
		}
		
		seqRep.clear();
		
	}
	
	public OverlapManager getACopies() {
		return aCopies;
	}

	public void setACopies(OverlapManager copies) {
		aCopies = copies;
	}

}
