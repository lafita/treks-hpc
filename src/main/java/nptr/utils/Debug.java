package nptr.utils;

public class Debug {

	private static boolean debug = false;

	public static void set(boolean b) {
		Debug.debug = b;
	}

	public static void print(String p) {

		if (debug) {
			System.out.println(p);
		}
	}

}
