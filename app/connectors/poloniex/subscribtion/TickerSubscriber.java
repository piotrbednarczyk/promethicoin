package connectors.poloniex.subscribtion;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import models.CurrencyPair;
import models.Price;
import models.PriceUpdateDispatcher;
import models.Ticker;
import play.Configuration;
import play.Logger;
import rx.functions.Action1;
import ws.wamp.jawampa.PubSubData;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.function.Function;

/**
 * Created by Piotr Bednarczyk on 2017-02-19.
 */
@Singleton
public class TickerSubscriber implements Action1<PubSubData> {

    private final PriceUpdateDispatcher priceUpdateDispatcher;
    private final Config config;
    private final ArrayNodeToTicker arrayNodeToTicker;

    @Inject
    public TickerSubscriber(PriceUpdateDispatcher priceUpdateDispatcher, Configuration configuration) {
        Logger.info("TickerSubscriber construction");

        this.priceUpdateDispatcher = priceUpdateDispatcher;
        this.config = configuration.underlying();
        this.arrayNodeToTicker = new ArrayNodeToTicker(config.getString("poloniex.name"));//TODO move poloniex to constructor
    }

    @Override
    public void call(PubSubData data) {
        Logger.trace("Received {}", data.arguments());

        Ticker ticker = arrayNodeToTicker.apply(data.arguments());

        try {
            priceUpdateDispatcher.onPriceUpdate(ticker);
        } catch (Exception e) {
            Logger.error("onPriceUpdate exception ", e);
            throw  e;
        }
    }

    /**
     * Ticker returned from Poloniex in JSON format looks as follows:
     * [currencyPair, last, lowestAsk, highestBid, percentChange, baseVolume, quoteVolume, isFrozen, 24hrHigh, 24hrLow]
     */
    public class ArrayNodeToTicker implements Function<ArrayNode, Ticker> {

        private final String source;

        public ArrayNodeToTicker(String source) {
            this.source = source;
        }

        @Override
        public Ticker apply(ArrayNode node) {
            Ticker.TickerBuilder builder = Ticker.builder();
            
            builder.source(source);
            builder.occurrenceTime(LocalDateTime.now());
            builder.lastPrice(new Price(
                    new BigDecimal(node.get(1).asText()),
                    new CurrencyPair(node.get(0).asText()))
            );
            builder.lowestAsk(new BigDecimal(node.get(2).asText()));
            builder.highestBid(new BigDecimal(node.get(3).asText()));
            builder.percentChange(new BigDecimal(node.get(4).asText()));
            builder.baseVolume(new BigDecimal(node.get(5).asText()));
            builder.quoteVolume(new BigDecimal(node.get(6).asText()));
            builder.dayHigh(new BigDecimal(node.get(8).asText()));
            builder.dayLow(new BigDecimal(node.get(9).asText()));
            builder.isFrozen(node.get(7).intValue() == 0 ? Boolean.FALSE : Boolean.TRUE);

            return builder.build();
        }
    }
}


