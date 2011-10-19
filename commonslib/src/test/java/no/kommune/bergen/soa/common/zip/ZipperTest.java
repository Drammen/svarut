package no.kommune.bergen.soa.common.zip;

import no.kommune.bergen.soa.util.Files;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ZipperTest {
	private static Log log = LogFactory.getLog( ZipperTest.class );
	private final String zipFile = "target/ZipperTest.zip";
	private final String fileContent = "File Content of file ";
	//private final String[] fileNames = { "apache-ant-1.7.0-bin.zip", "seda-sosp01.pdf", "excel.xls", "winword.doc", "serverconfig.PNG" };
	private final String[] fileNames = { "target/ZipperTest0.txt", "target/ZipperTest1.txt", "target/ZipperTest2.txt" };

	private void createTestFiles() throws IOException {
		for (String fileName : fileNames) {
			Files.writeToFile( fileName, (fileContent + fileName).getBytes() );
		}
	}

	@Test
	@Ignore
	public void zipUpSomeFiles() throws IOException {
		createTestFiles();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		Zipper zipper = new Zipper( byteArrayOutputStream );
		try {
			for (String fileName : fileNames) {
				try {
					byte[] fileContent = Files.getFileContent( fileName );
					zipper.addEntry( fileName, fileContent );
				} catch (Exception e) {
					log.error( fileName, e );
					fail( fileName + " - " + e.getMessage() );
				}
			}
		} finally {
			try {
				zipper.close();
			} catch (IOException e) {
				log.error( e.getMessage(), e );
				fail( e.getMessage() );
			}
		}
		Files.writeToFile( zipFile, byteArrayOutputStream.toByteArray() );
		verifyFileContent();
	}

	@Test
	public void zipUpSomeStreams() throws IOException {
		createTestFiles();
		FileOutputStream outputStream = new FileOutputStream( zipFile );
		Zipper zipper = new Zipper( outputStream );
		try {
			for (String fileName : fileNames) {
				try {
					FileInputStream inputStream = new FileInputStream( fileName );
					zipper.addEntry( fileName, inputStream );
				} catch (Exception e) {
					log.error( fileName, e );
					fail( fileName + " - " + e.getMessage() );
				}
			}
		} finally {
			try {
				zipper.close();
			} catch (IOException e) {
				log.error( e.getMessage(), e );
				fail( e.getMessage() );
			}
		}
		verifyFileContent();
	}

	private void verifyFileContent() {
		try {
			FileInputStream fileInputStream = new FileInputStream( zipFile );
			Unzipper unzipper = new Unzipper( fileInputStream );
			for (int i = 0; i < fileNames.length; i++) {
				String fileName = fileNames[i];
				String content = new String( unzipper.getEntry( fileName ) );
				assertEquals( (fileContent + fileName), content );
			}
		} catch (Exception e) {
			log.error( e.getMessage(), e );
			fail( e.getMessage() );
		}
	}
}
