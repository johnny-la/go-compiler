package golite.code;

import golite.parser.*;
import golite.lexer.*;
import golite.node.*;
import golite.analysis.*;
import golite.code.*;
import golite.type.*;
import java.math.*;
import golite.symbol.*;
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
    // * Print fields as "static"
    // * Add "String[] args" to main method
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
    //structs
    private SemanticAnalyzer semanticAnalyzer;

    public static HashMap<Node, TypeClass> nodeTypes;
    private HashSet<Node> newShortDeclarationVariables; // The lvalues that were declared in short declaration statements
    public static boolean printType;
    private String leftBlock = "/*";
    private String rightBlock = "*/";
    private boolean inFunction = false;
    private boolean inATypeDecl = false;
    private boolean isInnerStruct = false;
    private int levels = 0;
    private String topLevelName = "";

    // The string buffer used for concatenating strings
    private String stringBuffer = "buffer";

    // File name
    private String fileHeader = "";

    private static final String fileFooter =
        	"    public static <T> T _get_(ArrayList<T> list, int index, boolean isArray, int maxSize, T defaultValue) {\n" +
		    "        _ensureCapacity_(list,isArray,maxSize,defaultValue);\n" +
		    "        return list.get(index);\n" +
	        "    }\n" +
	        "\n" + 
            "    public static <T> void _set_(ArrayList<T> list, int index, T data, boolean isArray, int maxSize, T defaultValue) {\n" +
		    "        _ensureCapacity_(list,isArray,maxSize,defaultValue);\n" +
            "        list.set(index, data);\n" +
            "    }\n" +
            "\n" +
            "    @SuppressWarnings(\"unchecked\")\n" +
            "    public static <T> void _ensureCapacity_(ArrayList<T> list, boolean isArray, int maxSize, T defaultValue) {\n" +
	        "        if (isArray) {\n" +
			"            for (int i = list.size(); i < maxSize; i++) {\n" +
			"                try {\n" +
	        "                    T value = null;\n" +
		    "                    if (isPrimitive(defaultValue))\n" +
			"                        value = defaultValue;\n" +
			"                    else\n" +
			"                        value = (T)defaultValue.getClass().newInstance();\n" +
            "                    list.add(value);\n" +
		    "                } catch (Exception e) {\n" +
			"                    throw new RuntimeException(e);\n" +
			"                }\n" +
			"            }\n" +
		    "        }\n" +
	        "    }\n" +
            "\n" +
            "    public static <T> boolean isPrimitive(T value) {\n" +
	        "        if (value.getClass().equals(Integer.class)\n" +
			"            || value.getClass().equals(Double.class)\n" +
			"            || value.getClass().equals(Character.class)\n" +
			"            || value.getClass().equals(Boolean.class)\n" +
		    "            || value.getClass().equals(String.class))\n" +
		    "            return true;\n" +
            "\n" +
		    "        return false;\n" +
	        "    }\n";
	
	public static <T> boolean isPrimitive(T value)
	{
		if (value.getClass().equals(Integer.class) 
			|| value.getClass().equals(Double.class)
			|| value.getClass().equals(Character.class)
			|| value.getClass().equals(Boolean.class)
		    || value.getClass().equals(String.class)) 
		{
		    return true;
		}

		return false;
	}

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
    private HashMap<Node,String> staticStructs = new HashMap<Node,String>();
    private HashMap<Node,String> localStructs = new HashMap<Node,String>();

    public CodeGenerator(Node root, HashMap<Node, TypeClass> nodeTypes, HashSet<Node> newShortDeclarationVariables, SemanticAnalyzer semanticAnalyzer, String fileName)
    {
        this.root = root;
        this.nodeTypes = nodeTypes;
        this.newShortDeclarationVariables = newShortDeclarationVariables;
        this.semanticAnalyzer = semanticAnalyzer;
        this.fileHeader = 
        "import java.util.*;\n" +
        "\n" +
        "public class " + fileName + "{\n";
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
        // Print the helper methods
        print(fileFooter);

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
            if (!(decl instanceof AFuncDeclAstDecl) && !(decl instanceof ATypeDeclAstDecl)) 
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


    public boolean isSameIdType(PIdType left, PIdType right) {
        if (left.getClass().equals(right.getClass())) {
            if (left instanceof AIdIdType) {
                AIdIdType lId = (AIdIdType) left, rId = (AIdIdType) right;
                return lId.getId().getText().equals(rId.getId().getText());
            } else {
                ATypeIdType lId = (ATypeIdType) left, rId = (ATypeIdType) right;
                return lId.getType().getText().equals(rId.getType().getText());
            }
        }
        return false;
    }

    public boolean isSameInnerField(PInnerFields l, PInnerFields r) {
        ASingleInnerFields left = (ASingleInnerFields) l, right = (ASingleInnerFields) r;        
        List<PIdType> leftIds = left.getIdType(), rightIds = right.getIdType();
        PVarType leftType = left.getVarType(), rightType = right.getVarType();

        if (leftIds.size() != rightIds.size()) {
            return false;
        }

        for (int i = 0; i < leftIds.size(); i++) {
            if (!isSameIdType(leftIds.get(i), rightIds.get(i))) {
                return false;
            }
        }

        return isSameVarType(leftType, rightType);
    }

    public boolean isSameVarType(PVarType left, PVarType right) {
        if (left.getClass().equals(right.getClass())) {
            if (left instanceof ATypeVarType) {
                ATypeVarType lType = (ATypeVarType) left, rType = (ATypeVarType) right;
                return lType.getType().getText().equals(rType.getType().getText());
            } else if (left instanceof ASliceVarType) {
                return isSameVarType(((ASliceVarType) left).getVarType(),
                    ((ASliceVarType) right).getVarType());
            } else if (left instanceof AArrayVarType) {
                AArrayVarType lArray = (AArrayVarType) left, rArray = (AArrayVarType) right;
                if (lArray.getInt().getText().equals(rArray.getInt().getText())) {
                    return isSameVarType(lArray.getVarType(), rArray.getVarType());
                }
                return false;
            } else if (left instanceof AStructVarType) {
                AStructVarType lStruct = (AStructVarType) left, rStruct = (AStructVarType) right;
                return isSameStruct(lStruct.getInnerFields(), rStruct.getInnerFields());
            } else {
                AIdVarType lId = (AIdVarType) left, rId = (AIdVarType) right;
                return lId.getId().getText().equals(rId.getId().getText());
            }
        }

        return false;
    }


    public boolean isSameStruct(List<PInnerFields> left, List<PInnerFields> right) {
        if (left == null && right == null) {
            return true;
        }

        if (left.size() != right.size()) {
            return false;
        }

        for (int i = 0; i < left.size(); i++) {
            if (!isSameInnerField(left.get(i), right.get(i))) {
                return false;
            }
        }

        return true;
    }

    public void outAStructVarType(AStructVarType node) {
        String name = null;
        for (Node n : staticStructs.keySet()) {
            AStructVarType cur = (AStructVarType) n;
            if (isSameStruct(cur.getInnerFields(), node.getInnerFields())) {
                return;
            }
        }

        for (Node n : localStructs.keySet()) {
            AStructVarType cur = (AStructVarType) n;
            if (isSameStruct(cur.getInnerFields(), node.getInnerFields())) {
                return;
            }
        }

        String className = "";
        if (!inFunction) {
            className += "static ";
        }
        if (levels == 1) {
            printi(className + "class ");
        } else {
            print(className + "class ");
        }

        if (levels > 1) {
            name = "AnonymousClass" + anonymousClassIndex;
            print(name);
            anonymousClassIndex++;
        } else {
            name = topLevelName;
            print(name);
        }

        println(" {");
        for (Node n : node.getInnerFields()) {
            indentLevel++;
            ASingleInnerFields cur = (ASingleInnerFields) n;
            for (int i = 0; i < cur.getIdType().size(); i++) {
                printi("");
                declareVariable(cur.getIdType().get(i), true);
                println(";");
            }
            indentLevel--;
        }
        indentLevel++;
        printiln("public boolean equals(Object object) {");
        indentLevel++;

        printiln("if (object instanceof " + name + " ) {");
        indentLevel++;
        printiln(name + " " + "cur = ((" + name + ") object);");

        printiln("if (");
        indentLevel++;
        for (int j = 0 ; j < node.getInnerFields().size(); j++) {
            Node n = node.getInnerFields().get(j);
            ASingleInnerFields cur = (ASingleInnerFields) n;
            for (int i = 0; i < cur.getIdType().size(); i++) {
                String curField = getIdName(cur.getIdType().get(i));
                printi("");
                print("this." + curField + " == ");
                if (i == (cur.getIdType().size() - 1)) {
                    print("cur." + curField);
                } else {
                    print("cur." + curField + " &&");
                    println("");
                }
            }
            if (j != (node.getInnerFields().size() - 1)) {
                println(" &&");
            }
        }
        println("");
        indentLevel--;
        printiln(" ) return true;");

        indentLevel--;
        // printiln("} else {");
        // indentLevel++;
        printiln("}");
        printiln("return false;");
        // indentLevel--;
        // printiln("}");
        indentLevel--;
        printiln("}");
        indentLevel--;
        printiln("}");
        if (!inFunction) staticStructs.put(node, name);
        else localStructs.put(node, name);
    }

    public void caseAStructVarType(AStructVarType node) {
        inAStructVarType(node);
        levels++;
        {
            List<PInnerFields> copy = new ArrayList<PInnerFields>(node.getInnerFields());
            for(PInnerFields e : copy)
            {
                ASingleInnerFields cur = (ASingleInnerFields) e;
                cur.getVarType().apply(this);
            }
        }
        outAStructVarType(node);
        levels--;
    }

    //base case idTypes

    public void caseAIdIdType(AIdIdType node) {
        print(node.getId().getText());
    }

    public void caseATypeIdType(ATypeIdType node) {
        print("_" + node.getType().getText());
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
        
        Symbol symbol = semanticAnalyzer.symbolMap.get(node);
        if(symbol != null && symbol.kind != null && symbol.kind == Symbol.SymbolKind.FIELD) {
           print("static "); 
        }

        String variableName = getIdName(node);
        
        // If the variable wasn't declared in this function, declare it
        if (symbol == null || !symbol.alreadyDeclared)
        {
            // System.out.println("Declaring variable: " + node);
            String typeName = (node != null)? getTypeName(node) : getTypeName(expNode);
            print(typeName + " ");

            // Insert id into HashMap so we don't redeclare it
            //declaredVariables.put(variableName, nodeTypes.get(node));
        }

        
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
        // print("type ");
        node.getTypeDecl().apply(this);
        //println("");
    }

    public void caseATypeAliasTypeDecl(ATypeAliasTypeDecl node)
    {
        inATypeAliasTypeDecl(node);
        // if(node.getIdType() != null)
        // {
        //     node.getIdType().apply(this);
        // }
        // if(node.getVarType() != null)
        // {
        //     node.getVarType().apply(this);
        // }
        // outATypeAliasTypeDecl(node);
    }

    public void inATypeAliasTypeDecl(ATypeAliasTypeDecl node) {
        //node.getIdType().apply(this);
        //print(" ");
        Node varType = node.getVarType();
        while (varType instanceof ASliceVarType || varType instanceof AArrayVarType) {
            if (varType instanceof ASliceVarType) {
                varType = ((ASliceVarType) varType).getVarType();
            } else {
                varType = ((AArrayVarType) varType).getVarType();
            }
        }

        TypeClass type = nodeTypes.get(node.getIdType());

        if (type.baseType == Type.STRUCT && type.typeAliases.size() == 0)
        {
            topLevelName = getIdName(node.getIdType());
            if (varType instanceof AStructVarType) {
                varType.apply(this);
            }
            // // TODO: Create thei class
            // String name = null;
            // for (Node n : declaredStructs.keys()) {
            //     if (isAliasedCorrectly(getType(n), getType(node))) {
            //         if (isSameStruct()) {
            //             //print name
            //             return;
            //         }
            //     }
            // }
            // if (!inFunction) print("static ");
            //     println("class ");
            //     indentLevel++;
            //     for (Node n : node.getInnerFields()) {
            //         printi("");
            //         declareVariable(n, true);
            //         println(";");
            //     }
            //     print("}");
            System.out.println("First time encountering struct: " + node.getIdType() + ". Creating the class");
        }
    } 

    // public void caseAStructVarDeclTypeDecl(AStructVarDeclTypeDecl node) {
    //     inATypeDecl = true;
    //     node.getVarType().apply(this);
    //     outATypeAliasTypeDecl(node);
    //     inATypeDecl = false;
    // }
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
        inFunction = true;
        print("public static ");
        node.getFuncDecl().apply(this);
        println("\n");
        inFunction = false;
        localStructs = new HashMap<Node,String>();
    }
    
    public void caseANoReturnFuncDecl(ANoReturnFuncDecl node) {
        String functionName = node.getIdType().toString().trim();
        if((new String(functionName).equals("main"))){
            print("void main(String[] args)");
        }
        else{
            print("void ");
            node.getIdType().apply(this);
            print("(");
            if (node.getSignature() != null) node.getSignature().apply(this);
                print(") ");
        }
        node.getBlock().apply(this);
    }

    public void caseASingleReturnFuncDecl(ASingleReturnFuncDecl node) {
        // Return type
        TypeClass type = nodeTypes.get(node.getIdType());
        print(getTypeName(type.functionSignature.returnType,node.getIdType()) + " ");
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
        return getTypeName(nodeTypes.get(node), node);
    }

    public String getTypeName(TypeClass type, Node node)
    {
        // TypeClass type = nodeTypes.get(node);
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
                    return getStructName(type);
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
                    typeName += getStructName(type);
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
        return getStructName(type);
    }

    private String getStructName(TypeClass type)
    {
        if (type.baseType != Type.STRUCT) 
        { 
            System.out.println("getStructName(): type is not a struct: " + type);// + ". node = " + node);
            return null; 
        }

        // If the struct was declared using a type alias
        if (type.typeAliases.size() > 0)
        {
            // Index 0 stores the first type alias of the struct 
            TypeAlias structAlias = type.typeAliases.get(0);

            // System.out.println(node + " has type alias: " + structAlias + "[" + type.structNode + ", " + structAlias.node.getClass() + "]");

            if (structAlias.node instanceof ATypeAliasTypeDecl)
            {
                ATypeAliasTypeDecl typeAlias = (ATypeAliasTypeDecl)structAlias.node;
                String structName = getIdName(typeAlias.getIdType());
                return structName;
            }
        }
        else
        {   
            // System.out.println(node + " has an anonymous struct type: " + type.structNode);
            for (Node n : staticStructs.keySet()) {
                AStructVarType cur = (AStructVarType) n;
                if (isSameStruct(cur.getInnerFields(), type.innerFields)) {
                    return staticStructs.get(n);
                }
            }

            for (Node n : localStructs.keySet()) {
                AStructVarType cur = (AStructVarType) n;
                if (isSameStruct(cur.getInnerFields(), type.innerFields)) {
                return localStructs.get(n);
                }
            }
        }

        return null;
    }

    // Declares a struct if it doens't yet exist
    // private void declareStruct(String structName, Node node)
    // {
    //     TypeClass type = nodeTypes.get(node);

    //     // Don't declare a struct that was already declared
    //     if (declaredStructs.containsKey(type.structNode))
    //     {
    //         return;
    //     }

    //     String classText = CLASS_MODIFIER;

    //     // The struct was created using a type alias
    //     if (structName != null)
    //     {
    //         classText += structName;
    //     }
    //     // The struct is an anonymous struct
    //     else
    //     {
    //         // Give the struct the next available name
    //         classText += ANONYMOUS_CLASS_NAME + anonymousClassIndex;
    //         anonymousClassIndex++;
    //     }

    //     int indentLevel = 0;
    //     classText += "{\n";
    //     indentLevel++;
        
    //     classText += "}\n";
    // }

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
                    print(" + \" \" + "); 
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
            if (lvalue == null || expression instanceof AAppendedExprExp)
            {
                expression.apply(this);
            }
            // The lvalue is not a blank ID -- print it
            else
            {
                if (i != 0)
                    printi("");

                // Array assignment
                if (lvalue instanceof AArrayElementExp)
                {
                    AArrayElementExp arrayElementExp = (AArrayElementExp)lvalue;
                    PExp array = arrayElementExp.getArray();
                    print("_set_(");
                    array.apply(this);
                    print(",");
                    arrayElementExp.getIndex().apply(this);
                    print(",");
                    expression.apply(this);
                    print(",");
                    printLastArrayArguments(arrayElementExp, false);
                }
                // Regular assignment
                else
                {
                    lvalue.apply(this);
                    if (!(node.getOp() instanceof AOpEqualsExp))
                        print(" = ");
                    else 
                    {
                        String op = node.getOp().toString().trim();
                        if (!op.equals("&^="))
                            node.getOp().apply(this);
                        else
                            print("&= ~");
                    }
                    expression.apply(this);
                    // Use temporary variables for swapping
                    if (swappedExpressions.contains(expression))
                        print("Temp");
                }
            }

            //if (i != lvalues.size()-1)
                println(";");
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
                    return "new " + getStructName(node) + "()" ;
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
		// node.getArray().apply(this);
		// print(".get(");
		// node.getIndex().apply(this);
		// print(")");

        print("_get_(");
        node.getArray().apply(this);
        print(",");
        node.getIndex().apply(this);
        print(",");
        
        printLastArrayArguments(node,true);
        
		// print(")");
	}

    // Print the last arguments in a _get_/_set_() call
    private void printLastArrayArguments(AArrayElementExp node, boolean get)
    {
        TypeClass arrayType =  nodeTypes.get(node.getArray());//(get)? nodeTypes.get(node.getArray()) : nodeType.get(node);
        int dimensions = arrayType.totalArrayDimension.size();

        System.out.println(node.getArray() + " array dimensions: ");
        for (int i = 0; i < dimensions; i++)
        {
            System.out.print(arrayType.totalArrayDimension.get(i) + ",");
        }
        System.out.println();

        Dimension firstDimension = arrayType.totalArrayDimension.get(0);
        print(firstDimension.isArray + ",");
        print(firstDimension.size + ",");

        TypeClass elementType = nodeTypes.get(node);
        String defaultValue = getDefaultValue(node);
        // Cast the default value to a char
        if (defaultValue.equals("char"))
            print("(char)");
        print(getDefaultValue(node) + ")"); 
    }

    public void caseAFieldExp(AFieldExp node) {
		print("(");
		node.getExp().apply(this);
		print(").");
		node.getIdType().apply(this);
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
            if (!isTypeDecl(stmt) && !isControlStatement(stmt) && !isEmptyMultilineList(stmt) && !isAssignList(stmt)) 
                println(";");
            else
                println("");
        }
        indentLevel--;
        printi("}");
    }

    private boolean isTypeDecl(Node stmt) {
        if (stmt instanceof ADeclStmt) {
            return (((ADeclStmt) stmt).getDecl() instanceof ATypeDeclAstDecl);
        }
        return false;
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
            // println("; ");
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

            AForCondExp forCond = (AForCondExp)forCondition;
            if (forCond.getFirst() != null)
            {
                printi("");
                forCond.getFirst().apply(this);
            }

            printi("for (;");
            if (forCond.getSecond() != null)
                forCond.getSecond().apply(this);
            print(";");
            if (forCond.getThird() != null)
                forCond.getThird().apply(this);
            //forCondition.apply(this);
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
            //println("; ");
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
        TypeClass type1 = nodeTypes.get(node.getL());
        TypeClass type2 = nodeTypes.get(node.getR());
        if(type1.baseType == Type.STRING && type2.baseType == Type.STRING){
             node.getL().apply(this);
             print(".compareTo(");
             node.getR().apply(this);
             print(") == 0");
        }
        else{
            print("(");
            node.getL().apply(this);
            print("==");
            node.getR().apply(this);
            print(")");
            printWithType(node);
        }
    }

    public void caseANotEqualExp(ANotEqualExp node)
    {
        TypeClass type1 = nodeTypes.get(node.getL());
        TypeClass type2 = nodeTypes.get(node.getR());
        if(type1.baseType == Type.STRING && type2.baseType == Type.STRING){
             node.getL().apply(this);
             print(".compareTo(");
             node.getR().apply(this);
             print(") != 0");
        }
        else{
            print("(");
            node.getL().apply(this);
            print("!=");
            node.getR().apply(this);
            print(")");
            printWithType(node);
        }
    }

    public void caseALessExp(ALessExp node)
    {
        TypeClass type1 = nodeTypes.get(node.getL());
        TypeClass type2 = nodeTypes.get(node.getR());
        if(type1.baseType == Type.STRING && type2.baseType == Type.STRING){
             node.getL().apply(this);
             print(".compareTo(");
             node.getR().apply(this);
             print(") < 0");
        }
        else{
            print("(");
            node.getL().apply(this);
            print("<");
            node.getR().apply(this);
            print(")");
            printWithType(node);
        }
    }

    public void caseAGreaterExp(AGreaterExp node)
    {
        TypeClass type1 = nodeTypes.get(node.getL());
        TypeClass type2 = nodeTypes.get(node.getR());
        if(type1.baseType == Type.STRING && type2.baseType == Type.STRING){
             node.getL().apply(this);
             print(".compareTo(");
             node.getR().apply(this);
             print(") > 0");
        }
        else{
            print("(");
            node.getL().apply(this);
            print(">");
            node.getR().apply(this);
            print(")");
            printWithType(node);
        }
    }

    public void caseALessEqualsExp(ALessEqualsExp node)
    {
        TypeClass type1 = nodeTypes.get(node.getL());
        TypeClass type2 = nodeTypes.get(node.getR());
        if(type1.baseType == Type.STRING && type2.baseType == Type.STRING){
             node.getL().apply(this);
             print(".compareTo(");
             node.getR().apply(this);
             print(") <= 0");
        }
        else{
            print("(");
            node.getL().apply(this);
            print("<=");
            node.getR().apply(this);
            print(")");
            printWithType(node);
        }
    }

    public void caseAGreaterEqualsExp(AGreaterEqualsExp node)
    {
        TypeClass type1 = nodeTypes.get(node.getL());
        TypeClass type2 = nodeTypes.get(node.getR());
        if(type1.baseType == Type.STRING && type2.baseType == Type.STRING){
             node.getL().apply(this);
             print(".compareTo(");
             node.getR().apply(this);
             print(") >= 0");
        }
        else{
            print("(");
            node.getL().apply(this);
            print(">=");
            node.getR().apply(this);
            print(")");
            printWithType(node);
        }
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
