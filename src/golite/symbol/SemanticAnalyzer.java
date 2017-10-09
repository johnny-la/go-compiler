package golite.symbol;

import golite.*;
import golite.node.*;
import golite.type.*;
import golite.type.FunctionSignature;
import golite.analysis.*;
import golite.symbol.Symbol;
import golite.symbol.Symbol.SymbolKind;
import java.util.*;

/**
 * Identifies the types for variables and identifiers
 */
public class SemanticAnalyzer extends DepthFirstAdapter
{
    // Checks that variables are declared before use
    private SymbolTable symbolTable;

    // Mapping between nodes and symbols, used later for type checking
    public HashMap<Node, Symbol> symbolMap;
    // The lvalues that were declared in short declaration statements
    public HashSet<Node> newShortDeclarationVariables;

    // Maps every struct declaration node to a symbol table for each inner field
    private HashMap<Node,SymbolTable> structHierarchy;
    private SymbolTable currentStructScope;

    // If true, the top-most frame of the symbol table is dumped when a scope is left.
    private boolean dumpSymbolTable;
    public String dumpSymbolTableOutput = "";

    // True if we are traversing inside a function block
    private boolean justEnterredFunction;
    // True if we are inside a function declaration
    private boolean inAFunction;

    public SemanticAnalyzer()
    {
        symbolTable = new SymbolTable();
        symbolMap = new HashMap<Node, Symbol>();
        structHierarchy = new HashMap<Node, SymbolTable>();
        newShortDeclarationVariables = new HashSet<Node>();
    }

    public SemanticAnalyzer(SymbolTable symbolTable, boolean dumpSymbolTable)
    {
        this();

        this.symbolTable = symbolTable;
        this.dumpSymbolTable = dumpSymbolTable;
    }

    public void inANoReturnFuncDecl(ANoReturnFuncDecl node)
    {
        declareFunction(node.getIdType(), null, node);
    }

    public void inASingleReturnFuncDecl(ASingleReturnFuncDecl node)
    {
        declareFunction(node.getIdType(), null, node);
    }

    public void outANoReturnFuncDecl(ANoReturnFuncDecl node)
    {
        outAFuncDecl(node);
    }

    public void outASingleReturnFuncDecl(ASingleReturnFuncDecl node)
    {
        outAFuncDecl(node);
    }

    /** 
     * Called when we leave a function declaration.
     */
    public void outAFuncDecl(PFuncDecl node)
    {   
        // Used to determine the kind of a variable declaration (FIELD vs. LOCAL)
        inAFunction = false;
    }

    /** 
     * Inserts the given function ID in the the symbol table,
     * Creates a new scope for the contents of the function
     */
    public void declareFunction(PIdType id, PVarType varType, PFuncDecl node)
    {
        Symbol symbol = declareVariable(id, varType, SymbolKind.FUNCTION, node);
        symbolMap.put(node, symbol);
        scope();

        // Get the half-populated type class defined in declareVariable()
        Symbol functionSymbol = symbolTable.get(getIdName(id));
        TypeClass typeClass = functionSymbol.typeClass;
        typeClass.functionSignature = new FunctionSignature();

        // Get the return type of the function
        TypeClass returnType = getReturnType(node);
        typeClass.functionSignature.returnType = returnType;

        // Get the parameter types of the function
        ArrayList<TypeClass> parameterTypes = getParameterTypes(node);
        typeClass.functionSignature.parameterTypes = parameterTypes;

        // Tells inABlockStmt() to not create a new scope 
        justEnterredFunction = true;
        // Determines the kind of a variable declaration (FIELD vs. LOCAL)
        inAFunction = true;
    }

    /**
     * Returns the return type of the given function 
     */
    private TypeClass getReturnType(PFuncDecl node)
    {
        // Find the return type of the function
        TypeClass returnType = new TypeClass();

        if (node instanceof ANoReturnFuncDecl)
        {
            returnType.baseType = Type.VOID;
        }
        else if (node instanceof ASingleReturnFuncDecl)
        {
            ASingleReturnFuncDecl funcDecl = (ASingleReturnFuncDecl)node;
            returnType = getTypeClass(funcDecl.getVarType());
        }

        return returnType;
    }

