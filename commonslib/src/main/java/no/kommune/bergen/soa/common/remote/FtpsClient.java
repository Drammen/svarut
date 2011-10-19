package no.kommune.bergen.soa.common.remote;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.commons.net.ftp.FTPSClient;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetAddress;
import java.security.NoSuchAlgorithmException;

/** A very simple ftps client */
public class FtpsClient extends FtpClient {
	private final Logger log = Logger.getLogger( FtpsClient.class );
	public final Protocol protocol;

	public FtpsClient( String hostname, String username, String password, String ftpDirectory, Protocol protocol ) {
		super( hostname, username, password, ftpDirectory );
		this.protocol = protocol;
	}

	@Override
	public FTPClient connect() throws IOException {
		if (log.isDebugEnabled()) log.debug( String.format( "Connecting to ftps://%s:%s@%s over %s", username, password, hostname, this.protocol ) );
		FTPSClient ftpClient;
		try {
			ftpClient = new FTPSClient( this.protocol.asString() );
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException( String.format( "% does not understand %s", this.hostname, this.protocol ), e );
		}
		ftpClient.connect( InetAddress.getByName( hostname ) );
		if (!FTPReply.isPositiveCompletion( ftpClient.getReplyCode() )) {
			ftpClient.disconnect();
			throw new RuntimeException( String.format( "% refused connection over %s", this.hostname, this.protocol ) );
		}
		ftpClient.login( this.username, this.password );
		ftpClient.enterLocalPassiveMode();
		return ftpClient;
	}

	public static enum Protocol {
		SSL("SSL"), TLS("TLS"); // "Med" må avtales spesielt
		private String protocol;

		Protocol( String protocol ) {
			this.protocol = protocol;
		}

		public String asString() {
			return this.protocol;
		}
	}
}
