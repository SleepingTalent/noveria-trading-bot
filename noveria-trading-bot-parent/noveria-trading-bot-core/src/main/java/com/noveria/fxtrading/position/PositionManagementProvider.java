package com.noveria.fxtrading.position;

import com.noveria.fxtrading.instrument.TradeableInstrument;

import java.util.Collection;


/**
 * A provider of services for instrument positions. A position for an instrument
 * is by definition aggregated trades for the instrument with an average price
 * where all trades must all be a LONG or a SHORT. It is a useful service to
 * project a summary of a given instrument and also if required close all trades
 * for a given instrument, ideally using a single call.
 * 
 * The implementation might choose to maintain an internal cache of positions in
 * order to reduce latency. If this is the case then it must find means to
 * either 1)hook into the event streaming and refresh the cache based on an
 * order/trade event or 2) regularly refresh the cache after a given time
 * period.
 * 
 *
 * @param <M>
 *            The type of instrumentId in class TradeableInstrument
 * @param <N>
 *            the type of accountId
 * 
 * @see TradeableInstrument
 */
public interface PositionManagementProvider<M, N> {

	/**
	 * 
	 * @param accountId
	 * @param instrument
	 * @return Position<M> for a given instrument and accountId(may be null if
	 *         all trades under a single account).
	 */
	Position<M> getPositionForInstrument(N accountId, TradeableInstrument<M> instrument);

	/**
	 * 
	 * @param accountId
	 * @return Collection of Position<M> objects for a given accountId.
	 */
	Collection<Position<M>> getPositionsForAccount(N accountId);

	/**
	 * close the position for a given instrument and accountId. This is one shot
	 * way to close all trades for a given instrument in an account.
	 * 
	 * @param accountId
	 * @param instrument
	 * @return if the operation was successful
	 */
	boolean closePosition(N accountId, TradeableInstrument<M> instrument);

}
