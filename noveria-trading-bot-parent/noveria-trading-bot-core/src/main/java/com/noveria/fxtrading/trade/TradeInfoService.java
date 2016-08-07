package com.noveria.fxtrading.trade;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.PostConstruct;

import com.noveria.fxtrading.account.Account;
import com.noveria.fxtrading.account.AccountDataProvider;
import com.noveria.fxtrading.instrument.TradeableInstrument;
import com.noveria.fxtrading.utils.TradingUtils;
import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

public class TradeInfoService<M, N, K> {

	private static final Logger LOG = Logger.getLogger(TradeInfoService.class);
	private final TradeManagementProvider<M, N, K> tradeManagementProvider;
	private final AccountDataProvider<K> accountDataProvider;
	private final ConcurrentMap<K, Map<TradeableInstrument<N>, Collection<Trade<M, N, K>>>> tradesCache = Maps
			.newConcurrentMap();

	private final ReadWriteLock lock = new ReentrantReadWriteLock();

	public TradeInfoService(TradeManagementProvider<M, N, K> tradeManagementProvider,
			AccountDataProvider<K> accountDataProvider) {
		this.tradeManagementProvider = tradeManagementProvider;
		this.accountDataProvider = accountDataProvider;
	}

	/**
	 * 
	 * @param instrument
	 * @return a Collection of accountIds that have at least 1 trade for the
	 *         given instrument. An empty Collection is returned if none satisfy
	 *         this condition.
	 */
	public Collection<K> findAllAccountsWithInstrumentTrades(TradeableInstrument<N> instrument) {
		Collection<K> accountIds = Sets.newHashSet();
		lock.readLock().lock();
		try {
			for (K accountId : this.tradesCache.keySet()) {
				if (isTradeExistsForInstrument(instrument, accountId)) {
					accountIds.add(accountId);
				}
			}
		} finally {
			lock.readLock().unlock();
		}
		return accountIds;
	}

	/**
	 * A method that initializes the service and primarily the cache of trades
	 * per account for the service. This method is automatically invoked by
	 * Spring Framework(if used) once the ApplicationContext is initialized. If
	 * not using Spring, the consumer of this service must call this method
	 * first in order to construct the cache.
	 * 
	 * @see TradeManagementProvider
	 */
	@PostConstruct
	public void init() {
		reconstructCache();
	}

