package com.noveria.fxtrading.heartbeats;

import java.util.Collection;

import com.noveria.fxtrading.streaming.heartbeats.HeartBeatStreamingService;
import org.joda.time.DateTime;

public class DefaultHeartBeatService extends AbstractHeartBeatService<DateTime> {

	public DefaultHeartBeatService(Collection<HeartBeatStreamingService> heartBeatStreamingServices) {
		super(heartBeatStreamingServices);
	}

	@Override
	protected boolean isAlive(HeartBeatPayLoad<DateTime> payLoad) {
		return payLoad != null
				&& (DateTime.now().getMillis() - payLoad.getHeartBeatPayLoad().getMillis()) < MAX_HEARTBEAT_DELAY;
	}

}
