package golite.type;

import golite.node.*;
import golite.symbol.*;
import golite.analysis.*;
import golite.*;

import java.util.*;

public class TypeChecker extends DepthFirstAdapter
{
    public enum Operator { PLUS, MINUS, MULTIPLY, DIVIDE, CARET, EXCLAMATION_MARK};

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

        Type resultType = operationType(leftType.baseType, rightType.baseType, Operator.DIVIDE);

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

    public TypeClass invalidType(){
        TypeClass errorType = new TypeClass();
        errorType.baseType = Type.INVALID;
        return errorType;
    }

    // Resolve the type of an expression after a unary operator 
    public TypeClass unaryOperationType(Operator op, TypeClass exprType){
        if(exprType == null){
            return invalidType();
        }

        if(exprType.baseType != Type.RUNE && exprType.baseType != Type.FLOAT64 && exprType.baseType != Type.INT){
            return invalidType();
        }

        if(op == Operator.PLUS || op == Operator.MINUS){
            if(exprType.baseType == Type.INT || exprType.baseType == Type.FLOAT64 || exprType.baseType == Type.RUNE){
                return exprType;
            }
            else{
                return invalidType();
            }
        }

        if(op == Operator.CARET){
            if(exprType.baseType == Type.INT || exprType.baseType == Type.RUNE){

                return exprType;
            }
            else{
                return invalidType();
            }
        }

        if(op == Operator.EXCLAMATION_MARK){
            if(exprType.baseType == Type.BOOL){
                return exprType;
            }
            else{
                return invalidType();
            }
        }
        return exprType;
    }

    // Unary Expressions Start
    // ----------------------------------------

    public void outAUnaryPlusExp(AUnaryPlusExp node){
        TypeClass typeClass = unaryOperationType(Operator.PLUS, getType(node.getExp()));
        if(typeClass.baseType == Type.INVALID){
            ErrorManager.printError("A + unary operation can only be used with an int, float64, or rune literal");
            return;
        }
        addType(node, typeClass.baseType);
    }

    public void outAUnaryMinusExp(AUnaryPlusExp node){
        TypeClass typeClass = unaryOperationType(Operator.MINUS, getType(node.getExp()));
        if(typeClass.baseType == Type.INVALID){
            ErrorManager.printError("A - unary operation can only be used with an int, float64, or rune literal");
            return;
        }
        addType(node, typeClass.baseType);
    }

    public void outACaretedFactorsExp(ACaretedFactorsExp node){
        TypeClass typeClass = unaryOperationType(Operator.CARET, getType(node.getExp()));
        if(typeClass.baseType == Type.INVALID){
            ErrorManager.printError("A ^ unary operation can only be used with an int or a rune literal");
            return;
        }
        addType(node, typeClass.baseType);
    }

    public void outAExclamatedFactorsExp(AExclamatedFactorsExp node){
        TypeClass typeClass = unaryOperationType(Operator.EXCLAMATION_MARK, getType(node.getExp()));
        if(typeClass.baseType == Type.INVALID){
            ErrorManager.printError("A ! unary operation can only be used with a bool literal");
            return;
        }
        addType(node, typeClass.baseType);
    }    

    // ----------------------------------------
    // Unary Expressions End
  

    // Base Literals Start
    // ----------------------------------------

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

    // ----------------------------------------
    // Base Literals End

    // ----------------------------------------
    // Statements Start

    public void outAPrintStmt(APrintStmt node){
        LinkedList<PExp> expList = node.getExp();
        if(expList != null){
            int size = expList.size();
            int i = 0;
            while(i<size){
                System.out.println(expList.get(i));
                TypeClass typeClass = getType(expList.get(i));
                if (typeClass.baseType == Type.INT || typeClass.baseType == Type.FLOAT64 ||
                    typeClass.baseType == Type.BOOL || typeClass.baseType == Type.STRING ||
                    typeClass.baseType == Type.RUNE){
                    i++;
                    continue;
                }
                else{
                    ErrorManager.printError("Argument to print at index " + i + " is not well-type. Must be int, float64, bool, string, or rune.");
                    return;
                }
            }
        }
            // Iterate over all the list of the expressions
            // if none of them are invalid, then the expression is correct
    }

    public void outAPrintlnStmt(APrintlnStmt node){
        LinkedList<PExp> expList = node.getExp();
        if(expList != null){
            int size = expList.size();
            int i = 0;
            while(i<size){
                System.out.println(expList.get(i));
                TypeClass typeClass = getType(expList.get(i));
                if (typeClass.baseType == Type.INT || typeClass.baseType == Type.FLOAT64 ||
                    typeClass.baseType == Type.BOOL || typeClass.baseType == Type.STRING ||
                    typeClass.baseType == Type.RUNE){
                    i++;
                    continue;
                }
                else{
                    ErrorManager.printError("Argument to print at index " + i + " is not well-type. Must be int, float64, bool, string, or rune.");
                    return;
                }
            }
        }
    }

    public void outAForStmt(AForStmt node){
        LinkedList<PExp> expList = node.getCondition();
        LinkedList<PStmt> stmtBlocks = node.getBlock();

        if(expList != null){
            if(expList.size() == 1){
               if(getType(expList.get(0)) != Type.BOOL){
                    ErrorManager.printError("The expression of a for loop has to be of type bool");
                    return;
                }
            }
            if(expList.size() == 3){
                if(getType(expList.get(1)) != Type.BOOL){
                    ErrorManager.printError("The expression of a for loop has to be of type bool");
                    return;
                }
            }
        }
    }

    // Statements End
    // ----------------------------------------


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
