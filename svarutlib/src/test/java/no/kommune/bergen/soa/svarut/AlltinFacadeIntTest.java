package no.kommune.bergen.soa.svarut;

import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkivTest;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;
import no.kommune.bergen.soa.svarut.dto.ShipmentPolicy;
import no.kommune.bergen.soa.svarut.util.FilHenter;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AlltinFacadeIntTest {
	private final static String org = "910558919";

	@Test
	@Ignore
	public void sendToAltinnDEV() {
		System.setProperty( "CONSTRETTO_TAGS", "DEV" );
		sendToAltinn();
	}

	@Test
	@Ignore
	public void sendToAltinnATEST() {
		System.setProperty( "CONSTRETTO_TAGS", "ATEST" );
		sendToAltinn();
	}

	private void sendToAltinn() {
		ApplicationContext context = new ClassPathXmlApplicationContext( "applicationContext.xml" );
		ServiceContext serviceContext = (ServiceContext)context.getBean( "serviceContext" );
		AltinnFacade altinnFacade = serviceContext.getAltinnFacade();
		Forsendelse forsendelse = ForsendelsesArkivTest.createForsendelse( 1 );
		forsendelse.setShipmentPolicy(ShipmentPolicy.KUN_ALTINN.value());
        forsendelse.setFile(FilHenter.getFileAsFile(ForsendelsesArkivTest.testPdf));
		forsendelse.setOrgnr( org );
		forsendelse.setTittel( "Fra AlltinFacadeIntTest" );
		forsendelse.setEmail( "einarvalen@gmail.com" );
		forsendelse.setSms( "004795996325" );
		forsendelse.setId( FilHenter.getFileAsFile(ForsendelsesArkivTest.testPdf).getName() );
		altinnFacade.send( forsendelse );
	}

}
/*
https://tt02.altinn.basefarm.net/no/
login: 04017002479
Org: 910558919
Pins: 1: ajhhs, Pin2: piksd, Pin3: iuyhs, Pin4: asdfg, Pin5: rtefs, Pin6: loj7s, Pin7: mmmyp, Pin8: juksa, Pin9: fizck, Pin10: qicks, Pin11: 98ujs, Pin12: mnbvs, Pin13: qwers, Pin14: polze, Pin15: ztang, Pin16: alt1n, Pin17: zcatt, Pin18: kjasd, Pin19: 23as3, Pin20: lkiju, Pin21: 4564s, Pin22: zxhfg, Pin23: alsks, Pin24: ooiks, Pin25: likme, Pin26: kaffe, Pin27: arbei, Pin28: 00kks, Pin29: mjhgg, Pin30: ziste.
*/
