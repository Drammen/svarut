package no.kommune.bergen.soa.common.remote;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;

/** Remote file transfer */
public interface RemoteFileTransferClient {

	void send( InputStream inputStream, String filename );

	void send( File file );

	void retrieve( OutputStream outputStream, String filename );

}
