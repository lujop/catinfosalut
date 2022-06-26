package cat.joanpujol.lambda;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.services.ssm.SsmClient;

public class AWSConfig {

    @Produces
    @ApplicationScoped
    public SsmClient provideSsmClient() {
        return SsmClient.builder()
                .credentialsProvider(ProfileCredentialsProvider.create())
                .build();
    }
}
