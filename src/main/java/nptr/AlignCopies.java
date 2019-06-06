package nptr;

import java.util.LinkedList;

@SuppressWarnings("serial")
public class AlignCopies extends LinkedList<Copy>{
	
	private int regionLength;
	
	public AlignCopies() {
		super();
		this.regionLength=0;
	}
	
	public int length() {
		if( this.size()>0) {
			return this.size()*this.get(0).getSequence().length();
		}else return 0;
	}
	
	/**
	 * controls if total length is ok
	 * 
	 * @return boolean
	 */
	public boolean hasValidLength() {
			return this.regionLength>=Parameters.totalLength;
	}
	
	/** cast the alignment of copies in a Linkedlist of Strings
	 * 
	 * @return linkedlist
	 */
	public LinkedList<String> toLString() {
		LinkedList<String> lString=new LinkedList<String>();
		for(int i=0;i<this.size();i++) {
			lString.add(this.get(i).getSequence());
		}
		return lString;
	}
	
	
	
	@Override
	public String toString() {
		String ret="";
		for(int i=0;i<this.size();i++) {
			ret+=this.get(i).toString()+"\n";
		}
		ret+="######## region Length="+regionLength+" ########";
		return ret;
	}

	public int getRegionLength() {
		return regionLength;
	}

	public void setRegionLength(int regionLength) {
		this.regionLength = regionLength;
	}

	public void removeFromRegion(Copy cop) {
		this.regionLength-=cop.getLength();		
	}
	
	public Copy removeFirst() {
		this.regionLength-=this.getFirst().getLength();	
		return this.remove(0);
	}
	public Copy removeLast() {
		this.regionLength-=this.getLast().getLength();	
		return this.remove(this.size()-1);
	}
	@Override
	public boolean add(Copy cop) {
		boolean bool=super.add(cop);
		this.regionLength+=cop.getLength();
		//System.out.println("copy "+cop.toString()+" added to alignCopies , region length is equal to "+this.regionLength);
		return bool;
	}
	public AlignCopies[] split () {
		AlignCopies left=new AlignCopies();
		AlignCopies right=new AlignCopies();
		//int middle=this.get(0).getSequence().length()/2;
		for (int c=0;c<this.size();c++) {
			/* TODO */
		}
		AlignCopies[]splitted =new AlignCopies[2];
		splitted[0]=left;
		splitted[1]=right;
		return splitted;
	}

	
}
