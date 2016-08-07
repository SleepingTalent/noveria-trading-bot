package com.noveria.fxtrading.position;

import com.noveria.fxtrading.TradingSignal;
import com.noveria.fxtrading.instrument.TradeableInstrument;

public class Position<T> {

	private final TradeableInstrument<T> instrument;
	private final long units;
	private final TradingSignal side;
	private final double averagePrice;

	public Position(TradeableInstrument<T> instrument, long units, TradingSignal side, double averagePrice) {
		this.instrument = instrument;
		this.units = units;
		this.side = side;
		this.averagePrice = averagePrice;
	}

	public TradeableInstrument<T> getInstrument() {
		return instrument;
	}

	public long getUnits() {
		return units;
	}

	public TradingSignal getSide() {
		return side;
	}

	public double getAveragePrice() {
		return averagePrice;
	}
}
