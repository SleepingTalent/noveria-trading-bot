package com.noveria.fxtrading.streaming.events;

/**
 * A service that provides trade/order/account related events streaming.
 * Normally the implementation would create a dedicated connection to the
 * platform or register callback listener(s) to receive events. It is
 * recommended that the service delegate the handling of events to specific
 * handlers which can parse and make sense of the different plethora of events
 * received.
 * 
 * @author Shekhar Varshney
 *
 */
public interface EventsStreamingService {

	/**
	 * Start the streaming service which would ideally create a dedicated
	 * connection to the platform or callback listener(s). Ideally multiple
	 * connections requesting the same event types should not be created.
	 */
	void startEventsStreaming();

	/**
	 * Stop the events streaming services and dispose any resources/connections
	 * in a suitable manner such that no resource leaks are created.
	 */
	void stopEventsStreaming();
}
