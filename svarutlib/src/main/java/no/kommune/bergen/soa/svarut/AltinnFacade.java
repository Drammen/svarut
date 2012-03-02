package no.kommune.bergen.soa.svarut;

import java.util.Map;

import no.kommune.bergen.soa.common.util.TemplateEngine;
import no.kommune.bergen.soa.svarut.altin.CorrespondenceClient;
import no.kommune.bergen.soa.svarut.altin.CorrespondenceMessage;
import no.kommune.bergen.soa.svarut.altin.CorrespondenceSettings;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;
import org.apache.log4j.Logger;

/** Intended for communicating with Altinn Correspondence Service */
public class AltinnFacade {

	public static final Logger log = Logger.getLogger(AltinnFacade.class);
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

	public int send(Forsendelse f) {
		return correspondenceClient.send( createMessage( f ) );
	}

	CorrespondenceMessage createMessage( Forsendelse f ) {
		Map<String, String> model = modelFactory.createModel( f );
		if(log.isDebugEnabled()){
			log.debug("Forsendelse: " + f);
			log.debug("Template Engine" + templateEngine.toString());
			log.debug("subject template: " + this.settings.getSubjectTemplate());
			log.debug("body template: " + this.settings.getBodyTemplate());
		}
		String subject = templateEngine.merge( model, this.settings.getSubjectTemplate() );
		String body = templateEngine.merge( model, this.settings.getBodyTemplate() );
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
		return new CorrespondenceClient( this.settings );
	}

}
