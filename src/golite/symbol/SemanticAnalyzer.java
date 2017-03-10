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
    private SymbolTable symbolTable;

    // If true, the top-most frame of the symbol table is dumped when a scope is left.
    private boolean dumpSymbolTable;

    // True if we are traversing inside a function block
    private boolean justEnterredFunction;
    // True if we are inside a function declaration
    private boolean inAFunction;

    public SemanticAnalyzer()
    {
        symbolTable = new SymbolTable();
    }

    public SemanticAnalyzer(SymbolTable symbolTable, boolean dumpSymbolTable)
    {
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
        ABaseTypeVarType type = getVariableType(node);
        declareVariable(node.getIdType(), type, SymbolKind.LOCAL, node);
    }

    public void inAInlineListWithExpVarDecl(AInlineListWithExpVarDecl node)
    {
        ABaseTypeVarType type = getVariableType(node);
        declareVariable(node.getIdType(), type, SymbolKind.LOCAL, node);
    }

    public void inATypeAliasTypeDecl(ATypeAliasTypeDecl node)
    {
        declareVariable(node.getIdType(), node.getVarType(), SymbolKind.TYPE, node);
    }

    public void inAStructVarDeclTypeDecl(AStructVarDeclTypeDecl node)
    {
        for (int i = 0; i < node.getIdList().size(); i++)
        {
            PIdType id = node.getIdList().get(i);
            declareVariable(id, node.getVarType(), SymbolKind.STRUCT, node);
        }
    }

    public void inAStructWithIdTypeDecl(AStructWithIdTypeDecl node)
    {
        declareVariable(node.getIdType(), null, SymbolKind.STRUCT, node);
    }
        
    /**
     * Returns the type of a variable declaration
     */
    public ABaseTypeVarType getVariableType(PVarDecl node)
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
        ABaseTypeVarType type = null;

        if (current instanceof AVarWithTypeVarDecl)
        {
            type = (ABaseTypeVarType)((AVarWithTypeVarDecl)current).getVarType();
        }
        else if (current instanceof AVarWithTypeAndExpVarDecl)
        {
            type = (ABaseTypeVarType)((AVarWithTypeAndExpVarDecl)current).getVarType();
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
    public void declareVariable(PIdType id, PVarType varType, SymbolKind kind, Node node)
    {
        //System.out.println("Symbol table at var decl:\n" + symbolTable);
        String idName = null;
        
        if (id instanceof AIdIdType) { idName = ((AIdIdType)id).getId().getText(); }
        else if (id instanceof ATypeIdType) { idName = ((ATypeIdType)id).getType().getText(); }

        // Throw an exception if the identifier was already declared
        if (symbolTable.contains(idName))
        {
            ErrorManager.printError("\"" + id + "\" is already declared in this block.");
            return;
        }    

        Type type = Type.INVALID;
        
        if (varType instanceof ABaseTypeVarType)
            type = Type.stringToType(((ABaseTypeVarType)varType).getType().getText());

        //System.out.println("Type of " + idName + ": " + type);

        // Insert the symbol in the symbol table
        Symbol symbol = new Symbol(idName, node, type, kind);
        symbolTable.put(idName, symbol);
    }

    /**
     * Throws an exception if the variable was not declared
     */
    private boolean checkVariableDeclared(String id)
    {
        if (symbolTable.contains(id) == false)
        {
            ErrorManager.printError("\"" + id + "\" is not declared");
            return true;
        }

        return false;
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
