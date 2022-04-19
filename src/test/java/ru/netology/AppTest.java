package ru.netology;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataGenerator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class AppTest {

    @Test
    public void test() {
        DataGenerator.authUser(DataGenerator.getAuthInfo());
        var token = DataGenerator.verificationUser(DataGenerator.getVerificationInfo());
        var transfer = DataGenerator.getTransfer("5559 0000 0000 0002", "5559 0000 0000 0008", 1000);
        DataGenerator.transfer(transfer, token);
        var response = DataGenerator.getResponse(token);
        int index = DataGenerator.getCardIndex(response, "0002");
        String balance = DataGenerator.getBalance(response, index);
        assertThat(balance, equalTo("9000"));
    }

    @AfterAll
    static void shouldCleanTables() {
        DataGenerator.cleanTables();
    }
}
