package com.noveria.fxtrading.oanda.restapi.marketdata.historic;

import java.util.List;

import com.noveria.fxtrading.common.BaseTest;
import com.noveria.fxtrading.common.CurrencyPairs;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.noveria.fxtrading.instrument.TradeableInstrument;
import com.noveria.fxtrading.marketdata.historic.CandleStick;
import com.noveria.fxtrading.marketdata.historic.CandleStickGranularity;
import com.noveria.fxtrading.marketdata.historic.HistoricMarketDataProvider;
import org.junit.Before;
import org.junit.Test;

public class HistoricMarketDataProviderTest extends BaseTest {

	private static final Logger LOG = Logger.getLogger(HistoricMarketDataProviderTest.class);

	HistoricMarketDataProvider<String> historicMarketDataProvider;

	@Before
	public void setUp() {
		historicMarketDataProvider = new OandaHistoricMarketDataProvider(url, accessToken);
	}

	@Test
	public void getCandleSticks_with_dailyGranularity_for_USD_CHF() {
		List<CandleStick<String>> candlesUsdChf = historicMarketDataProvider.getCandleSticks(
				CurrencyPairs.USB_CHF.getTradableInstrument(), CandleStickGranularity.D, 15);

		LOG.info(String.format("++++++++++++++++++ Last %d Candle Sticks with Daily Granularity for %s ++++++++++ ",
				candlesUsdChf.size(), CurrencyPairs.USB_CHF.getTradableInstrument().getInstrument()));

		for (CandleStick<String> candle : candlesUsdChf) {
			LOG.info(candle);
		}
	}

    @Test
    public void getCandleSticks_with_monthlyGranularity_for_GBP_AUD() {
          DateTime from = new DateTime(1420070400000L);// 01 Jan 2015
        DateTime to = new DateTime(1451606400000L);// 01 Jan 2016

        List<CandleStick<String>> candlesGbpAud = historicMarketDataProvider.getCandleSticks(
				CurrencyPairs.GBP_AUD.getTradableInstrument(), CandleStickGranularity.M, from, to);

        LOG.info(String.format("+++++++++++Candle Sticks From %s To %s with Monthly Granularity for %s ++++++++++ ",
				from, to, CurrencyPairs.GBP_AUD.getTradableInstrument().getInstrument()));

        for (CandleStick<String> candle : candlesGbpAud) {
            LOG.info(candle);
        }
    }
}
