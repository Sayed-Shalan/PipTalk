package com.pip.talk.pip.pc.piptalk.xmpp.server;

import android.util.Log;

import com.pip.talk.pip.pc.piptalk.xmpp.util.Util;

import org.jivesoftware.smack.packet.ExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Stanza;


/**
 * XMPP Packet Extension for GCM Cloud Connection Server
 */
public class GcmPacketExtension implements ExtensionElement {

	private String json;

	public GcmPacketExtension(String json) {
		Log.e("SEND"," insdide GcmPacketExtension GcmPacketExtension() method , json before converting to packet is : "+json );
		this.json = json;
	}

	public String getJson() {
		return json;
	}

	@Override
	public String toXML() {
		// TODO: Do we need to scape the json? StringUtils.escapeForXML(json)
		return String.format("<%s xmlns=\"%s\">%s</%s>", getElementName(), getNamespace(), json, Util.FCM_ELEMENT_NAME);
	}

	public Stanza toPacket() {
		Message message = new Message();
		message.addExtension(this);
		return message;
	}

	@Override
	public String getElementName() {
		return Util.FCM_ELEMENT_NAME;
	}

	@Override
	public String getNamespace() {
		return Util.FCM_NAMESPACE;
	}
}
