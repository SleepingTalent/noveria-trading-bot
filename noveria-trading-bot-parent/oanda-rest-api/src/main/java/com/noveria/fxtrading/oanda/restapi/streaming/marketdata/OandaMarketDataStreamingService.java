package com.noveria.fxtrading.oanda.restapi.streaming.marketdata;

import static com.noveria.fxtrading.oanda.restapi.OandaJsonKeys.ask;
import static com.noveria.fxtrading.oanda.restapi.OandaJsonKeys.bid;
import static com.noveria.fxtrading.oanda.restapi.OandaJsonKeys.disconnect;
import static com.noveria.fxtrading.oanda.restapi.OandaJsonKeys.heartbeat;
import static com.noveria.fxtrading.oanda.restapi.OandaJsonKeys.tick;
import static com.noveria.fxtrading.oanda.restapi.OandaJsonKeys.time;

import java.io.BufferedReader;
import java.util.Collection;

import com.noveria.fxtrading.TradingConstants;
import com.noveria.fxtrading.heartbeats.HeartBeatCallback;
import com.noveria.fxtrading.instrument.TradeableInstrument;
import com.noveria.fxtrading.marketdata.MarketEventCallback;
import com.noveria.fxtrading.oanda.restapi.OandaConstants;
import com.noveria.fxtrading.oanda.restapi.OandaJsonKeys;
import com.noveria.fxtrading.oanda.restapi.streaming.OandaStreamingService;
import com.noveria.fxtrading.streaming.marketdata.MarketDataStreamingService;
import com.noveria.fxtrading.utils.TradingUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class OandaMarketDataStreamingService extends OandaStreamingService implements MarketDataStreamingService {

	private static final Logger LOG = Logger.getLogger(OandaMarketDataStreamingService.class);
	private final String url;
	private final MarketEventCallback<String> marketEventCallback;

	public OandaMarketDataStreamingService(String url, String accessToken, long accountId,
			Collection<TradeableInstrument<String>> instruments, MarketEventCallback<String> marketEventCallback,
			HeartBeatCallback<DateTime> heartBeatCallback, String heartbeatSourceId) {
		super(accessToken, heartBeatCallback, heartbeatSourceId);
		this.url = url + OandaConstants.PRICES_RESOURCE + "?accountId=" + accountId + "&instruments="
				+ instrumentsAsCsv(instruments);
		this.marketEventCallback = marketEventCallback;
	}

	private String instrumentsAsCsv(Collection<TradeableInstrument<String>> instruments) {
		StringBuilder csvLst = new StringBuilder();
		boolean firstTime = true;
		for (TradeableInstrument<String> instrument : instruments) {
			if (firstTime) {
				firstTime = false;
			} else {
				csvLst.append(TradingConstants.ENCODED_COMMA);
			}
			csvLst.append(instrument.getInstrument());
		}
		return csvLst.toString();
	}

	@Override
	protected String getStreamingUrl() {
		return this.url;
	}

	@Override
	public void stopMarketDataStreaming() {
		this.serviceUp = false;
		if (streamThread != null && streamThread.isAlive()) {
			streamThread.interrupt();
		}
	}

	@Override
	public void startMarketDataStreaming() {
		stopMarketDataStreaming();
		this.streamThread = new Thread(new Runnable() {

			@Override
			public void run() {
				CloseableHttpClient httpClient = getHttpClient();
				try {
					BufferedReader br = setUpStreamIfPossible(httpClient);
					if (br != null) {
						String line;
						while ((line = br.readLine()) != null && serviceUp) {
							Object obj = JSONValue.parse(line);
							JSONObject instrumentTick = (JSONObject) obj;
							// unwrap if necessary
							if (instrumentTick.containsKey(tick)) {
								instrumentTick = (JSONObject) instrumentTick.get(tick);
							}

							if (instrumentTick.containsKey(OandaJsonKeys.instrument)) {
								final String instrument = instrumentTick.get(OandaJsonKeys.instrument).toString();
								final String timeAsString = instrumentTick.get(time).toString();
								final long eventTime = Long.parseLong(timeAsString);
								final double bidPrice = ((Number) instrumentTick.get(bid)).doubleValue();
								final double askPrice = ((Number) instrumentTick.get(ask)).doubleValue();
								marketEventCallback.onMarketEvent(new TradeableInstrument<String>(instrument),
										bidPrice, askPrice, new DateTime(TradingUtils.toMillisFromNanos(eventTime)));
							} else if (instrumentTick.containsKey(heartbeat)) {
								handleHeartBeat(instrumentTick);
							} else if (instrumentTick.containsKey(disconnect)) {
								handleDisconnect(line);
							}
						}
						br.close();
						// stream.close();
					}
				} catch (Exception e) {
					LOG.error("error encountered inside market data streaming thread", e);
				} finally {
					serviceUp = false;
					TradingUtils.closeSilently(httpClient);
				}

			}
		}, "OandMarketDataStreamingThread");
		this.streamThread.start();

	}

	@Override
	protected void startStreaming() {
		startMarketDataStreaming();

	}

	@Override
	protected void stopStreaming() {
		stopMarketDataStreaming();

	}

}
