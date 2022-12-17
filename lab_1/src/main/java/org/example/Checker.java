package org.example;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;

import static java.lang.Character.isLetterOrDigit;

public class Checker
{
    private  ArrayList<Bracket> brackets = new ArrayList<>();
    private  ArrayDeque<Character> dequeForChecking = new ArrayDeque<>();
    private  ArrayDeque<Integer> dequeForPositions = new ArrayDeque<>();
    // private  ArrayDeque<ImmutablePair<Character, Integer>> dequeForChecking = new ArrayDeque<>();
    public Checker(String filepath)
    {
        //JSON-парсер для файла
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(filepath))
        {
            //читаем файл и записываем объекты
            Object obj = jsonParser.parse(reader);
            JSONArray jsonBracketsList = (JSONArray) obj;
            jsonBracketsList.forEach( bracket -> brackets.add(parseBracketObject( (JSONObject) bracket )) );

        } catch (FileNotFoundException e)
        {
            System.err.println("Файл не найден! :(");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e)
        {
            System.err.println("Ошибка парсинга файла на позиции: " + e.getPosition());
        }
    }
    private Bracket parseBracketObject(JSONObject jsonBracket)
    {
        //новый тип скобок
        JSONObject bracketsConfig = (JSONObject) jsonBracket.get("bracket");

        //левая и правая скобки скобка
        String left = (String) bracketsConfig.get("left");
        String right = (String) bracketsConfig.get("right");

        return new Bracket(left,right);
    }
    public void printBrackets()
    {
        System.out.println("Скобки из конфигурационного файла: ");
        brackets.forEach(bracket -> System.out.println("правая скобка: " + bracket.getOpen() +
                                        " левая скобка: " + bracket.getClose()));
    }
    public boolean checkBracketsInFile(String filepath)
    {
        File file = new File(filepath);

        try (FileReader fr = new FileReader(file))
        {
            int symbol;
            int pos = 0;
            while ((symbol = fr.read()) != -1)
            {
                if (isLetterOrDigit(symbol)) {pos++; continue;}
                for (Bracket bracket : brackets)
                {
                    if (bracket.isLeft((char)symbol))
                    {
                        dequeForChecking.push((char)symbol);
                        dequeForPositions.push(pos);
                        break;
                    }
                    if (bracket.isRight((char)symbol))
                    {
                        if (dequeForChecking.isEmpty())  // не может быть пустой стек на закрывающей скобке
                        {
                            System.err.println("\nПустой стек! Ошибка на позиции: " + pos);
                            return false;
                        }
                        if (dequeForChecking.pop() != bracket.getOpen().charAt(0)) // ошибка - не та открывающая скобка
                        {
                            System.err.println("\nОшибка на позиции: " + pos);
                            return false;
                        }
                        dequeForPositions.pop();
                        break;
                    }
                }
                pos++;
                System.out.print((char) symbol); //выводим для удобства выражение
            }
            if (!dequeForChecking.isEmpty())
            {
                System.err.println("\nОшибка: остались незакрытые скобки на позиции " + dequeForPositions.pop());
                return false;
            }
            return true;
        } catch (IOException e)
        {
            System.err.println("IOException!");
            e.printStackTrace();
            return false;
        }
    }

}
