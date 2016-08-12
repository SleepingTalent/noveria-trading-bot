package com.noveria.fxtrading.oanda.restapi.streaming.marketdata;

import com.google.common.collect.Lists;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.noveria.fxtrading.common.BaseTest;
import com.noveria.fxtrading.common.CurrencyPairs;
import com.noveria.fxtrading.heartbeats.HeartBeatCallback;
import com.noveria.fxtrading.heartbeats.HeartBeatCallbackImpl;
import com.noveria.fxtrading.heartbeats.HeartBeatPayLoad;
import com.noveria.fxtrading.instrument.TradeableInstrument;
import com.noveria.fxtrading.marketdata.MarketDataPayLoad;
import com.noveria.fxtrading.marketdata.MarketEventCallback;
import com.noveria.fxtrading.marketdata.MarketEventHandlerImpl;
import com.noveria.fxtrading.streaming.marketdata.MarketDataStreamingService;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

public class MarketDataStreamingServiceTest extends BaseTest{

	private static final Logger LOG = Logger.getLogger(MarketDataStreamingServiceTest.class);

    private final String heartbeatSourceId = "DEMO_MKTDATASTREAM";

    MarketDataStreamingService mktDataStreaminService;

    EventBus eventBus;

    MarketEventCallback<String> mktEventCallback;
    HeartBeatCallback<DateTime> heartBeatCallback;

    @Before
	public void setUp() {
        eventBus = new EventBus();
        eventBus.register(new DataSubscriber());

        mktEventCallback = new MarketEventHandlerImpl<String>(eventBus);
        heartBeatCallback = new HeartBeatCallbackImpl<DateTime>(eventBus);

        Collection<TradeableInstrument<String>> instruments = Lists.newArrayList(
                CurrencyPairs.EUR_USD.getTradableInstrument(), CurrencyPairs.GBP_NZD.getTradableInstrument());

        mktDataStreaminService = new OandaMarketDataStreamingService(streamUrl, accessToken,
                accountId, instruments, mktEventCallback, heartBeatCallback, heartbeatSourceId);
	}

    @Test
    public void startMarketDataStreaming_streamsMarketData_for_20_seconds() throws Exception {
        LOG.info("++++++++++++ Starting Market Data Streaming +++++++++++++++++++++");
        mktDataStreaminService.startMarketDataStreaming();
        Thread.sleep(20000L);
        mktDataStreaminService.stopMarketDataStreaming();
    }

	private static class DataSubscriber {

		@Subscribe
		@AllowConcurrentEvents
		public void handleMarketDataEvent(MarketDataPayLoad<String> marketDataPayLoad) {
			LOG.info(String.format("TickData event: %s @ %s. Bid Price = %3.5f, Ask Price = %3.5f", marketDataPayLoad
                            .getInstrument().getInstrument(), marketDataPayLoad.getEventDate(),
                    marketDataPayLoad.getBidPrice(), marketDataPayLoad.getAskPrice()));
		}

		@Subscribe
		@AllowConcurrentEvents
		public void handleHeartBeats(HeartBeatPayLoad<DateTime> payLoad) {
			LOG.info(String.format("Heartbeat received @ %s from source %s", payLoad.getHeartBeatPayLoad(), payLoad
                    .getHeartBeatSource()));
		}

	}
}
