package golite;

import golite.parser.*;
import golite.lexer.*;
import golite.node.*;
import golite.analysis.*;

import java.util.*;

public class PrettyPrinter extends DepthFirstAdapter
{
    private static int indentLevel;
    private static StringBuffer output; 

    public static String prettyPrint(Node node)
    {
        // Reset the indentation level before printing the tree
        indentLevel = 0;
        // Create a new output string
        output = new StringBuffer();

        // Start the pretty printing at the given node
        node.apply(new PrettyPrinter());

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

    /** PROGRAM */
    public void caseAProgram(AProgram node)
    {
        // Print declarations
        for (int i = 0; i < node.getDecl().size(); i++)
        {
            Node decl = node.getDecl().get(i);
            decl.apply(this);
            // Don't print newlines/semicolons for function declarations
            if (!(decl instanceof AFuncDeclAstDecl)) println(";");
        }

        println("");
        printNodes(node.getStmt());
    }

    /** DECLARATIONS */
    //base case varTypes

    public void caseABaseTypeVarType(ABaseTypeVarType node) {
        print(node.getType().getText());
    }

    public void caseASliceVarType(ASliceVarType node) {
        print("[]");
        node.getVarType().apply(this);
    }

    public void caseAArrayVarType(AArrayVarType node) {
        print("[" + node.getInt().getText() + "]");
        node.getVarType().apply(this);
    }

    public void caseAStructVarType(AStructVarType node) {
        print(node.getId().getText());
    }

    //var declarations
    public void caseAVarDeclAstDecl(AVarDeclAstDecl node) {
        print("var ");
        node.getVarDecl().apply(this);
        //println("");

    }

    public void caseAVarWithTypeVarDecl(AVarWithTypeVarDecl node) {
        print(node.getId().getText() + " ");
        node.getVarType().apply(this);
    }

    public void caseAVarWithOnlyExpVarDecl(AVarWithOnlyExpVarDecl node) {
        print(node.getId().getText() + " = ");
        node.getExp().apply(this);
    }

    public void caseAVarWithTypeAndExpVarDecl(AVarWithTypeAndExpVarDecl node) {
        print(node.getId().getText() + " ");
        node.getVarType().apply(this);
        print(" = ");
        node.getExp().apply(this);
    }

    public void caseAInlineListNoExpVarDecl(AInlineListNoExpVarDecl node) {
        print(node.getId().getText() + ", ");
        node.getVarDecl().apply(this);
    }

    public void caseAInlineListWithExpVarDecl(AInlineListWithExpVarDecl node) {
        print(node.getId().getText() + ", ");
        node.getVarDecl().apply(this);
        print(", ");
        node.getExp().apply(this);
    }

    public void caseAMultilineListVarDecl(AMultilineListVarDecl node) {
        println("(");
        indentLevel++;
        for (Node n: node.getVarDecl()) {
            printi("");
            n.apply(this);
            println("");
        }
        indentLevel--;
        printi(")");
    }

    //type declarations
    public void caseATypeDeclAstDecl(ATypeDeclAstDecl node) {
        print("type ");
        node.getTypeDecl().apply(this);
        //println("");
    }

    public void caseATypeAliasTypeDecl(ATypeAliasTypeDecl node) {
        print(node.getId().getText() + " ");
        node.getVarType().apply(this);
    } 

    public void caseATypeAliasBaseTypeDecl(ATypeAliasBaseTypeDecl node) {
        print(node.getType().getText() + " ");
        node.getVarType().apply(this);
    }

    public void caseATypeWithManyIdsTypeDecl(ATypeWithManyIdsTypeDecl node) {
        print(node.getId().getText() + ", ");
        node.getTypeDecl().apply(this);
    }

    public void caseAStructWithIdTypeDecl(AStructWithIdTypeDecl node) {
        print(node.getId().getText() + " struct {");
        println("");

        indentLevel++;
        for (Node n: node.getTypeDecl()) {
            printi("");
            n.apply(this);
            println("");
        }
        
        indentLevel--;
        printi("}");
    }

    public void caseAMultilineListTypeDecl(AMultilineListTypeDecl node) {
        println("(");
        indentLevel++;
        for (Node n: node.getTypeDecl()) {
            printi("");
            n.apply(this);
            println("");
        }
        indentLevel--;
        printi(" )");
    }

    /** STATEMENTS */
    public void caseAPrintStmt(APrintStmt node)
    {
        printi("print("); 
        if (node.getExp() != null) node.getExp().apply(this);
        print(")");
    }

    public void caseAPrintlnStmt(APrintlnStmt node)
    {
        printi("println(");
        if (node.getExp() != null) node.getExp().apply(this);
        print(")");
    }

    public void caseAReturnStmt(AReturnStmt node)
    {
        printi("return");
        if (node.getExp() != null) 
        {
            print(" ");
            node.getExp().apply(this);
        }
    }

    public void caseAIncrementStmt(AIncrementStmt node)
    {
        printi("");
        node.getExp().apply(this);
        print("++");
    }

    public void caseADecrementStmt(ADecrementStmt node)
    {
        printi("");
        node.getExp().apply(this);
        print("--");
    }

    public void caseADeclStmt(ADeclStmt node)
    {
        printi("");
        node.getDecl().apply(this);
    }

    public void caseAAssignStmt(AAssignStmt node)
    {

    }

    public void caseABlockStmt(ABlockStmt node)
    {
        println("{");
        indentLevel++;
        for (int i = 0; i < node.getStmt().size(); i++)
        {
            node.getStmt().get(i).apply(this);
            println(";");
        }
        indentLevel--;
        println("}");
    }

    /*public void caseAAssignStmt(AAssignStmt node)
    {
        node.getL().apply(this);
        printi(" = ");
        node.getR().apply(this);
        println(";");
    }

    public void caseAWhileStmt(AWhileStmt node)
    {
        printi("while ");
        node.getExp().apply(this);
        println(" do");

        // Print the statements in the while-loop
        indentLevel++;
        printNodes(node.getStmt());       
 
        indentLevel--;
        printi("done");
        println("");
    }*/

    // public void caseAIfStmt(AIfStmt node)
    // {
    //     printi("if ");
    //     node.getExp().apply(this);
    //     println(" then");

    //     indentLevel++;
    //     node.getBlock().apply(this);

    //     indentLevel--;
    //     if (node.getEnd() != null) node.getEnd().apply(this);

    //     println("endif");
    // }

    // public void caseAElseStmt(AElseStmt node)
    // {
    //     printi("else");
    //     println("");
 
    //     indentLevel++;
    //     node.getStmt().apply(this);

    //     indentLevel--;
    // }

    // // /** DECLARAIONS */
    // // public void caseAVarDecl(AVarDecl node)
    // // {
    // //     println("var " + node.getId().getText() + ":" +
    // //         node.getType().getText().trim() + ";");
    // // }
   
    // /** EXPRESSIONS */
    // public void caseAPlusExp(APlusExp node)
    // {
    //     print("(");
    //     node.getL().apply(this);
    //     print("+");
    //     node.getR().apply(this);
    //     print(")");
    // }

    // public void caseAMinusExp(AMinusExp node)
    // {
    //     print("(");
    //     node.getL().apply(this);
    //     print("-");
    //     node.getR().apply(this);
    //     print(")");
    // }

    // public void caseAMultExp(AMultExp node)
    // {
    //     print("(");
    //     node.getL().apply(this);
    //     print("*");
    //     node.getR().apply(this);
    //     print(")");
    // }

    // public void caseADivideExp(ADivideExp node)
    // {
    //     print("(");
    //     node.getL().apply(this);
    //     print("/");
    //     node.getR().apply(this);
    //     print(")");
    // }
 
    // // public void caseAUminusExp(AUminusExp node)
    // // {
    // //     print("(-");
    // //     node.getExp().apply(this);
    // //     print(")");
    // // }
 
    // public void caseAIdExp(AIdExp node)
    // {
    //     print(node.getId().getText());
    // }

    // public void caseAFloatExp(AFloatExp node)
    // {
    //     print(node.getFloat().getText());
    // }

    // public void caseAIntExp(AIntExp node)
    // {
    //     print(node.getInt().getText());
    // }
//     public void caseAIdExp(AIdExp node)
//     {
//         print(node.getId().getText());
//     }

//     public void caseAFloatExp(AFloatExp node)
//     {
//         print(node.getFloat().getText());
//     }

//     public void caseAIntExp(AIntExp node)
//     {
//         print(node.getInt().getText());
//     }

}
