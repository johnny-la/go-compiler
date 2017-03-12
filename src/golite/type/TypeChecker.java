package golite.type;

import golite.node.*;
import golite.symbol.*;
import golite.analysis.*;
import golite.*;

import java.util.*;

public class TypeChecker extends DepthFirstAdapter
{
    public enum BinaryOps { BOOL, COMPARABLE, ORDERED, NUMERIC, NUMORSTRING, INTEGER }

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

    // public void outAIfStmt(AIfStmt node)
    // {
    //     TypeClass expType = getType(node.getExp());

    //     if (expType.baseType != Type.BOOL)
    //     {
    //         ErrorManager.printError("If-statement expression type: "
    //                 + expType + " (" + node.getExp().toString().trim() + 
    //                 "). Expected an integer.");
    //     }
    // }

    public boolean isBool(Type isBool) {
        if (isBool == Type.BOOL) {
            return true;
        }
        return false;
    }

    public boolean isComparable(Type isComparable) {
        if (isComparable != Type.VOID && isComparable != Type.INVALID) {
            return true;
        }
        return false;
    }

    public boolean isOrdered(Type isOrdered) {
        if (isOrdered == Type.INT || isOrdered == Type.STRING || isOrdered == Type.FLOAT64 
            || isOrdered == Type.RUNE) {
            return true;
        }
        return false;
    }

    public boolean isNumericOrString(Type isNumericOrString) {
        if (isNumericOrString == Type.INT || isNumericOrString == Type.STRING 
            || isNumericOrString == Type.FLOAT64 || isNumericOrString == Type.RUNE) {
            return true;
        }
        return false;
    }

    public boolean isNumeric(Type isNumeric) {
        if (isNumeric == Type.INT || isNumeric == Type.FLOAT64 || isNumeric == Type.RUNE) {
            return true;
        }
        return false;
    }

    public boolean isInteger(Type isInteger) {
        if (isInteger == Type.INT) {
            return true;
        }
        return false;
    }

    public boolean isComparable(TypeClass left, TypeClass right, BinaryOps op) {

        if (left.totalArrayDimension > 0 || right.totalArrayDimension > 0) {
            ErrorManager.printError("Unable to perform binary operations on array type");
            return false;
        }

        List<TypeAlias> leftAliases = left.typeAliases, rightAliases = right.typeAliases;
        int leftSize = leftAliases.size(), rightSize = rightAliases.size();

        if (leftSize > 0 && rightSize > 0) {
            if (leftAliases.get(leftSize - 1).node != rightAliases.get(rightSize - 1).node) {
                ErrorManager.printError("Aliases aren't compatible with each other");
                return false;
            }
        } else if (leftSize > 0 || rightSize > 0) {
            ErrorManager.printError("Aliases aren't compatible with base types");
            return false;
        }

        switch (op) {
            case BOOL:
                return (isBool(left.baseType) && isBool(right.baseType));

            case COMPARABLE:
                if (left.baseType == Type.STRUCT && right.baseType == Type.STRUCT 
                        && left.structNode == right.structNode) return true;
                return (isComparable(left.baseType) && isComparable(right.baseType) 
                        && left.baseType == right.baseType);

            case ORDERED:
                return (isOrdered(left.baseType) && isOrdered(right.baseType) 
                    && (left.baseType == right.baseType));

            case NUMERIC:
                return (isNumeric(left.baseType) && isNumeric(right.baseType) 
                    && (left.baseType == right.baseType));

            case NUMORSTRING:
                return (isNumericOrString(left.baseType) && isNumericOrString(right.baseType) 
                    && (left.baseType == right.baseType));

            case INTEGER:
                return (isInteger(left.baseType) && isInteger(right.baseType));

            default: 
                return false;
        }
    }

    public void outALogicalOrExp(ALogicalOrExp node) {
        TypeClass leftType = getType(node.getL());
        TypeClass rightType = getType(node.getR());

        if (isComparable(leftType, rightType, BinaryOps.BOOL)) {
            nodeTypes.put(node, leftType);
        } else {
            ErrorManager.printError("Comparison of incompatible types: " +
                    leftType.baseType + ", " + rightType.baseType + ". (" + node.getL() + " - " 
                    + node.getR() + ")");
        }
    }

    public void outALogicalAndExp(ALogicalAndExp node) {
        TypeClass leftType = getType(node.getL());
        TypeClass rightType = getType(node.getR());

        if (isComparable(leftType, rightType, BinaryOps.BOOL)) {
            nodeTypes.put(node, leftType);
        } else {
            ErrorManager.printError("Comparison of incompatible types: " +
                    leftType.baseType + ", " + rightType.baseType + ". (" + node.getL() + " - " 
                    + node.getR() + ")");
        }
    }

