package no.kommune.bergen.soa.svarut;

import java.util.Map;

import no.kommune.bergen.soa.common.util.TemplateEngine;
import no.kommune.bergen.soa.svarut.altinn.authorization.client.AltinnAuthorization;
import no.kommune.bergen.soa.svarut.altinn.correspondence.CorrespondenceClient;
import no.kommune.bergen.soa.svarut.altinn.correspondence.CorrespondenceMessage;
import no.kommune.bergen.soa.svarut.altinn.correspondence.CorrespondenceSettings;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;

import org.apache.log4j.Logger;

/** Intended for communicating with Altinn Services */
public class AltinnFacade {

	public static final Logger log = Logger.getLogger(AltinnFacade.class);
	public final TemplateEngine templateEngine;
	public final VelocityModelFactory modelFactory;
	public final CorrespondenceClient correspondenceClient;
	public final CorrespondenceSettings correspondenceSettings;
	public final AltinnAuthorization altinnAuthorization;

	public AltinnFacade( TemplateEngine templateEngine, CorrespondenceClient correspondenceClient, VelocityModelFactory modelFactory ) {
		this.templateEngine = templateEngine;
		this.modelFactory = modelFactory;
		this.correspondenceClient = correspondenceClient;
		this.correspondenceSettings = correspondenceClient.getSettings();
		this.altinnAuthorization = null;

	}

	public AltinnFacade( TemplateEngine templateEngine, CorrespondenceClient correspondenceClient, AltinnAuthorization altinnAuthorization, VelocityModelFactory modelFactory ) {
		this.templateEngine = templateEngine;
		this.modelFactory = modelFactory;
		this.correspondenceClient = correspondenceClient;
		this.correspondenceSettings = correspondenceClient.getSettings();
		this.altinnAuthorization = altinnAuthorization;
	}

	public int send(Forsendelse f) {
		return correspondenceClient.send( createMessage( f ) );
	}

	public boolean authorizeUserAgainstOrgNr(String fodselsNr, String orgNr) {
		return altinnAuthorization.authorize(fodselsNr, orgNr);
	}

	CorrespondenceMessage createMessage( Forsendelse f ) {
		Map<String, String> model = modelFactory.createModel( f );
		if(log.isDebugEnabled()){
			log.debug("Forsendelse: " + f);
			log.debug("Template Engine" + templateEngine.toString());
			log.debug("subject template: " + this.correspondenceSettings.getSubjectTemplate());
			log.debug("body template: " + this.correspondenceSettings.getBodyTemplate());
		}
		String subject = templateEngine.merge( model, this.correspondenceSettings.getSubjectTemplate() );
		String body = templateEngine.merge( model, this.correspondenceSettings.getBodyTemplate() );
		if(log.isDebugEnabled())log.debug("Subject: " + subject + " body: " + body);
		CorrespondenceMessage message = new CorrespondenceMessage();
		if (f.getOrgnr() != null)
			message.setOrgNr(f.getOrgnr());
		else
			message.setOrgNr(f.getFnr());
		message.setMessageTitle( subject );
		message.setMessageSummary( subject );
		message.setMessageBody( body );
		message.setExternalReference( f.getId() );
		return message;
	}

	protected CorrespondenceClient newCorrespondenceClient() {
		return new CorrespondenceClient( this.correspondenceSettings );
	}

}
