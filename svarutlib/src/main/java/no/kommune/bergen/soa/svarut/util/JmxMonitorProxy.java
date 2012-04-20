package no.kommune.bergen.soa.svarut.util;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import no.kommune.bergen.soa.svarut.ServiceDelegate;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;
import no.kommune.bergen.soa.svarut.domain.JuridiskEnhet;
import no.kommune.bergen.soa.util.OppsynJmx;

import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedOperationParameter;
import org.springframework.jmx.export.annotation.ManagedOperationParameters;
import org.springframework.jmx.export.annotation.ManagedResource;

/**
 * JMX Monitor for general purpose usage. The invoke() method is meant to be promoted as an MBean by Spring configuration and
 * invoked via Spring AOP.
 *
 * @author einar
 */
@ManagedResource(objectName = "bean:name=JmxMonitorProxy", description = "Svarbrev monitor", log = true, logFile = "jmx.log", currencyTimeLimit = 15, persistPolicy = "OnUpdate", persistPeriod = 200, persistLocation = "jmx", persistName = "SvarbrevJmxMonitorProxy")
public class JmxMonitorProxy implements ServiceDelegate, OppsynJmx {

	private final ServiceDelegate serviceDelegate;
	final InvocationRecord dispatchIr = new InvocationRecord( "dispatch" );
	final InvocationRecord printUnreadIr = new InvocationRecord( "printUnread" );
	private final InvocationRecord printIr = new InvocationRecord( "print" );
	private final InvocationRecord removeOldIr = new InvocationRecord( "removeOld" );
	private final InvocationRecord retrieveIr = new InvocationRecord( "retrieve" );
	private final InvocationRecord retrieveContentIr = new InvocationRecord( "retrieveContent" );
	private final InvocationRecord retrieveContentNoAuthorizationIr = new InvocationRecord( "retrieveContentNoAuthorization" );
	private final InvocationRecord retrieveForPersonIr = new InvocationRecord( "retrieveForPerson" );
	private final InvocationRecord sendIr = new InvocationRecord( "send" );
	private final InvocationRecord[] invocationRecords = { dispatchIr, printUnreadIr, printIr, removeOldIr, retrieveIr, retrieveContentIr, sendIr, retrieveForPersonIr };

	public JmxMonitorProxy( ServiceDelegate serviceDelegate ) {
		this.serviceDelegate = serviceDelegate;
		dispatchIr.addPolicy( new TimeLimitBetweenInvocations( 1000 * 60 * 60 * 13 ) ); // 13 hours
		printUnreadIr.addPolicy( new TimeLimitBetweenInvocations( 1000 * 60 * 60 * 16 ) ); // 16 hours
		removeOldIr.addPolicy( new TimeLimitBetweenInvocations( 1000 * 60 * 60 * 24 * 15 ) ); // 15 days

		dispatchIr.addPolicy( new RecurringFailureLimit( 10 ) );
		printIr.addPolicy( new RecurringFailureLimit( 3 ) );
		removeOldIr.addPolicy( new RecurringFailureLimit( 30 ) );
		sendIr.addPolicy( new RecurringFailureLimit( 3 ) );

		//retrieveIr.addPolicy( new MaxAcceptableFailureRate( .10 ) ); // Denne forstyrrer HpOpenview overvåkingen
		retrieveContentIr.addPolicy( new MaxAcceptableFailureRate( .10 ) );
		retrieveForPersonIr.addPolicy( new MaxAcceptableFailureRate( .10 ) );
		sendIr.addPolicy( new MaxAcceptableFailureRate( .02 ) );
	}

	@ManagedOperation(description = "Health check of all entries")
	public String reportOperationalPolicyViolations() {
		StringBuilder sb = new StringBuilder();
		for (InvocationRecord ir : invocationRecords) {
			sb.append( ir.reportPolicyViolations() );
		}
		if (0 == sb.length()) sb.append( "none" );
		return sb.toString();
	}

