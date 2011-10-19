package no.kommune.bergen.soa.svarut.dto;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DokumentRs", propOrder = {
		"filnavn",
		"data"
})
public class DokumentRs {

	@XmlElement(required = true)
	protected String filnavn;
	@XmlMimeType("application/octet-stream")
	protected DataHandler data;

	/**
	 * Gets the value of the filnavn property.
	 *
	 * @return possible object is
	 *         {@link String }
	 */
	public String getFilnavn() {
		return filnavn;
	}

	/**
	 * Sets the value of the filnavn property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setFilnavn(String value) {
		this.filnavn = value;
	}

	/**
	 * Gets the value of the data property.
	 *
	 * @return possible object is
	 *         {@link DataHandler }
	 */
	public DataHandler getData() {
		return data;
	}

	/**
	 * Sets the value of the data property.
	 *
	 * @param value allowed object is
	 *              {@link DataHandler }
	 */
	public void setData(DataHandler value) {
		this.data = value;
	}

}
