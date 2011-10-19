package no.kommune.bergen.soa.common.zip;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Zipper {

	final ZipOutputStream zipOutputStream;

	/**
	 * @param outputStream
	 *            - zip archive
	 */
	public Zipper( OutputStream outputStream ) {
		zipOutputStream = new ZipOutputStream( outputStream );
	}

	/** Add file to archive */
	public void addEntry( String name, byte[] data ) throws IOException {
		if (name == null) {
			throw new IllegalArgumentException( "name == null" );
		}
		if (data == null) {
			throw new IllegalArgumentException( "data == null" );
		}
		ZipEntry entry = new ZipEntry( name );
		zipOutputStream.putNextEntry( entry );
		zipOutputStream.write( data );
	}

	/** Add file to archive */
	public void addEntry( String name, InputStream inputStream ) throws IOException {
		if (name == null) {
			throw new IllegalArgumentException( "name == null" );
		}
		if (inputStream == null) {
			throw new IllegalArgumentException( "inputStream == null" );
		}
		ZipEntry entry = new ZipEntry( name );
		zipOutputStream.putNextEntry( entry );
		byte[] data = new byte[512 * 4];
		for (int count = 0; (count = inputStream.read( data )) != -1;) {
			zipOutputStream.write( data, 0, count );
		}
	}

	public void close() throws IOException {
		zipOutputStream.close();
	}

}
