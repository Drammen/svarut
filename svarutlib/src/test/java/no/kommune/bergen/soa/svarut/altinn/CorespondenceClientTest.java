package no.kommune.bergen.soa.svarut.altinn;

import junit.framework.Assert;

import no.kommune.bergen.soa.svarut.altinn.correspondence.CorrespondenceClient;
import no.kommune.bergen.soa.svarut.altinn.correspondence.CorrespondenceMessage;
import no.kommune.bergen.soa.svarut.altinn.correspondence.CorrespondenceSettings;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class CorespondenceClientTest {
	private CorrespondenceSettings settings;

	@Before
	public void setup() {
		settings = MockCorrespondenceClient.newSettings();
	}

	@Test
	@Ignore
	public void sendLive() {
		CorrespondenceClient client = new CorrespondenceClient( settings );
		CorrespondenceMessage message = MockCorrespondenceClient.createMessage();
		int receiptId = client.send( message );
		Assert.assertFalse( 0 == receiptId );
	}

	@Test
	public void send() {
		CorrespondenceClient client = new MockCorrespondenceClient( settings );
		CorrespondenceMessage message = MockCorrespondenceClient.createMessage();
		int receiptId = client.send( message );
		Assert.assertEquals( 1, receiptId );
	}

}
