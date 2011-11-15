package no.kommune.bergen.soa.svarut.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Forsendelse", propOrder = {"fodselsnummer", "navn", "orgnr", "adresse", "avsenderNavn", "avsenderadresse", "tittel", "meldingstekst", "appid", "forsendelsesMate", "epost", "replyTo", "fargePrint", "ansvarsSted"})
public class Forsendelse {

	@XmlElement(required = true)
	protected String fodselsnummer;
	protected String navn;
	protected int orgnr;
	@XmlElement(required = true)
	protected Adresse123 adresse;
	@XmlElement(name = "avsender-navn")
	protected String avsenderNavn;
	@XmlElement(required = true)
	protected Adresse123 avsenderadresse;
	protected String tittel;
	@XmlElement(required = true)
	protected String meldingstekst;
	protected String appid;
	protected ShipmentPolicy forsendelsesMate;
	@XmlElement(required = true)
	protected String epost;
	@XmlElement(required = true)
	protected String replyTo;
	@XmlElement(defaultValue = "false")
	protected boolean fargePrint;
	protected String ansvarsSted;


	public String getAnsvarsSted() {
		return ansvarsSted;
	}

	public void setAnsvarsSted(String ansvarsSted) {
		this.ansvarsSted = ansvarsSted;
	}



	/**
	 * Gets the value of the fodselsnummer property.
	 *
	 * @return possible object is {@link String }
	 */
	public String getFodselsnummer() {
		return fodselsnummer;
	}

	/**
	 * Sets the value of the fodselsnummer property.
	 *
	 * @param value allowed object is {@link String }
	 */
	public void setFodselsnummer(String value) {
		this.fodselsnummer = value;
	}

	/**
	 * Gets the value of the navn property.
	 *
	 * @return possible object is {@link String }
	 */
	public String getNavn() {
		return navn;
	}

	/**
	 * Sets the value of the navn property.
	 *
	 * @param value allowed object is {@link String }
	 */
	public void setNavn(String value) {
		this.navn = value;
	}

	/**
	 * Gets the value of the orgnr property.
	 */
	public int getOrgnr() {
		return orgnr;
	}

	/**
	 * Sets the value of the orgnr property.
	 */
	public void setOrgnr(int value) {
		this.orgnr = value;
	}

	/**
	 * Gets the value of the adresse property.
	 *
	 * @return possible object is {@link Adresse123 }
	 */
	public Adresse123 getAdresse() {
		return adresse;
	}

	/**
	 * Sets the value of the adresse property.
	 *
	 * @param value allowed object is {@link Adresse123 }
	 */
	public void setAdresse(Adresse123 value) {
		this.adresse = value;
	}

	/**
	 * Gets the value of the avsenderNavn property.
	 *
	 * @return possible object is {@link String }
	 */
	public String getAvsenderNavn() {
		return avsenderNavn;
	}

	/**
	 * Sets the value of the avsenderNavn property.
	 *
	 * @param value allowed object is {@link String }
	 */
	public void setAvsenderNavn(String value) {
		this.avsenderNavn = value;
	}

	/**
	 * Gets the value of the avsenderadresse property.
	 *
	 * @return possible object is {@link Adresse123 }
	 */
	public Adresse123 getAvsenderadresse() {
		return avsenderadresse;
	}

	/**
	 * Sets the value of the avsenderadresse property.
	 *
	 * @param value allowed object is {@link Adresse123 }
	 */
	public void setAvsenderadresse(Adresse123 value) {
		this.avsenderadresse = value;
	}

	/**
	 * Gets the value of the tittel property.
	 *
	 * @return possible object is {@link String }
	 */
	public String getTittel() {
		return tittel;
	}

	/**
	 * Sets the value of the tittel property.
	 *
	 * @param value allowed object is {@link String }
	 */
	public void setTittel(String value) {
		this.tittel = value;
	}

	/**
	 * Gets the value of the meldingstekst property.
	 *
	 * @return possible object is {@link String }
	 */
	public String getMeldingstekst() {
		return meldingstekst;
	}

	/**
	 * Sets the value of the meldingstekst property.
	 *
	 * @param value allowed object is {@link String }
	 */
	public void setMeldingstekst(String value) {
		this.meldingstekst = value;
	}

	/**
	 * Gets the value of the appid property.
	 *
	 * @return possible object is {@link String }
	 */
	public String getAppid() {
		return appid;
	}

	/**
	 * Sets the value of the appid property.
	 *
	 * @param value allowed object is {@link String }
	 */
	public void setAppid(String value) {
		this.appid = value;
	}

	/**
	 * Gets the value of the forsendelsesMate property.
	 *
	 * @return possible object is {@link ShipmentPolicy }
	 */
	public ShipmentPolicy getForsendelsesMate() {
		return forsendelsesMate;
	}

	/**
	 * Sets the value of the forsendelsesMate property.
	 *
	 * @param value allowed object is {@link ShipmentPolicy }
	 */
	public void setForsendelsesMate(ShipmentPolicy value) {
		this.forsendelsesMate = value;
	}

	/**
	 * Gets the value of the epost property.
	 *
	 * @return possible object is {@link String }
	 */
	public String getEpost() {
		return epost;
	}

	/**
	 * Sets the value of the epost property.
	 *
	 * @param value allowed object is {@link String }
	 */
	public void setEpost(String value) {
		this.epost = value;
	}

	/**
	 * Gets the value of the replyTo property.
	 *
	 * @return possible object is {@link String }
	 */
	public String getReplyTo() {
		return replyTo;
	}

	/**
	 * Sets the value of the replyTo property.
	 *
	 * @param value allowed object is {@link String }
	 */
	public void setReplyTo(String value) {
		this.replyTo = value;
	}

	/**
	 * Gets the value of the fargePrint property.
	 */
	public boolean isFargePrint() {
		return fargePrint;
	}

	/**
	 * Sets the value of the fargePrint property.
	 */
	public void setFargePrint(boolean value) {
		this.fargePrint = value;
	}

}
