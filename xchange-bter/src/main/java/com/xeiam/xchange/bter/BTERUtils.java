/**
 * Copyright (C) 2012 - 2014 Xeiam LLC http://xeiam.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.xeiam.xchange.bter;


import com.xeiam.xchange.ExchangeException;
import com.xeiam.xchange.bter.service.BTERBaseService;
import com.xeiam.xchange.currency.CurrencyPair;

/**
 * A central place for shared Bter properties
 */
public final class BTERUtils {

  /**
   * private Constructor
   */
  private BTERUtils() {

  }

  public static CurrencyPair parseCurrencyPairString(String currencyPairString) {

    String baseCurrency = currencyPairString.split("_")[0];
    String counterCurrency = currencyPairString.split("_")[1];

    CurrencyPair currencyPair = new CurrencyPair(baseCurrency.toUpperCase(), counterCurrency.toUpperCase());

    if (BTERBaseService.CURRENCY_PAIRS.contains(currencyPair)) {
      return currencyPair;
    } else {
      throw new ExchangeException("No support for currency pair: " + currencyPair);
    }
  }

}
