package no.kommune.bergen.soa.svarut.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ForsendelseStatus", propOrder = {
		"id",
		"forsendelse",
		"lestElektronisk",
		"forsendelsesdato",
		"sendtBrevpost",
		"sendtNorgedotno",
		"printId",
		"antallSider",
    	"antallSiderPostlagt",
    	"sendtAltinn",
    	"konteringkode",
    	"tidspunktPostlagt",
		"antallSortHvitSider",
		"antallFargeSider",
		"antallArkKonvoluttertAutomatisk",
		"antallEkstraArkKonvoluttertAutomatisk",
		"antallArkKonvoluttertManuelt",
		"antallEkstraArkKonvoluttertManuelt",
		"vekt",
		"produksjonskostnader",
		"porto"
})
public class ForsendelseStatus {

	@XmlElement(required = true)
	protected String id;
	protected Forsendelse forsendelse;
	@XmlElement(name = "lest-elektronisk")
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar lestElektronisk;
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar forsendelsesdato;
	@XmlElement(name = "sendt-brevpost")
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar sendtBrevpost;
	@XmlElement(name = "sendt-norgedotno")
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar sendtNorgedotno;
	protected String printId;
	@XmlSchemaType(name = "dateTime")
	protected XMLGregorianCalendar tidspunktPostlagt;
	@XmlElement(name = "sendt-altinn")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar sendtAltinn;
	protected int antallSider;
	protected int antallSiderPostlagt;
	protected String konteringkode;
	protected int antallSortHvitSider;
	protected int antallFargeSider;
	protected int antallArkKonvoluttertAutomatisk;
	protected int antallEkstraArkKonvoluttertAutomatisk;
	protected int antallArkKonvoluttertManuelt;
	protected int antallEkstraArkKonvoluttertManuelt;
	protected int vekt;
	protected double produksjonskostnader;
	protected double porto;

	/**
	 * Gets the value of the id property.
	 *
	 * @return possible object is
	 *         {@link String }
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the value of the id property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setId(String value) {
		this.id = value;
	}

	/**
	 * Gets the value of the forsendelse property.
	 *
	 * @return possible object is
	 *         {@link Forsendelse }
	 */
	public Forsendelse getForsendelse() {
		return forsendelse;
	}

	/**
	 * Sets the value of the forsendelse property.
	 *
	 * @param value allowed object is
	 *              {@link Forsendelse }
	 */
	public void setForsendelse(Forsendelse value) {
		this.forsendelse = value;
	}

	/**
	 * Gets the value of the lestElektronisk property.
	 *
	 * @return possible object is
	 *         {@link XMLGregorianCalendar }
	 */
	public XMLGregorianCalendar getLestElektronisk() {
		return lestElektronisk;
	}

	/**
	 * Sets the value of the lestElektronisk property.
	 *
	 * @param value allowed object is
	 *              {@link XMLGregorianCalendar }
	 */
	public void setLestElektronisk(XMLGregorianCalendar value) {
		this.lestElektronisk = value;
	}

	/**
	 * Gets the value of the forsendelsesdato property.
	 *
	 * @return possible object is
	 *         {@link XMLGregorianCalendar }
	 */
	public XMLGregorianCalendar getForsendelsesdato() {
		return forsendelsesdato;
	}

	/**
	 * Sets the value of the forsendelsesdato property.
	 *
	 * @param value allowed object is
	 *              {@link XMLGregorianCalendar }
	 */
	public void setForsendelsesdato(XMLGregorianCalendar value) {
		this.forsendelsesdato = value;
	}

	/**
	 * Gets the value of the sendtBrevpost property.
	 *
	 * @return possible object is
	 *         {@link XMLGregorianCalendar }
	 */
	public XMLGregorianCalendar getSendtBrevpost() {
		return sendtBrevpost;
	}

	/**
	 * Sets the value of the sendtBrevpost property.
	 *
	 * @param value allowed object is
	 *              {@link XMLGregorianCalendar }
	 */
	public void setSendtBrevpost(XMLGregorianCalendar value) {
		this.sendtBrevpost = value;
	}

	/**
     * Gets the value of the sendtAlltinn property.
     *
     * @param value allowed object is
     *     			{@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getSendtAltinn() {
        return sendtAltinn;
    }

    /**
     * Sets the value of the sendtAlltinn property.
     *
     * @param value allowed object is
     *     			{@link XMLGregorianCalendar }
     *
     */
    public void setSendtAltinn(XMLGregorianCalendar value) {
        this.sendtAltinn = value;
    }

