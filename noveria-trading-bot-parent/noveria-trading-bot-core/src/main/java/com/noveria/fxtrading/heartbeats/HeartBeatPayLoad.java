package com.noveria.fxtrading.heartbeats;

import org.apache.commons.lang3.StringUtils;

public class HeartBeatPayLoad<T> {

	private final T payLoad;
	private final String source;

	public HeartBeatPayLoad(T payLoad) {
		this(payLoad, StringUtils.EMPTY);
	}

	public HeartBeatPayLoad(T payLoad, String source) {
		this.payLoad = payLoad;
		this.source = source;
	}

	public T getHeartBeatPayLoad() {
		return this.payLoad;
	}

	public String getHeartBeatSource() {
		return this.source;
	}
}
