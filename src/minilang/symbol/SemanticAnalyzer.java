package minilang.symbol;

import minilang.*;
import minilang.node.*;
import minilang.analysis.*;

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
   
    public void inAVarDecl(AVarDecl node)
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
    }

    /**
     * Throws an exception if the variable was not declared
     */
    private void checkVariableDeclared(String id)
    {
        if (symbolTable.contains(id) == false)
        {
            ErrorManager.printError("\"" + id + "\" is not declared");
        }
    }
}
