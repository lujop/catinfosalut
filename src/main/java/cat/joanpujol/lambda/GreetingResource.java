package cat.joanpujol.lambda;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("/rest")
public class GreetingResource {

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
}
