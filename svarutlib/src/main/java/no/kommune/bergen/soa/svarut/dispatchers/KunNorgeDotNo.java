package no.kommune.bergen.soa.svarut.dispatchers;

import no.kommune.bergen.soa.common.exception.UserException;
import no.kommune.bergen.soa.svarut.DispatchPolicy;
import no.kommune.bergen.soa.svarut.EmailFacade;
import no.kommune.bergen.soa.svarut.LdapFacade;
import no.kommune.bergen.soa.svarut.ServiceDelegate;
import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkiv;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;
import org.apache.log4j.Logger;

public class KunNorgeDotNo extends AbstractDispatcher {
	final Logger logger = Logger.getLogger( KunNorgeDotNo.class );
	LdapFacade ldapFacade;
	final EmailFacade emailFacade;
	private final long leadTimeBeforeStop = 1000 * 60 * 60 * 24 * 5;

	public KunNorgeDotNo(ServiceDelegate serviceDelegate, LdapFacade ldapFacade, ForsendelsesArkiv forsendelsesArkiv, EmailFacade emailFacade, DispatchPolicy dispatchPolicy) {
		super(serviceDelegate, dispatchPolicy, forsendelsesArkiv);
		this.ldapFacade = ldapFacade;
		this.forsendelsesArkiv = forsendelsesArkiv;
		this.emailFacade = emailFacade;
	}

	public void send( Forsendelse f ) {
		if (ldapFacade.lookup( f.getFnr() )) {
			emailFacade.send( f );
			forsendelsesArkiv.setSentNorgeDotNo( f.getId() );
			logger.info( String.format( "Successfully sent to Norge.no. Id=%s, Fnr=%s", f.getId(), f.getFnr() ) );
		} else {
			logger.info( String.format( "Transmission to Norge.no failed. No mailbox found. Id=%s, Fnr=%s", f.getId(), f.getFnr() ) );
			forsendelsesArkiv.stop( f.getId() );
		}
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
		final String[] names = { "Fnr", "Tittel" };
		final String[] required = { f.getFnr(), f.getTittel() };
		for (int i = 0; i < required.length; i++) {
			if (required[i] == null) {
				throw new UserException( "Required fields are: fodselsnr, tittel. Missing " + names[i] );
			}
		}
	}

}
