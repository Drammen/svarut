package no.kommune.bergen.soa.svarut.altinn.authorization;

public class Avgiver {

	private String name;
	private String organizationNumber;
	private String reporteeType;
	private String ssn;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getOrganizationNumber() {
		return organizationNumber;
	}
	public void setOrganizationNumber(String organizationNumber) {
		this.organizationNumber = organizationNumber;
	}
	public String getReporteeType() {
		return reporteeType;
	}
	public void setReporteeType(String reporteeType) {
		this.reporteeType = reporteeType;
	}
	public String getSsn() {
		return ssn;
	}
	public void setSsn(String ssn) {
		this.ssn = ssn;
	}
}
