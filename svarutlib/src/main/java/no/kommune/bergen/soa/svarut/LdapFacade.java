package no.kommune.bergen.soa.svarut;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;

/** Intended for communicating with Norge.no, who require a successfull ldap lookup prior to submitting a message */
public class LdapFacade {
	private static final Log log = LogFactory.getLog( LdapFacade.class );
	LdapTemplate ldapTemplate = null;
	String filterTemplate = "(&(sn={0})(objectclass=person))";

	public LdapFacade( LdapTemplate ldapTemplate, String filterTemplate ) {
		this.ldapTemplate = ldapTemplate;
		this.filterTemplate = filterTemplate;
	}

	public boolean lookup( String uid ) {
		if (log.isDebugEnabled()) log.debug( "lookup(" + uid + ") {" );
		return (null != find( uid ));
	}

	public List<?> search( String base, String filter ) {
		if (log.isDebugEnabled()) log.debug( "search( base=" + base + ", filter=" + filter + ") {" );
		return ldapTemplate.search( base, filter, new MySearchControls(), new MyAttributesMapper() );
	}

	public Map<?, ?> find( String id ) {
		if (log.isDebugEnabled()) log.debug( "find(" + id + ") {" );
		if (id == null) return null;
		try {
			MessageFormat messageFormat = new MessageFormat( this.filterTemplate );
			String filter = messageFormat.format( new Object[] { id } );
			List<?> list = search( "", filter );
			if (log.isDebugEnabled()) log.debug( "find() }" + list );
			if (list == null || list.size() < 1) {
				return null;
			} else {
				return (Map<?, ?>) list.get( 0 );
			}
		} catch (Exception e) {
			String msg = "Error in LDAP communication";
			log.error( msg, e );
			throw new RuntimeException( msg, e );
		}
	}

	static class MyAttributesMapper implements AttributesMapper {
		@SuppressWarnings("unchecked")
		public Object mapFromAttributes( Attributes attrs ) throws NamingException {
			Map<String, Set> map = new HashMap<String, Set>();
			if (attrs != null) {
				for (NamingEnumeration<?> e = attrs.getAll(); e.hasMore();) {
					Attribute a = (Attribute) e.next();
					Set<Object> set = new HashSet<Object>();
					if (a != null) {
						for (int i = 0; i < a.size(); i++) {
							set.add( a.get( i ) );
						}
						map.put( a.getID(), set );
					}
				}
			}
			return map;
		}
	} // class MyAttributesMapper

	static class MySearchControls extends SearchControls {
		private static final long serialVersionUID = 1L;

		public MySearchControls() {
			super();
			this.setCountLimit( 1000 );
			this.setSearchScope( SearchControls.ONELEVEL_SCOPE );
			this.setReturningObjFlag( true );
			this.setTimeLimit( 10000 );
		}
	} // class MySearchControls

}
