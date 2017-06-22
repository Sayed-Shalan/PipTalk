package com.pip.talk.pip.pc.piptalk.xmpp.service.impl;


import com.pip.talk.pip.pc.piptalk.xmpp.bean.CcsInMessage;
import com.pip.talk.pip.pc.piptalk.xmpp.bean.CcsOutMessage;
import com.pip.talk.pip.pc.piptalk.xmpp.server.CcsClient;
import com.pip.talk.pip.pc.piptalk.xmpp.server.MessageHelper;
import com.pip.talk.pip.pc.piptalk.xmpp.service.PayloadProcessor;
import com.pip.talk.pip.pc.piptalk.xmpp.util.Util;

/**
 * Handles an upstream message request
 */
public class MessageProcessor implements PayloadProcessor {

	@Override
	public void handleMessage(CcsInMessage inMessage) {
		CcsClient client = CcsClient.getInstance();
		String messageId = Util.getUniqueMessageId();
		String to = inMessage.getDataPayload().get(Util.PAYLOAD_ATTRIBUTE_RECIPIENT);

		// TODO: handle the data payload sent to the client device. Here, I just
		// resend the incoming message.
		CcsOutMessage outMessage = new CcsOutMessage(to, messageId, inMessage.getDataPayload());
		String jsonRequest = MessageHelper.createJsonOutMessage(outMessage);
		client.send(jsonRequest);
	}

}