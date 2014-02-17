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
package com.xeiam.xchange.mtgox.v1.service.streaming.marketdata;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xeiam.xchange.ExchangeException;
import com.xeiam.xchange.dto.marketdata.OrderBookUpdate;
import com.xeiam.xchange.dto.marketdata.Ticker;
import com.xeiam.xchange.dto.marketdata.Trade;
import com.xeiam.xchange.mtgox.v1.MtGoxAdapters;
import com.xeiam.xchange.mtgox.v1.dto.marketdata.MtGoxDepthUpdate;
import com.xeiam.xchange.mtgox.v1.dto.marketdata.MtGoxTicker;
import com.xeiam.xchange.mtgox.v1.dto.marketdata.MtGoxTrade;
import com.xeiam.xchange.service.streaming.DefaultExchangeEvent;
import com.xeiam.xchange.service.streaming.ExchangeEvent;
import com.xeiam.xchange.service.streaming.ExchangeEventListener;
import com.xeiam.xchange.service.streaming.ExchangeEventType;

/**
 * @author timmolter
 * @deprecated Use V2! This will be removed in 1.8.0+
 */
@Deprecated
public class MtGoxExchangeEventListener extends ExchangeEventListener {

  private static final Logger log = LoggerFactory.getLogger(MtGoxExchangeEventListener.class);

  private final ObjectMapper streamObjectMapper;

  private final BlockingQueue<ExchangeEvent> consumerEventQueue;

  /**
   * Constructor
   * 
   * @param consumerEventQueue
   */
  public MtGoxExchangeEventListener(BlockingQueue<ExchangeEvent> consumerEventQueue) {

    this.consumerEventQueue = consumerEventQueue;
    streamObjectMapper = new ObjectMapper();
    streamObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  }

  @Override
  public void handleEvent(ExchangeEvent exchangeEvent) {

    // log.debug("exchangeEvent: " + exchangeEvent.getEventType());

    switch (exchangeEvent.getEventType()) {
    case CONNECT:
      log.debug("MtGox connected");
      addToEventQueue(exchangeEvent);
      break;
    case DISCONNECT:
      log.debug("MtGox disconnected");
      addToEventQueue(exchangeEvent);
      break;
    case MESSAGE:
      try {
        // Get raw JSON
        // Map<String, Object> rawJSON = JSONUtils.getJsonGenericMap(exchangeEvent.getData(), streamObjectMapper);
        Map<String, Object> rawJSON;
        rawJSON = streamObjectMapper.readValue(exchangeEvent.getData(), new TypeReference<Map<String, Object>>() {
        });

        String operation = (String) rawJSON.get("op");
        if ("private".equals(operation)) {

        }
        else if ("result".equals(operation)) {

          String id = (String) rawJSON.get("id");

          if ("idkey".equals(id)) {
            ExchangeEvent idEvent = new DefaultExchangeEvent(ExchangeEventType.PRIVATE_ID_KEY, null, rawJSON.get("result"));
            addToEventQueue(idEvent);
            break;
          }

          else if (id.startsWith("order_add")) {
            ExchangeEvent userOrderAddedEvent = new DefaultExchangeEvent(ExchangeEventType.USER_ORDER_ADDED, exchangeEvent.getData(), rawJSON.get("result"));
            addToEventQueue(userOrderAddedEvent);
            break;

          }
          else if (id.startsWith("order_cancel")) {

            break;
          }

        }
        else if ("remark".equals(operation)) {
          System.out.println("Msg from server: " + rawJSON.toString());
          break;
        }

        // Determine what has been sent
        if (rawJSON.containsKey("ticker")) {

          // Get MtGoxTicker from JSON String
          // MtGoxTicker mtGoxTicker = JSONUtils.getJsonObject(JSONUtils.getJSONString(rawJSON.get("ticker"), streamObjectMapper), MtGoxTicker.class, streamObjectMapper);
          MtGoxTicker mtGoxTicker = streamObjectMapper.readValue(streamObjectMapper.writeValueAsString(rawJSON.get("ticker")), MtGoxTicker.class);

          // Adapt to XChange DTOs
          Ticker ticker = MtGoxAdapters.adaptTicker(mtGoxTicker);

          // Create a ticker event
          ExchangeEvent tickerEvent = new DefaultExchangeEvent(ExchangeEventType.TICKER, exchangeEvent.getData(), ticker);
          addToEventQueue(tickerEvent);
          break;
        }
        else {
          if (rawJSON.containsKey("trade")) {

            // log.debug("exchangeEvent: " + exchangeEvent.getEventType());

            // Get MtGoxTradeStream from JSON String
            // MtGoxTrade mtGoxTradeStream = JSONUtils.getJsonObject(JSONUtils.getJSONString(rawJSON.get("trade"), streamObjectMapper), MtGoxTrade.class, streamObjectMapper);
            MtGoxTrade mtGoxTradeStream = streamObjectMapper.readValue(streamObjectMapper.writeValueAsString(rawJSON.get("trade")), MtGoxTrade.class);

            // Adapt to XChange DTOs
            Trade trade = MtGoxAdapters.adaptTrade(mtGoxTradeStream);

            // Create a trade event
            ExchangeEvent tradeEvent = new DefaultExchangeEvent(ExchangeEventType.TRADE, exchangeEvent.getData(), trade);
            addToEventQueue(tradeEvent);
            break;
          }
          else {
            if (rawJSON.containsKey("depth")) {

              // Get MtGoxDepthStream from JSON String
              // MtGoxDepthUpdate mtGoxDepthStream = JSONUtils.getJsonObject(JSONUtils.getJSONString(rawJSON.get("depth"), streamObjectMapper), MtGoxDepthUpdate.class, streamObjectMapper);
              MtGoxDepthUpdate mtGoxDepthStream = streamObjectMapper.readValue(streamObjectMapper.writeValueAsString(rawJSON.get("depth")), MtGoxDepthUpdate.class);

              // Adapt to XChange DTOs
              OrderBookUpdate orderBookUpdate = MtGoxAdapters.adaptDepthUpdate(mtGoxDepthStream);

              // Create a depth event
              ExchangeEvent depthEvent = new DefaultExchangeEvent(ExchangeEventType.DEPTH, exchangeEvent.getData(), orderBookUpdate);
              addToEventQueue(depthEvent);
              break;
            }
            else {

              log.debug("MtGox operational message");
              System.out.println("msg: " + rawJSON.toString());
              addToEventQueue(exchangeEvent);
            }
          }
        }
      } catch (JsonParseException e) {
        log.error("Error parsing returned JSON", e);
      } catch (JsonMappingException e) {
        log.error("Error parsing returned JSON", e);
      } catch (IOException e) {
        log.error("Error parsing returned JSON", e);
      }
      break;
    case ERROR:
      log.error("Error message: " + exchangeEvent.getPayload());
      addToEventQueue(exchangeEvent);
      break;
    default:
      throw new IllegalStateException("Unknown ExchangeEventType " + exchangeEvent.getEventType().name());
    }

  }

  private void addToEventQueue(ExchangeEvent event) {

    try {
      consumerEventQueue.put(event);
    } catch (InterruptedException e) {
      throw new ExchangeException("InterruptedException!", e);
    }
  }

}
