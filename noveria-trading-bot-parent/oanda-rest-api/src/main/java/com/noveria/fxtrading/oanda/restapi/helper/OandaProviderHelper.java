package com.noveria.fxtrading.oanda.restapi.helper;

import com.noveria.fxtrading.TradingConstants;
import com.noveria.fxtrading.helper.ProviderHelper;
import com.noveria.fxtrading.oanda.restapi.OandaConstants;
import com.noveria.fxtrading.oanda.restapi.utils.OandaUtils;
import com.noveria.fxtrading.utils.TradingUtils;

public class OandaProviderHelper implements ProviderHelper<String> {

	public String fromIsoFormat(String instrument) {
		return OandaUtils.isoCcyToOandaCcy(instrument);
	}

	public String fromPairSeparatorFormat(String instrument) {
		String[] pair = TradingUtils.splitInstrumentPair(instrument);
		return String.format("%s%s%s", pair[0], OandaConstants.CCY_PAIR_SEP, pair[1]);
	}

	public String toIsoFormat(String instrument) {
		String tokens[] = TradingUtils.splitCcyPair(instrument, TradingConstants.CURRENCY_PAIR_SEP_UNDERSCORE);
		String isoInstrument = tokens[0] + tokens[1];
		return isoInstrument;
	}

	public String fromHashTagCurrency(String instrument) {
		return OandaUtils.hashTagCcyToOandaCcy(instrument);
	}

	public String getLongNotation() {
		return OandaConstants.BUY;
	}

	public String getShortNotation() {
		return OandaConstants.SELL;
	}

}