    /**
     * Returns a list of the parameter types in the function's signature
     */
    private ArrayList<TypeClass> getParameterTypes(PFuncDecl node)
    {
        ArrayList<TypeClass> parameterTypes = new ArrayList<TypeClass>();

        PSignature signature = null;
        if (node instanceof ANoReturnFuncDecl)
        {
            signature = ((ANoReturnFuncDecl)node).getSignature();
        }
        else if (node instanceof ASingleReturnFuncDecl)
        {
            ASingleReturnFuncDecl funcDecl = (ASingleReturnFuncDecl)node;
            signature = funcDecl.getSignature();
        }

        // Iterate through each set of signatures
        while (signature != null)
        {
            TypeClass parameterType = null;
            LinkedList<? extends Node> idList = null;
            if (signature instanceof AMultipleTypesSignature)
            {
                AMultipleTypesSignature multiTypeSig = (AMultipleTypesSignature)signature;
                parameterType = getTypeClass(multiTypeSig.getVarType());
                idList = multiTypeSig.getIdList(); 
                
            }
            else if (signature instanceof ASingleTypeSignature)
            {
                ASingleTypeSignature singleTypeSig = (ASingleTypeSignature)signature;
                parameterType = getTypeClass(singleTypeSig.getVarType());
                idList = singleTypeSig.getIdList();
            }

            // Add the parameter type "n" times, for each identifier in the signature list
            for (int i = 0; i < idList.size(); i++)
            {
                TypeClass newType = new TypeClass(parameterType);
                parameterTypes.add(newType);
            }

            if (signature instanceof AMultipleTypesSignature)
            {
                // Move to the next signature
                signature = ((AMultipleTypesSignature)signature).getSignature();
            }
            else if (signature instanceof ASingleTypeSignature)
            {
                // End the iteration
                signature = null;
            }
        }

        return parameterTypes;
    }

    public void inAMultipleTypesSignature(AMultipleTypesSignature node)
    {
        // Declare all the variables in the signature
        declareVariableList(node.getIdList(), node.getVarType(), SymbolKind.FORMAL, node);
    }

    public void inASingleTypeSignature(ASingleTypeSignature node)
    {
        // Declare all the variables in the signature
        declareVariableList(node.getIdList(), node.getVarType(), SymbolKind.FORMAL, node);
    }

    public void inAIfStmt(AIfStmt node)
    {
        // New scope for the init statement
        scope();
    }

    public void outAIfStmt(AIfStmt node)
    {
        unscope();
    }

    public void inAForStmt(AForStmt node)
    {
        // New scope for the init statement
        scope();
    }

    public void outAForStmt(AForStmt node)
    {
        unscope();
    }

    public void inASwitchStmt(ASwitchStmt node)
    {
        // Open a new scope for the init statement
        scope();
    }

    public void outASwitchStmt(ASwitchStmt node)
    {
        unscope();
    }

    // This function is called for the following constructs:
    // case e1,e2,..,en:
    //    statements
    // OR 
    // default: 
    //    statements
    public void inACaseStmt(ACaseStmt node)
    {
        // Open a new scope for each case statement (case 1,2,..,3: statements)
        scope();
    }

    public void outACaseStmt(ACaseStmt node)
    {
        unscope();
    }

    public void inAAssignListStmt(AAssignListStmt node)
    {
        // True if the assignment is a short declaration (x,y,..,z := 1,2,..,3)
        boolean isShortDeclaration = (node.getOp() instanceof AColonEqualsExp);

        // Declare any new variables
        if (isShortDeclaration)
        {
            boolean idDeclared = false;

            for (int i = 0; i < node.getL().size(); i++)
            {
                // Determine the name of the ID
                AIdExp idNode = (AIdExp)node.getL().get(i);
                String idName = getIdName(idNode.getIdType());

                // If the id was not declared yet, declare it
                if (!symbolTable.contains(idName))
                {
                    Symbol s = declareVariable(idNode.getIdType(), null, SymbolKind.LOCAL, node);
                    symbolMap.put(idNode.getIdType(),s);
                    symbolMap.put(idNode,s);
                    idDeclared = true;
                    // Tells code generator which variables were newly declared
                    newShortDeclarationVariables.add(idNode);
                }
            }

            if (!idDeclared)
                ErrorManager.printError("No new variables on the left side of := \"" + node + "\"");
        }        
    }

    public void inABlockStmt(ABlockStmt node)
    {
        // If we just enterred a function, we already started a new scope for
        // the formal parameters
        if (!justEnterredFunction)
            // Start a new scope at each block
            scope();

        // This boolean has fulfilled its purpose
        justEnterredFunction = false;
    }

    public void outABlockStmt(ABlockStmt node)
    {
        // Pop the current scope
        unscope();
    }


