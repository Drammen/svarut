package no.kommune.bergen.soa.svarut;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import no.kommune.bergen.soa.svarut.dispatchers.AltinnOgPost;
import no.kommune.bergen.soa.svarut.dispatchers.EmailOgPost;
import no.kommune.bergen.soa.svarut.dispatchers.KunAltinn;
import no.kommune.bergen.soa.svarut.dispatchers.KunEmail;
import no.kommune.bergen.soa.svarut.dispatchers.KunNorgeDotNo;
import no.kommune.bergen.soa.svarut.dispatchers.KunPost;
import no.kommune.bergen.soa.svarut.dispatchers.NorgeDotNoOgPost;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class JobController {

	private static final Logger log = Logger.getLogger(JobController.class);
	private static int numWorkers = 1;
	private static long timeoutSecs = 60 * 60 * 1; // 1 hour

	private static final String everyHour = "0 0 * * * * ";


	@Autowired
	private DispatcherFactory dispatcherFactory;

	private static Map<Class<? extends Dispatcher>, ThreadPoolExecutor> sendServices = createServices();
	private static Map<Class<? extends Dispatcher>, ThreadPoolExecutor> handleUnreadServices = createServices();

	private List<Dispatcher> dispatchers;
	private final Map<Class<? extends Dispatcher>, Dispatcher> dispatchMap = new HashMap<Class<? extends Dispatcher>, Dispatcher>();

	private static Map<Class<? extends Dispatcher>, ThreadPoolExecutor> createServices() {

		Map<Class<? extends Dispatcher>, ThreadPoolExecutor> serviceMap = new HashMap<Class<? extends Dispatcher>, ThreadPoolExecutor>();

		serviceMap.put(AltinnOgPost.class, getNewExecutor());
		serviceMap.put(EmailOgPost.class, getNewExecutor());
		serviceMap.put(KunAltinn.class, getNewExecutor());
		serviceMap.put(KunEmail.class, getNewExecutor());
		serviceMap.put(KunNorgeDotNo.class, getNewExecutor());
		serviceMap.put(KunPost.class, getNewExecutor());
		serviceMap.put(NorgeDotNoOgPost.class, getNewExecutor());

		return serviceMap;
	}

	private static ThreadPoolExecutor getNewExecutor() {
		return new ThreadPoolExecutor(numWorkers,
				numWorkers, timeoutSecs, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>());
	}

	/*
	 * Sender alt som ikkje har blitt sendt når det ble motatt.
	 */
	@Scheduled(cron = everyHour)
	public void triggerSends() {
		List<Dispatcher> list = dispatcherFactory.getAllDispatchers();
		for (Dispatcher dispatcher : list) {
			triggerSend(dispatcher.getClass());
		}
	}

	/*
	 * Sender alt som ikkje har blitt lest elektronisk og har ventet lenge nok.
	 */
	@Scheduled(cron = everyHour)
	public void triggerHandleUnread() {
		List<Dispatcher> list = dispatcherFactory.getAllDispatchers();
		for (Dispatcher dispatcher : list) {
			triggerHandleUnread(dispatcher.getClass());
		}
	}

	public Future<Boolean> triggerSend(Forsendelse f) {
		try {
			return triggerSend(dispatcherFactory.getDispatcher( f ).getClass());
		} catch (Exception e) {
			log.info("Failed to trigger for " + f, e);
		}
		return null;
	}

	public Future<Boolean> triggerSend( Class<? extends Dispatcher> clazz ) {
		log.debug("Triggering send for dispatcher " + clazz.getName());
		if (null == dispatchers) {
			setupDispatchMap();
		}
		final ThreadPoolExecutor service = getHandleSendService(clazz);
		log.debug("Got executor " + service + " submitting new Job...");
		final Dispatcher dispatcher = getDispatcher(clazz);
		log.debug("dispatcher = " + dispatcher);
		return service.submit(new Job(dispatcher, true));
	}

	public Future<Boolean> triggerHandleUnread(Class<? extends Dispatcher> clazz) {
		if (null == dispatchers) {
			setupDispatchMap();
		}
		final ThreadPoolExecutor service = getHandleUnreadService(clazz);
		return service.submit(new Job(getDispatcher(clazz), false));
	}

	private Dispatcher getDispatcher(Class<? extends Dispatcher> clazz) {
		return dispatchMap.get(clazz);
	}

	private ThreadPoolExecutor getHandleUnreadService(Class<? extends Dispatcher> clazz) {
		return handleUnreadServices.get(clazz);
	}

	private ThreadPoolExecutor getHandleSendService(Class<? extends Dispatcher> clazz) {
		return sendServices.get(clazz);
	}

	private void setupDispatchMap() {
		log.debug("Setting up dispatchmap");
		dispatchers = dispatcherFactory.getAllDispatchers();
		for (Dispatcher d : dispatchers) {
			dispatchMap.put(d.getClass(), d);
		}
	}

	public boolean isRunning(Class<? extends Dispatcher> clazz) {
		final ThreadPoolExecutor service = getHandleUnreadService(clazz);
		return service.getActiveCount() > 0 && service.getQueue().size() == 0;
	}

	public void waitTillFinished() {
		int total = 1;
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {}
		while (total != 0) {
			total = 0;
			for (ThreadPoolExecutor executor : sendServices.values()) {
				total += executor.getQueue().size();
				total += executor.getActiveCount();
			}
			if (total > 0) try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}
	}

}
