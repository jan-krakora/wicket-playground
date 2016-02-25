package cz.krakora.wicket.concurrent;

import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.head.CssReferenceHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.protocol.ws.api.WebSocketBehavior;
import org.apache.wicket.protocol.ws.api.WebSocketRequestHandler;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;
import org.apache.wicket.request.resource.CssResourceReference;
import org.apache.wicket.request.resource.ResourceReference;

public class MainPage extends WebPage {

	private static final ResourceReference CSS = new CssResourceReference(MainPage.class, "MainPage.css");

	private Label messageLabel;
	private IModel<String> messageClassModel = new Model<>();

	@Override
	public void renderHead(IHeaderResponse response) {
		super.renderHead(response);
		response.render(CssReferenceHeaderItem.forReference(CSS));
	}

	@Override
	protected void onInitialize() {
		super.onInitialize();

		messageLabel = new Label("message");
		messageLabel.add(AttributeAppender.append("class", messageClassModel));
		messageLabel.setOutputMarkupId(true);
		add(messageLabel);

		add(new WebSocketBehavior() {

			@Override
			protected void onPush(WebSocketRequestHandler handler, IWebSocketPushMessage message) {
				if (message instanceof TextMessage) {
					TextMessage textMessage = (TextMessage) message;
					messageLabel.setDefaultModel(textMessage);
					handler.add(messageLabel);

				} else if (message instanceof LongTextMessage) {
					LongTextMessage longTextMessage = (LongTextMessage) message;
					handler.push(longTextMessage.getText());
				}
			}
		});
	}

}
