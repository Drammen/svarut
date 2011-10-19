package no.kommune.bergen.soa.common.util;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class PersistentCounterTest {

	@Test
	public void increment() throws IOException {
		final String fileName = "target/PersistentCounterTest.txt";
		new File( fileName ).delete();
		PersistentCounter persistentCounter = new PersistentCounter( fileName );
		for (long i = 1; i < 100; i++) {
			persistentCounter.increment();
			assertEquals( "" + i, "" + persistentCounter.read() );
		}
	}

}
