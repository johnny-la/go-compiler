package golite.symbol;

import java.util.*;
import golite.node.*;
import golite.symbol.Symbol;

public class SymbolTable
{
    private HashMap<String, Symbol> table;
    private SymbolTable next;   // Pointer to outer scope

    public SymbolTable()
    {
        table = new HashMap<String, Symbol>();
    }

    /**
     * Creates and returns a new SymbolTable and return it.
     * (Equivalent to pushing a new symbol table on the stack)
     * Call this function when entering a new scope
     */
    public SymbolTable scope()
    {
        SymbolTable newTable = new SymbolTable();
        newTable.next = this;
        return newTable; 
    }

    /**
     * Returns the symbol table at the next outer scope.
     * (Equivalent to popping the symbol table stack)
     * Call this function when leaving a block
     */
    public SymbolTable unscope()
    {   
        return next;
    }
    /** 
     * Inserts a symbol in the table
     */
    public void put(String name, Symbol symbol)
    {
        if (!table.containsKey(name))
        {
            table.put(name, symbol);
        }
    }

    /**
     * Returns the symbol at the closest scope
     */
    public Symbol get(String name)
    {
        SymbolTable current = this;

        while (current != null)
        {
            if (current.table.containsKey(name))
                return current.table.get(name);

            current = current.next;
        }
        
        // Symbol not found
        return null;
    }

    /**
     * Returns true if the symbol table in the current scope contains the given symbol
     */
    public boolean contains(String name)
    {
        return table.containsKey(name);
    }

    public String toString()
    {
        StringBuilder output = new StringBuilder();

        output.append("ID\t\tSYMBOL\n");

        for (Map.Entry<String, Symbol> entry : table.entrySet())
        {
            String key = entry.getKey();
            Symbol value = entry.getValue();

            output.append(key + "\t\t\""+value + "\"\n");
        }
        return output.toString();
    }

}
