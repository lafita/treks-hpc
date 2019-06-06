package nptr;

import java.util.ArrayList;

@SuppressWarnings("serial")
public class OverlapManager extends ArrayList<Repeat>{

	private boolean active;
	private ArrayList<Repeat> repeatsDeleted;
	/**
	 * constructeur
	 *
	 */
	public OverlapManager() {
		super();
		active=true;
		repeatsDeleted=new ArrayList<Repeat>();
	}
	/**
	 permet d'activer ou non le Manager
	 * @param a: booléen d'activation/désactivation du manager
	 */
	public boolean isActive() {
		return active;
	}
	
	public void setActivated(boolean a) {
		active=a;
	}
	
	/**
	 * Si le manager est actif, cette méthode permet de garder le meilleur 
	 * repeat dans le cas de chevauchement
	 */
	public void deleteOverlaps() {
		if(active && this.size()>1) {
			
			for(int i=0;i<this.size();i++) {
				int j=0;
				Repeat referent=this.get(i);
				int begOfReference=referent.getBeginPosition();
				int endOfReference=referent.getEndPosition();
				
				while(j<i && repeatsDeleted.size()<this.size()-1) {
						Repeat compared=this.get(j);
						int begToCompare=compared.getBeginPosition();
						int endToCompare=compared.getEndPosition();
						//cas où c'est non chevauchant
						if((begOfReference>endToCompare || endOfReference<begToCompare)) {
							//on ne fait rien
						}else {
							//on stocke les mauvais repeats chevauchants
							if (Position.nearDist(referent.getRegionLength(),compared.getRegionLength())|| Position.nearDist(compared.getRegionLength(),referent.getRegionLength())) {
								if(referent.getSimilarity()==compared.getSimilarity()) {
									if(referent.getLength()>compared.getLength()) {
										this.addDeleted(compared);
									}else this.addDeleted(referent);
								}else {
									if(referent.getSimilarity()>compared.getSimilarity()) {
										this.addDeleted(compared);
									}else this.addDeleted(referent);
								}
								
							}else {
								if(referent.getRegionLength()>compared.getRegionLength()){
									this.addDeleted(compared);
								}else this.addDeleted(referent);
							}
						}
					j++;
				}
			}
			/** élimination de l'arraylist des repeats chevauchants **/
			for(int i=0;i<repeatsDeleted.size();i++) {
				this.remove(repeatsDeleted.get(i));
			}
			
		}
	}
	private void addDeleted(Repeat r) {
		if(!repeatsDeleted.contains(r))repeatsDeleted.add(r);
	}
}
