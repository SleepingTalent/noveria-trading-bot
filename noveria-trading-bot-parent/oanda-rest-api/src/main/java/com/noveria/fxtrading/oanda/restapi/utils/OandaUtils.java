package com.noveria.fxtrading.oanda.restapi.utils;

import com.noveria.fxtrading.TradingConstants;
import com.noveria.fxtrading.TradingSignal;
import com.noveria.fxtrading.events.Event;
import com.noveria.fxtrading.events.EventPayLoad;
import com.noveria.fxtrading.oanda.restapi.OandaConstants;
import com.noveria.fxtrading.oanda.restapi.events.*;
import com.noveria.fxtrading.order.OrderType;
import com.noveria.fxtrading.utils.TradingUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.message.BasicHeader;
import org.json.simple.JSONObject;

import com.google.common.base.Preconditions;

public class OandaUtils {

	private OandaUtils() {
	}

	private static final Event findAppropriateType(Event[] events, String transactionType) {
		for (Event evt : events) {
			if (evt.name().equals(transactionType)) {
				return evt;
			}
		}
		return null;
	}

	public static Event toOandaTransactionType(String transactionType) {
		Preconditions.checkNotNull(transactionType);
		Event evt = findAppropriateType(AccountEvents.values(), transactionType);
		if (evt == null) {
			evt = findAppropriateType(OrderEvents.values(), transactionType);
			if (evt == null) {
				evt = findAppropriateType(TradeEvents.values(), transactionType);
			}
		}
		return evt;
	}

	public static EventPayLoad<JSONObject> toOandaEventPayLoad(String transactionType, JSONObject payLoad) {
		Preconditions.checkNotNull(transactionType);
		Event evt = findAppropriateType(AccountEvents.values(), transactionType);
		if (evt == null) {
			evt = findAppropriateType(OrderEvents.values(), transactionType);
			if (evt == null) {
				evt = findAppropriateType(TradeEvents.values(), transactionType);
				if (evt == null) {
					return null;
				} else {
					return new TradeEventPayLoad((TradeEvents) evt, payLoad);
				}
			} else {
				return new OrderEventPayLoad((OrderEvents) evt, payLoad);
			}
		} else {
			return new AccountEventPayLoad((AccountEvents) evt, payLoad);
		}

	}

	public static final BasicHeader createAuthHeader(String accessToken) {
		return new BasicHeader("Authorization", "Bearer " + accessToken);
	}

	public static String[] splitCcyPair(String instrument) {
		return TradingUtils.splitCcyPair(instrument, OandaConstants.CCY_PAIR_SEP);
	}

	public static final String toOandaCcy(String baseCcy, String quoteCcy) {
		final int expectedLen = 3;
		if (!StringUtils.isEmpty(baseCcy) && !StringUtils.isEmpty(quoteCcy) && baseCcy.length() == expectedLen
				&& quoteCcy.length() == expectedLen) {
			return String.format("%s%s%s", baseCcy, OandaConstants.CCY_PAIR_SEP, quoteCcy);
		}
		throw new IllegalArgumentException(String.format("base currency and quote currency cannot be null or empty"
				+ " and must be %d char length", expectedLen));
	}

	public static final String isoCcyToOandaCcy(String ccy) {
		final int expectedLen = 6;
		if (!StringUtils.isEmpty(ccy) && ccy.length() == expectedLen) {
			return String.format("%s%s%s", ccy.substring(0, 3), OandaConstants.CCY_PAIR_SEP, ccy.substring(3));
		}
		throw new IllegalArgumentException(String.format("expected a string with length = %d but got %s", expectedLen,
				ccy));
	}

	public static final String oandaToHashTagCcy(String oandaCcy) {
		String[] currencies = OandaUtils.splitCcyPair(oandaCcy);
		final String instrumentAsHashtag = TradingConstants.HASHTAG + currencies[0] + currencies[1];
		return instrumentAsHashtag;
	}

	public static final String hashTagCcyToOandaCcy(String ccy) {
		final int expectedLen = TradingUtils.CCY_PAIR_LEN;
		if (!StringUtils.isEmpty(ccy) && ccy.startsWith(TradingConstants.HASHTAG) && ccy.length() == expectedLen) {

			return isoCcyToOandaCcy(ccy.substring(1));
		}
		throw new IllegalArgumentException(String.format(
				"expected a string with length = %d beginning with %s but got %s", expectedLen,
				TradingConstants.HASHTAG, ccy));
	}

	public static TradingSignal toTradingSignal(String side) {
		if (OandaConstants.BUY.equals(side)) {
			return TradingSignal.LONG;
		} else if (OandaConstants.SELL.equals(side)) {
			return TradingSignal.SHORT;
		} else {
			return TradingSignal.NONE;
		}
	}

	public static OrderType toOrderType(String type) {
		if (OandaConstants.ORDER_MARKET.equals(type)) {
			return OrderType.MARKET;
		} else if (OandaConstants.ORDER_LIMIT.equals(type) || OandaConstants.ORDER_MARKET_IF_TOUCHED.equals(type)) {
			return OrderType.LIMIT;
		} else {
			throw new IllegalArgumentException("Unsupported order type:" + type);
		}
	}

	public static String toType(OrderType orderType) {
		switch (orderType) {
		case LIMIT:
			return OandaConstants.ORDER_LIMIT;
		case MARKET:
			return OandaConstants.ORDER_MARKET;
		default:
			return null;
		}
	}

	public static String toSide(TradingSignal signal) {
		switch (signal) {
		case LONG:
			return OandaConstants.BUY;
		case SHORT:
			return OandaConstants.SELL;
		default:
			return OandaConstants.NONE;
		}
	}

}
