package no.kommune.bergen.soa.svarut.dispatchers;

import no.kommune.bergen.soa.common.exception.UserException;
import no.kommune.bergen.soa.svarut.DispatchPolicy;
import no.kommune.bergen.soa.svarut.EmailFacade;
import no.kommune.bergen.soa.svarut.ServiceDelegate;
import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkiv;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;

import no.kommune.bergen.soa.svarut.dto.ShipmentPolicy;
import org.apache.log4j.Logger;

public class KunEmail extends AbstractDispatcher {
	final Logger logger = Logger.getLogger( KunEmail.class );
	final EmailFacade emailFacade;
	private final long leadTimeBeforeStop = 1000 * 60 * 60 * 24 * 5;

	public KunEmail(ServiceDelegate serviceDelegate, ForsendelsesArkiv forsendelsesArkiv, EmailFacade emailFacade, DispatchPolicy dispatchPolicy) {
		super(serviceDelegate, dispatchPolicy, forsendelsesArkiv);
		this.forsendelsesArkiv = forsendelsesArkiv;
        this.emailFacade = emailFacade;
    }

	public void send( Forsendelse f ) {
        logger.debug("Sender forsendelse " + f.getId() + "...");
		if (isSkipAttachments(f)) f.setFile( null );
		emailFacade.send( f );
        logger.debug("Markerer forsendelse som sendt " + f.getId() + "...");

		forsendelsesArkiv.setSentEmail( f.getId() );
		forsendelsesArkiv.stop( f.getId() );
		logger.info( "Successfully sent email to " + f.getEmail() + ", Name=" + f.getNavn() );
	}

	@Override
	public void handleUnread( Forsendelse f ) {
		long sent = f.getSendt().getTime();
		long now = System.currentTimeMillis();
		if (sent + this.leadTimeBeforeStop < now) {
			forsendelsesArkiv.stop( f.getId() );
		}
	}

	@Override
	public void verify( Forsendelse f ) {
		final String[] required = { f.getNavn(), f.getEmail(), f.getTittel() };
		for (String field : required) {
			if (field == null) {
				throw new UserException( String.format( "Required fields are: navn, email, tittel. Received: navn=%s, email=%s, tittel=%s.", f.getNavn(), f.getEmail(), f.getTittel() ) );
			}
		}
	}

	public boolean isSkipAttachments(Forsendelse f) {
		return  ShipmentPolicy.fromValue(f.getShipmentPolicy() ).equals(ShipmentPolicy.KUN_EMAIL_INGEN_VEDLEGG);
	}

}
