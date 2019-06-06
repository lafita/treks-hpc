package nptr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;

import nptr.database.ObjectToDB;
import nptr.hclustering.divisive.DivisiveHClustering;
import nptr.kMeans.Cluster;
import nptr.kMeans.Kmeans;
import nptr.utils.Hash;

/**
 * @author      julien
 * @uml.dependency   supplier="nptr.Seq"
 */
public class SeqRepeat extends Seq implements ObjectToDB{

	
	/**
	 * @uml.property  name="refineSeeds"
	 */
	private ArrayList <Sstring> refineSeeds;
	
	private OverlapManager patterns;
	private int compress;
	
	public int getCompress() {
		return compress;
	}
	public void setCompress(int compress) {
		this.compress = compress;
	}
	public SeqRepeat() {
		super();
		refineSeeds=new ArrayList<Sstring>();
		patterns=new OverlapManager();
	}
	public SeqRepeat(String desc,String seq) {
		super(desc,seq);
		refineSeeds=new ArrayList<Sstring>();
		patterns=new OverlapManager();
		
	}
	
	/**
	 * Contient deux phases dans la détermination de la distance du motif
	 * -  phase de clusterisation des distances sur chaque graine
	 * - phase  de clusterisation afin de filtrer les graines internes au motif parasitant 
	 * la détermination de la distance ->weeding
	 *
	 */
	public void scanSeed() {
		SequenceSeeder seeder=new SequenceSeeder(this);
		seeder.scanSequence(Parameters.seedLength);
		ArrayList <Sstring> aSeeds=seeder.getSeeds();
		refineSeeds.clear();
		LinkedList<Position> posGraines;
		
		if(!Parameters.compress || (Parameters.compress &&(this.compress>=(Parameters.totalLength/2)-1 || this.size()<200 ||this.size()>800))) {	
		
			for(int i=0;i<aSeeds.size();i++) {
				Sstring graine=aSeeds.get(i);			
				posGraines=graine.getPositions();
				
				
				//determination of nb & location of starting points for Kmeans by hierarchical clustering
				DivisiveHClustering hc=new DivisiveHClustering();
				for (int p=0;p<posGraines.size()-1;p++) {
					if(posGraines.get(p).getDist()!=0) hc.addPoint(posGraines.get(p).getDist());
				}
				hc.start();
				/* instanciation d'un nouveau Kmeans pr clusteriser les distances*/
				Kmeans kM = new Kmeans();
				kM.setCentroids(hc.getHClusters());
				
				/* on ne prend que les graines ayant plusieurs occurences */
				if(posGraines.size()>1) {
					/* on récupère les distances entre les graines lorsqu'on est en présence de runs*/
					int dist=0;
					for (int p=0;p<posGraines.size()-1;p++) {
						
						if(posGraines.get(p).nearDist(dist) || dist==0) {
							//System.out.println("graine "+graine.getNom()+" dist "+dist+"grainedist "+posGraines.get(p).getDist());
						 	//cas où est c'est la dernière position de la graine et qu'elle est sucseptible de participer à un repeat
						
							kM.setPoint(posGraines.get(p).getDist());
						 	
						}else {
								if((posGraines.get(p+1).getDist()>0 && posGraines.get(p).getDist()%posGraines.get(p+1).getDist()==0) ||(posGraines.get(p).getDist()>0 && posGraines.get(p+1).getDist()%posGraines.get(p).getDist()==0)) {
									kM.setPoint(posGraines.get(p).getDist());
								}else {
								for(int q=p+1;q<posGraines.size()-1;q++) {
									if(posGraines.get(q).getDist()==posGraines.get(p).getDist()) {
										int internDist=0;
										for(int r=p+1;r<q;r++) {
											internDist+=posGraines.get(r).getDist();
										}
										//System.out.println("intern "+internDist);
										if(internDist!=0 && internDist==posGraines.get(p).getDist()) {
											kM.setPoint(posGraines.get(p).getDist());
											kM.setPoint(posGraines.get(q).getDist());
											p=q;
										}
									}
								}
							}
						}
						dist=posGraines.get(p).getDist();
					}
					
					
					kM.runKMeans();
					//on regarde pr chaque cluster K des kmeans le nombre d'occurence de distances de chaque pattern
					for (int j=0;j<kM.getK();j++) {
						Hash kPoints = kM.getCluster(j).getPoints();
												 
						if(kPoints.size()>=1) {
							/* cas où on a plusieurs clusters avec une seule graine mais dont les distances(ou centres sont proches */
							/* dans ce cas, on duplique les points dans tous les clusters */
							/*for(Iterator it = kPoints.entrySet().iterator(); it.hasNext(); ){
								  Map.Entry mapEntry = (Map.Entry)it.next();
								  System.out.println("Key = " + mapEntry.getKey() + " value = " + mapEntry.getValue());
								  if(Integer.parseInt(mapEntry.getValue().toString())==1) {
									  
								  }
							}*/
							
							// on ordonne par ordre décroissant d'occurences 
							kPoints=kPoints.sort(false);
							if((Integer)kPoints.getFirstValue()>=1) {// on supprime les occurences uniques
								//System.out.println("dist clusterisée "+(Integer)kPoints.getFirst());
								// on ne prend que les premiers objets du Hash
								if(kPoints.size()>=1) {
									//si le nombre de points dans ce cluster est > au nombre du précedent
									if((Integer)kPoints.getFirstValue()>graine.getNbRepeat() || (graine.getDist()==0 && graine.getNbRepeat()==0) ) {
										graine.setNbRepeat((Integer)kPoints.getFirstValue());
										
										// on prend, pour un meme nombre de repeats, la distance la plus courte
										ArrayList<Integer> distMini=new ArrayList<Integer>();
										if(kPoints.size()>1) {
											int ind=1;
											distMini.add((Integer)kPoints.getFirst());
											boolean end=true;
											while((Integer)kPoints.getValueAt(ind)==graine.getNbRepeat() && end) {
													distMini.add((Integer)kPoints.getKeyAt(ind));
													ind++;
													if(ind>=kPoints.size()) {
														end=false;
														ind=1;
													}
											}
											Collections.sort(distMini);
											graine.setDist(distMini.get(0));
										}else	graine.setDist((Integer)kPoints.getFirst());
									}else {
										// cas où on ait une plusieurs distance répétées le mm nombre de fois 
										while((Integer)kPoints.getFirstValue()==graine.getNbRepeat() ) {
											if((Integer)kPoints.getFirst()<graine.getDist() && kPoints.size()>0) {
												graine.setDist((Integer)kPoints.getFirst());
												graine.setNbRepeat((Integer)kPoints.getFirstValue());
											}
											kPoints.filter(0,0);
											if(kPoints.size()==0) break;
										}
									}
								}
							}
						}
					}
					if(graine.getDist()>0) refineSeeds.add(graine);
					//System.out.println(graine.getName()+" dist après premier pass "+graine.getDist()+" nb: "+graine.getNbRepeat());
				}
			}
			for(int t=0;t<refineSeeds.size()-1;t++) {
				//System.out.println(refineSeeds.get(t).getName()+" "+refineSeeds.get(t).getDist());
				this.weedPhase(refineSeeds.get(t));
			}
		}
	}
	
