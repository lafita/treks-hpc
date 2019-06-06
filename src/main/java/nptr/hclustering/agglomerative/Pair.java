package nptr.hclustering.agglomerative;

import nptr.hclustering.HCluster;

public class Pair {
	private HCluster cluster1;
	private HCluster cluster2;
	private int distance;
	
	public Pair() {
		cluster1=null;
		cluster2=null;
	}
	public Pair(HCluster c1, HCluster c2) {
		cluster1=c1;
		cluster2=c2;
	}


	public void calculate() {
		/*for(int point1:cluster1.getDataPoints()) {
			for(int point2:cluster2.getDataPoints()) {
				distance+=Math.abs(point2-point1);
			}
		}*/
		distance=Math.abs(cluster1.getCentroid()-cluster2.getCentroid());
	}
	
	public int getDistance() {
		return distance;
	}


	public void setDistance(int distance) {
		this.distance = distance;
	}
	
	/**
	 * Merges two cluster in one  in case of agglomerative hierarchical clustering
	 * @return the new One cluster with its new parameters
	 */
	public HCluster mergeCluster() {
		HCluster oneHC=new HCluster();
		oneHC.addDataPoints(this.cluster1.getDataPoints());
		oneHC.addDataPoints(this.cluster2.getDataPoints());
		oneHC.calculateCentroid();
		return oneHC;
	}
	/** replace clusters in case of agglomerative hierarchical clustering */
	
	public void replaceCluster(HCluster oldC,HCluster newC) {
		if(oldC.equals(this.cluster1)) this.cluster1=newC;
		else if(oldC.equals(this.cluster2)) this.cluster2=newC;
	}
	
	
	
	/**
	 * test if pair is equal to current pair
	 * @param hp
	 * @return if pair is equal to current pair
	 */
	@Override
	public boolean equals(Object hp) {
		Pair p=(Pair)hp;
		if((p.getCluster1().equals(cluster1)&&p.getCluster2().equals(cluster2))||(p.getCluster1().equals(cluster2)&&p.getCluster2().equals(cluster1)) ) 	return true;
		else return false;
	}

	
	public boolean contains(HCluster c) {
		if(c.equals(cluster1)||c.equals(cluster2)) return true;
		else return false;
	}
	
	
	public HCluster getCluster1() {
		return cluster1;
	}


	public void setCluster1(HCluster cluster1) {
		this.cluster1 = cluster1;
	}


	public HCluster getCluster2() {
		return cluster2;
	}


	public void setCluster2(HCluster cluster2) {
		this.cluster2 = cluster2;
	}


	
}
