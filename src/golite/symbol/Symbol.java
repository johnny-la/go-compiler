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
        TYPE,         // Type alias
        STRUCT_FIELD, // Variable inside a struct
        FIELD,        // Top-level variable
        FORMAL,       // Function argument
        LOCAL;        // Variable declared in function
    }

    public String name; 
    public Node node;
    public TypeClass typeClass;
    public SymbolKind kind;

    public Symbol() {}

    public Symbol(String name, Node node, TypeClass typeClass, SymbolKind kind)
    {
        this.name = name;
        this.node = node;
        this.typeClass = typeClass;
        this.kind = kind;
    }

    public String toString()
    {
        return name + ", " +  kind + ", " + typeClass;
    }
}