	public void weedPhase(Sstring refGraine) {
//		 deuxième passage de Kmeans pour trouver les repeats de plus haut niveau 
			Sstring tempGraine=new Sstring();
			LinkedList<Position> aPos=refGraine.getPositions();
			//on efface toutes les positions de la graine
			//System.out.println(refineSeeds.get(t).getNom()+" dist après refine :"+refineSeeds.get(t).getDist());
			tempGraine.clearPositions();
			for(int pT=0;pT<aPos.size();pT++) {
				Position tempPos=aPos.get(pT);
				// si la position n'a pas une distance proche de la longueur estimée au premier Kmeans , on le vire
					if(!tempPos.nearDist(refGraine.getDist())) {
					// ajout dela position dont la longueur égale à la distance estimée
					tempGraine.addPosition(tempPos.getPos());
				}
			}
			// si on a des positions stockées dans notre linked list position de la graine
			if(tempGraine.getPositions().size()>1) {
				Hash kPoints2=new Hash();
				for (int p=0;p<tempGraine.getPositions().size()-1;p++) {
					int dist2=tempGraine.getPositions().get(p).getDist();
					if(kPoints2.containsKey(dist2)) kPoints2.put(dist2,(Integer)kPoints2.get(dist2)+1);
					else kPoints2.put(dist2,1);
				}
				//System.out.println(refGraine.getName());
				//System.out.println(kPoints2);
				
				if(kPoints2.containsKey(0)) kPoints2.remove(0);
				if(kPoints2.size()>0) {
					// on ordonne par ordre décroissant d'occurences 
					
					kPoints2=kPoints2.sort(false);
					int newNbRepeat=(Integer)kPoints2.getFirstValue();
					//int newDist=(Integer)kPoints2.getFirst();
					if(newNbRepeat>1 && (refGraine.getNbRepeat()<=newNbRepeat )) {// on supprime les occurences uniques
						// on ne prend que les premiers objets du Hash
						kPoints2.filterFirst();
						//on garde la nouvelle distance si elle est différente
						if(refGraine.getDist()!=(Integer)kPoints2.getFirst()) {
							tempGraine.setDist((Integer)kPoints2.getFirst());
							Position lastPos=refGraine.getPositions().getLast();
							refGraine.setPositions(tempGraine.getPositions());
							refGraine.addPosition(lastPos.getPos());
							refGraine.setDist((Integer)kPoints2.getFirst());
							this.weedPhase(refGraine);
						}
					}
				}
			}	
	}
	
