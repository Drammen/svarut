package org.svarut.sample.printprovider;

import java.io.InputStream;

import no.kommune.bergen.soa.svarut.PrintServiceProvider;
import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkiv;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

/** PrintServiceProvider skriver ut dokumenter pakker frakerer og postlegger forsendelser. Typisk er dette en ekstern leverandør. */
@Service()
public class MyPrintServiceProvider implements PrintServiceProvider {
	private final Logger log = Logger.getLogger( MyPrintServiceProvider.class );

	public MyPrintServiceProvider(  ) {

	}

	@Override
	public String sendToPrint( InputStream inputStream, Forsendelse f ) {
		String filename = generateFileName( f );
		if (log.isDebugEnabled()) log.debug( "Transferring for printing " + filename );
		return filename;
	}

	String generateFileName( Forsendelse f ) {
		final String CUSTOMER_ID = "MYID";
		StringBuilder sb = new StringBuilder();
		sb.append( CUSTOMER_ID );
		sb.append( "_" );
		sb.append( f.getId() );
		return sb.toString();
	}

	@Override
	public void importPrintStatements( ForsendelsesArkiv forsendelsesArkiv ) {
		log.info("ImportPrintStatements called");
	}

}
