package no.kommune.bergen.soa.svarut.domain;

public class Orgnr implements JuridiskEnhet {
	private final String value;

	public Orgnr( String value ) {
		if (value.length() != 9) throw new IllegalArgumentException( "Orgnr skal være 9 siffer." );
		this.value = value;
	}

	@Override
	public String getValue() {
		return this.value;
	}

	@Override
	public String toString() {
		return getValue();
	}

}
