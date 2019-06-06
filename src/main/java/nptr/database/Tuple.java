package nptr.database;

import java.util.ArrayList;
import java.util.HashMap;

@SuppressWarnings("serial")
public class Tuple extends ArrayList<HashMap>{

	public Tuple() {
		super();
	}
	
	public HashMap contains(Object field,Object Value) {
		for (int i=0;i<this.size();i++) {
			HashMap entry=this.get(i);
			if (entry.get(field).equals(Value)) return entry;
		}
		return null;
	}
	public boolean isEntry(Object field,Object Value) {
		for (int i=0;i<this.size();i++) {
			HashMap entry=this.get(i);
			if (entry.get(field).equals(Value)) return true;
		}
		return false;
	}
	
}
