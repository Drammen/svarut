package no.kommune.bergen.soa.common.util;

import java.util.Map;

/** Attempt to wrap the template engine and make dependent code vendor neutral */
public interface TemplateEngine {
	String merge( Map<String, String> data, String template );
}
