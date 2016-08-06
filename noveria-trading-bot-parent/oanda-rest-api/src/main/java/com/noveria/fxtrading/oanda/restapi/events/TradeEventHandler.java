package com.noveria.fxtrading.oanda.restapi.events;

import java.util.Set;

import com.noveria.fxtrading.TradingSignal;
import com.noveria.fxtrading.account.transaction.Transaction;
import com.noveria.fxtrading.account.transaction.TransactionDataProvider;
import com.noveria.fxtrading.events.EventHandler;
import com.noveria.fxtrading.events.EventPayLoad;
import com.noveria.fxtrading.events.EventPayLoadToTweet;
import com.noveria.fxtrading.events.notification.email.EmailContentGenerator;
import com.noveria.fxtrading.events.notification.email.EmailPayLoad;
import com.noveria.fxtrading.instrument.InstrumentService;
import com.noveria.fxtrading.instrument.TradeableInstrument;
import com.noveria.fxtrading.oanda.restapi.OandaJsonKeys;
import com.noveria.fxtrading.oanda.restapi.utils.OandaUtils;
import com.noveria.fxtrading.trade.TradeInfoService;
import org.json.simple.JSONObject;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.Subscribe;

public class TradeEventHandler implements EventHandler<JSONObject, TradeEventPayLoad>,
		EmailContentGenerator<JSONObject>, EventPayLoadToTweet<JSONObject, TradeEventPayLoad> {

	private final Set<TradeEvents> tradeEventsSupported = Sets.newHashSet(TradeEvents.STOP_LOSS_FILLED,
			TradeEvents.TRADE_CLOSE, TradeEvents.TAKE_PROFIT_FILLED);
	private final TradeInfoService<Long, String, Long> tradeInfoService;
	private final TransactionDataProvider<Long, Long, String> transactionDataProvider;
	private final InstrumentService<String> instrumentService;

	public TradeEventHandler(TradeInfoService<Long, String, Long> tradeInfoService,
			TransactionDataProvider<Long, Long, String> transactionDataProvider,
			InstrumentService<String> instrumentService) {
		this.tradeInfoService = tradeInfoService;
		this.transactionDataProvider = transactionDataProvider;
		this.instrumentService = instrumentService;
	}

	@Subscribe
	@AllowConcurrentEvents
	public void handleEvent(TradeEventPayLoad payLoad) {
		Preconditions.checkNotNull(payLoad);
		if (!tradeEventsSupported.contains(payLoad.getEvent())) {
			return;
		}
		JSONObject jsonPayLoad = payLoad.getPayLoad();
		long accountId = (Long) jsonPayLoad.get(OandaJsonKeys.accountId);
		tradeInfoService.refreshTradesForAccount(accountId);
	}

	public EmailPayLoad generate(EventPayLoad<JSONObject> payLoad) {
		JSONObject jsonPayLoad = payLoad.getPayLoad();
		TradeableInstrument<String> instrument = new TradeableInstrument<String>(jsonPayLoad.get(
				OandaJsonKeys.instrument).toString());
		final String type = jsonPayLoad.get(OandaJsonKeys.type).toString();
		final long accountId = (Long) jsonPayLoad.get(OandaJsonKeys.accountId);
		final double accountBalance = ((Number) jsonPayLoad.get(OandaJsonKeys.accountBalance)).doubleValue();
		final long tradeId = (Long) jsonPayLoad.get(OandaJsonKeys.tradeId);
		final double pnl = ((Number) jsonPayLoad.get(OandaJsonKeys.pl)).doubleValue();
		final double interest = ((Number) jsonPayLoad.get(OandaJsonKeys.interest)).doubleValue();
		final long tradeUnits = (Long) jsonPayLoad.get(OandaJsonKeys.units);
		final String emailMsg = String
				.format("Trade event %s received for account %d. Trade id=%d. Pnl=%5.3f, Interest=%5.3f, Trade Units=%d. Account balance after the event=%5.2f",
						type, accountId, tradeId, pnl, interest, tradeUnits, accountBalance);
		final String subject = String.format("Trade event %s for %s", type, instrument.getInstrument());
		return new EmailPayLoad(subject, emailMsg);
	}

	public String toTweet(TradeEventPayLoad payLoad) {
		if (!tradeEventsSupported.contains(payLoad.getEvent())) {
			return null;
		}

		final JSONObject jsonPayLoad = payLoad.getPayLoad();
		final String instrument =  jsonPayLoad.get(OandaJsonKeys.instrument).toString();
		final String instrumentAsHashtag = OandaUtils.oandaToHashTagCcy(instrument);
		final long tradeUnits = (Long) jsonPayLoad.get(OandaJsonKeys.units);
		final double price = ((Number) jsonPayLoad.get(OandaJsonKeys.price)).doubleValue();

		final long origTransactionId = (Long) jsonPayLoad.get(OandaJsonKeys.tradeId);
		final long accountId = (Long) jsonPayLoad.get(OandaJsonKeys.accountId);
		Transaction<Long, Long, String> origTransaction = this.transactionDataProvider.getTransaction(
				origTransactionId,
				accountId);
		if (origTransaction == null) {
			String side = jsonPayLoad.get(OandaJsonKeys.side).toString();
			TradingSignal signal = OandaUtils.toTradingSignal(side);
			return String.format("Closed %s %d units of %s@%2.5f.", signal.flip().name(), tradeUnits,
					instrumentAsHashtag, price);
		}

		double pips = 0;

		if (origTransaction.getSide() == TradingSignal.LONG) {
			pips = (price - origTransaction.getPrice())
					/ this.instrumentService.getPipForInstrument(new TradeableInstrument<String>(instrument));
		} else {
			pips = (origTransaction.getPrice() - price)
					/ this.instrumentService.getPipForInstrument(new TradeableInstrument<String>(instrument));
		}
		return String.format("Closed %s %d units of %s@%2.5f for %3.1f pips.", origTransaction.getSide().name(),
				tradeUnits,
				instrumentAsHashtag, price, pips);

	}

}
