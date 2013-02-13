package no.kommune.bergen.mock.altinn.authorization;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class AltinnAuthorizationDecisionPointExternalFakeService extends HttpServlet {
	private static final long serialVersionUID = 1L;
	final static Logger logger = LoggerFactory.getLogger(AltinnAuthorizationDecisionPointExternalFakeService.class);

	@Override
	public void service( HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.setContentType("text/xml; charset=utf-8");
		InputStream responseInputStream = null;
		InputStreamReader responseInputStreamReader = null;
		BufferedReader responseBufferedReader = null;
		boolean isWdslRequest = (request.getParameter( "wsdl" ) != null || request.getParameter( "WSDL" ) != null);
		String operation = (isWdslRequest ? "AuthorizationDecisionPointExternal.svc.wsdl" : "AltinnAuthorizationDecisionPointExternal.soap.response");
		try {
			responseInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(operation);
			responseInputStreamReader = new InputStreamReader(responseInputStream);
			responseBufferedReader = new BufferedReader(responseInputStreamReader);
			copy(responseBufferedReader, response.getWriter());
		} finally {
			if (responseBufferedReader != null) responseBufferedReader.close();
			if (responseInputStreamReader != null) responseInputStreamReader.close();
			if (responseInputStream != null) responseInputStream.close();
		}
	}

	void copy( BufferedReader reader, PrintWriter writer ) throws IOException {
		while (true) {
			String line = reader.readLine();
			if (line == null) {
				writer.flush();
				break;
			}
			writer.println( line );
		}
		writer.close();
	}
}
