package controllers;

import com.google.inject.Inject;
import com.typesafe.config.Config;
import models.Ticker;
import play.Configuration;
import play.Logger;
import play.libs.ws.*;
import play.mvc.Controller;
import play.mvc.Result;

import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    private final WSClient wsClient;
    private final Config config;

    @Inject
    public HomeController(WSClient wsClient, Configuration configuration) {
        this.wsClient = wsClient;
        this.config = configuration.underlying();
    }

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public CompletionStage<Result> index() throws ExecutionException, InterruptedException {
        Logger.info("index");

        WSRequest request = wsClient.url(config.getString("poloniex.api.public.url"))
                .setQueryParameter(config.getString("poloniex.api.public.command"), config.getString("poloniex.api.public.commands.volume.name"));

        CompletionStage<WSResponse> responsePromise = request.get();

        return responsePromise.thenApply(response -> ok(response.asJson()));
    }

}