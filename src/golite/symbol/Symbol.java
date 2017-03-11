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

    // Deep copy of the given symbol
    public Symbol(Symbol other)
    {
        if (other == null) { return; }

        name = other.name;
        node = other.node;
        typeClass = new TypeClass(other.typeClass);
        kind = other.kind;
    }

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
