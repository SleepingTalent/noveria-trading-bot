package com.noveria.fxtrading.oanda.restapi.trade;

import com.noveria.fxtrading.BaseTradingConfig;
import com.noveria.fxtrading.account.AccountDataProvider;
import com.noveria.fxtrading.common.BaseTest;
import com.noveria.fxtrading.common.CurrencyPairs;
import com.noveria.fxtrading.oanda.restapi.account.OandaAccountDataProviderService;
import com.noveria.fxtrading.trade.Trade;
import com.noveria.fxtrading.trade.TradeInfoService;
import com.noveria.fxtrading.trade.TradeManagementProvider;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;

public class TradeInfoServiceTest extends BaseTest {

	private static final Logger LOG = Logger.getLogger(TradeInfoServiceTest.class);

	TradeInfoService<Long, String, Long> tradeInfoService;

	@Before
	public void setUp() {
		AccountDataProvider<Long> accountDataProvider = new OandaAccountDataProviderService(url, accessToken);

		BaseTradingConfig tradingConfig = new BaseTradingConfig();
		tradingConfig.setMinReserveRatio(0.05);
		tradingConfig.setMinAmountRequired(100.00);
		tradingConfig.setMaxAllowedQuantity(10);

		TradeManagementProvider<Long, String, Long> tradeManagementProvider = new OandaTradeManagementProvider(url,
				accessToken);

		tradeInfoService = new TradeInfoService<Long, String, Long>(tradeManagementProvider, accountDataProvider);
        tradeInfoService.init();
	}

	@Test
	public void getAllTrades_returns_allTradesInProgress() {
		Collection<Trade<Long, String, Long>> allTrades = tradeInfoService.getAllTrades();
		LOG.info(allTrades.size()+" Trades Found");

		for (Trade<Long, String, Long> trade : allTrades) {
			LOG.info(String.format("Units=%d,Side=%s,Instrument=%s,Price=%2.5f", trade.getUnits(), trade.getSide(),
					trade.getInstrument().getInstrument(), trade.getExecutionPrice()));
		}
	}

	@Test
	public void findNetPositionCountForCurrency_CHF() {
		int chfTrades = tradeInfoService.findNetPositionCountForCurrency("CHF");
		LOG.info("Net Position for CHF = " + chfTrades);
	}

	@Test
	public void findNetPositionCountForCurrency_CAD() {
		int cadTrades = tradeInfoService.findNetPositionCountForCurrency("CAD");
		LOG.info("Net Position for CAD = " + cadTrades);
	}

	@Test
	public void isTradeExistsForInstrument_CAD() {
		boolean isUsdCadTradeExists = tradeInfoService.isTradeExistsForInstrument(CurrencyPairs.USD_CAD.getTradableInstrument());
		LOG.info(CurrencyPairs.USD_CAD.getTradableInstrument().getInstrument() + " exists?" + isUsdCadTradeExists);

	}

	@Test
	public void isTradeExistsForInstrument_CHF() {
		boolean isCadChdTradeExists = tradeInfoService.isTradeExistsForInstrument(CurrencyPairs.CAD_CHF.getTradableInstrument());
		LOG.info(CurrencyPairs.CAD_CHF.getTradableInstrument().getInstrument() + " exists?" + isCadChdTradeExists);
	}

}
