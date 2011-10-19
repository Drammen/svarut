package no.kommune.bergen.soa.util;

import junit.framework.Assert;

import org.junit.Test;

public class StringsTest {

	@Test
	public void isEmpty() {
		Assert.assertTrue( Strings.isEmpty( null ) );
		Assert.assertTrue( Strings.isEmpty( "" ) );
		Assert.assertTrue( Strings.isEmpty( " " ) );
		Assert.assertFalse( Strings.isEmpty( "0" ) );
	}

	@Test
	public void fill() {
		Assert.assertEquals( 1, Strings.fill( "", 1 ).length() );
		Assert.assertEquals( 12, Strings.fill( "1", 12 ).length() );
		Assert.assertEquals( 0, Strings.fill( "", 0 ).length() );
		Assert.assertEquals( 1, Strings.fill( " ", 1 ).length() );
		Assert.assertEquals( 2, Strings.fill( "  ", 1 ).length() );
	}

}
