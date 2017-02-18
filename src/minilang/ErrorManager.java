package minilang;

public class ErrorManager
{
    public static int errorCount = 0;

    public static void printError(String message)
    {
        System.out.println("INVALID: " + message);
        errorCount++;
    }
}
