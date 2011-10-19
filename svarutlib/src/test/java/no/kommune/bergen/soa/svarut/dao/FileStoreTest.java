package no.kommune.bergen.soa.svarut.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import no.kommune.bergen.soa.common.pdf.PdfGeneratorImpl;
import no.kommune.bergen.soa.svarut.util.FilHenter;
import no.kommune.bergen.soa.util.Files;

import org.junit.Test;

public class FileStoreTest {
	private final static String storePath = "target";
	PdfGeneratorImpl pdfGenerator = new PdfGeneratorImpl( storePath );
	FileStore fs = new FileStore( storePath, pdfGenerator );

	@Test
	public void saveAndFetch() throws IOException {
		byte[] fileContent = FilHenter.getFileContent("test.pdf");
		UUID uuid = UUID.randomUUID();
		String filename = fs.save( new ByteArrayInputStream( fileContent ), uuid.toString() );
		String savedContent = getFileContent( filename );
		assertEquals( new String( fileContent ), savedContent );
		assertTrue( fs.getFile( filename ).getPath().indexOf( storePath ) > -1 );
		remove( filename );
	}

	@Test
	public void saveAndFetchTextContent() throws IOException {
		byte[] fileContent = "Text content\nline two\nline three".getBytes();
		UUID uuid = UUID.randomUUID();
		String filename = fs.save( new ByteArrayInputStream( fileContent ), uuid.toString() );
		String savedContent = getFileContent( filename );
		assertNotNull( savedContent );
		//PrintFacadeTest.view( new File( storePath, filename ) );
		assertTrue( fs.getFile( filename ).getPath().indexOf( storePath ) > -1 );
		remove( filename );
	}

	private void remove( String filename ) {
		fs.remove( filename );
		try {
			fs.fetch( filename );
			fail( "remove() failed" );
		} catch (Exception e) {
			assertTrue( true );
		}
	}

	private String getFileContent( String filename ) throws IOException {
		InputStream inputStream = fs.fetch( filename );
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		Files.stream( inputStream, byteArrayOutputStream );
		byte[] ba = byteArrayOutputStream.toByteArray();
		inputStream.close(); //Ellers får man ikke slettet filen
		byteArrayOutputStream.close();
		return new String( ba );
	}

}
