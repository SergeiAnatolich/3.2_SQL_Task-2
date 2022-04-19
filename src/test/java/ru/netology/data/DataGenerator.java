package ru.netology.data;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.SneakyThrows;
import lombok.Value;

import java.sql.DriverManager;
import java.util.List;

import static io.restassured.RestAssured.given;

public class DataGenerator {
    private DataGenerator() {
    }

    @Value
    public static class AuthInfo {
        String login;
        String password;
    }

    public static AuthInfo getAuthInfo() {
        return new AuthInfo("vasya", "qwerty123");
    }

    @Value
    public static class VerificationInfo {
        String login;
        String code;
    }

    public static VerificationInfo getVerificationInfo() {
        return new VerificationInfo(getAuthInfo().getLogin(), getVerificationCodeFor(getAuthInfo()).getCode());
    }

    @Value
    public static class VerificationCode {
        private String code;
    }

    @SneakyThrows
    public static VerificationCode getVerificationCodeFor(AuthInfo authInfo) {
        String codeUser;

        var codeUserQuery = "SELECT code FROM auth_codes WHERE user_id=" + '"' + getIdUserFor(authInfo) + '"' + " ORDER BY created DESC;";
        try (var connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/alfabank_test", "sergei", "mypassword");
             var statement = connection.createStatement();
        ) {
            try (var rs = statement.executeQuery(codeUserQuery)) {
                rs.next();
                codeUser = rs.getString("code");
            }
        }
        return new VerificationCode(codeUser);
    }

    @SneakyThrows
    private static String getIdUserFor(AuthInfo authInfo) {
        String idUser;

        var idUserQuery = "SELECT id FROM users WHERE login=" + '"' + authInfo.getLogin() + '"' + ";";
        try (var connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/alfabank_test", "sergei", "mypassword");
             var statement = connection.createStatement();
        ) {
            try (var rs = statement.executeQuery(idUserQuery)) {
                rs.next();
                idUser = rs.getString("id");
            }
        }
        return idUser;
    }

    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    public static void authUser(AuthInfo user) {
        given()
                .spec(requestSpec)
                .body(user)
                .when()
                .post("/api/auth")
                .then()
                .statusCode(200);
    }

    public static String verificationUser(VerificationInfo user) {
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

    public static String getBalance(Response response, int index) {
        JsonPath jsonPathValidator = response.jsonPath();
        String balance = jsonPathValidator.getString("balance[" + index + "]");
        return balance;
    }

    public static int getCardIndex(Response response, String number) {
        JsonPath jsonPathValidator = response.jsonPath();
        List<String> numbers = jsonPathValidator.getList("number");
        int index = numbers.indexOf("**** **** **** " + number);
        return index;
    }

    @Value
    public static class Transfer {
        String from;
        String to;
        int amount;
    }

    public static Transfer getTransfer(String from, String to, int amount) {
        return new Transfer(from, to, amount);
    }

    public static void transfer(Transfer transfer, String token) {
        given()
                .spec(requestSpec)
                .auth().oauth2(token)
                .body(transfer)
                .when()
                .post("/api/transfer")
                .then()
                .statusCode(200);
    }

    @SneakyThrows
    public static void cleanTables() {
        var cleanAuthCodes = "DELETE FROM auth_codes;";
        var cleanCardTransactions = "DELETE FROM card_transactions;";
        var cleanCards = "DELETE FROM cards;";
        var cleanUsers = "DELETE FROM users;";
        try (var connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/alfabank_test", "sergei", "mypassword");
             var statement = connection.createStatement();
        ) {
            statement.execute(cleanAuthCodes);
            statement.execute(cleanCardTransactions);
            statement.execute(cleanCards);
            statement.execute(cleanUsers);
        }
    }
}