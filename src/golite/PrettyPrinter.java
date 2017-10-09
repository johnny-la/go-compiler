package golite;

import golite.parser.*;
import golite.lexer.*;
import golite.node.*;
import golite.analysis.*;
import golite.type.*;
import java.util.*;

public class PrettyPrinter extends DepthFirstAdapter
{
    private static int indentLevel;
    private static StringBuffer output; 
    public static HashMap<Node, TypeClass> nodeTypes;
    public static boolean printType;
    private String leftBlock = "/*";
    private String rightBlock = "*/";
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

    // Pretty prints the list of nodes, separating each element by a comma
    private void printNodesWithComma(LinkedList<? extends Node> nodes)
    {
        for (int i = 0; i < nodes.size(); i++)
        {
            nodes.get(i).apply(this);
            if (i != nodes.size()-1) { print(","); }
        }
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
    }

    /** DECLARATIONS */

    public void caseATypeVarType(ATypeVarType node) 
    {
        print(node.getType().getText());
    }

    public void caseASliceVarType(ASliceVarType node) 
    {
        print("[]");
        node.getVarType().apply(this);
    }

    public void caseAArrayVarType(AArrayVarType node) 
    {
        print("[" + node.getInt().getText() + "]");
        node.getVarType().apply(this);
    }

    public void caseAStructVarType(AStructVarType node) 
    {
            println("struct {");
            indentLevel++;
            for (Node n : node.getInnerFields()) {
                    printi("");
                    n.apply(this);
                    println("");
            }
            print("}");
    }

    public void caseAIdVarType(AIdVarType node) 
    {
          print(node.getId().getText());
    }

    /** Field declarations */

    public void caseASingleInnerFields(ASingleInnerFields node) 
    {
        for (int i = 0; i < node.getIdType().size() - 1; i++) 
        {
            node.getIdType().get(i).apply(this);
            print(", ");
        }
        node.getIdType().get(node.getIdType().size() - 1).apply(this);
        node.getVarType().apply(this);
    }

    /** Base case idTypes */

    public void caseAIdIdType(AIdIdType node) 
    {
        print(node.getId().getText());
    }

    public void caseATypeIdType(ATypeIdType node) 
    {
        print(node.getType().getText());
    }

    // Package declarations
    public void caseAPackageDecl(APackageDecl node) 
    {
        print("package ");
        node.getIdType().apply(this);
        print("; \n");
    }

    // Variable declarations
    public void caseAVarDeclAstDecl(AVarDeclAstDecl node) 
    {
        print("var ");
        node.getVarDecl().apply(this);
    }

    public void caseAVarWithTypeVarDecl(AVarWithTypeVarDecl node) 
    {
        node.getIdType().apply(this);
        print(" ");
        node.getVarType().apply(this);
    }

    public void caseAVarWithOnlyExpVarDecl(AVarWithOnlyExpVarDecl node) 
    {
        node.getIdType().apply(this);
        print(" = ");
        node.getExp().apply(this);
    }

    public void caseAVarWithTypeAndExpVarDecl(AVarWithTypeAndExpVarDecl node) 
    {
        node.getIdType().apply(this);
        print(" ");
        node.getVarType().apply(this);
        print(" = ");
        node.getExp().apply(this);
    }

    public void caseAInlineListNoExpVarDecl(AInlineListNoExpVarDecl node) 
    {
        node.getIdType().apply(this);
        print(", ");
        node.getVarDecl().apply(this);
    }

    public void caseAInlineListWithExpVarDecl(AInlineListWithExpVarDecl node) 
    {
        node.getIdType().apply(this);
        print(", ");
        node.getVarDecl().apply(this);
        print(", ");
        node.getExp().apply(this);
    }

