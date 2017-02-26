package golite;

import golite.parser.*;
import golite.lexer.*;
import golite.node.*;
import golite.symbol.*;
import golite.type.*;
import golite.code.*;

import java.io.*;

public class Main
{
    private static final String PRETTY_PRINT_SUFFIX = ".pretty.min",
                                SYMBOL_TABLE_SUFFIX = ".symbol.txt",
                                CODE_GENERATOR_SUFFIX = ".c";    

    // If true, print to file. Else, print to STDOUT
    private static final boolean PRINT_TO_FILE = true;

    // Pretty prints the given AST
    // private static void prettyPrint(Start tree, String filename)
    // {
    //     PrettyPrinter prettyPrinter = new PrettyPrinter();
    //     String prettyPrint = prettyPrinter.prettyPrint(tree);

    //     printDebug(prettyPrint);
    //     printToFile(filename + PRETTY_PRINT_SUFFIX, prettyPrint);
    // }

    /**
     * Prints the given string to stdout if PRINT_TO_FILE == false
     */
    private static void printDebug(String output)
    {
        // Don't print debug lines if we are printing to files
        if (PRINT_TO_FILE) { return; }

        System.out.println(output);
    }

    /** 
     * Generates C code from the given AST
     */
    private static void generateCode(Start tree, TypeChecker typeChecker,
            String filename)
    {
        printDebug("Code Generator:");
        CodeGenerator codeGenerator = new CodeGenerator(tree, typeChecker);
        String code = codeGenerator.generateCode();

        printDebug(code);
        printToFile(filename + CODE_GENERATOR_SUFFIX, code);
    }

    /**
     * Prints the given string to the specified file
     */
    private static void printToFile(String filename, String output)
    {
        try
        {
            // Writers used to output to a file
            FileWriter fileWriter = new FileWriter(filename, false);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            printWriter.printf("%s", output);
 
            fileWriter.close();
            printWriter.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args)
    {
        try 
        {
            Parser parser = null;

            if (args.length == 0)
            {
                 // Read from stdin
                 parser = new Parser(
                            new Lexer(
                                new PushbackReader(
                                    new InputStreamReader(System.in), 1024)));
            }
            else if (args.length == 1)
            {
                // Read from a file
                parser = new Parser(
                            new Lexer(
                                new PushbackReader(
                                    new BufferedReader(
                                        new FileReader(args[0])), 1024)));
            }
            else 
            {
                System.out.println("Usage: java minilang.Main <input-file>");
                System.exit(1);
            }

            Start tree = parser.parse();

            if (args[0] != null)
            {
                String filename = args[0].split(".min")[0];
                // prettyPrint(tree, filename);

                /*SymbolTable symbolTable = new SymbolTable();
                SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(symbolTable);
                printDebug("Semantic Analyzer:");
                tree.apply(semanticAnalyzer);
                printDebug("\nSymbol table:");
                printDebug(symbolTable.toString());
                printToFile(filename + SYMBOL_TABLE_SUFFIX, symbolTable.toString());
                
                printDebug("\nType Checker:");
                TypeChecker typeChecker = new TypeChecker(symbolTable);
                tree.apply(typeChecker);
                printDebug(typeChecker.toString());

                // Generate C code if no type errors occurred
                if (ErrorManager.errorCount <= 0)
                {
                    generateCode(tree, typeChecker, filename);
                }
                else
                {
                    System.exit(1);
                }*/
                
            }

            System.out.println("VALID");
            System.exit(0);
            
        }
        catch (Exception e)
        {
            System.out.print("INVALID: " + e);
            e.printStackTrace();
            System.exit(1);
        }
    }
}
