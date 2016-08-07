package com.noveria.fxtrading.events.notification.email;

import com.noveria.fxtrading.events.EventPayLoad;

public interface EmailContentGenerator<T> {

	EmailPayLoad generate(EventPayLoad<T> payLoad);

}
