package org.svarut.sample.utils;

import javax.activation.DataHandler;
import java.io.InputStream;
import java.net.URL;


public class ForsendelseUtil {

	private static String testfil = "/Undervisningsfritak.pdf";

	public static InputStream hentTestFil(){
		return ForsendelseUtil.class.getResourceAsStream(testfil);
	}

	public static DataHandler hentTestFilDataHandler(){
		URL resource = ForsendelseUtil.class.getResource(testfil);
		DataHandler dh = new DataHandler(resource);
	   return dh;
	}
}
