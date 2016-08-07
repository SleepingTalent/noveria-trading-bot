package com.noveria.fxtrading.trade;

import java.util.Collection;

/**
 * A provider of RUD operations on a single Trade. Normally Trades are grouped
 * under an account, so in order to perform these operations, a valid accountId
 * is normally required. Some providers may just have the concept of a single
 * account, so any operation on the Trade may always default to that single
 * account, in which case the accountId may be null.
 * 
 * A bulk operation to closeAll trades is deliberately left out to avoid
 * potential misuse.
 * 
 *
 * @param <M>
 *            The type of tradeId
 * @param <N>
 *            The type of instrumentId in class TradeableInstrument
 * @param <K>
 *            The type of accountId
 */
public interface TradeManagementProvider<M, N, K> {

	/**
	 * Modify an existing trade by providing accountId and tradeId to identify
	 * the trade. In some cases the tradeId may be sufficient to identify the
	 * trade and therefore accountId may be null. Only stopLoss and takeProfit
	 * parameters for the trade can be modified through this operation.
	 * 
	 * @param accountId
	 * @param tradeId
	 * @param stopLoss
	 * @param takeProfit
	 * @return boolean to indicate whether the modification was successful or
	 *         not.
	 */
	boolean modifyTrade(K accountId, M tradeId, double stopLoss, double takeProfit);

	/**
	 * Close an existing trade by providing accountId and tradeId to identify a
	 * given trade. Again, the accountId may be optional to close the trade
	 * 
	 * @param tradeId
	 * @param accountId
	 * @return boolean to indicate when the trade was successfully closed or
	 *         not.
	 */
	boolean closeTrade(M tradeId, K accountId);

	/**
	 * Retrieve trade for the given tradeId and/or accountId
	 * 
	 * @param tradeId
	 * @param accountId
	 * @return a Trade or null if not found.
	 */
	Trade<M, N, K> getTradeForAccount(M tradeId, K accountId);

	/**
	 * All Trades for a given account or an empty collection if none found. The
	 * ordering of trades such as by instruments or in chronological order is
	 * not guaranteed.
	 * 
	 * @param accountId
	 * @return a Collection of trades for the given account Id.
	 */
	Collection<Trade<M, N, K>> getTradesForAccount(K accountId);
}
