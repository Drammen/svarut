package no.kommune.bergen.soa.common.zip;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/** Un-zip files form zip archive */
public class Unzipper {
	final ZipInputStream zipInputStream;
	final Map<String, byte[]> map = new HashMap<String, byte[]>();

	/**
	 * @param inputStream
	 *            - zip archive
	 */
	public Unzipper( InputStream inputStream ) throws IOException {
		zipInputStream = new ZipInputStream( inputStream );
		ZipEntry entry;
		try {
			while ((entry = zipInputStream.getNextEntry()) != null) {
				String name = entry.getName();
				ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
				int b = 0;
				while ((b = zipInputStream.read()) > -1) {
					byteArrayOutputStream.write( b );
				}
				map.put( name, byteArrayOutputStream.toByteArray() );
			}
		} finally {
			zipInputStream.close();
		}
	}

	/** Unzip one file identified by name */
	public byte[] getEntry( String name ) throws IOException {
		return map.get( name );
	}

	/** Iterate over all files in zip archive */
	public Iterator<String> iterator() {
		return map.keySet().iterator();
	}

	public void close() throws IOException {
		zipInputStream.close();
	}

}