	private void reconstructCache() {
		lock.writeLock().lock();
		try {
			tradesCache.clear();
			Collection<Account<K>> accounts = this.accountDataProvider.getLatestAccountInfo();
			for (Account<K> account : accounts) {
				Map<TradeableInstrument<N>, Collection<Trade<M, N, K>>> tradeMap = getTradesPerInstrumentForAccount(account
						.getAccountId());
				tradesCache.put(account.getAccountId(), tradeMap);
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	public int findNetPositionCountForCurrency(String currency) {
		int posCt = 0;
		lock.readLock().lock();
		try {
			for (K accountId : this.tradesCache.keySet()) {
				posCt += findNetPositionCountForCurrency(currency, accountId);
			}
		} finally {
			lock.readLock().unlock();
		}
		return posCt;
	}

	/**
	 * 
	 * @return a Collection of all trades that belong to all accounts for the
	 *         user
	 */
	public Collection<Trade<M, N, K>> getAllTrades() {
		lock.readLock().lock();
		try {
			Collection<Trade<M, N, K>> trades = Lists.newArrayList();
			for (K accId : this.tradesCache.keySet()) {
				trades.addAll(getTradesForAccount(accId));
			}
			return trades;
		} finally {
			lock.readLock().unlock();
		}

	}

	private Map<TradeableInstrument<N>, Collection<Trade<M, N, K>>> getTradesPerInstrumentForAccount(K accountId) {
		Collection<Trade<M, N, K>> trades = this.tradeManagementProvider.getTradesForAccount(accountId);
		Map<TradeableInstrument<N>, Collection<Trade<M, N, K>>> tradeMap = Maps.newHashMap();
		for (Trade<M, N, K> ti : trades) {
			Collection<Trade<M, N, K>> tradeLst = null;
			if (tradeMap.containsKey(ti.getInstrument())) {
				tradeLst = tradeMap.get(ti.getInstrument());
			} else {
				tradeLst = Lists.newArrayList();
				tradeMap.put(ti.getInstrument(), tradeLst);
			}
			tradeLst.add(ti);
		}
		return tradeMap;
	}

	/**
	 * 
	 * @param accountId
	 * @param instrument
	 * @return a Collection of Trades for a given instrument and for a given
	 *         account.An empty Collection is returned if none found.
	 */
	public Collection<Trade<M, N, K>> getTradesForAccountAndInstrument(K accountId, TradeableInstrument<N> instrument) {
		Map<TradeableInstrument<N>, Collection<Trade<M, N, K>>> tradesForAccount = this.tradesCache.get(accountId);
		synchronized (tradesForAccount) {
			if (!TradingUtils.isEmpty(tradesForAccount) && tradesForAccount.containsKey(instrument)) {
				return Lists.newArrayList(tradesForAccount.get(instrument));
			}
		}
		return Collections.emptyList();
	}

	/**
	 * 
	 * @param accountId
	 * @return a Collection of all Trades for a given accountId. An empty
	 *         Collection is returned if none found.
	 */
	public Collection<Trade<M, N, K>> getTradesForAccount(K accountId) {
		Map<TradeableInstrument<N>, Collection<Trade<M, N, K>>> tradesForAccount = this.tradesCache.get(accountId);
		Collection<Trade<M, N, K>> trades = Lists.newArrayList();
		synchronized (tradesForAccount) {
			if (TradingUtils.isEmpty(tradesForAccount)) {
				return trades;
			}
			for (Collection<Trade<M, N, K>> tradeLst : tradesForAccount.values()) {
				trades.addAll(tradeLst);
			}
		}
		return trades;
	}

	private int findNetPositionCountForCurrency(String currency, K accountId) {
		Map<TradeableInstrument<N>, Collection<Trade<M, N, K>>> tradeMap = this.tradesCache.get(accountId);
		synchronized (tradeMap) {
			if (TradingUtils.isEmpty(tradeMap)) {
				return 0;
			} else {
				int positionCtr = 0;
				for (Collection<Trade<M, N, K>> trades : tradeMap.values()) {
					for (Trade<M, N, K> tradeInfo : trades) {
						positionCtr += TradingUtils.getSign(tradeInfo.getInstrument().getInstrument(), tradeInfo
								.getSide(), currency);
					}
				}

				return positionCtr;
			}
		}
	}

	/**
	 * A convenient method to refresh the internal cache of trades for an
	 * account in a ThreadSafe manner. Ideally this method should be invoked
	 * when the Trading platform notifies of a Trading event such as a creation
	 * of Trade when an Order is filled or the Trade is settled when a stopLoss
	 * or a takeProfit level is hit.
	 * 
	 * @param accountId
	 */
	public void refreshTradesForAccount(K accountId) {
		Map<TradeableInstrument<N>, Collection<Trade<M, N, K>>> tradeMap = getTradesPerInstrumentForAccount(accountId);
		Map<TradeableInstrument<N>, Collection<Trade<M, N, K>>> oldTradeMap = tradesCache.get(accountId);
		synchronized (oldTradeMap) {
			oldTradeMap.clear();
			oldTradeMap.putAll(tradeMap);
		}

	}

	/**
	 * 
	 * @param instrument
	 * @return boolean to indicate whether a trade with given instrument exists.
	 */
	public boolean isTradeExistsForInstrument(TradeableInstrument<N> instrument) {
		lock.readLock().lock();
		try {
			for (K accountId : this.tradesCache.keySet()) {
				if (isTradeExistsForInstrument(instrument, accountId)) {
					return true;
				}
			}
		} finally {
			lock.readLock().unlock();
		}
		return false;
	}

	private boolean isTradeExistsForInstrument(TradeableInstrument<N> instrument, K accountId) {
		Map<TradeableInstrument<N>, Collection<Trade<M, N, K>>> tradesForAccount = this.tradesCache.get(accountId);
		synchronized (tradesForAccount) {
			if (TradingUtils.isEmpty(tradesForAccount)) {
				return false;
			}
			return tradesForAccount.containsKey(instrument);
		}

	}
}
