import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Main {
    public static ArrayList<MyThread> tasks = new ArrayList<>();
    public static void main(String[] args)
    {
        Scanner scan = new Scanner(System.in);

        System.out.println("\nВведите строку, которую хотите найти в файле:");
        String toFind = scan.nextLine();
        System.out.println("\nВведите количество строк, которое необходимо вывести до строки, содержащей вашу подстроку:");
        int prevLinesAmount = scan.nextInt();
        System.out.println("\nВведите количество строк, которое необходимо вывести после строки, содержащей вашу подстроку:");
        int nextLinesAmount = scan.nextInt();

        //файл, который надо прочесть -- аргумент командной строки
        //файл, куда будем записывать (даже если он ещё не существует) - тоже

        //пул, в который поток будет передавать куски файла
        ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() - 1);


        try (BufferedReader in = new BufferedReader(new FileReader(args[0])))
        {
            giveTasksToThreadPool(toFind, prevLinesAmount, nextLinesAmount, in);

            //ждём пока все завершатся и помещаем их в future
            var futures = es.invokeAll(tasks);
            FileReader fileRead = new FileReader(args[0]);
            BufferedReader bufRead = new BufferedReader(fileRead);
            BufferedWriter bufWrite = new BufferedWriter(new FileWriter(args[1]));

            //выводим в файл
            printFinishedTasksToFile(futures, bufWrite, bufRead);

            bufWrite.close();
            bufRead.close();
            es.shutdown();

        } catch (FileNotFoundException e)
        {
            System.err.println("Файл не найден! :(");
            System.err.println(e.getMessage());
            e.printStackTrace();
        } catch (IOException e)
        {
            System.err.println("Ошибка ввода/вывода :(");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        catch (ExecutionException e)
        {
            System.err.println("Ошибка выполнения потока.");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        catch (InterruptedException e)
        {
            System.err.println("Поток прервался при выполнении.");
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
    }


    private static void giveTasksToThreadPool(String toFind, int prevLinesAmount, int nextLinesAmount,
                                              BufferedReader in) throws IOException
    {
        int startPos = 0;
        var fileContents = new ArrayList<String>();
        while (in.ready())
        {
            for (int i = 0; i < 5; i++)
            {
                String str = in.readLine();
                if (str != null)
                {
                    fileContents.add(str);
                }
            }
            var task = new MyThread(fileContents, toFind, startPos, prevLinesAmount, nextLinesAmount);
            tasks.add(task);
            fileContents.clear();
            startPos +=5;
        }
    }

    private static void printFinishedTasksToFile(List<Future<ArrayList<Integer>>> futures, BufferedWriter bufWrite,
                                                 BufferedReader bufRead) throws InterruptedException, IOException,
                                                                                ExecutionException
    {
        int lineNum = 0;
        String line;
        var resultingLineNumbers = new ArrayList<Integer>();
        var lineMap = new HashMap<Integer, String>();

        //добавляем из "будущего" все элементы
        for (var futureLineNumber : futures)
        {
            resultingLineNumbers.addAll(futureLineNumber.get());
        }

        while (bufRead.ready())
        {
            line = bufRead.readLine();
            if (resultingLineNumbers.contains(lineNum)) {lineMap.put(lineNum, line);}
            lineNum++;

        }
        for(Integer number : resultingLineNumbers)
        {
            if(lineMap.get(number)!=null)
            {
                bufWrite.write("Line №" + (number+1) + " found: " + lineMap.get(number) + '\n');
            }
        }
    }

}
