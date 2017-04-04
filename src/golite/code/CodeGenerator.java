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
    // * Structs are instantiated upon declaration
    // * Slices and arrays are instantiated as arraylists.
    //   slices start empty, arrays start with n values populated,
    //   where n is the size of the array (each element populated
    //   with that element's default value)

    private int indentLevel;
    private StringBuffer output; 

    private Node root;
    private TypeChecker typeChecker;

    public static HashMap<Node, TypeClass> nodeTypes;
    private HashSet<Node> newShortDeclarationVariables; // The lvalues that were declared in short declaration statements
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
                                RUNE_DEFAULT = "0",
                                BOOL_DEFAULT = "false",
                                STRING_DEFAULT = "\"\"";

    private static final String CLASS_MODIFIER = "class ",
                                ANONYMOUS_CLASS_NAME = "AnonymousClass";

    // The index of the next anonymous class
    private int anonymousClassIndex = 1;

    // The struct nodes which have already had their classes declared
    // Maps the struct node to its class text
    private HashMap<Node,String> declaredStructs = new HashMap<Node,String>();

    public CodeGenerator(Node root, HashMap<Node, TypeClass> nodeTypes, HashSet<Node> newShortDeclarationVariables)
    {
        this.root = root;
        this.nodeTypes = nodeTypes;
        this.newShortDeclarationVariables = newShortDeclarationVariables;
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
        output.append(getIndent());
        print(s);
    }

    private String getIndent()
    {
        String output = "";
        for (int i = 0; i < indentLevel; i++) { output += ("    "); }
        return output;
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
        //node.getPackageDecl().apply(this);

        // Print the file header        
        print(fileHeader);
        indentLevel++;
        
        for (int i = 0; i < node.getDecl().size(); i++)
        {
            Node decl = node.getDecl().get(i);
            if (decl instanceof AVarDeclAstDecl)
            {
                PVarDecl varDecl = ((AVarDeclAstDecl)decl).getVarDecl();
                if (isEmptyMultilineList(varDecl) || isBlankId(varDecl))
                    continue;
            }
            
            printi("");
            decl.apply(this);
            // Don't print newlines/semicolons for function declarations
            if (!(decl instanceof AFuncDeclAstDecl)) 
                println(";");
        }

        // Close the main class
        indentLevel--;
        println("\n}"); 

        // Print the classes
    }

    private boolean isEmptyMultilineList(PStmt node)
    {
        if (node instanceof ADeclStmt)
        {
            PDecl decl = ((ADeclStmt)node).getDecl();
            if (decl instanceof AVarDeclAstDecl)
            {
                return isEmptyMultilineList(((AVarDeclAstDecl)decl).getVarDecl());
            }
        }

        return false;
    }

    private boolean isEmptyMultilineList(PVarDecl node)
    {
        return node instanceof AMultilineListVarDecl 
            && ((AMultilineListVarDecl)node).getVarDecl().size() == 0;
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
        //print("int ");
        node.getVarDecl().apply(this);
        //println("");

    }

    // Prints the type of the node, followed by the name of the id
    private void declareVariable(PIdType node)
    {
        declareVariable(node, false);
    }

    private void declareVariable(PExp node, boolean printDefaultValue)
    {
        if (node instanceof AIdExp)
        {
            declareVariable(null, node, printDefaultValue);
        }
    }

    // Prints the type of the node, followed by the name of the id
    private void declareVariable(PIdType node, boolean printDefaultValue)
    {
        declareVariable(node, null, printDefaultValue);
    }

    // Prints the type of the node, followed by the name of the id
    private void declareVariable(PIdType node, PExp expNode, boolean printDefaultValue)
    {
        // Ignore blank ids
        if ((node != null && isBlankId(node)) || 
            (expNode != null && isBlankId(expNode))) 
        {   
            return; 
        }

        // System.out.println("Declaring variable: " + node);
        String typeName = (node != null)? getTypeName(node) : getTypeName(expNode);

        print(typeName + " ");
        if (node != null)
            node.apply(this);
        else if (expNode != null)
            expNode.apply(this);

        if (printDefaultValue)
        {
            String defaultValue = (node != null)? getDefaultValue(node) : getDefaultValue(expNode);
            print(" = " + defaultValue);
        }
    }

    public void caseAVarWithTypeVarDecl(AVarWithTypeVarDecl node) {
        declareVariable(node.getIdType(), true);
        //print(" ");
        //node.getVarType().apply(this);
    }

    public void caseAVarWithOnlyExpVarDecl(AVarWithOnlyExpVarDecl node) {
        if (isBlankId(node.getIdType())) { return; }
        
        declareVariable(node.getIdType());
        print(" = ");
        node.getExp().apply(this);
    }

    public void caseAVarWithTypeAndExpVarDecl(AVarWithTypeAndExpVarDecl node) {
        declareVariable(node.getIdType());
        //node.getVarType().apply(this);
        print(" = ");
        node.getExp().apply(this);
    }

    // Returns true if the node is a blank identifier
    private boolean isBlankId(PIdType node)
    {
        return getIdName(node).trim().equals("_");
    }

    // Returns true if the declaration has a blank identifier
    private boolean isBlankId(PVarDecl node)
    {
        // System.out.println(node + " type = " + node.getClass());
        return getIdName(node).trim().equals("_");
    }

    private boolean isBlankId(PStmt node)
    {
        if (node instanceof ADeclStmt)
        {
            PDecl decl = ((ADeclStmt)node).getDecl();
            if (decl instanceof AVarDeclAstDecl)
            {
                return isBlankId(((AVarDeclAstDecl)decl).getVarDecl());
            }
        }

        return false;
    }

    private boolean isBlankId(PExp node)
    {
        if (node instanceof AIdExp)
        {
            return isBlankId(((AIdExp)node).getIdType());
        }

        return false;
    }

    /**
     * Returns the name of the id_type node
     */
    private String getIdName(PIdType node)
    {
        String idName = null;

        if (node instanceof AIdIdType) { idName = ((AIdIdType)node).getId().getText(); }
        else if (node instanceof ATypeIdType) { idName = ((ATypeIdType)node).getType().getText(); }
    
        return idName;
    }

    /**
     * Returns the name of the varDecl node
     */ 
    private String getIdName(PVarDecl node)
    {
        String idName = "";

        if (node instanceof AVarWithTypeVarDecl) { idName = ((AVarWithTypeVarDecl)node).getIdType().toString(); }
        else if (node instanceof AVarWithOnlyExpVarDecl) { idName = ((AVarWithOnlyExpVarDecl)node).getIdType().toString(); }
        else if (node instanceof AVarWithTypeAndExpVarDecl) { idName = ((AVarWithTypeAndExpVarDecl)node).getIdType().toString(); }

        return idName;
    }

    /**
     * Returns the name of the expression if it is an ID
     */
    private String getIdName(PExp node)
    {
        if (node instanceof AIdExp)
        {
            return getIdName(((AIdExp)node).getIdType());
        }

        return "";
    }

    public void caseAInlineListNoExpVarDecl(AInlineListNoExpVarDecl node) {
        if (isBlankId(node.getIdType())) { return; }

        declareVariable(node.getIdType(),true);
        println(";");
        printi("");
        node.getVarDecl().apply(this);
    }

    public void caseAInlineListWithExpVarDecl(AInlineListWithExpVarDecl node) {
        // declareVariable(node.getIdType());
        // // node.getIdType().apply(this);
        // // print(", ");
        // // node.getVarDecl().apply(this);
        // // print(", ");
        // print(" = ");
        // node.getExp().apply(this);

        List<PIdType> leftArgs = new ArrayList<PIdType>();
        LinkedList<PExp> rightArgs = new LinkedList<PExp>();
        PVarDecl current = node;

        while (current instanceof AInlineListWithExpVarDecl) {
            AInlineListWithExpVarDecl temp = (AInlineListWithExpVarDecl) current;
            leftArgs.add(temp.getIdType());
            rightArgs.addFirst(temp.getExp());
            current = temp.getVarDecl();
        }

        //finished recursion
        if (current instanceof AVarWithOnlyExpVarDecl) {
            AVarWithOnlyExpVarDecl varDecl = (AVarWithOnlyExpVarDecl)current;
            leftArgs.add(varDecl.getIdType());
            rightArgs.addFirst(varDecl.getExp());
        } else if (current instanceof AVarWithTypeAndExpVarDecl) {
            AVarWithTypeAndExpVarDecl varDecl = (AVarWithTypeAndExpVarDecl)current;
            leftArgs.add(varDecl.getIdType());
            rightArgs.addFirst(varDecl.getExp());
        }

        int idsPrinted = 0;
        for (int i = 0; i < leftArgs.size(); i++) {
            // Skip blank ids
            if (isBlankId(leftArgs.get(i)))
                continue;

            if (idsPrinted != 0)
                println(";");

            if (idsPrinted != 0) { printi(""); }

            declareVariable(leftArgs.get(i));
            print(" = ");
            rightArgs.get(i).apply(this);

            idsPrinted++;
        }
    }

    public void caseAMultilineListVarDecl(AMultilineListVarDecl node) {
        //println("(");
        //indentLevel++;
        
        int idsPrinted = 0;
        // Print all the variable declarations in the multiline list
        for (int i = 0; i < node.getVarDecl().size(); i++) {
            // Ignore blank identifiers
            if (isBlankId(node.getVarDecl().get(i))) 
                continue; 

            Node varDecl = node.getVarDecl().get(i);
            if (idsPrinted != 0) 
            { 
                println(";");
                printi(""); 
            }
                
            varDecl.apply(this);

            idsPrinted++;
        }

        //if (idsPrinted > 0)
        //    println(";");
        //indentLevel--;
        //printi(")");
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

    public String getTypeName(Node node)
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

            switch (type.baseType)
            {
                case INT:
                    typeName += "Integer";
                    break;
                case FLOAT64:
                    typeName += "Double";
                    break;
                case BOOL:
                    typeName += "Boolean";
                    break;
                case RUNE:
                    typeName += "Character";
                    break;
                case STRING:
                    typeName += "String";
                    break;
                case STRUCT:
                    typeName += getStructName(node);
                    break;
                default:
                    ErrorManager.printError("CodeGenerator.getTypeName(): Invalid type: " + type.baseType);

            }

            for (int i = 0; i < type.totalArrayDimension.size(); i++)
                typeName += ">";
        }

        return typeName;
    }

    private String getStructName(Node node)
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
                String structName = typeAlias.getIdType().toString().trim();
                declareStruct(structName, node);
                return structName;
            }
        }
        else
        {
            System.out.println(node + " has an anonymous struct type: " + type.structNode);
        }

        return null;
    }

    // Declares a struct if it doens't yet exist
    private void declareStruct(String structName, Node node)
    {
        TypeClass type = nodeTypes.get(node);

        // Don't declare a struct that was already declared
        if (declaredStructs.containsKey(type.structNode))
        {
            return;
        }

        String classText = CLASS_MODIFIER;

        // The struct was created using a type alias
        if (structName != null)
        {
            classText += structName;
        }
        // The struct is an anonymous struct
        else
        {
            // Give the struct the next available name
            classText += ANONYMOUS_CLASS_NAME + anonymousClassIndex;
            anonymousClassIndex++;
        }

        int indentLevel = 0;
        classText += "{\n";
        indentLevel++;
        
        classText += "}\n";
    }

    /** STATEMENTS */
    public void caseAPrintExp(APrintExp node)
    {
        print("System.out.print("); 
        if (node.getExp() != null && node.getExp().size() > 0) 
        {
            print("\"\" + ");
            for (int i = 0; i < node.getExp().size(); i++)
            {
                TypeClass type = nodeTypes.get(node.getExp().get(i));
                if(type.baseType == Type.RUNE){
                    print("(int)");
                }
                node.getExp().get(i).apply(this);
                if (i != node.getExp().size()-1) { 
                    print(" + \"\" + "); 
                }
            }
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
            for (int i = 0; i < node.getExp().size(); i++)
            {
                TypeClass type = nodeTypes.get(node.getExp().get(i));
                if(type.baseType == Type.RUNE){
                    print("(int)");
                }
                node.getExp().get(i).apply(this);
                if (i != node.getExp().size()-1) { 
                    print(" + \"\" + "); 
                }
            }
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

    // Returns true if the given node is a function call or 
    // an append expression
    private boolean isFunctionCall(PExp node)
    {
        return node instanceof AFunctionCallExp || node instanceof AAppendedExprExp;
    }

    public void caseAAssignListStmt(AAssignListStmt node)
    {
        ArrayList<PExp> lvalues = new ArrayList<PExp>();
        ArrayList<PExp> expressions = new ArrayList<PExp>();
        // Mapping from expression ID name to index in expressions array 
        HashMap<String,ArrayList<Integer>> expressionIndexMap = new HashMap<String,ArrayList<Integer>>();
        HashMap<String,ArrayList<Integer>> lvalueIndexMap = new HashMap<String,ArrayList<Integer>>();

        for (int i = 0; i < node.getL().size(); i++)
        {
            PExp lvalue = node.getL().get(i);
            PExp expression = node.getR().get(i);
            if (isBlankId(lvalue)) 
            { 
                // If the RHS is a function call, evaluate it
                if (isFunctionCall(expression))
                {
                    // Insert an empty lvalue to ensure it won't be printed
                    lvalues.add(null);
                    expressions.add(expression);
                }
                continue; 
            }
            
            // Add the lvalue/expression to its corresponding list
            lvalues.add(lvalue);
            expressions.add(expression);
            // Keep track of the index of each lvalue
            if (!lvalueIndexMap.containsKey(lvalue.toString()))
                lvalueIndexMap.put(lvalue.toString(),new ArrayList<Integer>());
            lvalueIndexMap.get(lvalue.toString()).add(i);
            // Keep track of the index of each expression
            if (!expressionIndexMap.containsKey(expression.toString()))
                expressionIndexMap.put(expression.toString(),new ArrayList<Integer>());
            expressionIndexMap.get(expression.toString()).add(i);
        }

        System.out.println("Lvalue Index Map:");
        printIndexMap(lvalueIndexMap);
        System.out.println("Expression Index Map:");
        printIndexMap(expressionIndexMap);

        // Get all lvalues that are swapped
        HashSet<PExp> swappedExpressions = new HashSet<PExp>();
        HashSet<PExp> swappedLvalues = getSwappedLvalues(lvalues,expressions,expressionIndexMap,lvalueIndexMap,swappedExpressions);

        boolean newVariablesDeclared = false;
        // Declare new variables from short declarations
        for (int i = 0; i < lvalues.size(); i++)
        {
            PExp lvalue = lvalues.get(i);
            if (newShortDeclarationVariables.contains(lvalue))
            {
                if (newVariablesDeclared)
                    printi("");
                // String typeName = getTypeName(lvalue);
                // print(typeName + " ");
                // lvalue.apply(this);
                // print(" = ");
                // String defaultValue = getDefaultValue(lvalue);
                // println(defaultValue + ";");
                declareVariable(lvalue, true);
                println(";");

                newVariablesDeclared = true;
            }
        }
        if (newVariablesDeclared)
            printi("");

        // Create a new scope and create temporary variables to perform swaps
        if (swappedLvalues.size() != 0)
        {
            println("{");
            indentLevel++;

            int lvaluesPrinted = 0;
            // Print any temporary variables for swaps
            for (PExp lvalue : swappedLvalues)
            {
                String typeName = getTypeName(lvalue);
                // The name of the temporary variable
                String tempVariableName = lvalue.toString().trim() + "Temp";
                
                printi("");
                print(typeName + " " + tempVariableName + " = ");
                lvalue.apply(this);
                println(";");

                lvaluesPrinted++;
            }

            println("");
            printi("");
        }

        // Print the assignment statements
        for (int i = 0; i < lvalues.size(); i++)
        {
            PExp lvalue = lvalues.get(i);
            PExp expression = expressions.get(i);

            // The lvalue is a blank ID
            if (lvalue == null)
            {
                expression.apply(this);
            }
            // The lvalue is not a blank ID -- print it
            else
            {
                if (i != 0)
                    printi("");

                lvalue.apply(this);
                print(" = ");
                expression.apply(this);
                // Use temporary variables for swapping
                if (swappedExpressions.contains(expression))
                    print("Temp");
                
                //if (i != lvalues.size()-1)
                    println(";");
            }
        }

        // End the temporary block
        if (swappedLvalues.size() != 0)
        {
            indentLevel--;
            printiln("}");
        }

        // printNodesWithComma(node.getL());
        // node.getOp().apply(this);
        // printNodesWithComma(node.getR()); 
    } 

    // Returns a hashset of all lvalues that were swapped in the assignment list
    private HashSet<PExp> getSwappedLvalues(ArrayList<PExp> lvalues, ArrayList<PExp> expressions,
        HashMap<String,ArrayList<Integer>> expressionIndexMap,
        HashMap<String,ArrayList<Integer>> lvalueIndexMap,
        HashSet<PExp> swappedExpressions)
    {
        // Contains all lvalues that are swapped
        HashSet<PExp> swappedLvalues = new HashSet<PExp>();

        // Check for value swaps
        for (int i = 0; i < lvalues.size(); i++)
        {
            PExp lvalue = lvalues.get(i);
            PExp expression = expressions.get(i);

            // If the lvalue is an expression on the LHS
            if (expressionIndexMap.containsKey(lvalue.toString()))
            {
                //System.out.println("expressionIndexMap contains lvalue:" + lvalue);
                // Find the corresponding expression on the RHS
                ArrayList<Integer> matchingExpressionIndices = expressionIndexMap.get(lvalue.toString());
                for (int j = 0; j < matchingExpressionIndices.size(); j++)
                {
                    int lvalueRhsIndex = matchingExpressionIndices.get(j);
                    PExp lvalueLhs = lvalues.get(lvalueRhsIndex);
                    if (lvalueLhs != lvalue)
                    {
                        if (expressionIndexMap.containsKey(lvalueLhs.toString()))
                        {
                            ArrayList<Integer> matchingExpressionRhsIndices = expressionIndexMap.get(lvalueLhs.toString());
                            for (int k = 0; k < matchingExpressionRhsIndices.size(); k++)
                            {
                                int expressionRhsIndex = matchingExpressionRhsIndices.get(k);
                                // The current lvalue is being swapped with another lvalue
                                if (expressionRhsIndex == i)
                                {
                                    swappedLvalues.add(lvalue);
                                    swappedExpressions.add(expression);
                                    System.out.println(lvalue + " is being swapped with " + lvalueLhs);
                                }
                            }
                        }
                    }
                }
            }
        }

        return swappedLvalues;
    }

    /** 
     * Returns the default value for the node's type
     */
    private String getDefaultValue(Node node)
    {
        TypeClass type = nodeTypes.get(node);
        String defaultValue = "";

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
                    return INT_DEFAULT;
                case FLOAT64:
                    return FLOAT_DEFAULT;
                case BOOL:
                    return BOOL_DEFAULT;
                case RUNE:
                    return RUNE_DEFAULT;
                case STRING:
                    return STRING_DEFAULT;
                case STRUCT:
                    return getStructName(node);
                default:
                    ErrorManager.printError("CodeGenerator.getTypeName(): Invalid type: " + type);
            }
        }
        // The node is an array
        else
        {
            defaultValue += "new ";
            for (int i = 0; i < type.totalArrayDimension.size(); i++)
                defaultValue += "ArrayList<";

            switch (type.baseType)
            {
                case INT:
                    defaultValue += "Integer";
                    break;
                case FLOAT64:
                    defaultValue += "Double";
                    break;
                case BOOL:
                    defaultValue += "Boolean";
                    break;
                case RUNE:
                    defaultValue += "Character";
                    break;
                case STRING:
                    defaultValue += "String";
                    break;
                case STRUCT:
                    defaultValue += getStructName(node);
                    break;
                default:
                    ErrorManager.printError("CodeGenerator.getdefaultValue(): Invalid type: " + type.baseType);

            }

            for (int i = 0; i < type.totalArrayDimension.size(); i++)
                defaultValue += ">";

            defaultValue += "()";

            /*if (type.totalArrayDimension.size() > 1)
            {
                defaultValue += ";\n";
                defaultValue += getIndent();
            }

            //String idName = getIdName(node);

            for (int i = 0; i < type.totalArrayDimension.size(); i++)
            {
                Dimension dimension = type.totalArrayDimension.get(i);
                if (dimension.isArray)
                {
                    for (int j = 0; j < dimension.size; j++)
                    {

                    }
                }
            }*/
        }

        return defaultValue;
    }

    private void printIndexMap(HashMap<String,ArrayList<Integer>> indexMap)
    {
        for (Map.Entry<String,ArrayList<Integer>> entries : indexMap.entrySet())
        {
            System.out.print(entries.getKey() + ":");
            for (int i = 0; i < entries.getValue().size(); i++)
            {
                System.out.print(entries.getValue().get(i) + ",");
            }
            System.out.println();
        }
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
		// print("(");
		node.getArray().apply(this);
		print(".get(");
		node.getIndex().apply(this);
		print(")");
		// print(")");
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
            PStmt stmt = node.getStmt().get(i);
            if (isBlankId(stmt)) { continue; }

            printi("");
            stmt.apply(this);
            // Print a semicolon after every non-control statement (not if/for/switch stmts)
            if (!isControlStatement(stmt) && !isEmptyMultilineList(stmt) && !isAssignList(stmt)) 
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
    
    // Returns true if this node is an assignment list statement
    private boolean isAssignList(Node node)
    {
        return node instanceof AAssignListStmt;
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
        TypeClass type = nodeTypes.get(node.getName());
        // This is a type cast
        if (type.functionSignature == null)
        {
            String typeName = getTypeName(node.getName());
            print("(" + typeName + ")");
        }
        // This is a function call
        else
        {
            node.getName().apply(this);
        }
        
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
        node.getL().apply(this);
        print(".add(");
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
       String newString = "";
       for(char c: strValueWithoutRawQuotes.toCharArray()){
        if(c == '\\'){
            newString += "\\";
        }
         newString += c;
       }
       print("\"" + newString + "\""); 
       printWithType(node);
    }

    public void caseAInterpretedStringLiteralExp(AInterpretedStringLiteralExp node)
    {
       print(node.getInterpretedStringLiteral().getText()); 
       printWithType(node);
    }

}
