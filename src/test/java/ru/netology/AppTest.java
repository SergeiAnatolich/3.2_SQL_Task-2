package ru.netology;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataGenerator;

import static org.hamcrest.MatcherAssert.assertThat;

public class AppTest {

    @Test
    public void test() {
        DataGenerator.authUser(DataGenerator.getAuthInfo());
        var token = DataGenerator.verificationUser(DataGenerator.getVerificationInfo());
        var cards = DataGenerator.getCardUser(token);
        String count = cards.path("balance");
        assertThat(count, equals("1000000"));
    }

    @AfterAll
    static void shouldCleanDB() {
        DataGenerator.cleanDB();
    }
}