	/** scanne toutes les graines générées et détermine si elles 
	 ** sont d'interêt pour générer un nouveau pattern 
	 **/
	public void buildFromSeeds() {
		
//		determination of nb & location of starting points for Kmeans
		DivisiveHClustering hc=new DivisiveHClustering();
		
		for (int i=0;i<refineSeeds.size();i++) {
			Sstring currentGr=refineSeeds.get(i);
			hc.addPoint(currentGr.getDist());
		}
		hc.start();
		Kmeans kM = new Kmeans();
		kM.setCentroids(hc.getHClusters());
		//récupère le nombre de repeats
		int []repeats=new  int [refineSeeds.size()];
		for (int i=0;i<refineSeeds.size();i++) {
			Sstring currentGr=refineSeeds.get(i); 
			kM.setPoint(currentGr);
			repeats[i]=currentGr.getNbRepeat();
			//System.out.println(currentGr.getName()+" dist après Weeding "+currentGr.getDist());
		}
		if(kM.getDataPoints().size()>0) {
			kM.runKMeans();
			for (int j=0;j<kM.getK();j++) {
				Cluster currentCluster=kM.getCluster(j);
				Hash kPoints = currentCluster.getPoints();
				kPoints=kPoints.sort(false);
				
				if(kPoints.size()>0) {
					int longueur =(Integer)kPoints.getFirst();
					int nbRepeat=(Integer)kPoints.getFirstValue();
					if(kPoints.size()>1) {
						
						//System.out.println(kPoints.toString());
							if((Integer)kPoints.getFirstValue()>1) {/* on supprime les occurences uniques */
								int ind=0;
								do {
										
										longueur=(Integer)kPoints.getKeyAt(ind);
										this.ExploreSequenceFromCluster(currentCluster,longueur);
										ind++;
								}while(ind<kPoints.size() && (Integer)kPoints.getValueAt(ind)==nbRepeat );
							}
							
							currentCluster.getPointSeeds().clear();
							currentCluster.empty();
					}else {
						this.ExploreSequenceFromCluster(currentCluster,longueur);
					}
				}
			}
		}
	}
	
	
	public void ExploreSequenceFromCluster(Cluster currentCluster,int length) {
//		longueur =longueur du pattern 
		//System.out.println("longueur "+length);
		/* on récupère les graines qui ont été clusterisées */
		ArrayList<Sstring> currentSeeds=currentCluster.getPointSeeds();
		RepeatBuilder repB=new RepeatBuilder(length);
		for (int s=0;s<currentSeeds.size();s++) {
			//System.out.println(currentSeeds.get(s).getName());
			if(Position.nearDist(currentSeeds.get(s).getDist(),length) ) {
				repB.addGraine(currentSeeds.get(s));
			}
		}
		
		/* on génère toutes les copies en tandem du pattern */
		repB.search(this);
		this.addTandemRepeat(repB);
		
	}
	
	
	public void addTandemRepeat(RepeatBuilder repB) {
		/* on ne garde que les patterns ayant au moins deux repeats */
		if(repB.getBDrepeats().size()>0) {
			patterns.addAll(repB.getBDrepeats());			
		}
	}
	
	
	/**
	 * 
	 *clear the seed array
	 */
	public void clear() {
		refineSeeds.clear();
	}
	
	
	
	
	@Override
	public String toString() {
		return this.getDesc()+"\n"+this.getSequence()+"\n";
	}
	
	/**
	 * @return
	 * @uml.property  name="refineSeeds"
	 */
	public ArrayList<Sstring> getRefineSeeds() {
		return refineSeeds;
	}
	/**
	 * @param refineSeeds
	 * @uml.property  name="refineSeeds"
	 */
	public void setRefineSeeds(ArrayList<Sstring> refineSeeds) {
		this.refineSeeds = refineSeeds;
	}
	public OverlapManager getPatterns() {
		return patterns;
	}
	public void setPatterns(OverlapManager patterns) {
		this.patterns = patterns;
	}
	
	
}
