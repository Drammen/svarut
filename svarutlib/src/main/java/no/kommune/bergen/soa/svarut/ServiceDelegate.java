package no.kommune.bergen.soa.svarut;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import no.kommune.bergen.soa.svarut.domain.Forsendelse;
import no.kommune.bergen.soa.svarut.domain.JuridiskEnhet;

/** Main service entry point */
public interface ServiceDelegate {
	String send( Forsendelse forsendelse, byte[] content );

	String send( Forsendelse forsendelse, InputStream inputStream );

	Forsendelse retrieve( String id, JuridiskEnhet juridiskEnhet );

	List<Forsendelse> retrieveList( JuridiskEnhet juridiskEnhet );

	InputStream retrieveContent( String id, JuridiskEnhet juridiskEnhet );

	InputStream retrieveContentNoAuthorization( String id );

	void print( String id );

	void importPrintStatements();

	String retrieveStatus( String id );

	List<Forsendelse> retrieveStatus( String[] ids );

	List<Forsendelse> retrieveStatus( Date fromAndIncluding, Date toNotIncluding );

	String statistics();

	void confirm( String id );

	void setUnread( String id );

	void stop( String id );

	String getUrl( JuridiskEnhet juridiskEnhet, String id );

	void dispatch();

	void printUnread();

	void removeOld();

	void remove( String id );

	List<Forsendelse> retrieveIgnored();

	void updateSentToPrint(String forsendelsesId, Date sentPrintDate);

	List<String> retrieveFailedToPrint();

	void markMessageSendt( String forsendelsesId );

	void markMessageSendFailed( String forsendelsesId, String msg );

	void markMessageHandleUnreadCompleted( String forsendelsesId );

	void markMessageHandleUnreadFailed( String forsendelsesId, String msg );

	void markSendAlleForsendelserCalled();

}
