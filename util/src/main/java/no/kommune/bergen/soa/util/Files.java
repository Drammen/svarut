package no.kommune.bergen.soa.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Files {

	/** File utliity class */
	public static void copy( File source, String destinationDir ) throws IOException {
		File target = new File( destinationDir, source.getName() );
		copy( source, target );
	}

	public static void copy( File source, File target ) throws IOException {
		FileInputStream inputStream = null;
		FileOutputStream outputStream = null;
		try {
			inputStream = new FileInputStream( source );
			outputStream = new FileOutputStream( target );
			byte[] buffer = new byte[1024 * 4];
			for (int len = 0; (len = inputStream.read( buffer )) > -1;) {
				outputStream.write( buffer, 0, len );
			}
		} finally {
			if (outputStream != null) outputStream.close();
			if (inputStream != null) inputStream.close();
		}

	}

	public static void writeToFile( String fileName, byte[] data ) throws IOException {
		writeToFile( new File( fileName ), data );
	}

	public static void writeToFile( File file, byte[] data ) throws IOException {
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream( file );
			fileOutputStream.write( data );
		} finally {
			if (fileOutputStream != null) fileOutputStream.close();
		}
	}

	public static void writeToFile( File file, InputStream inputStream ) throws IOException {
		FileOutputStream fileOutputStream = null;
		try {
			fileOutputStream = new FileOutputStream( file );
			byte[] data = new byte[512 * 4];
			for (int count = 0; (count = inputStream.read( data )) != -1;) {
				fileOutputStream.write( data, 0, count );
			}
		} finally {
			if (fileOutputStream != null) fileOutputStream.close();
		}
	}

	public static byte[] getFileContent( String fileName ) throws IOException {
		return getFileContent( new File( fileName ) );
	}

	public static byte[] getFileContent( File file ) throws IOException {
		byte[] fileContent = null;
		FileInputStream fileInputStream = null;
		long fileSize = file.length();
		try {
			if (fileSize >= Integer.MAX_VALUE) {
				throw new IOException( file.getPath() + " is too large: " + fileSize );
			}
			fileContent = new byte[(int) fileSize];
			fileInputStream = new FileInputStream( file );
			fileInputStream.read( fileContent );
		} finally {
			if (fileInputStream != null) {
				fileInputStream.close();
			}
		}
		return fileContent;
	}

	public static void stream( InputStream inputStream, OutputStream outputStream ) throws IOException {
		int b = 0;
		while ((b = inputStream.read()) != -1) {
			outputStream.write( b );
		}
	}

}
