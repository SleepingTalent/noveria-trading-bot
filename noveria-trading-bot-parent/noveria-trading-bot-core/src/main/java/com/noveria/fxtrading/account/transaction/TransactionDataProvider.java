package com.noveria.fxtrading.account.transaction;

import java.util.List;

public interface TransactionDataProvider<M, N, T> {

	Transaction<M, N, T> getTransaction(M transactionId, N accountId);

	List<Transaction<M, N, T>> getTransactionsGreaterThanId(M minTransactionId, N accountId);
}