	/**
	 * Gets the value of the sendtNorgedotno property.
	 *
	 * @return possible object is
	 *         {@link XMLGregorianCalendar }
	 */
	public XMLGregorianCalendar getSendtNorgedotno() {
		return sendtNorgedotno;
	}

	/**
	 * Sets the value of the sendtNorgedotno property.
	 *
	 * @param value allowed object is
	 *              {@link XMLGregorianCalendar }
	 */
	public void setSendtNorgedotno(XMLGregorianCalendar value) {
		this.sendtNorgedotno = value;
	}

	/**
	 * Gets the value of the printId property.
	 *
	 * @return possible object is
	 *         {@link String }
	 */
	public String getPrintId() {
		return printId;
	}

	/**
	 * Sets the value of the printId property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setPrintId(String value) {
		this.printId = value;
	}

	/**
	 * Gets the value of the tidspunktPostlagt property.
	 *
	 * @return possible object is
	 *         {@link XMLGregorianCalendar }
	 */
	public XMLGregorianCalendar getTidspunktPostlagt() {
		return tidspunktPostlagt;
	}

	/**
	 * Sets the value of the tidspunktPostlagt property.
	 *
	 * @param value allowed object is
	 *              {@link XMLGregorianCalendar }
	 */
	public void setTidspunktPostlagt(XMLGregorianCalendar value) {
		this.tidspunktPostlagt = value;
	}

	/**
	 * Gets the value of the antallSider property.
	 */
	public int getAntallSider() {
		return antallSider;
	}

	/**
	 * Sets the value of the antallSider property.
	 */
	public void setAntallSider(int value) {
		this.antallSider = value;
	}

	/**
	 * Gets the value of the antallSiderPostlagt property.
	 */
	public int getAntallSiderPostlagt() {
		return antallSiderPostlagt;
	}

	/**
	 * Sets the value of the antallSiderPostlagt property.
	 */
	public void setAntallSiderPostlagt(int value) {
		this.antallSiderPostlagt = value;
	}

	/**
	 * Gets the value of the konteringkode property.
	 */
	public String getKonteringkode() {
		return konteringkode;
	}

	/**
	 * Sets the value of the konteringkode property.
	 */
	public void setKonteringkode(String konteringkode) {
		this.konteringkode = konteringkode;
	}

	public int getAntallSortHvitSider() {
		return antallSortHvitSider;
	}

	public void setAntallSortHvitSider(int antallSortHvitSider) {
		this.antallSortHvitSider = antallSortHvitSider;
	}

	public int getAntallFargeSider() {
		return antallFargeSider;
	}

	public void setAntallFargeSider(int antallFargeSider) {
		this.antallFargeSider = antallFargeSider;
	}

	public int getAntallArkKonvoluttertAutomatisk() {
		return antallArkKonvoluttertAutomatisk;
	}

	public void setAntallArkKonvoluttertAutomatisk(
			int antallArkKonvoluttertAutomatisk) {
		this.antallArkKonvoluttertAutomatisk = antallArkKonvoluttertAutomatisk;
	}

	public int getAntallEkstraArkKonvoluttertAutomatisk() {
		return antallEkstraArkKonvoluttertAutomatisk;
	}

	public void setAntallEkstraArkKonvoluttertAutomatisk(
			int antallEkstraArkKonvoluttertAutomatisk) {
		this.antallEkstraArkKonvoluttertAutomatisk = antallEkstraArkKonvoluttertAutomatisk;
	}

	public int getAntallArkKonvoluttertManuelt() {
		return antallArkKonvoluttertManuelt;
	}

	public void setAntallArkKonvoluttertManuelt(int antallArkKonvoluttertManuelt) {
		this.antallArkKonvoluttertManuelt = antallArkKonvoluttertManuelt;
	}

	public int getAntallEkstraArkKonvoluttertManuelt() {
		return antallEkstraArkKonvoluttertManuelt;
	}

	public void setAntallEkstraArkKonvoluttertManuelt(
			int antallEkstraArkKonvoluttertManuelt) {
		this.antallEkstraArkKonvoluttertManuelt = antallEkstraArkKonvoluttertManuelt;
	}

	public int getVekt() {
		return vekt;
	}

	public void setVekt(int vekt) {
		this.vekt = vekt;
	}

	public double getProduksjonskostnader() {
		return produksjonskostnader;
	}

	public void setProduksjonskostnader(double produksjonskostnader) {
		this.produksjonskostnader = produksjonskostnader;
	}

	public double getPorto() {
		return porto;
	}

	public void setPorto(double porto) {
		this.porto = porto;
	}
}
