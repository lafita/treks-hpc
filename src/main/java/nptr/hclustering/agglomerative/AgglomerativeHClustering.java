package nptr.hclustering.agglomerative;

import java.util.ArrayList;
import java.util.LinkedList;

import nptr.hclustering.HCluster;

public class AgglomerativeHClustering {

	private LinkedList<HCluster> hclusters;
	private int threshold;
	
	public AgglomerativeHClustering() {
		 hclusters=new LinkedList<HCluster>();
		 threshold=5;
	}
	public void addPoint(int point) {
		boolean doublon=false;
		for(HCluster hc:hclusters) {
			//if this point already exist we add it to the existing cluster
			if(hc.getCentroid()==point) {
				doublon=true;
				hc.addPoint(point);
				break;
			}
		}
		if(!doublon)hclusters.add(new HCluster(point));
	}
	
		
	public void start() {
		Pairs clusterPairs=new Pairs();
		int minDist=-99999;
		Pair minPair=new Pair();
		boolean first=true;
		if(hclusters.size()>1) {
			for(HCluster hc:hclusters) {
				for(HCluster hc2:hclusters) {
					Pair nPair=new Pair(hc,hc2);
					if(!hc.equals(hc2) && !clusterPairs.contains(nPair)) {
						//distance calculcation between clusters pairs 
						nPair.calculate();
						int dist=nPair.getDistance();
						//determination of the pair with shortest distance
						if(dist<minDist || first) {
							minPair=nPair;
							minDist=dist;
							first=false;
						}
						clusterPairs.add(nPair);
					}
					
				}
			}
			if(minDist<=threshold) {
				// merging the best pair in one cluster
				HCluster oneCluster=minPair.mergeCluster();
				
				// delete the best pair from the list
				clusterPairs.remove(minPair);
				hclusters.remove(minPair.getCluster1());
				hclusters.remove(minPair.getCluster2());
				
				//replace the clusters by the new one
				for(Pair np:clusterPairs) {
					if(np.contains(minPair.getCluster1())) {
						np.replaceCluster(minPair.getCluster1(),oneCluster);
					}else if(np.contains(minPair.getCluster2())) {
						np.replaceCluster(minPair.getCluster2(),oneCluster);
					}
				}
				//update list of clusters
				hclusters.add(oneCluster);
				//recursive call
				//System.out.println("new Cluster - centroid: "+oneCluster.getCentroid()+ " distance: "+minDist);
				if(hclusters.size()>10) this.start();
			}
		}
	}
	
	public ArrayList<Integer> getHClusters() {
		ArrayList<Integer> clusters=new ArrayList<Integer>();
		for(HCluster hc:hclusters) {
			clusters.add(hc.getCentroid());
		}
		return clusters;
	}
	
	public static void main (String[] args) {
		AgglomerativeHClustering hc=new AgglomerativeHClustering();
		hc.addPoint(16);
		hc.addPoint(16);
		hc.addPoint(2);
		hc.addPoint(15);
		hc.addPoint(16);
		hc.start();
		
	}
	@Override
	public String toString() {
		String ret="Nb of clusters "+hclusters.size()+"\n";
		for(HCluster hc:hclusters) {
			ret+=hc.getCentroid()+"\n";
		}
		return ret;
	}
}