    public void inAVarWithTypeVarDecl(AVarWithTypeVarDecl node)
    {   
        Node type = node.getIdType();
        Node varType = node.getVarType();
        String idName;
        String typeName;

        declareVariable(node.getIdType(), node.getVarType(), SymbolKind.LOCAL, node);
    }

    public void inAVarWithOnlyExpVarDecl(AVarWithOnlyExpVarDecl node)
    {
        declareVariable(node.getIdType(), null, SymbolKind.LOCAL, node);
    }

    public void inAVarWithTypeAndExpVarDecl(AVarWithTypeAndExpVarDecl node)
    {   
        declareVariable(node.getIdType(), node.getVarType(), SymbolKind.LOCAL, node);
    }

    public void inAInlineListNoExpVarDecl(AInlineListNoExpVarDecl node)
    {
        PVarType type = getVariableType(node);
        declareVariable(node.getIdType(), type, SymbolKind.LOCAL, node);
    }

    public void inAInlineListWithExpVarDecl(AInlineListWithExpVarDecl node)
    {
        PVarType type = getVariableType(node);
        declareVariable(node.getIdType(), type, SymbolKind.LOCAL, node);
    }

    public void outATypeAliasTypeDecl(ATypeAliasTypeDecl node)
    {
        //traverse VarType until base check if its an id, check if the id is declared
        Symbol symbol = declareVariable(node.getIdType(), node.getVarType(), SymbolKind.TYPE, node);
    }

    /**
     * Returns the type of a variable declaration
     */
    public PVarType getVariableType(PVarDecl node)
    {
        // Get to the end of the variable list to retrieve the variable type 
        PVarDecl current = node;
        while (current instanceof AInlineListNoExpVarDecl)
        {
            current = ((AInlineListNoExpVarDecl)current).getVarDecl();
        }
        while (current instanceof AInlineListWithExpVarDecl)
        {
            current = ((AInlineListWithExpVarDecl)current).getVarDecl();
        }

        // Get the type of the variable
        PVarType type = null;

        if (current instanceof AVarWithTypeVarDecl)
        {
            type = ((AVarWithTypeVarDecl)current).getVarType();
        }
        else if (current instanceof AVarWithTypeAndExpVarDecl)
        {
            type = ((AVarWithTypeAndExpVarDecl)current).getVarType();
        }

        return type;
    }

    /**
     * Declares all the identifiers in the list, giving them all the same type
     */
    public void declareVariableList(LinkedList<? extends PIdType> list, PVarType varType,
        SymbolKind kind, Node originatingNode)
    {
        for (int i = 0; i < list.size(); i++)
        {
            PIdType node = list.get(i);
            
            declareVariable(node, varType, kind, originatingNode);
        }
    }

