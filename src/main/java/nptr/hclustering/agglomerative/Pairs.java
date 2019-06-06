package nptr.hclustering.agglomerative;

import java.util.LinkedList;

@SuppressWarnings("serial")
public class Pairs extends LinkedList<Pair>{

	public Pairs() {
		super();
	}
	
	@Override
	public boolean contains(Object hp) {
		for(Pair p:this) {
			if(p.equals(hp)) return true;
		}
		return false;
	}
}
