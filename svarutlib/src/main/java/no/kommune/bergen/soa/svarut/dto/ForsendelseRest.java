package no.kommune.bergen.soa.svarut.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"forsendelse"
})
@XmlRootElement(name = "ForsendelseRest")
public class ForsendelseRest {

	protected Forsendelse forsendelse;

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

}
