import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        try {
        System.out.println("\nВведите сумму для обмена:");
        long totalMoneyAmount = scan.nextLong();
        if (totalMoneyAmount <= 0L)
            throw new NumberFormatException();

        System.out.println("\nМонетами скольких номиналов вы хотите совершить обмен?:");
        int numberOfDifferentCoins = scan.nextInt();

        ArrayList<Long> differentCoins = new ArrayList<>();

        for (int i = 0; i < numberOfDifferentCoins; i++)
        {
            System.out.println("\nВведите номинал " + (i+1) + "-й монеты:" );
            Long anotherCoin = scan.nextLong();
            differentCoins.add(anotherCoin);
        }

        differentCoins.sort(Collections.reverseOrder());
        HashMap<Long, Long> result = new HashMap<>();

        System.out.println("\nКоличество денег на обмен: " + totalMoneyAmount);

        System.out.println("\nДоступные номиналы монет:" );
        for (Long differentCoin : differentCoins)
        {
            System.out.print(differentCoin + " ");
            result.put(differentCoin, 0L); //заполняем, чтобы потом было к чему обращаться (иначе ошибка)
        }

        ArrayList<Long> finArray = new ArrayList<>();

        if (moneyExchanger(totalMoneyAmount, differentCoins, finArray)) //начинаем рекурсию
        {
            for (Long num : finArray)
            {
                result.put(num, result.get(num) + 1); // заполняем map: 1) ключ 2) +1 к значению
            }
            System.out.print("\nСумма " + totalMoneyAmount + " разменивается как: ");

            for (Long coinValue : differentCoins) // окончательный вывод
            {
                System.out.print(coinValue + "[" + result.get(coinValue) + "] ");
            }
        }
        else System.out.println("\nРазменять монетами таких номиналов сумму " + totalMoneyAmount + " невозможно.");

    } catch (InputMismatchException e) //например если ввести кучу букв
        {
            System.err.println("Пожалуйста, вводите только положительные числа.");
        }
        catch (StackOverflowError e) // на случай очень больших чисел и маленьких номиналов, ловим из moneyExchanger
        {
            System.err.println("\nЧто-то вызвало переполнение стека!");
        }
        catch (NumberFormatException e) // ловим отрицательные числа и ноль
        {
            System.err.println("\nВведите положительное число!");
        }
    }

    public static boolean moneyExchanger(long val, ArrayList<Long> coinValues, ArrayList<Long> out)
            throws StackOverflowError // на случай очень больших чисел и маленьких номиналов монет
    {
        if (val == 0L)
            return true;
        if (val < 0L)
            return false;
        Iterator<Long> iterator = coinValues.iterator();
        while (iterator.hasNext())  //идём по всем номиналам монет
        {
            long currentValue = iterator.next();
            if (moneyExchanger(val - currentValue, coinValues, out))
            {
                out.add(currentValue);
                return true;
            }
        }
        return false;
    }

}
