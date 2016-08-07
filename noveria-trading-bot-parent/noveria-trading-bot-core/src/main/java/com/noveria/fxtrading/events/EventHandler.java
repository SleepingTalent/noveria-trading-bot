package com.noveria.fxtrading.events;

public interface EventHandler<K, T extends EventPayLoad<K>> {

	void handleEvent(T payLoad);
}
