package com.noveria.fxtrading.order;

import com.noveria.fxtrading.instrument.TradeableInstrument;
import com.noveria.fxtrading.utils.TradingUtils;

import java.util.Collection;

//TODO: introduce a cache  like in TradeInfoService in order to avoid making expensive rest calls.
public class OrderInfoService<M, N, K> {

	private final OrderManagementProvider<M, N, K> orderManagementProvider;

	public OrderInfoService(OrderManagementProvider<M, N, K> orderManagementProvider) {
		this.orderManagementProvider = orderManagementProvider;
	}

	public Collection<Order<N, M>> allPendingOrders() {
		return this.orderManagementProvider.allPendingOrders();
	}

	public Collection<Order<N, M>> pendingOrdersForAccount(K accountId) {
		return this.orderManagementProvider.pendingOrdersForAccount(accountId);
	}

	public Collection<Order<N, M>> pendingOrdersForInstrument(TradeableInstrument<N> instrument) {
		return this.orderManagementProvider.pendingOrdersForInstrument(instrument);
	}

	public Order<N, M> pendingOrderForAccount(M orderId, K accountId) {
		return this.orderManagementProvider.pendingOrderForAccount(orderId, accountId);
	}

	public int findNetPositionCountForCurrency(String currency) {
		Collection<Order<N, M>> allOrders = allPendingOrders();
		int positionCount = 0;
		for (Order<N, M> order : allOrders) {
			positionCount += TradingUtils.getSign(order.getInstrument().getInstrument(), order.getSide(), currency);
		}
		return positionCount;
	}
}
