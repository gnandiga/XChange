package com.xeiam.xchange.poloniex.service.polling;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

import com.xeiam.xchange.ExchangeException;
import com.xeiam.xchange.ExchangeSpecification;
import com.xeiam.xchange.NotAvailableFromExchangeException;
import com.xeiam.xchange.NotYetImplementedForExchangeException;
import com.xeiam.xchange.dto.account.AccountInfo;
import com.xeiam.xchange.dto.trade.Wallet;
import com.xeiam.xchange.service.polling.PollingAccountService;

/**
 * @author Zach Holmes
 */

public class PoloniexAccountService extends PoloniexAccountServiceRaw implements PollingAccountService {

  public PoloniexAccountService(ExchangeSpecification exchangeSpecification) {

    super(exchangeSpecification);
  }

  @Override
  public AccountInfo getAccountInfo() throws ExchangeException, NotAvailableFromExchangeException, NotYetImplementedForExchangeException, IOException {

    List<Wallet> wallets = getWallets();
    return new AccountInfo(null, wallets);
  }

  @Override
  public String withdrawFunds(String currency, BigDecimal amount, String address) throws ExchangeException, NotAvailableFromExchangeException, NotYetImplementedForExchangeException, IOException {

    throw new NotYetImplementedForExchangeException();
  }

  @Override
  public String requestDepositAddress(String currency, String... args) throws ExchangeException, NotAvailableFromExchangeException, NotYetImplementedForExchangeException, IOException {

    return getDepositAddress(currency);
  }

}
