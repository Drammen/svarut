package no.kommune.bergen.soa.common.jms;

public class PoisonMessageException extends Exception {
	private static final long serialVersionUID = 1L;

	public PoisonMessageException( String message ) {
		super( message );
	}

	public PoisonMessageException( String message, Throwable t ) {
		super( message, t );
	}
}
