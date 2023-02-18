package org.example;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;


public class APITest {

    @BeforeAll
    public static void setup(){
        RestAssured.baseURI = "https://automationexercise.com";
    }

    @Test
    public void canReturnProductListTest() {

        given()
                .accept(ContentType.JSON).
        when().
                get("/api/productsList").
        then().
                statusCode(HttpStatus.SC_OK).
                // The API return content-type: "text/html" rather than JSON. It does not respect the header "accept:application/json".
                // This cases RestAssured to use XMLPath rather than JSON Path for assertions.
                // !!! This should be defect !!!!
                // To make this test work, forcing the response to be parsed as JSON.
                using().parser("text/html", Parser.JSON).
                body("responseCode", equalTo(200)).
                body("products.find{it.id==1}.name", equalTo("Blue Top"));

    }

    @Test
    public void canCreateAccountTest() {

        // This test will break once the cookie and thus the csrf token expires.
        // In a real work env. this test will retrieve the token from the previous request i.e. login
        // Not including the login request here to keep the exercise scope short.

        // !! CSRF tokens should not be transmitted within cookies, like it's been done in this request !!

        HashMap<String, String> headers = new HashMap<>();
        headers.put("content-type", "application/x-www-form-urlencoded");
        headers.put("referer", "https://automationexercise.com/signup");
        headers.put("origin", "https://automationexercise.com");
        headers.put("cookie", "csrftoken=YbxysQMJdDuqlaxAsioaxh7fvLh8m4E3j1ApOziFdaxBYSmCIaibhMPDQaRnZWTx; __gads=ID=9ca124621800b807-225f381fe6d90055:T=1676716909:RT=1676716909:S=ALNI_MYqlQI-_MS91KMLj1hnViACS8WF_w; __gpi=UID=0000094a01cc2f4a:T=1676716909:RT=1676716909:S=ALNI_MY7HpZU6-lmltfR8AAGaah2X7jooQ; sessionid=tsshf7iap3fycao9z2n1or7zzxitnlao");

        HashMap<String, String> formParams = new HashMap<>();
        formParams.put("name", "Prateek");
        formParams.put("email_address", "asd" + UUID.randomUUID().toString() + "%40gmail.com");  //needs to be unique
        formParams.put("password", "12345");
        formParams.put("first_name", "prateek");
        formParams.put("last_name", "sharma");
        formParams.put("address1", "address");
        formParams.put("country", "Canada");
        formParams.put("state", "asd");
        formParams.put("city", "asd");
        formParams.put("zipcode", "2134");
        formParams.put("mobile_number", "2423534545");
        formParams.put("form_type", "create_account");
        formParams.put("csrfmiddlewaretoken", "5yRMOGfr200MUbP2lWuwyUclCnNumCGvqoUDapLn2x3XxTE4BOoxipUJXMnJZuVZ");

        given().
                headers(headers).
                formParams(formParams).
        when().
                post("/signup").
        then().
                statusCode(302);
                // !!This is defect!!! This should be a 303. 302 with POST requires user confirmation before redirect as per HTTP Specification
                // RestAssured does not support redirections for 302.

    }
}
