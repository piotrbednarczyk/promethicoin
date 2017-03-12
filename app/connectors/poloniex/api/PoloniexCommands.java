package connectors.poloniex.api;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import play.Configuration;

/**
 * Created by Piotr Bednarczyk on 2017-03-11.
 */
@Singleton
public class PoloniexCommands {

    private final String publicApiUrl;
    private final String command;
    private final String returnOrderBook;
    private final String currencyPair;
    private final String orderBookDepth;

    @Inject
    public PoloniexCommands(Configuration configuration) {
        Config config = configuration.underlying();

        this.publicApiUrl = config.getString("poloniex.api.public.url");
        this.command = config.getString("poloniex.api.public.command");
        this.returnOrderBook = config.getString("poloniex.api.public.commands.orderBook.name");
        this.currencyPair = config.getString("poloniex.api.public.commands.orderBook.parameters.currencyPair");
        this.orderBookDepth = config.getString("poloniex.api.public.commands.orderBook.parameters.depth");
    }

    public String getCommand() {
        return command;
    }

    public String getCurrencyPair() {
        return currencyPair;
    }

    public String getPublicApiUrl() {
        return publicApiUrl;
    }

    public String getReturnOrderBook() {
        return returnOrderBook;
    }

    public String getOrderBookDepth() {
        return orderBookDepth;
    }
}
