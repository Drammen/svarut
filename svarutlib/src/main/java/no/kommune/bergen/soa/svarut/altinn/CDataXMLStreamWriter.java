package no.kommune.bergen.soa.svarut.altinn;

import org.apache.cxf.staxutils.DelegatingXMLStreamWriter;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;


public class CDataXMLStreamWriter extends DelegatingXMLStreamWriter {

	private String currentElementName;

	public CDataXMLStreamWriter(XMLStreamWriter del) {
		super(del);
	}

	@Override
	public void writeCharacters(String text) throws XMLStreamException {
		boolean useCData = checkIfCDATAneededForCurrentElement();
		if (useCData) {
			super.writeCData(text);
		}else {
		super.writeCharacters(text);
		}
	}

	private boolean checkIfCDATAneededForCurrentElement() {
		if("MessageBody".equals(currentElementName)) return true;
		return false;
	}

	public void writeStartElement(String prefix, String local, String uri) throws XMLStreamException {
		currentElementName = local;
		super.writeStartElement(prefix, local, uri);
	}
}
