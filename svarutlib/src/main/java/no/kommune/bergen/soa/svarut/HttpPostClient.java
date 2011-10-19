package no.kommune.bergen.soa.svarut;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import no.kommune.bergen.soa.common.remote.RemoteFileTransferClient;
import no.kommune.bergen.soa.common.zip.Zipper;

import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpState;
import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.log4j.Logger;

public class HttpPostClient implements RemoteFileTransferClient {
	private final Logger logger = Logger.getLogger( HttpPostClient.class );
	private final String user;
	private final String password;
	private final String uri;
	private int connectionTimeoutInMs = 8000;

	public void retrieve( OutputStream outputStream, String filename ) {
		throw new RuntimeException( "Not implemented for HttpPostClient" );
	}

	public HttpPostClient( String uri, String user, String password ) {
		this.user = user;
		this.password = password;
		this.uri = uri;
	}

	public void send( InputStream inputStream, String filename ) {
		int response = 0;
		PostMethod postMethod = newPostMethod( filename, inputStream, this.uri );
		try {
			HttpClient client = newHttpClient( this.uri );
			response = client.executeMethod( postMethod );
		} catch (Exception e) {
			throw new RuntimeException( String.format( "Problems posting to %s,  filename: %s", this.uri, filename ), e );
		} finally {
			if (postMethod != null) postMethod.releaseConnection();
		}
		if (response != 200) {
			throw new RuntimeException( String.format( "Unable to post to %s, filename: %s, response kode: %s, RequestLength: %s", this.uri, filename, response, postMethod.getRequestEntity().getContentLength() ) );
		}
	}

	private PostMethod newPostMethod( String filename, InputStream inputstream, String uri ) {
		PostMethod postMethod = new PostMethod( uri );
		addMultipartParameters( filename, inputstream, postMethod );
		return postMethod;
	}

	private void addMultipartParameters( String filename, InputStream inputstream, PostMethod postMethod ) {
		if (logger.isDebugEnabled()) logger.debug( "userid=" + this.user + ",  password=" + this.password + ", filename=" + filename );
		Part[] parts = { new StringPart( "userid", this.user ), new StringPart( "password", this.password ), new StringPart( "email", "" ), new StringPart( "description", "" ), newFilePart( filename, inputstream ), };
		postMethod.setRequestEntity( new MultipartRequestEntity( parts, postMethod.getParams() ) );
	}

	private FilePart newFilePart( String filename, InputStream inputstream ) {
		String zipFilename = filenameDotZip( filename );
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		zipItUp( inputstream, filename, outputStream );
		byte[] data = outputStream.toByteArray();
		FilePart filePart = new FilePart( filename, new ByteArrayPartSource( zipFilename, data ) );
		return filePart;
	}

	private HttpClient newHttpClient( String uri ) throws URIException {
		HttpClient client = new HttpClient();
		includeBasicAuthentication( uri, client );
		accomodateForHttpProxy( client );
		setConnectionTimeout( client, this.connectionTimeoutInMs );
		return client;
	}

	private void includeBasicAuthentication( String uri, HttpClient client ) throws URIException {
		HostConfiguration host = client.getHostConfiguration();
		host.setHost( new URI( uri, true ) );
		Credentials credentials = new UsernamePasswordCredentials( this.user, this.password );
		AuthScope authScope = new AuthScope( host.getHost(), host.getPort() );
		HttpState state = client.getState();
		state.setCredentials( authScope, credentials );
	}

	private void setConnectionTimeout( HttpClient client, int connectionTimeoutInMs ) {
		HttpConnectionManagerParams params = client.getHttpConnectionManager().getParams();
		params.setConnectionTimeout( connectionTimeoutInMs );
	}

	private void accomodateForHttpProxy( HttpClient client ) {
		String httpProxyHost = System.getProperty( "http.proxyHost" );
		String httpProxyPort = System.getProperty( "http.proxyPort" );
		boolean useProxy = !this.uri.contains( "localhost" );
		if (useProxy && httpProxyHost != null && httpProxyPort != null) {
			client.getHostConfiguration().setProxy( httpProxyHost, Integer.parseInt( httpProxyPort.trim() ) );
		}
	}

	void zipItUp( InputStream inputstream, String filename, OutputStream outputStream ) {
		Zipper zipper = new Zipper( outputStream );
		try {
			zipper.addEntry( filename, inputstream );
			zipper.close();
		} catch (IOException e1) {
			throw new RuntimeException( "Failed to zip up " + filename, e1 );
		}
	}

	static String filenameDotZip( String filename ) {
		String[] parts = filename.split( "\\." );
		int length = parts.length;
		if (length < 1) {
			return "";
		}
		String result = parts[0] + ".";
		for (int i = 1; i < (length - 1); i++) {
			result = result + parts[i] + ".";
		}
		return result + "zip";
	}

	public void send( File file ) {
		try {
			InputStream inputStream = new FileInputStream( file );
			try {
				send( inputStream, file.getName() );
			} finally {
				try {
					if (inputStream != null) inputStream.close();
				} catch (IOException e) {
					throw new RuntimeException( "Problems closing file " + file.getAbsolutePath(), e );
				}
			}
		} catch (FileNotFoundException e) {
			throw new RuntimeException( "Could not find file " + file.getAbsolutePath(), e );
		}
	}

	public int getConnectionTimeoutInMs() {
		return connectionTimeoutInMs;
	}

	public void setConnectionTimeoutInMs( int connectionTimeoutInMs ) {
		this.connectionTimeoutInMs = connectionTimeoutInMs;
	}
}
