package com.example.Crypto.logic;

import com.binance.api.client.domain.market.TickerPrice;

import com.example.Crypto.api.BinanceApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
public class CryptoPrice {
    @Autowired
    BinanceApi binanceApi;


    public String reportCurrentCryptoPrice(Message message) {
        String symbol = message.getText().toUpperCase() + "USDT";
        TickerPrice cryptoPrice = binanceApi.getSymbolPriceTicker(symbol);
        String replyMessage;
        if (cryptoPrice == null) {
            replyMessage = "Не удалось получить цену для указанной криптовалюты.";
        } else {
            replyMessage = String.format("%s price: %s", symbol, cryptoPrice.getPrice());
        }

        return replyMessage;
    }

    public String getBalance() {
        return binanceApi.getBalance();
    }

    public void registerApiKey(String apiKey) {
        binanceApi.setApiKey(apiKey);
    }

    public void registerSecretKey(String secret) {
        binanceApi.setSecretKey(secret);
    }

    public String reportCurrentBtcUsdtPrice() {
        TickerPrice btcUsdtPrice = binanceApi.getBtcUsdtPrice();
        TickerPrice ethUsdtPrice = binanceApi.getEthUsdtPrice();
        TickerPrice xrpUsdtPrice = binanceApi.getXrpUsdtPrice();
        String message = String.format("BTCUSDT price: %s \n" +
                        "ETHUSDT price: %s \n" +
                        "XRPUSDT price: %s", btcUsdtPrice.getPrice(), ethUsdtPrice.getPrice(), xrpUsdtPrice.getPrice());
        return message;
    }
}
