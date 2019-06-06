package nptr.hclustering.divisive;


public class Pair {
	private int point1;
	private int point2;
	private int distance;
	
	public Pair() {
		point1=0;
		point2=0;
	}
	public Pair(Integer p1,int p2) {
		point1=p1;
		point2=p2;
	}


	public void calculate() {
		/*for(int point1:cluster1.getDataPoints()) {
			for(int point2:cluster2.getDataPoints()) {
				distance+=Math.abs(point2-point1);
			}
		}*/
		distance=Math.abs(point1-point2);
	}
	
	public int getDistance() {
		return distance;
	}


	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	
	
	
	/**
	 * test if pair is equal to current pair
	 * @param hp
	 * @return if pair is equal to current pair
	 */
	@Override
	public boolean equals(Object hp) {
		Pair p=(Pair)hp;
		if(this.contains(p.getPoint1())&& this.contains(p.getPoint2())) 	return true;
		else return false;
	}

	
	public boolean contains(int p) {
		if(p==point1||p==point2) return true;
		else return false;
	}
	
	
	public int getPoint1() {
		return point1;
	}


	public void setpoint1(int p1) {
		this.point1 = p1;
	}


	public int getPoint2() {
		return point2;
	}


	public void setpoint2(int p2) {
		this.point2 = p2;
	}


	
}
