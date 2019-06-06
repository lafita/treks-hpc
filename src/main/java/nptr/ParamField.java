package nptr;

/**
 * @author julien
 */
public class ParamField {

	private String tLength;
	private String tPercent;

	public ParamField() {
		this("", "");
	}

	public ParamField(String l, String p) {

		tLength = l;
		tPercent = p;

	}

	public void setLength(int l) {
		tLength = String.valueOf(l);
	}

	public String getLength() {
		return tLength;
	}

	public void setPercent(int p) {
		tPercent = String.valueOf(p);
	}

	public String getPercent() {
		return tPercent;
	}

	public String toString() {
		return getLength() + " res. - " + getPercent() + "% ";
	}

}
