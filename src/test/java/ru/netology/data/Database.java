package ru.netology.data;

import lombok.SneakyThrows;

import java.sql.DriverManager;

public class Database {

    @SneakyThrows
    public static DataGenerator.VerificationCode getVerificationCodeFor(DataGenerator.AuthInfo authInfo) {
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
        return new DataGenerator.VerificationCode(codeUser);
    }

    @SneakyThrows
    private static String getIdUserFor(DataGenerator.AuthInfo authInfo) {
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
