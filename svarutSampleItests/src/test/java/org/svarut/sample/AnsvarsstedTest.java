package org.svarut.sample;

import no.kommune.bergen.svarut.v1.*;
import org.junit.Test;
import org.svarut.sample.utils.SvarUtServiceCreator;

import javax.activation.DataHandler;
import javax.mail.util.ByteArrayDataSource;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AnsvarsstedTest {

	private SvarUtService service = SvarUtServiceCreator.getService();

	@Test
	public void testSenderAvsender() throws Exception {
		ForsendelsesRq rq = new ForsendelsesRq();
		rq.setData(new DataHandler(new ByteArrayDataSource(new byte[]{},"application/txt")));
		Forsendelse f = new Forsendelse();
		f.setNavn("Test Testesen");
		f.setMeldingstekst("Hei fra SvarUtItest");
		f.setTittel("Viktig melding!");
		f.setEpost("test@hudson.iktfou.no");
		f.setForsendelsesMate(ShipmentPolicy.KUN_EMAIL_INGEN_VEDLEGG);
		f.setAnsvarsSted("EnKulPlass");
		rq.setForsendelse(f);

		String forsendelsesId = service.send(null, rq);
		SvarUtServiceCreator.waitTillFinishedWorking();
		List<ForsendelseStatus> status = service.retrieveStatus(null, Arrays.asList(new String[]{forsendelsesId}));
		assertEquals("EnKulPlass", status.get(0).getForsendelse().getAnsvarsSted());
	}
}
