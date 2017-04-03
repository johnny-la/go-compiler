package golite.code;

import golite.parser.*;
import golite.lexer.*;
import golite.node.*;
import golite.analysis.*;
import golite.code.*;
import golite.type.*;
import java.math.*;
import golite.*;
import java.security.spec.EllipticCurve;
import java.util.*;

public class CodeGenerator extends DepthFirstAdapter
{
    // TODO:
    // * Print for-loop init statement
    //   in code block above for-loop
    //   (Also: print post-for-loop statement at end
    //   of loop body?) 
    // * Print characters as integers
    // * Handle array capacity (var x [3]int)

    private int indentLevel;
    private StringBuffer output; 

    private Node root;
    private TypeChecker typeChecker;

    public static HashMap<Node, TypeClass> nodeTypes;
    public static boolean printType;
    private String leftBlock = "/*";
    private String rightBlock = "*/";

    // The string buffer used for concatenating strings
    private String stringBuffer = "buffer";

    private static final String fileHeader = 
        "import java.util.*;\n" +
        "\n" +
        "public class Main {\n";

    private static final int STRING_SIZE = 1024;

    private static final String STRING_TYPE = "char",
                                STRING_ARRAY_SIZE = "[" + STRING_SIZE + "]";

    private static final String INT_DEFAULT = "0",
                                FLOAT_DEFAULT = "0.0",
                                STRING_DEFAULT = "\"\"";

