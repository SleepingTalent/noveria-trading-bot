package com.noveria.fxtrading.marketdata;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;
import java.util.Map;

import com.noveria.fxtrading.TradingTestConstants;
import com.noveria.fxtrading.instrument.InstrumentService;
import com.noveria.fxtrading.instrument.TradeableInstrument;
import org.joda.time.DateTime;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class PipJumpCutOffCalculatorServiceTest {

	@SuppressWarnings("unchecked")
	@Test
	public void foo() {

		final double pip1 = 0.0001;
		final double pip2 = 0.01;

		CurrentPriceInfoProvider<String> currentPriceInfoProvider = mock(CurrentPriceInfoProvider.class);
		InstrumentService<String> instrumentService = mock(InstrumentService.class);
		DateTime now = DateTime.now();

		TradeableInstrument<String> eurusd = new TradeableInstrument<>("EUR_USD");
		Price<String> eurusdPrice = new Price<>(eurusd, 1.11905, 1.11915, now);

		TradeableInstrument<String> nzdchf = new TradeableInstrument<>("NZD_CHF");
		Price<String> nzdchfPrice = new Price<>(nzdchf, 0.65382, 0.65402, now);

		TradeableInstrument<String> gbpjpy = new TradeableInstrument<>("GBP_JPY");
		Price<String> gbpjpyPrice = new Price<>(gbpjpy, 166.506, 166.524, now);

		TradeableInstrument<String> gbpnzd = new TradeableInstrument<>("GBP_NZD");
		Price<String> gbpnzdPrice = new Price<>(gbpnzd, 2.17625, 2.17671, now);

		final double basePip = 45.0;
		PipJumpCutOffCalculator<String> pipCalculator = new PipJumpCutOffCalculatorService<>(eurusd,
				currentPriceInfoProvider, basePip, instrumentService);

		Map<TradeableInstrument<String>, Price<String>> eurusdnzdchfMap = Maps.newHashMap();
		eurusdnzdchfMap.put(eurusd, eurusdPrice);
		eurusdnzdchfMap.put(nzdchf, nzdchfPrice);

		Map<TradeableInstrument<String>, Price<String>> gbpnzdMap = Maps.newHashMap();
		gbpnzdMap.put(gbpnzd, gbpnzdPrice);

		Map<TradeableInstrument<String>, Price<String>> gbpjpyMap = Maps.newHashMap();
		gbpjpyMap.put(gbpjpy, gbpjpyPrice);

		when(currentPriceInfoProvider.getCurrentPricesForInstruments(eq(Lists.newArrayList(eurusd, nzdchf))))
				.thenReturn(eurusdnzdchfMap);
		when(currentPriceInfoProvider.getCurrentPricesForInstruments(eq(Lists.newArrayList(gbpnzd))))
				.thenReturn(gbpnzdMap);
		when(currentPriceInfoProvider.getCurrentPricesForInstruments(eq(Lists.newArrayList(gbpjpy))))
				.thenReturn(gbpjpyMap);

		when(instrumentService.getPipForInstrument(eurusd)).thenReturn(pip1);
		when(instrumentService.getPipForInstrument(gbpnzd)).thenReturn(pip1);
		when(instrumentService.getPipForInstrument(gbpjpy)).thenReturn(pip2);
		when(instrumentService.getPipForInstrument(nzdchf)).thenReturn(pip1);

		double v = pipCalculator.calculatePipJumpCutOff(nzdchf);
		assertEquals(26.2947, v, TradingTestConstants.precision);

		v = pipCalculator.calculatePipJumpCutOff(gbpjpy);
		assertEquals(66.9571, v, TradingTestConstants.precision);

		v = pipCalculator.calculatePipJumpCutOff(gbpnzd);
		assertEquals(87.5181, v, TradingTestConstants.precision);

		v = pipCalculator.calculatePipJumpCutOff(nzdchf);
		v = pipCalculator.calculatePipJumpCutOff(gbpjpy);

		verify(currentPriceInfoProvider, times(3)).getCurrentPricesForInstruments(any(Collection.class));

	}
}
