package golite.symbol;

import golite.node.*;
import golite.type.*;
import java.util.*;

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

    public ArrayList<Symbol> symbolsToInheritType = new ArrayList<Symbol>();

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

    // Sets the type of the symbol, along with all other symbols that
    // reference this symbol
    public void setType(TypeClass typeClass)
    {
        this.typeClass = typeClass;

        for (int i = 0; i < symbolsToInheritType.size(); i++)
        {
            Symbol symbolToInherit = symbolsToInheritType.get(i);
            TypeClass newType = new TypeClass(typeClass);
            System.out.println("Setting dynamic type of " + symbolToInherit + " to: " + newType);
            symbolToInherit.typeClass = newType;
        } 
    }

    public String toString()
    {
        String output = name + ", " +  kind + ", " + typeClass;

        if (symbolsToInheritType.size() != 0)
        {
            output += ", Inheriting types: {";
            for (int i = 0; i < symbolsToInheritType.size(); i++)
            {
                output += symbolsToInheritType.get(i);
                if (i != symbolsToInheritType.size()-1) 
                    output += ", ";
            }
            output += "}";
        }

        return output;
    }
}
