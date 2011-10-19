package no.kommune.bergen.soa.svarut.context;

import javax.sql.DataSource;

/**
 * Holder oppsetts parametre for Forsendelsesarkivet. Forsendelsesarkivet består av en database del og en mappe der kokumentene
 * legges
 */
public class ArchiveContext {
	private int retirementAgeIndays = 3650;
	private String tempDir = "/tmp", fileStorePath = "/tmp";
	private DataSource dataSource;

	public void verify() {
		if (dataSource == null) throw new RuntimeException( "Undefined field: dataSource" );
	}

	@Override
	public String toString() {
		return String.format( "{\n retirementAgeIndays=%s\n tempDir=%s\n fileStorePath=%s\n dataSource;=%s\n \n}", retirementAgeIndays, tempDir, fileStorePath, dataSource );
	}

	public int getRetirementAgeIndays() {
		return retirementAgeIndays;
	}

	/** Når skal scavenger jobben fjerne forsendelser */
	public void setRetirementAgeIndays( int retirementAgeIndays ) {
		this.retirementAgeIndays = retirementAgeIndays;
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
	public void setFileStorePath( String fileStorePath ) {
		this.fileStorePath = fileStorePath;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	/** Database-delen av forsendelses-arkivet */
	public void setDataSource( DataSource dataSource ) {
		this.dataSource = dataSource;
	}

}
