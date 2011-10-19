package no.kommune.bergen.soa.util;

/**
 * A string utility class
 */
public class Strings {
	private Strings() {
	}

	public static boolean isEmpty(String str) {
		return str == null || str.trim().length() == 0;
	}

	public static String fill(String str, int length) {
		int max = length - str.length();
		for (int i = 0; i < max; i++)
			str += " ";
		return str;
	}

}
