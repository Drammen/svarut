package no.kommune.bergen.soa.svarut;

import java.io.InputStream;

import no.kommune.bergen.soa.svarut.dao.ForsendelsesArkiv;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;

/** Interface for providers of print and regular mail distributions */
public interface PrintServiceProvider {

	/** Returns print reference id */
	String sendToPrint( InputStream inputStream, Forsendelse forsendelse );

	void importPrintStatements( ForsendelsesArkiv forsendelsesArkiv );

}
