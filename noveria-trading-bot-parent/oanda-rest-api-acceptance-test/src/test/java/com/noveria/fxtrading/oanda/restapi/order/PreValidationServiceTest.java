package com.noveria.fxtrading.oanda.restapi.order;

import com.noveria.fxtrading.common.BaseTest;
import com.noveria.fxtrading.common.CurrencyPairs;
import com.noveria.fxtrading.order.OrderInfoService;
import com.noveria.fxtrading.order.OrderManagementProvider;
import com.noveria.fxtrading.order.PreOrderValidationService;
import org.apache.log4j.Logger;

import com.noveria.fxtrading.BaseTradingConfig;
import com.noveria.fxtrading.TradingSignal;
import com.noveria.fxtrading.account.AccountDataProvider;
import com.noveria.fxtrading.instrument.TradeableInstrument;
import com.noveria.fxtrading.marketdata.historic.HistoricMarketDataProvider;
import com.noveria.fxtrading.marketdata.historic.MovingAverageCalculationService;
import com.noveria.fxtrading.oanda.restapi.account.OandaAccountDataProviderService;
import com.noveria.fxtrading.oanda.restapi.marketdata.historic.OandaHistoricMarketDataProvider;
import com.noveria.fxtrading.oanda.restapi.order.OandaOrderManagementProvider;
import com.noveria.fxtrading.oanda.restapi.trade.OandaTradeManagementProvider;
import com.noveria.fxtrading.trade.TradeInfoService;
import com.noveria.fxtrading.trade.TradeManagementProvider;
import org.junit.Before;
import org.junit.Test;

public class PreValidationServiceTest extends BaseTest {

	private static final Logger LOG = Logger.getLogger(PreValidationServiceTest.class);

    PreOrderValidationService<Long, String, Long> preOrderValidationService;

    TradeInfoService<Long, String, Long> tradeInfoService;
    OrderInfoService<Long, String, Long> orderInfoService;

	@Before
	public void setUp() {
        AccountDataProvider<Long> accountDataProvider = new OandaAccountDataProviderService(url, accessToken);

        OrderManagementProvider<Long, String, Long> orderManagementProvider = new OandaOrderManagementProvider(url,
                accessToken, accountDataProvider);

        TradeManagementProvider<Long, String, Long> tradeManagementProvider = new OandaTradeManagementProvider(url,
                accessToken);

        BaseTradingConfig tradingConfig = new BaseTradingConfig();
        tradingConfig.setMinReserveRatio(0.05);
        tradingConfig.setMinAmountRequired(100.00);
        tradingConfig.setMaxAllowedQuantity(10);
        tradingConfig.setMaxAllowedNetContracts(3);

        tradeInfoService = new TradeInfoService<Long, String, Long>(
                tradeManagementProvider, accountDataProvider);

        orderInfoService = new OrderInfoService<Long, String, Long>(
                orderManagementProvider);

        HistoricMarketDataProvider<String> historicMarketDataProvider = new OandaHistoricMarketDataProvider(url, accessToken);

        MovingAverageCalculationService<String> movingAverageCalculationService = new MovingAverageCalculationService<String>(
                historicMarketDataProvider);

        preOrderValidationService = new PreOrderValidationService<Long, String, Long>(
                tradeInfoService, movingAverageCalculationService, tradingConfig, orderInfoService);

        tradeInfoService.init();
	}

    @Test
    public void checkInstrumentNotAlreadyTraded_EUR_USD() {
        boolean isEurUsdTraded = preOrderValidationService.checkInstrumentNotAlreadyTraded(CurrencyPairs.EUR_USD.getTradableInstrument());
        LOG.info(CurrencyPairs.EUR_USD.getTradableInstrument().getInstrument() + " trade present? " + !isEurUsdTraded);
    }

    @Test
    public void checkInstrumentNotAlreadyTraded_USD_JPY() {
        boolean isUsdJpyTraded = preOrderValidationService.checkInstrumentNotAlreadyTraded(CurrencyPairs.USD_JPY.getTradableInstrument());
        LOG.info(CurrencyPairs.USD_JPY.getTradableInstrument().getInstrument() + " trade present? " + !isUsdJpyTraded);
    }

    @Test
    public void isInSafeZone_USD_ZAR() {
        boolean isUsdZarTradeInSafeZone = preOrderValidationService.isInSafeZone(TradingSignal.LONG, 17.9, CurrencyPairs.USD_ZAR.getTradableInstrument());
        LOG.info(CurrencyPairs.USD_ZAR.getTradableInstrument().getInstrument() + " in safe zone? " + isUsdZarTradeInSafeZone);
    }

    @Test
    public void isInSafeZone_EUR_USD() {
        boolean isEurUsdTradeInSafeZone = preOrderValidationService.isInSafeZone(TradingSignal.LONG, 1.2, CurrencyPairs.EUR_USD.getTradableInstrument());
        LOG.info(CurrencyPairs.EUR_USD.getTradableInstrument().getInstrument() + " in safe zone? " + isEurUsdTradeInSafeZone);
    }

    @Test
    public void checkLimitsForCcy_NZD_CHF() {
        preOrderValidationService.checkLimitsForCcy(CurrencyPairs.NZD_CHF.getTradableInstrument(), TradingSignal.LONG);
    }

}
