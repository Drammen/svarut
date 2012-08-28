package org.svarut.sample;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import no.kommune.bergen.svarut.v1.Adresse123;
import no.kommune.bergen.svarut.v1.Forsendelse;
import no.kommune.bergen.svarut.v1.ForsendelseStatus;
import no.kommune.bergen.svarut.v1.ForsendelsesRq;
import no.kommune.bergen.svarut.v1.ShipmentPolicy;
import no.kommune.bergen.svarut.v1.SvarUtService;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.joda.time.DateTime;
import org.junit.Test;
import org.svarut.sample.utils.Constants;
import org.svarut.sample.utils.ForsendelseUtil;
import org.svarut.sample.utils.SvarUtServiceCreator;

public class PrintProviderTest {

	private SvarUtService service = SvarUtServiceCreator.getService();

	@Test
	public void sjekkApostForsendelse() throws InterruptedException {
		ForsendelsesRq rq = getForsendelsesRequestData();
		String forsendelsesId = service.send(null, rq);
		assertNotNull("ForsendelseId var null", forsendelsesId);
		SvarUtServiceCreator.waitTillFinishedWorking();
		List<ForsendelseStatus> status = service.retrieveStatus(null, Arrays.asList(forsendelsesId));
		assertNotNull("Forsendelsen var null", status.get(0).getSendtBrevpost());
	}

	@Test
	public void testShouldSendAForsendelsesAndCheckThatFailedToPrintIsNotEmpty() throws Exception{
		String forsendelsesId = service.send(null, getForsendelsesRequestData());
		SvarUtServiceCreator.waitTillFinishedWorking();
		service.retrieveStatus(null, Arrays.asList(forsendelsesId));
		String responsString = getFailedToPrintString();
		assertEquals("FailedToPrintId-list should be empty.", "[]", responsString);

		DateTime dateTime = new DateTime();
		dateTime = dateTime.minusDays(8);
		Date date = dateTime.toDate();

		PostMethod methodUpdatePrinted = new PostMethod(Constants.webContainer + "/service/rest/forsendelsesservice/updateSentToPrint/"+ forsendelsesId +"/"+date.getTime());
		HttpClient client = new HttpClient();
		int resultUpdatePrinted = client.executeMethod(methodUpdatePrinted);
		assertEquals("Http response for UpdatePrinted should be 200.", 200, resultUpdatePrinted);

		service.retrieveStatus(null, Arrays.asList(forsendelsesId));
		String respons = getFailedToPrintString();
		assertEquals("Response should contain one item which failed to print", "[" + forsendelsesId + "]", respons);

		service.deleteForsendelse(null, forsendelsesId);
	}

	private String getFailedToPrintString() throws IOException {
		HttpClient client = new HttpClient();
		GetMethod methodFailedToPrint = new GetMethod(Constants.webContainer + "/service/rest/forsendelsesservice/retrieve/failedToPrint");
		int result = client.executeMethod(methodFailedToPrint);
		assertEquals("Http response for FailedToPrint should be 200.", 200, result);
		return methodFailedToPrint.getResponseBodyAsString();
	}

	private ForsendelsesRq getForsendelsesRequestData() {
		ForsendelsesRq rq = new ForsendelsesRq();
		rq.setData(ForsendelseUtil.hentTestFilDataHandler());
		rq.setForsendelse(getForsendelseData());
		return rq;
	}

	private Forsendelse getForsendelseData() {
		Forsendelse forsendelse = new Forsendelse();
		forsendelse.setAdresse(getAdresseData());
		forsendelse.setNavn("Ola Normann");
		forsendelse.setMeldingstekst("Hei fra SvarUtItest");
		forsendelse.setTittel("Viktig melding!");
		forsendelse.setForsendelsesMate(ShipmentPolicy.KUN_APOST);
		return forsendelse;
	}

	private Adresse123 getAdresseData() {
		Adresse123 adresse = new Adresse123();
		adresse.setAdresse1("Skvaldrekroken 2");
		adresse.setPostnr("9999");
		adresse.setPoststed("LagtINord");
		return adresse;
	}
}
