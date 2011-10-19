package org.svarut.sample.rest;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.activation.FileDataSource;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import no.kommune.bergen.soa.svarut.DispatcherFactory;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;
import no.kommune.bergen.soa.svarut.dto.ForsendelseStatusRest;
import no.kommune.bergen.soa.svarut.dto.ShipmentPolicy;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import org.apache.cxf.jaxrs.ext.multipart.MultipartBody;
import org.apache.cxf.jaxrs.ext.xml.XMLSource;
import org.apache.cxf.jaxrs.impl.MetadataMap;
import org.junit.Ignore;
import org.junit.Test;

public class SvarUtSampleServiceRestTest {

	@Test
	@Ignore
	public void statuslist() throws Exception {
		WebClient wc = WebClient.create( "http://localhost:8080/forsendelse/service/rest/forsendelsesservice/statuslist?ids=000000001689.pdf,000000001690.pdf" );
		wc.type( "text/xml" );
		XMLSource source = wc.get( XMLSource.class );
		source.setBuffering( true );
		ForsendelseStatusRest b1 = source.getNode( "/books/book[position() = 1]", ForsendelseStatusRest.class );
		assertNotNull( b1 );
		ForsendelseStatusRest b2 = source.getNode( "/books/book[position() = 2]", ForsendelseStatusRest.class );
		assertNotNull( b2 );
	}

	@Test
	@Ignore
	public void statuslist2() throws Exception {
		InputStream inputStream = invokeRestfulService( "http://localhost:8080/forsendelse/service/rest/forsendelsesservice/statuslist?ids=000000001689.pdf,000000001690.pdf", "text/xml" );
		byte[] buffer = new byte[1024 * 10];
		int count = inputStream.read( buffer );
		assertTrue( count > 0 );
		System.out.println( new String( buffer ) );
	}

	private InputStream invokeRestfulService( String endpoint, String contentType ) throws MalformedURLException, IOException {
		URL url = new URL( endpoint );
		URLConnection urlConnection = url.openConnection();
		urlConnection.setUseCaches( false );
		urlConnection.setRequestProperty( "Content-Type", String.format( "%s", contentType ) );
		urlConnection.setRequestProperty( "Accept", contentType );
		return urlConnection.getInputStream();
	}

	@Test
	@Ignore
	public void shit() throws Exception {
		WebClient client = WebClient.create( "http://localhost:8080/forsendelse/service/rest/forsendelsesservice/send" );
		client.type( "multipart/form-data" );
		List<Attachment> attacments = new ArrayList<Attachment>();
		attacments.add( createDocumentAttachment( new File( "src/test/resources/Undervisningsfritak.pdf" ) ) );
		attacments.add( createForsendelsesAttachment( createForsendelse( 1 ) ) );
		Response rs = client.post( new MultipartBody( attacments ) );
		System.out.println( rs.getStatus() );
	}

	Attachment createForsendelsesAttachment( Forsendelse forsendelse ) throws Exception {
		JAXBContext jaxbContext = JAXBContext.newInstance( new Class[] { Forsendelse.class } );
		Marshaller marshaller = jaxbContext.createMarshaller();
		File tmpFile = File.createTempFile( "fors-", ".xml" );
		marshaller.marshal( forsendelse, tmpFile );
		FileDataSource fileDataSource = new FileDataSource( tmpFile );
		MultivaluedMap<String, String> headers = new MetadataMap<String, String>();
		headers.add( "Content-Disposition", "attachment;filename=" + tmpFile.getName() );
		headers.add( "Content-Type", " text/xml; charset=utf-8" );
		return new Attachment( "forsendelse", fileDataSource, headers );
	}

	Attachment createDocumentAttachment( File pdfFile ) {
		String filename = pdfFile.getName();
		MultivaluedMap<String, String> headers = new MetadataMap<String, String>();
		headers.add( "Content-Disposition", "attachment;filename=" + filename );
		headers.add( "Content-Type", "application/x-www-form-urlencoded" );
		FileDataSource fileDataSource = new FileDataSource( pdfFile );
		return new Attachment( "dokument", fileDataSource, headers );
	}

	private Forsendelse createForsendelse( int variant ) {
		final String fnr = "12345678901";
		final String orgnr = "987654321";
		return createForsendelse( variant, fnr, orgnr );
	}

	private Forsendelse createForsendelse( int variant, String fnr, String orgnr ) {
		final String navn = "navn";
		final String adresse1 = "adresse1";
		final String adresse2 = "adresse2";
		final String adresse3 = "adresse3";
		final String postnr = "postnr";
		final String poststed = "poststed";
		final String avsender_navn = "avsender_navn";
		final String avsender_adresse1 = "avsender_adresse1";
		final String avsender_adresse2 = "avsender_adresse2";
		final String avsender_adresse3 = "avsender_adresse3";
		final String avsender_postnr = "avsender_postnr";
		final String avsender_poststed = "avsender_poststed";
		final String land = "land";
		final String tittel = "tittel";
		final String melding = "melding";
		final String appid = "appid";
		Forsendelse f = new Forsendelse();
		f.setFnr( fnr );
		f.setOrgnr( orgnr );
		f.setNavn( navn + variant );
		f.setAdresse1( adresse1 + variant );
		f.setAdresse2( adresse2 + variant );
		f.setAdresse3( adresse3 + variant );
		f.setPostnr( postnr + variant );
		f.setPoststed( poststed + variant );
		f.setLand( land + variant );
		f.setAvsenderNavn( avsender_navn + variant );
		f.setAvsenderAdresse1( avsender_adresse1 + variant );
		f.setAvsenderAdresse2( avsender_adresse2 + variant );
		f.setAvsenderAdresse3( avsender_adresse3 + variant );
		f.setAvsenderPostnr( avsender_postnr + variant );
		f.setAvsenderPoststed( avsender_poststed + variant );
		f.setTittel( tittel + variant );
		f.setMeldingsTekst( melding + variant );
		f.setAppid( appid + variant );
		f.setShipmentPolicy( ShipmentPolicy.NORGE_DOT_NO_OG_APOST.value());
		return f;
	}

}
