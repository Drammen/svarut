package org.svarut.sample.rest;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import no.kommune.bergen.soa.svarut.JobController;
import no.kommune.bergen.soa.svarut.JuridiskEnhetFactory;
import no.kommune.bergen.soa.svarut.ServiceDelegate;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;
import no.kommune.bergen.soa.svarut.dto.ForsendelseRest;
import no.kommune.bergen.soa.svarut.dto.ForsendelseStatus;
import no.kommune.bergen.soa.svarut.dto.ForsendelseStatusRest;
import no.kommune.bergen.soa.svarut.util.ForsendelseMapper;
import no.kommune.bergen.soa.util.XMLDatatypeUtil;

import org.apache.log4j.Logger;

/** Rest (Jax-rs) service */
@Path("/forsendelsesservice")
public class SvarUtSampleServiceRest {
	final Logger logger = Logger.getLogger( SvarUtSampleServiceRest.class );
	private final ServiceDelegate serviceDelegate;
	private final ForsendelseMapper forsendelseMapper = new ForsendelseMapper();

	private JobController controller;

	public JobController getController() {
		return controller;
	}

	public void setController(JobController controller) {
		this.controller = controller;
	}

	@XmlRootElement(name = "List")
	@XmlSeeAlso(ForsendelseStatus.class)
	private static class ForsendelseStatusList {
		public ArrayList<ForsendelseStatus> list;
	}

	public SvarUtSampleServiceRest( ServiceDelegate service ) {
		this.serviceDelegate = service;
	}

	@GET
	@Produces("text/plain;charset=UTF-8")
	@Path("/statistikk")
	public String statistics() {
		return this.serviceDelegate.statistics();
	}

	@GET
	@Path("/status/{id}")
	@Produces("text/plain;charset=UTF-8")
	public String retrieveStatus( @PathParam("id") String id ) {
		return this.serviceDelegate.retrieveStatus( id ).replace( ',', '\n' );
	}

	@GET
	@Path("/statuslist/")
	@Produces("text/xml")
	public List<ForsendelseStatusRest> retrieveStatusXml( @QueryParam("ids") String idsCommaSeparated ) {
		List<ForsendelseStatusRest> statusList = new ArrayList<ForsendelseStatusRest>();
		String[] ids = idsCommaSeparated.split( "," );
		List<Forsendelse> forsendelseList = serviceDelegate.retrieveStatus( ids );
		for (Forsendelse f : forsendelseList) {
			statusList.add( mapForsendelsesStatusRest( f ) );
		}
		return statusList;
	}

	private ForsendelseStatusRest mapForsendelsesStatusRest( Forsendelse f ) {
		ForsendelseStatusRest forsendelseStatusRest = new ForsendelseStatusRest();
		forsendelseStatusRest.setForsendelseStatus( mapForsendelsesStatus( f ) );
		return forsendelseStatusRest;
	}

	private ForsendelseStatus mapForsendelsesStatus( Forsendelse f ) {
		ForsendelseStatus forsendelseStatus = new ForsendelseStatus();
		forsendelseStatus.setForsendelse( forsendelseMapper.toDto( f ) );
		forsendelseStatus.setForsendelsesdato( XMLDatatypeUtil.toXMLGregorianCalendar( f.getSendt() ) );
		forsendelseStatus.setId( f.getId() );
		forsendelseStatus.setLestElektronisk( XMLDatatypeUtil.toXMLGregorianCalendar( f.getLest() ) );
		forsendelseStatus.setSendtBrevpost( XMLDatatypeUtil.toXMLGregorianCalendar( f.getUtskrevet() ) );
		forsendelseStatus.setSendtNorgedotno( XMLDatatypeUtil.toXMLGregorianCalendar( f.getNorgedotno() ) );
		return forsendelseStatus;
	}

	@GET
	@Path("/print/{id}")
	@Produces("text/plain;charset=UTF-8")
	public String print( @PathParam("id") String id ) {
		this.serviceDelegate.print( id );
		return this.serviceDelegate.retrieveStatus( id ).replace( ',', '\n' );
	}

	@GET
	@Path("/dispatch")
	@Produces("text/plain;charset=UTF-8")
	public String dispatch() {
		this.serviceDelegate.dispatch();
		return "Dispatch invoked";
	}

	@GET
	@Path("/retrieve/{juridiskEnhet}/{id}")
	@Produces("text/xml")
	public ForsendelseRest retrieve( @PathParam("id") String id, @PathParam("juridiskEnhet") String juridiskEnhet ) {
		no.kommune.bergen.soa.svarut.dto.Forsendelse forsendelse = new ForsendelseMapper().toDto( this.serviceDelegate.retrieve( id, JuridiskEnhetFactory.create( juridiskEnhet ) ) );
		ForsendelseRest result = new ForsendelseRest();
		result.setForsendelse( forsendelse );
		return result;
	}

	@GET
	@Path("/retrieve/ignorerte")
	@Produces("text/xml")
	public ForsendelseStatusList retrieveIgnored() {
		try {
			List<Forsendelse> ignored = this.serviceDelegate.retrieveIgnored();
			ForsendelseStatusList forsendelseStatusList = new ForsendelseStatusList();
			forsendelseStatusList.list = new ArrayList<ForsendelseStatus>();
			for (Forsendelse f : ignored) {
				forsendelseStatusList.list.add( mapForsendelsesStatus( f ) );
			}
			return forsendelseStatusList;
		} catch (RuntimeException e) {
			logger.error( "retrieveIgnored() has problems", e );
			throw e;
		}
	}

	@POST
	@Path("/updateSentToPrint/{forsendelsesId}/{dateInMillisec}")
	@Produces("text/plain")
	public String updateSentToPrint(@PathParam("forsendelsesId") String forsendelsesId, @PathParam("dateInMillisec") String dateInMillisec) {
		serviceDelegate.updateSentToPrint(forsendelsesId, new Date(Long.parseLong(dateInMillisec)));
		return "OK";
	}

	@GET
	@Path("/retrieve/failedToPrint")
	@Produces("text/plain")
	public String retrieveFailedToPrint() {
		try {
			List<String> failedToPrint = this.serviceDelegate.retrieveFailedToPrint();
			return failedToPrint.toString();
		} catch (RuntimeException e) {
			logger.error( "retrieveFailedToPrint() has problems", e );
			throw e;
		}
	}

	@GET
	@Path("/start/importPrintStatements")
	public void importPrintStatements() {
		try {
			this.serviceDelegate.importPrintStatements();
		} catch (RuntimeException e) {
			logger.error( "importPrintStatements() has problems", e );
			throw e;
		}
	}

	@GET
	@Path("/download/{juridiskEnhet}/{id}")
	@Produces("application/pdf")
	public InputStream retrieveContent( @Context Request rs, @PathParam("id") String id, @PathParam("juridiskEnhet") String juridiskEnhet ) {
		return this.serviceDelegate.retrieveContent( id, JuridiskEnhetFactory.create( juridiskEnhet ) );
	}

	@GET
	@Path("/downloadNoAuthorization/{id}")
	@Produces("application/pdf")
	public InputStream retrieveContentNoAuthorization( @Context Request rs, @PathParam("id") String id ) {
		return this.serviceDelegate.retrieveContentNoAuthorization( id );
	}

	@GET
	@Path("/waitTillFinished")
	@Produces("text/plain")
	public String waitTillFinished(){
		controller.waitTillFinished();
		return "Done";
	}

}
