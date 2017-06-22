package com.pip.talk.pip.pc.piptalk.xmpp.server;

import android.util.Log;

import com.pip.talk.pip.pc.piptalk.xmpp.bean.CcsInMessage;
import com.pip.talk.pip.pc.piptalk.xmpp.bean.CcsOutMessage;
import com.pip.talk.pip.pc.piptalk.xmpp.service.PayloadProcessor;
import com.pip.talk.pip.pc.piptalk.xmpp.util.Util;

import org.jivesoftware.smack.ConnectionConfiguration.SecurityMode;
import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.ReconnectionManager;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.SmackException.NotConnectedException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.provider.ExtensionElementProvider;
import org.jivesoftware.smack.provider.ProviderManager;
import org.jivesoftware.smack.sm.predicates.ForEveryStanza;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.impl.JidCreate;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.SSLSocketFactory;


/**
 * Sample Smack implementation of a client for FCM Cloud Connection Server. Most
 * of it has been taken more or less verbatim from Google's documentation:
 * https://firebase.google.com/docs/cloud-messaging/xmpp-server-ref
 */
public class CcsClient implements StanzaListener {

	public static final Logger logger = Logger.getLogger(CcsClient.class.getName());

	private static CcsClient sInstance = null;
	private XMPPTCPConnection connection;
	private String mApiKey = null;
	private String mProjectId = null;
	private boolean mDebuggable = false;
	private String fcmServerUsername = null;


	public static CcsClient getInstance() {
		if (sInstance == null) {
			throw new IllegalStateException("You have to prepare the client first");
		}
		return sInstance;
	}

	public static CcsClient prepareClient(String projectId, String apiKey, boolean debuggable) {
		synchronized (CcsClient.class) {
			if (sInstance == null) {
				Log.e("SEND"," inside prepare client ");
				sInstance = new CcsClient(projectId, apiKey, debuggable);
			}
		}
		return sInstance;
	}

	private CcsClient(String projectId, String apiKey, boolean debuggable) {
		this();
		Log.e("SEND"," inside Ccs constructor ");

		mApiKey = apiKey;
		mProjectId = projectId;
		mDebuggable = debuggable;

		Log.e("SEND"," inside Ccs constructor values are apikey is :"+mApiKey+" ** sender id is :"+mProjectId+" **");

		fcmServerUsername = mProjectId + "@" + Util.FCM_SERVER_CONNECTION;
	}

	private CcsClient() {
		// Add GcmPacketExtension
		ProviderManager.addExtensionProvider(Util.FCM_ELEMENT_NAME, Util.FCM_NAMESPACE,
				new ExtensionElementProvider<GcmPacketExtension>() {
					@Override
					public GcmPacketExtension parse(XmlPullParser parser, int initialDepth)
							throws XmlPullParserException, IOException, SmackException {
						String json = parser.nextText();
						return new GcmPacketExtension(json);
					}
				});
	}

	/**
	 * Connects to FCM Cloud Connection Server using the supplied credentials
	 */
	public void connect() throws XMPPException, SmackException, IOException {


		Log.e("SEND"," inside connect()  ");

		XMPPTCPConnection.setUseStreamManagementResumptionDefault(true);
		XMPPTCPConnection.setUseStreamManagementDefault(true);


		DomainBareJid jid = JidCreate.domainBareFrom(Util.FCM_SERVER);

		Log.e("SEND"," inside connect() , jid is :"+jid);


		XMPPTCPConnectionConfiguration.Builder config = XMPPTCPConnectionConfiguration.builder();
//		config.setServiceName((DomainBareJid) jid);////// this
        config.setXmppDomain(jid);
		config.setHost(Util.FCM_SERVER);
		config.setPort(Util.FCM_PORT);
		config.setSecurityMode(SecurityMode.ifpossible);
		config.setSendPresence(false);
		config.setSocketFactory(SSLSocketFactory.getDefault());
		// Launch a window with info about packets sent and received
		config.setDebuggerEnabled(mDebuggable);

		// Create the connection
		connection = new XMPPTCPConnection(config.build());


		// Connect
		try {
			connection.connect();
			Log.e("SEND"," inside connect() (try) , connection established");
		} catch (InterruptedException e) {
			Log.e("SEND"," inside connect() (catsh) , connection failed");
			e.printStackTrace();
		}

		// Enable automatic reconnection
		ReconnectionManager.getInstanceFor(connection).enableAutomaticReconnection();

		// Handle reconnection and connection errors
		connection.addConnectionListener(new ConnectionListener() {

			@Override
			public void reconnectionSuccessful() {
				logger.log(Level.INFO, "Reconnection successful ...");
				// TODO: handle the reconnecting successful
			}

			@Override
			public void reconnectionFailed(Exception e) {
				logger.log(Level.INFO, "Reconnection failed: ", e.getMessage());
				Log.e("SEND"," inside reconnectionFailed() , "+ e);

				// TODO: handle the reconnection failed
			}

			@Override
			public void reconnectingIn(int seconds) {
				logger.log(Level.INFO, "Reconnecting in %d secs", seconds);
				// TODO: handle the reconnecting in
			}

			@Override
			public void connectionClosedOnError(Exception e) {
				logger.log(Level.INFO, "Connection closed on error");
				// TODO: handle the connection closed on error
			}

			@Override
			public void connectionClosed() {
				logger.log(Level.INFO, "Connection closed");
				// TODO: handle the connection closed
			}

			@Override
			public void authenticated(XMPPConnection arg0, boolean arg1) {
				logger.log(Level.INFO, "User authenticated");
				// TODO: handle the authentication
			}

			@Override
			public void connected(XMPPConnection arg0) {
				logger.log(Level.INFO, "Connection established");
				Log.e("SEND"," inside connected() ");

				// TODO: handle the connection
			}
		});

		// Handle incoming packets (the class implements the StanzaListener)
		connection.addAsyncStanzaListener(this, new StanzaFilter() {
			@Override
			public boolean accept(Stanza stanza) {

				Log.e("SEND","inside  addAsyncStanzaListener(), stanza is :"+stanza.toXML());

				return stanza.hasExtension(Util.FCM_ELEMENT_NAME, Util.FCM_NAMESPACE);
			}
		});

		// Log all outgoing packets
		connection.addPacketInterceptor(new StanzaListener() {
			@Override
			public void processPacket(Stanza stanza) throws NotConnectedException {
				Log.e("SEND","inside  addPacketInterceptor() ");
				logger.log(Level.INFO, "Sent: {}", stanza.toXML());
			}
		}, ForEveryStanza.INSTANCE);

		try {
			Log.e("SEND","inside  addPacketInterceptor(), (try) ");
			connection.login(fcmServerUsername, mApiKey);
		} catch (InterruptedException e) {
			Log.e("SEND","inside  addPacketInterceptor(), (catch) ");
			e.printStackTrace();
		}
		logger.log(Level.INFO, "Logged in: " + fcmServerUsername);


	}


