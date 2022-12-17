import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Scanner;

public class ClientHandler implements Runnable {

    private static final Logger logger = Logger.getLogger(ClientHandler.class.getName());

    private static final String ANCHOR_NAME = "###";

    private Socket client;
    private Server server;
    private String name;

    public ClientHandler(Socket client, Server server) {
        this.client = client;
        this.server = server;
    }

    @Override
    public void run() {
        Scanner inputStream = null;
        try {
            inputStream = new Scanner(client.getInputStream());
            var userNums = new ArrayList<Float>();
            while (inputStream.hasNext())
            {
                    String[] str = inputStream.nextLine().split(",");
                    for(int i = 0; i < str.length; i++)
                    {
                        userNums.add(Float.parseFloat(str[i]));
                        logger.info("Цифра: " + str[i]);
                    }
                    server.sendMessageToChat(userNums, name);
            }
            inputStream.close();


        } catch (IOException e) {
            logger.error("Ошибка при работе с клиентом", e);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }

            if (client != null) {
                try {
                    client.close();
                } catch (IOException ex) {
                    logger.error("Ошибка при закрытии клиента!", ex);
                }
            }
        }
    }

    private static float getMean(ArrayList<Float> numList)
    {
        float mean;
        Collections.sort(numList);
        mean = numList.get(numList.size() / 2);
        return mean;
    }
    public void sendMessage(ArrayList<Float> nums, String name) {
        PrintWriter outputStream = null;
        try {
            outputStream = new PrintWriter(client.getOutputStream());
            outputStream.println("Cреднее от клиента: " + getMean(nums));
            outputStream.flush();
        } catch (IOException e) {
            logger.error("Проблема при записи сообщения в поток клиента: " + client.toString(), e);
        }
    }

    public String getName() {
        return name;
    }
}