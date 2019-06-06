package nptr.utils;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class Utils {

	public static String getName(String f) {
		int last=f.lastIndexOf(".");
		String name=f;
		if(last>-1) name =f.substring(0,last);
		return name;
	}
	/*
     * Get the extension of a file.
     */  
	public static String getExtension(String f) {
		return getExtension(new File(f));
	}
    public static String getExtension(File f) {
        String ext = null;
        String s = f.getName();
        int i = s.lastIndexOf('.');

        if (i > 0 &&  i < s.length() - 1) {
            ext = s.substring(i+1).toLowerCase();
        }
        return ext;
    }
    
    public static HashMap sortMapByValue(LinkedHashMap statesMap) {
        HashMap sortedStates = new HashMap();
        SortedMap tempMap = new TreeMap();
   
        // first, swap the key/value and save to a tempMap
        Set keySet = statesMap.keySet();
        Iterator i = keySet.iterator();
        while (i.hasNext()) {
            Object key = i.next();
            Object value =  statesMap.get(key);
           // System.out.println("adding " + value + " : " + key);
            tempMap.put(value, key);
        }
   
        // now, swap back the key/value
        Set keySet1 = tempMap.keySet();
        Iterator j = keySet1.iterator();
        while (j.hasNext()) {
            Object key =  j.next();
            Object value = tempMap.get(key);
   
            System.out.println("adding " + value + " : " + key);
            sortedStates.put(value, key);
        }
        System.out.println("---------");
        return sortedStates;
      }
    
}
