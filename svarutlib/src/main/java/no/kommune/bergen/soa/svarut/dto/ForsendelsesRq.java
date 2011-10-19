package no.kommune.bergen.soa.svarut.dto;

import javax.activation.DataHandler;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlMimeType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ForsendelsesRq", propOrder = {
		"forsendelse",
		"data"
})
public class ForsendelsesRq {

	protected Forsendelse forsendelse;
	@XmlMimeType("application/octet-stream")
	protected DataHandler data;

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
