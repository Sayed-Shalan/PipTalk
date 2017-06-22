package com.pip.talk.pip.pc.piptalk.xmpp.service.impl;


import com.pip.talk.pip.pc.piptalk.xmpp.bean.CcsInMessage;
import com.pip.talk.pip.pc.piptalk.xmpp.bean.CcsOutMessage;
import com.pip.talk.pip.pc.piptalk.xmpp.server.CcsClient;
import com.pip.talk.pip.pc.piptalk.xmpp.server.MessageHelper;
import com.pip.talk.pip.pc.piptalk.xmpp.service.PayloadProcessor;
import com.pip.talk.pip.pc.piptalk.xmpp.util.Util;

/**
 * Handles an echo request
 */
public class EchoProcessor implements PayloadProcessor {

	@Override
	public void handleMessage(CcsInMessage inMessage) {

		CcsClient client = CcsClient.getInstance();
		String messageId = Util.getUniqueMessageId();
		String to = inMessage.getFrom();

		// Send the incoming message to the the device that made the request
		CcsOutMessage outMessage = new CcsOutMessage(to, messageId, inMessage.getDataPayload());
		String jsonRequest = MessageHelper.createJsonOutMessage(outMessage);
		client.send(jsonRequest);


	}

}