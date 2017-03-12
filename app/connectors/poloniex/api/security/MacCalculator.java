package connectors.poloniex.api.security;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilderBase;
import org.asynchttpclient.SignatureCalculator;
import play.Logger;
import play.libs.ws.WSSignatureCalculator;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicLong;

import static org.apache.commons.codec.binary.Hex.encodeHexString;

/**
 * Created by Piotr Bednarczyk on 2017-03-10.
 */
@Singleton
public class MacCalculator implements SignatureCalculator, WSSignatureCalculator {

    private final static String MAC_ALGORITHM = "HmacSHA512";

    private final String key;
    private final byte[] secret;
    private final AtomicLong nonce;

    @Inject
    public MacCalculator(SecretKeyProvider keyProvider) {
        this.key = keyProvider.getKey();
        this.secret = keyProvider.getSecret().getBytes();
        this.nonce = new AtomicLong(0);
    }

    @Override
    public void calculateAndAddSignature(Request request, RequestBuilderBase<?> requestBuilder) {
        Logger.info("calculateAndAddSignature for {} with data {}", request.getUri(), request.getStringData());

        String requestData = generateRequestDataWithNonce(request.getStringData());
        requestBuilder.setBody(requestData);

        requestBuilder.setHeader("Key", key);
        requestBuilder.setHeader("Sign", getPostSignature(requestData));
    }

    private String getPostSignature(String requestData) {
        return encodeHexString(getInitializedMac().doFinal(requestData.getBytes()));
    }

    private String generateRequestDataWithNonce(String requestData) {
        return new StringBuffer(requestData)
                .append('&')
                .append("nonce")
                .append('=')
                .append(getNonce())
                .toString();
    }

    private Mac getInitializedMac() {
        try {
            Mac mac = Mac.getInstance(MAC_ALGORITHM);
            mac.init(getSecretKey());
            return mac;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            Logger.error("Mac creation failed. This is fatal!", e);
            throw new IllegalStateException("Mac creation failed.", e);
        }
    }

    private Key getSecretKey() {
        return new SecretKeySpec(secret, MAC_ALGORITHM);
    }

    private String getNonce() {
        return Long.toString(System.currentTimeMillis() + nonce.getAndIncrement());
    }
}
