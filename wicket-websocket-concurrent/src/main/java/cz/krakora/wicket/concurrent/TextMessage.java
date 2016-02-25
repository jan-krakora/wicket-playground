package cz.krakora.wicket.concurrent;

import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;

public class TextMessage extends AbstractReadOnlyModel<String> implements IWebSocketPushMessage {

	private final String text;

	public TextMessage(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}

	@Override
	public String getObject() {
		return text;
	}

}
