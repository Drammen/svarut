package no.kommune.bergen.mock.altinn;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AltinnCorrespondenceFakeService extends HttpServlet {
	final static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger( AltinnCorrespondenceFakeService.class );
	private static final long serialVersionUID = 1L;

	@Override
	public void service( HttpServletRequest request, HttpServletResponse response ) throws IOException, ServletException {
		response.setContentType( "text/xml; charset=utf-8" );
		InputStream responseInputStream = null;
		InputStreamReader responseInputStreamReader = null;
		BufferedReader responseBufferedReader = null;
		boolean isWdslRequest = (request.getParameter( "wsdl" ) != null || request.getParameter( "WSDL" ) != null);
		String operation = (isWdslRequest ? "CorrespondenceAgencyExternalBasic.wsdl" : "CorrespondenceAgencyExternalBasic.soap.response");
		logger.info( "Operation=" + operation );
		try {
			responseInputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream( operation );
			responseInputStreamReader = new InputStreamReader( responseInputStream );
			responseBufferedReader = new BufferedReader( responseInputStreamReader );
			copy( responseBufferedReader, response.getWriter() );
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
