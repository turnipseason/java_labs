import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.InvalidParameterException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;


public class Main {
    private static long paramValueInSeconds = 0;
    private static boolean wereAnomalies = false;

    public static void main(String[] args)
    {
        System.out.println("\nОбрабатываемый лог-файл: " + args[0]);
        try
        {   // здесь берём параметры от пользователя (или, если их нет, идём дальше)
            getUserInput();

            HashMap<Integer, QueryDateTime> queries = new HashMap<>();

            //проход по лог-файлу
            processLogFile(args[0], queries);

            if(queries.isEmpty())
                throw new ArithmeticException("Пустой лог-файл!");

            //медианное значение времени
            float averageDuration = getMeanQueryDuration(queries);

            //выводим окончательную информацию по логам
            printLogFileInfo(queries, averageDuration);

        }
        catch (IOException e)
        {
            System.err.println("Ошибка ввода/вывода.");
            e.printStackTrace();
        }
        catch (NumberFormatException e)
        {
            System.err.println("Введите число!");
            e.printStackTrace();
        }
        catch(InterruptedException e)
        {
            System.out.println(e.getMessage());
            e.printStackTrace();
        }
        catch (InvalidParameterException e)
        {
            System.err.println(e.getMessage());
        }
    }

    public static void getUserInput()
    {
        Scanner inScanner = new Scanner(System.in);
        System.out.println("""
                \nВведите параметр отклонения от медианного времени выполнения.
                Параметр должен содержать только одни единицы изменения времени.
                Например: '1 second', '4 hours', '5 days'.
                Если не хотите уточнять параметр, нажмите 'Enter'""");

        int userDefinedTimeParamValue;
        String paramName;
        String userTimeParam = inScanner.nextLine();

        if(!userTimeParam.isEmpty())
        {
            userDefinedTimeParamValue = Integer.parseInt(userTimeParam.substring(0, userTimeParam.indexOf(' ')));
            paramName = userTimeParam.substring(userTimeParam.indexOf(' ') + 1);
            paramValueInSeconds = switch (paramName){
                case "seconds", "second" -> userDefinedTimeParamValue;
                case "minutes", "minute" -> userDefinedTimeParamValue * 60L;
                case "hours", "hour" -> userDefinedTimeParamValue * 3600L;
                case "days", "day" -> userDefinedTimeParamValue * 3600L * 24L;
                case "month", "months" -> userDefinedTimeParamValue * 30L * 24L * 3600L;
                case "year", "years" -> userDefinedTimeParamValue * 365L * 24L * 3600L;
                default -> throw new IllegalStateException("Недопустимый параметр: " + paramName);
            };
            System.out.println("Значение параметра: " + paramValueInSeconds + " секунд/" +
                    TimeUnit.SECONDS.toMinutes(paramValueInSeconds) + " минут/" +
                    TimeUnit.SECONDS.toHours(paramValueInSeconds) + " часов.");
        }
        inScanner.close();
    }
    private static void processLogFile(String fileArg, HashMap<Integer, QueryDateTime> queryMap)
            throws IOException, InterruptedException, InvalidParameterException
    {
        BufferedReader in = new BufferedReader(new FileReader(fileArg));
        String logStr;
        int stringNum = 1;
        int finishedQueriesAmount = 0;

        while((logStr = in.readLine()) != null)
        {
            System.out.println("Обработка " + stringNum + " строки.");

            String queryNumAsString;
            int queryNum;
            if(logStr.contains("="))
            {
                queryNumAsString = logStr.substring(logStr.indexOf('=') + 2);
                queryNum = Integer.parseInt(queryNumAsString, 10);
            }
            else
            {
                throw new InvalidParameterException("Ошибка в формате лога: на строке №" +
                                                stringNum + " отсутствует номер запроса.");
            }

            if (logStr.contains("QUERY"))
            {
                var currentDateTime = new QueryDateTime();
                LocalDateTime dateBegin = createDateTime(logStr);
                currentDateTime.setBeginDateTime(dateBegin);
                queryMap.put(queryNum, currentDateTime);
            }
            else if (logStr.contains("RESULT"))
            {
                LocalDateTime dateEnd = createDateTime(logStr) ;
                queryMap.get(queryNum).setEndDateTime(dateEnd);
                finishedQueriesAmount++;
                System.out.println("\tЗавершён запрос №" + queryNum +
                        "\n\tКоличество обработанных завершённых запросов: " + finishedQueriesAmount);
                Thread.sleep(500);
            }
            else
            {
                throw new InvalidParameterException("Ошибка в формате лога: на строке №" +
                                                    stringNum + " отсутствуют ключевые параметры и/или их значения.");
            }
            stringNum++;
        }
        in.close();
    }
    public static LocalDateTime createDateTime(String dateTimeStr)
    {
        String year = dateTimeStr.substring(0, 4);
        String month = dateTimeStr.substring(5, 7);
        String day = dateTimeStr.substring(8, 10);
        String hour = dateTimeStr.substring(11, 13);
        String minute = dateTimeStr.substring(14, 16);
        String second = dateTimeStr.substring(17, 19);
        return LocalDateTime.of(Integer.parseInt(year), Integer.parseInt(month), Integer.parseInt(day),
                Integer.parseInt(hour), Integer.parseInt(minute), Integer.parseInt(second));
    }
    private static float getMeanQueryDuration(HashMap<Integer, QueryDateTime> queryMap)
    {
        float meanQueryDuration;
        ArrayList<Long> timeValues = new ArrayList<>();

        for (Integer key : queryMap.keySet())
        {
            timeValues.add(queryMap.get(key).getQueryExecutionTime());
        }
        Collections.sort(timeValues);
        meanQueryDuration = timeValues.get(timeValues.size() / 2);
        return meanQueryDuration;
    }
    private static void printLogFileInfo(HashMap<Integer, QueryDateTime> queryMap, float meanQueryDuration)
    {
        System.out.println("\nОбработка лог-файла завершена!");
        System.out.printf("Медианное время выполнения запроса составило: %.2f секунд.%n", meanQueryDuration);
        System.out.println("\nСледующие запросы выполнялись аномальное количество времени:");


        for (Integer key : queryMap.keySet())
        {
            long queryInSeconds = queryMap.get(key).getQueryExecutionTime();
            printAnomalies(meanQueryDuration, key, queryInSeconds);
        }
        if(!wereAnomalies)
        {
            System.out.println("Запросов, соответствующих заданному параметру аномальности, не наблюдалось.");
        }
    }
    private static void printAnomalies(float meanQueryDuration, Integer key, long queryInSeconds)
    {
        if(queryInSeconds != meanQueryDuration)
        {
            if ( queryInSeconds >= meanQueryDuration + paramValueInSeconds)
            {
                System.out.println("Запрос №" + key + " выполнялся " + queryInSeconds +
                        " секунд: на " + (queryInSeconds-meanQueryDuration) + " секунд/" +
                        TimeUnit.SECONDS.toMinutes(queryInSeconds) + " минут/"+
                        TimeUnit.SECONDS.toHours(queryInSeconds) + " часов больше медианного времени выполнения.");
                wereAnomalies = true;
            }
            else if ( (queryInSeconds <= meanQueryDuration - paramValueInSeconds) && (paramValueInSeconds>0) )
            {
                System.out.println("Запрос №" + key + " выполнялся " + queryInSeconds +
                        " секунд: на " + (meanQueryDuration-queryInSeconds) + " секунд/" +
                        TimeUnit.SECONDS.toMinutes(queryInSeconds) + " минут/"+
                        TimeUnit.SECONDS.toHours(queryInSeconds) + " часов меньше медианного времени выполнения");
                wereAnomalies = true;
            }
        }

    }
}
