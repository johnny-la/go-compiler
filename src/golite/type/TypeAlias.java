package golite.type;

import golite.node.*;
import java.util.*;

// Stores a mapping between a type alias and the dimension 
// of the array (if applicable)
public class TypeAlias
{
    public Node node;
    public LinkedList<Dimension> arrayDimensions;

    public TypeAlias() {}

    public TypeAlias(TypeAlias other)
    {
        this.node = other.node;
        this.arrayDimensions = other.arrayDimensions;
    }

    public String toString()
    {
        String output = "";

        if (arrayDimensions !=null) {
            for (int i = 0; i < arrayDimensions.size(); i++) {
                output += arrayDimensions.get(i).toString();
            }
        }
        
        output += "(" + node.toString().trim() + ")";

        return output;
    }
}