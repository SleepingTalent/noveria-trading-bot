package com.noveria.fxtrading.oanda.restapi.events;

import com.noveria.fxtrading.events.EventPayLoad;
import org.json.simple.JSONObject;

public class AccountEventPayLoad extends EventPayLoad<JSONObject> {

	public AccountEventPayLoad(AccountEvents event, JSONObject payLoad) {
		super(event, payLoad);
	}

}
