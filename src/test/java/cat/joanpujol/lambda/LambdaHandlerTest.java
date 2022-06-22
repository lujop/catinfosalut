package cat.joanpujol.lambda;

import static org.hamcrest.Matchers.equalTo;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

@QuarkusTest
public class LambdaHandlerTest {

    @Test
    public void testHello() {
        RestAssured.when()
                .get("/rest/hello?name=Joan")
                .then()
                .contentType("text/plain")
                .body(equalTo("hello Joan"));
    }

    @Test
    public void testHelloPerson() {
        RestAssured.when()
                .get("/rest/helloPerson?name=Joan")
                .then()
                .contentType("application/json")
                .body(equalTo("{\"name\":\"Joan\"}"));
    }
}
