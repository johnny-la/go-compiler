package golite.symbol;

import golite.node.*;
import golite.type.*;

/**
 * Represents a symbol stored in the symbol table 
 */
public class Symbol
{
    public enum SymbolKind
    {
        FUNCTION,
        STRUCT,
        TYPE,   // Type alias
        FIELD,  // Top-level variable
        FORMAL, // Function argument
        LOCAL;  // Variable declared in function
    }

    public String name; 
    public Node node;
    public Type type;
    public SymbolKind kind;

    public Symbol() {}

    public Symbol(String name, Node node, Type type, SymbolKind kind)
    {
        this.name = name;
        this.node = node;
        this.type = type;
        this.kind = kind;
    }

    public String toString()
    {
        return name + ", " +  kind + ", " + type;
    }
}
