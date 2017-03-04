package golite.symbol;

import golite.*;
import golite.node.*;
import golite.analysis.*;
import golite.symbol.Symbol;
import golite.symbol.Symbol.SymbolKind;

import java.util.*;

public class SemanticAnalyzer extends DepthFirstAdapter
{
    private SymbolTable symbolTable;

    public SemanticAnalyzer()
    {
        symbolTable = new SymbolTable();
    }

    public SemanticAnalyzer(SymbolTable symbolTable)
    {
        this.symbolTable = symbolTable;
    }

    public void inAVarWithTypeVarDecl(AVarWithTypeVarDecl node)
    {
        declareVariable(node.getIdType(), node.getVarType(), node);
    }

    public void inAVarWithOnlyExpVarDecl(AVarWithOnlyExpVarDecl node)
    {
        declareVariable(node.getIdType(), null, node);
    }

    public void inAVarWithTypeAndExpVarDecl(AVarWithTypeAndExpVarDecl node)
    {
        declareVariable(node.getIdType(), node.getVarType(), node);
    }

    public void inAInlineListNoExpVarDecl(AInlineListNoExpVarDecl node)
    {
        declareVariable(node.getIdType(), null, node);
    }

    public void inAInlineListWithExpVarDecl(AInlineListWithExpVarDecl node)
    {
        declareVariable(node.getIdType(), null, node);
    }

    /** 
     * Inserts the given ID in the the symbol table,
     * and throws an error if it's already declared in
     * the current scope
     */
    public void declareVariable(PIdType id, PVarType varType, Node node)
    {
        //System.out.println("Symbol table at var decl:\n" + symbolTable);
        String idName = null;
        
        if (id instanceof AIdIdType) { idName = ((AIdIdType)id).getId().getText(); }
        else if (id instanceof ATypeIdType) { idName = ((ATypeIdType)id).getType().getText(); }

        // Throw an exception if the identifier was already declared
        if (symbolTable.contains(idName))
        {
            ErrorManager.printError("\"" + id + "\" is already declared.");
            return;
        }    

        // Find the kind of the variable
        SymbolKind kind = SymbolKind.FIELD;

        // Insert the symbol in the symbol table
        Symbol symbol = new Symbol(idName, node, kind);
        symbolTable.put(idName, node);
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
