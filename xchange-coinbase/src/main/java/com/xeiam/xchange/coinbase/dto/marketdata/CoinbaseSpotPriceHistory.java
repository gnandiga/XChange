package com.xeiam.xchange.coinbase.dto.marketdata;

import java.io.IOException;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.xeiam.xchange.coinbase.dto.marketdata.CoinbaseSpotPriceHistory.CoibaseSpotPriceHistoryDeserializer;
import com.xeiam.xchange.utils.DateUtils;

@JsonDeserialize(using = CoibaseSpotPriceHistoryDeserializer.class)
public class CoinbaseSpotPriceHistory {

  private final List<CoinbaseHistoricalSpotPrice> spotPriceHistory;

  public CoinbaseSpotPriceHistory(final List<CoinbaseHistoricalSpotPrice> spotPriceHistory) {

    this.spotPriceHistory = spotPriceHistory;
  }

  public List<CoinbaseHistoricalSpotPrice> getSpotPriceHistory() {

    return spotPriceHistory;
  }

  @Override
  public String toString() {

    return "CoinbaseSpotPriceHistory [spotPriceHistory=" + spotPriceHistory + "]";
  }

  static class CoibaseSpotPriceHistoryDeserializer extends JsonDeserializer<CoinbaseSpotPriceHistory> {

    private static final Pattern historicalRateStringPatternInReverse = Pattern.compile("(\\d{1,2}\\.\\d+),(\\d{2}:\\d{2}-\\d{2}:\\d{2}:\\d{2}T\\d{2}\\-\\d{2}-\\d{4})");

    @Override
    public CoinbaseSpotPriceHistory deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {

      final List<CoinbaseHistoricalSpotPrice> historicalPrices = new ArrayList<CoinbaseHistoricalSpotPrice>();

      // If there is a better way to get to the actual String returned please replace this section.
      final StringReader reader = (StringReader) ctxt.getParser().getTokenLocation().getSourceRef();
      reader.reset();
      final StringBuilder historyStringBuilder = new StringBuilder();
      for (int c = reader.read(); c > 0; c = reader.read())
        historyStringBuilder.append((char) c);

      // Parse in reverse because they are inconsistent with the number of decimals for the rates
      // which makes it difficult to differentiate from the following year. Going in reverse
      // we can rely on the comma.
      final String entireHistoryString = historyStringBuilder.reverse().toString();
      final Matcher matcher = historicalRateStringPatternInReverse.matcher(entireHistoryString);
      while (matcher.find()) {
        final String rateString = new StringBuilder(matcher.group(1)).reverse().toString();
        final BigDecimal spotRate = new BigDecimal(rateString);
        final String timestampString = new StringBuilder(matcher.group(2)).reverse().toString();
        final Date timestamp = DateUtils.fromISO8601DateString(timestampString);

        final CoinbaseHistoricalSpotPrice historicalSpotPrice = new CoinbaseHistoricalSpotPrice(timestamp, spotRate);
        historicalPrices.add(historicalSpotPrice);
      }

      return new CoinbaseSpotPriceHistory(historicalPrices);
    }
  }
}
