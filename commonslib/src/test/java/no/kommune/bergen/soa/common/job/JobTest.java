package no.kommune.bergen.soa.common.job;

import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createStrictMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;

import org.apache.log4j.Logger;
import org.junit.Test;

public class JobTest {

	@Test
	public void start() throws Exception {
		Logger mockLogger = createStrictMock( Logger.class );
		JobCommand mockJobCommand = createStrictMock( JobCommand.class );
		Job job = new Job();
		job.logger = mockLogger;
		job.setJobCommand( mockJobCommand );
		mockJobCommand.service();
		Exception e = new Exception( "TESTING" );
		expectLastCall().andThrow( e );
		mockLogger.error( "Job failed.", e );
		replay( mockJobCommand );
		replay( mockLogger );
		job.start();
		verify( mockJobCommand );
		verify( mockLogger );
	}
}
