package golite.code;

import golite.parser.*;
import golite.lexer.*;
import golite.node.*;
import golite.analysis.*;
import golite.code.*;
import golite.type.*;

import java.util.*;

public class CodeGenerator extends DepthFirstAdapter
{
    private int indentLevel;
    private StringBuffer output; 

    private Node root;
    private TypeChecker typeChecker;

    private static final int STRING_SIZE = 1024;

    private static final String STRING_TYPE = "char",
                                STRING_ARRAY_SIZE = "[" + STRING_SIZE + "]";

    private static final String INT_DEFAULT = "0",
                                FLOAT_DEFAULT = "0.0",
                                STRING_DEFAULT = "\"\"";

    // The string buffer used for concatenating strings
    private String stringBuffer = "buffer";

    /*private static final String fileHeader = String.join("\n",
        "#include <stdlib.h>",
        "#include <stdio.h>",
        "#include <string.h>",
        "",
        "char *cat(char *s1, char *s2) {",
        "    char *newString = (char *)malloc(1024);",
        "    newString[0] = '\\0';",
        "",
        "    strcpy(newString, s1);",
        "    strcat(newString, s2);",
        "",
        "    return newString;",
        "}",
        "",
        "char *times(char *string, int factor)",
        "{",
        "    if (factor < 0)",
        "    {",
        "        printf(\"Error: multiplying a string by a negative number: '%s * %d'\\n\", string, factor);",
        "        exit(1);",
        "    }",
        "",
        "    char *newString = (char *)malloc(strlen(string) * factor + 1);",
        "    newString[0] = '\\0';",
        "",
        "    int i;",
        "    for (i = 0; i < factor; i++)",
        "    {",
        "        strcat(newString, string);",
        "    }",
        "",
        "    return newString;",
        "}");*/

    public CodeGenerator(Node root, TypeChecker typeChecker)
    {
        this.root = root;
        this.typeChecker = typeChecker;
    }

    public String generateCode()
    {
        // Reset the indentation level before printing the tree
        indentLevel = 0;
        // Create a new output string
        output = new StringBuffer();

        // Start pretty printing at the root
        root.apply(this);

        return output.toString();
    }

    // Prints the given string without a newline
    private void print(String s)
    {
        output.append(s);
    }

    // Prints the given string with preceding indentation
    private void printi(String s)
    {
        for (int i = 0; i < indentLevel; i++) { output.append("    "); }
        print(s);
    }

    /** 
     * Prints the given string with indentation.
     * Prints a newline at the end of the string.
     */
    private void printiln(String s)
    {
        printi(s + "\n");
    }

    // Prints the given string, along with a newline
    private void println(String s)
    {
        output.append(s);
        output.append("\n");
    }

    // Pretty prints the list of nodes
    private void printNodes(LinkedList<? extends Node> nodes)
    {
        for (Node node : nodes) { node.apply(this); }
    }

    /**
     * Returns the printf/scanf codes for the given type
     */
    private String getScanCode(Type type)
    {
        switch (type)
        {
            case INT:
                return "%d";
            case FLOAT:
                return "%f";
            case STRING:
                return "%s";
            default:
                return "INVALID_TYPE";
        }
    }  

    /** PROGRAM */
    /*public void caseAProgram(AProgram node)
    {
        println(fileHeader + "\n");
        // Print the declarations
        printNodes(node.getDecl());

        // Print the main function and all the statements
        println("\nint main() {");
        indentLevel++;
        printNodes(node.getStmt());
        indentLevel--;
        println("}");
    }*/

