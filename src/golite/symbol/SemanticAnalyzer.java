package golite.symbol;

import golite.*;
import golite.node.*;
import golite.type.*;
import golite.analysis.*;
import golite.symbol.Symbol;
import golite.symbol.Symbol.SymbolKind;

import java.util.*;

// TODO: type_decl.multiline_list, var_decl.multiline_list???

public class SemanticAnalyzer extends DepthFirstAdapter
{
    // Symbol table used to check that variables are not 
    // redeclared and that they are declared before use
    private SymbolTable symbolTable;

    // Mapping between nodes and symbols, used later for type checking
    private HashMap<Node,Symbol> symbolMap;
    // Maps every struct declaration node to a symbol table which contains
    // the struct's fields 
    private HashMap<Node,SymbolTable> structHierarchy;
    // If a struct selector expression is encountered, this variable stores
    // the symbol table belonging to the struct definition. This allows us
    // to determine if the struct selector is valid 
    // (e.g., "x.y". We must ensure that "y" is declared in x's struct declaration)
    private SymbolTable currentStructScope;

    // If true, the top-most frame of the symbol table is dumped when a scope is left.
    private boolean dumpSymbolTable;

    // True if we are traversing inside a function block
    private boolean justEnterredFunction;
    // True if we are inside a function declaration
    private boolean inAFunction;

    public SemanticAnalyzer()
    {
        symbolTable = new SymbolTable();
        symbolMap = new HashMap<Node, Symbol>();
        structHierarchy = new HashMap<Node, SymbolTable>();
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
        // NOT NEEDED: outABlockStmt() will unscope for us
        // Move to the outer scope
        // symbolTable = symbolTable.unscope();

        // Used to determine the kind of a variable declaration (FIELD vs. LOCAL)
        inAFunction = false;
    }

