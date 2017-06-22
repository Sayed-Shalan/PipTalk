package com.pip.talk.pip.pc.piptalk.xmpp.service;


import com.pip.talk.pip.pc.piptalk.xmpp.bean.CcsInMessage;

/**
 * All messages from the user have a specific format. The Action field defines,
 * what the action is about. An example is the action com.wedevol.MESSAGE, used
 * to tell the server about a new message that needs to be sent. Any further
 * fields are specific for the given action.
 */
public interface PayloadProcessor {

	void handleMessage(CcsInMessage msg);

}