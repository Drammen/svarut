package no.kommune.bergen.soa.svarut;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServiceContextTest {
	private ServiceContext serviceContext;

	@Test
	public void init() {
		loadSpringConfig();
	}

	private void loadSpringConfig() {
		ApplicationContext context = new ClassPathXmlApplicationContext( "classpath:applicationContext.xml" );
		serviceContext = (ServiceContext)context.getBean( "serviceContext" );
		serviceContext.verify();
	}

}