    /** 
     * Inserts the given function ID in the the symbol table,
     * Creates a new scope for the contents of the function
     */
    public void declareFunction(PIdType id, PVarType varType, Node node)
    {
        declareVariable(id, varType, SymbolKind.FUNCTION, node);
        scope();

        // Tells inABlockStmt() to not create a new scope 
        justEnterredFunction = true;
        // Determines the kind of a variable declaration (FIELD vs. LOCAL)
        inAFunction = true;
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

    public void inATypeAliasTypeDecl(ATypeAliasTypeDecl node)
    {
        Symbol symbol = declareVariable(node.getIdType(), node.getVarType(), SymbolKind.TYPE, node);
        System.out.println("Declared type alias: " + symbol);
    }

    // Struct declaration 
    // "type point struct { ... }"
    public void inAStructWithIdTypeDecl(AStructWithIdTypeDecl node)
    {
        declareVariable(node.getIdType(), null, SymbolKind.STRUCT, node);
        
        // Create a new scope which will contain the struct fields
        scope();
        // Add a mapping between the struct and its internal symbol table
        structHierarchy.put(node, symbolTable);
    }

    public void outAStructWithIdTypeDecl(AStructWithIdTypeDecl node)
    {
        unscope();
    }

    public void inAStructVarDeclTypeDecl(AStructVarDeclTypeDecl node)
    {
        for (int i = 0; i < node.getIdList().size(); i++)
        {
            PIdType id = node.getIdList().get(i);
            declareVariable(id, node.getVarType(), SymbolKind.STRUCT_FIELD, node);
        }
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
     * Inserts the given ID in the the symbol table,
     * and throws an error if it's already declared in
     * the current scope
     */
    public Symbol declareVariable(PIdType id, PVarType varType, SymbolKind kind, Node node)
    {
        //System.out.println("Symbol table at var decl:\n" + symbolTable);
        String idName = null;
        
        if (id instanceof AIdIdType) { idName = ((AIdIdType)id).getId().getText(); }
        else if (id instanceof ATypeIdType) { idName = ((ATypeIdType)id).getType().getText(); }

        // Throw an exception if the identifier was already declared
        if (symbolTable.contains(idName))
        {
            ErrorManager.printError("\"" + id + "\" is already declared in this block.");
            return null;
        }    

        Type type = Type.INVALID;
        
        if (varType instanceof ABaseTypeVarType)
            type = Type.stringToType(((ABaseTypeVarType)varType).getType().getText());
        //else 
        //    type = getType(varType);

        TypeClass typeClass = getTypeClass(varType);

        // Struct declaration
        if (node instanceof AStructWithIdTypeDecl)
            typeClass.structNode = node;

        //System.out.println("Type of " + idName + ": " + type);

        // Insert the symbol in the symbol table
        Symbol symbol = new Symbol(idName, node, typeClass, kind);
        symbolTable.put(idName, symbol);

        return symbol;
    }

    private TypeClass getTypeClass(PVarType varType)
    {
        TypeClass typeClass = new TypeClass();

        typeClass.varTypeNode = varType;
        //typeClass.baseType = getBaseType(varType);

        if (varType == null)
            return typeClass;

        PVarType current = varType;
        int nodeDepth = 0;  // The number of nodes traversed
        while (!(current instanceof ABaseTypeVarType))
        {
            if (current instanceof ASliceVarType)
            {
                current = ((ASliceVarType)current).getVarType();
                typeClass.totalArrayDimension++;
            }
            else if (current instanceof AArrayVarType)
            {
                current = ((AArrayVarType)current).getVarType();
                typeClass.totalArrayDimension++;
            }
            else if (current instanceof AStructVarType)
            {
                // Get the name of the type alias
                String typeAliasName = ((AStructVarType)current).getId().getText();
                
                Symbol typeAliasSymbol = symbolTable.get(typeAliasName);

                if (typeAliasSymbol == null)
                {
                    ErrorManager.printError("Type alias was never declared: \"" + typeAliasName + "\"");
                    break;
                }

                ArrayList<TypeAlias> typeAliasesToInherit = typeAliasSymbol.typeClass.typeAliases;
                System.out.println("Inheriting " + typeAliasesToInherit.size() + " from type: " + typeAliasSymbol);
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
                    typeAlias.arrayDimension = typeClass.totalArrayDimension;
                    typeClass.typeAliases.add(typeAlias);
                }

                typeClass.baseType = typeAliasSymbol.typeClass.baseType;
                typeClass.structNode = typeAliasSymbol.typeClass.structNode;
                typeClass.totalArrayDimension += typeAliasSymbol.typeClass.totalArrayDimension;

                break;
            }
            else 
            {
                System.out.println("Traversing node: " + current);
            }

            nodeDepth++;
        }

        // Determine the base type of the variable
        if (current instanceof ABaseTypeVarType)
        {
            typeClass.baseType = Type.stringToType(((ABaseTypeVarType)current).getType().getText());
        }

        return typeClass;
    }

    /**
     * Removes any aliasing from the varType and returns the 
     * most specific type of the variable (either a primitive or struct type)
     */
    /*private Type getBaseType(PVarType varType)
    {
        PVarType current = varType;

        while (!(current instanceof ABaseTypeVarType))
        {
            if (current instanceof ASliceVarType)
            {
                current = ((ASliceVarType)current).getVarType();
            }
            else if (current instanceof AArrayVarType)
            {
                current = ((AArrayVarType)current).getVarType();
            }
            else
            {
                // Get the name of the type alias
                String typeAlias = ((AStructVarType)current).getId().getText();
                
                Symbol typeAliasSymbol = symbolTable.get(typeAlias);
                if (typeAliasSymbol.type == Type.TYPE)
                {

                }
            }
        }

        return Type.INVALID;
    }*/

    public void outAIdExp(AIdExp node)
    {
        String id = node.getId().getText();
        Symbol symbol = checkVariableDeclared(id);

        if (symbol != null)
        {
            // Add a node->symbol mapping for future type checking
            symbolMap.put(node, new Symbol(symbol));

            System.out.println("Inserting (" + node + "," + symbol + ") into symbolMap");
        }
    }

    public void outAArrayIndexExp(AArrayIndexExp node)
    {
        Symbol lvalueSymbol = symbolMap.get(node.getLvalue());
        
        if (lvalueSymbol == null || lvalueSymbol.typeClass.totalArrayDimension == 0)
        {
            ErrorManager.printError("Indexing a non-array type: " + lvalueSymbol);
            return;
        }

        Symbol newSymbol = new Symbol(lvalueSymbol);
        newSymbol.typeClass.decrementDimension();

        symbolMap.put(node, newSymbol);
        System.out.println("Inserting (" + node + "," + newSymbol + ") into symbolMap");
    }

    public void caseAStructSelectorExp(AStructSelectorExp node)
    {
        node.getL().apply(this);
        // Get the symbol for the RHS of the struct selector
        Symbol symbol = symbolMap.get(node.getL());

        if (symbol == null || symbol.typeClass.structNode == null)
        {
            ErrorManager.printError("Using the . operator on a non-struct type: " + node.getL()
                + ", symbol = " + symbol);
            return;
        }

        if (symbol.typeClass.totalArrayDimension != 0)
        {
            ErrorManager.printError("Using the . operator on an array: " + symbol);
        }

        Node structNode = symbol.typeClass.structNode;

        System.out.println("Stepping into struct: " + structNode);
        currentStructScope = structHierarchy.get(structNode);
        System.out.println("Struct's symbol table: \n" + currentStructScope);

        String rightId = node.getR().getText();
        Symbol rightSymbol = currentStructScope.get(rightId);
        if (symbol == null)
        {
            ErrorManager.printError("\"" + rightId + "\" is not declared");
            return;
        }
        //node.getR().apply(this);

        currentStructScope = null;

        Symbol newSymbol = new Symbol(rightSymbol);
        symbolMap.put(node, newSymbol);

        System.out.println("Inserting (" + node + "," + newSymbol + ") into symbolMap");
        //String structId = node.getL().getText();
        //Symbol symbol = checkVariableDeclared(structId);

        //if (symbol != null)
        //{
            // Retrieve the symbol table containing the fields defined in the struct
            //Node structDeclarationNode = symbol.node;
            //SymbolTable structSymbolTable = structHierarchy.get(structDeclarationNode);
            
            //String rightId = getId(node.getR());
            //structSymbolTable.contains() 
        //}
    }

    /**
     * Returns the name of the left-most identifier in the given lvalue node
     */
    /*private String getLvalueId(PExp node)
    {
        if (node instanceof AIdExp) 
            return ((AIdExp)node).getId().getText();
        else if (node instanceof AStructSelectorExp)
            return ((AStructSelectorExp)node).getL().getText();
        else if (node instanceof AArrayIndex)
            retu
    }*/

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
            System.out.println(symbolTable + "\n----------------------");

        // Creates a new scope after enterring a block
        symbolTable = symbolTable.scope();
    }

    /**
     * Unscopes the symbol table
     */
    private void unscope()
    {
        if (dumpSymbolTable)
            System.out.println(symbolTable + "\n----------------------");

        // Pop the inner-most scope after leaving a block
        symbolTable = symbolTable.unscope();
    }

    /*public void inAVarDecl(AVarDecl node)
    {
        String id = node.getId().getText();

        // Throw an exception if the identifier was already declared
        if (symbolTable.contains(id))
        {
            ErrorManager.printError("\"" + id + "\" is already declared.");
        }    

        // Add the identifier to the symbol table so the program knows it exists
        symbolTable.put(id, node);
    }

    public void inAAssignStmt(AAssignStmt node)
    {
        String id = node.getId().getText();

        // Ensure that the variable is declared before use
        checkVariableDeclared(id);
    }

    public void inAReadStmt(AReadStmt node)
    {
        String id = node.getId().getText();

        // Ensure that the variable was declared before use
        checkVariableDeclared(id);
    }

    public void inAIdExp(AIdExp node)
    {
        String id = node.getId().getText();

        // Ensure that the variable was declared before use
        checkVariableDeclared(id);
    }*/
}
