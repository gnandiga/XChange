package com.xeiam.xchange.bitvc.service.polling;

import static com.xeiam.xchange.dto.Order.OrderType.BID;

import java.io.IOException;
import java.math.BigDecimal;

import com.xeiam.xchange.ExchangeSpecification;
import com.xeiam.xchange.bitvc.dto.trade.BitVcCancelOrderResult;
import com.xeiam.xchange.bitvc.dto.trade.BitVcOrder;
import com.xeiam.xchange.bitvc.dto.trade.BitVcOrderResult;
import com.xeiam.xchange.bitvc.dto.trade.BitVcPlaceOrderResult;
import com.xeiam.xchange.dto.Order.OrderType;

public class BitVcTradeServiceRaw extends BitVcBaseTradeService {

  protected BitVcTradeServiceRaw(ExchangeSpecification exchangeSpecification) {

    super(exchangeSpecification);
  }

  public BitVcOrder[] getBitVcOrders(int coinType) throws IOException {

    BitVcOrderResult orders = bitvc.getOrders(accessKey, coinType, nextCreated(), digest);

    return orders.getOrders();
  }

  public BitVcOrder getBitVcOrder(int coinType, long id) throws IOException {

    return bitvc.getOrder(accessKey, coinType, nextCreated(), digest, id);
  }

  public BitVcPlaceOrderResult placeBitVcLimitOrder(OrderType type, int coinType, BigDecimal price, BigDecimal amount) throws IOException {

    final String method = type == BID ? "buy" : "sell";

    final BitVcPlaceOrderResult result;

    result = bitvc.placeLimitOrder(accessKey, amount.toPlainString(), coinType, nextCreated(), price.toPlainString(), digest, method);
    return result;
  }

  public BitVcPlaceOrderResult placeBitVcMarketOrder(OrderType type, int coinType, BigDecimal amount) throws IOException {

    final String method = type == BID ? "buy_market" : "sell_market";

    final BitVcPlaceOrderResult result;

    result = bitvc.placeMarketOrder(accessKey, amount.toPlainString(), coinType, nextCreated(), digest, method);

    return result;
  }

  public BitVcCancelOrderResult cancelBitVcOrder(int coinType, long id) throws IOException {

    return bitvc.cancelOrder(accessKey, coinType, nextCreated(), id, digest, id);
  }

}
