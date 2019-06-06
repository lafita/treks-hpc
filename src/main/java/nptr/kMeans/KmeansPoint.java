package nptr.kMeans;

import nptr.Sstring;

/*
 * Represents an abstraction for a data point in two dimensional space
 *
 * Manas Somaiya
 * Computer and Information Science and Engineering
 * University of Florida
 *
 * Created: October 29, 2003
 * Last updated: October 30, 2003
 *
 */
 
 
 
/**
 * Represents an abstraction for a data point in two dimensional space
 * @author   	Manas Somaiya	mhs@cise.ufl.edu
 */ 
public class KmeansPoint {


	/**
	 * Value in dimension x
	 * @uml.property  name="x"
	 */
	private int x;
	private Sstring gr;

	

	/**
	 * Assigned cluster
	 * @uml.property  name="clusterNumber"
	 */
	private int clusterNumber;
	

	/**
	 * Creates a new instance of data point
	 *
	 * @param	_x	value in dimension x
	 */
	public KmeansPoint(int _x) {
	
		this.x = _x;
		this.clusterNumber=0;
	} // end of kMeansPoint()
	
	
	/**
	 * Creates a new instance of data point
	 *
	 * @param	_x	value in dimension x
	 */
	public KmeansPoint(Sstring g) {
	
		this.gr = g;
		this.x=gr.getDist();
		this.clusterNumber=0;
	} // end o
	
	
	/**
	 * Assigns the data point to a cluster
	 *
	 * @param	_clusterNumber	the cluster to which this data point is to be assigned
	 */
	public void assignToCluster(int _clusterNumber) {
	
		this.clusterNumber = _clusterNumber;
	
	} // end of assignToCluster()
	
	
	/**
	 * Returns the cluster to which the data point belongs
	 * @return  	the cluster number to which the data point belongs
	 * @uml.property  name="clusterNumber"
	 */
	public int getClusterNumber() {
	
		return this.clusterNumber;
	
	} // end of getClusterNumber()
	
	
	/**
	 * Returns the value of data point in x dimension
	 * @return  	the value in x dimension
	 * @uml.property  name="x"
	 */
	public int getX() {
	
		return this.x;
	
	} // end of getX()
	
	
	
	
	
	/**
	 * Returns the distance between two data points
	 *
	 * @param	dp1 	the first data point
	 * @param	dp2 	the second data point
	 * @return	the distance between the two data points
	 */
	public static double distance(KmeansPoint dp1, KmeansPoint dp2) {
	
		double result = 0;
		result = Math.abs(dp1.getX() - dp2.getX());
		return result;
	
	} // end of distance()
	
	
	/**
	 * Returns a string representation of this kMeansPoint
	 *
	 * @return	a string representation of this data point
	 */
	public String toString(){
	
		return "distance: (" + this.x + ")[" + this.clusterNumber + "]";
	
	} // end of toString()


	/**
	 * @return
	 * @uml.property  name="gr"
	 */
	public Sstring getGr() {
		return gr;
	}


	/**
	 * @param gr
	 * @uml.property  name="gr"
	 */
	public void setGr(Sstring gr) {
		this.gr = gr;
	}



} // end of class

