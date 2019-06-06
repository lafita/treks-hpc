package nptr;

public class Copy {
	/**
	 * @author      julien
	 */
	
	private String sequence;
	private int beginPosition, endPosition;
	private int length;
	
	public Copy() {
		this.sequence="";
		this.beginPosition=0;
		this.endPosition=0;
	}
	
	public Copy(String sequence,int beginPosition, int endPosition) {
		this.sequence=sequence;
		this.beginPosition=beginPosition;
		this.endPosition=endPosition;
		this.length=endPosition-beginPosition+1;
	}
	public Copy(String sequence) {
		this.sequence=sequence;
		this.beginPosition=0;
		this.endPosition=0;
		this.length=0;
	}

	public int getBeginPosition() {
		return beginPosition;
	}

	public void setBeginPosition(int beginPosition) {
		this.beginPosition = beginPosition;
	}

	public int getEndPosition() {
		return endPosition;
	}

	public void setEndPosition(int endPosition) {
		this.endPosition = endPosition;
	}

	public String getSequence() {
		return sequence;
	}

	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	public String clean() {
		return sequence.replace("-","");
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}
	
	public String toString() {
		return this.sequence+" -length: "+this.length;
	}
}
