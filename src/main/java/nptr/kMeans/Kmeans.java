package nptr.kMeans;

import java.util.ArrayList;
import java.util.Iterator;

import nptr.Sstring;

/**
 * @author          julien
 * @uml.dependency   supplier="nptr.Cluster"
 */
@SuppressWarnings("serial")
public class Kmeans {


	/**
	 * Number of clusters
	 * @uml.property  name="k"
	 */
	private int k;
	

	/**
	 * Array of clusters
	 * @uml.property  name="clusters"
	 * @uml.associationEnd  multiplicity="(0 -1)"
	 */
	private Cluster[] clusters;
	
	
	/** Number of iterations */
	private int nIterations;
	
	
	/** ArrayList of data points
	 * @uml.property  name="kMeansPoints"
	 * 
	 */
	private ArrayList<KmeansPoint> kMeansPoints;
	
	/** random selection of starting points  or not */
	private boolean random=true;
	
	
	/**
	 * Returns a new instance of kMeans algorithm
	 *
	 */
         public Kmeans() {
        	 this.nIterations = 0;
        	 this.kMeansPoints=new ArrayList<KmeansPoint>();
        	 this.random=false;
	}
	
	/**
	 * Returns a new instance of kMeans algorithm
	 *
	 * @param	k		number of clusters
	 */
         public Kmeans(int k) {
	
		this.k = k;
		this.clusters = new Cluster[this.k];
		this.nIterations = 0;
		this.kMeansPoints=new ArrayList<KmeansPoint>();	
	}
	
	
	/**
	 * Returns a new instance of kMeans algorithm
	 *
	 * @param	k		number of clusters
	 * @param	kMeansPoints	List containing objects of type kMeansPoint
	 */
         public Kmeans(int k, ArrayList<KmeansPoint> kMeansPoints) {
	
		this.k = k;
		this.clusters = new Cluster[this.k];
		this.nIterations = 0;
		this.kMeansPoints=kMeansPoints;
	
	} // end of kMeans()
	
	
	public void setCentroids (ArrayList<Integer> points ) {
		this.k=points.size();
		this.clusters=new Cluster[k];
		for (int i=0; i <k; i++){
			this.clusters[i] = new Cluster(i);
			KmeansPoint centroid=new KmeansPoint(points.get(i));
			this.clusters[i].setMean(centroid);
			
		}
	}
	
	public void setRandomCentroids() {
		
//		 Select k points as initial means
		for (int i=0; i < k; i++){
		
			this.clusters[i] = new Cluster(i);
			if(this.kMeansPoints.size()>0)this.clusters[i].setMean((this.kMeansPoints.get((int)(Math.random() * this.kMeansPoints.size()))));
		
		}
	}
	
	
	/**
	 * Runs the k-means algorithm over the data set
	 */
	public void runKMeans() {
	
		if(random) {
			this.setRandomCentroids();
		}
		do {
			// Form k clusters
			Iterator i = this.kMeansPoints.iterator();
			while (i.hasNext())
				this.assignToCluster((KmeansPoint)(i.next()));
				
			this.nIterations++;
		
		}
		// Appliquer jusqu'à ce que les centres ne bougent plus
		while (this.updateMeans());
	
	} // end of runKMeans()
	
	/**
	 * ajoute un Kmeanspoint de valeur p à la liste des points 
	 */
	public void setPoint(int p) {
		KmeansPoint kp=new KmeansPoint(p);
		kMeansPoints.add(kp);
	}
	public void setPoint(Sstring g) {
		/* on ajoute un point pr chaque position ayant une distance égale à la distance clusterisée de la graine */
		for(int i=0;i<g.getPositions().size();i++) {
			if(g.getPositions().get(i).getDist()==g.getDist()) {
				KmeansPoint kp=new KmeansPoint(g);
				kMeansPoints.add(kp);
			}
		}
	}
	
	/**
	 * Assigns a data point to one of the k clusters based on its distance from the means of the clusters
	 *
	 * @param	dp	data point to be assigned
	 */
	private void assignToCluster(KmeansPoint dp) {
	
		int currentCluster = dp.getClusterNumber();
		double minDistance = KmeansPoint.distance(dp, this.clusters[currentCluster].getMean());
		
		for (int i=0; i <this.k; i++)
			if (KmeansPoint.distance(dp, this.clusters[i].getMean()) < minDistance) {
		
				minDistance = KmeansPoint.distance(dp, this.clusters[i].getMean());
				currentCluster = i;
				
			}
		
		dp.assignToCluster(currentCluster);
		this.clusters[currentCluster].addPoint(dp);
	
	} // end of assignToCluster
	
	
	/**
	 * Updates the means of all k clusters, and returns if they have changed or not
	 *
	 * @return	have the updated means of the clusters changed or not
	 */
	private boolean updateMeans() {
	
		boolean reply = false;
		
		int[] x = new int[this.k];
		int[] size = new int[this.k];
		KmeansPoint[] pastMeans = new KmeansPoint[this.k];
		
		for (int i=0; i<this.k; i++) {
		
			x[i] = 0;
			size[i] = 0;
			pastMeans[i] = this.clusters[i].getMean();
			/* on vide les clusters  */
		}
		
		Iterator i = this.kMeansPoints.iterator();
		while (i.hasNext()) {
		
		
			KmeansPoint dp = (KmeansPoint)(i.next());
			int currentCluster = dp.getClusterNumber();
			
			x[currentCluster] += dp.getX();
			size[currentCluster]++;
		
		}
		
		for (int j=0; j < this.k; j++ ) 
			if(size[j] != 0) {
			
				x[j] /= size[j];
				KmeansPoint temp = new KmeansPoint(x[j]);
				temp.assignToCluster(j);
				this.clusters[j].setMean(temp);
				if (KmeansPoint.distance(pastMeans[j], this.clusters[j].getMean()) !=0 )
					reply = true;
					
			}
		if(reply) {
			for (int m=0; m<this.k; m++) {
				this.clusters[m].empty();
			
			}
		}
		
		return reply;
		
	} // end of updateMeans()


	/**
	 * Returns the value of k
	 * @return  	the value of k
	 * @uml.property  name="k"
	 */
	public int getK() {
	
		return this.k;
	
	} // end of getK()
	
	
	/**
	 * Returns the specified cluster by index
	 *
	 * @param	index	index of the cluster to be returned
	 * @return	return the specified cluster by index
	 */
	public Cluster getCluster(int index) {
	
		return this.clusters[index];
	
	} // end of getCluster() 
        
	/**
	 * Returns the string output of the data points
	 *
	 * @return  the string output of the data points
	 */
	public String toString(){
            
		return this.kMeansPoints.toString();
            
	} // end of toString()
        
        
	/**
	 * Returns the data points
	 *
	 * @return  the data points
	 */
	public ArrayList<KmeansPoint> getDataPoints() {
            
		return this.kMeansPoints ;
            
	} // end of getDataPoints()




	
}
