package com.noveria.fxtrading.oanda.restapi.events;

import com.noveria.fxtrading.events.EventPayLoad;
import org.json.simple.JSONObject;


public class TradeEventPayLoad extends EventPayLoad<JSONObject> {

	public TradeEventPayLoad(TradeEvents event, JSONObject payLoad) {
		super(event, payLoad);
	}

}
