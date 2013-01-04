package no.kommune.bergen.soa.svarut.soap;

import java.io.InputStream;
import java.util.List;

import javax.activation.DataHandler;
import javax.jws.WebParam;

import no.kommune.bergen.soa.common.exception.UserException;
import no.kommune.bergen.soa.svarut.JobController;
import no.kommune.bergen.soa.svarut.ServiceDelegate;
import no.kommune.bergen.soa.svarut.ServiceDelegateImpl;
import no.kommune.bergen.soa.svarut.domain.Fodselsnr;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;
import no.kommune.bergen.soa.svarut.domain.JuridiskEnhet;
import no.kommune.bergen.soa.svarut.domain.Orgnr;
import no.kommune.bergen.soa.svarut.dto.DokumentRs;
import no.kommune.bergen.soa.svarut.dto.ForsendelseStatus;
import no.kommune.bergen.soa.svarut.dto.ForsendelsesRq;
import no.kommune.bergen.soa.svarut.dto.UserContext;
import no.kommune.bergen.soa.svarut.util.ForsendelseMapper;
import no.kommune.bergen.soa.svarut.util.InputStreamDataSource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SvarUtServiceImpl implements SvarUtService {
	final Logger logger = Logger.getLogger( SvarUtServiceImpl.class );
	private final ForsendelseMapper forsendelseMapper = new ForsendelseMapper();

	@Autowired
	protected ServiceDelegate serviceDelegate;

	@Autowired
	private JobController jobController;

	@Override
	public void confirmRead( UserContext userContext, String id ) {
		try {
			serviceDelegate.confirm( id );
		} catch (Exception e) {
			throw ServiceDelegateImpl.handleException( e );
		}
	}

	@Override
	public void printAgain( UserContext userContext, String id ) {
		try {
			serviceDelegate.print( id );
		} catch (Exception e) {
			throw ServiceDelegateImpl.handleException( e );
		}
	}

	@Override
	public void setUnread( UserContext userContext, String id ) {
		try {
			serviceDelegate.setUnread( id );
		} catch (Exception e) {
			throw ServiceDelegateImpl.handleException( e );
		}
	}

	@Override
	public List<no.kommune.bergen.soa.svarut.dto.ForsendelseStatus> retrieveForPerson( UserContext userContext, String fnr ) {
		return retrieveForJuridiskEnhet( new Fodselsnr( fnr ) );
	}

	@Override
	public List<ForsendelseStatus> retrieveForOrgenhet( @WebParam(name = "userContext") UserContext userContext, @WebParam(name = "orgnr") String orgnr ) {
		return retrieveForJuridiskEnhet( new Orgnr( orgnr ) );
	}

	private List<ForsendelseStatus> retrieveForJuridiskEnhet( JuridiskEnhet enhet ) {
		try {
			List<Forsendelse> forsendelser = serviceDelegate.retrieveList( enhet );
			return forsendelseMapper.mapForsendelseStatusList( forsendelser );
		} catch (Exception e) {
			throw ServiceDelegateImpl.handleException( e );
		}
	}

	@Override
	public List<ForsendelseStatus> retrieveStatus( UserContext userContext, String[] ids ) {
		List<Forsendelse> forsendelseList = serviceDelegate.retrieveStatus( ids );
		return forsendelseMapper.mapForsendelseStatusList( forsendelseList );
	}

	@Override
	public void deleteForsendelse( @WebParam(name = "userContext") UserContext userContext, @WebParam(name = "id") String id ) {
		serviceDelegate.stop( id );
		serviceDelegate.remove( id );
	}

	@Override
	public DokumentRs retrieveContent( UserContext userContext, String id ) {
		try {
			InputStream inputStream = serviceDelegate.retrieveContent( id, userContext.getUserid() );
			InputStreamDataSource dataSource = new InputStreamDataSource( inputStream, "application/pdf", id );
			DataHandler dataHandler = new DataHandler( dataSource );
			DokumentRs rs = new DokumentRs();
			rs.setFilnavn( id );
			rs.setData( dataHandler );
			return rs;
		} catch (Exception e) {
			throw ServiceDelegateImpl.handleException( e );
		}
	}

	@Override
	/** Returns filename */
	public String send( UserContext userContext, ForsendelsesRq forsendelsesRq ) {
		logger.info( "send() - called" );
		try {
			DataHandler dataHandler = forsendelsesRq.getData();
			if (dataHandler == null) throw new UserException( "DataHandler is null" );
			Forsendelse forsendelse = forsendelseMapper.fromDto( forsendelsesRq.getForsendelse() );
			if (forsendelse == null) throw new UserException( "Forsendelse is null" );
			String id = serviceDelegate.send(forsendelse, dataHandler.getInputStream());
			jobController.triggerSend(forsendelse);
			return id;
		} catch (Exception e) {
			throw ServiceDelegateImpl.handleException( e );
		}
	}

	public void setJobController(JobController jobController) {
		this.jobController = jobController;
	}
}
