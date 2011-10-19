package no.kommune.bergen.soa.svarut.soap;

import java.util.List;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import no.kommune.bergen.soa.svarut.dto.DokumentRs;
import no.kommune.bergen.soa.svarut.dto.ForsendelseStatus;
import no.kommune.bergen.soa.svarut.dto.ForsendelsesRq;
import no.kommune.bergen.soa.svarut.dto.UserContext;

@WebService(serviceName = "SvarUtService", targetNamespace = "http://bergen.kommune.no/svarut/v1")
public interface SvarUtService {

	@WebMethod
	void confirmRead( @WebParam(name = "userContext") UserContext userContext, @WebParam(name = "forsendelsesId") String id );

	@WebMethod
	void setUnread( @WebParam(name = "userContext") UserContext userContext, @WebParam(name = "forsendelsesId") String id );

	@WebMethod
	void printAgain( @WebParam(name = "userContext") UserContext userContext, @WebParam(name = "forsendelsesId") String id );

	@WebMethod
	List<ForsendelseStatus> retrieveForPerson( @WebParam(name = "userContext") UserContext userContext, @WebParam(name = "fodselsnr") String fnr );

	@WebMethod
	List<ForsendelseStatus> retrieveForOrgenhet( @WebParam(name = "userContext") UserContext userContext, @WebParam(name = "orgnr") String orgnr );

	@WebMethod
	List<ForsendelseStatus> retrieveStatus( @WebParam(name = "userContext") UserContext userContext, @WebParam(name = "ids") String[] ids );

	@WebMethod
	void deleteForsendelse( @WebParam(name = "userContext") UserContext userContext, @WebParam(name = "id") String id );

	@WebMethod
	String send( @WebParam(name = "userContext") UserContext userContext, @WebParam(name = "forsendelse") ForsendelsesRq forsendelsesRq );

	@WebMethod
	DokumentRs retrieveContent( @WebParam(name = "userContext") UserContext userContext, @WebParam(name = "forsendelsesId") String id );

}
