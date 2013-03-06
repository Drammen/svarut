package no.kommune.bergen.soa.svarut.altinn.administration.external.client;

import java.util.ArrayList;
import java.util.List;

import no.altinn.schemas.services.authorization.administration._2012._11.ExternalReporteeBE;
import no.altinn.schemas.services.authorization.administration._2012._11.ExternalReporteeBEList;
import no.altinn.schemas.services.register._2009._10.PartyType;
import no.kommune.bergen.soa.svarut.altinn.administration.external.Avgiver;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AltinnGetReportees {

	private static final Logger log = LoggerFactory.getLogger(AltinnGetReportees.class);

	private AltinnAdministrationExternalClient altinnAdministrationExternal;

	public AltinnGetReportees(AltinnAdministrationExternalSettings settings) {
		altinnAdministrationExternal = new AltinnAdministrationExternalClient(settings);
	}

	public void setAltinnAdministrationExternalClient(AltinnAdministrationExternalClient client) {
		this.altinnAdministrationExternal = client;
	}

	public List<Avgiver> getOrganisasjonsAvgivere(String fodselsNr) {
		return submitRequest(fodselsNr);
	}

	private List<Avgiver> lagOrganisasjonsAvgiverListe(ExternalReporteeBEList externalReporteeBEList) {
		return lagAvgiverListe(externalReporteeBEList, PartyType.ORGANIZATION.value());
	}

	private List<Avgiver> lagAvgiverListe(ExternalReporteeBEList externalReporteeBEList, String typeFilter) {
		List<Avgiver> organisasjoner = new ArrayList<Avgiver>();
		List<ExternalReporteeBE> list = externalReporteeBEList.getExternalReporteeBE();
		for(ExternalReporteeBE externalReporteeBE : list) {
			if(typeFilter != null && !typeFilter.equals(externalReporteeBE.getReporteeType().value()))
				continue;

			Avgiver reportee = new Avgiver();
			reportee.setName(externalReporteeBE.getName());
			reportee.setOrganizationNumber(externalReporteeBE.getOrganizationNumber());
			reportee.setReporteeType(externalReporteeBE.getReporteeType().value());
			reportee.setSsn(externalReporteeBE.getSSN());
			organisasjoner.add(reportee);
		}
		return organisasjoner;
	}

	protected List<Avgiver> submitRequest(String fodselsNr) {
		ExternalReporteeBEList list = this.altinnAdministrationExternal.getAvgivere(fodselsNr);
		List<Avgiver> organisasjoner = new ArrayList<Avgiver>();
		if(list != null) {
			organisasjoner = lagOrganisasjonsAvgiverListe(list);
		}
		return organisasjoner;
	}
}
