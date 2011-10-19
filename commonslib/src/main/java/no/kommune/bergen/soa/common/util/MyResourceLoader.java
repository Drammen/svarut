package no.kommune.bergen.soa.common.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;

/** Velocity resource loader to support loading of inline macros for spring configuration xml files */
public class MyResourceLoader extends ResourceLoader {

	@Override
	public void init( ExtendedProperties configuration ) {}

	@Override
	public InputStream getResourceStream( String source ) {
		try {
			return new ByteArrayInputStream( source.getBytes( "UTF-8" ) );
		} catch (UnsupportedEncodingException e) {
			throw new ResourceNotFoundException( e );
		}
	}

	@Override
	public long getLastModified( Resource arg0 ) {
		return new Date().getTime();
	}

	@Override
	public boolean isSourceModified( Resource arg0 ) {
		return true;
	}

}
