package nptr;

import java.util.LinkedList;

import nptr.utils.Hash;

/**
 * Default parameters of T-Reks are set, and graphic management
 * of the user parameters is implemented in this class.
 * 
 * @author   julien
 */
public class Parameters {
	
	/// DEFAULT PARAMETERS
	
	/* for proteins */
	private static int nbClusterProt=12;
	private static int seedLengthProt=2;
	
	/* for dna */
	private static int nbClusterDNA=20;
	private static int seedLengthDNA=4;
	
	/* by default =proteins */
	public static int nbCluster=nbClusterProt;
	public static int seedLength=seedLengthProt;
	
	/**
	 * The minimum length of a repeting region to be considered.
	 */
	public static int totalLength=(seedLength*5+4);
	
	/* sequence type */
	private static int pType=0;//protein
	private static int dType=1;// DNA
	public static int seqType=pType;
	
	/* common parameters */
	public static double threshold=0.7;
	public static boolean overlapActivated=true;
	public static String clustalPath="";
	public static String musclePath="";
	public static boolean compress=false;
	
	// What the hell is this!? Well it seems to be the varIndel parameter (indel:percent)
	public static LinkedList <ParamField> aParam  =new LinkedList <ParamField> ();
	
	/**
	 ** ajoute le param�tre par d�faut � la linkedList aParam pour version ligne de commande
	 ** @param val: valeur � d�finir par d�faut
	 */
	
	public static void setParamDefault(String val) {
		ParamField defaultParam=new ParamField("Default",val);
		aParam.add(defaultParam);
	}

	/**
	 ** ajoute le param�tre � la linkedList aParam pour version ligne de commande
	 ** @param l: la longueur du repeat en question
	 ** @param p:le pourcentage de variabilit� de longueur accept�
	 **/
	public static void setParam(String l,String p) {
		ParamField pf=new ParamField(l,p);
		aParam.add(pf);
	}
	
	private static void setType (int type) {
		if(dType==type) {
			Parameters.seqType=dType;
			Parameters.nbCluster=Parameters.nbClusterDNA;
			Parameters.seedLength=Parameters.seedLengthDNA;
		}else {
			Parameters.nbCluster=Parameters.nbClusterProt;
			Parameters.seedLength=Parameters.seedLengthProt;
		}
		Parameters.totalLength=5*seedLength+4;
	}
	
	public static Hash getParams() {
		Hash hParam=new Hash();
		/* on r�cup�re les params */
		for (int i=0;i<aParam.size();i++) {
			hParam.put(aParam.get(i).getLength(),aParam.get(i).getPercent());
		}
		return hParam;
	}
	
}
