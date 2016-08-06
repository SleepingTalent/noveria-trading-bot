package com.noveria.fxtrading.oanda.restapi.events;

import com.noveria.fxtrading.events.Event;

public enum AccountEvents implements Event {
	MARGIN_CALL_ENTER, MARGIN_CALL_EXIT, MARGIN_CLOSEOUT, SET_MARGIN_RATE, TRANSFER_FUNDS, DAILY_INTEREST, FEE;
}
