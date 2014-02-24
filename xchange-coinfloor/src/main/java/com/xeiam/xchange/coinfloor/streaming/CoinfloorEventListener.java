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
package com.xeiam.xchange.coinfloor.streaming;

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
import com.xeiam.xchange.service.streaming.ExchangeEvent;
import com.xeiam.xchange.service.streaming.ExchangeEventListener;

/**
 * @author gnandiga
 */
public class CoinfloorEventListener extends ExchangeEventListener {

  private static final Logger log = LoggerFactory.getLogger(CoinfloorEventListener.class);
  private final BlockingQueue<ExchangeEvent> consumerEventQueue;
  private final ObjectMapper streamObjectMapper;

  /**
   * @param consumerEventQueue
   */
  public CoinfloorEventListener(BlockingQueue<ExchangeEvent> consumerEventQueue) {

    this.consumerEventQueue = consumerEventQueue;
    this.streamObjectMapper = new ObjectMapper();
    this.streamObjectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
  }

  @Override
  public void handleEvent(ExchangeEvent event) throws ExchangeException {

    switch(event.getEventType()){
      case CONNECT:
        log.debug("Coinfloor connected");
        addToEventQueue(event);
        break;
      case DISCONNECT:
        log.debug("Coinfloor disconnected");
        addToEventQueue(event);
        break;
      case MESSAGE:
        try {
          Map<String, Object> rawJSON = streamObjectMapper.readValue(event.getData(), 
                                                                      new TypeReference<Map<String, Object>>() {});
          
          
          
        } catch (JsonParseException e) {
          log.error("Error parsing returned JSON", e);
        } catch (JsonMappingException e) {
          log.error("Error parsing returned JSON", e);
        } catch (IOException e) {
          log.error("Error parsing returned JSON", e);
        }
        break;
    }
    log.debug("Received event: " + event.getData());
  }

  private void addToEventQueue(ExchangeEvent event) {

    try {
      consumerEventQueue.put(event);
    } catch (InterruptedException e) {
      throw new ExchangeException("InterruptedException!", e);
    }
  }
}
