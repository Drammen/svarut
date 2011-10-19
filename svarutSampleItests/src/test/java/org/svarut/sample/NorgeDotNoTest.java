package org.svarut.sample;

import no.kommune.bergen.svarut.v1.*;
import org.junit.Test;
import org.svarut.sample.utils.ForsendelseUtil;
import org.svarut.sample.utils.SvarUtServiceCreator;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNotNull;

public class NorgeDotNoTest {

	private SvarUtService service = SvarUtServiceCreator.getService();

	@Test
	public void sjekkNorgeDotNoForsendelse() throws InterruptedException {
		ForsendelsesRq rq = new ForsendelsesRq();
		rq.setData(ForsendelseUtil.hentTestFilDataHandler());

		Forsendelse f = new Forsendelse();
		f.setFodselsnummer("24035738572");
		f.setMeldingstekst("Hei fra SvarUtItest");
		f.setTittel("Viktig melding!");

		f.setForsendelsesMate(ShipmentPolicy.KUN_NORGE_DOT_NO);

		rq.setForsendelse(f);

		String forsendelsesId = service.send(null, rq);
		SvarUtServiceCreator.waitTillFinishedWorking();
		List<ForsendelseStatus> status = service.retrieveStatus(null, Arrays.asList(new String[]{forsendelsesId}));
		assertNotNull(status.get(0).getSendtNorgedotno());
	}
}
