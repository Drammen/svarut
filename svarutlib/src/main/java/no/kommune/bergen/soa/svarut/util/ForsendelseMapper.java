package no.kommune.bergen.soa.svarut.util;

import java.util.ArrayList;
import java.util.List;

import no.kommune.bergen.soa.svarut.dto.Adresse123;
import no.kommune.bergen.soa.svarut.dto.Forsendelse;
import no.kommune.bergen.soa.svarut.dto.ForsendelseStatus;
import no.kommune.bergen.soa.svarut.dto.ShipmentPolicy;
import no.kommune.bergen.soa.util.XMLDatatypeUtil;

import org.apache.log4j.Logger;

/** Mapping between domain object and data transfer objects */
public class ForsendelseMapper {
	private static Logger log = Logger.getLogger( ForsendelseMapper.class );

	public Forsendelse toDto( no.kommune.bergen.soa.svarut.domain.Forsendelse f ) {
		return toDto( new Forsendelse(), f );
	}

	public Forsendelse toDto( Forsendelse result, no.kommune.bergen.soa.svarut.domain.Forsendelse f ) {
		if (f == null || result == null) {
			return null;
		}
		result.setFodselsnummer(f.getFnr());
		String orgnr = f.getOrgnr();
		if (orgnr != null) {
			result.setOrgnr( Integer.parseInt( orgnr ) );
		}
		result.setNavn( f.getNavn() );
		result.setAvsenderNavn( f.getAvsenderNavn() );

		Adresse123 adresse = new Adresse123();
		result.setAdresse( adresse );
		adresse.setAdresse1( f.getAdresse1() );
		adresse.setAdresse2( f.getAdresse2() );
		adresse.setAdresse3( f.getAdresse3() );
		adresse.setLand( f.getLand() );
		adresse.setPostnr( f.getPostnr() );
		adresse.setPoststed( f.getPoststed() );

		Adresse123 avsenderAdresse = new Adresse123();
		result.setAvsenderadresse( avsenderAdresse );
		avsenderAdresse.setAdresse1( f.getAvsenderAdresse1() );
		avsenderAdresse.setAdresse2( f.getAvsenderAdresse2() );
		avsenderAdresse.setAdresse3( f.getAvsenderAdresse3() );
		avsenderAdresse.setPostnr( f.getAvsenderPostnr() );
		avsenderAdresse.setPoststed( f.getAvsenderPoststed() );
		result.setAppid( f.getAppid() );
		result.setTittel( f.getTittel() );
		result.setMeldingstekst( f.getMeldingsTekst() );
		result.setEpost( f.getEmail() );
		result.setReplyTo( f.getReplyTo() );
		result.setFargePrint( f.isPrintFarge() );
		try {
			result.setForsendelsesMate( ShipmentPolicy.fromValue( f.getShipmentPolicy() ) );
		} catch (IllegalArgumentException e) {
			// ignore
		}
		return result;
	}

	public no.kommune.bergen.soa.svarut.domain.Forsendelse fromDto( no.kommune.bergen.soa.svarut.dto.Forsendelse rq ) {
		no.kommune.bergen.soa.svarut.domain.Forsendelse result = new no.kommune.bergen.soa.svarut.domain.Forsendelse();
		if (rq == null) {
			if (log.isDebugEnabled()) log.debug( "Request er null returnerer null" );
			return null;
		}
		result.setFnr( rq.getFodselsnummer() );
		result.setNavn( rq.getNavn() );
		Adresse123 adresse = rq.getAdresse();
		result.setAdresse1( (adresse == null) ? null : adresse.getAdresse1() );
		result.setAdresse2( (adresse == null) ? null : adresse.getAdresse2() );
		result.setAdresse3( (adresse == null) ? null : adresse.getAdresse3() );
		result.setPostnr( (adresse == null) ? null : adresse.getPostnr() );
		result.setPoststed( (adresse == null) ? null : adresse.getPoststed() );
		result.setLand( (adresse == null) ? null : adresse.getLand() );
		int orgnr = rq.getOrgnr();
		result.setOrgnr( (orgnr == 0) ? null : String.valueOf( orgnr ) );
		result.setAvsenderNavn( rq.getAvsenderNavn() );
		Adresse123 avsenderAdresse = rq.getAvsenderadresse();
		result.setAvsenderAdresse1( (avsenderAdresse == null) ? null : avsenderAdresse.getAdresse1() );
		result.setAvsenderAdresse2( (avsenderAdresse == null) ? null : avsenderAdresse.getAdresse2() );
		result.setAvsenderAdresse3( (avsenderAdresse == null) ? null : avsenderAdresse.getAdresse3() );
		result.setAvsenderPostnr( (avsenderAdresse == null) ? null : avsenderAdresse.getPostnr() );
		result.setAvsenderPoststed( (avsenderAdresse == null) ? null : avsenderAdresse.getPoststed() );
		result.setAppid( rq.getAppid() );
		result.setTittel( rq.getTittel() );
		result.setMeldingsTekst( rq.getMeldingstekst() );
		result.setEmail( rq.getEpost() );
		result.setReplyTo( rq.getReplyTo() );
		result.setPrintFarge( rq.isFargePrint() );
		ShipmentPolicy forsendelsesMate = rq.getForsendelsesMate();
		if (forsendelsesMate != null) {
			result.setShipmentPolicy( forsendelsesMate.value() );
		}
		if (log.isDebugEnabled()) log.debug( "Returning " + result );
		return result;
	}

	public no.kommune.bergen.soa.svarut.dto.ForsendelseStatus mapForsendelseStatus( no.kommune.bergen.soa.svarut.domain.Forsendelse f ) {
		if (f == null) return null;
		ForsendelseStatus status = new ForsendelseStatus();
		status.setId( f.getId() );
		status.setForsendelse( this.toDto( f ) );
		status.setForsendelsesdato( XMLDatatypeUtil.toXMLGregorianCalendar( f.getSendt() ) );
		status.setLestElektronisk( XMLDatatypeUtil.toXMLGregorianCalendar( f.getLest() ) );
		status.setSendtBrevpost( XMLDatatypeUtil.toXMLGregorianCalendar( f.getUtskrevet() ) );
		status.setSendtNorgedotno( XMLDatatypeUtil.toXMLGregorianCalendar( f.getNorgedotno() ) );
		status.setPrintId( f.getPrintId() );
		status.setTidspunktPostlagt( XMLDatatypeUtil.toXMLGregorianCalendar( f.getTidspunktPostlagt() ) );
		status.setAntallSider( f.getAntallSider() );
		status.setAntallSiderPostlagt( f.getAntallSiderPostlagt() );
		status.setSendtAltinn(XMLDatatypeUtil.toXMLGregorianCalendar(f.getAltinn() ));
		return status;
	}

	public List<no.kommune.bergen.soa.svarut.dto.ForsendelseStatus> mapForsendelseStatusList( List<no.kommune.bergen.soa.svarut.domain.Forsendelse> forsendelseList ) {
		List<ForsendelseStatus> statusList = new ArrayList<ForsendelseStatus>();
		for (no.kommune.bergen.soa.svarut.domain.Forsendelse f : forsendelseList) {
			statusList.add( mapForsendelseStatus( f ) );
		}
		return statusList;
	}

}