    public void outAEqualsEqualsExp(AEqualsEqualsExp node) {
        TypeClass leftType = getType(node.getL());
        TypeClass rightType = getType(node.getR());

        if (isComparable(leftType, rightType, BinaryOps.COMPARABLE)) {
            nodeTypes.put(node, leftType);
        } else {
            ErrorManager.printError("Comparison of incompatible types: " +
                    leftType.baseType + ", " + rightType.baseType + ". (" + node.getL() + " - " 
                    + node.getR() + ")");
        }
    }

    public void outANotEqualExp(ANotEqualExp node) {
        TypeClass leftType = getType(node.getL());
        TypeClass rightType = getType(node.getR());

        if (isComparable(leftType, rightType, BinaryOps.COMPARABLE)) {
            nodeTypes.put(node, leftType);
        } else {
            ErrorManager.printError("Comparison of incompatible types: " +
                    leftType.baseType + ", " + rightType.baseType + ". (" + node.getL() + " - " 
                    + node.getR() + ")");
        }
    }

    public void outALessExp(ALessExp node) {
        TypeClass leftType = getType(node.getL());
        TypeClass rightType = getType(node.getR());

        if (isComparable(leftType, rightType, BinaryOps.ORDERED)) {
            nodeTypes.put(node, leftType);
        } else {
            ErrorManager.printError("Comparison of incompatible types: " +
                    leftType.baseType + ", " + rightType.baseType + ". (" + node.getL() + " - " 
                    + node.getR() + ")");
        }
    }

    public void outALessEqualsExp(ALessEqualsExp node) {
        TypeClass leftType = getType(node.getL());
        TypeClass rightType = getType(node.getR());

        if (isComparable(leftType, rightType, BinaryOps.ORDERED)) {
            nodeTypes.put(node, leftType);
        } else {
            ErrorManager.printError("Comparison of incompatible types: " +
                    leftType.baseType + ", " + rightType.baseType + ". (" + node.getL() + " - " 
                    + node.getR() + ")");
        }
    }

    public void outAGreaterExp(AGreaterExp node) {
        TypeClass leftType = getType(node.getL());
        TypeClass rightType = getType(node.getR());

        if (isComparable(leftType, rightType, BinaryOps.ORDERED)) {
            nodeTypes.put(node, leftType);
        } else {
            ErrorManager.printError("Comparison of incompatible types: " +
                    leftType.baseType + ", " + rightType.baseType + ". (" + node.getL() + " - " 
                    + node.getR() + ")");
        }
    }

    public void outAGreaterEqualsExp(AGreaterEqualsExp node) {
        TypeClass leftType = getType(node.getL());
        TypeClass rightType = getType(node.getR());

        if (isComparable(leftType, rightType, BinaryOps.ORDERED)) {
            nodeTypes.put(node, leftType);
        } else {
            ErrorManager.printError("Comparison of incompatible types: " +
                    leftType.baseType + ", " + rightType.baseType + ". (" + node.getL() + " - " 
                    + node.getR() + ")");
        }
    }

    public void outAMinusExp(AMinusExp node) {
        TypeClass leftType = getType(node.getL());
        TypeClass rightType = getType(node.getR());

        if (isComparable(leftType, rightType, BinaryOps.NUMERIC)) {
            nodeTypes.put(node, leftType);
        } else {   
            ErrorManager.printError("Subtraction of incompatible types: " +
                    leftType + ", " + rightType + ". (" + node.getL() + " - " 
                    + node.getR() + ")");
        }  
    }

    public void outAPlusExp(APlusExp node) {
        TypeClass leftType = getType(node.getL());
        TypeClass rightType = getType(node.getR());

        if (isComparable(leftType, rightType, BinaryOps.NUMORSTRING)) {
            nodeTypes.put(node, leftType);
        } else {
            ErrorManager.printError("Addition of incorrect types: " + leftType
                + ", " + rightType + ". (" + node.getL() + " + " + node.getR()
                + ")");
        }
    }

    public void outAMultExp(AMultExp node) {
        TypeClass leftType = getType(node.getL());
        TypeClass rightType = getType(node.getR());

        if (isComparable(leftType, rightType, BinaryOps.NUMERIC)) {
            nodeTypes.put(node, leftType);
        } else {
            ErrorManager.printError("Multiplication of incompatible types: " +
                    leftType + ", " + rightType + ". (" + node.getL() + " * " +
                    node.getR() + ")");
        }
    }

    public void outADivideExp(ADivideExp node) {
        TypeClass leftType = getType(node.getL());
        TypeClass rightType = getType(node.getR());

        if (isComparable(leftType, rightType, BinaryOps.NUMERIC)) {
            nodeTypes.put(node, leftType);
        } else {
            ErrorManager.printError("Division of incompatible types: " +
                    leftType + ", " + rightType + ". (" + node.getL() + " / " +
                    node.getR() + ")");
        }
    }

