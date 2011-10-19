package no.kommune.bergen.soa.svarut.dispatchers;

import no.kommune.bergen.soa.common.exception.UserException;
import no.kommune.bergen.soa.svarut.DispatchPolicy;
import no.kommune.bergen.soa.svarut.PrintFacade;
import no.kommune.bergen.soa.svarut.ServiceDelegate;
import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkiv;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;
import no.kommune.bergen.soa.svarut.domain.PrintReceipt;

import org.apache.log4j.Logger;

public class KunPost extends AbstractDispatcher {
	final Logger logger = Logger.getLogger(KunPost.class);
	final PrintFacade printFacade;

	public KunPost(ServiceDelegate serviceDelegate, ForsendelsesArkiv forsendelsesArkiv, PrintFacade printFacade, DispatchPolicy dispatchPolicy) {
		super(serviceDelegate, dispatchPolicy, forsendelsesArkiv);
		this.forsendelsesArkiv = forsendelsesArkiv;
		this.printFacade = printFacade;
	}

	public void send(Forsendelse f) {
		PrintReceipt printReceipt = printFacade.print(f);
		forsendelsesArkiv.setPrinted(f.getId(), printReceipt);
		logger.info("Successfully sent to print. Id= " + f.getId() + ", Name=" + f.getNavn());
	}

	@Override
	public void handleUnread(Forsendelse f) {
		long sent = f.getSendt().getTime();
		DispatchPolicy dp = getDispatchPolicy();
		long now = System.currentTimeMillis();

		if (sent + dp.getLeadTimeBeforeStopInMilliseconds(f) < now) {
			forsendelsesArkiv.stop(f.getId());
		}

	}

	@Override
	public void verify(Forsendelse f) {
		final String[] required = {f.getNavn(), f.getPostnr(), f.getPoststed(), f.getTittel()};
		for (String field : required) {
			if (field == null) {
				throw new UserException(String.format("Required fields are: navn, postnr, poststed, tittel. Received: navn%s, adresse1%s, postnr%s, poststed%s, tittel=%s.", f.getNavn(), f.getAdresse1(), f.getPostnr(), f.getPoststed(), f
						.getTittel()));
			}
		}
	}

}
