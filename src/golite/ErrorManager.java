package golite;

public class ErrorManager
{
    public static int errorCount = 0;

    public static void printError(String message)
    {
        errorCount++;
        throw new RuntimeException("INVALID: " + message);
    }
}
