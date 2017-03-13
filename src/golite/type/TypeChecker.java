package golite.type;

import golite.node.*;
import golite.symbol.*;
import golite.analysis.*;
import golite.*;

import java.util.*;

public class TypeChecker extends DepthFirstAdapter
{

    public enum BinaryOps { BOOL, COMPARABLE, ORDERED, NUMERIC, NUMORSTRING, INTEGER }
    public enum Operator { PLUS, MINUS, MULTIPLY, DIVIDE, CARET, EXCLAMATION_MARK}

    private HashMap<Node, Symbol> symbolTable;

    private HashMap<Node, TypeClass> nodeTypes;

    public TypeClass global_return_type;

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
        TypeClass lhs = nodeTypes.get(node.getExp());
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
        if(typeClass != null){
            if(typeClass.baseType == Type.INVALID){
                ErrorManager.printError("Unary operator + used on an expression of type: " + getType(node.getExp()) + "(" + node.getExp().toString().trim() + ")." + " A + unary operation can only be used with an int, float64, or rune literal.");
                return;
            }
        }
        addType(node, typeClass.baseType);
    }

    public void outAUnaryMinusExp(AUnaryPlusExp node){
        TypeClass typeClass = unaryOperationType(Operator.MINUS, getType(node.getExp()));
        if(typeClass != null){
            if(typeClass.baseType == Type.INVALID){
                ErrorManager.printError("Unary operator - used on an expression of type: " + getType(node.getExp()) + "(" + node.getExp().toString().trim() + ")." + " A - unary operation can only be used with an int, float64, or rune literal.");
                return;
            }
        }
        addType(node, typeClass.baseType);
    }

    public void outACaretedFactorsExp(ACaretedFactorsExp node){
        TypeClass typeClass = unaryOperationType(Operator.CARET, getType(node.getExp()));
        if(typeClass != null){
            if(typeClass.baseType == Type.INVALID){
                ErrorManager.printError("Unary operator ^ used on an expression of type: " + getType(node.getExp()) + "(" + node.getExp().toString().trim() + ")." + " A ^ unary operation can only be used with an int or a rune literal.");
                return;
            }
        }
        addType(node, typeClass.baseType);
    }

    public void outAExclamatedFactorsExp(AExclamatedFactorsExp node){
        TypeClass typeClass = unaryOperationType(Operator.EXCLAMATION_MARK, getType(node.getExp()));
        if(typeClass != null){
            if(typeClass.baseType == Type.INVALID){
                ErrorManager.printError("Unary operator ! used on an expression of type: " + getType(node.getExp()) + "(" + node.getExp().toString().trim() + ")." + " A ! unary operation can only be used with a bool literal.");
                return;
            }
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

    // Print stmt
    public void outAPrintStmt(APrintStmt node){
        LinkedList<PExp> expList = node.getExp();
        if(expList != null){
            int size = expList.size();
            int i = 0;
            while(i<size){
                TypeClass typeClass = getType(expList.get(i));
                if(typeClass != null){
                    if (typeClass.baseType == Type.INT || typeClass.baseType == Type.FLOAT64 ||
                    typeClass.baseType == Type.BOOL || typeClass.baseType == Type.STRING ||
                    typeClass.baseType == Type.RUNE){
                        i++;
                        continue;
                    }
                }

                else{
                    ErrorManager.printError("Argument to print at index " + i + " is of type: " + typeClass +". Type must be int, float64, bool, string, or rune.");
                    return;
                }
            }
        }
            // Iterate over all the list of the expressions
            // if none of them are invalid, then the expression is correct
    }

    // Println stmt
    public void outAPrintlnStmt(APrintlnStmt node){
        LinkedList<PExp> expList = node.getExp();
        if(expList != null){
            int size = expList.size();
            int i = 0;
            while(i<size){
                TypeClass typeClass = getType(expList.get(i));
                if(typeClass != null){
                    if (typeClass.baseType == Type.INT || typeClass.baseType == Type.FLOAT64 ||
                    typeClass.baseType == Type.BOOL || typeClass.baseType == Type.STRING ||
                    typeClass.baseType == Type.RUNE){
                        i++;
                        continue;
                    }
                }

                else{
                    ErrorManager.printError("Argument to print at index " + i + " is of type: " + typeClass +". Type must be int, float64, bool, string, or rune.");
                    return;
                }
            }
        }
    }

    // For loops
    public void outAForStmt(AForStmt node){
        PExp exp = node.getCondition();
        if(exp instanceof AForCondExp){
            return;
        }

        if(exp != null){
            if(getType(exp)!= null){
                if(getType(exp).baseType != Type.BOOL){
                    ErrorManager.printError("Expression of for loop: " + getType(exp) + "("  +node.getCondition().toString().trim() + ")"+ ". Expected a bool.");
                    return;
                }
            }
        }
    }

    // For loops
    public void outAForCondExp(AForCondExp node){
        PExp exp = node.getSecond();
        if(exp != null){
            if(getType(exp)!= null){
                if(getType(exp).baseType != Type.BOOL){
                    ErrorManager.printError("Expression of for loop: " + getType(exp) + "(" + node.getSecond().toString().trim() + ")" +". Expected a bool.");
                    return;                
                }
            }
        }
    }

    // If condition
    public void outAIfStmt(AIfStmt node){
        TypeClass expType = getType(node.getExp());
        if(expType != null){
            if (expType.baseType != Type.BOOL){
                ErrorManager.printError("If-statement expression type: " + expType + " (" + node.getExp().toString().trim() + "). Expected a boolean.");
                return;
            }
        }
    }

    // Return statement with no expression
    public void inASingleReturnFuncDecl(ASingleReturnFuncDecl node){
        Symbol signature_return_type_symbol = symbolTable.get(node);
        if(signature_return_type_symbol != null){
            TypeClass tc = signature_return_type_symbol.typeClass;
            FunctionSignature fs = tc.functionSignature;
            tc = fs.returnType;
            global_return_type = tc;
            }
        }
    }

    public void outASingleReturnFuncDecl(ASingleReturnFuncDecl node){
        global_return_type = null;
    }

    public void outAReturnStmt(AReturnStmt node){
        TypeClass return_type = getType(node.getExp());
        if(return_type != null){
            if(return_type != global_return_type){
                ErrorManager.printError("Function returns a type: " +  return_type + " that does not match the function signature return type: " + global_return_type  + ".");
                return;
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
