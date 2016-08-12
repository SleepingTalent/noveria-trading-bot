package com.noveria.fxtrading.oanda.restapi.account;

import com.noveria.fxtrading.BaseTradingConfig;
import com.noveria.fxtrading.account.Account;
import com.noveria.fxtrading.account.AccountDataProvider;
import com.noveria.fxtrading.account.AccountInfoService;
import com.noveria.fxtrading.common.BaseTest;
import com.noveria.fxtrading.common.CurrencyPairs;
import com.noveria.fxtrading.helper.ProviderHelper;
import com.noveria.fxtrading.instrument.TradeableInstrument;
import com.noveria.fxtrading.marketdata.CurrentPriceInfoProvider;
import com.noveria.fxtrading.oanda.restapi.helper.OandaProviderHelper;
import com.noveria.fxtrading.oanda.restapi.marketdata.OandaCurrentPriceInfoProvider;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AccountInfoServiceTest extends BaseTest{

    private static final Logger LOG = Logger.getLogger(AccountInfoServiceTest.class);

    AccountInfoService<Long, String> accountInfoService;

    @Before
    public void setUp() {
        AccountDataProvider<Long> accountDataProvider = new OandaAccountDataProviderService(url, accessToken);
        CurrentPriceInfoProvider<String> currentPriceInfoProvider = new OandaCurrentPriceInfoProvider(url, accessToken);
        BaseTradingConfig tradingConfig = new BaseTradingConfig();
        tradingConfig.setMinReserveRatio(0.05);
        tradingConfig.setMinAmountRequired(100.00);
        ProviderHelper<String> providerHelper = new OandaProviderHelper();

        accountInfoService = new AccountInfoService<Long, String>(accountDataProvider,
                currentPriceInfoProvider, tradingConfig, providerHelper);
    }

    @Test
        public void getAllAccounts_returns_expectAccountData() {
        List<Account<Long>> accounts = (List<Account<Long>>) accountInfoService.getAllAccounts();

        assertEquals("Expected Only 1 Account", 1, accounts.size());
        LOG.info(String.format("Found %d accounts to trade", accounts.size()));

        Account<Long> sampleAccount = accounts.get(0);
        LOG.info(sampleAccount);

        assertEquals(accountId, sampleAccount.getAccountId());
    }

    @Test
    public void getAccountInfo_returns_expectAccountData() {
        Account<Long> sampleAccount = accountInfoService.getAccountInfo(accountId);
        assertEquals(new Long(9813375), sampleAccount.getAccountId());
    }

    @Test
    public void calculateMarginForTrade_returns_marginRequirement_for_GBP_USD() {
        Account<Long> sampleAccount = accountInfoService.getAccountInfo(accountId);
        assertEquals(new Long(9813375), sampleAccount.getAccountId());

        final int units = 5000;

        double marginRequired = accountInfoService.calculateMarginForTrade(sampleAccount, CurrencyPairs.GBP_USD.getTradableInstrument(), units);

        //assertEquals(500,marginRequired,0);

        LOG.info(String.format("Marging requirement for trading pair %d units of %s is %5.2f %s ", units, CurrencyPairs.GBP_USD.getTradableInstrument()
                .getInstrument(), marginRequired, sampleAccount.getCurrency()));
    }

    @Test
    public void calculateMarginForTrade_returns_marginRequirement_for_EUR_GBP() {
        Account<Long> sampleAccount = accountInfoService.getAccountInfo(accountId);
        assertEquals(new Long(9813375), sampleAccount.getAccountId());

        final int units = 5000;

        double marginRequired = accountInfoService.calculateMarginForTrade(sampleAccount, CurrencyPairs.EUR_GBP.getTradableInstrument(), units);

        //assertEquals(428,marginRequired,0);

        LOG.info(String.format("Marging requirement for trading pair %d units of %s is %5.2f %s ", units, CurrencyPairs.EUR_GBP.getTradableInstrument()
                .getInstrument(), marginRequired, sampleAccount.getCurrency()));
    }

}
