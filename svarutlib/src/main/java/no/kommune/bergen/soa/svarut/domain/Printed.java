package no.kommune.bergen.soa.svarut.domain;

import java.util.Date;

/** Status for print og postutsendelse for en forsendelse */
public class Printed {
	private int antallSiderPostlagt;
	private Date tidspunktPostlagt;
	private String forsendelsesId;

	public int getAntallSiderPostlagt() {
		return antallSiderPostlagt;
	}

	public void setAntallSiderPostlagt( int antallSiderPostlagt ) {
		this.antallSiderPostlagt = antallSiderPostlagt;
	}

	public Date getTidspunktPostlagt() {
		return tidspunktPostlagt;
	}

	public void setTidspunktPostlagt( Date tidspunktPostlagt ) {
		this.tidspunktPostlagt = tidspunktPostlagt;
	}

	public String getForsendelsesId() {
		return forsendelsesId;
	}

	public void setForsendelsesId( String forsendelsesId ) {
		this.forsendelsesId = forsendelsesId;
	}

	@Override
	public String toString() {
		return String.format( "forsendelsesId=%s, tidspunktPostlagt=%s, antallSiderPostlagt=%s", forsendelsesId, tidspunktPostlagt, antallSiderPostlagt );
	}
}
