package no.kommune.bergen.soa.svarut.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
		"forsendelseStatus"
})
@XmlRootElement(name = "ForsendelseStatusRest")
public class ForsendelseStatusRest {

	protected ForsendelseStatus forsendelseStatus;

	/**
	 * Gets the value of the forsendelseStatus property.
	 *
	 * @return possible object is
	 *         {@link ForsendelseStatus }
	 */
	public ForsendelseStatus getForsendelseStatus() {
		return forsendelseStatus;
	}

	/**
	 * Sets the value of the forsendelseStatus property.
	 *
	 * @param value allowed object is
	 *              {@link ForsendelseStatus }
	 */
	public void setForsendelseStatus(ForsendelseStatus value) {
		this.forsendelseStatus = value;
	}

}
