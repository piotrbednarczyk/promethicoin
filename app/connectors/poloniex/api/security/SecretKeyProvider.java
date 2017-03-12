package connectors.poloniex.api.security;

import com.google.inject.Singleton;

/**
 * Created by Piotr Bednarczyk on 2017-03-11.
 */
@Singleton
public class SecretKeyProvider {

    private String key = "";
    private  String secret = "";

    public String getKey() {
        return key;
    }

    public String getSecret() {
        return secret;
    }
}
