package com.noveria.fxtrading.marketdata;

import static org.junit.Assert.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import com.noveria.fxtrading.TradingTestConstants;
import com.noveria.fxtrading.instrument.TradeableInstrument;
import org.joda.time.DateTime;
import org.junit.Test;

import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public class MarketEventHandlerImplTest {

	private final double bid = 1.55;
	private final double ask = 1.5502;
	private final String gbpusd = "GBP_USD";
	private final int numSubscribers = 2;
	private final CountDownLatch done = new CountDownLatch(numSubscribers);

	@Test
	public void testRideEventBus() throws Exception {
		EventBus evtBus = new AsyncEventBus(Executors.newFixedThreadPool(numSubscribers));
		for (int i = 1; i <= numSubscribers; i++) {
			evtBus.register(new MarketDataSubscriber());
		}
		MarketEventCallback<String> callback = new MarketEventHandlerImpl<String>(evtBus);
		callback.onMarketEvent(new TradeableInstrument<String>(gbpusd), bid, ask, DateTime.now());
		done.await();
	}

	private class MarketDataSubscriber {

		@Subscribe
		@AllowConcurrentEvents
		public void processPayLoad(MarketDataPayLoad<String> payload) {
			assertEquals(gbpusd, payload.getInstrument().getInstrument());
			assertEquals(bid, payload.getBidPrice(), TradingTestConstants.precision);
			assertEquals(ask, payload.getAskPrice(), TradingTestConstants.precision);
			done.countDown();
		}
	}

}
