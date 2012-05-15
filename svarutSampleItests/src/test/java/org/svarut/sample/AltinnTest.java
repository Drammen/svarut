package org.svarut.sample;

import no.kommune.bergen.svarut.v1.*;
import org.junit.Test;
import org.svarut.sample.utils.ForsendelseUtil;
import org.svarut.sample.utils.SvarUtServiceCreator;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class AltinnTest {

	private SvarUtService service = SvarUtServiceCreator.getService();


	@Test
	public void sendAltinn() throws Exception {
		ForsendelsesRq rq = new ForsendelsesRq();
		rq.setForsendelse(new Forsendelse());
		rq.getForsendelse().setForsendelsesMate(ShipmentPolicy.KUN_ALTINN);
		rq.getForsendelse().setOrgnr(99999999);
		rq.getForsendelse().setTittel("abc");
		rq.getForsendelse().setMeldingstekst("Kul tekst");
		rq.setData(ForsendelseUtil.hentTestFilDataHandler());
		String forsendelsesId = service.send(null, rq);
		SvarUtServiceCreator.waitTillFinishedWorking();

		List<ForsendelseStatus> status = service.retrieveStatus(null, Arrays.asList(new String[]{forsendelsesId}));
		assertNotNull("Ikkje sendt til altinn", status.get(0).getSendtAltinn());

	}

	@Test
	public void testFarAltinnFaultSkalSendeTilPrint() throws Exception {
		ForsendelsesRq rq = new ForsendelsesRq();
		rq.setForsendelse(new Forsendelse());
		rq.getForsendelse().setNavn("Et Navn");
		Adresse123 adresse = new Adresse123();
		adresse.setAdresse1("Skvaldrekroken 2");
		adresse.setPostnr("9999");
		adresse.setPoststed("LagtINord");
		rq.getForsendelse().setAdresse(adresse);
		rq.getForsendelse().setForsendelsesMate(ShipmentPolicy.ALTINN_OG_APOST);
		rq.getForsendelse().setOrgnr(111111111);
		rq.getForsendelse().setTittel("abc");
		rq.getForsendelse().setMeldingstekst("Kul tekst");
		rq.setData(ForsendelseUtil.hentTestFilDataHandler());
		String forsendelsesId = service.send(null, rq);
		SvarUtServiceCreator.waitTillFinishedWorking();

		List<ForsendelseStatus> status = service.retrieveStatus(null, Arrays.asList(new String[]{forsendelsesId}));
		assertNull("Sending til altinn skulle feilet", status.get(0).getSendtAltinn());
		assertNotNull("Ikkje sendt til Post", status.get(0).getSendtBrevpost());
	}
}
