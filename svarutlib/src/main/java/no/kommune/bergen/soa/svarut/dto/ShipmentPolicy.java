package no.kommune.bergen.soa.svarut.dto;

import no.kommune.bergen.soa.common.exception.UserException;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "ShipmentPolicy")
@XmlEnum
public enum ShipmentPolicy {

	@XmlEnumValue("KunApost")
	KUN_APOST("KunApost"),
	@XmlEnumValue("KunBpost")
	KUN_BPOST("KunBpost"),
	@XmlEnumValue("KunRekommandert")
	KUN_REKOMMANDERT("KunRekommandert"),
	@XmlEnumValue("KunNorgeDotNo")
	KUN_NORGE_DOT_NO("KunNorgeDotNo"),
	@XmlEnumValue("KunNorgeDotNoAttachDocumet")
	KUN_NORGE_DOT_NO_ATTACH_DOCUMET("KunNorgeDotNoAttachDocumet"),
	@XmlEnumValue("NorgeDotNoOgApost")
	NORGE_DOT_NO_OG_APOST("NorgeDotNoOgApost"),
	@XmlEnumValue("NorgeDotNoOgBpost")
	NORGE_DOT_NO_OG_BPOST("NorgeDotNoOgBpost"),
	@XmlEnumValue("NorgeDotNoOgRekommandert")
	NORGE_DOT_NO_OG_REKOMMANDERT("NorgeDotNoOgRekommandert"),
	@XmlEnumValue("KunAltinn")
	KUN_ALTINN("KunAltinn"),
	@XmlEnumValue("AltinnOgApost")
	ALTINN_OG_APOST("AltinnOgApost"),
	@XmlEnumValue("AltinnOgBpost")
	ALTINN_OG_BPOST("AltinnOgBpost"),
	@XmlEnumValue("AltinnOgRekommandert")
	ALTINN_OG_REKOMMANDERT("AltinnOgRekommandert"),
	@XmlEnumValue("KunEmail")
	KUN_EMAIL("KunEmail"),
	@XmlEnumValue("KunEmailIngenVedlegg")
	KUN_EMAIL_INGEN_VEDLEGG("KunEmailIngenVedlegg"),
	@XmlEnumValue("EmailOgApost")
	EMAIL_OG_APOST("EmailOgApost"),
	@XmlEnumValue("EmailOgBpost")
	EMAIL_OG_BPOST("EmailOgBpost"),
	@XmlEnumValue("EmailOgRekommandert")
	EMAIL_OG_REKOMMANDERT("EmailOgRekommandert");
	private final String value;

	ShipmentPolicy(String v) {
		value = v;
	}

	public String value() {
		return value;
	}

	public static ShipmentPolicy fromValue(String v) {
		for (ShipmentPolicy c : ShipmentPolicy.values()) {
			if (c.value.equals(v)) {
				return c;
			}
		}

		throw new UserException("Failed to convert string argument to ShipmentPolicy: " + v);
	}

}
