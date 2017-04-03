package golite.type;

import golite.node.*;
import java.util.*;

public class TypeClass
{
    public PVarType varTypeNode;    // The top-most PVarType node
    public Type baseType;   // The base type of the variable (INT, FLOAT64, etc.)
    public List<PInnerFields> innerFields; // If this is a struct-type, this stores the struct declaration node
    public LinkedList<Dimension> totalArrayDimension; // The dimension of the base type
    public FunctionSignature functionSignature; // Only populated if baseType == FUNCTION
    public ArrayList<TypeAlias> typeAliases = new ArrayList<TypeAlias>(); 
    //public Node typeAliasNode;      // If this variable is a custom type, this stores the type alias declaration node 
    //public int aliasArrayDimension; // The array dimension of the outer-most type alias


    public TypeClass() {
        this.totalArrayDimension = new LinkedList<Dimension>();
    }

    // Deep copy of the TypeClass
    public TypeClass(TypeClass other)
    {
        if (other == null) { return;}
        
        varTypeNode = other.varTypeNode;
        baseType = other.baseType;
        innerFields = other.innerFields;
        totalArrayDimension = other.totalArrayDimension;
        functionSignature = (other.functionSignature != null)? 
                                new FunctionSignature(other.functionSignature) : null;
        //typeAliasNode = other.typeAliasNode;
        //aliasArrayDimension = other.aliasArrayDimension;

        for (int i = 0; i < other.typeAliases.size(); i++)
        {
            typeAliases.add(new TypeAlias(other.typeAliases.get(i)));
        }
    }

    public void incrementDimension(Dimension d) {
        totalArrayDimension.add(d);
    }

    /**
     * Decrements the dimension of this type
     */
    // public void decrementDimension()
    // {
    //     totalArrayDimension--;

    //     for (int i = typeAliases.size()-1; i >= 0; i--)
    //     {
    //         TypeAlias typeAlias = typeAliases.get(i);

    //         // Get rid of the type aliases that are not arrays
    //         if (typeAlias.arrayDimension <= 0)
    //         {
    //             typeAliases.remove(i);
    //         }
    //         else
    //         {
    //             typeAlias.arrayDimension--;
                
    //             //if (typeAlias.arrayDimension <= 0)
    //                 //typeAliases.remove(i);

    //             break;
    //         }
    //     }
    // }

    public boolean isNull()
    {
        return (baseType == null && innerFields == null);
    }

    public String toString()
    {
        String output = "";

        for (int i = typeAliases.size() - 1; i >= 0; i--)
        {
            output += typeAliases.get(i) + " ";
        }
        
        for (int i = 0; i < totalArrayDimension.size(); i++)
        {
            output += "[]";
        }

        if (functionSignature == null)
        {
            output += (baseType != null)? baseType : "No Type";
        }

        else
        {
            output += " " + functionSignature;
        }

        return output;
    }
}