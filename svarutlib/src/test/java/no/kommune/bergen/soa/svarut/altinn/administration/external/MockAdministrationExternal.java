package no.kommune.bergen.soa.svarut.altinn.administration.external;

import java.util.ArrayList;
import java.util.List;

import no.altinn.schemas.services.authorization.administration._2012._11.ExternalReporteeBE;
import no.altinn.schemas.services.authorization.administration._2012._11.ExternalReporteeBEList;
import no.altinn.schemas.services.register._2009._10.PartyType;
import no.kommune.bergen.soa.svarut.altinn.administration.external.client.AltinnAdministrationExternalClient;
import no.kommune.bergen.soa.svarut.altinn.administration.external.client.AltinnAdministrationExternalSettings;

public class MockAdministrationExternal extends AltinnAdministrationExternalClient {

	private List<ExternalReporteeBE> externalReporteeBETestList;

	public MockAdministrationExternal(AltinnAdministrationExternalSettings settings) {
		super(settings);
		externalReporteeBETestList = new ArrayList<ExternalReporteeBE>();
	}

	public static List<ExternalReporteeBE> createTestData() {
		List<ExternalReporteeBE> list = new ArrayList<ExternalReporteeBE>();
		ExternalReporteeBE be1 = new ExternalReporteeBE();
		be1.setName("ALMA SKOGLAND");
		be1.setOrganizationNumber(null);
		be1.setReporteeType(PartyType.PERSON);
		be1.setSSN("02035701829");
		list.add(be1);

		ExternalReporteeBE be2 = new ExternalReporteeBE();
		be2.setName("ALTA OG NAMSOS REGNSKAP");
		be2.setOrganizationNumber("910824929");
		be2.setReporteeType(PartyType.ORGANIZATION);
		be2.setSSN(null);
		list.add(be2);

		ExternalReporteeBE be3 = new ExternalReporteeBE();
		be3.setName("KOKSTAD OG ENEBAKKNESET REGNSKAP");
		be3.setOrganizationNumber("910854453");
		be3.setReporteeType(PartyType.ORGANIZATION);
		be3.setSSN(null);
		list.add(be3);

		return list;
	}

	public void setExternalReporteeBETestList(List<ExternalReporteeBE> list) {
		this.externalReporteeBETestList = list;
	}

	@Override
	public ExternalReporteeBEList getAvgivere(String fodselsNr) {
		ExternalReporteeBEList list = new ExternalReporteeBEList();
		list.getExternalReporteeBE().addAll(externalReporteeBETestList);
		return list;
	}
}
