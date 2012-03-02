package no.kommune.bergen.soa.svarut.altin;

import org.apache.cxf.interceptor.AttachmentOutInterceptor;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.staxutils.StaxUtils;

import javax.xml.stream.XMLStreamWriter;
import java.io.OutputStream;

public class CdataWriterInterceptor extends AbstractPhaseInterceptor<Message> {

	public CdataWriterInterceptor() {
		super(Phase.PRE_STREAM);
		addAfter(AttachmentOutInterceptor.class.getName());
	}

	@Override
	public void handleMessage(Message message) {
		message.put("disable.outputstream.optimization", Boolean.TRUE);
		XMLStreamWriter writer = StaxUtils.createXMLStreamWriter(message.getContent(OutputStream.class));
		message.setContent(XMLStreamWriter.class, new CDataXMLStreamWriter(writer));
	}
}
