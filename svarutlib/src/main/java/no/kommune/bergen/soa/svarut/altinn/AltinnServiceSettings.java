package no.kommune.bergen.soa.svarut.altinn;

public interface AltinnServiceSettings {
	String getEndpoint();
	void setEndpoint( String endpoint );
	String getServiceEdition();
	void setServiceEdition( String serviceEdition );
}
