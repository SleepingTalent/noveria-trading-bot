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
package com.noveria.fxtrading.tradingbot.social.twitter.tweethandler;

import java.util.Collection;

import com.noveria.fxtrading.instrument.TradeableInstrument;
import com.noveria.fxtrading.tradingbot.social.twitter.CloseFXTradeTweet;
import com.noveria.fxtrading.tradingbot.social.twitter.NewFXTradeTweet;

public interface TweetHarvester<T> {

	Collection<NewFXTradeTweet<T>> harvestNewTradeTweets(String userId);

	Collection<CloseFXTradeTweet<T>> harvestHistoricTradeTweets(String userId, TradeableInstrument<T> instrument);
}
