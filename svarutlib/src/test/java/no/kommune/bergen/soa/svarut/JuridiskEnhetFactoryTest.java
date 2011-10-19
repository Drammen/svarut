package no.kommune.bergen.soa.svarut;

import no.kommune.bergen.soa.svarut.domain.Fodselsnr;
import no.kommune.bergen.soa.svarut.domain.IngenJuridiskEnhet;
import no.kommune.bergen.soa.svarut.domain.JuridiskEnhet;
import no.kommune.bergen.soa.svarut.domain.Orgnr;
import no.kommune.bergen.soa.svarut.dto.UserContext;

import org.junit.Assert;
import org.junit.Test;

public class JuridiskEnhetFactoryTest {

	@Test
	public void createJuridiskEnhetFromFnrOk() {
		Assert.assertTrue( JuridiskEnhetFactory.create( "12345678901" ) instanceof Fodselsnr );
	}

	@Test
	public void createJuridiskEnhetfromFnrFail() {
		Assert.assertTrue( JuridiskEnhetFactory.create( "1234567890" ) instanceof IngenJuridiskEnhet );
	}

	@Test
	public void createJuridiskEnhetFromOrgOk() {
		Assert.assertTrue( JuridiskEnhetFactory.create( "123456789" ) instanceof Orgnr );
	}

	@Test
	public void createJuridiskEnhetFromOrgFail() {
		Assert.assertTrue( JuridiskEnhetFactory.create( "1234567" ) instanceof IngenJuridiskEnhet );
	}

	@Test
	public void createJuridiskEnhetFromUserIdOk() {
		Assert.assertTrue( JuridiskEnhetFactory.create( newUserContext( "12345678901", null ) ) instanceof Fodselsnr );
	}

	@Test
	public void createJuridiskEnhetFrombehalfOfIdOk() {
		String onBehalfOfId = "12345678902";
		UserContext userContext = newUserContext( "12345678901", onBehalfOfId );
		JuridiskEnhet juridiskEnhet = JuridiskEnhetFactory.create( userContext );
		Assert.assertTrue( juridiskEnhet instanceof Fodselsnr );
		Assert.assertEquals( onBehalfOfId, juridiskEnhet.getValue() );
	}

	@Test
	public void createJuridiskEnhetFrombehalfOfIdFail() {
		String onBehalfOfId = "12345";
		UserContext userContext = newUserContext( "12345678901", onBehalfOfId );
		Assert.assertTrue( JuridiskEnhetFactory.create( userContext ) instanceof IngenJuridiskEnhet );
	}

	@Test
	public void createJuridiskEnhetUserIdFail() {
		String userId = "12345";
		UserContext userContext = newUserContext( userId, "" );
		Assert.assertTrue( JuridiskEnhetFactory.create( userContext ) instanceof IngenJuridiskEnhet );
	}

	private UserContext newUserContext( String userId, String onBehalfOfId ) {
		UserContext userContext = new UserContext();
		userContext.setUserid( userId );
		userContext.setOnBehalfOfId( onBehalfOfId );
		return userContext;
	}
}
