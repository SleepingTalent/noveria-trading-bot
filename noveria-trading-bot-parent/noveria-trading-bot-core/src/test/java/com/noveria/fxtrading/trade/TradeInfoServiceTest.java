package com.noveria.fxtrading.trade;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collection;

import com.noveria.fxtrading.TradingSignal;
import com.noveria.fxtrading.TradingTestConstants;
import com.noveria.fxtrading.account.Account;
import com.noveria.fxtrading.account.AccountDataProvider;
import com.noveria.fxtrading.instrument.TradeableInstrument;
import org.joda.time.DateTime;
import org.junit.Test;

import com.google.common.collect.Lists;

public class TradeInfoServiceTest {

	@Test
	public void netPositionCountForCurrencyTest() {
		TradeInfoService<Long, String, Long> service = createService();
		int jpyCt = service.findNetPositionCountForCurrency("JPY");
		assertEquals(0, jpyCt);
		int nzdCt = service.findNetPositionCountForCurrency("NZD");
		assertEquals(1, nzdCt);
		int audCt = service.findNetPositionCountForCurrency("AUD");
		assertEquals(-1, audCt);
	}

	@Test
	public void allTradesTest() {
		TradeInfoService<Long, String, Long> service = createService();
		Collection<Trade<Long, String, Long>> allTrades = service.getAllTrades();
		assertEquals(7, allTrades.size());
	}

	@Test
	public void allTradesForAccountAndInstrumentTest() {
		TradeInfoService<Long, String, Long> service = createService();
		Collection<Trade<Long, String, Long>> trades = service.getTradesForAccountAndInstrument(
				TradingTestConstants.accountId, new TradeableInstrument<String>("USD_JPY"));
		assertFalse(trades.isEmpty());
		trades = service.getTradesForAccountAndInstrument(TradingTestConstants.accountId,
				new TradeableInstrument<String>("USD_CHF"));
		assertTrue(trades.isEmpty());
	}

	@Test
	public void tradeExistsTest() {
		TradeInfoService<Long, String, Long> service = createService();
		assertTrue(service.isTradeExistsForInstrument(new TradeableInstrument<String>("AUD_USD")));
		assertFalse(service.isTradeExistsForInstrument(new TradeableInstrument<String>("AUD_CHF")));
	}

	@Test
	public void tradesForAccountTest() {
		TradeInfoService<Long, String, Long> service = createService();
		Collection<Trade<Long, String, Long>> allTradesAcc1 = service
				.getTradesForAccount(TradingTestConstants.accountId);
		assertEquals(3, allTradesAcc1.size());
		// assertEquals(allTradesAcc1.contains(new
		// TradeableInstrument<String>("EUR_USD")));
		Collection<Trade<Long, String, Long>> allTradesAcc2 = service
				.getTradesForAccount(TradingTestConstants.accountId2);
		assertEquals(4, allTradesAcc2.size());
		// assertEquals(allTradesAcc1.contains(new
		// TradeableInstrument<String>("GBP_NZD")));

	}

	@Test
	public void accountsForInstrumentsTest() {
		TradeInfoService<Long, String, Long> service = createService();
		Collection<Long> accountIds = service
				.findAllAccountsWithInstrumentTrades(new TradeableInstrument<String>("EUR_USD"));
		assertEquals(2, accountIds.size());
		accountIds = service.findAllAccountsWithInstrumentTrades(new TradeableInstrument<String>("EUR_JPY"));
		assertEquals(1, accountIds.size());
	}

	@SuppressWarnings("unchecked")
	private TradeInfoService<Long, String, Long> createService() {
		TradeManagementProvider<Long, String, Long> tradeManagementProvider = mock(TradeManagementProvider.class);
		AccountDataProvider<Long> accountDataProvider = mock(AccountDataProvider.class);
		TradeInfoService<Long, String, Long> service = new TradeInfoService<Long, String, Long>(tradeManagementProvider,
				accountDataProvider);
		service.init();
		Account<Long> account1 = mock(Account.class);
		Account<Long> account2 = mock(Account.class);
		when(account1.getAccountId()).thenReturn(TradingTestConstants.accountId);
		when(account2.getAccountId()).thenReturn(TradingTestConstants.accountId2);
		when(accountDataProvider.getLatestAccountInfo()).thenReturn(Lists.newArrayList(account1, account2));
		when(tradeManagementProvider.getTradesForAccount(TradingTestConstants.accountId))
				.thenReturn(createSampleTrades1());
		when(tradeManagementProvider.getTradesForAccount(TradingTestConstants.accountId2))
				.thenReturn(createSampleTrades2());
		service.init();
		return service;
	}

	private Collection<Trade<Long, String, Long>> createSampleTrades1() {
		Collection<Trade<Long, String, Long>> trades = Lists.newArrayList();
		trades.add(
				new Trade<Long, String, Long>(2001L, 10, TradingSignal.LONG, new TradeableInstrument<String>("GBP_USD"),
						DateTime.now(), 0.0, 1.5365, 0.0, TradingTestConstants.accountId));
		trades.add(
				new Trade<Long, String, Long>(2003L, 10, TradingSignal.LONG, new TradeableInstrument<String>("USD_JPY"),
						DateTime.now(), 0.0, 120.15, 0.0, TradingTestConstants.accountId));
		trades.add(new Trade<Long, String, Long>(2005L, 10, TradingSignal.SHORT,
				new TradeableInstrument<String>("EUR_USD"), DateTime.now(), 0.0, 1.2429, 0.0,
				TradingTestConstants.accountId));
		return trades;
	}

	private Collection<Trade<Long, String, Long>> createSampleTrades2() {
		Collection<Trade<Long, String, Long>> trades = Lists.newArrayList();

		trades.add(new Trade<Long, String, Long>(2002L, 10, TradingSignal.SHORT,
				new TradeableInstrument<String>("EUR_JPY"), DateTime.now(), 0.0, 135.55, 0.0,
				TradingTestConstants.accountId2));
		trades.add(new Trade<Long, String, Long>(2004L, 10, TradingSignal.SHORT,
				new TradeableInstrument<String>("GBP_NZD"), DateTime.now(), 0.0, 2.39, 0.0,
				TradingTestConstants.accountId2));
		trades.add(new Trade<Long, String, Long>(2006L, 10, TradingSignal.SHORT,
				new TradeableInstrument<String>("AUD_USD"), DateTime.now(), 0.0, 0.8123, 0.0,
				TradingTestConstants.accountId2));
		trades.add(
				new Trade<Long, String, Long>(2007L, 10, TradingSignal.LONG, new TradeableInstrument<String>("EUR_USD"),
						DateTime.now(), 0.0, 1.2515, 0.0, TradingTestConstants.accountId2));
		return trades;
	}
}
