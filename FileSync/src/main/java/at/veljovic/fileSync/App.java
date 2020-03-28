package at.veljovic.fileSync;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.impl.DefaultCamelContext;

public class App {

	public static void main(String[] args) {
		final long durationMs = 300000;
		final CamelContext camelContext = new DefaultCamelContext();
		try {
			camelContext.addRoutes(new RouteBuilder() {
				@Override
				public void configure() throws Exception {
					from("file:/home/av/Schreibtisch/src?noop=true&recursive=true&maxDepth=100&idempotentKey=${file:name}-${file:size}")
							.to("file:/home/av/Schreibtisch/target");
				}
			});
			camelContext.start();
			Thread.sleep(durationMs);
			camelContext.stop();

		} catch (Exception camelException) {
			camelException.printStackTrace();
		}
	}

}
