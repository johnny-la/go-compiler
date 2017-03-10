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
        FIELD,  // Top-level variable
        METHOD,
        FORMAL, // Function argument
        LOCAL   // Variable declared in function
    }

    public String name; 
    public Node value;
    public Type type;
    public SymbolKind kind;

    public Symbol() {}

    public Symbol(String name, Node value, Type type, SymbolKind kind)
    {
        this.name = name;
        this.value = value;
        this.type = type;
        this.kind = kind;
    }
}
