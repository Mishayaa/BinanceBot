package com.example.Crypto.api;

import com.binance.api.client.BinanceApiClientFactory;
import com.binance.api.client.BinanceApiRestClient;
import com.binance.api.client.domain.market.TickerPrice;
import org.springframework.stereotype.Component;


@Component
public class BinanceApi {

    private BinanceApiRestClient binanceClient;
    private String apiKey;
    private String secretKey;

    public BinanceApi() {
        this.binanceClient = null;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;

    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance(apiKey, secretKey);
        binanceClient = factory.newRestClient();
    }


    public TickerPrice getBtcUsdtPrice() {
        return binanceClient.getPrice("BTCUSDT");
    }

    public TickerPrice getXrpUsdtPrice() {
        return binanceClient.getPrice("XRPUSDT");
    }

    public TickerPrice getEthUsdtPrice() {
        return binanceClient.getPrice("ETHUSDT");
    }

    public String getBalance() {
        String balanceMessage = "Your balances:\n\n";
        for (var balance : binanceClient.getAccount(5000L, System.currentTimeMillis()).getBalances()) {
            if (Double.parseDouble(balance.getFree()) > 0 || Double.parseDouble(balance.getLocked()) > 0) {
                balanceMessage += balance.getAsset() + ": " + balance.getFree() + " (free), " + balance.getLocked() + " (locked)\n";
            }
        }
        return balanceMessage;


    }


    public TickerPrice getSymbolPriceTicker(String symbol) {
        return binanceClient.getPrice(symbol);
    }


}
