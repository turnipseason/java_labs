import org.apache.log4j.BasicConfigurator;

public class ClientOne {
    public static void main(String[] args) {
        BasicConfigurator.configure();
        Client client = new Client("localhost", 8888, "John");
        client.start();
    }
}