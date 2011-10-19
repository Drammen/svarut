package no.kommune.bergen.soa.svarut;

import no.kommune.bergen.soa.svarut.domain.Orgnr;

import org.junit.Assert;
import org.junit.Test;

public class OrgnrTest {

	@Test
	public void createOrgOk() {
		String orgnr = "123456789";
		Assert.assertEquals( orgnr, new Orgnr( orgnr ).toString() );
	}

	@Test(expected = java.lang.IllegalArgumentException.class)
	public void createOrgFail() {
		String orgnr = "1234567";
		Assert.assertEquals( orgnr, new Orgnr( orgnr ).toString() );
	}

}
