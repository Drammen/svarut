package no.kommune.bergen.soa.svarut;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

import no.kommune.bergen.soa.common.exception.UserException;
import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkiv;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;
import no.kommune.bergen.soa.svarut.domain.JuridiskEnhet;
import no.kommune.bergen.soa.svarut.domain.PrintReceipt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Main business logic entry point */
public class ServiceDelegateImpl implements ServiceDelegate {

	private static final Logger logger = LoggerFactory.getLogger(ServiceDelegateImpl.class);

	final ServiceContext serviceContext;
	private final ForsendelsesArkiv forsendelsesArkiv;
	private final DispatcherFactory dispatcherFactory;
	private int retirementAgeInDays = 360 * 10;
	private final PrintFacade printFacade;

	public ServiceDelegateImpl( ServiceContext serviceContext, DispatchRateConfig rateConfig ) {
		logger.debug("serviceContext: {}", serviceContext);
		this.serviceContext = serviceContext;
		forsendelsesArkiv = serviceContext.getForsendelsesArkiv();
		dispatcherFactory = new DispatcherFactory( this, serviceContext, rateConfig );
		retirementAgeInDays = serviceContext.getArchiveContext().getRetirementAgeInDays();
		this.printFacade = serviceContext.getPrintFacade();
	}

	/** Aksepterer en forsendelse og lagrer den i forsendelsesarkivet. Returnerer forsendelsesId */
	@Override
	public String send( Forsendelse forsendelse, byte[] content ) {
		logger.debug("Sending forsendelse : Tittel {} email {}", forsendelse.getTittel(), forsendelse.getEmail());
		if (content == null || content.length < 1)
			throw new UserException( "Document content is empty" );
		dispatcherFactory.getDispatcher(forsendelse); // Verify that we have a dispatcher for this
		String forsendelsesId = forsendelsesArkiv.save( forsendelse, new ByteArrayInputStream( content ) );
		logger.info("Successfully saved. Id={}, Name={}", forsendelsesId, forsendelse.getNavn());
		return forsendelsesId;
	}

	/** Aksepterer en forsendelse og lagrer den i forsendelsesarkivet. Returnerer forsendelsesId */
	@Override
	public String send( Forsendelse forsendelse, InputStream inputStream ) {
		logger.debug("Sending forsendelse : Tittel {} email {}", forsendelse.getTittel(), forsendelse.getEmail());
		dispatcherFactory.getDispatcher(forsendelse); // Verify that we have a dispatcher for this
		String forsendelsesId = forsendelsesArkiv.save( forsendelse, inputStream );
		logger.info("Successfully saved. Id={}, Name={}", forsendelsesId, forsendelse.getNavn());
		return forsendelsesId;
	}

	/** Returnerer en Forsendelse angitt ved forsendelsesId og som tilhører en gitt JuridiskEnhet */
	@Override
	public Forsendelse retrieve( String id, JuridiskEnhet juridiskEnhet ) {
		forsendelsesArkiv.authorize( id, juridiskEnhet );
		return forsendelsesArkiv.retrieve( id );
	}

	/** Returnerer en Forsendelse angitt ved forsendelsesId og som tilhÃ¸rer et gitt fodselsNr.
	 *  Hvis forsendelsen er knyttet til et orgnr gjÃ¸res det en autoriseringssjekk mot Altinn pÃ¥ fÃ¸dselsnr.
	 *  */
	@Override
	public Forsendelse retrieve( String id, String fodselsNr ) {
		forsendelsesArkiv.authorize( id, fodselsNr );
		return forsendelsesArkiv.retrieve( id );
	}


	/** Returnerer Forsendelser som tilhører en gitt JuridiskEnhet */
	@Override
	public List<Forsendelse> retrieveList( JuridiskEnhet juridiskEnhet ) {
		return forsendelsesArkiv.retrieveList( juridiskEnhet );
	}

	/** Returnerer dokument-innhold for en forsendelse angitt ved forsendelsesId og som tilhører en gitt JuridiskEnhet.
	 *  */
	@Override
	public InputStream retrieveContent( String id, JuridiskEnhet juridiskEnhet ) {
		forsendelsesArkiv.authorize( id, juridiskEnhet );
		return forsendelsesArkiv.retrieveContent( id );
	}

	/** Returnerer dokument-innhold for en forsendelse angitt ved forsendelsesId og som tilhÃ¸rer en gitt JuridiskEnhet.
	 *  Hvis forsendelsen er knyttet til et orgnr gjÃ¸res det en autoriseringssjekk mot Altinn pÃ¥ fÃ¸dselsnr.
	 *  */
	@Override
	public InputStream retrieveContent( String id, String fodselsNr ) {
		forsendelsesArkiv.authorize( id, fodselsNr );
		return forsendelsesArkiv.retrieveContent( id );
	}

	/** Returnerer dokument-innholde for en forsendelse angitt ved forsendelsesId */
	@Override
	public InputStream retrieveContentNoAuthorization( String id ) {
		return forsendelsesArkiv.retrieveContent( id );
	}

	/** Sender en gitt forsendelse til PrintServiceProvider */
	@Override
	public void print( String id ) {
		Forsendelse f = forsendelsesArkiv.retrieve( id );
		PrintReceipt printReceipt = printFacade.print( f );
		if (f.getUtskrevet() == null) {
			forsendelsesArkiv.setPrinted( f.getId(), printReceipt );
		}
	}

	/** Henter status for nye post-forsendeser fra PrintServiceProvider */
	@Override
	public void importPrintStatements() {
		this.printFacade.importPrintStatements( forsendelsesArkiv );
	}

