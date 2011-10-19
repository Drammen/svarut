package no.kommune.bergen.soa.svarut.domain;

public class Fodselsnr implements JuridiskEnhet {
	private final String value;

	public Fodselsnr( String value ) {
		if (value.length() != 11) throw new IllegalArgumentException( "Fodselsnr skal være 11 siffer. " + value );
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	@Override
	public String toString() {
		return getValue();
	}

}
