package no.kommune.bergen.soa.svarut;

import org.apache.log4j.Logger;

import java.util.concurrent.Callable;

class Job implements Callable<Boolean> {

	private static final Logger log = Logger.getLogger(Job.class);
    private Dispatcher dispatcher;
    private boolean send;

    public Job(Dispatcher dispatcher, boolean send) {
        log.debug("Initializing job");
        this.dispatcher = dispatcher;
        this.send = send;
    }

	@Override
	public Boolean call() throws Exception {
        log.debug("Executing job.call");
        boolean success = true;
        try {
        if ( send ) {
            dispatcher.sendAlleForsendelser();
        } else {
            dispatcher.handleAllUnread();
        }
        } catch ( Exception e) {
            log.error("Dispatching forsendelser failed: " + e.getMessage(), e);
            success=false;
        }
		return success;
	}
}