    public CodeGenerator(Node root, HashMap<Node, TypeClass> nodeTypes)
    {
        this.root = root;
        this.nodeTypes = nodeTypes;
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

    // Pretty prints the list of nodes, separating each element by the given delimiter
    private void printNodes(LinkedList<? extends Node> nodes, String delimiter)
    {
        for (int i = 0; i < nodes.size(); i++)
        {
            nodes.get(i).apply(this);
            if (i != nodes.size()-1) { print(delimiter); }
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
            printi("");
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

    // public void caseABaseTypeVarType(ABaseTypeVarType node) {
    //     print(node.getType().getText());
    // }

    public void caseASliceVarType(ASliceVarType node) {
        print("[]");
        node.getVarType().apply(this);
    }

    public void caseAArrayVarType(AArrayVarType node) {
        print("[" + node.getInt().getText() + "]");
        node.getVarType().apply(this);
    }

    public void caseAStructVarType(AStructVarType node) {
    		println("struct {");
    		indentLevel++;
    		for (Node n : node.getInnerFields()) {
    				printi("");
    				n.apply(this);
    				println("");
    		}
    		print("}");
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
        print("int ");
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
        //node.getIdType().apply(this);
        //print(" ");
        //node.getVarType().apply(this);

        TypeClass type = nodeTypes.get(node.getIdType());

        if (type.baseType == Type.STRUCT && type.typeAliases.size() == 0)
        {
            // TODO: Create the class
            System.out.println("First time encountering struct: " + node.getIdType() + ". Creating the class");
        }
    } 

    // public void caseAStructVarDeclTypeDecl(AStructVarDeclTypeDecl node)
    // {
    //     printNodesWithComma(node.getIdList());
    //     print(" ");
    //     node.getVarType().apply(this);
    // }
    
    // public void caseAStructWithIdTypeDecl(AStructWithIdTypeDecl node) {
    //     node.getIdType().apply(this);
    //     print(" struct {");
    //     println("");

    //     indentLevel++;
    //     for (Node n: node.getTypeDecl()) {
    //         printi("");
    //         n.apply(this);
    //         println("");
    //     }
        
    //     indentLevel--;
    //     printi("}");
    // }

    // public void caseAMultilineListTypeDecl(AMultilineListTypeDecl node) {
    //     println("(");
    //     indentLevel++;
    //     for (Node n: node.getTypeDecl()) {
    //         printi("");
    //         n.apply(this);
    //         println("");
    //     }
    //     indentLevel--;
    //     printi(" )");
    // }

    //function declarations
    public void caseAFuncDeclAstDecl(AFuncDeclAstDecl node) {
        print("public static ");
        node.getFuncDecl().apply(this);
        println("\n");
    }
    
    public void caseANoReturnFuncDecl(ANoReturnFuncDecl node) {
        print("void ");
        node.getIdType().apply(this);
        print("(");
        if (node.getSignature() != null) node.getSignature().apply(this);
        print(") ");
        node.getBlock().apply(this);
    }

    public void caseASingleReturnFuncDecl(ASingleReturnFuncDecl node) {
        // Return type
        node.getVarType().apply(this);
        print(" ");
        // Function name
        node.getIdType().apply(this);
        // Signature
        print("(");
        if (node.getSignature() != null) node.getSignature().apply(this);
        print(") ");
        node.getBlock().apply(this);
    }

    //signature declarations
    public void caseAMultipleTypesSignature(AMultipleTypesSignature node)
    {
        printMethodParameters(node.getIdList());
        //print("");
        //node.getVarType().apply(this);
        print(", ");
        node.getSignature().apply(this);
    }

    public void caseASingleTypeSignature(ASingleTypeSignature node)
    {
        printMethodParameters(node.getIdList());
        //print(" ");
        //node.getVarType().apply(this);
    }

    private void printMethodParameters(LinkedList<PIdType> parameters)
    {
        if (parameters == null) { return; }

        for (int i = 0; i < parameters.size(); i++)
        {
            PIdType parameter = parameters.get(i);
            
            String typeName = getTypeName(parameter);
            print(typeName + " ");
            parameter.apply(this);
            if (i != parameters.size()-1) { print(", "); }
        }
    }

    public String getTypeName(PIdType node)
    {
        TypeClass type = nodeTypes.get(node);
        String typeName = "";

        if (type == null) 
        {
            ErrorManager.printWarning("Node has null type: " + node);
            return "";
        }
        
        if (type.totalArrayDimension.size() <= 0)
        {
            switch (type.baseType)
            {
                case INT:
                    return "int";
                case FLOAT64:
                    return "double";
                case BOOL:
                    return "boolean";
                case RUNE:
                    return "char";
                case STRING:
                    return "String";
                case STRUCT:
                    return getStructName(node);
                default:
                    ErrorManager.printError("CodeGenerator.getTypeName(): Invalid type: " + type);
            }
        }
        // The node is an array
        else
        {
            for (int i = 0; i < type.totalArrayDimension.size(); i++)
            typeName += "ArrayList<";
            for (int i = 0; i < type.totalArrayDimension.size(); i++)
                typeName += ">";

            switch (type.baseType)
            {
                case INT:
                    typeName += "Integer";
                case FLOAT64:
                    typeName += "Double";
                case BOOL:
                    typeName += "Boolean";
                case RUNE:
                    typeName += "Character";
                case STRING:
                    typeName += "String";
                case STRUCT:
                    return getStructName(node);
                default:
                    ErrorManager.printError("CodeGenerator.getTypeName(): Invalid type: " + type);

            }
        }

        return typeName;
    }

    private String getStructName(PIdType node)
    {
        TypeClass type = nodeTypes.get(node);
        
        if (type.baseType != Type.STRUCT) { return null; }

        // If the struct was declared using a type alias
        if (type.typeAliases.size() > 0)
        {
            // Index 0 stores the first type alias of the struct 
            TypeAlias structAlias = type.typeAliases.get(0);

            System.out.println(node + " has type alias: " + structAlias + "[" + type.structNode + ", " + structAlias.node.getClass() + "]");

            if (structAlias.node instanceof ATypeAliasTypeDecl)
            {
                ATypeAliasTypeDecl typeAlias = (ATypeAliasTypeDecl)structAlias.node;
                return typeAlias.getIdType().toString().trim();
            }
        }
        else
        {
            System.out.println(node + " has an anonymous struct type: " + type.structNode);
        }

        return null;
    }

    /** STATEMENTS */
    public void caseAPrintExp(APrintExp node)
    {
        print("System.out.print("); 
        if (node.getExp() != null && node.getExp().size() > 0) 
        {
            print("\"\" + ");
            printNodes(node.getExp(), " + \"\" + ");
        }
        else
        {
            // System.out.print() requires at least one argument
            print("\"\"");
        }
        print(")");
    }

    public void caseAPrintlnExp(APrintlnExp node)
    {
        print("System.out.println(");
        if (node.getExp() != null && node.getExp().size() > 0) 
        {
            print("\"\" + ");
            printNodes(node.getExp(), " + \" \" + ");
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

    public void caseAArrayElementExp(AArrayElementExp node){
		print("(");
		node.getArray().apply(this);
		print("[");
		node.getIndex().apply(this);
		print("]");
		print(")");
	}

    public void caseAFieldExp(AFieldExp node) {
		print("(");
		node.getIdType().apply(this);
		print(").");
		node.getExp().apply(this);
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
        println(" else {");

        indentLevel++;
        printi("");

        node.getStmt().apply(this);

        indentLevel--;
        println("");
        printi("}");
    }

    public void caseAElseStmt(AElseStmt node)
    {
        print(" else ");
 
        node.getStmt().apply(this);
    }

    public void caseAForStmt(AForStmt node)
    {
        PExp forCondition = node.getCondition();
        // For loop
        if (forCondition instanceof AForCondExp)
        {
            println("{");
            indentLevel++;

            printi("for (");
            forCondition.apply(this);
            print(") ");
        }
        // Infinite loop
        else if (forCondition instanceof AEmptyExp)
        {
            print("while (true) ");
        }
        // While loop
        else 
        {
            print("while (");
            forCondition.apply(this);
            print(") ");
        }

        node.getBlock().apply(this);

        if (forCondition instanceof AForCondExp)
        {
            indentLevel--;
            println("");
            printiln("}");
        }
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
        // Anonymous code block for init statement
        println("{");
        indentLevel++;

        if (node.getSimpleStmt() != null)
        {
            printi("");
            node.getSimpleStmt().apply(this);
            println("; ");
        }

        PExp switchExp = node.getExp();
        // Print the default stmt last
        ACaseStmt defaultStmt = null;
        int caseIndex = 0;

        for (int i = 0; i < node.getCaseStmts().size(); i++)
        {
            ACaseStmt caseStmt = (ACaseStmt)node.getCaseStmts().get(i);
            
            // Don't print the default statement until the end
            if (caseStmt.getCaseExp() instanceof ADefaultExp)
            {
                defaultStmt = caseStmt;
                continue;
            }

            caseACaseStmt(caseStmt, switchExp, caseIndex);
            caseIndex++;
        }

        // Print the default statement last
        if (defaultStmt != null)
        {
            caseACaseStmt(defaultStmt, switchExp, caseIndex);            
        }

        // End anonymous code block
        println("");
        indentLevel--;
        printi("}");
    }

    public void caseACaseStmt(ACaseStmt node, PExp switchExp, int caseIndex)
    {
        if (node.getCaseExp() instanceof ACaseExp)
        {
            if (caseIndex > 0) { printi("else if "); }
            else { printi("if "); }

            print("(");
            caseACaseExp((ACaseExp)node.getCaseExp(), switchExp);
            println(") {");
        }
        else if (node.getCaseExp() instanceof ADefaultExp)
        {
            //node.getCaseExp().apply(this);
            printiln("else {");
        }

        indentLevel++;
        for (int i = 0; i < node.getStmtList().size(); i++)
        {
            PStmt stmt = node.getStmtList().get(i);
            // Stop printing statements once a break is found
            if (stmt instanceof ABreakStmt) 
            {
                //println(""); 
                break; 
            }
            printi("");
            stmt.apply(this);
            println(";");
        }

        indentLevel--;
        printiln("}");
    }

    public void caseACaseExp(ACaseExp node, PExp switchExp)
    {
        for (int i = 0; i < node.getExpList().size(); i++)
        {
            PExp exp = node.getExpList().get(i);

            // Take a disjunction of all expressions
            if (i > 0) { print(" || "); }

            if (switchExp != null)
            {
                switchExp.apply(this);
                print(" == ");
            }
            exp.apply(this);
        }
    }

    public void caseAContinueStmt(AContinueStmt node)
    {
        print("continue");
    }

    public void caseABreakStmt(ABreakStmt node)
    {
        print("break");
    }

    public void printWithType(Node node) {
        if (printType) {
            if (nodeTypes.containsKey(node)) {
                print(leftBlock);
                print("" + nodeTypes.get(node));
                print(rightBlock);
            }
        }
    }

    // EXPRESSIONS

    public void caseAFunctionCallExp(AFunctionCallExp node)
    {
        node.getName().apply(this);
        print("(");
        printNodesWithComma(node.getArgs());
        print(")");
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
        String[] splittedHex = node.getHex().getText().split("x");
        Integer integerValue = Integer.parseInt(splittedHex[1], 16);  
        print(integerValue.toString());
        printWithType(node);
    }

    public void caseAOctExp(AOctExp node)
    {   
       String octSuffix = node.getOct().getText().substring(1);
       Integer integerValue = Integer.parseInt(octSuffix, 8);   
       print(integerValue.toString()); 
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
        print("~");
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
        print("& ~(");
        node.getR().apply(this);
        print("))");
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
       String strValue = node.getRawStringLit().getText();
       String strValueWithoutRawQuotes = strValue.substring(1,strValue.length()-1);
       print("\"" + strValueWithoutRawQuotes + "\""); 
       printWithType(node);
    }

    public void caseAInterpretedStringLiteralExp(AInterpretedStringLiteralExp node)
    {
       print(node.getInterpretedStringLiteral().getText()); 
       printWithType(node);
    }

}