    /** 
     * Inserts the given ID in the the symbol table, and throws an error
     * if it's already declared in the current scope
     */
    public Symbol declareVariable(PIdType id, PVarType varType, SymbolKind kind, Node node)
    {
        String idName = getIdName(id);

        // Throw an exception if the identifier was already declared
        if (symbolTable.contains(idName) && !idName.trim().equals("_"))
        {
            ErrorManager.printError("\"" + id + "\" is already declared in this block.");
            return null;
        }    

        Type type = Type.INVALID;
        
        if (varType instanceof ATypeVarType) {
            type = Type.stringToType(((ATypeVarType)varType).getType().getText());
        }
      
        TypeClass typeClass = getTypeClass(varType);
        if (typeClass.baseType == Type.STRUCT && kind != SymbolKind.TYPE) {
            kind = SymbolKind.LOCAL;
        }

        if(!inAFunction && kind == SymbolKind.LOCAL){
            kind = SymbolKind.FIELD;
        }

        // Insert the symbol in the symbol table
        Symbol symbol = new Symbol(idName, node, typeClass, kind);
        // Check if the symbol already exists in the symbol table and is
        // a redeclared local
        Symbol existingSymbol = symbolTable.get(idName);
        if (existingSymbol != null && existingSymbol.kind != SymbolKind.FIELD 
            && symbol.kind != SymbolKind.STRUCT_FIELD)
        {
            System.out.println("Symbol is already declared: " + existingSymbol);
            symbol.alreadyDeclared = true;
        }

        symbolTable.put(idName, symbol);
        symbolMap.put(node, symbol);
        symbolMap.put(id, symbol);

        return symbol;
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

    private TypeClass getTypeClass(PVarType varType)
    {
        TypeClass typeClass = new TypeClass();

        typeClass.varTypeNode = varType;

        if (varType == null)
            return typeClass;

        PVarType current = varType;
        int nodeDepth = 0;  // The number of nodes traversed
        while (!(current instanceof ATypeVarType))
        {
            if (current instanceof ASliceVarType)
            {
                Dimension d = new Dimension(false, 0);
                typeClass.incrementDimension(d);
                current = ((ASliceVarType)current).getVarType();
               
            }
            else if (current instanceof AArrayVarType)
            {
                Dimension d = new Dimension(true, 
                    Integer.valueOf(((AArrayVarType)current).getInt().getText()));
                typeClass.incrementDimension(d);
                current = ((AArrayVarType)current).getVarType();
            } 
            else if (current instanceof AStructVarType) 
            {
                    AStructVarType struct = (AStructVarType) current;
                    typeClass.innerFields = struct.getInnerFields();
                    typeClass.structNode = struct;
                    typeClass.baseType = Type.STRUCT;

                    break;
            }
            // Type alias
            else if (current instanceof AIdVarType)
            {
                //Check if alias exists in symbol table
                String typeAliasName = ((AIdVarType)current).getId().getText();
                Symbol typeAliasSymbol = symbolTable.get(typeAliasName);
                if (typeAliasSymbol == null)
                {
                    ErrorManager.printError("Type alias was never declared: \"" + typeAliasName + "\"");
                    break;
                }

                if (typeAliasSymbol.kind != SymbolKind.TYPE) {
                    ErrorManager.printError("Type aliasing of variable");
                }

                // Alias exists case
                ArrayList<TypeAlias> typeAliasesToInherit = typeAliasSymbol.typeClass.typeAliases;
                for (int i = 0; i < typeAliasesToInherit.size(); i++)
                {
                    TypeAlias typeAlias = new TypeAlias(typeAliasesToInherit.get(i));
                    typeClass.typeAliases.add(typeAlias);
                }

                // Store the type alias
                if (typeAliasSymbol.node instanceof ATypeAliasTypeDecl)
                {
                    TypeAlias typeAlias = new TypeAlias();
                    typeAlias.node = typeAliasSymbol.node;
                    typeAlias.setArrayDimensions(typeAliasSymbol.typeClass.totalArrayDimension);
                    typeClass.typeAliases.add(typeAlias);
                }

                typeClass.baseType = typeAliasSymbol.typeClass.baseType;
                typeClass.innerFields = typeAliasSymbol.typeClass.innerFields;
                typeClass.structNode = typeAliasSymbol.typeClass.structNode;
                typeClass.addDimensions(typeAliasSymbol.typeClass.totalArrayDimension);
                break;
            } 

            nodeDepth++;
        }

        // Determine the base type of the variable
        if (current instanceof ATypeVarType)
        {
            Symbol s = symbolTable.get(((ATypeVarType)current).getType().getText());
            if (s != null && s.kind != Symbol.SymbolKind.TYPE) 
            {
                ErrorManager.printError(s + " is already declared as a variable");
            }

            typeClass.baseType = Type.stringToType(((ATypeVarType)current).getType().getText());
        }

        return typeClass;
    }

    public void inAFunctionCallExp(AFunctionCallExp node)
    {   
        PExp lhs = node.getName();

        if (lhs instanceof AIdExp)
        {
            PIdType lhsIdTypeNode = ((AIdExp)lhs).getIdType();
            String lhsIdName = getIdName(lhsIdTypeNode);
            Type lhsIdType = Type.stringToType(lhsIdName);
            
            // If the LHS is a valid type-casting primitive type 
            if (lhsIdType == Type.INT || lhsIdType == Type.FLOAT64 || lhsIdType == Type.RUNE
                || lhsIdType == Type.BOOL)
            {
                // If the id is not in the symbol table, define it as a type cast
                if (symbolTable.get(lhsIdName) == null)
                {
                    Symbol symbol = new Symbol();
                    symbol.name = lhsIdName;
                    symbol.node = lhs;
                    symbol.kind = SymbolKind.TYPE;
                    symbol.typeClass = new TypeClass();
                    symbol.typeClass.baseType = lhsIdType;

                    symbolMap.put(lhs, symbol);
                }
            }
        }
    }

    public void outAIdExp(AIdExp node)
    {
        String id = getIdName(node.getIdType());

        // Leave true/false to type checking
        if (id.equals("true") || id.equals("false"))
            return;
        
        // Don't redefine the symbols for nodes that already have a symbol
        if (symbolMap.get(node) != null)
            return;

        Symbol symbol = checkVariableDeclared(id);

        if (symbol != null)
        {
            Symbol newSymbol = new Symbol(symbol);
            if (symbol.kind == SymbolKind.TYPE)
            {
                TypeAlias alias = new TypeAlias();
                alias.node = symbol.node;
                newSymbol.typeClass.typeAliases.add(alias);
            }
            // If this is a symbol for a implicitly-typed variable
            else if ((symbol.kind == SymbolKind.FIELD || symbol.kind == SymbolKind.LOCAL) && symbol.typeClass.isNull())
            {
                symbol.symbolsToInheritType.add(newSymbol);
                if (symbol.symbolsToInheritType.size() == 1)
                {
                    newSymbol = symbol;
                }   
            }
            // Add a node->symbol mapping for future type checking
            symbolMap.put(node, newSymbol);

        }
    }

    public void outAArrayElementExp(AArrayElementExp node)
    {
        Symbol arraySymbol = symbolMap.get(node.getArray());
        
        if (arraySymbol == null || arraySymbol.typeClass.totalArrayDimension.size() == 0)
        {
            ErrorManager.printError("Indexing a non-array type: " + arraySymbol);
            return;
        }

        Symbol newSymbol = new Symbol(arraySymbol);
        newSymbol.typeClass.decrementDimension();

        symbolMap.put(node, newSymbol);
    }

    // Struct declaration:
    // "type point struct { ... }"
    public void inAStructVarType(AStructVarType node)
    {
        // Struct is already declared. Exit function
        if (structHierarchy.containsKey(node)) { return; }

        // Create a new scope which will contain the struct fields
        scope();

        // Add a mapping between the struct and its internal symbol table
        structHierarchy.put(node, symbolTable);
    }

    public void outAStructVarType(AStructVarType node)
    {
        unscope();
    }   

    public void inASingleInnerFields(ASingleInnerFields node)
    {
        TypeClass type = getTypeClass(node.getVarType());
        
        for (int i = 0; i < node.getIdType().size(); i++)
        {
            // Thus, declare the struct field
            Symbol s = declareVariable(node.getIdType().get(i), node.getVarType(), SymbolKind.STRUCT_FIELD, node);
            symbolMap.put(node.getIdType().get(i), s);
        }
    }

    public void caseAFieldExp(AFieldExp node)
    {
        node.getExp().apply(this);
        // Get the symbol for the LHS of the struct selector
        Symbol symbol = symbolMap.get(node.getExp());

        if (symbol == null || symbol.typeClass.structNode == null)
        {
            ErrorManager.printError("Using the . operator on a non-struct type: " + node.getExp()
                + ", symbol = " + symbol);
            return;
        }

        if (symbol.kind != Symbol.SymbolKind.LOCAL 
            && symbol.kind != Symbol.SymbolKind.FIELD) 
        {
             ErrorManager.printError("Using the . operator on a non-variable: " + node.getExp()
                + ", symbol = " + symbol);
            return;
        }

        if (symbol.typeClass.totalArrayDimension.size() != 0)
        {
            ErrorManager.printError("Using the . operator on an array: " + symbol);
        }

        Node structNode = symbol.typeClass.structNode;

        currentStructScope = structHierarchy.get(structNode);
        
        String rightId = getIdName(node.getIdType());
        Symbol rightSymbol = currentStructScope.get(rightId);
        if (rightSymbol == null)
        {
            ErrorManager.printError("\"" + rightId + "\" is not declared");
            return;
        }

        currentStructScope = null;

        Symbol newSymbol = new Symbol(rightSymbol);
        symbolMap.put(node, newSymbol);

    }

    /**
     * Throws an exception if the variable was not declared
     */
    private Symbol checkVariableDeclared(String id)
    {
        Symbol symbol = symbolTable.get(id);
        if (symbol == null)
        {
            ErrorManager.printError("\"" + id + "\" is not declared");
            return null;
        }

        return symbol;
    }
   
    /**
     * Scopes the symbol table
     */
    private void scope()
    {
        if (dumpSymbolTable)
            dumpSymbolTableOutput += symbolTable + "\n----------------------\n";

        // Creates a new scope after enterring a block
        symbolTable = symbolTable.scope();
    }

    /**
     * Unscopes the symbol table
     */
    private void unscope()
    {
        if (dumpSymbolTable)
            dumpSymbolTableOutput += symbolTable + "\n----------------------\n";

        // Pop the inner-most scope after leaving a block
        symbolTable = symbolTable.unscope();
    }
}
