package no.kommune.bergen.soa.svarut;

import no.kommune.bergen.soa.svarut.domain.Fodselsnr;

import org.junit.Assert;
import org.junit.Test;

public class FodselsnrTest {

	@Test
	public void createOrgOk() {
		String fnr = "12345678901";
		Assert.assertEquals( fnr, new Fodselsnr( fnr ).toString() );
	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void createOrgFail() {
		String fnr = "123456789012";
		Assert.assertEquals( fnr, new Fodselsnr( fnr ).toString() );
	}

}
