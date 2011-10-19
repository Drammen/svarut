package no.kommune.bergen.soa.svarut;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import no.kommune.bergen.soa.common.remote.RemoteFileTransferClient;
import no.kommune.bergen.soa.svarut.util.FilHenter;

import org.junit.Ignore;
import org.junit.Test;

public class HttpPostClientTest {
	private final static String URI = "https://services.grafiskdigital.no/gdadmin/upload", username = "bergenkommune", password = "ht85nvz61";

	@Ignore
	@Test
	public void send() throws FileNotFoundException {
		String data = "123qweæøåÆØÅ!#";
		String filename = "1000_110000_000000000555.txt", username = "mule", password = "mule1234";
		RemoteFileTransferClient client = new HttpPostClient( "http://localhost:8080/mockservices/httppost", username, password );
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream( data.getBytes() );
		client.send( byteArrayInputStream, filename );
	}

	@Test
	public void testFilenameDotZip() {
		String result = HttpPostClient.filenameDotZip( "oh.my.pdf" );
		assertEquals( "oh.my.zip", result );
		result = HttpPostClient.filenameDotZip( "my.pdf" );
		assertEquals( "my.zip", result );
		result = HttpPostClient.filenameDotZip( "ohmy" );
		assertEquals( "ohmy.zip", result );
	}

	@Test
	public void zipItUp() throws Exception {
		String filename = "000000000061.pdf";
		HttpPostClient client = new HttpPostClient( URI, username, password );
		File zipFile = new File( "target/HttpPostClientTest.zip" );
		OutputStream outputStream = new FileOutputStream( zipFile );
		client.zipItUp( FilHenter.getFileAsInputStream(filename), filename, outputStream );
		//view( zipFile );
	}

	static void view( File zip ) {
		try {
			Runtime.getRuntime().exec( "file-roller " + zip.getPath() );
		} catch (IOException e) {
			throw new RuntimeException( "Unable to launch: file-roller " + zip.getPath() );
		}
	}

}
