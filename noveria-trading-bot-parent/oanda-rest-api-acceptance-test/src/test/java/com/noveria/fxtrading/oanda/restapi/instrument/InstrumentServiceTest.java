package com.noveria.fxtrading.oanda.restapi.instrument;

import com.noveria.fxtrading.common.BaseTest;
import com.noveria.fxtrading.common.CurrencyPairs;
import com.noveria.fxtrading.instrument.InstrumentDataProvider;
import com.noveria.fxtrading.instrument.InstrumentService;
import com.noveria.fxtrading.instrument.TradeableInstrument;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertEquals;

public class InstrumentServiceTest extends BaseTest{

	private static final Logger LOG = Logger.getLogger(InstrumentServiceTest.class);

	InstrumentService<String> instrumentService;

	@Before
	public void setUp() {
		InstrumentDataProvider<String> instrumentDataProvider = new OandaInstrumentDataProviderService(url, accountId, accessToken);
		instrumentService = new InstrumentService<String>(instrumentDataProvider);
	}

	@Test
	public void getAllPairsWithCurrency_for_GBP_returns_asExpected() {
		Collection<TradeableInstrument<String>> gbpInstruments = instrumentService.getAllPairsWithCurrency("GBP");

		assertEquals(15,gbpInstruments.size());

		for (TradeableInstrument<String> instrument : gbpInstruments) {
			LOG.info(instrument);
		}
	}

	@Test
	public void getPipForInstrument_EUR_AUD_returns_expected_PIP() {

		Double euraudPip = instrumentService.getPipForInstrument(CurrencyPairs.EUR_AUD.getTradableInstrument());
		assertEquals(new Double(0.00010),euraudPip);

		LOG.info(String.format("Pip for instrument %s is %1.5f", CurrencyPairs.EUR_AUD.getTradableInstrument().getInstrument(), euraudPip));
	}

	@Test
	public void getPipForInstrument_USD_JPY_returns_expected_PIP() {


		Double usdjpyPip = instrumentService.getPipForInstrument(CurrencyPairs.USD_JPY.getTradableInstrument());
		assertEquals(new Double(0.01),usdjpyPip);

		LOG.info(String.format("Pip for instrument %s is %1.5f", CurrencyPairs.USD_JPY.getTradableInstrument().getInstrument(), usdjpyPip));
	}

	@Test
	public void getPipForInstrument_USD_ZAR_returns_expected_PIP() {


		Double usdzarPip = instrumentService.getPipForInstrument(CurrencyPairs.USD_ZAR.getTradableInstrument());
		assertEquals(new Double(0.00010),usdzarPip);

		LOG.info(String.format("Pip for instrument %s is %1.5f", CurrencyPairs.USD_ZAR.getTradableInstrument().getInstrument(), usdzarPip));
	}
}
