package no.kommune.bergen.soa.svarut.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Adresse123", propOrder = {
		"adresse1",
		"adresse2",
		"adresse3",
		"postnr",
		"poststed",
		"landkode",
		"land"
})
public class Adresse123 {

	@XmlElement(required = true, nillable = true)
	protected String adresse1;
	@XmlElement(required = true, nillable = true)
	protected String adresse2;
	@XmlElement(required = true, nillable = true)
	protected String adresse3;
	@XmlElement(required = true, nillable = true)
	protected String postnr;
	@XmlElement(required = true, nillable = true)
	protected String poststed;
	@XmlElement(required = true, nillable = true)
	protected String landkode;
	@XmlElement(required = true, nillable = true)
	protected String land;

	/**
	 * Gets the value of the adresse1 property.
	 *
	 * @return possible object is
	 *         {@link String }
	 */
	public String getAdresse1() {
		return adresse1;
	}

	/**
	 * Sets the value of the adresse1 property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setAdresse1(String value) {
		this.adresse1 = value;
	}

	/**
	 * Gets the value of the adresse2 property.
	 *
	 * @return possible object is
	 *         {@link String }
	 */
	public String getAdresse2() {
		return adresse2;
	}

	/**
	 * Sets the value of the adresse2 property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setAdresse2(String value) {
		this.adresse2 = value;
	}

	/**
	 * Gets the value of the adresse3 property.
	 *
	 * @return possible object is
	 *         {@link String }
	 */
	public String getAdresse3() {
		return adresse3;
	}

	/**
	 * Sets the value of the adresse3 property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setAdresse3(String value) {
		this.adresse3 = value;
	}

	/**
	 * Gets the value of the postnr property.
	 *
	 * @return possible object is
	 *         {@link String }
	 */
	public String getPostnr() {
		return postnr;
	}

	/**
	 * Sets the value of the postnr property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setPostnr(String value) {
		this.postnr = value;
	}

	/**
	 * Gets the value of the poststed property.
	 *
	 * @return possible object is
	 *         {@link String }
	 */
	public String getPoststed() {
		return poststed;
	}

	/**
	 * Sets the value of the poststed property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setPoststed(String value) {
		this.poststed = value;
	}

	/**
	 * Gets the value of the landkode property.
	 *
	 * @return possible object is
	 *         {@link String }
	 */
	public String getLandkode() {
		return landkode;
	}

	/**
	 * Sets the value of the landkode property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setLandkode(String value) {
		this.landkode = value;
	}

	/**
	 * Gets the value of the land property.
	 *
	 * @return possible object is
	 *         {@link String }
	 */
	public String getLand() {
		return land;
	}

	/**
	 * Sets the value of the land property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setLand(String value) {
		this.land = value;
	}

}
