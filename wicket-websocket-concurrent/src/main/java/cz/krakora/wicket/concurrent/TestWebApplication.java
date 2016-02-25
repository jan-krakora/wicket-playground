package cz.krakora.wicket.concurrent;

import static org.apache.wicket.RuntimeConfigurationType.DEPLOYMENT;
import static org.apache.wicket.RuntimeConfigurationType.DEVELOPMENT;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.ws.WebSocketSettings;
import org.apache.wicket.protocol.ws.api.message.IWebSocketPushMessage;
import org.apache.wicket.protocol.ws.api.registry.IWebSocketConnectionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestWebApplication extends WebApplication {

	private static final Logger LOG = LoggerFactory.getLogger(TestWebApplication.class);

	private final AtomicLong firstCounter = new AtomicLong(1);
	private final AtomicLong secondCounter = new AtomicLong(1);

	private ScheduledExecutorService firstExecutor = Executors.newSingleThreadScheduledExecutor();
	private ScheduledExecutorService secondExecutor = Executors.newSingleThreadScheduledExecutor();

	@Override
	protected void init() {
		super.init();

		firstExecutor.scheduleAtFixedRate(() -> {
			try {
				sendMessage(new TextMessage(String.valueOf(firstCounter.incrementAndGet())));
			} catch (Exception e) {
				LOG.error("An error from the first executor", e);
			}
		}, 0, 1, TimeUnit.SECONDS);

		secondExecutor.scheduleAtFixedRate(() -> {
			try {
				long count = secondCounter.getAndUpdate(val -> val * 2);
				StringBuilder sb = new StringBuilder();

				for (int i = 0; i < count; i++) {
					sb.append("This is some dummy text.");
				}
				sendMessage(new LongTextMessage(sb.toString()));
			} catch (Exception e) {
				LOG.error("An error from the second executor", e);
			}
		}, 0, 1, TimeUnit.SECONDS);
	}

	@Override
	protected void onDestroy() {
		firstExecutor.shutdownNow();
		secondExecutor.shutdownNow();
	}

	private void sendMessage(IWebSocketPushMessage message) {
		WebSocketSettings webSocketSettings = WebSocketSettings.Holder.get(this);
		IWebSocketConnectionRegistry webSocketConnectionRegistry = webSocketSettings.getConnectionRegistry();

		webSocketConnectionRegistry.getConnections(this).forEach(connection -> {
			try {
				connection.sendMessage(message);
			} catch (Exception e) {
				LOG.error("An error when sending push message", e);
			}
		});
	}

	@Override
	public Class<? extends Page> getHomePage() {
		return MainPage.class;
	}

	public static TestWebApplication get() {
        return (TestWebApplication) Application.get();
    }

	public static boolean isDeployment() {
		return get().getConfigurationType() == DEPLOYMENT;
	}

	public static boolean isDevelopment() {
		return get().getConfigurationType() == DEVELOPMENT;
	}

}