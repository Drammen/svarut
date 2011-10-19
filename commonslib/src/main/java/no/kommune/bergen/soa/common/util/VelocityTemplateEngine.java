package no.kommune.bergen.soa.common.util;

import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.ui.velocity.VelocityEngineUtils;

public class VelocityTemplateEngine implements TemplateEngine {
	static final Logger logger = Logger.getLogger( VelocityTemplateEngine.class );
	private VelocityEngine velocityEngine = null;
	private String encoding = "UTF-8";

	public String merge( Map<String, String> data, String templateLocation ) {
		String result = VelocityEngineUtils.mergeTemplateIntoString( this.velocityEngine, templateLocation, encoding, data );
		if (result.indexOf( "${" ) > -1) {
			logger.warn( "Unsubstituted velocity macro in string: " + result );
		}
		return result;
	}

	public VelocityEngine getVelocityEngine() {
		return velocityEngine;
	}

	public void setVelocityEngine( VelocityEngine velocityEngine ) {
		this.velocityEngine = velocityEngine;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setEncoding( String encoding ) {
		this.encoding = encoding;
	}

}
