package no.kommune.bergen.soa.common.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FodselsnummerTest {

	@Test
	public void verifyFodselsnummerTest() {
		String[] valids = { "04055934369  ", " 24035738572", " 65038300827 " };
		String[] invalids = { "04055934368", "1234567890", "asd", "", "           ", null, "1234567890123", "24035738575" };
		for (String fnr : valids) {
			assertTrue( fnr + " is invalid! expected valid", Fodselsnummer.verify( fnr ) );
		}
		for (String fnr : invalids) {
			assertFalse( fnr + " is valid! expected invalid", Fodselsnummer.verify( fnr ) );
		}
	}

}
