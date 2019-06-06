package nptr.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
/**
* Hash.java by JORDA Julien
*/
@SuppressWarnings("serial")
public class Hash extends LinkedHashMap<Object,Object>{
	private Set cles;
	public Hash() {
		super();
	}
	public String toString () {
		cles = this.keySet();
		Iterator ite = cles.iterator();
		String retour="";
		while(ite.hasNext()) {
			Object cle=ite.next();
			retour+="cle :"+cle+" valeur: "+this.get(cle)+"\n";
		}
		return retour;
	}
	public Hash sort(boolean ascending) {

		ArrayList<Object> mapKeys = new ArrayList<Object>(this.keySet());
		ArrayList mapValues = new ArrayList(this.values());
		Collections.sort(mapValues);

		if (!ascending)
		Collections.reverse(mapValues);

		Hash someMap = new Hash();
		Iterator valueIt = mapValues.iterator();
			while (valueIt.hasNext()) {
				Object val = valueIt.next();
				Iterator keyIt = mapKeys.iterator();
				//System.out.println("value : "+val);
				while (keyIt.hasNext()) {
				Object key = keyIt.next();
				if (this.get(key).toString().equals(val.toString())) {
					this.remove(key);
					mapKeys.remove(key);
					someMap.put(key,val);
					break;
				}
			}
		}
		return someMap;
	}
	
	public Object getFirst() {
		Object[] tabKeys = this.keySet().toArray();
		return tabKeys[0];
	}
	public Object getKeyAt(int ind) {
		Object[] tabKeys = this.keySet().toArray();
		return tabKeys[ind];
	}
	public Object getValueAt(int ind) {
		Object[] tabKeys = this.keySet().toArray();
		if(tabKeys.length>0) return this.get(tabKeys[ind]);
		else return null;
	}
	public Object getFirstValue() {
		Object[] tabKeys = this.keySet().toArray();
		if(tabKeys.length>0) return this.get(tabKeys[0]);
		else return null;
	}
	
	public void filterFirst() {
		cles = this.keySet();
		if(cles.size()>1) this.filter(1,cles.size()-1);
	}
	
	public void  filter(int deb,int fin) {
		Object[] tabKeys = this.keySet().toArray();
		for(int i=deb;i<=fin;i++) {
			Object cle=tabKeys[i];
			this.remove(cle);
			deb++;
		}
	}
	public void  filter(int index) {
		Object[] tabKeys = this.keySet().toArray();
			Object cle=tabKeys[index];
			this.remove(cle);
	}
}
