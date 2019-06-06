package nptr.hclustering.divisive;

import java.util.ArrayList;
import java.util.LinkedList;

import nptr.hclustering.HCluster;
import nptr.Parameters;
public class DivisiveHClustering {

	private LinkedList<HCluster> hclusters;
	private int threshold;
	
	public DivisiveHClustering () {
		 hclusters=new LinkedList<HCluster>();
		 //add the first global cluster
		 hclusters.add(new HCluster());
		 threshold=5;
	}
	
	public void addPoint(int point) {
		HCluster mainCluster=hclusters.get(0);
		mainCluster.addPoint(point);
	}
	
	public void start() {
		int maxDist=-999999;
		HCluster clusterToSplit=new HCluster();
		Pair maxPair=new Pair();
		for(HCluster hc:hclusters) {
			ArrayList<Integer> datapoints=hc.getDataPoints();
			for(int i=0;i<datapoints.size();i++) {
				for(int j=0;j<datapoints.size();j++) {
					int dist=Math.abs(datapoints.get(i)-datapoints.get(j));
					if(dist>maxDist) {
						maxPair=new Pair(datapoints.get(i),datapoints.get(j));
						clusterToSplit=hc;
						maxDist=dist;
					}
				}
			}			
		}
//		divide the current cluster hc in two clusters based on maxPair
		if(maxDist>threshold && hclusters.size()<Parameters.nbCluster) {
			ArrayList<Integer> datapoints=clusterToSplit.getDataPoints();
			hclusters.remove(clusterToSplit);
			HCluster hc1=new HCluster(maxPair.getPoint1());
			HCluster hc2=new HCluster(maxPair.getPoint2());
			for(int i=0;i<datapoints.size();i++) {
				if(hc1.getCentroid()==datapoints.get(i)) {
					hc1.addPoint(datapoints.get(i));
				}else if(hc2.getCentroid()==datapoints.get(i)) {
					hc2.addPoint(datapoints.get(i));
				}else {
					if(Math.abs(hc2.getCentroid()-datapoints.get(i))>Math.abs(hc1.getCentroid()-datapoints.get(i))) {
						hc2.addPoint(datapoints.get(i));
					}else hc1.addPoint(datapoints.get(i));
				}
			}
			hc1.calculateCentroid();
			hc2.calculateCentroid();
			hclusters.add(hc1);
			hclusters.add(hc2);
			//recursive call
			this.start();
		}
	}
	
	@Override
	public String toString() {
		String ret="Nb of clusters "+hclusters.size()+"\n";
		for(HCluster hc:hclusters) {
			ret+=hc.getCentroid()+"\n";
		}
		return ret;
	}
	public ArrayList<Integer> getHClusters() {
		ArrayList<Integer> clusters=new ArrayList<Integer>();
		for(HCluster hc:hclusters) {
			clusters.add(hc.getCentroid());
		}
		return clusters;
	}
}
