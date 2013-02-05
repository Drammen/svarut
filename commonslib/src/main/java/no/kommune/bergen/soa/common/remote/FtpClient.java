package no.kommune.bergen.soa.common.remote;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.InetAddress;

/** A very simple ftp client */
public class FtpClient implements RemoteFileTransferClient {
	private final Logger log = Logger.getLogger( FtpClient.class );
	protected final String hostname;
	protected final String username;
	protected final String password;
	protected final String ftpDirectory;
	protected final int port;

	public FtpClient( String hostname, int port, String username, String password, String ftpDirectory ) {
		this.hostname = hostname;
		this.username = username;
		this.password = password;
		this.ftpDirectory = ftpDirectory;
		this.port = port;
	}

	public FtpClient( String hostname, String username, String password, String ftpDirectory ) {
		this( hostname, 0, username, password, ftpDirectory );
	}

	//@Override
	public void retrieve( OutputStream outputStream, String filename ) {
		if (log.isDebugEnabled()) log.debug( "retrieve() begin file " + filename );
		try {
			FTPClient ftpClient = null;
			try {
				ftpClient = connect();
				ftpClient.setFileType( FTPClient.BINARY_FILE_TYPE );
				if (!ftpClient.retrieveFile( getFullPath( filename ), outputStream )) {
					throw new RuntimeException("Retrieving file '" + filename + "' was not successful. FTPClient.retrieveFile() returned false");
				}
			} finally {
				disconnect( ftpClient );
			}
		} catch (IOException e) {
			throw new RuntimeException("Problem occurred while retrieving file " + filename, e);
		}
		if (log.isDebugEnabled()) log.debug( "retrieve() end file " + filename );
	}

	public void send( InputStream inputStream, String filename ) {
		if (log.isDebugEnabled()) log.debug( "send() begin file " + filename );
		try {
			FTPClient ftpClient = null;
			try {
				ftpClient = connect();
				ftpClient.setFileType( FTPClient.BINARY_FILE_TYPE );
				if (!ftpClient.storeFile( getFullPath( filename ), inputStream )) {
					throw new RuntimeException( "Sending file was not successfull. FTPClient.storeFile() returned false" );
				}
			} finally {
				disconnect( ftpClient );
			}
		} catch (IOException e) {
			throw new RuntimeException( "Problem occurred while sending file " + filename, e );
		}
		if (log.isDebugEnabled()) log.debug( "send() end file " + filename );
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

	public FTPClient connect() throws IOException {
		if (log.isDebugEnabled()) log.debug( String.format( "Connecting to ftp://%s:%s@%s", username, password, hostname ) );
		FTPClient ftpClient = new FTPClient();
		ftpClient.setConnectTimeout( 30000 );
		if (this.port == 0) {
			ftpClient.connect( InetAddress.getByName( hostname ) );
		} else {
			ftpClient.connect( InetAddress.getByName( hostname ), this.port );
		}
		if (!FTPReply.isPositiveCompletion( ftpClient.getReplyCode() )) {
			ftpClient.disconnect();
			throw new RuntimeException( String.format( "% refused connection.", this.hostname ) );
		}
		ftpClient.login( username, password );
		ftpClient.enterLocalPassiveMode();
		return ftpClient;
	}

	public void disconnect( FTPClient ftpClient ) throws IOException {
		if (ftpClient != null) {
			ftpClient.logout();
			ftpClient.disconnect();
		}
	}

	String getFullPath( String filename ) {
		String path = ftpDirectory + "/" + filename;
		path = path.replaceAll( "/+", "/" );
		return path;
	}

}
