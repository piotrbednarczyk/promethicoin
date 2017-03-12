package connectors.poloniex.api;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.typesafe.config.Config;
import connectors.poloniex.api.security.MacCalculator;
import io.netty.handler.codec.http.HttpMethod;
import models.Balance;
import models.Currency;
import play.Configuration;
import play.Logger;
import play.libs.ws.WSClient;
import play.libs.ws.WSRequest;
import play.libs.ws.WSResponse;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletionStage;

/**
 * Created by Piotr Bednarczyk on 2017-03-10.
 */
@Singleton
public class PoloniexTradingConnector {

    public static final String CONTENT_TYPE = "application/x-www-form-urlencoded; charset=UTF-8";

    private final Config config;
    private final WSClient wsClient;
    private MacCalculator signatureCalculator;

    @Inject
    public PoloniexTradingConnector(Configuration configuration, WSClient wsClient, MacCalculator signatureCalculator) {
        this.config = configuration.underlying();
        this.wsClient = wsClient;
        this.signatureCalculator = signatureCalculator;
    }

    public CompletionStage<Map<Currency, Balance>> getAccountBalances() {

        WSRequest request = createRequest("command=returnBalances");

        return request.execute().thenApply(
                response -> {
                    Logger.debug("Received answer: " + response.asJson());
                    return responseToBalanceMap(response);
                }
        );
    }

    private Map<Currency, Balance> responseToBalanceMap(WSResponse response) {
        Map<Currency, Balance> balances = new HashMap<>();

        response.asJson().fields().forEachRemaining(node -> {
            BigDecimal amount = node.getValue().decimalValue();

            if(BigDecimal.ZERO.compareTo(amount) < 0) {
                Currency currency = new Currency(node.getKey());
                balances.put(currency, new Balance(amount, currency));
            }
        });

        return balances;
    }

    private WSRequest createRequest(String requestBody) {
        return wsClient.url(config.getString("poloniex.api.private.url"))
                .setMethod(HttpMethod.POST.name())
                .setContentType(CONTENT_TYPE)
                .setBody(requestBody)
                .sign(signatureCalculator);
    }
}

//        Test synchronous request.
//
//        HttpClient httpClient = HttpClients.createDefault();
//        HttpPost httppost = new HttpPost("https://poloniex.com/tradingApi");
//        List<NameValuePair> params = new ArrayList<NameValuePair>(2);
//        params.add(new BasicNameValuePair("command", "returnBalances"));
//        String nonce = getNonce();
//        params.add(new BasicNameValuePair("nonce", nonce));
//
//        try {
//            UrlEncodedFormEntity entity1 = new UrlEncodedFormEntity(params, "UTF-8");
//
//            Logger.info("ENTITY1 " + getString(entity1));
//
//            httppost.setEntity(entity1);
//            httppost.addHeader("Key", KEY);
//            httppost.addHeader("Sign", encodeHexString(getInitializedMac().doFinal(getString(entity1).getBytes())));
//
//
//            org.apache.http.HttpResponse response = httpClient.execute(httppost);
//            HttpEntity entity = response.getEntity();
//
//            String theString = getString(entity);
//
//            Logger.info("@@@@@@@@@@@@@@Answer: " + theString);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        } catch (ClientProtocolException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        private String getString(HttpEntity entity) throws IOException {
//            StringWriter writer = new StringWriter();
//            IOUtils.copy(entity.getContent(), writer, "UTF-8");
//            return writer.toString();
//        }