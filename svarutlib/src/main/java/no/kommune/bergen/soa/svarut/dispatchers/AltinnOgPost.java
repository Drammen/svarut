package no.kommune.bergen.soa.svarut.dispatchers;

import no.kommune.bergen.soa.common.exception.UserException;
import no.kommune.bergen.soa.svarut.AltinnFacade;
import no.kommune.bergen.soa.svarut.DispatchPolicy;
import no.kommune.bergen.soa.svarut.PrintFacade;
import no.kommune.bergen.soa.svarut.ServiceDelegate;
import no.kommune.bergen.soa.svarut.altinn.AltinnException;
import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkiv;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;
import no.kommune.bergen.soa.svarut.domain.PrintReceipt;

import org.slf4j.LoggerFactory;

public class AltinnOgPost extends AbstractDispatcher {

	final org.slf4j.Logger logger = LoggerFactory.getLogger(AltinnOgPost.class);
	final PrintFacade printFacade;
	final AltinnFacade altinnFacade;

	public AltinnOgPost(ServiceDelegate serviceDelegate, ForsendelsesArkiv forsendelsesArkiv, AltinnFacade alltinnFacade, PrintFacade printFacade, DispatchPolicy dispatchPolicy) {
		super(serviceDelegate, dispatchPolicy, forsendelsesArkiv);

		this.altinnFacade = alltinnFacade;
		this.forsendelsesArkiv = forsendelsesArkiv;
		this.printFacade = printFacade;
	}

	@Override
	public void send( Forsendelse f ) {
		try{
			int receiptId = altinnFacade.send( f );
			forsendelsesArkiv.setSentAltinn( f.getId(), receiptId);
			logger.info( String.format( "Successfully sent to Altinn. Id=%s, Org=%s, Navn=%s", f.getId(), f.getOrgnr(), f.getNavn() ) );
		} catch(AltinnException e){
			logger.warn("Altinn failed for " + f.getId() + " feil: " + e.getMessage(), e);
			logger.info("Sending {} to Print", f.getId());
			sendToPrint(f);
		}
	}

	@Override
	public void handleUnread( Forsendelse f ) {
		long sent = f.getSendt().getTime();
		long now = System.currentTimeMillis();
		if (sent + getDispatchPolicy().getLeadTimeBeforePrintInMilliseconds(f) < now) {
			sendToPrint(f);
		}
	}

	private void sendToPrint(Forsendelse f) {
		PrintReceipt printReceipt = printFacade.print( f );
		forsendelsesArkiv.setPrinted( f.getId(), printReceipt );
	}

	@Override
	public void verify( Forsendelse f ) {
		final String[] required = { f.getNavn(), f.getPostnr(), f.getPoststed(), f.getTittel(), f.getMeldingsTekst() };
		for (String field : required) {
			if (field == null) {
				throw new UserException( String.format( "Required fields are: orgnr, navn,  postnr, poststed, tittel, meldingsTekst. Received: navn=%s, postnr=%s, poststed=%s, tittel=%s, meldingsTekst=%s.", f.getNavn(), f.getPostnr(), f.getPoststed(), f
						.getTittel(), f.getMeldingsTekst() ) );
			}
		}
		if (f.getOrgnr() == null && f.getFnr() == null) {
			throw new UserException( String.format( "Required fields are: orgnr/fodselsnr, tittel. Received: orgnr=%s, fodselsnr=%s, tittel=%s.", f.getOrgnr(), f.getTittel(), f.getFnr() ) );
		}
	}

}
