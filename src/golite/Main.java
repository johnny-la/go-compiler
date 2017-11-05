package golite;

import golite.parser.*;
import golite.lexer.*;
import golite.node.*;
import golite.symbol.*;
import golite.type.*;
import golite.code.*;
import java.util.*;
import java.io.*;

public class Main
{
    // Suffixes for saved files
    private static final String PRETTY_PRINT_SUFFIX = ".pretty.go",
                                PRETTY_PRINT_TYPE_SUFFIX = ".pptype.go",
                                DUMP_SYMBOL_TABLE_SUFFIX = ".symtab",
                                SYMBOL_TABLE_SUFFIX = ".symbol.txt",
                                CODE_GENERATOR_SUFFIX = ".java";   
                            
    // Possible command-line arguments
    private static final String DUMP_SYMBOL_TABLE_ARG = "-dumpsymtab",
                                PRETTY_PRINT_TYPE_ARG = "-pptype";

    // If true, print to file. Else, print to STDOUT
    private static final boolean PRINT_TO_FILE = true;

    public static void main(String[] args)
    {
        // Caches command-line arguments
        String inputFilename = null;
        boolean dumpSymbolTable = false;
        boolean prettyPrintType = false;

        // Parse the command-line arguments
        if (args.length >= 1)
        {
            inputFilename = args[args.length-1];
            
            for (int i = 0; i < args.length; i++)
            {
                if (args[i].equals(DUMP_SYMBOL_TABLE_ARG))
                    dumpSymbolTable = true;
                if (args[i].equals(PRETTY_PRINT_TYPE_ARG))
                    prettyPrintType = true;
            }
        }
        else 
        {
            // Error
            System.out.println("Usage: ./run.sh <input-file>");
            System.exit(1);
        }

        try 
        {
            // Read from the Go file
            Parser parser = new Parser(
                                new GoliteLexer(
                                    new PushbackReader(
                                        new BufferedReader(
                                            new FileReader(inputFilename)), 1024)));

            Start tree = parser.parse();
            ParseTree(tree, inputFilename, dumpSymbolTable, prettyPrintType);

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

    private static void ParseTree(Start tree, String inputFilename, boolean dumpSymbolTable, boolean prettyPrintType)
    {
        Weeder weeder = new Weeder();
        tree.apply(weeder);

        if (inputFilename != null)
        {
            String filenamePrefix = inputFilename.split(".go")[0];
            prettyPrint(tree, filenamePrefix);

            SymbolTable symbolTable = new SymbolTable();
            SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(symbolTable, dumpSymbolTable);
            tree.apply(semanticAnalyzer);

            if (dumpSymbolTable)
                printToFile(inputFilename + DUMP_SYMBOL_TABLE_SUFFIX, semanticAnalyzer.dumpSymbolTableOutput);

            TypeChecker typeChecker = new TypeChecker(semanticAnalyzer.symbolMap);
            tree.apply(typeChecker);

            if (prettyPrintType) 
                prettyPrint(tree, filenamePrefix, typeChecker.nodeTypes, true);

            // Generate code if no type errors occurred
            if (ErrorManager.errorCount <= 0)
            {
                generateCode(tree, typeChecker.nodeTypes, semanticAnalyzer, filenamePrefix);
            }
            else
            {
                System.exit(1);
            }
        }
    }

    /**
     * Pretty prints the given AST
     */
    private static void prettyPrint(Start tree, String inputFilename)
    {
        PrettyPrinter prettyPrinter = new PrettyPrinter();
        String prettyPrint = prettyPrinter.prettyPrint(tree);
        printDebug(prettyPrint);
        printToFile(inputFilename + PRETTY_PRINT_SUFFIX, prettyPrint);
    }

    private static void prettyPrint(Start tree, String inputFilename, 
        HashMap<Node, TypeClass> nodeTypes, boolean flag)
    {
        PrettyPrinter prettyPrinter = new PrettyPrinter();
        prettyPrinter.nodeTypes = nodeTypes;
        prettyPrinter.printType = flag;
        String prettyPrint = prettyPrinter.prettyPrint(tree);

        printDebug(prettyPrint);
        printToFile(inputFilename + PRETTY_PRINT_TYPE_SUFFIX, prettyPrint);
    }

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
    private static void generateCode(Start tree, HashMap<Node,TypeClass> nodeTypes,
            SemanticAnalyzer semanticAnalyzer, String inputFilename)
    {
        printDebug("Code Generator:");
        File f = new File(inputFilename);
        String baseFileName = f.getName().replaceAll("-", "_");
        CodeGenerator codeGenerator = new CodeGenerator(tree, nodeTypes, semanticAnalyzer.newShortDeclarationVariables, semanticAnalyzer, "GoLite" + baseFileName);
        String code = codeGenerator.generateCode();

        String absolutePath = f.getAbsolutePath();
        String filePath = absolutePath.substring(0,absolutePath.lastIndexOf(File.separator));
        printDebug(code);
        printToFile(filePath + "/GoLite" + baseFileName + CODE_GENERATOR_SUFFIX, code);
    }

    /**
     * Prints the given string to the specified file
     */
    private static void printToFile(String inputFilename, String output)
    {
        try
        {
            // Writers used to output to a file
            FileWriter fileWriter = new FileWriter(inputFilename, false);
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
}
