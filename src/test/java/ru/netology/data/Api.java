package ru.netology.data;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.List;

import static io.restassured.RestAssured.given;

public class Api {

    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    public static void authUser(DataGenerator.AuthInfo user) {
        given()
                .spec(requestSpec)
                .body(user)
                .when()
                .post("/api/auth")
                .then()
                .statusCode(200);
    }

    public static String verificationUser(DataGenerator.VerificationInfo user) {
        String token = given()
                .spec(requestSpec)
                .body(user)
                .when()
                .post("/api/auth/verification")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
        return token;
    }

    public static Response getResponse(String token) {
        Response response = given()
                .spec(requestSpec)
                .auth().oauth2(token)
                .when()
                .get("/api/cards")
                .then()
                .statusCode(200)
                .extract()
                .response();
        return response;
    }

    public static int getBalanceFrom(Response response, DataGenerator.Transfer transfer) {
        JsonPath jsonPathValidator = response.jsonPath();
        int balance = jsonPathValidator.getInt("balance[" + getCardIndexFrom(response, transfer) + "]");
        return balance;
    }

    public static int getBalanceTo(Response response, DataGenerator.Transfer transfer) {
        JsonPath jsonPathValidator = response.jsonPath();
        int balance = jsonPathValidator.getInt("balance[" + getCardIndexTo(response, transfer) + "]");
        return balance;
    }

    public static int getCardIndexFrom(Response response, DataGenerator.Transfer transfer) {
        JsonPath jsonPathValidator = response.jsonPath();
        List<String> numbers = jsonPathValidator.getList("number");
        int index = numbers.indexOf("**** **** **** " + transfer.getFrom().substring(15));
        return index;
    }

    public static int getCardIndexTo(Response response, DataGenerator.Transfer transfer) {
        JsonPath jsonPathValidator = response.jsonPath();
        List<String> numbers = jsonPathValidator.getList("number");
        int index = numbers.indexOf("**** **** **** " + transfer.getTo().substring(15));
        return index;
    }

    public static void transfer(DataGenerator.Transfer transfer, String token) {
        given()
                .spec(requestSpec)
                .auth().oauth2(token)
                .body(transfer)
                .when()
                .post("/api/transfer")
                .then()
                .statusCode(200);
    }
    public static void notTransfer(DataGenerator.Transfer transfer, String token) {
        given()
                .spec(requestSpec)
                .auth().oauth2(token)
                .body(transfer)
                .when()
                .post("/api/transfer")
                .then()
                .statusCode(500);
    }
}
