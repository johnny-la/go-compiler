package golite.type;

import golite.node.*;
import golite.symbol.*;
import golite.analysis.*;
import golite.*;

import java.util.*;

public class TypeChecker extends DepthFirstAdapter
{
    public enum Operator { PLUS, MINUS, MULTIPLY, DIVIDE };

    private HashMap<Node, Symbol> symbolTable;

    private HashMap<Node, TypeClass> nodeTypes;

    public TypeChecker(HashMap<Node, Symbol> symbolTable)
    {
        this.symbolTable = symbolTable;

        nodeTypes = new HashMap<Node, TypeClass>();
    }

    // public void outAWhileStmt(AWhileStmt node)
    // {
    //     Type expType = getType(node.getExp());
        
    //     if (expType != Type.INT)
    //     {
    //         ErrorManager.printError("While-loop expression type: " 
    //                 + expType + " (" + node.getExp().toString().trim() +
    //                 "). Expected an integer.");
    //     }
    // }

    public void outAIfStmt(AIfStmt node)
    {
        TypeClass expType = getType(node.getExp());

        if (expType.baseType != Type.BOOL)
        {
            ErrorManager.printError("If-statement expression type: "
                    + expType + " (" + node.getExp().toString().trim() + 
                    "). Expected an integer.");
        }
    }

    // public void outAAssignStmt(AAssignStmt node)
    // {
    //     Type idType = getIdType(node.getId());   
    //     Type expType = getType(node.getExp());
        
    //     boolean compatibleTypes = false;

    //     if (idType == expType || (idType == Type.FLOAT && expType == Type.INT))
    //     {
    //         compatibleTypes = true;
    //     }
        
    //     if (!compatibleTypes)
    //     {
    //         ErrorManager.printError("\"" + node.getId().getText().trim() + 
    //                 "\" assigned type " + expType + ". Expecting type: " + idType);
    //     }
    // }

    public void outAMinusExp(AMinusExp node)
    {
        TypeClass leftType = getType(node.getL());
        TypeClass rightType = getType(node.getR());

        Type resultType = operationType(leftType.baseType, rightType.baseType, Operator.MINUS);

        if (resultType != Type.INVALID)
        {
            //nodeTypes.put(node, resultType);
        }
        else
        {   
            ErrorManager.printError("Subtraction of incompatible types: " +
                    leftType + ", " + rightType + ". (" + node.getL() + " - " 
                    + node.getR() + ")");
        }  
    }

    public void outAPlusExp(APlusExp node)
    {
        TypeClass leftType = getType(node.getL());
        TypeClass rightType = getType(node.getR());

        Type resultType = operationType(leftType.baseType, rightType.baseType, Operator.PLUS);
        
        if (resultType != Type.INVALID)
        {
            //nodeTypes.put(node, resultType);
        }
        else
        {
            ErrorManager.printError("Addition of incorrect types: " + leftType
                + ", " + rightType + ". (" + node.getL() + " + " + node.getR()
                + ")");
        }

    }

    public void outAMultExp(AMultExp node)
    {
        TypeClass leftType = getType(node.getL());
        TypeClass rightType = getType(node.getR());
        

        Type resultType = operationType(leftType.baseType, rightType.baseType, Operator.MULTIPLY);

        if (resultType != Type.INVALID)
        {
            //nodeTypes.put(node, resultType);
        }
        else
        {
            ErrorManager.printError("Multiplication of incompatible types: " +
                    leftType + ", " + rightType + ". (" + node.getL() + " * " +
                    node.getR() + ")");
        }
    }

    public void outADivideExp(ADivideExp node)
    {
        TypeClass leftType = getType(node.getL());
        TypeClass rightType = getType(node.getR());

        Type resultType = operationType(leftType, rightType, Operator.DIVIDE);

        if (resultType != Type.INVALID)
        {
            //nodeTypes.put(node, resultType);
        }
        else 
        {
            ErrorManager.printError("Division of incompatible types: " +
                    leftType + ", " + rightType + ". (" + node.getL() + " / " +
                    node.getR() + ")");
        }
    }

