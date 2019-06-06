package nptr;
import java.util.LinkedList;


public class FastaSplitter extends DataToSequence{

	private SeqRepeat fastaToSplit;
	public static int lengthToCut=1500;
	
	public FastaSplitter(SeqRepeat fileF) {
		super();
		fastaToSplit=fileF;
	}
	
	
	public LinkedList<SeqRepeat> split() {
		
			LinkedList<SeqRepeat> splitted=new LinkedList<SeqRepeat>();
			double nbOfCuts=Math.floor(fastaToSplit.size()/lengthToCut);
			
			for (int c=0;c<nbOfCuts;c++) {
				SeqRepeat tempSplit=new SeqRepeat();
				tempSplit.setDesc(fastaToSplit.getDesc());
				tempSplit.setGi(fastaToSplit.getGi());
				tempSplit.setDB(fastaToSplit.getDB());
				if(c<nbOfCuts-1) {
					tempSplit.setSequence(fastaToSplit.getSequence().substring(c*lengthToCut,(c+1)*lengthToCut));
					splitted.add(tempSplit);
				}else {
					int endCut=fastaToSplit.size();
					
					tempSplit.setSequence(fastaToSplit.getSequence().substring(c*lengthToCut,endCut));
					splitted.add(tempSplit);
				}
			}
			return splitted;
	}
}
