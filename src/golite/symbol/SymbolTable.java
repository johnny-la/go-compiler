package golite.symbol;

import java.util.*;
import golite.node.*;

public class SymbolTable
{
    private HashMap<String, Node> table;

    public SymbolTable()
    {
        table = new HashMap<String, Node>();
    }

    /** 
     * Inserts a symbol in the table
     */
    public void put(String name, Node value)
    {
        if (!table.containsKey(name))
        {
            table.put(name, value);
        }
    }

    public Node get(String name)
    {
        return table.get(name);
    }

    /**
     * Returns true if the symbol table contains the given symbol
     */
    public boolean contains(String name)
    {
        return table.containsKey(name);
    }

    public String toString()
    {
        StringBuilder output = new StringBuilder();

        output.append("ID\t\tNODE\n");

        for (Map.Entry<String, Node> entry : table.entrySet())
        {
            String key = entry.getKey();
            Node value = entry.getValue();

            output.append(key + "\t\t" + value.getClass() + ": \""+value + "\"\n");
        }

        return output.toString();
    }

}
