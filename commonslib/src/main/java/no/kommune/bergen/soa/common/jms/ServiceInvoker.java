package no.kommune.bergen.soa.common.jms;

import javax.jms.Message;

public interface ServiceInvoker {
	void invoke( Message message ) throws Exception;
}
