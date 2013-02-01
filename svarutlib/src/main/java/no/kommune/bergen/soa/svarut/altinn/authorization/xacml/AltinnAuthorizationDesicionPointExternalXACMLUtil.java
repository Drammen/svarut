package no.kommune.bergen.soa.svarut.altinn.authorization.xacml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.AccessControlException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.jboss.security.xacml.core.model.context.ActionType;
import org.jboss.security.xacml.core.model.context.EnvironmentType;
import org.jboss.security.xacml.core.model.context.RequestType;
import org.jboss.security.xacml.core.model.context.ResourceType;
import org.jboss.security.xacml.core.model.context.SubjectType;
import org.jboss.security.xacml.factories.RequestAttributeFactory;
import org.jboss.security.xacml.factories.RequestResponseContextFactory;
import org.jboss.security.xacml.interfaces.RequestContext;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class AltinnAuthorizationDesicionPointExternalXACMLUtil {

	private static final String ACTION_IDENTIFIER = "urn:oasis:names:tc:xacml:2.0:action:urn:altinn:action-id"; // ACTION
	private static final String ENVIRONMENT_IDENTIFIER = "urn:oasis:names:tc:xacml:2.0:action:urn:altinn:environment"; // ENVIRONMENT
	//private static final String CURRENT_TIME_IDENTIFIER = "urn:oasis:names:tc:xacml:1.0:environment:current-time";
	private static final String RESOURCE_IDENTIFIER_ORGNR = "urn:oasis:names:tc:xacml:2.0:resource:urn:altinn:reportee-orgno"; //RESOURCE
	private static final String RESOURCE_IDENTIFIER_SERVICECODE = "urn:oasis:names:tc:xacml:2.0:resource:urn:altinn:externalservicecode"; //RESOURCE
	private static final String RESOURCE_IDENTIFIER_SERVICEEDITIONCODE = "urn:oasis:names:tc:xacml:2.0:resource:urn:altinn:externalserviceeditioncode"; //SUBJECT
	private static final String SUBJECT_IDENTIFIER_SSN = "urn:oasis:names:tc:xacml:2.0:subject:urn:altinn:ssn";
	//private final String SUBJECT_ROLE_IDENTIFIER = "urn:oasis:names:tc:xacml:2.0:subject:role";

	public static String createXACMLRequest(String fodselsNr, String orgNr, String serviceCode, String serviceEditionCode, String environment) {
		String xacmlString = null;
		RequestContext requestContext = RequestResponseContextFactory.createRequestCtx();

		// Subject
		SubjectType subject = new SubjectType();
		subject.getAttribute().add(RequestAttributeFactory.createStringAttributeType(SUBJECT_IDENTIFIER_SSN, "jboss_org", fodselsNr));

		// Resources
		ResourceType resourceType = new ResourceType();
		resourceType.getAttribute().add(RequestAttributeFactory.createStringAttributeType(RESOURCE_IDENTIFIER_ORGNR, "jboss_org", orgNr));
		resourceType.getAttribute().add(RequestAttributeFactory.createStringAttributeType(RESOURCE_IDENTIFIER_SERVICECODE, "jboss_org", serviceCode));
		resourceType.getAttribute().add(RequestAttributeFactory.createStringAttributeType(RESOURCE_IDENTIFIER_SERVICEEDITIONCODE, "jboss_org", serviceEditionCode));

		// Action
		ActionType actionType = new ActionType();
		actionType.getAttribute().add(RequestAttributeFactory.createStringAttributeType(ACTION_IDENTIFIER, "jboss.org", "Read"));

		// Environment
		EnvironmentType environmentType = new EnvironmentType();
		environmentType.getAttribute().add(RequestAttributeFactory.createStringAttributeType(ENVIRONMENT_IDENTIFIER, "jboss.org", environment));

		// Create Request Type
		RequestType requestType = new RequestType();
		requestType.getSubject().add(subject);
		requestType.getResource().add(resourceType);
		requestType.setAction(actionType);
		requestType.setEnvironment(environmentType);

		try {
			requestContext.setRequest(requestType);
		} catch (IOException e) {
			e.printStackTrace();
			throw new AccessControlException("Could not authorize. Failed to create RequestContext.");
		}

		try {
			Node doc = requestContext.getDocumentElement();
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer transformer = tf.newTransformer();

			transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			transformer.setOutputProperty(OutputKeys.INDENT, "no");
			StringWriter writer = new StringWriter();
			transformer.transform(new DOMSource(doc), new StreamResult(writer));
			xacmlString = writer.getBuffer().toString().replaceAll("\n|\r", "").trim().replaceAll(">\\s*<", "><");;
		} catch (TransformerException te) {
			te.printStackTrace();
			throw new AccessControlException("Could not authorize. Failed to create RequestContext.");
		}

		return xacmlString;
	}

	public static boolean parseXACMLResponseAndVerifyPermitted(String xacmlResponse) {
		InputStream is = new ByteArrayInputStream(xacmlResponse.getBytes());
		return getDecision(is);
	}

	private static boolean getDecision(InputStream is) {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringComments(true);
		Document doc = null;
		try {
			doc = factory.newDocumentBuilder().parse(is);
		} catch (SAXException e) {
			System.out.println(e.getStackTrace());
		} catch (IOException e) {
			System.out.println(e.getStackTrace());
		} catch (ParserConfigurationException e) {
			System.out.println(e.getStackTrace());
		}

		List<Node> decisionsFound = new ArrayList<Node>();
		decisionsFound.addAll(findDecisionChildNodes(doc.getChildNodes()));

		if(decisionsFound.size() > 1 || decisionsFound.isEmpty())
			return false;

		String decision = decisionsFound.get(0).getTextContent();

		if(decision.equals("Permit"))
			return true;

		return false;
	}

	private static List<Node> findDecisionChildNodes(NodeList nodeList) {
		List<Node> decisions = new ArrayList<Node>();
		for(int i = 0; i < nodeList.getLength(); i++) {
			if(nodeList.item(i).getNodeName().toLowerCase().equals("xacml:decision"))
				decisions.add(nodeList.item(i));
			if(nodeList.item(i).getChildNodes().getLength() > 0)
				decisions.addAll(findDecisionChildNodes(nodeList.item(i).getChildNodes()));
		}
		return decisions;
	}

}
