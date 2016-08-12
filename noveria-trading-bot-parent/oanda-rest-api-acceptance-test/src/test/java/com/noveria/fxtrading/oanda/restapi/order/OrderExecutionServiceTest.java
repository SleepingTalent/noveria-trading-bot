package com.noveria.fxtrading.oanda.restapi.order;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.noveria.fxtrading.common.BaseTest;
import com.noveria.fxtrading.common.CurrencyPairs;
import com.noveria.fxtrading.order.OrderExecutionService;
import com.noveria.fxtrading.order.OrderInfoService;
import com.noveria.fxtrading.order.OrderManagementProvider;
import com.noveria.fxtrading.order.PreOrderValidationService;
import org.apache.log4j.Logger;

import com.noveria.fxtrading.BaseTradingConfig;
import com.noveria.fxtrading.TradingDecision;
import com.noveria.fxtrading.TradingSignal;
import com.noveria.fxtrading.account.AccountDataProvider;
import com.noveria.fxtrading.account.AccountInfoService;
import com.noveria.fxtrading.helper.ProviderHelper;
import com.noveria.fxtrading.instrument.TradeableInstrument;
import com.noveria.fxtrading.marketdata.CurrentPriceInfoProvider;
import com.noveria.fxtrading.marketdata.historic.HistoricMarketDataProvider;
import com.noveria.fxtrading.marketdata.historic.MovingAverageCalculationService;
import com.noveria.fxtrading.oanda.restapi.account.OandaAccountDataProviderService;
import com.noveria.fxtrading.oanda.restapi.helper.OandaProviderHelper;
import com.noveria.fxtrading.oanda.restapi.marketdata.OandaCurrentPriceInfoProvider;
import com.noveria.fxtrading.oanda.restapi.marketdata.historic.OandaHistoricMarketDataProvider;
import com.noveria.fxtrading.oanda.restapi.trade.OandaTradeManagementProvider;
import com.noveria.fxtrading.trade.TradeInfoService;
import com.noveria.fxtrading.trade.TradeManagementProvider;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class OrderExecutionServiceTest extends BaseTest {

	private static final Logger LOG = Logger.getLogger(OrderExecutionServiceTest.class);

    AccountInfoService<Long, String> accountInfoService;
    OrderManagementProvider<Long, String, Long> orderManagementProvider;
    BaseTradingConfig tradingConfig;
    PreOrderValidationService<Long, String, Long> preOrderValidationService;
    CurrentPriceInfoProvider<String> currentPriceInfoProvider;

    @Before
	public void setUp() {
        AccountDataProvider<Long> accountDataProvider = new OandaAccountDataProviderService(url, accessToken);
        currentPriceInfoProvider = new OandaCurrentPriceInfoProvider(url, accessToken);

        tradingConfig = new BaseTradingConfig();
        tradingConfig.setMinReserveRatio(0.05);
        tradingConfig.setMinAmountRequired(100.00);
        tradingConfig.setMaxAllowedQuantity(10);

        ProviderHelper<String> providerHelper = new OandaProviderHelper();

        accountInfoService = new AccountInfoService<Long, String>(accountDataProvider, currentPriceInfoProvider, tradingConfig, providerHelper);

        orderManagementProvider = new OandaOrderManagementProvider(url, accessToken, accountDataProvider);

        TradeManagementProvider<Long, String, Long> tradeManagementProvider = new OandaTradeManagementProvider(url, accessToken);

        OrderInfoService<Long, String, Long> orderInfoService = new OrderInfoService<Long, String, Long>(orderManagementProvider);

        TradeInfoService<Long, String, Long> tradeInfoService = new TradeInfoService<Long, String, Long>(tradeManagementProvider, accountDataProvider);

        HistoricMarketDataProvider<String> historicMarketDataProvider = new OandaHistoricMarketDataProvider(url, accessToken);

        MovingAverageCalculationService<String> movingAverageCalculationService = new MovingAverageCalculationService<String>(historicMarketDataProvider);

        preOrderValidationService = new PreOrderValidationService<Long, String, Long>(tradeInfoService, movingAverageCalculationService, tradingConfig, orderInfoService);
	}

	@Test
    @Ignore
	public void create_new_trading_decision_for_GBP_USD() throws Exception {
        BlockingQueue<TradingDecision<String>> orderQueue = new LinkedBlockingQueue<TradingDecision<String>>();

        OrderExecutionService<Long, String, Long> orderExecService = new OrderExecutionService<Long, String, Long>(
                orderQueue, accountInfoService, orderManagementProvider, tradingConfig, preOrderValidationService,
                currentPriceInfoProvider);

		orderExecService.init();

        double takeProfitPrice = 1.44;
        double stopLossPrice = 1.35;
        double limitPrice = 1.4;

        TradingDecision<String> decision = new TradingDecision<String>(CurrencyPairs.GBP_USD.getTradableInstrument(), TradingSignal.LONG, takeProfitPrice, stopLossPrice, limitPrice);

        orderQueue.offer(decision);

		Thread.sleep(10000);// enough time to place an order

		orderExecService.shutDown();

	}
}