    public void caseAMultilineListVarDecl(AMultilineListVarDecl node) 
    {
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
    public void caseATypeDeclAstDecl(ATypeDeclAstDecl node) 
    {
        print("type ");
        node.getTypeDecl().apply(this);
        //println("");
    }

    public void caseATypeAliasTypeDecl(ATypeAliasTypeDecl node) 
    {
        node.getIdType().apply(this);
        print(" ");
        node.getVarType().apply(this);
    } 

    public void caseAMultilineListTypeDecl(AMultiTypeDecl node) 
    {
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

    //function declarations
    public void caseAFuncDeclAstDecl(AFuncDeclAstDecl node) 
    {
        print("func ");
        node.getFuncDecl().apply(this);
        println("\n");
    }
    
    public void caseANoReturnFuncDecl(ANoReturnFuncDecl node) 
    {
        node.getIdType().apply(this);
        print(" (");
        if (node.getSignature() != null) node.getSignature().apply(this);
        print(") ");
        node.getBlock().apply(this);
    }

    public void caseASingleReturnFuncDecl(ASingleReturnFuncDecl node) 
    {
        node.getIdType().apply(this);
        print(" (");
        if (node.getSignature() != null) node.getSignature().apply(this);
        print(") ");
        node.getVarType().apply(this);
        print(" ");
        node.getBlock().apply(this);
    }

    // Signature declarations
    public void caseAMultipleTypesSignature(AMultipleTypesSignature node)
    {
        printNodesWithComma(node.getIdList());
        print(" ");
        node.getVarType().apply(this);
        print(", ");
        node.getSignature().apply(this);
    }

    public void caseASingleTypeSignature(ASingleTypeSignature node)
    {
        printNodesWithComma(node.getIdList());
        print(" ");
        node.getVarType().apply(this);
    }

    /** STATEMENTS */

    public void caseAExpStmt(AExpStmt node)
    {
        print("(");
        node.getExp().apply(this);
        print(")");
    }

    public void caseAReturnStmt(AReturnStmt node)
    {
        print("return");
        if (node.getExp() != null) 
        {
            print(" ");
            node.getExp().apply(this);
        }
    }

    public void caseAIncrementStmt(AIncrementStmt node)
    {
        node.getExp().apply(this);
        print("++");
    }

    public void caseADecrementStmt(ADecrementStmt node)
    {
        node.getExp().apply(this);
        print("--");
    }

    public void caseADeclStmt(ADeclStmt node)
    {
        node.getDecl().apply(this);
    }

    public void caseAAssignListStmt(AAssignListStmt node)
    {
        printNodesWithComma(node.getL());
        node.getOp().apply(this);
        printNodesWithComma(node.getR()); 
    } 

    public void caseAEqualsExp(AEqualsExp node)
    {
        print("=");
    } 

    public void caseAColonEqualsExp(AColonEqualsExp node)
    {
        print(":=");
    }

    public void caseAOpEqualsExp(AOpEqualsExp node)
    {
        print(node.getOpEquals().getText());
    }

    private void printIndices(LinkedList<? extends Node> nodes)
    {
        for( int i = 0; i<nodes.size(); i++)
        {
            if (i != nodes.size()) 
            { 
                print("["); 
                nodes.get(i).apply(this);
                print("]");
            }
        }
    }

    public void caseABlockStmt(ABlockStmt node)
    {
        println("{");
        indentLevel++;
        for (int i = 0; i < node.getStmt().size(); i++)
        {
            printi("");
            node.getStmt().get(i).apply(this);
            println(";");
        }
        indentLevel--;
        printi("}");
    }


    public void caseAIfStmt(AIfStmt node)
    {
        print("if ");
        if (node.getSimpleStmt() != null)
        {
            node.getSimpleStmt().apply(this);
            print("; ");
        }
        node.getExp().apply(this);
    
        node.getBlock().apply(this);

        if (node.getEnd() != null) node.getEnd().apply(this);
    }

    public void caseAElseIfStmt(AElseIfStmt node)
    {
        println(" else");
        printi("");
        node.getStmt().apply(this);
    }

    public void caseAElseStmt(AElseStmt node)
    {
        print(" else ");
 
        node.getStmt().apply(this);
    }

    public void caseAForStmt(AForStmt node)
    {
        print("for ");
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
        print("switch ");
        if (node.getSimpleStmt() != null) 
        { 
            node.getSimpleStmt().apply(this);
            print("; ");
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
        indentLevel++;

        for (int i = 0; i < node.getStmtList().size(); i++)
        {
            printi("");
            node.getStmtList().get(i).apply(this);
            println(";");
        }

        indentLevel--;
    }

    public void caseACaseExp(ACaseExp node)
    {
        printi("case ");
        print("(");
        printNodesWithComma(node.getExpList());
        print(")");
        println(":");
    }

    public void caseADefaultExp(ADefaultExp node)
    {
        printi("default:\n");
    }

    public void caseAContinueStmt(AContinueStmt node)
    {
        print("continue");
    }

    public void caseABreakStmt(ABreakStmt node)
    {
        print("break");
    }

    /** EXPRESSIONS */

    public void caseAArrayElementExp(AArrayElementExp node)
    {
        print("(");
        node.getArray().apply(this);
        print("[");
        node.getIndex().apply(this);
        print("]");
        print(")");
    }


    public void caseAFieldExp(AFieldExp node)
    {
        print("(");
        node.getIdType().apply(this);
        print(").");
        node.getExp().apply(this);
    }

    public void caseAFunctionCallExp(AFunctionCallExp node)
    {
        node.getName().apply(this);
        print("(");
        printNodesWithComma(node.getArgs());
        print(")");
    }

    public void printWithType(Node node) 
    {
        if (printType && nodeTypes.containsKey(node)) 
        {
            print(leftBlock);
            print("" + nodeTypes.get(node));
            print(rightBlock);
        }
    }

    public void caseAPlusExp(APlusExp node)
    {
        print("(");
        node.getL().apply(this);
        print("+");
        node.getR().apply(this);
        print(")");
        printWithType(node);
    }

    public void caseAMinusExp(AMinusExp node)
    {
        print("(");
        node.getL().apply(this);
        print("-");
        node.getR().apply(this);
        print(")");
        printWithType(node);
    }

    public void caseAMultExp(AMultExp node)
    {
        print("(");
        node.getL().apply(this);
        print("*");
        node.getR().apply(this);
        print(")");
        printWithType(node);
    }

    public void caseADivideExp(ADivideExp node)
    {
        print("(");
        node.getL().apply(this);
        print("/");
        node.getR().apply(this);
        print(")");
        printWithType(node);
    }

    public void caseAModuloExp(AModuloExp node)
    {
        print("(");
        node.getL().apply(this);
        print("%");
        node.getR().apply(this);
        print(")");
        printWithType(node);
    }

    public void caseALogicalOrExp(ALogicalOrExp node)
    {
        print("(");
        node.getL().apply(this);
        print("||");
        node.getR().apply(this);
        print(")");
        printWithType(node);
    }

    public void caseALogicalAndExp(ALogicalAndExp node)
    {
        print("(");
        node.getL().apply(this);
        print("&&");
        node.getR().apply(this);
        print(")");
        printWithType(node);
    }

    public void caseAPipeExp(APipeExp node)
    {
        print("(");
        node.getL().apply(this);
        print("|");
        node.getR().apply(this);
        print(")");
        printWithType(node);
    }

    public void caseACaretExp(ACaretExp node)
    {
        print("(");
        node.getL().apply(this);
        print("^");
        node.getR().apply(this);
        print(")");
        printWithType(node);
    }

    public void caseAEqualsEqualsExp(AEqualsEqualsExp node)
    {
        print("(");
        node.getL().apply(this);
        print("==");
        node.getR().apply(this);
        print(")");
        printWithType(node);
    }

    public void caseANotEqualExp(ANotEqualExp node)
    {
        print("(");
        node.getL().apply(this);
        print("!=");
        node.getR().apply(this);
        print(")");
        printWithType(node);
    }

    public void caseALessExp(ALessExp node)
    {
        print("(");
        node.getL().apply(this);
        print("<");
        node.getR().apply(this);
        print(")");
        printWithType(node);
    }

    public void caseAGreaterExp(AGreaterExp node)
    {
        print("(");
        node.getL().apply(this);
        print(">");
        node.getR().apply(this);
        print(")");
        printWithType(node);
    }

    public void caseALessEqualsExp(ALessEqualsExp node)
    {
        print("(");
        node.getL().apply(this);
        print("<=");
        node.getR().apply(this);
        print(")");
        printWithType(node);
    }

    public void caseAGreaterEqualsExp(AGreaterEqualsExp node)
    {
        print("(");
        node.getL().apply(this);
        print(">=");
        node.getR().apply(this);
        print(")");
        printWithType(node);
    }

    public void caseAIdExp(AIdExp node)
    {
        node.getIdType().apply(this);
        printWithType(node);
    }

    public void caseAFloat64LiteralExp(AFloat64LiteralExp node)
    {
        print(node.getFloat64Literal().getText());
        printWithType(node);
    }

    public void caseAIntExp(AIntExp node)
    {
        print(node.getInt().getText());
        printWithType(node);
    }

    public void caseAHexExp(AHexExp node)
    {
        print(node.getHex().getText());
        printWithType(node);
    }

    public void caseAOctExp(AOctExp node)
    {
       print(node.getOct().getText()); 
       printWithType(node);
    }

    public void caseAUnaryMinusExp(AUnaryMinusExp node)
    {
        print("(-");
        node.getExp().apply(this);
        print(")");
        printWithType(node);
    }
 
    public void caseAUnaryPlusExp(AUnaryPlusExp node)
    {
        print("(+");
        node.getExp().apply(this);
        print(")");
        printWithType(node);
    }

    public void caseAUnaryXorExp(AUnaryXorExp node)
    {
        print("^");
        print("(");
        node.getExp().apply(this);
        print(")");
        printWithType(node);
    }

    public void caseAUnaryExclamationExp(AUnaryExclamationExp node)
    {
        print("!");
        print("(");
        node.getExp().apply(this);
        print(")");
        printWithType(node);
    }

    public void caseAAmpersandCaretExp(AAmpersandCaretExp node)
    {
        print("(");
        node.getL().apply(this);
        print("&^");
        node.getR().apply(this);
        print(")");
        printWithType(node);
    }

    public void caseAAmpersandExp(AAmpersandExp node)
    {
        print("(");
        node.getL().apply(this);
        print("&");
        node.getR().apply(this);
        print(")"); 
        printWithType(node);
    }

    public void caseAShiftLeftExp(AShiftLeftExp node)
    {
        print("(");
        node.getL().apply(this);
        print("<<");
        node.getR().apply(this);
        print(")");
        printWithType(node);
    }

    public void caseAShiftRightExp(AShiftRightExp node)
    {
        print("(");
        node.getL().apply(this);
        print(">>");
        node.getR().apply(this);
        print(")");
        printWithType(node);
    }

    public void caseAAppendedExprExp(AAppendedExprExp node)
    {
        print("append(");
        node.getL().apply(this);
        print(", ");
        node.getR().apply(this);
        print(")");
        printWithType(node);
    }

    public void caseARuneLiteralExp(ARuneLiteralExp node)
    {
       print(node.getRuneLiteral().getText()); 
       printWithType(node);
    }

    public void caseARawStringLitExp(ARawStringLitExp node)
    {
       print(node.getRawStringLit().getText()); 
       printWithType(node);
    }

    public void caseAInterpretedStringLiteralExp(AInterpretedStringLiteralExp node)
    {
       print(node.getInterpretedStringLiteral().getText()); 
       printWithType(node);
    }

    public void caseAPrintExp(APrintExp node) 
    {
        print("print(");
        printNodesWithComma(node.getExp());
        print(")");
    }

    public void caseAPrintlnExp(APrintlnExp node) 
    {
        print("println(");
        printNodesWithComma(node.getExp());
        print(")");
    }
}