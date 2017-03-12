package golite.type;

import golite.node.*;
import java.util.*;

public class FunctionSignature 
{
    public TypeClass returnType = new TypeClass();
    
    public ArrayList<TypeClass> parameterTypes = new ArrayList<TypeClass>();

    public FunctionSignature() {}

    // Deep copy of a function signature
    public FunctionSignature(FunctionSignature other)
    {
        if (other == null) { return; }

        returnType = new TypeClass(other.returnType);

        // Deep copy the parameter types
        for (int i = 0; i < other.parameterTypes.size(); i++)
        {
            parameterTypes.add(new TypeClass(other.parameterTypes.get(i)));
        }
    }

    public String toString()
    {
        StringBuffer output = new StringBuffer();
        
        // Parameter types
        output.append("(");
        for (int i = 0; i < parameterTypes.size(); i++)
        {
            output.append(parameterTypes.get(i).toString());
            output.append(", ");
        }
        output.append("):");

        // Return type
        output.append(returnType.toString());

        return output.toString();
    }
}