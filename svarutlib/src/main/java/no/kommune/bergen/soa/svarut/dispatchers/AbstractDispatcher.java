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

import org.apache.log4j.Logger;

public abstract class AbstractDispatcher implements Dispatcher {

	final Logger logger = Logger.getLogger( AbstractDispatcher.class );

	protected DispatchPolicy dispatchPolicy;
	protected ForsendelsesArkiv forsendelsesArkiv;
	protected ServiceDelegate serviceDelegate;

	protected AbstractDispatcher() {
		dispatchPolicy = new DispatchPolicy();
	}

	protected AbstractDispatcher( ServiceDelegate serviceDelegate, DispatchPolicy dispatchPolicy, ForsendelsesArkiv forsendelsesArkiv ) {
		this.dispatchPolicy = dispatchPolicy;
		this.forsendelsesArkiv = forsendelsesArkiv;
		this.serviceDelegate = serviceDelegate;
	}

	public DispatchPolicy getDispatchPolicy() {
		return dispatchPolicy;
	}

	public abstract void verify( Forsendelse f );

	public abstract void send( Forsendelse f );

	public abstract void handleUnread( Forsendelse f );

	public void sendAlleForsendelser() {

		final String dispatcherName = getClass().getName();

		if (dispatchPolicy.isDispatchWindowOpen()) {

			logger.debug( "sendAlleForsendelser from " + getClass().getName() );

			final List<String> forsendelser = forsendelsesArkiv.readUnsent( dispatchPolicy.getShipmentPolicies() );
			final int antallForsendelser = forsendelser.size();
			logger.debug( "Antall forsendelser:" + antallForsendelser + " hentet for " + dispatcherName );

			long akkumulertDispatchTid = 0;
			int antallSendteForsendelser = 0;
			String shipmentPolicy = "<>";

			for (String forsendelsesId : forsendelser) {
				try {
					long start = System.currentTimeMillis();
					Forsendelse forsendelse = forsendelsesArkiv.retrieve( forsendelsesId );
					logger.debug( "Sender forsendelse " + forsendelsesId + " med " + dispatcherName );
					shipmentPolicy = forsendelse.getShipmentPolicy();

					try {
						send( forsendelse );
					} catch (Exception e) {
						Date nesteforsok = new Date( new Date().getTime() + getNesteForsok() ); // vent en time
						forsendelsesArkiv.markForsendelseFailed( forsendelse, nesteforsok );
						throw new RuntimeException( e );
					}

					serviceDelegate.markMessageSendt( forsendelsesId );
					logger.debug( "forsendelse " + forsendelsesId + " sendt" );

					long end = System.currentTimeMillis();
					akkumulertDispatchTid += (end - start);
					antallSendteForsendelser++;
					double gjennomsnittMsPrForsendelse = (double)akkumulertDispatchTid / (double)antallSendteForsendelser;

					int maksAntallForsendelserPrMinutt = dispatchPolicy.getMaxDispatchRate();
					if (maksAntallForsendelserPrMinutt > 0) { // 0 == no delay
						double minimumAntallMsPrForsendelse = (double)(60 * 1000) / (double)maksAntallForsendelserPrMinutt;
						long delay = (long)(minimumAntallMsPrForsendelse - gjennomsnittMsPrForsendelse);
						logger.debug( "sendAlleForsendelser delay: " + delay );

						if (delay > 0) {
							try {
								Thread.sleep( delay );
							} catch (InterruptedException ie) {
								logger.warn( "Thread sleep interrupted" );
							}
						}
					}
				} catch (Exception e) {
					logger.error( "En feil oppstod ved sending av forsendelse " + forsendelsesId + " med dispatcher " + dispatcherName + " og ShipmentPolicy " + shipmentPolicy, e );
					serviceDelegate.markMessageSendFailed( forsendelsesId, e.getMessage() );
				}
			}
		} else {
			logger.info( "Forsendelser ikke sendt, utenfor åpningstid" );
		}
		serviceDelegate.markSendAlleForsendelserCalled();
		logger.debug( "sendAlleForsendelser from " + dispatcherName + " ferdig" );
	}

	protected long getNesteForsok() {
		return 1000 * 60 * 60;
	}

	public void handleAllUnread() {
		final List<String> forsendelser = forsendelsesArkiv.retrieveYoungerThan( getDispatchPolicy().getPrintWindowAgeIndays(), dispatchPolicy.getShipmentPolicies() );
		final String dispatcherName = getClass().getName();
		logger.debug( "Antall uleste forsendelser:" + forsendelser.size() + " hentet for " + dispatcherName );

		if (dispatchPolicy.isDispatchWindowOpen()) {

			long akkumulertDispatchTid = 0;
			int antallSendteForsendelser = 0;
			for (String forsendelsesId : forsendelser) {
				try {
					long start = System.currentTimeMillis();
					logger.debug( "handleUnread forsendelse " + forsendelsesId + " med " + dispatcherName );
					Forsendelse forsendelse = forsendelsesArkiv.retrieve( forsendelsesId );
					handleUnread( forsendelse );
					logger.debug( "handleUnread forsendelse " + forsendelsesId + " handled" );
					serviceDelegate.markMessageHandleUnreadCompleted( forsendelsesId );

					long end = System.currentTimeMillis();
					akkumulertDispatchTid += (end - start);
					antallSendteForsendelser++;
					double gjennomsnittMsPrForsendelse = (double)akkumulertDispatchTid / (double)antallSendteForsendelser;

					int maksAntallForsendelserPrMinutt = dispatchPolicy.getMaxDispatchRate();
					if (maksAntallForsendelserPrMinutt > 0) { // 0 == no delay
						double minimumAntallMsPrForsendelse = (double)(60 * 1000) / (double)maksAntallForsendelserPrMinutt;
						long delay = (long)(minimumAntallMsPrForsendelse - gjennomsnittMsPrForsendelse);
						logger.debug( "handleAllUnread delay: " + delay );

						if (delay > 0) {
							try {
								Thread.sleep( delay );
							} catch (InterruptedException ie) {
								logger.warn( "Thread sleep interrupted" );
							}
						}
					}
				} catch (RuntimeException e) {
					logger.error( "En feil oppstod ved håndtering av ulest forsendelse " + forsendelsesId, e );
					serviceDelegate.markMessageHandleUnreadFailed( forsendelsesId, e.getMessage() );
				}
			}
		} else {
			logger.info( "Uleste forsendelser ikke håndtert, utenfor åpningstid" );
		}
		logger.debug( "handleAllUnread from " + dispatcherName + " ferdig" );

	}

	public boolean handlesShipmentPolicy( ShipmentPolicy sp ) {
		List<DispatchPolicyShipmentParams> params = getDispatchPolicy().getShipmentParams();
		for (DispatchPolicyShipmentParams param : params) {
			if (param.getShipmentPolicy().equals( sp )) return true;
		}
		return false;
	}

}
