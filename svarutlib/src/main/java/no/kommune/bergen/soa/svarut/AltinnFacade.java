package no.kommune.bergen.soa.svarut;

import java.util.List;
import java.util.Map;

import no.kommune.bergen.soa.common.util.TemplateEngine;
import no.kommune.bergen.soa.svarut.altinn.authorization.Avgiver;
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
	public final AltinnAuthorization altinnAuthorizationServiceClient;

	public AltinnFacade( TemplateEngine templateEngine, CorrespondenceClient correspondenceClient, VelocityModelFactory modelFactory ) {
		this.templateEngine = templateEngine;
		this.modelFactory = modelFactory;
		this.correspondenceClient = correspondenceClient;
		this.correspondenceSettings = correspondenceClient.getSettings();
		this.altinnAuthorizationServiceClient = null;

	}

	public AltinnFacade( TemplateEngine templateEngine, CorrespondenceClient correspondenceClient, AltinnAuthorization altinnAuthorizationServiceClient, VelocityModelFactory modelFactory ) {
		this.templateEngine = templateEngine;
		this.modelFactory = modelFactory;
		this.correspondenceClient = correspondenceClient;
		this.correspondenceSettings = correspondenceClient.getSettings();
		this.altinnAuthorizationServiceClient = altinnAuthorizationServiceClient;
	}

	public int send(Forsendelse f) {
		return correspondenceClient.send( createMessage( f ) );
	}

	public boolean authorizeUserAgainstOrgNr(String fodselsNr, String orgNr) {
		List<Avgiver> avgivere = altinnAuthorizationServiceClient.getOrganisasjonsAvgivere(fodselsNr);
		if(avgivere.isEmpty() || orgNr == null)
			return false;

		for(Avgiver avgiver : avgivere) {
			if(avgiver.getOrganizationNumber().equals(orgNr)) {
				return true;
			}
		}
		return false;
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
