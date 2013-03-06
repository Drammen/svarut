package no.kommune.bergen.soa.svarut.altinn.authorization;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;

import no.kommune.bergen.soa.svarut.altinn.authorization.client.AltinnAuthorizationDesicionPointExternalClient;
import no.kommune.bergen.soa.svarut.altinn.authorization.client.AltinnAuthorizationDesicionPointExternalSettings;
import no.kommune.bergen.soa.svarut.altinn.authorization.xacml.AltinnAuthorizationDesicionPointExternalXACMLUtil;

public class MockAuthorizationDecisionPointExternal extends AltinnAuthorizationDesicionPointExternalClient {

	private final String authorizedFnr = "02035701829";
	private final String authorizedOrgnr = "910824929";

	private String XACMLFile = null;

	public MockAuthorizationDecisionPointExternal(AltinnAuthorizationDesicionPointExternalSettings settings) {
		super(settings);
	}

	public String getXACMLFile() {
		return XACMLFile;
	}

	public void setXACMLFile(String xACMLFile) {
		XACMLFile = xACMLFile;
	}

	@Override
	public boolean authorizeAccessExternal(String fodselsNr, String orgNr) {
		if(XACMLFile != null) {
			try {
				return AltinnAuthorizationDesicionPointExternalXACMLUtil.parseXACMLResponseAndVerifyPermitted(parseXACMLFile());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		if(fodselsNr.equals(authorizedFnr) && orgNr.equals(authorizedOrgnr))
			return true;

		return false;
	}

	public String parseXACMLFile() throws IOException {
		OutputStream outputStream = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(outputStream);
		InputStream responseInputStream = null;
		InputStreamReader responseInputStreamReader = null;
		BufferedReader responseBufferedReader = null;
		String operation = XACMLFile;
		try {
			responseInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(operation);
			responseInputStreamReader = new InputStreamReader(responseInputStream);
			responseBufferedReader = new BufferedReader(responseInputStreamReader);
			copy(responseBufferedReader, writer);
		} finally {
			if (responseBufferedReader != null) responseBufferedReader.close();
			if (responseInputStreamReader != null) responseInputStreamReader.close();
			if (responseInputStream != null) responseInputStream.close();
		}
		return outputStream.toString();
	}


	private void copy( BufferedReader reader, PrintWriter writer ) throws IOException {
		while (true) {
			String line = reader.readLine();
			if (line == null) {
				writer.flush();
				break;
			}
			writer.print( line );
		}
		writer.close();
	}
}
