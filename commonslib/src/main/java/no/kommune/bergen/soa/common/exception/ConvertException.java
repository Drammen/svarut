package no.kommune.bergen.soa.common.exception;

/** Dataconvertion exceptions */
public class ConvertException extends RuntimeException {

	public ConvertException( Object input, Class<?> toClass, Exception e ) {
		super( "Cannot convert " + input + " to object of type " + toClass.getName(), e );
	}

	private static final long serialVersionUID = -7410937326732079759L;

}
