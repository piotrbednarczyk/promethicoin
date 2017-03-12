package connectors.poloniex.subscribtion;

import akka.actor.ActorRef;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.typesafe.config.Config;
import models.*;
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
public class PriceUpdateSubscriber implements Action1<PubSubData> {

    private final ActorRef rulesEngineActor;
    private final Config config;
    private final ArrayNodeToPriceUpdate arrayNodeToPriceUpdate;

    @Inject
    public PriceUpdateSubscriber(Configuration configuration, @Named("rulesEngineActor") ActorRef rulesEngineActor) {
        Logger.info("PriceUpdateSubscriber construction");
        this.rulesEngineActor = rulesEngineActor;
        this.config = configuration.underlying();
        this.arrayNodeToPriceUpdate = new ArrayNodeToPriceUpdate(config.getString("poloniex.name"));//TODO move poloniex to constructor
    }

    @Override
    public void call(PubSubData data) {
        Logger.trace("Received {}", data.arguments());
        try {
            PriceUpdate priceUpdate = arrayNodeToPriceUpdate.apply(data.arguments());
            rulesEngineActor.tell(priceUpdate, null);
        } catch (Exception e) {
            Logger.error("onPriceUpdate exception ", e);
            throw  e;
        }
    }

    /**
     * PriceUpdate returned from Poloniex in JSON format:
     * [currencyPair, last, lowestAsk, highestBid, percentChange, baseVolume, quoteVolume, isFrozen, 24hrHigh, 24hrLow]
     */
    public class ArrayNodeToPriceUpdate implements Function<ArrayNode, PriceUpdate> {

        private final String source;

        public ArrayNodeToPriceUpdate(String source) {
            this.source = source;
        }

        @Override
        public PriceUpdate apply(ArrayNode node) {
            PriceUpdate.PriceUpdateBuilder builder = PriceUpdate.builder();

            String currencyCodes = node.get(0).asText();

            builder.market(new Market(source, new CurrencyPair(currencyCodes)));
            builder.occurrenceTime(LocalDateTime.now());
            builder.lastPrice(new Price(
                    new BigDecimal(node.get(1).asText()),
                    new CurrencyPair(currencyCodes))
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


