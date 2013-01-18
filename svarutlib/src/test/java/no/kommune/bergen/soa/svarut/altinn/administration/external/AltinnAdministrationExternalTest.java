package no.kommune.bergen.soa.svarut.altinn.administration.external;

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

import org.junit.Test;

public class AltinnAdministrationExternalTest {

	private AltinnFacade altinnFacade;
	private AltinnAuthorization altinnAuthorization;
	private ForsendelsesArkiv forsendelsesArkiv;
	private final String AuthorizedFodselsNr = "02035701829";
	private final String UnauthorizedFodselsNr = "12345678910";
	private final String OrgNr = "910824929";

	private AltinnAuthorizationDesicionPointExternalSettings settings;

	public void initWithStandardReturnDataInMockService() {
		settings = new AltinnAuthorizationDesicionPointExternalSettings();
		settings.setEndpoint("http://brukes_ikke");
		altinnAuthorization = new AltinnAuthorization(settings);
		MockAdministrationExternal client = new MockAdministrationExternal(settings);
		client.setExternalReporteeBETestList(MockAdministrationExternal.createTestData());
		altinnAuthorization.setAltinnAdministrationExternalClient(client);
		CorrespondenceClient correspondenceClient = new CorrespondenceClient(MockCorrespondenceClient.newSettings());
		altinnFacade = new AltinnFacade(null, correspondenceClient, altinnAuthorization, null);
		forsendelsesArkiv = createForsendesesArkiv();
	}

	public void initWithNoReturnDataInMockService() {
		settings = new AltinnAuthorizationDesicionPointExternalSettings();
		settings.setEndpoint("http://brukes_ikke");
		altinnAuthorization = new AltinnAuthorization(settings);
		MockAdministrationExternal client = new MockAdministrationExternal(settings);
		altinnAuthorization.setAltinnAdministrationExternalClient(client);
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
	public void testAuthorizeMotFodselsNrOgOrganisasjonErOk() {
		initWithStandardReturnDataInMockService(); // Init with data

		Forsendelse forsendelse1 = ForsendelsesArkivTest.createForsendelse(1, AuthorizedFodselsNr, OrgNr);
		String forsendelseId1 = forsendelsesArkiv.save( forsendelse1, ForsendelsesArkivTest.class.getClassLoader().getResourceAsStream( "test.pdf" ) );

		Forsendelse forsendelse2 = ForsendelsesArkivTest.createForsendelse(1, AuthorizedFodselsNr, null);
		String forsendelseId2 = forsendelsesArkiv.save( forsendelse2, ForsendelsesArkivTest.class.getClassLoader().getResourceAsStream( "test.pdf" ) );

		Forsendelse forsendelse3 = ForsendelsesArkivTest.createForsendelse(1, AuthorizedFodselsNr, null);
		String forsendelseId3 = forsendelsesArkiv.save( forsendelse3, ForsendelsesArkivTest.class.getClassLoader().getResourceAsStream( "test.pdf" ) );

		forsendelsesArkiv.authorize(forsendelseId1, AuthorizedFodselsNr);
		forsendelsesArkiv.authorize(forsendelseId2, AuthorizedFodselsNr);
		forsendelsesArkiv.authorize(forsendelseId3, AuthorizedFodselsNr);

	}

	@Test(expected = AccessControlException.class)
	public void testAuthorizeMedUgyldigFodselsNrIkkeGirTilgangMedSjekkMotFodselsNr() {
		initWithNoReturnDataInMockService(); // Init with no data. Emulating not authorized.

		Forsendelse forsendelse1 = ForsendelsesArkivTest.createForsendelse(1, AuthorizedFodselsNr, null);
		String forsendelseId1 = forsendelsesArkiv.save( forsendelse1, ForsendelsesArkivTest.class.getClassLoader().getResourceAsStream( "test.pdf" ) );

		forsendelsesArkiv.authorize(forsendelseId1, UnauthorizedFodselsNr);
	}

	@Test(expected = AccessControlException.class)
	public void testAuthorizeMedUgyldigFodselsNrIkkeGirTilgangMedSjekkMotOrgNr() {
		initWithNoReturnDataInMockService(); // Init with no data. Emulating not authorized.

		Forsendelse forsendelse1 = ForsendelsesArkivTest.createForsendelse(1, AuthorizedFodselsNr, OrgNr);
		String forsendelseId1 = forsendelsesArkiv.save( forsendelse1, ForsendelsesArkivTest.class.getClassLoader().getResourceAsStream( "test.pdf" ) );

		forsendelsesArkiv.authorize(forsendelseId1, UnauthorizedFodselsNr);
	}

	@Test(expected = AccessControlException.class)
	public void testAuthorizeMedUgyldigFodselsNrIkkeGirTilgangMedSjekkMotOrgNr_FodselsNrIkkeSatt() {
		initWithNoReturnDataInMockService(); // Init with no data. Emulating not authorized.

		Forsendelse forsendelse1 = ForsendelsesArkivTest.createForsendelse(1, null, OrgNr);
		String forsendelseId1 = forsendelsesArkiv.save( forsendelse1, ForsendelsesArkivTest.class.getClassLoader().getResourceAsStream( "test.pdf" ) );

		forsendelsesArkiv.authorize(forsendelseId1, UnauthorizedFodselsNr);
	}
}
