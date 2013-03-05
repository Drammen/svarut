package no.kommune.bergen.soa.svarut.dispatchers;

import java.util.Date;
import java.util.List;

import no.kommune.bergen.soa.svarut.DispatchPolicy;
import no.kommune.bergen.soa.svarut.DispatchPolicyShipmentParams;
import no.kommune.bergen.soa.svarut.Dispatcher;
import no.kommune.bergen.soa.svarut.ServiceDelegate;
import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkiv;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;
import no.kommune.bergen.soa.svarut.dto.ShipmentPolicy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDispatcher implements Dispatcher {

	private static final Logger log = LoggerFactory.getLogger(AbstractDispatcher.class);

	public static final int ONE_HOUR = 1000 * 60 * 60;

	protected DispatchPolicy dispatchPolicy;
	protected ForsendelsesArkiv forsendelsesArkiv;
	protected ServiceDelegate serviceDelegate;

	protected AbstractDispatcher(ServiceDelegate serviceDelegate, DispatchPolicy dispatchPolicy, ForsendelsesArkiv forsendelsesArkiv) {
		this.dispatchPolicy = dispatchPolicy;
		this.forsendelsesArkiv = forsendelsesArkiv;
		this.serviceDelegate = serviceDelegate;
	}

	public DispatchPolicy getDispatchPolicy() {
		return dispatchPolicy;
	}

	public abstract void verify(Forsendelse f);

	public abstract void send(Forsendelse f);

	public abstract void handleUnread(Forsendelse f);

	@Override
	public void sendAlleForsendelser() {

		final String dispatcherName = getClass().getName();

		if (dispatchPolicy.isDispatchWindowOpen()) {

			log.debug("sendAlleForsendelser from " + getClass().getName());

			final List<String> forsendelser = forsendelsesArkiv.readUnsent(dispatchPolicy.getShipmentPolicies());
			final int antallForsendelser = forsendelser.size();
			log.debug("Antall forsendelser:" + antallForsendelser + " hentet for " + dispatcherName);

			long akkumulertDispatchTid = 0;
			int antallSendteForsendelser = 0;
			String shipmentPolicy = "<>";

			for (String forsendelsesId : forsendelser) {
				try {
					long start = System.currentTimeMillis();
					Forsendelse forsendelse = forsendelsesArkiv.retrieve(forsendelsesId);
					log.debug("Sender forsendelse " + forsendelsesId + " med " + dispatcherName);
					shipmentPolicy = forsendelse.getShipmentPolicy();

					try {
						send(forsendelse);
					} catch (Exception e) {
						Date nesteforsok = new Date(new Date().getTime() + ONE_HOUR);
						forsendelsesArkiv.markForsendelseFailed(forsendelse, nesteforsok);
						throw new RuntimeException(e);
					}

					serviceDelegate.markMessageSendt(forsendelsesId);
					log.debug("forsendelse " + forsendelsesId + " sendt");

					long end = System.currentTimeMillis();
					akkumulertDispatchTid += (end - start);
					antallSendteForsendelser++;
					double gjennomsnittMsPrForsendelse = (double) akkumulertDispatchTid / (double) antallSendteForsendelser;

					int maksAntallForsendelserPrMinutt = dispatchPolicy.getMaxDispatchRate();
					if (maksAntallForsendelserPrMinutt > 0) { // 0 == no delay
						double minimumAntallMsPrForsendelse = (double) (60 * 1000) / (double) maksAntallForsendelserPrMinutt;
						long delay = (long) (minimumAntallMsPrForsendelse - gjennomsnittMsPrForsendelse);
						log.debug("sendAlleForsendelser delay: " + delay);

						if (delay > 0) {
							try {
								Thread.sleep(delay);
							} catch (InterruptedException ie) {
								log.warn("Thread sleep interrupted");
							}
						}
					}
				} catch (Exception e) {
					log.error("En feil oppstod ved sending av forsendelse " + forsendelsesId + " med dispatcher " + dispatcherName + " og ShipmentPolicy " + shipmentPolicy, e);
					serviceDelegate.markMessageSendFailed(forsendelsesId, e.getMessage());
				}
			}
		} else {
			log.info("Forsendelser ikke sendt, utenfor åpningstid");
		}
		serviceDelegate.markSendAlleForsendelserCalled();
		log.debug("sendAlleForsendelser from " + dispatcherName + " ferdig");
	}

	@Override
	public void handleAllUnread() {
		List<String> forsendelser = forsendelsesArkiv.retrieveYoungerThan(getDispatchPolicy().getPrintWindowAgeInDays(), dispatchPolicy.getShipmentPolicies());
		forsendelser.addAll(forsendelsesArkiv.retrieveSentToAltinnButNotPrinted(dispatchPolicy.getShipmentPolicies())); //TODO PIA-1573 Ta bort dette når vi ikke skal sende til print lenger
		final String dispatcherName = getClass().getName();
		log.debug("Antall uleste forsendelser:" + forsendelser.size() + " hentet for " + dispatcherName);

		if (dispatchPolicy.isDispatchWindowOpen()) {

			long akkumulertDispatchTid = 0;
			int antallSendteForsendelser = 0;
			for (String forsendelsesId : forsendelser) {
				try {
					long start = System.currentTimeMillis();
					log.debug("handleUnread forsendelse " + forsendelsesId + " med " + dispatcherName);
					Forsendelse forsendelse = forsendelsesArkiv.retrieve(forsendelsesId);
					handleUnread(forsendelse);
					log.debug("handleUnread forsendelse " + forsendelsesId + " handled");
					serviceDelegate.markMessageHandleUnreadCompleted(forsendelsesId);

					long end = System.currentTimeMillis();
					akkumulertDispatchTid += (end - start);
					antallSendteForsendelser++;
					double gjennomsnittMsPrForsendelse = (double) akkumulertDispatchTid / (double) antallSendteForsendelser;

					int maksAntallForsendelserPrMinutt = dispatchPolicy.getMaxDispatchRate();
					if (maksAntallForsendelserPrMinutt > 0) { // 0 == no delay
						double minimumAntallMsPrForsendelse = (double) (60 * 1000) / (double) maksAntallForsendelserPrMinutt;
						long delay = (long) (minimumAntallMsPrForsendelse - gjennomsnittMsPrForsendelse);
						log.debug("handleAllUnread delay: " + delay);

						if (delay > 0) {
							try {
								Thread.sleep(delay);
							} catch (InterruptedException ie) {
								log.warn("Thread sleep interrupted");
							}
						}
					}
				} catch (RuntimeException e) {
					log.error("En feil oppstod ved håndtering av ulest forsendelse " + forsendelsesId, e);
					serviceDelegate.markMessageHandleUnreadFailed(forsendelsesId, e.getMessage());
				}
			}
		} else
			log.info("Uleste forsendelser ikke håndtert, utenfor åpningstid");

		log.debug("handleAllUnread from " + dispatcherName + " ferdig");
	}

	public boolean handlesShipmentPolicy(ShipmentPolicy sp) {
		List<DispatchPolicyShipmentParams> params = getDispatchPolicy().getShipmentParams();
		for (DispatchPolicyShipmentParams param : params)
			if (param.getShipmentPolicy().equals(sp))
				return true;

		return false;
	}
}
