package com.noveria.fxtrading.heartbeats;

import com.google.common.eventbus.EventBus;

public class HeartBeatCallbackImpl<T> implements HeartBeatCallback<T> {

	private final EventBus eventBus;

	public HeartBeatCallbackImpl(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	@Override
	public void onHeartBeat(HeartBeatPayLoad<T> payLoad) {
		this.eventBus.post(payLoad);
	}

}
