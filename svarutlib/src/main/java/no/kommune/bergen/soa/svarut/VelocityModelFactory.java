package no.kommune.bergen.soa.svarut;

import java.util.HashMap;
import java.util.Map;

import no.kommune.bergen.soa.svarut.domain.Forsendelse;
import no.kommune.bergen.soa.svarut.domain.JuridiskEnhet;

import org.apache.log4j.Logger;

/** Helper class to be used with Velocity */
public class VelocityModelFactory {
	final Logger logger = Logger.getLogger( VelocityModelFactory.class );
	private final String urlTemplate;
	private final String helpLink;
	private final String pdfLinkText;
	private final String helpLinkText;
	private final String readerDownloadLink;
	private final String readerDownloadLinkText;

	/** All arguments are Velocity templates */
	public VelocityModelFactory( String urlTemplate, String pdfLinkText, String helpLink, String helpLinkText, String readerDownloadLink, String readerDownloadLinkText ) {
		this.urlTemplate = urlTemplate;
		this.pdfLinkText = pdfLinkText;
		this.helpLink = helpLink;
		this.helpLinkText = helpLinkText;
		this.readerDownloadLink = readerDownloadLink;
		this.readerDownloadLinkText = readerDownloadLinkText;
	}

	/** Transforms a Forsendelse into a Velocity model that may be addressed from the templates */
	public Map<String, String> createModel( Forsendelse f ) {
		Map<String, String> model = new HashMap<String, String>();
		model.put( "ID", f.getId() );
		model.put( "FNR", f.getFnr() );
		model.put( "ORGNR", String.valueOf( f.getOrgnr() ) );
		model.put( "NAVN", f.getNavn() );
		model.put( "ADRESSE1", f.getAdresse1() );
		model.put( "ADRESSE2", f.getAdresse2() );
		model.put( "ADRESSE3", f.getAdresse3() );
		model.put( "LAND", f.getLand() );
		model.put( "POSTNR", f.getPostnr() );
		model.put( "POSTSTED", f.getPoststed() );
		model.put( "AVSENDER_NAVN", f.getAvsenderNavn() );
		model.put( "AVSENDER_ADRESSE1", f.getAvsenderAdresse1() );
		model.put( "AVSENDER_ADRESSE2", f.getAvsenderAdresse2() );
		model.put( "AVSENDER_ADRESSE3", f.getAvsenderAdresse3() );
		model.put( "AVSENDER_POSTNR", f.getAvsenderPostnr() );
		model.put( "AVSENDER_POSTSTED", f.getAvsenderPoststed() );
		model.put( "FORSENDELSES_MATE", f.getShipmentPolicy() );
		model.put( "TITTEL", f.getTittel() );
		model.put( "MELDING", f.getMeldingsTekst() );
		model.put( "FILE", getDocumentFileName( f ) );
		model.put( "URL", getUrl( JuridiskEnhetFactory.create( f ), f.getId() ) );
		model.put( "Link", getUrl( JuridiskEnhetFactory.create( f ), f.getId() ) );
		model.put( "Link-text", pdfLinkText );
		model.put( "Help-Link", getHelpUrl( f.getId() ) );
		model.put( "Help-Link-text", helpLinkText );
		model.put( "ReaderDownload-Link", readerDownloadLink );
		model.put( "ReaderDownload-Link-text", readerDownloadLinkText );
		return model;
	}

	private String getDocumentFileName( Forsendelse f ) {
		if (f == null || f.getFile() == null) return null;
		try {
			return f.getFile().getAbsolutePath();
		} catch (Exception e) {
			throw new RuntimeException( String.format( "Cannot find file %s for ForsendelsesId=%s", f.getFile(), f.getId() ) );
		}
	}

	public String getUrl( JuridiskEnhet juridiskEnhet, String id ) {
		if (takesTwoArguments( this.urlTemplate )) {
			return String.format( this.urlTemplate, juridiskEnhet.getValue(), id );
		} else {
			return String.format( this.urlTemplate, id );
		}
	}

	boolean takesTwoArguments( String urlTemplate2 ) {
		final String formating = "%s";
		int idxFirst = urlTemplate2.indexOf( formating );
		int idxLast = urlTemplate2.lastIndexOf( formating );
		return idxFirst != idxLast;
	}

	public String getHelpUrl( String id ) {
		return String.format( this.helpLink, id );
	}

}
