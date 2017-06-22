package com.pip.talk.pip.pc.piptalk.xmpp.util;

import java.util.UUID;



public class Util {

	// For the GCM connection
	public static final String FCM_SERVER = "fcm-xmpp.googleapis.com";
	public static final int FCM_PORT = 5236;
	public static final String FCM_ELEMENT_NAME = "gcm";
	public static final String FCM_NAMESPACE = "google:mobile:data";
	public static final String FCM_SERVER_CONNECTION = "gcm.googleapis.com";

	public static final String TEMPO_PHONE_RECIEVER = "clElKGb2Z_U:APA91bF2KLy4uQlXR4mcAj-GizSMJBylk58ARoaadA31d9ltVHR8wu5pecWqEWssUZBsc6O3IOwl4IBJF0wo4ki3RWNHAAq4a9wLBra8TilEHYup7zcdZ1zHEV8JrZEGeYPaQSr-lmmE";
	public static final String Emulator_token = "cZlq6VAXygo:APA91bHBVHeXl6YRjNC2e6wESa3oOn730yR3RDwXzeEH52hgj0dl28XKN-qtS-_aJVJPppmMJvOZ2Xe0tX5QjXm0bdq8SvyFmyvCf221LtCECpG-keRLBJqjeNQW_ssfSSAkKvSdyXSg";
	public static final String Nour_Basha_Emulator = "e6CRO4FpPU8:APA91bHkIt-ILJwezY0fkWcPGW9BEVoMu0hKdVdVghOKkIXOw3Rkc5LKgGBGBFlx_ZliMuBNmh651PbZqCFvr3iwdv-OAFu7ZHDNvLfK9eiAul0Geg85FOpj9DY1LD5tsmpZmG0srVS3";
	//

	public static final String FCM_SERVER_API_KEY = "AAAA5-N61o0:APA91bFhpEGRufek_sZQ55ycfO73zLVaOBzxH-V-dlsRrietiOFAgC2XfKfPP-YPXM74dZ1IzCLJRIHp4-Of-sQLkN80YYrGBdApxG_n2ZwUwHnPmilQUXjRiAQO1Kc_8VMjU3C_a4ehrZD55gqlgnepWNO3pIANqA";

	//
	public static final String WAMP_SERVER_DOMAIN = "192.168.193.1";


	// For the processor factory
	public static final String PACKAGE = "com.wedevol";
	public static final String BACKEND_ACTION_REGISTER = PACKAGE + ".REGISTER";
	public static final String BACKEND_ACTION_ECHO = PACKAGE + ".ECHO";
	public static final String BACKEND_ACTION_MESSAGE = PACKAGE + ".MESSAGE";

	// For the app common payload message attributes (android - xmpp server)
	public static final String PAYLOAD_ATTRIBUTE_MESSAGE = "message";
	public static final String PAYLOAD_ATTRIBUTE_LANGUAGE_CODE = "lang_code";
	public static final String PAYLOAD_ATTRIBUTE_ACTION = "action";
	public static final String PAYLOAD_ATTRIBUTE_RECIPIENT = "recipient";
	public static final String PAYLOAD_ATTRIBUTE_SENDER = "sender";
	public static final String PAYLOAD_ATTRIBUTE_MESSAGE_ID = "message_id";
	public static final String PAYLOAD_ATTRIBUTE_RECEIVER_ID = "receiver_id";
	public static final String PAYLOAD_ATTRIBUTE_RECEIVER_HAS_IMAGE = "receiver_has_image";
	public static final String PAYLOAD_ATTRIBUTE_SENDER_ID = "sender_id";
	public static final String PAYLOAD_ATTRIBUTE_SENDER_HAS_IMAGE = "sender_has_image";
	public static final String PAYLOAD_ATTRIBUTE_TIME = "time";
	public static final String PAYLOAD_ATTRIBUTE_DATE= "date";
	public static final String PAYLOAD_ATTRIBUTE_SENDER_LANGUAGE= "sender_language";



	public static final String PAYLOAD_ATTRIBUTE_ACCOUNT = "account";

	/**
	 * Returns a random message id to uniquely identify a message
	 */
	public static String getUniqueMessageId() {
		// TODO: replace for your own random message ID that the DB generates
		return "m-" + UUID.randomUUID().toString();
	}

}
