package golite.type;

import golite.node.*;

// Stores a mapping between a type alias and the dimension 
// of the array (if applicable)
public class TypeAlias
{
    public Node node;
    public int arrayDimension;

    public TypeAlias() {}

    public TypeAlias(TypeAlias other)
    {
        this.node = other.node;
        this.arrayDimension = other.arrayDimension;
    }

    public String toString()
    {
        String output = "";

        for (int i = 0; i < arrayDimension; i++)
        {
            output += "[]";
        }
        
        output += "(" + node.toString().trim() + ")";

        return output;
    }
}