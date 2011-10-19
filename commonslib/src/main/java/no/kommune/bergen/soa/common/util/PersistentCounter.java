package no.kommune.bergen.soa.common.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/** A simple counter that will survive process halts */
public class PersistentCounter {
	private final File file;

	public PersistentCounter( File file ) {
		this.file = file;
	}

	public PersistentCounter( String fileName ) {
		this( new File( fileName ) );
	}

	public void increment() {
		try {
			long counter = read();
			counter++;
			write( counter );
		} catch (IOException e) {
			throw new RuntimeException( "PerisetentCounter.increment() failed.", e );
		}
	}

	void write( long counter ) throws IOException {
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter( this.file );
			fileWriter.write( Long.toString( counter ) );
		} finally {
			if (fileWriter != null) fileWriter.close();
		}
	}

	long read() {
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		try {
			if (!this.file.exists()) return 0;
			fileReader = new FileReader( this.file );
			bufferedReader = new BufferedReader( fileReader );
			String s = bufferedReader.readLine();
			return Long.parseLong( s );
		} catch (Exception e) {} finally {
			try {
				if (bufferedReader != null) bufferedReader.close();
			} catch (IOException e) {}
			try {
				if (fileReader != null) fileReader.close();
			} catch (IOException e) {}
		}
		return 0;
	}

}
