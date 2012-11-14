package no.kommune.bergen.soa.svarut.dispatchers;

import no.kommune.bergen.soa.common.exception.UserException;
import no.kommune.bergen.soa.svarut.AltinnFacade;
import no.kommune.bergen.soa.svarut.DispatchPolicy;
import no.kommune.bergen.soa.svarut.ServiceDelegate;
import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkiv;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class KunAltinn extends AbstractDispatcher {

	private static final Logger log = LoggerFactory.getLogger(KunAltinn.class);

	private static final long LEAD_TIME_BEFORE_STOP = 1000 * 60 * 60 * 24 * 5;

	final AltinnFacade altinnFacade;

	public KunAltinn(ServiceDelegate serviceDelegate, ForsendelsesArkiv forsendelsesArkiv, AltinnFacade altinnFacade, DispatchPolicy dispatchPolicy) {
		super(serviceDelegate, dispatchPolicy, forsendelsesArkiv);
		this.forsendelsesArkiv = forsendelsesArkiv;
		this.altinnFacade = altinnFacade;
	}

	public void send(Forsendelse f) {
		int receiptId = altinnFacade.send(f);
		forsendelsesArkiv.setSentAltinn(f.getId(), receiptId);
		log.info(String.format("Successfully sent to Altinn. Id=%s, Org=%s, Navn=%s", f.getId(), f.getOrgnr(), f.getNavn()));
	}

	@Override
	public void handleUnread(Forsendelse f) {
		long sent = f.getSendt().getTime();
		long now = System.currentTimeMillis();
		if (sent + LEAD_TIME_BEFORE_STOP < now)
			forsendelsesArkiv.stop(f.getId());
	}

	@Override
	public void verify(Forsendelse f) {
		if (f.getTittel() == null || (f.getOrgnr() == null && f.getFnr() == null)) {
			throw new UserException(String.format("Required fields are: orgnr/fodselsnr, tittel. Received: orgnr=%s, fodselsnr=%s, tittel=%s.", f.getOrgnr(), f.getTittel(), f.getFnr()));
		}
	}
}
