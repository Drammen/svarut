package no.kommune.bergen.soa.svarut.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.activation.DataSource;

public class InputStreamDataSource implements DataSource {
	private final String contentType, name;
	private final InputStream inputStream;

	public InputStreamDataSource( InputStream inputStream, String contentType, String name ) {
		this.inputStream = inputStream;
		this.contentType = contentType;
		this.name = name;
	}

	@Override
	public String getContentType() {
		return this.contentType;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return this.inputStream;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public OutputStream getOutputStream() throws IOException {
		throw new UnsupportedOperationException();
	}

}
