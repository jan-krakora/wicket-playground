package cz.krakora.wicket.concurrent;

import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;

public class LongTextMessage implements IWebSocketPushMessage {

	private final String text;

	public LongTextMessage(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

}
