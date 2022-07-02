package cat.joanpujol.lambda;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParametersRequest;

/**
 * Read parameters form SSM at startup time and provide them
 */
@ApplicationScoped
public class SecretsConfig {
    private String ssmTelegramToken;
    private String ssmPrivateApiSecretToken;
    private String apiURL;
    private static final String SSM_TELEGRAM_TOKEN = "telegram_token";
    private static final String SSM_PRIVATE_API_SECRET_TOKEN = "private_api_secret";
    private static final String SSM_API_URL = "api_url";

    @PostConstruct
    void readSsmValues(SsmClient ssmClient) {
        var request = GetParametersRequest.builder()
                .names(SSM_TELEGRAM_TOKEN, SSM_PRIVATE_API_SECRET_TOKEN, SSM_API_URL)
                .withDecryption(true)
                .build();
        var params = ssmClient.getParameters(request).parameters();
        params.stream()
                .filter(param -> param.name().equals(SSM_TELEGRAM_TOKEN))
                .findFirst()
                .ifPresent(param -> ssmTelegramToken = param.value());
        params.stream()
                .filter(param -> param.name().equals(SSM_PRIVATE_API_SECRET_TOKEN))
                .findFirst()
                .ifPresent(param -> ssmPrivateApiSecretToken = param.value());
        params.stream()
                .filter(param -> param.name().equals(SSM_API_URL))
                .findFirst()
                .ifPresent(param -> apiURL = param.value());
    }

    public String getSsmTelegramToken() {
        return ssmTelegramToken;
    }

    public String getSsmPrivateApiSecretToken() {
        return ssmPrivateApiSecretToken;
    }

    public String getApiURL() {
        return apiURL;
    }
}
