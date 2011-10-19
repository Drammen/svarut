package org.svarut.sample;

import no.kommune.bergen.svarut.v1.*;
import org.junit.Test;
import org.svarut.sample.utils.ForsendelseUtil;
import org.svarut.sample.utils.SvarUtServiceCreator;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;

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
}
