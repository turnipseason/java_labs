import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void fiveHundredWithOneCoinOfDenominationHundredReturnsTrue()
    {
        long totalMoneyAmount = 500L;
        ArrayList<Long> differentCoins = new ArrayList<>();
        ArrayList<Long> finArray = new ArrayList<>();
        differentCoins.add(0, 100L);
        assertTrue(Main.moneyExchanger(totalMoneyAmount, differentCoins, finArray));

    }

    @Test
    void hundredCoinsWithCoinOfDenominationSeverReturnsFalse()
    {
        long totalMoneyAmount = 100L;
        ArrayList<Long> differentCoins = new ArrayList<>();
        ArrayList<Long> finArray = new ArrayList<>();
        differentCoins.add(0, 7L);
        assertFalse(Main.moneyExchanger(totalMoneyAmount, differentCoins, finArray));
    }


}