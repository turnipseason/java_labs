import org.apache.log4j.BasicConfigurator;

public class ClientTwo {
    public static void main(String[] args) {
        BasicConfigurator.configure();
        Client client =  new Client("localhost", 8888, "Alice");
        client.start();
    }
}