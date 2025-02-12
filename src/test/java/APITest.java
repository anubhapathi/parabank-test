import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class APITest {
    private final String baseUrl = "https://parabank.parasoft.com/parabank/services/bank";

    @Test
    void findTransactions() {
        RestAssured.baseURI = baseUrl;

        Response response = given()
                .queryParam("amount", "500")
                .when()
                .get("/findTransactions")
                .then()
                .statusCode(200)
                .body("transactions", not(empty()))  // Ensure transactions exist
                .extract()
                .response();

        System.out.println("Response: " + response.asString());

        assertNotNull(response.asString(), "Response body is empty!");
    }
}
