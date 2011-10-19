package no.kommune.bergen.soa.svarut;

import java.util.Map;

import no.kommune.bergen.soa.common.util.TemplateEngine;
import no.kommune.bergen.soa.svarut.altin.CorrespondenceClient;
import no.kommune.bergen.soa.svarut.altin.CorrespondenceMessage;
import no.kommune.bergen.soa.svarut.altin.CorrespondenceSettings;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;

/** Intended for communicating with Altinn Correspondence Service */
public class AltinnFacade {
	public final TemplateEngine templateEngine;
	public final VelocityModelFactory modelFactory;
	public final CorrespondenceClient correspondenceClient;
	public final CorrespondenceSettings settings;

	public AltinnFacade( TemplateEngine templateEngine, CorrespondenceClient correspondenceClient, VelocityModelFactory modelFactory ) {
		this.templateEngine = templateEngine;
		this.modelFactory = modelFactory;
		this.correspondenceClient = correspondenceClient;
		this.settings = correspondenceClient.getSettings();
	}

	public void send( Forsendelse f ) {
		correspondenceClient.send( createMessage( f ) );
	}

	private CorrespondenceMessage createMessage( Forsendelse f ) {
		Map<String, String> model = modelFactory.createModel( f );
		String subject = templateEngine.merge( model, this.settings.getSubjectTemplate() );
		String body = templateEngine.merge( model, this.settings.getBodyTemplate() );
		CorrespondenceMessage message = new CorrespondenceMessage();
		message.setOrgNr( f.getOrgnr() );
		message.setEmailToNotify( f.getEmail() );
		message.setSmsToNotify( f.getSms() );
		message.setMessageTitle( subject );
		message.setMessageSummary( subject );
		message.setMessageBody( body );
		message.setExternalReference( f.getId() );
		return message;
	}

	protected CorrespondenceClient newCorrespondenceClient() {
		return new CorrespondenceClient( this.settings );
	}

}
