import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        try
        {
        long dirSize = getDirSize(Path.of(args[0]));
        File dirString = new File(args[0]);
        System.out.println("\nРазмер заданной (" + args[0] + ") директории: \n" + dirSize + " bytes/" +
                                                                dirSize/1e+6 + " Mb/" + dirSize/1e+9 + " Gb");

            System.out.println("Размер директории с использованием Apache: \n" +
                    FileUtils.sizeOfDirectory(dirString) + " bytes.");
        } catch (NullPointerException e)
        {
            System.err.print("Пустая директория.");
        } catch (ArrayIndexOutOfBoundsException e)
        {
            System.err.println("Неверное количество аргументов командной строки!");
        }

    }
    public static long getDirSize(Path path) {

        long dirSize = 0L;

        try (Stream<Path> walk = Files.walk(path)) { //проходимся по всем файлам в директории
            dirSize = walk
                    .filter(Files::isRegularFile)
                    .mapToLong(Main::sizeOfFile)
                    .sum();

        } catch (IOException e) {
            System.out.printf("IO error: " + e.getMessage());
        } catch (NullPointerException e)
        {
            System.out.println("Пустая директория.");
        }
        return dirSize;
    }

    private static long sizeOfFile(Path filePath)
    {
        try
        {
            return Files.size(filePath);
        } catch (IOException e)
        {
            System.out.printf("No access to the size of file at path" + filePath + e);
            return 0L;
        }
    }
}
