package at.veljovic.fileSync;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class App implements AutoCloseable {

	private CamelContext camelContext = new DefaultCamelContext();
	private AtomicBoolean isRunning = new AtomicBoolean(false);
	private AtomicLong countRunning = new AtomicLong(0);
	private Timer timer = new Timer(true);

	public void run() throws Exception {
		isRunning.set(true);

		camelContext.addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("file:C:\\temp_av\\a?noop=true&recursive=true&maxDepth=100&idempotentKey=${file:name}-${file:size}")
						.threads(5).process(exchange -> onAfterRead()).to("file:C:\\temp_av\\b")
						.process(exchange -> onAfterWrite());
			}

		});

		camelContext.addStartupListener((context, alreadyStarted) -> {
			System.out.println("startuped");
			timer.scheduleAtFixedRate(new TimerTask() {
				@Override
				public void run() {
					System.out.println("count " + countRunning.get());
					if (countRunning.get() == 0) {
						isRunning.set(false);
					}
				}
			}, 1000, 1000);
		});

		camelContext.start();

		while (isRunning()) {
			Thread.sleep(10);
		}

		stop();
	}

	private void onAfterRead() {
		System.out.println("onAfterRead");
		countRunning.getAndIncrement();
	}

	private void onAfterWrite() {
		System.out.println("onAfterWrite");
		countRunning.decrementAndGet();
	}

	public void stop() {
		camelContext.stop();
	}

	@Override
	public void close() throws Exception {
		camelContext.close();
	}

	public boolean isRunning() {
		return isRunning.get();
	}

	public static void main(String[] args) {
		try (App app = new App()) {
			app.run();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
