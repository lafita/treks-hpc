package nptr.hclustering;

import java.util.ArrayList;

public class HCluster {
	
	public int centroid;
	public ArrayList<Integer> dataPoints;

	
	public HCluster() {
		centroid=0;
		dataPoints=new ArrayList<Integer>();
	}
	public HCluster(int firstPoint) {
		centroid=firstPoint;
		dataPoints=new ArrayList<Integer>();
		dataPoints.add(firstPoint);
	}
	
	
	
	/**
	 * Calculate the centroid of the current cluster
	 * @return the new centroid
	 */
	public int calculateCentroid () {
		int total=0;
		for(int point:dataPoints) {
			total+=point;
		}
		centroid=Math.round(total/dataPoints.size());
		return centroid;
	}
		
	/* Getters / Setters */
	public int getCentroid() {
		return centroid;
	}

	public void setCentroid(int centroid) {
		this.centroid = centroid;
	}

	public ArrayList<Integer> getDataPoints() {
		return dataPoints;
	}
	
	public void addPoint(int p) {
		this.dataPoints.add(p);
	}
	
	public void setDataPoints(ArrayList<Integer> dataPoints) {
		this.dataPoints = dataPoints;
	}
	public void addDataPoints(ArrayList<Integer> dataPoints) {
		this.dataPoints.addAll(dataPoints);
	}
	
	
}
