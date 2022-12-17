package org.example;

public class Main {
    public static void main(String[] args) {
        try{

            Checker checker = new Checker(args[0]);
            checker.printBrackets();
            if (checker.checkBracketsInFile(args[1]))
            {
                System.out.println("\nВсе скобки расставлены верно.");
            }

        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            System.err.println("Неверное количество аргументов командной строки!");
        }
    }
}