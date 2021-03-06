package com.noveria.fxtrading.oanda.restapi.streaming.events;

import java.io.BufferedReader;
import java.util.Collection;

import static com.noveria.fxtrading.oanda.restapi.OandaJsonKeys.heartbeat;
import static com.noveria.fxtrading.oanda.restapi.OandaJsonKeys.transaction;
import static com.noveria.fxtrading.oanda.restapi.OandaJsonKeys.type;

import com.noveria.fxtrading.TradingConstants;
import com.noveria.fxtrading.account.Account;
import com.noveria.fxtrading.account.AccountDataProvider;
import com.noveria.fxtrading.events.EventCallback;
import com.noveria.fxtrading.events.EventPayLoad;
import com.noveria.fxtrading.heartbeats.HeartBeatCallback;
import com.noveria.fxtrading.oanda.restapi.OandaConstants;
import com.noveria.fxtrading.oanda.restapi.OandaJsonKeys;
import com.noveria.fxtrading.oanda.restapi.streaming.OandaStreamingService;
import com.noveria.fxtrading.oanda.restapi.utils.OandaUtils;
import com.noveria.fxtrading.streaming.events.EventsStreamingService;
import com.noveria.fxtrading.utils.TradingUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

public class OandaEventsStreamingService extends OandaStreamingService implements EventsStreamingService {

	private static final Logger LOG = Logger.getLogger(OandaEventsStreamingService.class);
	private final String url;
	private final AccountDataProvider<Long> accountDataProvider;
	private final EventCallback<JSONObject> eventCallback;

	public OandaEventsStreamingService(final String url, final String accessToken,
			AccountDataProvider<Long> accountDataProvider, EventCallback<JSONObject> eventCallback,
			HeartBeatCallback<DateTime> heartBeatCallback, String heartBeatSourceId) {
		super(accessToken, heartBeatCallback, heartBeatSourceId);
		this.url = url;
		this.accountDataProvider = accountDataProvider;
		this.eventCallback = eventCallback;
	}

	@Override
	public void stopEventsStreaming() {
		this.serviceUp = false;
		if (streamThread != null && streamThread.isAlive()) {
			streamThread.interrupt();
		}
	}

	private String accountsAsCsvString(Collection<Account<Long>> accounts) {
		StringBuilder accountsAsCsv = new StringBuilder();
		boolean firstTime = true;
		for (Account<Long> account : accounts) {
			if (firstTime) {
				firstTime = false;
			} else {
				accountsAsCsv.append(TradingConstants.ENCODED_COMMA);
			}
			accountsAsCsv.append(account.getAccountId());
		}
		return accountsAsCsv.toString();
	}

	@Override
	protected String getStreamingUrl() {
		Collection<Account<Long>> accounts = accountDataProvider.getLatestAccountInfo();
		return this.url + OandaConstants.EVENTS_RESOURCE + "?accountIds=" + accountsAsCsvString(accounts);
	}

	@Override
	public void startEventsStreaming() {
		stopEventsStreaming();
		streamThread = new Thread(new Runnable() {

			@Override
			public void run() {
				CloseableHttpClient httpClient = getHttpClient();
				try {
					BufferedReader br = setUpStreamIfPossible(httpClient);
					if (br != null) {
						String line;
						while ((line = br.readLine()) != null && serviceUp) {
							Object obj = JSONValue.parse(line);
							JSONObject jsonPayLoad = (JSONObject) obj;
							if (jsonPayLoad.containsKey(heartbeat)) {
								handleHeartBeat(jsonPayLoad);
							} else if (jsonPayLoad.containsKey(transaction)) {
								JSONObject transactionObject = (JSONObject) jsonPayLoad.get(transaction);
								String transactionType = transactionObject.get(type).toString();
								/*convert here so that event bus can post to an appropriate handler, 
								 * event though this does not belong here*/
								EventPayLoad<JSONObject> payLoad = OandaUtils.toOandaEventPayLoad(transactionType,
										transactionObject);
								if (payLoad != null) {
									eventCallback.onEvent(payLoad);
								}
							} else if (jsonPayLoad.containsKey(OandaJsonKeys.disconnect)) {
								handleDisconnect(line);
							}
						}
						br.close();
					}

				} catch (Exception e) {
					LOG.error("error encountered inside event streaming thread", e);
				} finally {
					serviceUp = false;
					TradingUtils.closeSilently(httpClient);
				}

			}
		}, "OandEventStreamingThread");
		streamThread.start();
	}

	@Override
	protected void startStreaming() {
		this.startEventsStreaming();
	}

	@Override
	protected void stopStreaming() {
		this.stopEventsStreaming();
	}

}
