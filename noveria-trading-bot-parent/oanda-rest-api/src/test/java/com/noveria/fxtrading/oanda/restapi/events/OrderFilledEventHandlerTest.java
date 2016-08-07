package com.noveria.fxtrading.oanda.restapi.events;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.json.simple.JSONObject;
import org.junit.Test;

import com.noveria.fxtrading.events.EventPayLoad;
import com.noveria.fxtrading.events.notification.email.EmailPayLoad;
import com.noveria.fxtrading.oanda.restapi.OandaJsonKeys;
import com.noveria.fxtrading.oanda.restapi.OandaTestConstants;
import com.noveria.fxtrading.trade.TradeInfoService;

public class OrderFilledEventHandlerTest {

	@Test
	public void generatePayLoad() {
		OrderFilledEventHandler eventHandler = new OrderFilledEventHandler(null);
		JSONObject jsonPayLoad = mock(JSONObject.class);
		EventPayLoad<JSONObject> payLoad = new OrderEventPayLoad(OrderEvents.ORDER_FILLED, jsonPayLoad);
		when(jsonPayLoad.containsKey(OandaJsonKeys.instrument)).thenReturn(true);
		when(jsonPayLoad.get(OandaJsonKeys.instrument)).thenReturn("GBP_CHF");
		when(jsonPayLoad.get(OandaJsonKeys.type)).thenReturn(OrderEvents.ORDER_FILLED);
		when(jsonPayLoad.get(OandaJsonKeys.accountId)).thenReturn(OandaTestConstants.accountId);
		when(jsonPayLoad.containsKey(OandaJsonKeys.accountBalance)).thenReturn(true);
		when(jsonPayLoad.get(OandaJsonKeys.accountBalance)).thenReturn(178.95);
		when(jsonPayLoad.get(OandaJsonKeys.id)).thenReturn(1002L);
		EmailPayLoad emailPayLoad = eventHandler.generate(payLoad);
		assertEquals("Order event ORDER_FILLED for GBP_CHF", emailPayLoad.getSubject());
		assertEquals(
				"Order event ORDER_FILLED received on account 123456. Order id=1002. Account balance after the event=178.95",
				emailPayLoad.getBody());
	}

	@Test
	public void unSupportedOrderEvent() {
		JSONObject jsonPayLoad = mock(JSONObject.class);
		OrderEventPayLoad payLoad = new OrderEventPayLoad(OrderEvents.ORDER_CANCEL, jsonPayLoad);
		@SuppressWarnings("unchecked")
		TradeInfoService<Long, String, Long> tradeInfoService = mock(TradeInfoService.class);
		OrderFilledEventHandler eventHandler = new OrderFilledEventHandler(tradeInfoService);
		eventHandler.handleEvent(payLoad);
		verify(tradeInfoService, times(0)).refreshTradesForAccount(OandaTestConstants.accountId);
	}

	@Test
	public void orderEvent() {
		JSONObject jsonPayLoad = mock(JSONObject.class);
		OrderEventPayLoad payLoad = new OrderEventPayLoad(OrderEvents.ORDER_FILLED, jsonPayLoad);
		when(jsonPayLoad.get(OandaJsonKeys.accountId)).thenReturn(OandaTestConstants.accountId);
		@SuppressWarnings("unchecked")
		TradeInfoService<Long, String, Long> tradeInfoService = mock(TradeInfoService.class);
		OrderFilledEventHandler eventHandler = new OrderFilledEventHandler(tradeInfoService);
		eventHandler.handleEvent(payLoad);
		verify(tradeInfoService, times(1)).refreshTradesForAccount(OandaTestConstants.accountId);
	}
}
