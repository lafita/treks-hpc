package nptr;

public class HammingDistance {
	private String s1;
	private String s2;
	private int dist;
	
	public HammingDistance(String seq1,String seq2) {
		s1=seq1;
		s2=seq2;
		dist=0;
	}
	
	public int getDistance() {
		try {
			for(int i=0;i<s1.length();i++) {
				if(s1.charAt(i)!=s2.charAt(i)){
					dist++;
				}
			}
			
		}catch (Exception ex) {
			System.exit(1);
		}
		return dist;
	}
}
