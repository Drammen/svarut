package no.kommune.bergen.soa.common.util;

public class Fodselsnummer {

	public static boolean verify( String fodselsnummer ) {
		try {
			String fnr = fodselsnummer.trim();
			if (fnr == null || fnr.length() != 11) return false;
			Long.parseLong( fnr );

			byte[] ba = fnr.getBytes();
			int[] n = new int[11];
			for (int i = 0; i < 11; i++) {
				n[i] = ba[i] - 48;
			}

			int k1 = 11 - (3 * n[0] + 7 * n[1] + 6 * n[2] + 1 * n[3] + 8 * n[4] + 9 * n[5] + 4 * n[6] + 5 * n[7] + 2 * n[8]) % 11;
			if (k1 == 11) k1 = 0;
			if (k1 == 10 || k1 != n[9]) return false;

			int k2 = 11 - (5 * n[0] + 4 * n[1] + 3 * n[2] + 2 * n[3] + 7 * n[4] + 6 * n[5] + 5 * n[6] + 4 * n[7] + 3 * n[8] + 2 * k1) % 11;
			if (k2 == 11) k2 = 0;
			if (k2 == 10 || k2 != n[10]) return false;

			return true;
		} catch (Exception e) {
			// IGNORE e.printStackTrace();
		}
		return false;
	}

}
