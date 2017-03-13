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
    public TypeClass global_return_type, global_case_exp_type;

    public TypeChecker(HashMap<Node, Symbol> symbolTable)
    {
        this.symbolTable = symbolTable;

        nodeTypes = new HashMap<Node, TypeClass>();
    }

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

    public boolean isAliasedCorrectly(TypeClass left, TypeClass right) {
        List<TypeAlias> leftAliases = left.typeAliases, rightAliases = right.typeAliases;
        int leftSize = leftAliases.size(), rightSize = rightAliases.size();

        if (leftSize > 0 && rightSize > 0) {
            if (leftAliases.get(leftSize - 1).node != rightAliases.get(rightSize - 1).node) {
                ErrorManager.printError("Aliases aren't compatible with each other");
                return false;
            }
        } else if (leftSize > 0 || rightSize > 0) {

            ErrorManager.printError("Aliases aren't compatible with base types" + leftSize + " " + rightSize);
            return false;
        }
        return true;
    }

    public boolean isComparable(TypeClass left, TypeClass right, BinaryOps op) {

        if (left.totalArrayDimension > 0 || right.totalArrayDimension > 0) {
            ErrorManager.printError("Unable to perform binary operations on array type");
            return false;
        }

        if (!isAliasedCorrectly(left, right)) {
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
            addType(node, Type.BOOL);
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
            addType(node, Type.BOOL);
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
            addType(node, Type.BOOL);
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
            addType(node, Type.BOOL);
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
            addType(node, Type.BOOL);
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
            addType(node, Type.BOOL);
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

    public boolean isBaseType(Type check) {
        if (check == Type.INT || check == Type.BOOL || check == Type.RUNE || check == Type.FLOAT64) {
            return true;
        }
        return false;
    }

    public void outAFunctionCallSecondaryExp(AFunctionCallSecondaryExp node) {
        List<PExp> inputs = node.getExpList();
        int numberOfArgs = inputs.size();
        Symbol lhsSymbol = symbolTable.get(node.getExp());
        TypeClass lhs = nodeTypes.get(node.getExp());
        if (lhsSymbol == null) {
            ErrorManager.printError("Function doesn't exist");
            return;
        }

        //if function call
        if (lhsSymbol.kind == Symbol.SymbolKind.FUNCTION) {
            List<TypeClass> params = lhs.functionSignature.parameterTypes;
            if (numberOfArgs == params.size()) {
                for (int i = 0; i < params.size(); i++) {
                    if (params.get(i).baseType != nodeTypes.get(inputs.get(i)).baseType) {
                        ErrorManager.printError("Argument types don't match for function: " + lhsSymbol.name);
                        return;
                    }
                }
                nodeTypes.put(node, lhs.functionSignature.returnType);
                return;
            }
            ErrorManager.printError("Incorrect number of arguments for function: " + lhsSymbol.name);
            return;
        }
        //if casting call
        if (isBaseType(lhs.baseType)) {
            if (inputs.size() != 1) {
                ErrorManager.printError("Not a correct function call: " + lhsSymbol.name);
                return;
            }
            //cases for bool casting
            if (isBaseType(getType(inputs.get(0)).baseType)) {
                nodeTypes.put(node, new TypeClass(lhs));
                return;
            }
        }

        ErrorManager.printError("Not a correct function call: " + lhsSymbol.name);
        return;
    }

    public void outAAppendedExprExp(AAppendedExprExp node) {
        TypeClass leftType = getType(node.getL());
        TypeClass rightType = getType(node.getR());

        if (!isAliasedCorrectly(leftType, rightType)) {
            return;
        }

        if (leftType.totalArrayDimension > 0) {
            if (rightType.baseType == leftType.baseType) {
                nodeTypes.put(node, leftType);
                return;
            }
        }

        ErrorManager.printError("Append of incompatible types: " +
                    leftType + ", " + rightType + ". (" + node.getL() + " / " +
                    node.getR() + ")");
    }

    public void outAInlineListWithExpVarDecl(AInlineListWithExpVarDecl node) {
        List<PIdType> leftArgs = new ArrayList<PIdType>();
        LinkedList<PExp> rightArgs = new LinkedList<PExp>();
        PVarDecl current = node;

        while (current instanceof AInlineListWithExpVarDecl) {
            AInlineListWithExpVarDecl temp = (AInlineListWithExpVarDecl) current;
            leftArgs.add(temp.getIdType());
            rightArgs.addFirst(temp.getExp());
            current = temp.getVarDecl();
        }

        //finished recursion
        if (current instanceof AVarWithOnlyExpVarDecl) {
            for (int i = 0; i < leftArgs.size(); i++) {
                TypeClass temp = new TypeClass(getType(rightArgs.get(i)));
                Symbol lhsSymbol = symbolTable.get(leftArgs.get(i));
                lhsSymbol.setType(temp);
            }
        } else if (current instanceof AVarWithTypeAndExpVarDecl) {
            for (int i = 0; i < leftArgs.size(); i++) {
                TypeClass left = getType(leftArgs.get(i));
                TypeClass right = getType(rightArgs.get(i));
                if (!isAliasedCorrectly(left, right)) {
                    return;
                }
                if (left.baseType != right.baseType) {
                    ErrorManager.printError("Assignment of incompatible types: " + left + ", " + right);
                    return;
                }
            }  
        }
 
    }



    public void outAAssignListStmt(AAssignListStmt node) {
        List<PExp> leftArgs = node.getL();
        List<PExp> rightArgs = node.getR();
        PExp operator = node.getOp();

        if (operator instanceof AEqualsExp) {
            if (leftArgs.size() == rightArgs.size()) {
                for (int i = 0; i < leftArgs.size(); i++) {
                    TypeClass left = getType(leftArgs.get(i));
                    TypeClass right = getType(rightArgs.get(i));
                    if (left == null) {

                    }

                    if (!isAliasedCorrectly(left, right)) {
                        return;
                    }
                    if (right.baseType != left.baseType) {
                        ErrorManager.printError("Assignment of incompatible types: " + left + ", " + right);
                        return;
                    }
                }
                return;
            } else {
                 ErrorManager.printError("Assignment of incorrectlenghts: " 
                    + leftArgs.size() + ", " + rightArgs.size());
                 return;
            }
        } else if (operator instanceof AColonEqualsExp) {
            if (leftArgs.size() == rightArgs.size()) {
                for (int i = 0; i < leftArgs.size(); i++) {
                    TypeClass left = getType(leftArgs.get(i));
                    TypeClass right = getType(rightArgs.get(i));
                    if (!isAliasedCorrectly(left, right)) {
                        return;
                    }
                    if (left == null) {
                        TypeClass copy = new TypeClass(right);
                        //TODO: update symbol table
                    } else if (right.baseType != left.baseType) {
                        ErrorManager.printError("Assignment of incompatible types: " + left + ", " + right);
                        return;
                    }
                }
                return;
            }
        } else {
            if (leftArgs.size() == 1 && rightArgs.size() == 1) {
                TypeClass left = getType(leftArgs.get(0));
                TypeClass right = getType(rightArgs.get(0));
                AOpEqualsExp op = (AOpEqualsExp) operator;
                
                if (op.getOpEquals().getText().equals("-=") || op.getOpEquals().getText().equals("*=") 
                    || op.getOpEquals().getText().equals("/=")) {
                    if (!isComparable(left, right, BinaryOps.NUMERIC)) {
                        ErrorManager.printError("Operation of incompatible types: " +
                        left + ", " + right);
                        return;
                    }
                    return;
                }

                if (op.getOpEquals().getText().equals("&=") || op.getOpEquals().getText().equals("&^=") 
                    || op.getOpEquals().getText().equals("|=") || op.getOpEquals().getText().equals("<<=")
                    || op.getOpEquals().getText().equals(">>=") || op.getOpEquals().getText().equals("%=")
                    || op.getOpEquals().getText().equals("^=")) {
                    if (!isComparable(left, right, BinaryOps.INTEGER)) {
                        ErrorManager.printError("Operation of incompatible types: " +
                        left + ", " + right);
                        return;
                    }
                    return;
                }
            }
            ErrorManager.printError("Only single arguments allowed for operations");
            return;
        }
    }

    public void outAIdExp(AIdExp node)
    {
        if(getIdName(node.getIdType()).equals("true") || getIdName(node.getIdType()).equals("false")){
            addType(node, Type.BOOL);
            return;
        }

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

        addType(node, new TypeClass(type));        
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
    

    public void outASingleReturnFuncDecl(ASingleReturnFuncDecl node){
        global_return_type = null;
    }

    public void outAReturnStmt(AReturnStmt node){
        TypeClass return_type = getType(node.getExp());
        if(return_type != null){
            // meaning function signature and actual return type do not match
            if(global_return_type == null){
                if(!(return_type.toString().equals(global_return_type))) {
                    ErrorManager.printError("Function returns a type: " +  return_type + " that does not match the function signature return type: " + global_return_type  + ".");
                    return;
                }
            }

            else{
                if(!(return_type.toString().equals(global_return_type.toString()))){
                    ErrorManager.printError("Function returns a type: " +  return_type + " that does not match the function signature return type: " + global_return_type  + ".");
                    return;
                } 
            }
        }
        // return_type == null
        else{
            // their values being null
            if(global_return_type != return_type){
                ErrorManager.printError("Function does not return a type. "+ " Expecting: " + global_return_type  + ".");
                return;
            }
        }
    }

    public void outACaseExp(ACaseExp node){
        LinkedList<PExp> expList = node.getExpList();
        if(expList != null){
            int size = expList.size();
            int i = 0;
            TypeClass temp_case_exp_type = getType(expList.get(0));
            while(i<size && size>=2){
                PExp singleExp = expList.get(i);
                if(getType(singleExp).baseType.toString() != temp_case_exp_type.baseType.toString()){
                    if(temp_case_exp_type != null) {
                        ErrorManager.printError("The expressions in the case statement are not all of the same type");
                        return;
                    }
                }
                i++;
            }
            global_case_exp_type = temp_case_exp_type;
        }
    }

    public void outASwitchStmt(ASwitchStmt node){
        TypeClass typeClass = getType(node.getExp());
        // empty switch statement, assume it is of boolean type
        if (typeClass == null){
            if(global_case_exp_type.baseType.toString() != "BOOL"){
                ErrorManager.printError("Expecting BOOL in case statements, provided with: " + global_case_exp_type.baseType.toString());
                return;
            }
        }
        if(typeClass != null && global_case_exp_type != null){
            if(!(typeClass.baseType.toString().equals(global_case_exp_type.baseType.toString()))) {
                ErrorManager.printError("The expression type " + typeClass.baseType.toString().trim() + " does not match the case expression type " + global_case_exp_type.baseType.toString());
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