    /**
     * The resulting type of performing a binary operator on both types 
     */
    public Type operationType(Type leftType, Type rightType, Operator operator)
    {
        if (leftType == null || rightType == null)
        {
            return Type.INVALID;
        }

        if (leftType == rightType &&
                (operator == Operator.PLUS || leftType != Type.STRING))
        {
            return leftType;
        }
        else if ((leftType == Type.FLOAT64 && rightType == Type.INT) ||
                 (leftType == Type.INT && rightType == Type.FLOAT64))
        {
            return Type.FLOAT64;
        }

        boolean stringAndInt = (leftType == Type.STRING && rightType == Type.INT)
            || (leftType == Type.INT && rightType == Type.STRING);
        if (operator == Operator.MULTIPLY && stringAndInt)
        {
            return Type.STRING;
        }
        
        // Failure: operation on two incompatible types
        return Type.INVALID;
    }

    // public void outAUminusExp(AUminusExp node)
    // {
    //     Type expType = getType(node.getExp());

    //     if (expType == Type.INT || expType == Type.FLOAT)
    //     {
    //         nodeTypes.put(node, expType);
    //     }
    //     else
    //     {
    //         ErrorManager.printError("Error: unary minus on incompatible type: "
    //                + expType + ". ( " + node.getExp() + ")"); 
    //     }
    // }

    public void outAIdExp(AIdExp node)
    {
        TypeClass type = symbolTable.get(node).typeClass;

        if (type.baseType == Type.INVALID)
        {
            ErrorManager.printError("Identifier \"" + node.getIdType() + "\"has"
                   + " invalid type");
            return;
        }

        if(getIdName(node.getIdType()).equals("true") || getIdName(node.getIdType()).equals("false")){
            nodeTypes.put(node, Type.BOOL);
        }

        nodeTypes.put(node, type);        
    }

    /**
     * Returns the name of the id_type node
     */
    private String getIdName(PIdType node)
    {
        String idName = null;

        if (node instanceof AIdIdType) { idName = ((AIdIdType)node).getId().getText(); }
        else if (node instanceof ATypeIdType) { idName = ((ATypeIdType)node).getType().getText(); }
    
        return idName;
    }

    /**
     * Returns the type (int, float, string) of an identifier token
     */
    public Type getIdType(PIdType idTypeNode)
    {
        return getIdType(getIdName(idTypeNode));
    }

    /**
     * Returns the type of the given identifier
     */
    public Type getIdType(String id)
    {
        // Retrieves the declaration node that created this variable
        // AVarDecl declNode = (AVarDecl)symbolTable.get(id);
        // // Gets the type of the identifier
        // Type type = stringToType(declNode.getType().getText());

        return null;
    }

    public void outAIntExp(AIntExp node)
    {
        addType(node, Type.INT);
    }

    public void outAFloat64LiteralExp(AFloat64LiteralExp node){
        addType(node, Type.FLOAT64);
    }

    public void outARuneLiteralExp(ARuneLiteralExp node){
        addType(node, Type.RUNE);
    }

    public void outARawStringLitExp(ARawStringLitExp node){
        addType(node, Type.STRING);
    }

    public void outAInterpretedStringLiteralExp(AInterpretedStringLiteralExp node){
        addType(node, Type.STRING);
    }



    // public void outAFloatExp(AFloatExp node)
    // {
    //     nodeTypes.put(node, Type.FLOAT);
    // }

    /** 
     * Returns the type of the given node
     */
    public TypeClass getType(Node node)
    {
        return nodeTypes.get(node);
    }

    public void addType(Node node, Type type){
        nodeTypes.put(node, type);
    }

    public String toString()
    {
        StringBuilder output = new StringBuilder();

        for (Map.Entry<Node, Type> entries : nodeTypes.entrySet())
        {
            output.append(entries.getKey()  + ": " + entries.getValue() + "\n");    
        }

        return output.toString();
    }
}
