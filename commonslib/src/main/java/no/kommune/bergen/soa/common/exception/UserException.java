package no.kommune.bergen.soa.common.exception;

/** Exceptions to be reported back to the user. */
public class UserException extends RuntimeException {

	private static final long serialVersionUID = -8214271420630875710L;

	public UserException( String msg ) {
		super( msg );
	}

	public UserException( String msg, Throwable e ) {
		super( msg, e );
	}

}
