package no.kommune.bergen.soa.svarut.domain;

import java.io.File;
import java.util.Date;

/**
 * Domain object som inneholder destinasjon og avsender adresse hvordan det skal sendes, dokumentet som skal sendes samt
 * forsendelsesstatistikk
 */
public class Forsendelse {
	private String id, fnr, orgnr, navn, adresse1, adresse2, adresse3, postnr, poststed, land, avsenderNavn, avsenderAdresse1, avsenderAdresse2, avsenderAdresse3, avsenderPostnr, avsenderPoststed, tittel, meldingsTekst, appid, printId,
			shipmentPolicy, email, replyTo, sms;
	private File file;
	private Date sendt, lest, norgedotno, altinn, utskrevet, tidspunktPostlagt;
	private boolean printFarge;
	private int antallSider, antallSiderPostlagt;
    private Date nesteForsok;
	private String ansvarsSted;
	private String konteringkode;

	public boolean isPrintFarge() {
		return printFarge;
	}

	/** Hvis dokumentet sendes i poste, skal det da være i farger */
	public void setPrintFarge( boolean printFarge ) {
		this.printFarge = printFarge;
	}

	public String getId() {
		return id;
	}

	public void setId( String id ) {
		this.id = id;
	}

	public String getFnr() {
		return fnr;
	}

	/** Fødselsnr */
	public void setFnr( String fnr ) {
		this.fnr = fnr;
	}

	public void setOrgnr( String orgnr ) {
		this.orgnr = orgnr;
	}

	public String getOrgnr() {
		return orgnr;
	}

	public String getNavn() {
		return navn;
	}

	public void setNavn( String navn ) {
		this.navn = navn;
	}

	public String getAdresse1() {
		return adresse1;
	}

	public void setAdresse1( String adresse1 ) {
		this.adresse1 = adresse1;
	}

	public String getAdresse2() {
		return adresse2;
	}

	public void setAdresse2( String adresse2 ) {
		this.adresse2 = adresse2;
	}

	public String getAdresse3() {
		return adresse3;
	}

	public void setAdresse3( String adresse3 ) {
		this.adresse3 = adresse3;
	}

	public String getPostnr() {
		return postnr;
	}

	public void setPostnr( String postnr ) {
		this.postnr = postnr;
	}

	public String getPoststed() {
		return poststed;
	}

	public void setPoststed( String poststed ) {
		this.poststed = poststed;
	}

	public String getLand() {
		return land;
	}

	public void setLand( String land ) {
		this.land = land;
	}

	public String getTittel() {
		return tittel;
	}

	public void setTittel( String tittel ) {
		this.tittel = tittel;
	}

	public String getMeldingsTekst() {
		return meldingsTekst;
	}

	public void setMeldingsTekst( String meldingsTekst ) {
		this.meldingsTekst = meldingsTekst;
	}

	public File getFile() {
		return file;
	}

	/** Dokument som skal sendes */
	public void setFile( File file ) {
		this.file = file;
	}

	public void setAvsenderNavn( String avsenderNavn ) {
		this.avsenderNavn = avsenderNavn;
	}

	public String getAvsenderNavn() {
		return avsenderNavn;
	}

	public void setAvsenderAdresse1( String avsenderAdresse1 ) {
		this.avsenderAdresse1 = avsenderAdresse1;
	}

	public String getAvsenderAdresse1() {
		return avsenderAdresse1;
	}

	public void setAvsenderAdresse2( String avsenderAdresse2 ) {
		this.avsenderAdresse2 = avsenderAdresse2;
	}

	public String getAvsenderAdresse2() {
		return avsenderAdresse2;
	}

	public void setAvsenderAdresse3( String avsenderAdresse3 ) {
		this.avsenderAdresse3 = avsenderAdresse3;
	}

	public String getAvsenderAdresse3() {
		return avsenderAdresse3;
	}

	public void setAvsenderPostnr( String avsenderPostnr ) {
		this.avsenderPostnr = avsenderPostnr;
	}

	public String getAvsenderPostnr() {
		return avsenderPostnr;
	}

	public void setAvsenderPoststed( String avsenderPoststed ) {
		this.avsenderPoststed = avsenderPoststed;
	}

