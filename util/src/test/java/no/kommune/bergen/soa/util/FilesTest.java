package no.kommune.bergen.soa.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;

public class FilesTest {
	private static final byte[] DATA = "123qweæøåÆØÅ!#".getBytes();

	@Test
	public void copy() throws IOException {
		File rootFolder = new File( "target" );
		File sourceFolder = new File( rootFolder, "source" );
		sourceFolder.mkdir();
		File destinationFolder = new File( rootFolder, "destination" );
		destinationFolder.mkdir();
		String testFile = "FilesTest.tmp";
		File sourceFile = new File( sourceFolder, testFile );
		Files.writeToFile( sourceFile.getAbsolutePath(), DATA);
		Files.copy( sourceFile, destinationFolder.getAbsolutePath() );
		File targetFile = new File( sourceFolder, testFile );
		byte[] result = Files.getFileContent( targetFile.getAbsolutePath() );
		Assert.assertEquals( DATA.length, result.length );
		Assert.assertEquals( new String(DATA), new String( result ) );
		for (int i = 0; i < DATA.length; i++) {
			Assert.assertEquals( DATA[i], result[i] );
		}
	}

	@Test
	public void copy2() throws IOException {
		File rootFolder = new File( "target" );
		File sourceFolder = new File( rootFolder, "source" );
		sourceFolder.mkdir();
		File destinationFolder = new File( rootFolder, "destination" );
		destinationFolder.mkdir();
		String testFile = "FilesTest.tmp";
		File sourceFile = new File( sourceFolder, testFile );
		ByteArrayInputStream inputStream = new ByteArrayInputStream(DATA);
		Files.writeToFile( sourceFile, inputStream );
		Files.copy( sourceFile, destinationFolder.getAbsolutePath() );
		File targetFile = new File( sourceFolder, testFile );
		byte[] result = Files.getFileContent( targetFile.getAbsolutePath() );
		Assert.assertEquals( DATA.length, result.length );
		Assert.assertEquals( new String(DATA), new String( result ) );
		for (int i = 0; i < DATA.length; i++) {
			Assert.assertEquals( DATA[i], result[i] );
		}
	}

	@Test
	public void write() throws IOException {
		File sourceFile = new File( "src/test/resources/test.pdf" );
		File destinationFile = File.createTempFile( "test", ".pdf" );
		Files.writeToFile( destinationFile, new FileInputStream( sourceFile ) );
		byte[] baSource = Files.getFileContent( sourceFile );
		byte[] baDestination = Files.getFileContent( destinationFile );
		Assert.assertEquals( baSource.length, baDestination.length );
		for (int i = 0; i < baSource.length; i++) {
			Assert.assertEquals( baSource[i], baDestination[i] );
		}
		Assert.assertEquals( new String( baSource ), new String( baDestination ) );
	}

	@Test
	public void stream() throws IOException {
		FileInputStream inputStream = new FileInputStream( "src/test/resources/test.pdf" );
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Files.stream( inputStream, outputStream );
		byte[] baSource = Files.getFileContent( "src/test/resources/test.pdf" );
		byte[] baDestination = outputStream.toByteArray();
		for (int i = 0; i < baSource.length; i++) {
			Assert.assertEquals( baSource[i], baDestination[i] );
		}
		Assert.assertEquals( new String( baSource ), new String( baDestination ) );

	}

}
