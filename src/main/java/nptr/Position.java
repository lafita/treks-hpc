package nptr;

import nptr.utils.Hash;

/**
 * @author  julien
 */
public class Position {
	/**
	 * @uml.property  name="pos"
	 */
	private int pos;
	/**
	 * @uml.property  name="dist"
	 */
	private int dist;
	/**
	 * @uml.property  name="cluster"
	 */
	private int cluster;
	/**
	 * @uml.property  name="graine"
	 */
	private String graine;
	
	public Position(int p) {
		this.pos=p;
		this.dist=0;
	}
	
	/**
	 * @return
	 * @uml.property  name="cluster"
	 */
	public int getCluster() {
		return cluster;
	}
	/**
	 * @param cluster
	 * @uml.property  name="cluster"
	 */
	public void setCluster(int cluster) {
		this.cluster = cluster;
	}
	/**
	 * @return
	 * @uml.property  name="dist"
	 */
	public int getDist() {
		return dist;
	}
	/**
	 * @param dist
	 * @uml.property  name="dist"
	 */
	public void setDist(int dist) {
		this.dist = dist;
	}
	public int calcDist(Position pi) {
		this.dist=Math.abs(pi.getPos()-this.pos);
		return dist;
	}
	/**
	 * @return
	 * @uml.property  name="pos"
	 */
	public int getPos() {
		return pos;
	}
	/**
	 * @param pos
	 * @uml.property  name="pos"
	 */
	public void setPos(int pos) {
		this.pos = pos;
	}


	public boolean nearDist(int ref) {
		if(dist>0) return nearDist(this.dist,ref);
		else return false;
	}
	public static boolean nearDist (int p, int ref) {
		Hash paramIndels=Parameters.getParams();
		Object[] paramKeys =paramIndels.keySet().toArray();
		int i=0;
		/** Détection  des paramètres  **/
		//param par défaut
		double simpercent=Double.parseDouble(paramIndels.get("Default").toString());
		//param utilisateur
		boolean isParam=false;
		while(i<paramKeys.length && !isParam) {
			if(!paramKeys[i].equals("Default")) {
				if(ref==Integer.parseInt(paramKeys[i].toString())) {
					simpercent=Double.parseDouble(paramIndels.get(paramKeys[i]).toString());
					isParam=true;
				}
			}
			i++;
		}
		simpercent/=100;
		if(p>=Math.round(ref*((double)(1-simpercent))) && p<=Math.round(ref*((double)1+simpercent))) return true;
		else {
			return false;
		}
	}
	

	/**
	 * @return
	 * @uml.property  name="graine"
	 */
	public String getGraine() {
		return graine;
	}

	/**
	 * @param graine
	 * @uml.property  name="graine"
	 */
	public void setGraine(String graine) {
		this.graine = graine;
	}
	public String toString() {
		return "Sstring "+this.graine+" at position "+this.pos+" dist:"+this.dist;
	}
}
