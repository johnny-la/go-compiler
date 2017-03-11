package golite.type;

import golite.node.*;

public class TypeClass
{
    public PVarType varTypeNode;    // The top-most PVarType node
    public Type baseType;   // The base type of the variable (INT, FLOAT64, etc.)
    public Node structNode; // If this is a struct-type, this stores the struct declaration node
    public int totalArrayDimension; // The dimension of the base type
    public Node typeAliasNode;      // If this variable is a custom type, this stores the type alias declaration node 
    public int aliasArrayDimension; // The array dimension of the outer-most type alias

    public TypeClass() {}

    // Deep copy of the TypeClass
    public TypeClass(TypeClass other)
    {
        if (other == null) { return; }
        
        varTypeNode = other.varTypeNode;
        baseType = other.baseType;
        structNode = other.structNode;
        totalArrayDimension = other.totalArrayDimension;
        typeAliasNode = other.typeAliasNode;
        aliasArrayDimension = other.aliasArrayDimension;
    }

    public String toString()
    {
        String output = "";

        if (typeAliasNode != null)
        {
            for (int i = 0; i < aliasArrayDimension; i++)
            {
                output += "[]";
            }
            
            output += "(" + typeAliasNode.toString().trim() + ") ";
        }
        
        for (int i = 0; i < totalArrayDimension; i++)
        {
            output += "[]";
        }

        output += (baseType != null)? baseType:"Struct: " + structNode;
    
        return output;
    }
}