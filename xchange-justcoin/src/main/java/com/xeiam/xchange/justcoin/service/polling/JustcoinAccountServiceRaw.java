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
package com.xeiam.xchange.justcoin.service.polling;

import java.io.IOException;
import java.math.BigDecimal;

import si.mazi.rescu.RestProxyFactory;

import com.xeiam.xchange.ExchangeSpecification;
import com.xeiam.xchange.justcoin.JustcoinAuthenticated;
import com.xeiam.xchange.justcoin.dto.account.JustcoinBalance;
import com.xeiam.xchange.justcoin.service.JustcoinBaseService;
import com.xeiam.xchange.utils.AuthUtils;

/**
 * @author jamespedwards42
 */
public class JustcoinAccountServiceRaw extends JustcoinBaseService {

  private JustcoinAuthenticated justcoinAuthenticated;

  public JustcoinAccountServiceRaw(final ExchangeSpecification exchangeSpecification) {

    super(exchangeSpecification);
    this.justcoinAuthenticated = RestProxyFactory.createProxy(JustcoinAuthenticated.class, exchangeSpecification.getSslUri());
  }

  public JustcoinBalance[] getBalances() throws IOException {

    final String username = exchangeSpecification.getUserName();
    final String basicAuth = AuthUtils.getBasicAuth(username, exchangeSpecification.getPassword());
    final JustcoinBalance[] justcoinBalances = justcoinAuthenticated.getBalances(basicAuth, exchangeSpecification.getApiKey());

    return justcoinBalances;
  }

  public String requestDepositAddress(final String currency) throws IOException {

    return justcoinAuthenticated.getDepositAddress(currency, getBasicAuthentication(), exchangeSpecification.getApiKey()).getAddress();
  }

  public String withdrawFunds(final String currency, final BigDecimal amount, final String address) throws IOException {

    return justcoinAuthenticated.withdraw(currency, address, amount, getBasicAuthentication(), exchangeSpecification.getApiKey()).getId();
  }

  private String getBasicAuthentication() {

    return AuthUtils.getBasicAuth(exchangeSpecification.getUserName(), exchangeSpecification.getPassword());
  }
}
