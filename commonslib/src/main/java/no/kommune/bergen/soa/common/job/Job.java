package no.kommune.bergen.soa.common.job;

import org.apache.log4j.Logger;

/** Handles logging for batch jobs */
public class Job {
	static Logger logger = Logger.getLogger( Job.class );
	JobCommand jobCommand;

	public void start() {
		try {
			jobCommand.service();
		} catch (Exception e) {
			logger.error( "Job failed.", e );
		}

	}

	public void setJobCommand( JobCommand jobCommand ) {
		this.jobCommand = jobCommand;
	}
}
