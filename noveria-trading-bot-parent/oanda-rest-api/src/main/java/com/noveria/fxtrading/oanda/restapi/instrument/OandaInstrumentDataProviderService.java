/*
 *  Copyright 2015 Shekhar Varshney
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.noveria.fxtrading.oanda.restapi.instrument;

import static com.noveria.fxtrading.oanda.restapi.OandaJsonKeys.*;

import com.google.common.collect.Lists;
import com.noveria.fxtrading.instrument.InstrumentDataProvider;
import com.noveria.fxtrading.instrument.InstrumentPairInterestRate;
import com.noveria.fxtrading.instrument.TradeableInstrument;
import com.noveria.fxtrading.oanda.restapi.OandaConstants;
import com.noveria.fxtrading.oanda.restapi.OandaJsonKeys;
import com.noveria.fxtrading.oanda.restapi.utils.OandaUtils;
import com.noveria.fxtrading.utils.TradingUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.util.Collection;


public class OandaInstrumentDataProviderService implements InstrumentDataProvider<String> {

	private static final Logger LOG = Logger.getLogger(OandaInstrumentDataProviderService.class);

	private final String url;
	private final long accountId;
	private final BasicHeader authHeader;
	static final String fieldsRequested = "instrument%2Cpip%2CinterestRate";

	public OandaInstrumentDataProviderService(String url, long accountId, String accessToken) {
		this.url = url; // OANDA REST service base url
		this.accountId = accountId;// OANDA valid account id
		this.authHeader = OandaUtils.createAuthHeader(accessToken);
	}

	CloseableHttpClient getHttpClient() {
		return HttpClientBuilder.create().build();
	}

	String getInstrumentsUrl() {
		return url + OandaConstants.INSTRUMENTS_RESOURCE + "?accountId=" + accountId + "&fields=" + fieldsRequested;
	}

	public Collection<TradeableInstrument<String>> getInstruments() {
		Collection<TradeableInstrument<String>> instrumentsList = Lists.newArrayList();
		CloseableHttpClient httpClient = getHttpClient();
		try {
			HttpUriRequest httpGet = new HttpGet(getInstrumentsUrl());
			httpGet.setHeader(authHeader);
			LOG.info(TradingUtils.executingRequestMsg(httpGet));
			HttpResponse resp = httpClient.execute(httpGet);
			String strResp = TradingUtils.responseToString(resp);
			if (strResp != StringUtils.EMPTY) {
				Object obj = JSONValue.parse(strResp);
				JSONObject jsonResp = (JSONObject) obj;
				JSONArray instrumentArray = (JSONArray) jsonResp.get(instruments);

				for (Object o : instrumentArray) {
					JSONObject instrumentJson = (JSONObject) o;
					String instrument = (String) instrumentJson.get(OandaJsonKeys.instrument);
					String[] currencies = OandaUtils.splitCcyPair(instrument);
					Double pip = Double.parseDouble(instrumentJson.get(OandaJsonKeys.pip).toString());
					JSONObject interestRates = (JSONObject) instrumentJson.get(interestRate);
					if (interestRates.size() != 2) {
						throw new IllegalArgumentException();
					}

					JSONObject currency1Json = (JSONObject) interestRates.get(currencies[0]);
					JSONObject currency2Json = (JSONObject) interestRates.get(currencies[1]);

					final double baseCurrencyBidInterestRate = ((Number) currency1Json.get(bid)).doubleValue();
					final double baseCurrencyAskInterestRate = ((Number) currency1Json.get(ask)).doubleValue();
					final double quoteCurrencyBidInterestRate = ((Number) currency2Json.get(bid)).doubleValue();
					final double quoteCurrencyAskInterestRate = ((Number) currency2Json.get(ask)).doubleValue();

					InstrumentPairInterestRate instrumentPairInterestRate = new InstrumentPairInterestRate(
							baseCurrencyBidInterestRate, baseCurrencyAskInterestRate, quoteCurrencyBidInterestRate,
							quoteCurrencyAskInterestRate);
					TradeableInstrument<String> tradeableInstrument = new TradeableInstrument<String>(instrument, pip,
							instrumentPairInterestRate, null);
					instrumentsList.add(tradeableInstrument);
				}
			} else {
				TradingUtils.printErrorMsg(resp);
			}
		} catch (Exception e) {
			LOG.error("exception encountered whilst retrieving all instruments info", e);
		} finally {
			TradingUtils.closeSilently(httpClient);
		}
		return instrumentsList;
	}

}
