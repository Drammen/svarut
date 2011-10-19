package org.svarut.sample;

import no.kommune.bergen.svarut.v1.*;
import org.junit.Test;
import org.svarut.sample.utils.ForsendelseUtil;
import org.svarut.sample.utils.SvarUtServiceCreator;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class PrintProviderTest {

	private SvarUtService service = SvarUtServiceCreator.getService();

	@Test
	public void sjekkApostForsendelse() throws InterruptedException {
		ForsendelsesRq rq = new ForsendelsesRq();
		rq.setData(ForsendelseUtil.hentTestFilDataHandler());

		Forsendelse f = new Forsendelse();

		Adresse123 adresse = new Adresse123();
		adresse.setAdresse1("Skvaldrekroken 2");
		adresse.setPostnr("9999");
		adresse.setPoststed("LagtINord");
		f.setAdresse(adresse);

		f.setNavn("Ola Normann");
		f.setMeldingstekst("Hei fra SvarUtItest");
		f.setTittel("Viktig melding!");

		f.setForsendelsesMate(ShipmentPolicy.KUN_APOST);

		rq.setForsendelse(f);

		String forsendelsesId = service.send(null, rq);
		SvarUtServiceCreator.waitTillFinishedWorking();
		List<ForsendelseStatus> status = service.retrieveStatus(null, Arrays.asList(new String[]{forsendelsesId}));
		assertNotNull(status.get(0).getSendtBrevpost());

	}
}
