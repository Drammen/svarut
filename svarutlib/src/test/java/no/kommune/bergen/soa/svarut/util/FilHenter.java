package no.kommune.bergen.soa.svarut.util;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class FilHenter {

	public static InputStream getFileAsInputStream(String uri){
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(uri);
	}

	public static File getFileAsFile(String uri){
		return new File(Thread.currentThread().getContextClassLoader().getResource(uri).getFile());
	}

	public static byte[] getFileContent(String s) {
		try {
			return IOUtils.toByteArray(getFileAsInputStream(s));
		} catch (IOException e) {
			return null;
		}
	}
}
