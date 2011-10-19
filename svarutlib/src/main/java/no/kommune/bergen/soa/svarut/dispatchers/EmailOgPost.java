package no.kommune.bergen.soa.svarut.dispatchers;

import no.kommune.bergen.soa.common.exception.UserException;
import no.kommune.bergen.soa.svarut.*;
import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkiv;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;
import no.kommune.bergen.soa.svarut.domain.PrintReceipt;

import org.apache.log4j.Logger;

public class EmailOgPost extends AbstractDispatcher {
	final Logger logger = Logger.getLogger( EmailOgPost.class );
	final EmailFacade emailFacade;
	final PrintFacade printFacade;

	public EmailOgPost(ServiceDelegate serviceDelegate, ForsendelsesArkiv forsendelsesArkiv, EmailFacade emailFacade, PrintFacade printFacade, DispatchPolicy dispatchPolicy) {
		super(serviceDelegate, dispatchPolicy, forsendelsesArkiv);
		this.forsendelsesArkiv = forsendelsesArkiv;
		this.emailFacade = emailFacade;
		this.printFacade = printFacade;
	}

	public void send( Forsendelse f ) {
		try {
			sendEmail( f );
		} catch (Exception e) {
            logger.info("Sending email failed, sending to print: " + e.getMessage());
			sendToPrint( f );
		}
	}

	private void sendToPrint( Forsendelse f ) {
		PrintReceipt printReceipt = printFacade.print( f );
		forsendelsesArkiv.setPrinted( f.getId(), printReceipt );
		logger.info( "Successfully sent to print. Id= " + f.getId() + ", Name=" + f.getNavn() );
	}

	private void sendEmail( Forsendelse f ) {
		if (isOmitted( f.getEmail() )) throw new RuntimeException( "Unable to send email -- no address supplied." );
		emailFacade.send( f );
		forsendelsesArkiv.setSentEmail( f.getId() );
		logger.info( "Successfully sent email to " + f.getEmail() + ", Name=" + f.getNavn() );
		if (!(emailFacade instanceof EmailFacadeDocumentAlert)) {
			forsendelsesArkiv.stop( f.getId() );
		}
	}

	private boolean isOmitted( String str ) {
		return str == null || str.trim().isEmpty();
	}

	@Override
	public void handleUnread( Forsendelse f ) {
		long sent = f.getSendt().getTime();
		long now = System.currentTimeMillis();

		if (sent + getLeadTimeBeforePrint(f) < now) {
			PrintReceipt printReceipt = printFacade.print( f );
			forsendelsesArkiv.setPrinted( f.getId(), printReceipt );
		}
	}

    private long getLeadTimeBeforePrint(Forsendelse f) {
        return getDispatchPolicy().getLeadTimeBeforePrintInMilliseconds(f);
    }

    @Override
	public void verify( Forsendelse f ) {
		final String[] required = { f.getNavn(), f.getPostnr(), f.getPoststed(), f.getTittel() };
		for (String field : required) {
			if (field == null) {
				throw new UserException( String.format( "Required fields are: navn, postnr, poststed, tittel. Received email=%s, navn=%s, adresse1=%s, postnr=%s, poststed=%s, tittel=%s.", f.getEmail(), f.getNavn(), f.getAdresse1(), f.getPostnr(), f
						.getPostnr(), f.getTittel() ) );
			}
		}
	}

}
