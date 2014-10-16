package com.xeiam.xchange.anx.v2.service.polling;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import si.mazi.rescu.RestProxyFactory;

import com.xeiam.xchange.ExchangeException;
import com.xeiam.xchange.ExchangeSpecification;
import com.xeiam.xchange.anx.v2.ANXV2;
import com.xeiam.xchange.anx.v2.dto.ANXException;
import com.xeiam.xchange.anx.v2.dto.marketdata.ANXDepth;
import com.xeiam.xchange.anx.v2.dto.marketdata.ANXDepthWrapper;
import com.xeiam.xchange.anx.v2.dto.marketdata.ANXDepthsWrapper;
import com.xeiam.xchange.anx.v2.dto.marketdata.ANXTicker;
import com.xeiam.xchange.anx.v2.dto.marketdata.ANXTickerWrapper;
import com.xeiam.xchange.anx.v2.dto.marketdata.ANXTickersWrapper;
import com.xeiam.xchange.anx.v2.dto.marketdata.ANXTrade;
import com.xeiam.xchange.currency.CurrencyPair;
import com.xeiam.xchange.utils.Assert;

public class ANXMarketDataServiceRaw extends ANXBasePollingService {

  private final ANXV2 anxV2;

  /**
   * Initialize common properties from the exchange specification
   * 
   * @param exchangeSpecification The {@link com.xeiam.xchange.ExchangeSpecification}
   */
  protected ANXMarketDataServiceRaw(ExchangeSpecification exchangeSpecification) {

    // nonce is not used in this class, pass null
    super(exchangeSpecification, null);

    Assert.notNull(exchangeSpecification.getSslUri(), "Exchange specification URI cannot be null");
    this.anxV2 = RestProxyFactory.createProxy(ANXV2.class, exchangeSpecification.getSslUri());
  }

  public ANXTicker getANXTicker(CurrencyPair currencyPair) throws IOException {

    try {
      ANXTickerWrapper anxTickerWrapper = anxV2.getTicker(currencyPair.baseSymbol, currencyPair.counterSymbol);
      return anxTickerWrapper.getAnxTicker();
    } catch (ANXException e) {
      throw new ExchangeException("Error calling getTicker(): " + e.getError(), e);
    }
  }

  public Map<String, ANXTicker> getANXTickers(Collection<CurrencyPair> currencyPairs) throws IOException {

    CurrencyPair pathCurrencyPair = null;
    StringBuilder extraCurrencyPairs = new StringBuilder();
    int i = 1;
    for (CurrencyPair currencyPair : currencyPairs) {
      if (i++ == currencyPairs.size()) {
        pathCurrencyPair = currencyPair;
      }
      else {
        extraCurrencyPairs.append(currencyPair.baseSymbol).append(currencyPair.counterSymbol).append(",");
      }
    }
    if (pathCurrencyPair == null) {
      return null;
    }
    try {
      if (i == 2) {
        ANXTicker anxTicker = getANXTicker(pathCurrencyPair);
        Map<String, ANXTicker> ticker = new HashMap<String, ANXTicker>();
        ticker.put(pathCurrencyPair.baseSymbol + pathCurrencyPair.counterSymbol, anxTicker);
        return ticker;
      }
      ANXTickersWrapper anxTickerWrapper = anxV2.getTickers(pathCurrencyPair.baseSymbol, pathCurrencyPair.counterSymbol, extraCurrencyPairs.toString());
      return anxTickerWrapper.getAnxTickers();
    } catch (ANXException e) {
      throw new ExchangeException("Error calling getTicker(): " + e.getError(), e);
    }
  }

  public ANXDepthWrapper getANXFullOrderBook(CurrencyPair currencyPair) throws IOException {

    try {
      ANXDepthWrapper anxDepthWrapper = anxV2.getFullDepth(currencyPair.baseSymbol, currencyPair.counterSymbol);
      return anxDepthWrapper;
    } catch (ANXException e) {
      throw new ExchangeException("Error calling getANXFullOrderBook(): " + e.getError(), e);
    }
  }

  public Map<String, ANXDepth> getANXFullOrderBooks(Collection<CurrencyPair> currencyPairs) throws IOException {

    CurrencyPair pathCurrencyPair = null;
    StringBuilder extraCurrencyPairs = new StringBuilder();
    int i = 1;
    for (CurrencyPair currencyPair : currencyPairs) {
      if (i++ == currencyPairs.size()) {
        pathCurrencyPair = currencyPair;
      }
      else {
        extraCurrencyPairs.append(currencyPair.baseSymbol).append(currencyPair.counterSymbol).append(",");
      }
    }
    if (pathCurrencyPair == null) {
      return null;
    }
    try {
      if (i == 2) {
        ANXDepthWrapper anxDepthWrapper = getANXFullOrderBook(pathCurrencyPair);
        Map<String, ANXDepth> book = new HashMap<String, ANXDepth>();
        book.put(pathCurrencyPair.baseSymbol + pathCurrencyPair.counterSymbol, anxDepthWrapper.getAnxDepth());
        return book;
      }
      ANXDepthsWrapper anxDepthWrapper = anxV2.getFullDepths(pathCurrencyPair.baseSymbol, pathCurrencyPair.counterSymbol, extraCurrencyPairs.toString());
      return anxDepthWrapper.getAnxDepths();
    } catch (ANXException e) {
      throw new ExchangeException("Error calling getANXFullOrderBook(): " + e.getError(), e);
    }
  }

  public ANXDepthWrapper getANXPartialOrderBook(CurrencyPair currencyPair) throws IOException {

    try {
      ANXDepthWrapper anxDepthWrapper = anxV2.getPartialDepth(currencyPair.baseSymbol, currencyPair.counterSymbol);
      return anxDepthWrapper;
    } catch (ANXException e) {
      throw new ExchangeException("Error calling getANXPartialOrderBook(): " + e.getError(), e);
    }
  }

  public List<ANXTrade> getANXTrades(CurrencyPair currencyPair, Long sinceTimeStamp) throws IOException {

    try {
      return anxV2.getTrades(currencyPair.baseSymbol, currencyPair.counterSymbol, sinceTimeStamp).getANXTrades();
    } catch (ANXException e) {
      throw new ExchangeException("Error calling getTrades(): " + e.getError(), e);
    }
  }
}
