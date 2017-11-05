package golite.type;

import golite.node.*;
import java.util.*;

/**
 * Stores a mapping between a type alias and the dimension 
 * of its mapped array (if applicable)
 */
public class TypeAlias
{
    public Node node;
    public LinkedList<Dimension> arrayDimensions = new LinkedList<Dimension>();

    public TypeAlias() {}

    public TypeAlias(TypeAlias other)
    {
        this.node = other.node;
        setArrayDimensions(other.arrayDimensions);
    }

    public void setArrayDimensions(LinkedList<Dimension> dimensions)
    {
        arrayDimensions.clear();
        for (int i = 0; i < dimensions.size(); i++)
        {
            this.arrayDimensions.add(dimensions.get(i));   
        }
    }

    public String toString()
    {
        String output = "";

        if (arrayDimensions != null) 
        {
            for (int i = 0; i < arrayDimensions.size(); i++) 
            {
                output += arrayDimensions.get(i).toString();
            }
        }
        
        output += "(" + node.toString().trim() + ")";

        return output;
    }
}
