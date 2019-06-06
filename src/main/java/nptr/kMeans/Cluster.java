package nptr.kMeans;

import java.util.ArrayList;

import nptr.Sstring;
import nptr.utils.Hash;



/**
 * @author   julien
 */
public class Cluster {


	/**
	 * Cluster Number
	 * @uml.property  name="clusterNumber"
	 */
	private int clusterNumber;
	private Hash  points;
	/**
	 * @uml.property  name="pointSeeds"
	 */
	private ArrayList<Sstring> pointSeeds;
	
	
	/** Mean data point of this cluster */
	private KmeansPoint mean;
	
	
	/**
	 * Returns a new instance of cluster
	 *
	 * @param	_clusterNumber	the cluster number of this cluster
	 */
	public Cluster(int _clusterNumber) {
	
		this.clusterNumber = _clusterNumber;
		this.points=new Hash();
		this.pointSeeds=new ArrayList<Sstring>();
		
	} // end of cluster()
	
	
	/**
	 * Sets the mean data point of this cluster
	 * @param meanDataPoint  	the new mean data point for this cluster
	 * @uml.property  name="mean"
	 */
	public void setMean(KmeansPoint meanDataPoint) {
	
		this.mean = meanDataPoint;
	
	} // end of setMean()
	
	
	public void addPoint(KmeansPoint kp) {
		if(points.containsKey(kp.getX())) points.put((Integer)kp.getX(),(Integer)this.points.get(kp.getX())+1);
		else points.put((Integer)kp.getX(),1);
		if(!pointSeeds.contains(kp.getGr())) pointSeeds.add(kp.getGr());
	}
	/**
	 * @return
	 * @uml.property  name="points"
	 */
	public Hash getPoints () {
		return this.points;
	}
	
	public void empty() {
		this.points.clear();
	}
	
	/**
	 * Returns the mean data point of this cluster
	 * @return  	the mean data point of this cluster
	 * @uml.property  name="mean"
	 */
	public KmeansPoint getMean() {
	
		return this.mean;
	
	} // end of getMean()
	
	
	/**
	 * Returns the cluster number of this cluster
	 * @return  	the cluster number of this cluster
	 * @uml.property  name="clusterNumber"
	 */
	public int getClusterNumber() {
	
		return this.clusterNumber;
	
	} // end of getClusterNumber()


	/**
	 * @return
	 * @uml.property  name="pointSeeds"
	 */
	public ArrayList<Sstring> getPointSeeds() {
		return pointSeeds;
	}


	/**
	 * @param pointSeeds
	 * @uml.property  name="pointSeeds"
	 */
	public void setPointSeeds(ArrayList<Sstring> pointSeeds) {
		this.pointSeeds = pointSeeds;
	}
	

	

} // end of class
