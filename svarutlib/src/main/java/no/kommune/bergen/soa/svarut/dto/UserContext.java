package no.kommune.bergen.soa.svarut.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Brukes for å spore kall til tjenestene. Man må
 * oppgi userid, som unikt identifiserer den brukeren som kaller
 * tjenesten. Eksempel kan	være brukernavn i AD eller fodselsnummer.
 * Dersom man kaller tjenesten på vegne av	en tredjepart, oppgir du userid for tredjepart i feltet
 * onBehalfOfId. Appid skal inneholde en kort streng som unikt
 * identifiserer applikasjonen som kaller tjenesten. Brukes i
 * forbindelse med sporing av kallet. Denne kan du finne på selv, men
 * bruk helst samme verdi gjennom hele applikasjonen, og prøv å finne
 * på noe unikt. Transactionid skal unikt identifisere hvert kall, og
 * brukes for sporing i loggene.
 * <p/>
 * <p/>
 * <pre>
 * &lt;?xml version="1.0" encoding="UTF-8"?&gt;&lt;p xmlns="http://bergen.kommune.no/com/biz/bk/v1" xmlns:xsd="http://www.w3.org/2001/XMLSchema"&gt;
 * 					Kombinasjonen av appid og transactionid må være unikt for å kunne
 * 					brukes i supporthenvendelser.
 * 				&lt;/p&gt;
 * </pre>
 * <p/>
 * <p/>
 * <p/>
 * <p>Java class for UserContext complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="UserContext">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="userid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="onBehalfOfId" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="appid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="transactionid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "UserContext", propOrder = {
		"userid",
		"onBehalfOfId",
		"appid",
		"transactionid"
})
public class UserContext {

	@XmlElement(required = true)
	protected String userid;
	@XmlElement(required = true, nillable = true)
	protected String onBehalfOfId;
	@XmlElement(required = true)
	protected String appid;
	@XmlElement(required = true, nillable = true)
	protected String transactionid;

	/**
	 * Gets the value of the userid property.
	 *
	 * @return possible object is
	 *         {@link String }
	 */
	public String getUserid() {
		return userid;
	}

	/**
	 * Sets the value of the userid property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setUserid(String value) {
		this.userid = value;
	}

	/**
	 * Gets the value of the onBehalfOfId property.
	 *
	 * @return possible object is
	 *         {@link String }
	 */
	public String getOnBehalfOfId() {
		return onBehalfOfId;
	}

	/**
	 * Sets the value of the onBehalfOfId property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setOnBehalfOfId(String value) {
		this.onBehalfOfId = value;
	}

	/**
	 * Gets the value of the appid property.
	 *
	 * @return possible object is
	 *         {@link String }
	 */
	public String getAppid() {
		return appid;
	}

	/**
	 * Sets the value of the appid property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setAppid(String value) {
		this.appid = value;
	}

	/**
	 * Gets the value of the transactionid property.
	 *
	 * @return possible object is
	 *         {@link String }
	 */
	public String getTransactionid() {
		return transactionid;
	}

	/**
	 * Sets the value of the transactionid property.
	 *
	 * @param value allowed object is
	 *              {@link String }
	 */
	public void setTransactionid(String value) {
		this.transactionid = value;
	}

}
