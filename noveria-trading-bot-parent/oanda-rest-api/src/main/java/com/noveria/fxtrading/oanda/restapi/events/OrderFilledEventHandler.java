package com.noveria.fxtrading.oanda.restapi.events;

import java.util.Set;

import com.noveria.fxtrading.events.EventHandler;
import com.noveria.fxtrading.events.EventPayLoad;
import com.noveria.fxtrading.events.notification.email.EmailContentGenerator;
import com.noveria.fxtrading.events.notification.email.EmailPayLoad;
import com.noveria.fxtrading.instrument.TradeableInstrument;
import com.noveria.fxtrading.oanda.restapi.OandaJsonKeys;
import com.noveria.fxtrading.trade.TradeInfoService;
import org.json.simple.JSONObject;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;

public class OrderFilledEventHandler implements EventHandler<JSONObject, OrderEventPayLoad>,
		EmailContentGenerator<JSONObject> {
	private final Set<OrderEvents> orderEventsSupported = Sets.newHashSet(OrderEvents.ORDER_FILLED);
	private final TradeInfoService<Long, String, Long> tradeInfoService;

	public OrderFilledEventHandler(TradeInfoService<Long, String, Long> tradeInfoService) {
		this.tradeInfoService = tradeInfoService;
	}

	@Subscribe
	@AllowConcurrentEvents
	public void handleEvent(OrderEventPayLoad payLoad) {
		Preconditions.checkNotNull(payLoad);
		if (!orderEventsSupported.contains(payLoad.getEvent())) {
			return;
		}
		JSONObject jsonPayLoad = payLoad.getPayLoad();

		long accountId = (Long) jsonPayLoad.get(OandaJsonKeys.accountId);
		tradeInfoService.refreshTradesForAccount(accountId);
	}

	public EmailPayLoad generate(EventPayLoad<JSONObject> payLoad) {
		JSONObject jsonPayLoad = payLoad.getPayLoad();
		TradeableInstrument<String> instrument = new TradeableInstrument<String>(jsonPayLoad
				.containsKey(OandaJsonKeys.instrument) ? jsonPayLoad.get(OandaJsonKeys.instrument).toString() : "N/A");
		final String type = jsonPayLoad.get(OandaJsonKeys.type).toString();
		final long accountId = (Long) jsonPayLoad.get(OandaJsonKeys.accountId);
		final double accountBalance = jsonPayLoad.containsKey(OandaJsonKeys.accountBalance) ? ((Number) jsonPayLoad
				.get(OandaJsonKeys.accountBalance)).doubleValue() : 0.0;
		final long orderId = (Long) jsonPayLoad.get(OandaJsonKeys.id);
		final String emailMsg = String.format(
				"Order event %s received on account %d. Order id=%d. Account balance after the event=%5.2f", type,
				accountId, orderId, accountBalance);
		final String subject = String.format("Order event %s for %s", type, instrument.getInstrument());
		return new EmailPayLoad(subject, emailMsg);
	}

}
