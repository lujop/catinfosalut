package cat.joanpujol.lambda;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import software.amazon.awssdk.services.ssm.SsmClient;
import software.amazon.awssdk.services.ssm.model.GetParameterRequest;

@Path("/rest")
public class GreetingResource {

    private final SsmClient ssmClient;

    public GreetingResource(SsmClient ssmClient) {
        this.ssmClient = ssmClient;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/hello")
    public String hello(@QueryParam("name") String name) {
        return "hello " + name;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/helloPerson")
    public Person helloPerson(@QueryParam("name") String name) {
        return new Person(name);
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/token")
    public String token() {
        var paramRequest = GetParameterRequest.builder().name("telegram_token").build();
        return ssmClient.getParameter(paramRequest).parameter().value();
    }
}
