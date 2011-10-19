package no.kommune.bergen.soa.svarut.dispatchers;

import no.kommune.bergen.soa.common.exception.UserException;
import no.kommune.bergen.soa.svarut.*;
import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkiv;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;
import no.kommune.bergen.soa.svarut.domain.PrintReceipt;

import no.kommune.bergen.soa.svarut.dto.ShipmentPolicy;
import org.apache.log4j.Logger;

public class NorgeDotNoOgPost extends AbstractDispatcher {

	final Logger logger = Logger.getLogger( NorgeDotNoOgPost.class );
	LdapFacade ldapFacade;
    final EmailFacade emailFacade;
	final PrintFacade printFacade;

	public NorgeDotNoOgPost(ServiceDelegate serviceDelegate, LdapFacade ldapFacade, ForsendelsesArkiv forsendelsesArkiv, EmailFacade emailFacade, PrintFacade printFacade, DispatchPolicy dispatchPolicy) {
	    super(serviceDelegate, dispatchPolicy, forsendelsesArkiv);
		this.ldapFacade = ldapFacade;
        this.emailFacade = emailFacade;
		this.printFacade = printFacade;
	}

	public void send( Forsendelse f ) {
		if (ldapFacade.lookup( f.getFnr() )) {
			emailFacade.send( f );
			forsendelsesArkiv.setSentNorgeDotNo( f.getId() );
			logger.info( "Successfully sent to Norge.no. Id= " + f.getId() + ", Name=" + f.getNavn() );
		} else {
			PrintReceipt printReceipt = printFacade.print( f );
			forsendelsesArkiv.setPrinted( f.getId(), printReceipt );
			logger.info( "Successfully sent to print. Id= " + f.getId() + ", Name=" + f.getNavn() );
		}
	}

	@Override
	public void handleUnread( Forsendelse f ) {

		long sent = f.getSendt().getTime();
		long now = System.currentTimeMillis();
        ShipmentPolicy sp = ShipmentPolicy.fromValue(f.getShipmentPolicy());

        DispatchPolicy dp = getDispatchPolicy();

		if (sent + dp.getLeadTimeBeforePrintInMilliseconds(f)  < now) {
			PrintReceipt printReceipt = printFacade.print( f );
			forsendelsesArkiv.setPrinted( f.getId(), printReceipt );
		}
	}

	@Override
	public void verify( Forsendelse f ) {
		final String[] required = { f.getFnr(), f.getNavn(), f.getPostnr(), f.getPoststed(), f.getTittel() };
		for (String field : required) {
			if (field == null) {
				throw new UserException( String.format( "Required fields are: fodselsnr, navn,  postnr, poststed, tittel. Received: fodselsnr=%s, navn=%s, postnr=%s, poststed=%s, tittel=%s.", f.getFnr(), f.getNavn(), f.getPostnr(), f.getPoststed(),
						f.getTittel() ) );
			}
		}
	}
}
