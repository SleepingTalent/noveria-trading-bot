
package com.noveria.fxtrading.events;

public class EventPayLoad<T> {

	private final Event event;
	private final T payLoad;

	public EventPayLoad(Event event, T payLoad) {
		this.event = event;
		this.payLoad = payLoad;
	}

	public Event getEvent() {
		return this.event;
	}

	public T getPayLoad() {
		return this.payLoad;
	}
}
