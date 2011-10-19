package no.kommune.bergen.soa.svarut;

/**
 * Distribuerer
 * forsendelser
 */
public interface Dispatcher {

	void sendAlleForsendelser();

	void handleAllUnread();

}
