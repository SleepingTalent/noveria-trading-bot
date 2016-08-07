package com.noveria.fxtrading.events;

public interface EventCallback<T> {

	void onEvent(EventPayLoad<T> eventPayLoad);
}
