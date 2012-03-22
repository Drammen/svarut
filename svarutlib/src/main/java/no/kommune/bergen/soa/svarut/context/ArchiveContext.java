package no.kommune.bergen.soa.svarut.context;

import javax.sql.DataSource;

/**
 * Holder oppsetts parametre for Forsendelsesarkivet. Forsendelsesarkivet består av en database del og en mappe der kokumentene
 * legges
 */
public class ArchiveContext {

	private int retirementAgeInDays = 3650;
	private String tempDir = "/tmp", fileStorePath = "/tmp";
	private DataSource dataSource;

	public void verify() {
		if (dataSource == null) throw new RuntimeException( "Undefined field: dataSource" );
	}

	@Override
	public String toString() {
		return String.format("{\n retirementAgeInDays=%s\n tempDir=%s\n fileStorePath=%s\n dataSource;=%s\n \n}", retirementAgeInDays, tempDir, fileStorePath, dataSource);
	}

	public int getRetirementAgeInDays() {
		return retirementAgeInDays;
	}

	/** Når skal scavenger jobben fjerne forsendelser */
	@SuppressWarnings("JavaDoc")
	public void setRetirementAgeInDays(int retirementAgeInDays) {
		this.retirementAgeInDays = retirementAgeInDays;
	}

	public String getTempDir() {
		return tempDir;
	}

	public void setTempDir( String tempDir ) {
		this.tempDir = tempDir;
	}

	public String getFileStorePath() {
		return fileStorePath;
	}

	/** Dokument-delen av forsendelses-arkivet */
	@SuppressWarnings("JavaDoc")
	public void setFileStorePath( String fileStorePath ) {
		this.fileStorePath = fileStorePath;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	/** Database-delen av forsendelses-arkivet */
	@SuppressWarnings("JavaDoc")
	public void setDataSource( DataSource dataSource ) {
		this.dataSource = dataSource;
	}

}
