package no.kommune.bergen.soa.svarut;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.classextension.EasyMock.createStrictMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.directory.SearchControls;

import org.junit.Before;
import org.junit.Test;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;

public class LdapFacadeTest {
	LdapFacade ldapFacade;
	LdapTemplate ldapTemplateMock;
	private static String filter = "uid={0}";

	@Before
	public void init() {
		this.ldapTemplateMock = createStrictMock( LdapTemplate.class );
		this.ldapFacade = new LdapFacade( this.ldapTemplateMock, filter );
	}

	@Test
	public void lookup() {
		String uid = "MyUid";
		List<?> attributes = createResponse();
		expect( ldapTemplateMock.search( isA( String.class ), isA( String.class ), isA( SearchControls.class ), isA( AttributesMapper.class ) ) ).andReturn( attributes );
		replay( ldapTemplateMock );
		assertTrue( "looukup() returned false", ldapFacade.lookup( uid ) );
		verify( ldapTemplateMock );
	}

	private List<?> createResponse() {
		Set<String> set = new HashSet<String>();
		set.add( "value" );
		Map<String, Set<String>> map = new HashMap<String, Set<String>>();
		map.put( "key", set );
		List<Map<String, Set<String>>> attributes = new ArrayList<Map<String, Set<String>>>();
		attributes.add( map );
		return attributes;
	}
}