    /** STATEMENTS */
    /*public void caseAReadStmt(AReadStmt node)
    {
        Type idType = typeChecker.getIdType(node.getId());
        String scanCode = getScanCode(idType);

        printi("scanf(\"" + scanCode + "\", ");
        if (idType != Type.STRING)
            print ("&");
        println(node.getId().getText() + ");"); 
    }
  
    public void caseAPrintStmt(APrintStmt node)
    {
        Type expType = typeChecker.getType(node.getExp());
        printi("printf(\"" + getScanCode(expType) + "\", ");
        node.getExp().apply(this);
        println(");");
    }

    public void caseAAssignStmt(AAssignStmt node)
    {
        Type idType = typeChecker.getIdType(node.getId());

        if (idType != Type.STRING)
        {
            printi(node.getId().getText() + " = ");
        }
        else
        {
            printi("strncpy(" + node.getId().getText() + ", ");
        }

        node.getExp().apply(this);

        if (idType == Type.STRING)
        {
            print(", " + Integer.toString(STRING_SIZE) + ")");
        }
        println(";");
    }

    public void caseAWhileStmt(AWhileStmt node)
    {
        printi("while (");
        node.getExp().apply(this);
        println(") {");

        // Print the statements in the while-loop
        indentLevel++;
        printNodes(node.getStmt());       
 
        indentLevel--;
        printiln("}");
    }

    public void caseAIfStmt(AIfStmt node)
    {
        printi("if (");
        node.getExp().apply(this);
        println(") {");

        indentLevel++;
        printNodes(node.getStmt());

        indentLevel--;
        node.getEnd().apply(this);

        printiln("}");
    }

    public void caseAElseStmt(AElseStmt node)
    {
        printiln("} else {");
 
        indentLevel++;
        printNodes(node.getStmt());

        indentLevel--;
    }*/

    /** DECLARAIONS */
    /*public void caseAVarDecl(AVarDecl node)
    {
        Type varType = TypeChecker.stringToType(node.getType().getText());
        String varTypeString = node.getType().getText();
        String assignmentSuffix = "";

        if (varType == Type.INT)
        {
            assignmentSuffix = " = " + INT_DEFAULT;
        }
        else if (varType == Type.FLOAT)
        {
            assignmentSuffix = " = " + FLOAT_DEFAULT;
        }
        else if (varType == Type.STRING)
        {
            assignmentSuffix = " = " + STRING_DEFAULT;
            varTypeString = STRING_TYPE;
        }

        println(varTypeString + " " + node.getId().getText() + 
                ((varType == Type.STRING)? STRING_ARRAY_SIZE:"") + 
                assignmentSuffix + ";");
    }*/
   
    /** EXPRESSIONS */
    /*public void caseAPlusExp(APlusExp node)
    {
        Type type = typeChecker.getType(node.getL());
        
        // Adding numbers
        if (type != Type.STRING)
        {
            print("(");
            node.getL().apply(this);
            print("+");
            node.getR().apply(this);
            print(")");
        }
        // Adding strings
        else
        {
            print("cat(");
            node.getL().apply(this);
            print(", ");
            node.getR().apply(this);
            print(")");
        }
    }

    public void caseAMinusExp(AMinusExp node)
    {
        print("(");
        node.getL().apply(this);
        print("-");
        node.getR().apply(this);
        print(")");
    }

    public void caseAMultExp(AMultExp node)
    {
        Type leftType = typeChecker.getType(node.getL());
        Type rightType = typeChecker.getType(node.getR());

        // Multiplying numbers
        if (leftType != Type.STRING && rightType != Type.STRING)
        {
            print("(");
            node.getL().apply(this);
            print("*");
            node.getR().apply(this);
            print(")");
        }
        // Multiplying strings
        else
        {
            print("times(");
            // Print string
            if (leftType == Type.STRING) { node.getL().apply(this); }
            else { node.getR().apply(this); }
            print(", ");
            // Print integer
            if (leftType == Type.INT) { node.getL().apply(this); }
            else { node.getR().apply(this); }
            print(")"); 
        }
    }

    public void caseADivideExp(ADivideExp node)
    {
        print("(");
        node.getL().apply(this);
        print("/");
        node.getR().apply(this);
        print(")");
    }
 
    public void caseAUminusExp(AUminusExp node)
    {
        print("(-");
        node.getExp().apply(this);
        print(")");
    }
 
    public void caseAIdExp(AIdExp node)
    {
        print(node.getId().getText());
    }

    public void caseAFloatExp(AFloatExp node)
    {
        print(node.getFloat().getText());
    }

    public void caseAIntExp(AIntExp node)
    {
        print(node.getInt().getText());
    }*/

}
