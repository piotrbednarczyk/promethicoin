package connectors.poloniex.subscribtion.wamp;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import play.api.Configuration;
import ws.wamp.jawampa.WampClient;
import ws.wamp.jawampa.WampClientBuilder;
import ws.wamp.jawampa.connection.IWampConnectorProvider;
import ws.wamp.jawampa.transport.netty.NettyWampClientConnectorProvider;

import java.util.concurrent.TimeUnit;

/**
 * Created by Piotr on 2017-02-19.
 */
public class WampClientProvider {

    private final Config config;

    @Inject
    public WampClientProvider(Configuration configuration) {
        this.config = configuration.underlying();
    }

    public WampClient getWampClient() throws Exception {
        WampClientBuilder builder = new WampClientBuilder();
        IWampConnectorProvider connectorProvider = new NettyWampClientConnectorProvider();

        builder.withConnectorProvider(connectorProvider)
                .withUri(config.getString("poloniex.wamp.url"))
                .withRealm(config.getString("poloniex.wamp.realm"))
                .withReconnectInterval(config.getInt("poloniex.wamp.reconnect.interval"), TimeUnit.SECONDS)
                .withInfiniteReconnects();
        return builder.build();
    }
}