	/** Returner en Forsendelse som en komma-separert list med key-value-pairs */
	@Override
	public String retrieveStatus( String id ) {
		return toString( forsendelsesArkiv.retrieveRow( id ) );
	}

	@Override
	public List<Forsendelse> retrieveStatus( String[] ids ) {
		return forsendelsesArkiv.retrieveRows( ids );
	}

	@Override
	public List<Forsendelse> retrieveStatus( Date fromAndIncluding, Date toNotIncluding ) {
		return forsendelsesArkiv.retrieveRows( fromAndIncluding, toNotIncluding );
	}

	/** Returner statistikk fra forsendelsesarkivet som en komma-separert list med key-value-pairs */
	@Override
	public String statistics() {
		return toString( forsendelsesArkiv.statistics() );
	}

	/** Bekrefter at forsendelsesdokumentet er å betrakte som lest elektronisk */
	@Override
	public void confirm( String id ) { //TODO Forsendelsesdokument skal foreløpig ikke settes til lest hvis org
		forsendelsesArkiv.confirm( id );
	}

	/** Reset bekreftelse på at forsendelsesdokumentet er lest elektronisk */
	@Override
	public void setUnread( String id ) {
		forsendelsesArkiv.setUnread( id );
	}

	/** Stopp all videre behandling av denne forsendelsen */
	@Override
	public void stop( String id ) {
		forsendelsesArkiv.stop( id );
	}

	/** Returnerer url for å laste ned dokumentet tilhørende en forsendelse */
	@Override
	public String getUrl( JuridiskEnhet juridiskEnhet, String id ) {
		return this.serviceContext.getVelocityModelFactory().getUrl( juridiskEnhet, id );
	}

	@Override
	public void dispatch() {
		logger.debug( "Dispatch() start" );
		List<Dispatcher> dispatchers = dispatcherFactory.getAllDispatchers();
		for (Dispatcher dispatcher : dispatchers) {
			dispatcher.sendAlleForsendelser();
		}
		logger.debug( "Dispatch() end" );
	}

	@Override
	public void printUnread() {
		logger.debug( "printUnread() start" );
		List<Dispatcher> dispatchers = dispatcherFactory.getAllDispatchers();
		for (Dispatcher dispatcher : dispatchers) {
			dispatcher.handleAllUnread();
		}
		logger.debug( "printUnread() end" );
	}

	/** Fjerner forsendelser som er foreldet fra arkivet */
	@Override
	public void removeOld() {
		logger.debug("removeOld() start, retirementAgeInDays={}", retirementAgeInDays);
		forsendelsesArkiv.removeOlderThan(retirementAgeInDays);
		forsendelsesArkiv.removeUnreachable( 365 * 10 );
		this.serviceContext.getPdfGenerator().removeOldTempFiles( 1000 * 60 * 60 * 2 );
		logger.debug( "removeOld() end" );
	}

	/** Fjerner en forsendelser fra arkivet */
	@Override
	public void remove( String id ) {
		logger.debug( "remove() called" );
		forsendelsesArkiv.remove( id );
	}

	/** Sjekk om det finnes forsendelser som mot formodning ikke er blitt behandlet av dispatch() */
	@Override
	public List<Forsendelse> retrieveIgnored() {
		return forsendelsesArkiv.retrieveIgnored();
	}

	/** Returns a Map as a comma separated list of key value pairs */
	@SuppressWarnings("JavaDoc")
	public static String toString( Map<?, ?> map ) {
		StringBuilder sb = new StringBuilder();
		for (Map.Entry<?, ?> entry : map.entrySet()) {
			Object key = entry.getKey();
			if ("FNR".equals( key )) continue;
			Object value = entry.getValue();
			sb.append( key ).append( "=" ).append( value ).append( ", " );
		}
		return sb.toString();
	}

	public static UserException handleException( Exception e ) {
		logger.error( e.getMessage(), e );
		return (e instanceof UserException) ? (UserException) e : new UserException( "MSG_TECHNICAL_ISSUE_TRY_AGAIN_LATER", e );
	}

	/** Levetid for forsendelser i arkivet */
	@SuppressWarnings("JavaDoc")
	public void setRetirementAgeInDays(int retirementAgeInDays) {
		this.retirementAgeInDays = retirementAgeInDays;
	}

	@Override
	public void updateSentToPrint(String forsendelsesId, Date sentPrintDate){
		forsendelsesArkiv.updateSentToPrint(forsendelsesId, sentPrintDate);
	}

	/** Sjekk om det finnes forsendelser som mot formodning ikke er blitt behandlet av PrintServiceProvider */
	@Override
	public List<String> retrieveFailedToPrint() {
		return forsendelsesArkiv.retrieveFailedToPrint();
	}

	@Override
	public void markMessageSendt( String forsendelsesId ) {
		logger.debug("Forsendelse {} successfully sendt", forsendelsesId);
	}

	@Override
	public void markMessageSendFailed( String forsendelsesId, String msg ) {
		logger.debug("Forsendelse {} failed: {}", forsendelsesId, msg);
	}

	@Override
	public void markMessageHandleUnreadCompleted( String forsendelsesId ) {
		logger.debug("Ulest forsendelse {} successfully handled", forsendelsesId);
	}

	@Override
	public void markMessageHandleUnreadFailed( String forsendelsesId, String msg ) {
		logger.debug("Ulest forsendelse {} failed: {}", forsendelsesId, msg);
	}

	@Override
	public void markSendAlleForsendelserCalled() {
		logger.debug( "markSendAlleForsendelserCalled()" );
	}
}
