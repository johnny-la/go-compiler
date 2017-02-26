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
        printNodes(node.getDecl());
        println("");
        printNodes(node.getStmt());
    }

    /** DECLARATIONS */
    //base case varTypes

    public void caseABaseTypeVarType(ABaseTypeVarType node) {
        printi(node.getType().getText());
    }

    public void caseASliceVarType(ASliceVarType node) {
        printi("[]");
        node.getVarType().apply(this);
    }

    public void caseAArrayVarType(AArrayVarType node) {
        printi("[" + node.getInt().getText() + "]");
        node.getVarType().apply(this);
    }

    public void caseAStructVarType(AStructVarType node) {
        printi(node.getId().getText());
    }

    //base case idTypes

    public void caseAIdIdType(AIdIdType node) {
        print(node.getId().getText());
    }

    public void caseATypeIdType(ATypeIdType node) {
        print(node.getType().getText());
    }

    //var declarations
    public void caseAVarDeclAstDecl(AVarDeclAstDecl node) {
        printi("var ");
        node.getVarDecl().apply(this);
        println("");

    }

    public void caseAVarWithTypeVarDecl(AVarWithTypeVarDecl node) {
        node.getIdType().apply(this);
        print(" ");
        node.getVarType().apply(this);
    }

    public void caseAVarWithOnlyExpVarDecl(AVarWithOnlyExpVarDecl node) {
        node.getIdType().apply(this);
        print(" = ");
        node.getExp().apply(this);
    }

    public void caseAVarWithTypeAndExpVarDecl(AVarWithTypeAndExpVarDecl node) {
        node.getIdType().apply(this);
        print(" ");
        node.getVarType().apply(this);
        printi(" = ");
        node.getExp().apply(this);
    }

    public void caseAInlineListNoExpVarDecl(AInlineListNoExpVarDecl node) {
        node.getIdType().apply(this);
        print(", ");
        node.getVarDecl().apply(this);
    }

    public void caseAInlineListWithExpVarDecl(AInlineListWithExpVarDecl node) {
        node.getIdType().apply(this);
        print(", ");
        node.getVarDecl().apply(this);
        printi(", ");
        node.getExp().apply(this);
    }

    public void caseAMultilineListVarDecl(AMultilineListVarDecl node) {
        printi("(");
        println("");
        for (Node n: node.getVarDecl()) {
            n.apply(this);
            println("");
        }
        printi(" )");
    }

    //type declarations
    public void caseATypeDeclAstDecl(ATypeDeclAstDecl node) {
        printi("type ");
        node.getTypeDecl().apply(this);
        println("");
    }

    public void caseATypeAliasTypeDecl(ATypeAliasTypeDecl node) {
        node.getIdType().apply(this);
        print(" ");
        node.getVarType().apply(this);
    } 

    public void caseATypeWithManyIdsTypeDecl(ATypeWithManyIdsTypeDecl node) {
        node.getIdType().apply(this);
        print(", ");
        node.getTypeDecl().apply(this);
    }

    public void caseAStructWithIdTypeDecl(AStructWithIdTypeDecl node) {
        node.getIdType().apply(this);
        print(" struct {");
        println("");
        for (Node n: node.getTypeDecl()) {
            n.apply(this);
            println("");
        }
        printi(" }");
    }

    public void caseAMultilineListTypeDecl(AMultilineListTypeDecl node) {
        printi("(");
        println("");
        for (Node n: node.getTypeDecl()) {
            n.apply(this);
            println("");
        }
        printi(" )");
    }

    //function declarations
    public void caseAFuncDeclAstDecl(ATypeDeclAstDecl node) {
        print("func ");
        node.getFuncDecl().apply(this);
        println("");
    }
    
    public void caseANoReturnFuncDecl(ANoReturnFuncDecl node) {
        node.getIdType().apply(this);
        print(" (");
        node.getSignature().apply(this);
        print(") ");
        node.getStmt().apply(this);
    }

    public caseASingleReturnFuncDecl(ASingleReturnFuncDecl node) {
        node.getIdType().apply(this);
        print(" (");
        node.getSignature().apply(this);
        print(") ");
        node.getVarType().apply(this);
        print(" ");
        node.getStmt().apply(this);
    }

    // /** STATEMENTS */
    // public void caseAReadStmt(AReadStmt node)
    // {
    //     printi("read " + node.getId().getText() + ";");
    //     println("");
    // }
//     /** PROGRAM */
//     public void caseAProgram(AProgram node)
//     {
//         printNodes(node.getDecl());
//         println("");
//         printNodes(node.getStmt());
//     }

//     /** STATEMENTS */
//     public void caseAReadStmt(AReadStmt node)
//     {
//         printi("read " + node.getId().getText() + ";");
//         println("");
//     }
  
    // public void caseAPrintStmt(APrintStmt node)
    // {
    //     printi("print "); 
    //     if (node.getExp() != null) node.getExp().apply(this);
    //     println(";");
    // }

    // public void caseAAssignStmt(AAssignStmt node)
    // {
    //     node.getL().apply(this);
    //     printi(" = ");
    //     node.getR().apply(this);
    //     println(";");
    // }

    // public void caseAWhileStmt(AWhileStmt node)
    // {
    //     printi("while ");
    //     node.getExp().apply(this);
    //     println(" do");

    //     // Print the statements in the while-loop
    //     indentLevel++;
    //     printNodes(node.getStmt());       
 
    //     indentLevel--;
    //     printi("done");
    //     println("");
    // }

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
