package no.kommune.bergen.soa.common.util;

import java.util.Map;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.velocity.VelocityEngineUtils;

public class VelocityTemplateEngine implements TemplateEngine {

	private static final Logger logger = LoggerFactory.getLogger(VelocityTemplateEngine.class);

	private static final String VELOCITY_ENGINE_ENCODING = "UTF-8";

	private VelocityEngine velocityEngine = null;

	@Override
	public String merge( Map<String, String> data, String templateLocation ) {
		String result = VelocityEngineUtils.mergeTemplateIntoString(this.velocityEngine, templateLocation, VELOCITY_ENGINE_ENCODING, data);
		if (result.contains("${"))
			logger.warn("Unsubstituted velocity macro in string: {}", result);

		return result;
	}

	public void setVelocityEngine( VelocityEngine velocityEngine ) {
		this.velocityEngine = velocityEngine;
	}
}
