package com.noveria.fxtrading.oanda.restapi.order;

import java.util.Collection;

import com.noveria.fxtrading.common.BaseTest;
import com.noveria.fxtrading.common.CurrencyPairs;
import com.noveria.fxtrading.order.Order;
import com.noveria.fxtrading.order.OrderInfoService;
import com.noveria.fxtrading.order.OrderManagementProvider;
import org.apache.log4j.Logger;

import com.noveria.fxtrading.account.AccountDataProvider;
import com.noveria.fxtrading.instrument.TradeableInstrument;
import com.noveria.fxtrading.oanda.restapi.account.OandaAccountDataProviderService;
import com.noveria.fxtrading.oanda.restapi.order.OandaOrderManagementProvider;
import org.junit.Before;
import org.junit.Test;

public class OrderInfoServiceTest extends BaseTest {

	private static final Logger LOG = Logger.getLogger(OrderInfoServiceTest.class);

	OrderInfoService<Long, String, Long> orderInfoService;

	@Before
	public void setUp() {
		AccountDataProvider<Long> accountDataProvider = new OandaAccountDataProviderService(url,accessToken);
		OrderManagementProvider<Long, String, Long> orderManagementProvider = new OandaOrderManagementProvider(url, accessToken, accountDataProvider);

		orderInfoService = new OrderInfoService<Long, String, Long>(orderManagementProvider);
	}

	@Test
	public void test() {
		orderInfoService.allPendingOrders();

		Collection<Order<String, Long>> pendingOrdersGbpUsd = orderInfoService.pendingOrdersForInstrument(CurrencyPairs.GBP_USD.getTradableInstrument());

		LOG.info(String.format("+++++++++++++++++++ Dumping all pending orders for %s +++", CurrencyPairs.GBP_USD.getTradableInstrument().getInstrument()));

		for (Order<String, Long> order : pendingOrdersGbpUsd) {
			LOG.info(String.format("units=%d, takeprofit=%2.5f,stoploss=%2.5f,limitprice=%2.5f,side=%s", order
					.getUnits(), order.getTakeProfit(), order.getStopLoss(), order.getPrice(), order.getSide()));
		}

		int usdPosCt = orderInfoService.findNetPositionCountForCurrency("USD");
		int gbpPosCt = orderInfoService.findNetPositionCountForCurrency("GBP");

		LOG.info("Net Position count for USD = " + usdPosCt);
		LOG.info("Net Position count for GBP = " + gbpPosCt);

		Collection<Order<String, Long>> pendingOrders = orderInfoService.allPendingOrders();

		LOG.info("+++++++++++++++++++ Dumping all pending orders ++++++++");

		for (Order<String, Long> order : pendingOrders) {
			LOG.info(String.format("instrument=%s,units=%d, takeprofit=%2.5f,stoploss=%2.5f,limitprice=%2.5f,side=%s",
					order.getInstrument().getInstrument(), order.getUnits(), order.getTakeProfit(),
					order.getStopLoss(), order.getPrice(), order.getSide()));
		}
	}
}
