package com.noveria.fxtrading;

import com.noveria.fxtrading.instrument.TradeableInstrument;

public class TradingDecision<T> {
	private final TradingSignal signal;
	// private final double bidPriceAtDecision, askPriceAtDecision;
	private final TradeableInstrument<T> instrument;
	private final double takeProfitPrice;
	private final double stopLossPrice;
	private final SRCDECISION tradeSource;
	private final double limitPrice;

	public enum SRCDECISION {
		/*INTERNAL,*/SOCIAL_MEDIA, SPIKE, FADE_THE_MOVE, OTHER/*, CCY_EVENT*/
	}

	public TradingDecision(TradeableInstrument<T> instrument, TradingSignal signal) {
		this(instrument, signal, SRCDECISION.OTHER);
	}

	public TradingDecision(TradeableInstrument<T> instrument, TradingSignal signal, SRCDECISION tradeSource) {
		this(instrument, signal, 0.0, SRCDECISION.OTHER);
	}

	public TradingDecision(TradeableInstrument<T> instrument, TradingSignal signal, double takeProfitPrice) {
		this(instrument, signal, takeProfitPrice, SRCDECISION.OTHER);
	}

	public TradingDecision(TradeableInstrument<T> instrument, TradingSignal signal, double takeProfitPrice,
			SRCDECISION tradeSource) {
		this(instrument, signal, takeProfitPrice, 0.0, tradeSource);
	}

	public TradingDecision(TradeableInstrument<T> instrument, TradingSignal signal, double takeProfitPrice,
			double stopLossPrice) {
		this(instrument, signal, takeProfitPrice, stopLossPrice, SRCDECISION.OTHER);
	}

	public TradingDecision(TradeableInstrument<T> instrument, TradingSignal signal, double takeProfitPrice,
			double stopLossPrice, SRCDECISION tradeSource) {
		this(instrument, signal, takeProfitPrice, stopLossPrice, 0.0, tradeSource);
	}

	public TradingDecision(TradeableInstrument<T> instrument, TradingSignal signal, double takeProfitPrice,
			double stopLossPrice, double limitPrice) {
		this(instrument, signal, takeProfitPrice, stopLossPrice, limitPrice, SRCDECISION.OTHER);
	}

	public TradingDecision(TradeableInstrument<T> instrument, TradingSignal signal, double takeProfitPrice,
			double stopLossPrice, double limitPrice, SRCDECISION tradeSource) {
		this.signal = signal;
		this.instrument = instrument;
		this.limitPrice = limitPrice;
		this.tradeSource = tradeSource;
		this.takeProfitPrice = takeProfitPrice;
		this.stopLossPrice = stopLossPrice;
	}

	public SRCDECISION getTradeSource() {
		return tradeSource;
	}

	public double getLimitPrice() {
		return this.limitPrice;
	}

	public double getTakeProfitPrice() {
		return this.takeProfitPrice;
	}

	public double getStopLossPrice() {
		return this.stopLossPrice;
	}

	public TradeableInstrument<T> getInstrument() {
		return instrument;
	}

	public TradingSignal getSignal() {
		return signal;
	}
}
