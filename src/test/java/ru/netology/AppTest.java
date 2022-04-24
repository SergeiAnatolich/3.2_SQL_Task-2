package ru.netology;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import ru.netology.data.Api;
import ru.netology.data.DataGenerator;
import ru.netology.data.Database;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class AppTest {

    @Test
    public void shouldTransferFromCard1ToCard2() {
        Api.authUser(DataGenerator.getAuthInfo());
        var token = Api.verificationUser(DataGenerator.getVerificationInfo());
        var response = Api.getResponse(token);
        var transfer = DataGenerator.transferFromCard1ToCard2(1000);
        int initialBalanceFromCard = Api.getBalanceFrom(response, transfer);
        int initialBalanceToCard = Api.getBalanceTo(response, transfer);
        Api.transfer(transfer, token);
        var newResponse = Api.getResponse(token);
        int actualBalanceCardFrom = Api.getBalanceFrom(newResponse, transfer);
        int actualBalanceCardTo = Api.getBalanceTo(newResponse, transfer);

        assertThat(actualBalanceCardFrom, equalTo(initialBalanceFromCard - 1000));
        assertThat(actualBalanceCardTo, equalTo(initialBalanceToCard + 1000));
    }

    @Test
    public void shouldNotTransferFromCard1ToCard2() {
        Api.authUser(DataGenerator.getAuthInfo());
        var token = Api.verificationUser(DataGenerator.getVerificationInfo());
        var response = Api.getResponse(token);
        var transfer = DataGenerator.transferFromCard1ToCard2(0);
        int initialBalanceFromCard = Api.getBalanceFrom(response, transfer);
        int initialBalanceToCard = Api.getBalanceTo(response, transfer);
        var newTransfer = DataGenerator.transferFromCard1ToCard2(initialBalanceFromCard + 1000);
        Api.notTransfer(newTransfer, token);
        var newResponse = Api.getResponse(token);
        int actualBalanceCardFrom = Api.getBalanceFrom(newResponse, newTransfer);
        int actualBalanceCardTo = Api.getBalanceTo(newResponse, newTransfer);

        assertThat(actualBalanceCardFrom, equalTo(initialBalanceFromCard));
        assertThat(actualBalanceCardTo, equalTo(initialBalanceToCard));
    }

    @AfterAll
    static void shouldCleanTables() {
        Database.cleanTables();
    }
}