	public void reconnect() {

		while (true) {
			try {
				connect();
				return;
			} catch (XMPPException | SmackException | IOException e) {
				logger.log(Level.INFO, "Connecting again to FCM (manual reconnection)");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}


	}



	/**
	 * Handles incoming messages
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void processPacket(Stanza packet) {
		logger.log(Level.INFO, "Received: " + packet.toXML());
		GcmPacketExtension gcmPacket = (GcmPacketExtension) packet.getExtension(Util.FCM_NAMESPACE);
		String json = gcmPacket.getJson();

		Log.e("SEND","inside  processPacket(), coming stanza is : "+packet.toXML());
		Log.e("SEND","inside  processPacket(), coming json is : "+json);


		try {
			Log.e("SEND","inside  processPacket(), (try) ");

			Map<String, Object> jsonMap = (Map<String, Object>) JSONValue.parseWithException(json);
			Object messageType = jsonMap.get("message_type");
			Log.e("SEND","inside  processPacket(), message type is  : "+messageType);

			if (messageType == null) {

				Log.e("SEND","inside  processPacket(), message type is  NULL ");

				// this
				CcsInMessage inMessage = MessageHelper.createCcsInMessage(jsonMap);
				handleUpstreamMessage(inMessage); // normal upstream message

				return;
			}

			switch (messageType.toString()) {
			case "ack":
				Log.e("SEND","inside  processPacket(), message type is  ACK ");
				handleAckReceipt(jsonMap);
				break;
			case "nack":
				Log.e("SEND","inside  processPacket(), message type is  NACK ");
				handleNackReceipt(jsonMap);
				break;
			case "receipt":
				Log.e("SEND","inside  processPacket(), message type is  RECEIPT ");
				handleDeliveryReceipt(jsonMap);
				break;
			case "control":
				Log.e("SEND","inside  processPacket(), message type is  CONTROL ");
				handleControlMessage(jsonMap);
				break;
			default:
				logger.log(Level.INFO, "Received unknown FCM message type: " + messageType.toString());
			}
		} catch (ParseException e) {
			logger.log(Level.INFO, "Error parsing JSON: " + json, e.getMessage());
		}

	}



	/**
	 * Handles an upstream message from a device client through FCM
	 */
	private void handleUpstreamMessage(CcsInMessage inMessage) {
		final String action = inMessage.getDataPayload().get(Util.PAYLOAD_ATTRIBUTE_ACTION);

		Log.e("SEND","inside handleUpstreamMessage(), message is  : "+inMessage.getDataPayload().toString());

		if (action != null) {
			// this
			PayloadProcessor processor = ProcessorFactory.getProcessor(action);
			processor.handleMessage(inMessage);
		}

		// Send ACK to FCM
		String ack = MessageHelper.createJsonAck(inMessage.getFrom(), inMessage.getMessageId());
		send(ack);
	}

	/**
	 * Handles an ACK message from FCM
	 */
	private void handleAckReceipt(Map<String, Object> jsonMap) {
		// TODO: handle the ACK in the proper way


		Log.e("SEND","inside handleAckReceipt(), JsonMap is : "+jsonMap);

		Log.e("SEND","inside handleAckReceipt(), data is : "+jsonMap.get("data"));
		Log.e("SEND","inside handleAckReceipt(), message is : "+jsonMap.get("message"));



	}

	/**
	 * Handles a NACK message from FCM
	 */
	private void handleNackReceipt(Map<String, Object> jsonMap) {
		String errorCode = (String) jsonMap.get("error");

		Log.e("SEND"," inside handleNackReceipt(), JsonMap is : "+jsonMap);

		if (errorCode == null) {
			logger.log(Level.INFO, "Received null FCM Error Code");
			return;
		}

		switch (errorCode) {
		case "INVALID_JSON":
			handleUnrecoverableFailure(jsonMap);
			break;
		case "BAD_REGISTRATION":
			handleUnrecoverableFailure(jsonMap);
			break;
		case "DEVICE_UNREGISTERED":
			handleUnrecoverableFailure(jsonMap);
			break;
		case "BAD_ACK":
			handleUnrecoverableFailure(jsonMap);
			break;
		case "SERVICE_UNAVAILABLE":
			handleServerFailure(jsonMap);
			break;
		case "INTERNAL_SERVER_ERROR":
			handleServerFailure(jsonMap);
			break;
		case "DEVICE_MESSAGE_RATE_EXCEEDED":
			handleUnrecoverableFailure(jsonMap);
			break;
		case "TOPICS_MESSAGE_RATE_EXCEEDED":
			handleUnrecoverableFailure(jsonMap);
			break;
		case "CONNECTION_DRAINING":
			handleConnectionDrainingFailure();
			break;
		default:
			logger.log(Level.INFO, "Received unknown FCM Error Code: " + errorCode);
		}
	}

	/**
	 * Handles a Delivery Receipt message from FCM (when a device confirms that
	 * it received a particular message)
	 */
	private void handleDeliveryReceipt(Map<String, Object> jsonMap) {
		// TODO: handle the delivery receipt

		Log.e("SEND","inside handleDeliveryReceipt() , "+jsonMap.get("data"));
		Log.e("SEND","inside handleDeliveryReceipt() , "+jsonMap.get("message"));

	}

	/**
	 * Handles a Control message from FCM
	 */
	private void handleControlMessage(Map<String, Object> jsonMap) {
		// TODO: handle the control message
		String controlType = (String) jsonMap.get("control_type");


		Log.e("SEND","inside CcsCliet ,  inside handleControlMessage() method");

		if (controlType.equals("CONNECTION_DRAINING")) {
			handleConnectionDrainingFailure();
		} else {
			logger.log(Level.INFO, "Received unknown FCM Control message: " + controlType);
		}
	}

	private void handleServerFailure(Map<String, Object> jsonMap) {
		// TODO: Resend the message
		logger.log(Level.INFO, "Server error: " + jsonMap.get("error") + " -> " + jsonMap.get("error_description"));

	}

	private void handleUnrecoverableFailure(Map<String, Object> jsonMap) {
		// TODO: handle the unrecoverable failure
		logger.log(Level.INFO,
				"Unrecoverable error: " + jsonMap.get("error") + " -> " + jsonMap.get("error_description"));
	}

	private void handleConnectionDrainingFailure() {
		// TODO: handle the connection draining failure. Force reconnect?
		logger.log(Level.INFO, "FCM Connection is draining! Initiating reconnection ...");
	}

	/**
	 * Sends a downstream message to FCM
	 */
	public void send(String jsonRequest) {
		Log.e("SEND"," inside Ccs send() method");
		// TODO: Resend the message using exponential back-off!
		Log.e("SEND"," inside Ccs send() method, before ...onRequest).toPacket() ");
		Stanza request = new GcmPacketExtension(jsonRequest).toPacket();
		Log.e("SEND"," inside Ccs send() method, after ....toPacket() stanza request is : "+request.toXML());
		try {
			Log.e("SEND"," inside Ccs send() method, before sendStanza, (try)");
			connection.sendStanza(request);
			Log.e("SEND"," inside Ccs send() method, after sendStanza. (try)");
			Log.e("SEND","this is sent :"+request);
		} catch (NotConnectedException e) {
			Log.e("SEND"," inside Ccs send() method, after sendStanza, (catch1), exception is :"+e);
			logger.log(Level.INFO, "There is no connection and the packet could not be sent: {}", request.toXML());
		} catch (InterruptedException e) {
			Log.e("SEND"," inside Ccs send() method, after sendStanza, (catch2), exception is :"+e);
			e.printStackTrace();
		}
	}

	/**
	 * Sends a message to multiple recipients (list). Kind of like the old HTTP
	 * message with the list of regIds in the "registration_ids" field.
	 */
	public void sendBroadcast(CcsOutMessage outMessage, List<String> recipients) {

		// this

		Map<String, Object> map = MessageHelper.createAttributeMap(outMessage);
		for (String toRegId : recipients) {
			String messageId = Util.getUniqueMessageId();
			map.put("message_id", messageId);
			map.put("to", toRegId);
			String jsonRequest = MessageHelper.createJsonMessage(map);
			send(jsonRequest);
		}
	}

}
