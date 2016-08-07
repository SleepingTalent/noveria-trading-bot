package com.noveria.fxtrading.heartbeats;

public interface HeartBeatCallback<T> {

	void onHeartBeat(HeartBeatPayLoad<T> payLoad);
}
