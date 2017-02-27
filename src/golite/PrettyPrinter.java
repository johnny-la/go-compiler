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
        node.getPackageDecl().apply(this);
        
        for (int i = 0; i < node.getDecl().size(); i++)
        {
            Node decl = node.getDecl().get(i);
            decl.apply(this);
            // Don't print newlines/semicolons for function declarations
            if (!(decl instanceof AFuncDeclAstDecl)) println(";");
        }

        //println("");
        //printNodes(node.getStmt());
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

    //base case idTypes

    public void caseAIdIdType(AIdIdType node) {
        print(node.getId().getText());
    }

    public void caseATypeIdType(ATypeIdType node) {
        print(node.getType().getText());
    }

    //package declarations
    public void caseAPackageDecl(APackageDecl node) {
        print("package ");
        node.getIdType().apply(this);
        print("; \n");
    }
    //var declarations
    public void caseAVarDeclAstDecl(AVarDeclAstDecl node) {
        print("var ");
        node.getVarDecl().apply(this);

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
        print(" = ");
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
        print(")");
    }

    //type declarations
    public void caseATypeDeclAstDecl(ATypeDeclAstDecl node) {
        print("type ");
        node.getTypeDecl().apply(this);
        //println("");
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

        indentLevel++;
        for (Node n: node.getTypeDecl()) {
            printi("");
            n.apply(this);
            println("");
        }
        
        indentLevel--;
        print("}");
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
        print(" )");
    }

    //function declarations
    public void caseAFuncDeclAstDecl(AFuncDeclAstDecl node) {
        print("func ");
        node.getFuncDecl().apply(this);
        println("\n");
    }
    
    public void caseANoReturnFuncDecl(ANoReturnFuncDecl node) {
        node.getIdType().apply(this);
        print(" (");
        if (node.getSignature() != null) node.getSignature().apply(this);
        print(") ");
        node.getBlock().apply(this);
    }

    public void caseASingleReturnFuncDecl(ASingleReturnFuncDecl node) {
        node.getIdType().apply(this);
        print(" (");
        if (node.getSignature() != null) node.getSignature().apply(this);
        print(") ");
        node.getVarType().apply(this);
        print(" ");
        node.getBlock().apply(this);
    }

    //signature declarations
    public void caseASingleIdToTypeSignature(ASingleIdToTypeSignature node) {
        node.getIdType().apply(this);
        print(" ");
        node.getVarType().apply(this);
    }

    public void caseAManyIdToTypeSignature(AManyIdToTypeSignature node) {
        node.getIdType().apply(this);
        print(", ");
        node.getSignature().apply(this);
    }

    public void caseAMultipleTypesSignature(AMultipleTypesSignature node) {
        node.getRoot().apply(this);
        print(", ");
        node.getLeaf().apply(this);
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
        printi("}");
    }


    public void caseAIfStmt(AIfStmt node)
    {
        printi("if ");
        node.getExp().apply(this);
    
        node.getBlock().apply(this);

        if (node.getEnd() != null) node.getEnd().apply(this);
    }

    public void caseAElseIfStmt(AElseIfStmt node)
    {
        println(" else");
        node.getStmt().apply(this);
    }

    public void caseAElseStmt(AElseStmt node)
    {
        print(" else ");
 
        node.getStmt().apply(this);
    }

    public void caseAForStmt(AForStmt node)
    {
        printi("for ");
        node.getCondition().apply(this);
        print(" ");
        node.getBlock().apply(this);
    }

    public void caseAForCondExp(AForCondExp node)
    {
        if (node.getFirst() != null) node.getFirst().apply(this);
        print("; ");
        if (node.getSecond() != null) node.getSecond().apply(this);
        print("; ");
        if (node.getThird() != null) node.getThird().apply(this);
    }

    public void caseASwitchStmt(ASwitchStmt node)
    {
        printi("switch ");
        if (node.getSimpleStmt() != null) 
        { 
            node.getSimpleStmt().apply(this);

        }
        if (node.getExp() != null)
        {
            node.getExp().apply(this);
        }

        println("{");
        indentLevel++;
        for (int i = 0; i < node.getCaseStmts().size(); i++)
        {
            node.getCaseStmts().get(i).apply(this);
        }
        indentLevel--;
        printi("}");
    }

    public void caseACaseStmt(ACaseStmt node)
    {
        node.getCaseExp().apply(this);

        for (int i = 0; i < node.getStmtList().size(); i++)
        {
            printi("");
            node.getStmtList().get(i).apply(this);
            println(";");
        }
    }

    public void caseACaseExp(ACaseExp node)
    {
        printi("case ");
        node.getExpList().apply(this);
        println(":");
    }

    public void caseADefaultExp(ADefaultExp node)
    {
        printi("default:\n");
    }

    public void caseAContinueStmt(AContinueStmt node)
    {
        printi("continue");
    }

    public void caseABreakStmt(ABreakStmt node)
    {
        printi("break");
    }

    // // /** DECLARAIONS */
    // // public void caseAVarDecl(AVarDecl node)
    // // {
    // //     println("var " + node.getId().getText() + ":" +
    // //         node.getType().getText().trim() + ";");
    // // }
   
    /** EXPRESSIONS */

    public void caseAListExp(AListExp node)
    {
        node.getList().apply(this);
        print(",");
        node.getValue().apply(this);
    }

    public void caseAFunctionCallExp(AFunctionCallExp node)
    {
        print(node.getId().getText());
        print("(");
        node.getExp().apply(this);
        print(")");
    }

    public void caseAPlusExp(APlusExp node)
    {
        print("(");
        node.getL().apply(this);
        print("+");
        node.getR().apply(this);
        print(")");
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
        print("(");
        node.getL().apply(this);
        print("*");
        node.getR().apply(this);
        print(")");
    }

    public void caseADivideExp(ADivideExp node)
    {
        print("(");
        node.getL().apply(this);
        print("/");
        node.getR().apply(this);
        print(")");
    }

    public void caseAModuloExp(AModuloExp node)
    {
        print("(");
        node.getL().apply(this);
        print("%");
        node.getR().apply(this);
        print(")");
    }

    public void caseALogicalOrExp(ALogicalOrExp node)
    {
        print("(");
        node.getL().apply(this);
        print("||");
        node.getR().apply(this);
        print(")");
    }

    public void caseALogicalAndExp(ALogicalAndExp node)
    {
        print("(");
        node.getL().apply(this);
        print("&&");
        node.getR().apply(this);
        print(")");
    }

    public void caseAPipeExp(APipeExp node)
    {
        print("(");
        node.getL().apply(this);
        print("|");
        node.getR().apply(this);
        print(")");
    }

    public void caseACaretExp(ACaretExp node)
    {
        print("(");
        node.getL().apply(this);
        print("^");
        node.getR().apply(this);
        print(")");
    }


    public void caseAEqualsEqualsExp(AEqualsEqualsExp node)
    {
        print("(");
        node.getL().apply(this);
        print("==");
        node.getR().apply(this);
        print(")");
    }

    public void caseANotEqualExp(ANotEqualExp node)
    {
        print("(");
        node.getL().apply(this);
        print("!=");
        node.getR().apply(this);
        print(")");
    }

    public void caseALessExp(ALessExp node)
    {
        print("(");
        node.getL().apply(this);
        print("<");
        node.getR().apply(this);
        print(")");
    }

    public void caseAGreaterExp(AGreaterExp node)
    {
        print("(");
        node.getL().apply(this);
        print(">");
        node.getR().apply(this);
        print(")");
    }

    public void caseALessEqualsExp(ALessEqualsExp node)
    {
        print("(");
        node.getL().apply(this);
        print("<=");
        node.getR().apply(this);
        print(")");
    }

    public void caseAGreaterEqualsExp(AGreaterEqualsExp node)
    {
        print("(");
        node.getL().apply(this);
        print(">=");
        node.getR().apply(this);
        print(")");
    }

    public void caseAIdExp(AIdExp node)
    {
        print(node.getId().getText());
    }

    public void caseAFloat64LiteralExp(AFloat64LiteralExp node)
    {
        print(node.getFloat64Literal().getText());
    }

    public void caseAIntExp(AIntExp node)
    {
        print(node.getInt().getText());
    }

    public void caseAHexExp(AHexExp node)
    {
        print(node.getHex().getText());
    }

    public void caseAOctExp(AOctExp node)
    {
       print(node.getOct().getText()); 
    }

    public void caseAUnaryExclamationExp(AUnaryExclamationExp node)
    {
        print("(!");
        node.getExp().apply(this);
        print(")");
    }

    public void caseAUnaryMinusExp(AUnaryMinusExp node)
    {
        print("(-");
        node.getExp().apply(this);
        print(")");
    }
 
    public void caseAUnaryPlusExp(AUnaryPlusExp node)
    {
        print("(+");
        node.getExp().apply(this);
        print(")");
    }

    public void caseAUnaryXorExp(AUnaryXorExp node)
    {
        print("(^");
        node.getExp().apply(this);
        print(")");
    }

    public void caseACaretedFactorsExp(ACaretedFactorsExp node)
    {
        print("^");
        print("(");
        node.getExp().apply(this);
        print(")");
    }

    public void caseAExclamatedFactorsExp(AExclamatedFactorsExp node)
    {
        print("!");
        print("(");
        node.getExp().apply(this);
        print(")");
    }

    public void caseAAmpersandCaretExp(AAmpersandCaretExp node)
    {
        print("(");
        node.getL().apply(this);
        print("&^");
        node.getR().apply(this);
        print(")");
    }

    public void caseAAmpersandExp(AAmpersandExp node)
    {
        print("(");
        node.getL().apply(this);
        print("&");
        node.getR().apply(this);
        print(")"); 
    }

    public void caseAShiftLeftExp(AShiftLeftExp node)
    {
        print("(");
        node.getL().apply(this);
        print("<<");
        node.getR().apply(this);
        print(")");
    }

    public void caseAShiftRightExp(AShiftLeftExp node)
    {
        print("(");
        node.getL().apply(this);
        print(">>");
        node.getR().apply(this);
        print(")");
    }

    public void caseAAppendedExprExp(AAppendedExprExp node)
    {
        print("append(");
        node.getL().apply(this);
        print(", ");
        node.getR().apply(this);
        print(")");
    }

    public void caseARuneLiteralExp(ARuneLiteralExp node)
    {
       print(node.getRuneLiteral().getText()); 
    }

    public void caseARawStringLitExp(ARawStringLitExp node)
    {
       print(node.getRawStringLit().getText()); 
    }

    public void caseAInterpretedStringLiteralExp(AInterpretedStringLiteralExp node)
    {
       print(node.getInterpretedStringLiteral().getText()); 
    }

}
