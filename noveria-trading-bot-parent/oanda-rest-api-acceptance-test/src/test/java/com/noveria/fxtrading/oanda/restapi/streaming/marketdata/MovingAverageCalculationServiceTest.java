package com.noveria.fxtrading.oanda.restapi.streaming.marketdata;

import com.noveria.fxtrading.common.BaseTest;
import com.noveria.fxtrading.common.CurrencyPairs;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;

import com.noveria.fxtrading.instrument.TradeableInstrument;
import com.noveria.fxtrading.marketdata.historic.CandleStickGranularity;
import com.noveria.fxtrading.marketdata.historic.HistoricMarketDataProvider;
import com.noveria.fxtrading.marketdata.historic.MovingAverageCalculationService;
import com.noveria.fxtrading.oanda.restapi.marketdata.historic.OandaHistoricMarketDataProvider;
import org.junit.Before;
import org.junit.Test;

public class MovingAverageCalculationServiceTest extends BaseTest {

    private static final Logger LOG = Logger.getLogger(MovingAverageCalculationServiceTest.class);

    MovingAverageCalculationService<String> movingAverageCalcService;

    @Before
    public void setUp() {
        HistoricMarketDataProvider<String> historicMarketDataProvider = new OandaHistoricMarketDataProvider(url, accessToken);
        movingAverageCalcService = new MovingAverageCalculationService<String>(historicMarketDataProvider);
    }

    @Test
    public void show_movingAverages_for_EUR_NZD_by_hour() {
        final int countIntervals = 30;

        ImmutablePair<Double, Double> eurnzdSmaAndWma = movingAverageCalcService.calculateSMAandWMAasPair(CurrencyPairs.EUR_NZD.getTradableInstrument(),
                countIntervals, CandleStickGranularity.H1);

        LOG.info(String.format("SMA=%2.5f,WMA=%2.5f for instrument=%s,granularity=%s for the last %d intervals",
                eurnzdSmaAndWma.left, eurnzdSmaAndWma.right, CurrencyPairs.EUR_NZD.getTradableInstrument().getInstrument(), CandleStickGranularity.H1,
                countIntervals));

    }

    @Test
    public void show_movingAverages_for_GBP_CHF_by_date_range() {

        DateTime from = new DateTime(1444003200000L);// 5 Oct 2015
        DateTime to = new DateTime(1453075200000L);// 18 Jan 2016

        ImmutablePair<Double, Double> gbpchfSmaAndWma = movingAverageCalcService.calculateSMAandWMAasPair(CurrencyPairs.GBP_CHF.getTradableInstrument(), from,
                to, CandleStickGranularity.W);

        LOG.info(String
                .format("SMA=%2.5f,WMA=%2.5f for instrument=%s,granularity=%s from %s to %s", gbpchfSmaAndWma.left,
                        gbpchfSmaAndWma.right, CurrencyPairs.GBP_CHF.getTradableInstrument().getInstrument(), CandleStickGranularity.W, from, to));

    }
}
