package com.pip.talk.pip.pc.piptalk.xmpp.service.impl;


import com.pip.talk.pip.pc.piptalk.xmpp.bean.CcsInMessage;
import com.pip.talk.pip.pc.piptalk.xmpp.service.PayloadProcessor;

/**
 * Handles a user registration request
 */
public class RegisterProcessor implements PayloadProcessor {

	@Override
	public void handleMessage(CcsInMessage msg) {
		// TODO: handle the user registration. Keep in mind that a user name can
		// have more reg IDs associated. The messages IDs should be uniques. 
	}

}