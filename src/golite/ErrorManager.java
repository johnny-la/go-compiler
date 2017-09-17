package golite;

/**
 * Prints errors or warnings to stdout
 */
public class ErrorManager
{
    public static int errorCount = 0;

    public static void printError(String message)
    {
        errorCount++;
        throw new RuntimeException("INVALID: " + message);
    }

    public static void printWarning(String message)
    {
        System.out.println("WARNING: " + message);
    }
}
