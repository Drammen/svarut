package no.kommune.bergen.soa.svarut.dao;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import no.kommune.bergen.soa.common.pdf.PdfGenerator;
import no.kommune.bergen.soa.util.Files;

/** The document store */
public class FileStore {
	private static final String prefix = "", suffix = ".pdf";
	private final File directory;
	private final PdfGenerator pdfGenerator;

	public FileStore( String storePath, PdfGenerator pdfGenerator ) {
		this.directory = new File( storePath );
		if(!this.directory.exists()) directory.mkdirs();
		this.pdfGenerator = pdfGenerator;
	}

	public String save( InputStream inputStream, String identifier ) {
		File file = null;
		try {
			file = new File( this.directory, prefix + identifier + suffix );
			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			Files.stream( inputStream, outputStream );
			if (!pdfGenerator.isValidPdf( new ByteArrayInputStream( outputStream.toByteArray() ) )) {
				createPdf( file, outputStream.toString() );
			} else {
				Files.writeToFile( file, new ByteArrayInputStream( outputStream.toByteArray() ) );
			}
		} catch (IOException e) {
			throw new RuntimeException( "Trouble saving file: " + file, e );
		}
		return file.getName();
	}

	private void createPdf( File file, String fileContent ) throws IOException {
		Files.writeToFile( file, this.pdfGenerator.createPdf( new HashMap<String, String>(), fileContent ) );
	}

	public File getFile( String filename ) {
		return new File( directory, filename );
	}

	public void remove( String filename ) {
		File file = new File( directory, filename );
		if (!file.delete()) return;
	}

	public InputStream fetch( String filename ) {
		File file = new File( directory, filename );
		try {
			return new FileInputStream( file );
		} catch (FileNotFoundException e) {
			throw new RuntimeException( "Can't find file: " + file.getPath(), e );
		}
	}

}
