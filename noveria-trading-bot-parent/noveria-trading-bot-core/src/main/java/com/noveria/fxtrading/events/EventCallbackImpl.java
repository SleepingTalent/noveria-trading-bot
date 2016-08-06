package com.noveria.fxtrading.events;

import com.google.common.eventbus.EventBus;

public class EventCallbackImpl<T> implements EventCallback<T> {

	private final EventBus eventBus;

	public EventCallbackImpl(final EventBus eventBus) {
		this.eventBus = eventBus;
	}

	public void onEvent(EventPayLoad<T> eventPayLoad) {
		this.eventBus.post(eventPayLoad);
	}

}