	@Override
	@ManagedOperation(description = "Healt check for Oppsyn")
	public String healthCheck() {
		StringBuilder sb = new StringBuilder();
		for (InvocationRecord ir : invocationRecords) {
			sb.append( ir.reportPolicyViolations() );
		}
		if (0 == sb.length()) {
			return "NORMAL:";
		} else {
			return "WARNING:" + sb.toString();
		}
	}

	@ManagedOperation(description = "Produces an invocation report")
	public String invocationReport() {
		return InvocationRecord.reportAsHtml( invocationRecords );
	}

	@ManagedOperation(description = "Rapport fra forsendelses arkiv")
	public String statisticsReport() {
		String str = serviceDelegate.statistics();
		return str.replace( ',', '\n' );
	}

	@ManagedOperation(description = "Retrieves row from forsendelsesakriv")
	@ManagedOperationParameters( { @ManagedOperationParameter(name = "forsendelsesId", description = "forsendelsesId") })
	public String retrieveForsendelsesStatus( String forsendelsesId ) {
		String str = retrieveStatus( forsendelsesId );
		return str.replace( ',', '\n' );
	}

	@ManagedOperation(description = "Set forsendelsesakriv.stoppet = sysdate. Will prevent further processing on this entry.")
	@ManagedOperationParameters( { @ManagedOperationParameter(name = "forsendelsesId", description = "forsendelsesId") })
	public String stopForsendesle( String forsendelsesId ) {
		stop( forsendelsesId );
		return String.format( "Further processing on %s is stopped", forsendelsesId );
	}

	@ManagedOperation(description = "Send this entry to print.")
	@ManagedOperationParameters( { @ManagedOperationParameter(name = "forsendelsesId", description = "forsendelsesId") })
	public String printForsendesle( String forsendelsesId ) {
		print( forsendelsesId );
		return retrieveForsendelsesStatus( forsendelsesId );
	}

	@Override
	public void confirm( String id ) {
		serviceDelegate.confirm( id );
	}

	@Override
	public void dispatch() {
		try {
			serviceDelegate.dispatch();
			dispatchIr.recordSuccess();
		} catch (RuntimeException e) {
			dispatchIr.recordException( e.getMessage() );
			throw e;
		}
	}

	@Override
	public String getUrl( JuridiskEnhet juridiskEnhet, String id ) {
		return serviceDelegate.getUrl( juridiskEnhet, id );
	}

	@Override
	public void print( String id ) {
		try {
			serviceDelegate.print( id );
			printIr.recordSuccess();
		} catch (RuntimeException e) {
			printIr.recordException( e.getMessage() );
			throw e;
		}
	}

	@Override
	public void printUnread() {
		try {
			serviceDelegate.printUnread();
			printUnreadIr.recordSuccess();
		} catch (RuntimeException e) {
			printUnreadIr.recordException( e.getMessage() );
			throw e;
		}
	}

	@Override
	public void removeOld() {
		try {
			serviceDelegate.removeOld();
			removeOldIr.recordSuccess();
		} catch (RuntimeException e) {
			removeOldIr.recordException( e.getMessage() );
			throw e;
		}
	}

	@Override
	public void remove( String id ) {
		serviceDelegate.remove(id);
	}

	@Override
	public Forsendelse retrieve( String id, JuridiskEnhet juridiskEnhet ) {
		try {
			Forsendelse f = serviceDelegate.retrieve( id, juridiskEnhet );
			retrieveIr.recordSuccess();
			return f;
		} catch (RuntimeException e) {
			retrieveIr.recordException( e.getMessage() );
			throw e;
		}
	}

	@Override
	public InputStream retrieveContent( String id, JuridiskEnhet juridiskEnhet ) {
		try {
			InputStream is = serviceDelegate.retrieveContent( id, juridiskEnhet );
			retrieveContentIr.recordSuccess();
			return is;
		} catch (RuntimeException e) {
			retrieveContentIr.recordException( e.getMessage() );
			throw e;
		}
	}