	public String getAvsenderPoststed() {
		return avsenderPoststed;
	}

	/** Brukes for å gruppere forsendelser i henhold til økoonomibegreper for fordeling av kostnader o.l */
	public void setAppid( String appid ) {
		this.appid = appid;
	}

	public String getAppid() {
		return appid;
	}

	/** PrintServiceProvider sin referanse til forsendelse */
	public void setPrintId( String printId ) {
		this.printId = printId;
	}

	public String getPrintId() {
		return printId;
	}

	public Date getLest() {
		return lest;
	}

	/** Lest elektronisk */
	public void setLest( Date lest ) {
		this.lest = lest;
	}

	public Date getNorgedotno() {
		return norgedotno;
	}

	/** Sendt til Minside postkasse i Norge.no */
	public void setNorgedotno( Date norgedotno ) {
		this.norgedotno = norgedotno;
	}

	public Date getUtskrevet() {
		return utskrevet;
	}

	/** Sendt til PrintServiceProvider */
	public void setUtskrevet( Date utskrevet ) {
		this.utskrevet = utskrevet;
	}

	public Date getSendt() {
		return sendt;
	}

	/** Tidspunkt da forsendesen ble sendt fra avsender og mottatt og akseptert av Svarut */
	public void setSendt( Date date ) {
		this.sendt = date;
	}

	public String getShipmentPolicy() {
		return this.shipmentPolicy;
	}

	/** Hvordan skal forsendelsen foregå */
	public void setShipmentPolicy( String shipmentPolicy ) {
		this.shipmentPolicy = shipmentPolicy;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail( String email ) {
		this.email = email;
	}

	@Override
	public String toString() {
		return String
				.format(
						"id=%s fnr=%s orgnr=%s navn=%s adresse1=%s adresse2=%s adresse3=%s postnr=%s poststed=%s land=%s avsenderNavn=%s avsenderAdresse1=%s avsenderAdresse2=%s avsenderAdresse3=%s avsenderPostnr=%s avsenderPoststed=%s tittel=%s meldingsTekst=%s appid=%s printId=%s shipmentPolicy=%s email=%s",
						id, fnr, orgnr, navn, adresse1, adresse2, adresse3, postnr, poststed, land, avsenderNavn, avsenderAdresse1, avsenderAdresse2, avsenderAdresse3, avsenderPostnr, avsenderPoststed, tittel, meldingsTekst, appid, printId,
						shipmentPolicy, email );
	}

	public String getReplyTo() {
		return replyTo;
	}

	public void setReplyTo( String replyTo ) {
		this.replyTo = replyTo;
	}

	public String getSms() {
		return sms;
	}

	public void setSms( String sms ) {
		this.sms = sms;
	}

	public Date getAltinn() {
		return altinn;
	}

	/** Sendt til Altinn postkasse */
	public void setAltinn( Date altinn ) {
		this.altinn = altinn;
	}

	public Date getTidspunktPostlagt() {
		return tidspunktPostlagt;
	}

	/** Sendt i posten */
	public void setTidspunktPostlagt( Date tidspunktPostlagt ) {
		this.tidspunktPostlagt = tidspunktPostlagt;
	}

	public int getAntallSider() {
		return antallSider;
	}

	public void setAntallSider( int antallSider ) {
		this.antallSider = antallSider;
	}

	public int getAntallSiderPostlagt() {
		return antallSiderPostlagt;
	}

	public void setAntallSiderPostlagt( int antallSiderPostlagt ) {
		this.antallSiderPostlagt = antallSiderPostlagt;
	}


    public Date getNesteForsok() {
        return nesteForsok;
    }


    public void setNesteForsok(Date nesteForsok) {
        this.nesteForsok = nesteForsok;
    }

	public void setAnsvarsSted(String ansvarsSted) {
		this.ansvarsSted = ansvarsSted;
	}

	public String getAnsvarsSted() {
		return ansvarsSted;
	}

	public void setKonteringkode(String konteringkode) {
		this.konteringkode = konteringkode;
	}

	public String getKonteringkode() {
		return konteringkode;
	}
}
