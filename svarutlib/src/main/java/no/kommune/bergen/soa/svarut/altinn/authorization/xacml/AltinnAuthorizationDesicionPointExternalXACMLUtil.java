package no.kommune.bergen.soa.svarut.altinn.authorization.xacml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.AccessControlException;

import org.jboss.security.xacml.core.JBossResponseContext;
import org.jboss.security.xacml.core.model.context.ActionType;
import org.jboss.security.xacml.core.model.context.EnvironmentType;
import org.jboss.security.xacml.core.model.context.RequestType;
import org.jboss.security.xacml.core.model.context.ResourceType;
import org.jboss.security.xacml.core.model.context.SubjectType;
import org.jboss.security.xacml.factories.RequestAttributeFactory;
import org.jboss.security.xacml.factories.RequestResponseContextFactory;
import org.jboss.security.xacml.interfaces.RequestContext;
import org.jboss.security.xacml.interfaces.XACMLConstants;

public class AltinnAuthorizationDesicionPointExternalXACMLUtil {

	private final static String ACTION_IDENTIFIER = "urn:oasis:names:tc:xacml:2.0:action:urn:altinn:action-id"; // ACTION
	private final static String ENVIRONMENT_IDENTIFIER = "urn:oasis:names:tc:xacml:2.0:action:urn:altinn:environment"; // ENVIRONMENT
	//private final static String CURRENT_TIME_IDENTIFIER = "urn:oasis:names:tc:xacml:1.0:environment:current-time";
	private final static String RESOURCE_IDENTIFIER_ORGNR = "urn:oasis:names:tc:xacml:2.0:resource:urn:altinn:reportee-orgno"; //RESOURCE
	private final static String RESOURCE_IDENTIFIER_SERVICECODE = "urn:oasis:names:tc:xacml:2.0:resource:urn:altinn:externalservicecode"; //RESOURCE
	private final static String RESOURCE_IDENTIFIER_SERVICEEDITIONCODE = "urn:oasis:names:tc:xacml:2.0:resource:urn:altinn:externalserviceeditioncode"; //SUBJECT
	private final static String SUBJECT_IDENTIFIER_SSN = "urn:oasis:names:tc:xacml:2.0:subject:urn:altinn:ssn";
	//private final static String SUBJECT_ROLE_IDENTIFIER = "urn:oasis:names:tc:xacml:2.0:subject:role";

	public static String createXACMLRequest(String fodselsNr, String orgNr, String serviceCode, String serviceEditionCode, String environment) {
		String xacmlString = null;
		RequestContext requestContext = RequestResponseContextFactory.createRequestCtx();

		// Subject
		SubjectType subject = new SubjectType();
		subject.getAttribute().add(RequestAttributeFactory.createStringAttributeType(SUBJECT_IDENTIFIER_SSN, "jboss_org", fodselsNr));

		// Resources
		ResourceType resourceTypeOrgNr = new ResourceType();
		resourceTypeOrgNr.getAttribute().add(RequestAttributeFactory.createStringAttributeType(RESOURCE_IDENTIFIER_ORGNR, "jboss_org", orgNr));

		ResourceType resourceTypeServiceCode = new ResourceType();
		resourceTypeServiceCode.getAttribute().add(RequestAttributeFactory.createStringAttributeType(RESOURCE_IDENTIFIER_SERVICECODE, "jboss_org", serviceCode));

		ResourceType resourceTypeServiceEditionCode = new ResourceType();
		resourceTypeServiceEditionCode.getAttribute().add(RequestAttributeFactory.createStringAttributeType(RESOURCE_IDENTIFIER_SERVICEEDITIONCODE, "jboss_org", serviceEditionCode));

		// Action
		ActionType actionType = new ActionType();
		actionType.getAttribute().add(RequestAttributeFactory.createStringAttributeType(ACTION_IDENTIFIER, "jboss.org", "read"));

		// Environment
		EnvironmentType environmentType = new EnvironmentType();
		environmentType.getAttribute().add(RequestAttributeFactory.createStringAttributeType(ENVIRONMENT_IDENTIFIER, "jboss.org", environment));


		// Create Request Type
		RequestType requestType = new RequestType();
		requestType.getSubject().add(subject);
		requestType.getResource().add(resourceTypeOrgNr);
		requestType.getResource().add(resourceTypeServiceCode);
		requestType.getResource().add(resourceTypeServiceEditionCode);
		requestType.setAction(actionType);
		requestType.setEnvironment(environmentType);

		try {
			requestContext.setRequest(requestType);
		} catch (IOException e) {
			e.printStackTrace();
			throw new AccessControlException("Could not authorize. Failed to create RequestContext.");
		}

		return xacmlString;
	}

	public static boolean parseXACMLResponseAndVerifyPermitted(String xacmlResponse) {
		// Parse response string and fetch decision
		JBossResponseContext responseContext = new JBossResponseContext();
		InputStream is = new ByteArrayInputStream(xacmlResponse.getBytes());
		try {
			responseContext.readResponse(is);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if(responseContext.getDecision() == XACMLConstants.DECISION_PERMIT)
			return true;

		//		if(!authString.isEmpty() && xacmlResponse.contains("<Decision>Permit</Decision>") )
		//			return true;

		return false;
	}
}