	@Override
	public InputStream retrieveContentNoAuthorization( String id ) {
		try {
			InputStream is = serviceDelegate.retrieveContentNoAuthorization( id );
			retrieveContentNoAuthorizationIr.recordSuccess();
			return is;
		} catch (RuntimeException e) {
			retrieveContentNoAuthorizationIr.recordException( e.getMessage() );
			throw e;
		}
	}

	@Override
	public String retrieveStatus( String id ) {
		return serviceDelegate.retrieveStatus( id );
	}

	@Override
	public String send( Forsendelse forsendelse, byte[] content ) {
		try {
			String id = serviceDelegate.send( forsendelse, content );
			sendIr.recordSuccess();
			return id;
		} catch (RuntimeException e) {
			sendIr.recordException( e.getMessage() );
			throw e;
		}
	}

	@Override
	public String send( Forsendelse forsendelse, InputStream inputStream ) {
		try {
			String id = serviceDelegate.send( forsendelse, inputStream );
			sendIr.recordSuccess();
			return id;
		} catch (RuntimeException e) {
			sendIr.recordException( e.getMessage() );
			throw e;
		}
	}

	@Override
	public void setUnread( String id ) {
		serviceDelegate.setUnread( id );
	}

	@Override
	public void stop( String id ) {
		serviceDelegate.stop( id );
	}

	@Override
	public String statistics() {
		return serviceDelegate.statistics();
	}

	@Override
	public List<Forsendelse> retrieveList( JuridiskEnhet juridiskEnhet ) {
		try {
			List<Forsendelse> f = serviceDelegate.retrieveList( juridiskEnhet );
			retrieveForPersonIr.recordSuccess();
			return f;
		} catch (RuntimeException e) {
			retrieveIr.recordException( e.getMessage() );
			throw e;
		}
	}

	@Override
	public List<Forsendelse> retrieveStatus( String[] ids ) {
		return serviceDelegate.retrieveStatus( ids );
	}

	@Override
	public List<Forsendelse> retrieveStatus( Date fromAndIncluding, Date toNotIncluding ) {
		return serviceDelegate.retrieveStatus( fromAndIncluding, toNotIncluding );
	}

	@Override
	public List<Forsendelse> retrieveIgnored() {
		return serviceDelegate.retrieveIgnored();
	}

	@Override
	public void importPrintStatements() {
		serviceDelegate.importPrintStatements();
	}

	@Override
	public void updateSentToPrint(String forsendelsesId, Date sentPrintDate){
		serviceDelegate.updateSentToPrint(forsendelsesId, sentPrintDate);
	}

	@Override
	public List<String> retrieveFailedToPrint() {
		return serviceDelegate.retrieveFailedToPrint();
	}

	@Override
	public void markMessageSendt( String forsendelsesId ) {
		dispatchIr.recordSuccess();
		serviceDelegate.markMessageSendt( forsendelsesId );
	}

	@Override
	public void markMessageSendFailed( String forsendelsesId, String msg ) {
		dispatchIr.recordException( msg );
		serviceDelegate.markMessageSendFailed( forsendelsesId, msg );
	}

	@Override
	public void markMessageHandleUnreadCompleted( String forsendelsesId ) {
		printIr.recordSuccess();
		serviceDelegate.markMessageHandleUnreadCompleted( forsendelsesId );
	}

	@Override
	public void markMessageHandleUnreadFailed( String forsendelsesId, String msg ) {
		printIr.recordException( msg );
		serviceDelegate.markMessageHandleUnreadFailed( forsendelsesId, msg );
	}

	@Override
	public void markSendAlleForsendelserCalled() {
		printIr.recordSuccess();
		serviceDelegate.markSendAlleForsendelserCalled();
	}
}
