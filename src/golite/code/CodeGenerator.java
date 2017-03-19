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

    public static HashMap<Node, TypeClass> nodeTypes;
    public static boolean printType;
    private String leftBlock = "/*";
    private String rightBlock = "*/";

    private static final int STRING_SIZE = 1024;

    private static final String STRING_TYPE = "char",
                                STRING_ARRAY_SIZE = "[" + STRING_SIZE + "]";

    private static final String INT_DEFAULT = "0",
                                FLOAT_DEFAULT = "0.0",
                                STRING_DEFAULT = "\"\"";

    // The string buffer used for concatenating strings
    private String stringBuffer = "buffer";

    private static final String fileHeader = 
        "import java.util.*;\n" +
        "\n" +
        "public class Main {\n";

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
            case FLOAT64:
                return "%f";
            case STRING:
                return "%s";
            default:
                return "INVALID_TYPE";
        }
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

        // Print the file header        
        print(fileHeader);
        indentLevel++;
        
        for (int i = 0; i < node.getDecl().size(); i++)
        {
            Node decl = node.getDecl().get(i);
            decl.apply(this);
            // Don't print newlines/semicolons for function declarations
            if (!(decl instanceof AFuncDeclAstDecl)) println(";");
        }

        // Close the main class
        indentLevel--;
        println("}"); 

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
        println("package ");
        node.getIdType().apply(this);
        print("; \n");
    }
    //var declarations
    public void caseAVarDeclAstDecl(AVarDeclAstDecl node) {
        printi("var ");
        node.getVarDecl().apply(this);
        //println("");

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
        printi(")");
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

    public void caseAStructVarDeclTypeDecl(AStructVarDeclTypeDecl node)
    {
        printNodesWithComma(node.getIdList());
        print(" ");
        node.getVarType().apply(this);
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

    //function declarations
    public void caseAFuncDeclAstDecl(AFuncDeclAstDecl node) {
        printi("func ");
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
    public void caseAPrintStmt(APrintStmt node)
    {
        print("print("); 
        if (node.getExp() != null) 
        {
            printNodesWithComma(node.getExp());
        }
        print(")");
    }

    public void caseAPrintlnStmt(APrintlnStmt node)
    {
        print("println(");
        if (node.getExp() != null) 
        {
            printNodesWithComma(node.getExp());
        }
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

    private void printIndices(LinkedList<? extends Node> nodes){
        for( int i = 0; i<nodes.size(); i++){
            if (i != nodes.size()) { 
                print("["); 
                nodes.get(i).apply(this);
                print("]");
            }
        }
    }

    public void caseAArrayIndexingExp(AArrayIndexingExp node)
    {
        node.getExp().apply(this);
        printIndices(node.getExpList());
    }

    public void caseAStructSelectorExp(AStructSelectorExp node)
    {
        node.getL().apply(this);
        print(".");
        node.getR().apply(this);
    }

    public void caseABlockStmt(ABlockStmt node)
    {
        println("{");
        indentLevel++;
        for (int i = 0; i < node.getStmt().size(); i++)
        {
            printi("");
            node.getStmt().get(i).apply(this);
            // Print a semicolon after every non-control statement (not if/for/switch stmts)
            if (!isControlStatement(node.getStmt().get(i))) 
                println(";");
            else
                println("");
        }
        indentLevel--;
        printi("}");
    }

    // Returns true if the node is an if/switch/for statement
    private boolean isControlStatement(Node node)
    {
        return (node instanceof AIfStmt || node instanceof ASwitchStmt || 
                node instanceof AForStmt);
    }

    public void caseAIfStmt(AIfStmt node)
    {
        // Anonymous code block for init statement
        println("{");
        indentLevel++;

        if (node.getSimpleStmt() != null)
        {
            printi("");
            node.getSimpleStmt().apply(this);
            println("; ");
        }

        printi("if (");
        node.getExp().apply(this);
        print(") ");
    
        node.getBlock().apply(this);

        if (node.getEnd() != null) node.getEnd().apply(this);

        // End anonymous code block
        println("");
        indentLevel--;
        printi("}");
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
        printNodesWithComma(node.getExpList());
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

    public void caseAFunctionCallSecondaryExp(AFunctionCallSecondaryExp node)
    {
        node.getExp().apply(this);
        print("(");
        printNodesWithComma(node.getExpList());
        print(")");
    }
    //TODO: add function call secondary

    public void printWithType(Node node) {
        if (printType) {
            if (nodeTypes.containsKey(node)) {
                print(leftBlock);
                print("" + nodeTypes.get(node));
                print(rightBlock);
            }
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

    public void caseACaretedFactorsExp(ACaretedFactorsExp node)
    {
        print("^");
        print("(");
        node.getExp().apply(this);
        print(")");
        printWithType(node);
    }

    public void caseAExclamatedFactorsExp(AExclamatedFactorsExp node)
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

    public void caseAShiftRightExp(AShiftLeftExp node)
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

}
