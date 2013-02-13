package no.kommune.bergen.soa.svarut.altinn.authorization;

import java.security.AccessControlException;

import no.kommune.bergen.soa.common.pdf.PdfGeneratorImpl;
import no.kommune.bergen.soa.svarut.AltinnFacade;
import no.kommune.bergen.soa.svarut.JdbcHelper;
import no.kommune.bergen.soa.svarut.altinn.MockCorrespondenceClient;
import no.kommune.bergen.soa.svarut.altinn.authorization.client.AltinnAuthorization;
import no.kommune.bergen.soa.svarut.altinn.authorization.client.AltinnAuthorizationDesicionPointExternalSettings;
import no.kommune.bergen.soa.svarut.altinn.correspondence.CorrespondenceClient;
import no.kommune.bergen.soa.svarut.dao.FileStore;
import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkiv;
import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkivTest;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;

import org.junit.Ignore;
import org.junit.Test;
/**
 * Bruk denne testen for å sjekke servicen mot Altinn test server.
 * Ta bort Ignore for å kjøre.
 */
public class AltinnAuthorizationDecisionPointExternalEkteServiceTest {

	private AltinnFacade altinnFacade;
	private AltinnAuthorization altinnAuthorization;
	private ForsendelsesArkiv forsendelsesArkiv;
	private final String AuthorizedFodselsNr = "02035701829";
	private final String UnauthorizedFodselsNr = "12345678910";
	private final String OrgNr = "910824929";

	private AltinnAuthorizationDesicionPointExternalSettings settings;

	public void initWithAltinnTestService() {
		settings = new AltinnAuthorizationDesicionPointExternalSettings();
		settings.setEndpoint("https://tt02.altinn.basefarm.net/AuthorizationExternal/AuthorizationDecisionPointExternal.svc");
		settings.setSystemUserName("BK_meldinger");
		settings.setSystemPassword("UO6pXuIt");
		settings.setServiceEdition("1");
		settings.setServiceCode("1268");
		settings.setEnvironment("TT2");
		altinnAuthorization = new AltinnAuthorization(settings);

		CorrespondenceClient correspondenceClient = new CorrespondenceClient(MockCorrespondenceClient.newSettings());
		altinnFacade = new AltinnFacade(null, correspondenceClient, altinnAuthorization, null);
		forsendelsesArkiv = createForsendesesArkiv();
	}

	public ForsendelsesArkiv createForsendesesArkiv() {
		JdbcHelper jdbcHelper = new JdbcHelper();
		jdbcHelper.createTable( "FORSENDELSESARKIV" );
		FileStore fileStore = new FileStore("target", new PdfGeneratorImpl("target"));
		return new ForsendelsesArkiv( fileStore, jdbcHelper.getJdbcTemplate(), altinnFacade );
	}

	@Test
	@Ignore
	public void testAuthorizeMotEkteAltinnServiceAutoriserer() {
		initWithAltinnTestService(); // Init

		Forsendelse forsendelse1 = ForsendelsesArkivTest.createForsendelse(1, AuthorizedFodselsNr, OrgNr);
		String forsendelseId1 = forsendelsesArkiv.save( forsendelse1, ForsendelsesArkivTest.class.getClassLoader().getResourceAsStream( "test.pdf" ) );

		forsendelsesArkiv.authorize(forsendelseId1, AuthorizedFodselsNr); // Throws an exception if it fails to authorize
	}

	@Test(expected=AccessControlException.class)
	@Ignore
	public void testAuthorizeMotEkteAltinnServiceNekterTilgang() {
		initWithAltinnTestService(); // Init

		Forsendelse forsendelse1 = ForsendelsesArkivTest.createForsendelse(1, AuthorizedFodselsNr, OrgNr);
		String forsendelseId1 = forsendelsesArkiv.save( forsendelse1, ForsendelsesArkivTest.class.getClassLoader().getResourceAsStream( "test.pdf" ) );

		forsendelsesArkiv.authorize(forsendelseId1, UnauthorizedFodselsNr); // Throws an exception if it fails to authorize
	}
}
