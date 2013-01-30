package no.kommune.bergen.soa.svarut.altinn;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.apache.cxf.staxutils.DelegatingXMLStreamWriter;


public class CDataXMLStreamWriter extends DelegatingXMLStreamWriter {

	private String currentElementName;
	private String targetElementName;

	public CDataXMLStreamWriter(XMLStreamWriter del, String targetElementName) {
		super(del);
		this.targetElementName = targetElementName;
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
		if(targetElementName.equals(currentElementName)) return true;
		return false;
	}

	@Override
	public void writeStartElement(String prefix, String local, String uri) throws XMLStreamException {
		currentElementName = local;
		super.writeStartElement(prefix, local, uri);
	}
}
