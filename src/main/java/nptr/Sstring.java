package nptr;

import java.util.LinkedList;

public class Sstring implements Cloneable{
	/**
	 * @uml.property  name="nom"
	 */
	private String name;
	/**
	 * @uml.property  name="positions"
	 */
	private LinkedList<Position> positions;
	/**
	 * @uml.property  name="nbSeq"
	 */
	private int nbSeq;
	/**
	 * @uml.property  name="nbRepeat"
	 */
	private int nbRepeat;
	/**
	 * @uml.property  name="dist"
	 */
	private int dist;
	
	
	

	public Sstring() {
		this.positions=new LinkedList<Position>();
		this.nbSeq=0;
		this.nbRepeat=0;
		this.dist=0;
		this.name="";
	}
	
	public Sstring(String n) {
		this.name=n;
		this.positions=new LinkedList<Position>();
		this.nbSeq=0;
		this.nbRepeat=0;
		this.dist=0;
	}
	/**
	 * @param n
	 * @uml.property  name="nom"
	 */
	public void setName(String n) {
		this.name=n;
	}
	/**
	 * @return
	 * @uml.property  name="nom"
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @return
	 * @uml.property  name="positions"
	 */
	public LinkedList<Position> getPositions() {
		return positions;
	}

	/**
	 * @param positions
	 * @uml.property  name="positions"
	 */
	public void setPositions(LinkedList<Position> positions) {
		this.positions = positions;
	}
	/** ajoute une position � la graine et calcule la distance pour la position pr�cedente
	 * @param pos : indice de la position dans la s�quence
	 * @uml.property  name="positions"
	 */
	public void addPosition(int pos) {
			/*boolean chevauch=false;
			for (int p=0;p<positions.size();p++) {
				int pos2=(int)positions.get(p).getPos();
				if((pos>=pos2 && pos<pos2+this.nom.length())||( (pos+this.nom.length())>pos2 && pos<= pos2 )) {
					chevauch=true;
				}
			}*/
			//if(!chevauch) {
				Position nPos=new Position(pos);
				//System.out.println();
				if(positions.size()>0) {
					int previousDist=positions.get(positions.size()-1).calcDist(nPos);
//					 si c'est la premi�re graine r�pertori�e, distance initialis�e � 0, sinon dist=-(derni�re distance)
					nPos.setDist(-previousDist);
				}
				this.nbSeq++;
				
				positions.add(nPos);
			//}
		
	}
	/** permet de r�cup�rer une position par rapport � son int **/
	public Position getPosition(int f) {
		int p=0;
		boolean found=false;
		while(p<positions.size() &&!found) {
			if(positions.get(p).getPos()==f) {
				found=true;
			}else p++;
		}
		if(p!=0) return positions.get(p);
		else return null;
	}
	
	public boolean isPosition(int pos) {
		boolean found=false;
		int p=0;
		while(p<positions.size() &&!found) {
			if(positions.get(p).getPos()==pos) {
				found=true;
			}else p++;
		}
		return found;
	}
	
	public Position getNextPosition(int f) {
		int p=0;
		boolean found=false;
		while(p<positions.size() &&!found) {
			if(positions.get(p).getPos()>f) {
				found=true;
			}else p++;
		}
		if(p!=0 && f<positions.getLast().getPos()) return positions.get(p);
		else return null;
	}
	
	public Position getPreviousPosition(int f) {
		int p=0;
		boolean found=false;
		while(p<positions.size() &&!found) {
			if(positions.get(p).getPos()>=f) {
				if(p!=0)return positions.get(p-1);
				else return positions.get(0);
			}else p++;
		}
		return null;
	}
	
	public void clearPositions() {
		this.positions.clear();
	}
	/**
	 * @return
	 * @uml.property  name="nbRepeat"
	 */
	public int getNbRepeat() {
		return nbRepeat;
	}

	/**
	 * @param nbRepeat
	 * @uml.property  name="nbRepeat"
	 */
	public void setNbRepeat(int nbRepeat) {
		this.nbRepeat = nbRepeat;
	}

	/**
	 * @return
	 * @uml.property  name="nbSeq"
	 */
	public int getNbSeq() {
		return nbSeq;
	}

	/**
	 * @param nbSeq
	 * @uml.property  name="nbSeq"
	 */
	public void setNbSeq(int nbSeq) {
		this.nbSeq = nbSeq;
	}


	/**
	 * @param dist
	 * @uml.property  name="dist"
	 */
	public void setDist(int dist) {
		this.dist = dist;
	}
	/**
	 * @return
	 * @uml.property  name="dist"
	 */
	public int getDist() {
		return this.dist;
	}
	
	public void findbestLength() {
		int lastDist=this.positions.get(0).getDist();
		int bestDist=this.positions.get(0).getDist();
		int cptDist=1;
		int bestNbDist=1;
		for (int i=1;i<this.positions.size();i++) {
			Position currentPos=this.positions.get(i);
			if(currentPos.nearDist(lastDist)) {
				cptDist++;
			}else {
				if(cptDist>bestNbDist) {
					bestNbDist=cptDist;
					bestDist=this.positions.get(i-1).getDist();
				}
				cptDist=1;
			}
			lastDist=currentPos.getDist();
		}
		this.dist=bestDist;
		//System.out.println(this.nom+" longueur "+bestDist+" r�p�t� "+bestNbDist);
	}
	protected Object clone() throws CloneNotSupportedException
	  {   
	    return super.clone();
	  }
	public String toString() {
		return (this.name+" SML "+this.dist);
	}
}
