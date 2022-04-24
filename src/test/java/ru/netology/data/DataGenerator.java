package ru.netology.data;

import lombok.Value;

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
        return new VerificationInfo(getAuthInfo().getLogin(), Database.getVerificationCodeFor(getAuthInfo()).getCode());
    }

    @Value
    public static class VerificationCode {
        private String code;
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

    public static Transfer transferFromCard1ToCard2(int amount) {
        return new Transfer("5559 0000 0000 0001", "5559 0000 0000 0002", amount);
    }

    public static Transfer transferFromCard2ToCard1(int amount) {
        return new Transfer("5559 0000 0000 0002", "5559 0000 0000 0001", amount);
    }
}