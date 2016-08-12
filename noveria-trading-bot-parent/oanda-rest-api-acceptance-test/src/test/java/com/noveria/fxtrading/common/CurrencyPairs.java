package com.noveria.fxtrading.common;

import com.noveria.fxtrading.instrument.TradeableInstrument;

/**
 * Created by Noveria on 8/11/2016.
 */
public enum CurrencyPairs {

    USD_CAD(new TradeableInstrument<String>("USD_CAD")),
    USD_JPY(new TradeableInstrument<String>("USD_JPY")),
    USD_ZAR(new TradeableInstrument<String>("USD_ZAR")),
    USB_CHF(new TradeableInstrument<String>("USD_CHF")),
    EUR_GBP(new TradeableInstrument<String>("EUR_GBP")),
    EUR_USD(new TradeableInstrument<String>("EUR_USD")),
    EUR_AUD(new TradeableInstrument<String>("EUR_AUD")),
    EUR_NZD(new TradeableInstrument<String>("EUR_NZD")),
    GBP_USD(new TradeableInstrument<String>("GBP_USD")),
    GBP_CHF(new TradeableInstrument<String>("GBP_CHF")),
    GBP_AUD(new TradeableInstrument<String>("GBP_AUD")),
    GBP_NZD(new TradeableInstrument<String>("GBP_NZD")),
    CAD_CHF(new TradeableInstrument<String>("CAD_CHF")),
    NZD_CHF(new TradeableInstrument<String>("NZD_CHF"));

    private final TradeableInstrument tradableInstrument;

    CurrencyPairs(TradeableInstrument tradeableInstrument) {
        this.tradableInstrument = tradeableInstrument;
    }

    public TradeableInstrument getTradableInstrument() {
        return tradableInstrument;
    }
}
