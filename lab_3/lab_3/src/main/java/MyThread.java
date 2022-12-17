import java.util.ArrayList;
import java.util.concurrent.Callable;

class MyThread implements Callable<ArrayList<Integer>>
{

    private final String substringToFind;
    private final int startingPosition;
    private final int beforeAmountOfLines;
    private final int afterAmountOfLines;
    private final ArrayList<String> fileContents = new ArrayList<>();
    private final ArrayList<Integer> result = new ArrayList<>();

    public MyThread(ArrayList<String> buffer, String toFind, int start, int before, int after)
    {
        fileContents.addAll(buffer);
        substringToFind = toFind;
        startingPosition = start;
        beforeAmountOfLines  = before;
        afterAmountOfLines = after;
    }

    @Override
    public ArrayList<Integer> call()
    {
            for (int i = 0; i < fileContents.size(); i++)
            {
                if (this.fileContents.get(i).toLowerCase().contains(this.substringToFind.toLowerCase()))
                {
                    for (int j = startingPosition + i - beforeAmountOfLines; j <= startingPosition + i + afterAmountOfLines; j++)
                    {
                        result.add(j);
                    }
                }
            }
            return result;
    }
}