    public void outAModuloExp(AModuloExp node) {
        TypeClass leftType = getType(node.getL());
        TypeClass rightType = getType(node.getR());

        if (isComparable(leftType, rightType, BinaryOps.INTEGER)) {
            nodeTypes.put(node, leftType);
        } else {
            ErrorManager.printError("Modulo of incompatible type: " +
                    leftType + ", " + rightType + ". (" + node.getL() + " / " +
                    node.getR() + ")");
        }
    }

    public void outAAmpersandExp(AAmpersandExp node) {
        TypeClass leftType = getType(node.getL());
        TypeClass rightType = getType(node.getR());

        if (isComparable(leftType, rightType, BinaryOps.INTEGER)) {
            nodeTypes.put(node, leftType);
        } else {
            ErrorManager.printError("Bitwise operation of incompatible types: " +
                    leftType + ", " + rightType + ". (" + node.getL() + " / " +
                    node.getR() + ")");
        }
    }

    public void outAAmpersandCaretExp(AAmpersandCaretExp node) {
        TypeClass leftType = getType(node.getL());
        TypeClass rightType = getType(node.getR());

        if (isComparable(leftType, rightType, BinaryOps.INTEGER)) {
            nodeTypes.put(node, leftType);
        } else {
            ErrorManager.printError("Bitwise operation of incompatible types: " +
                    leftType + ", " + rightType + ". (" + node.getL() + " / " +
                    node.getR() + ")");
        }      
    }

    public void outAShiftLeftExp(AShiftLeftExp node) {
        TypeClass leftType = getType(node.getL());
        TypeClass rightType = getType(node.getR());

        if (isComparable(leftType, rightType, BinaryOps.INTEGER)) {
            nodeTypes.put(node, leftType);
        } else {
            ErrorManager.printError("Bitwise operation of incompatible types: " +
                    leftType + ", " + rightType + ". (" + node.getL() + " / " +
                    node.getR() + ")");
        }       
    }

    public void outAShiftRightExp(AShiftRightExp node) {
        TypeClass leftType = getType(node.getL());
        TypeClass rightType = getType(node.getR());

        if (isComparable(leftType, rightType, BinaryOps.INTEGER)) {
            nodeTypes.put(node, leftType);
        } else {
            ErrorManager.printError("Bitwise operation of incompatible types: " +
                    leftType + ", " + rightType + ". (" + node.getL() + " / " +
                    node.getR() + ")");
        }
    }

    public void outAPipeExp(APipeExp node) {
        TypeClass leftType = getType(node.getL());
        TypeClass rightType = getType(node.getR());

        if (isComparable(leftType, rightType, BinaryOps.INTEGER)) {
            nodeTypes.put(node, leftType);
        } else {
            ErrorManager.printError("Bitwise operation of incompatible types: " +
                    leftType + ", " + rightType + ". (" + node.getL() + " / " +
                    node.getR() + ")");
        }  
    }

    public void outACaretExp(ACaretExp node) {
        TypeClass leftType = getType(node.getL());
        TypeClass rightType = getType(node.getR());

        if (isComparable(leftType, rightType, BinaryOps.INTEGER)) {
            nodeTypes.put(node, leftType);
        } else {
            ErrorManager.printError("Bitwise operation of incompatible types: " +
                    leftType + ", " + rightType + ". (" + node.getL() + " / " +
                    node.getR() + ")");
        }
    }

    public void outAFunctionCallSecondaryExp(AFunctionCallSecondaryExp node) {
        int numberOfArgs = node.getExpList().size();
        TypeClass lhs = nodeTypes.get(functionNode);
        //check if lhs is a name of a function
    }

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
            addType(node, Type.BOOL);
            return;
        }

        addType(node, type.baseType);        
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
    // public Type getIdType(PIdType idTypeNode)
    // {
    //     return getIdType(getIdName(idTypeNode));
    // }

    // // /**
    // //  * Returns the type of the given identifier
    // //  *
    // public Type getIdType(String id)
    // {
    //     // Retrieves the declaration node that created this variable
    //     // AVarDecl declNode = (AVarDecl)symbolTable.get(id);
    //     // // Gets the type of the identifier
    //     // Type type = stringToType(declNode.getType().getText());

    //     return null;
    // }

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
        TypeClass typeClass = new TypeClass();
        typeClass.baseType = type;
        nodeTypes.put(node, typeClass);
    }

    public void addType(Node node, TypeClass typeClass){
        nodeTypes.put(node, typeClass);
    }

    public String toString()
    {
        StringBuilder output = new StringBuilder();

        for (Map.Entry<Node, TypeClass> entries : nodeTypes.entrySet())
        {
            output.append(entries.getKey()  + ": " + entries.getValue() + "\n");    
        }

        return output.toString();
    }
}
