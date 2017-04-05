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
    public HashMap<Node, TypeClass> nodeTypes;
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
        if (isInteger == Type.INT || isInteger == Type.RUNE) {
            return true;
        }
        return false;
    }

    public boolean isAliasedCorrectly(TypeClass left, TypeClass right) {
        //System.out.println("Checking aliases: " + left + " R:" + right);
        List<TypeAlias> leftAliases = left.typeAliases;
        List<TypeAlias> rightAliases = right.typeAliases;
        int leftSize = leftAliases.size(), rightSize = rightAliases.size();

        if (leftSize > 0 && rightSize > 0) {
            if (leftAliases.get(leftSize - 1).node != rightAliases.get(rightSize - 1).node) {
                ErrorManager.printError("Aliases aren't compatible with each other: " + left + ", " + right);
                return false;
            }
        } else if (leftSize > 0 || rightSize > 0) {
            ErrorManager.printError("Aliases aren't compatible with each other: " + left + ", " + right);
            return false;
        }
        return true;
    }

    public boolean isComparable(TypeClass left, TypeClass right, BinaryOps op) {

        if (left.totalArrayDimension.size() > 0 || right.totalArrayDimension.size() > 0) {
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
                if (left.baseType == Type.STRUCT && right.baseType == Type.STRUCT) {
                    if (left.innerFields == right.innerFields) return true;
                    // return isSameStruct(left.innerFields, right.innerFields);
                } else {
                    return (isComparable(left.baseType) && isComparable(right.baseType) 
                        && left.baseType == right.baseType);
                }

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

    public boolean isSameIdType(PIdType left, PIdType right) {
        if (left.getClass().equals(right.getClass())) {
            if (left instanceof AIdIdType) {
                AIdIdType lId = (AIdIdType) left, rId = (AIdIdType) right;
                return lId.getId().getText().equals(rId.getId().getText());
            } else {
                ATypeIdType lId = (ATypeIdType) left, rId = (ATypeIdType) right;
                return lId.getType().getText().equals(rId.getType().getText());
            }
        }
        return false;
    }

    public void ASingleInnerFields(ASingleInnerFields node) {
        for (int i = 0; i < node.getIdType().size(); i++)
        {
            Symbol s = symbolTable.get(node.getIdType().get(i));
            addType(node.getIdType().get(i), s.typeClass);
        }
    }

    public boolean isSameInnerField(PInnerFields l, PInnerFields r) {
        ASingleInnerFields left = (ASingleInnerFields) l, right = (ASingleInnerFields) r;        
        List<PIdType> leftIds = left.getIdType(), rightIds = right.getIdType();
        PVarType leftType = left.getVarType(), rightType = right.getVarType();

        if (leftIds.size() != rightIds.size()) {
            return false;
        }

        for (int i = 0; i < leftIds.size(); i++) {
            if (!isSameIdType(leftIds.get(i), rightIds.get(i))) {
                return false;
            }
        }

        return isSameVarType(leftType, rightType);
    }

    public boolean isSameVarType(PVarType left, PVarType right) {
        if (left.getClass().equals(right.getClass())) {
            if (left instanceof ATypeVarType) {
                ATypeVarType lType = (ATypeVarType) left, rType = (ATypeVarType) right;
                return lType.getType().getText().equals(rType.getType().getText());
            } else if (left instanceof ASliceVarType) {
                return isSameVarType(((ASliceVarType) left).getVarType(),
                    ((ASliceVarType) right).getVarType());
            } else if (left instanceof AArrayVarType) {
                AArrayVarType lArray = (AArrayVarType) left, rArray = (AArrayVarType) right;
                if (lArray.getInt().getText().equals(rArray.getInt().getText())) {
                    return isSameVarType(lArray.getVarType(), rArray.getVarType());
                }
                return false;
            } else if (left instanceof AStructVarType) {
                AStructVarType lStruct = (AStructVarType) left, rStruct = (AStructVarType) right;
                return isSameStruct(lStruct.getInnerFields(), rStruct.getInnerFields());
            } else {
                AIdVarType lId = (AIdVarType) left, rId = (AIdVarType) right;
                return lId.getId().getText().equals(rId.getId().getText());
            }
        }

        return false;
    }


    public boolean isSameStruct(List<PInnerFields> left, List<PInnerFields> right) {
        if (left == null && right == null) {
            return true;
        }

        if (left.size() != right.size()) {
            return false;
        }

        for (int i = 0; i < left.size(); i++) {
            if (!isSameInnerField(left.get(i), right.get(i))) {
                return false;
            }
        }

        return true;
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
                    leftType + ", " + rightType + ". (" + node.getL() + ", " 
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

    public void outAFunctionCallExp(AFunctionCallExp node) {
        List<PExp> inputs = node.getArgs();
        int numberOfArgs = inputs.size();
        Symbol lhsSymbol = symbolTable.get(node.getName());
        TypeClass lhs = nodeTypes.get(node.getName());
        if (lhsSymbol == null) {
            ErrorManager.printError("Function doesn't exist");
            return;
        }

        //if function call
        if (lhsSymbol.kind == Symbol.SymbolKind.FUNCTION) {
            List<TypeClass> params = lhs.functionSignature.parameterTypes;
            if (numberOfArgs == params.size()) {
                for (int i = 0; i < params.size(); i++) {
                    if (!isAliasedCorrectly(params.get(i), nodeTypes.get(inputs.get(i))) 
                        || params.get(i).baseType != nodeTypes.get(inputs.get(i)).baseType) {
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
        if (lhsSymbol.kind == Symbol.SymbolKind.TYPE && isBaseType(lhs.baseType)) {
            if (inputs.size() != 1) {
                ErrorManager.printError("Not a correct function call: " + lhsSymbol.name);
                return;
            }
            if (isBaseType(getType(inputs.get(0)).baseType)) {
                nodeTypes.put(node, new TypeClass(lhs));
                return;
            }
        }

        ErrorManager.printError("Not a correct function call: " + lhsSymbol.name);
        return;
    }

    // public TypeClass getArrayType(TypeClass array) {
    //     if (array.)
    // }
    //iterate though until slice size decreases or until we're at the end 
    // public TypeClass getSliceType(TypeClass t) {
    //     if (t.typeAliases != null && t.typeAliases.size() > 0) {
    //         for (int i = t.typeAliases.size() - 1; i >= 0; i--) {
    //             if (t.typeAliases.get(i).arrayDimensions.size() != t.typeAliases.size()) {
    //                 if (i != (t.typeAliases.size() - 1)) {
    //                     Symbol s = symbolTable.get(t.typeAliases.get(i + 1).node);
    //                     return s.typeClass;
    //                 } else {
    //                     return t;
    //                 }
    //             }     
    //         }
    //     }

    //     return t;
        // if (t.totalArrayDimension.size() == 0) {
        //     if (t.typeAliases != null && t.typeAliases.size() > 0) {
        //         return getSliceType(symbolTable.get(t.typeAliases.get(t.typeAliases.size() - 1).node).typeClass);
        //     } else {
        //         return null;
        //     }
        // } else {
        //     if (!(t.totalArrayDimension.getLast().isArray)) {
        //         return t;
        //     }
        //     return null;
        // }
    // }

    //   public TypeClass getArrayOrSliceType(TypeClass t) {
    //     if (t.totalArrayDimension.size() == 0) {
    //         if (t.typeAliases != null && t.typeAliases.size() > 0) {
    //             return getSliceType(symbolTable.get(t.typeAliases.get(t.typeAliases.size() - 1).node).typeClass);
    //         } else {
    //             return null;
    //         }
    //     } else {
    //         return t;
    //     }
    // }
    
    public TypeClass resolveType(TypeClass t) {
        int totalDim = t.totalArrayDimension.size();
        Node prevNode = null;
        List<TypeAlias> typeAliases = t.typeAliases;
        if (typeAliases != null && typeAliases.size() > 0) {
            for (int i = typeAliases.size() - 1; i >= 0; i--) {
                int curDimen = typeAliases.get(i).arrayDimensions.size();
                if (curDimen != totalDim) {
                    if (prevNode == null) {
                        return t;
                    }
                    Symbol s = symbolTable.get(prevNode);
                    System.out.println("prev typeclass is " + s.typeClass);
                    return s.typeClass;
                }
                prevNode = typeAliases.get(i).node;
            }
            Symbol s = symbolTable.get(typeAliases.get(0).node);
            System.out.println("cur typeclass is " + s.typeClass);
            return s.typeClass;
        } else {
            return t;
        }
    }

    //check if structs Are same when appending
    public void outAAppendedExprExp(AAppendedExprExp node) {
        TypeClass leftType = getType(node.getL());
        TypeClass rightType = getType(node.getR());
        // System.out.println(leftType.baseType);
        if (leftType == null) {
            ErrorManager.printError("Must append to slices only");
        }

        if (leftType.totalArrayDimension.size() > 0) {
            if (leftType.totalArrayDimension.getLast().isArray) {
                ErrorManager.printError("Must append to slices only");
            }

            TypeClass nleftType = resolveType(leftType);
            System.out.println("left type is " + nleftType);
            if (isAliasedCorrectly(nleftType, rightType)) {
                if (nleftType.baseType == Type.STRUCT && rightType.baseType == Type.STRUCT) {
                    if (!isSameStruct(nleftType.innerFields, rightType.innerFields)) {
                        ErrorManager.printError("not same struct");
                    }
                }
                if (rightType.baseType == nleftType.baseType) {
                    nodeTypes.put(node, leftType);
                    return;
                }
            } else if (rightType.baseType == nleftType.baseType) {
                    nodeTypes.put(node, leftType);
                    return;
            }
        }

        ErrorManager.printError("Append of incompatible types: " +
                    leftType + ", " + rightType + ". (" + node.getL() + " / " +
                    node.getR() + ")");
    }

    public void outAVarWithOnlyExpVarDecl(AVarWithOnlyExpVarDecl node) {
        Node left = node.getIdType();
        Node right = node.getExp();
        if (isBlankId(node.getIdType())) {
            return;
        }
        TypeClass temp = new TypeClass(getType(right));
        if (temp != null && temp.baseType == Type.VOID) {
            ErrorManager.printError("Assigning to a void value");
        }
        
        if (symbolTable.containsKey(right)) {
            Symbol rhsSymbol = symbolTable.get(right);
            if (rhsSymbol.kind != Symbol.SymbolKind.LOCAL) {
                ErrorManager.printError("Please assign to variables or base types");
            }
        }
        Symbol lhsSymbol = symbolTable.get(left);
        lhsSymbol.setType(temp);
        nodeTypes.put(left, lhsSymbol.typeClass);
    }

    public String getIdFromIdType(PIdType node) {
        if (node instanceof AIdIdType) {
            return ((AIdIdType) node).getId().getText();
        } else {
            return ((ATypeIdType) node).getType().getText();
        }
    }

    public boolean isSameDimension(TypeClass l, TypeClass r) {
        if (l.totalArrayDimension.size() != r.totalArrayDimension.size()) {
            return false;
        }

        for (int i = 0; i < l.totalArrayDimension.size(); i++) {
            Dimension lD = l.totalArrayDimension.get(i);
            Dimension rD = r.totalArrayDimension.get(i);
            if (lD.isArray != rD.isArray) return false;
            if (lD.size != rD.size) return false;
        }
        return true;
    }
    public void outAVarWithTypeAndExpVarDecl(AVarWithTypeAndExpVarDecl node) {
        // Skip blank ids
        if (isBlankId(node.getIdType()))
            return;
        TypeClass idType = symbolTable.get(node).typeClass;
        TypeClass expType = getType(node.getExp());
        System.out.println("" + idType);
        System.out.println("" + expType);
        if (!isAliasedCorrectly(idType, expType)) {
            return;
        }

        if (idType.baseType != expType.baseType 
            || !isSameDimension(idType, expType)) {
        ErrorManager.printError("Assignment of incompatible types: " + idType + ", " + expType);
        }

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
            AVarWithOnlyExpVarDecl varDecl = (AVarWithOnlyExpVarDecl)current;
            leftArgs.add(varDecl.getIdType());
            rightArgs.addFirst(varDecl.getExp());
            for (int i = 0; i < leftArgs.size(); i++) {
                // Skip blank ids
                if (isBlankId(leftArgs.get(i)))
                    continue;
                TypeClass temp = new TypeClass(getType(rightArgs.get(i)));
                Symbol lhsSymbol = symbolTable.get(leftArgs.get(i));
                lhsSymbol.setType(temp);
                nodeTypes.put(leftArgs.get(i), lhsSymbol.typeClass);
                System.out.println("Type of " + leftArgs.get(i) + " = " + lhsSymbol.typeClass);
            }
        } else if (current instanceof AVarWithTypeAndExpVarDecl) {
            for (int i = 0; i < leftArgs.size(); i++) {
                // Skip blank ids
                if (isBlankId(leftArgs.get(i)))
                    continue;
                TypeClass left = symbolTable.get(current).typeClass;
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

    public void inAExpStmt(AExpStmt node) {
        Node exp = node.getExp();
        if (exp instanceof AFunctionCallExp) {
            Symbol lhsSymbol = symbolTable.get(((AFunctionCallExp) exp).getName());
            if (lhsSymbol == null) {
                ErrorManager.printError("Function doesn't exist");
                return;
            }
            //if function call
            if (lhsSymbol.kind != Symbol.SymbolKind.FUNCTION) {
                ErrorManager.printError("Casts are not stand alone statements");
            }
        }
    }

    public void outADecrementStmt(ADecrementStmt node){
        Node exp = node.getExp();
        TypeClass expType = nodeTypes.get(exp);
        if ((expType.baseType != Type.INT && expType.baseType != Type.FLOAT64 
            && expType.baseType != Type.RUNE) || expType.totalArrayDimension.size() != 0) {
             ErrorManager.printError("-- operator requires a numeric argument");
        }
    }

    public void outAIncrementStmt(AIncrementStmt node){
        Node exp = node.getExp();
        TypeClass expType = nodeTypes.get(exp);
        if ((expType.baseType != Type.INT && expType.baseType != Type.FLOAT64
            && expType.baseType != Type.RUNE) || expType.totalArrayDimension.size() != 0) {
             ErrorManager.printError("++ operator requires a numeric argument");
        }
    }

    // Returns true if the node is a blank identifier
    private boolean isBlankId(PIdType node)
    {
        return getIdName(node).trim().equals("_");
    }

    public void outAAssignListStmt(AAssignListStmt node) {
        System.out.println("outAAssignListStmt: " + node);
        List<PExp> leftArgs = node.getL();
        List<PExp> rightArgs = node.getR();
        PExp operator = node.getOp();

        if (operator instanceof AEqualsExp) {
            if (leftArgs.size() == rightArgs.size()) {
                for (int i = 0; i < leftArgs.size(); i++) {
                    TypeClass left = getType(leftArgs.get(i));
                    TypeClass right = getType(rightArgs.get(i));
                    if (!isAliasedCorrectly(left, right)) {
                            return;
                    }
                    if (right.baseType == Type.STRUCT && left.baseType == Type.STRUCT) {
                        if (!isSameStruct(right.innerFields, left.innerFields)) {
                            ErrorManager.printError("incompatible structs");
                        }
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
                boolean newDecl = false;
                Set<String> newIds = new HashSet<String>();
                for (int i = 0; i < leftArgs.size(); i++) {
                    TypeClass left = getType(leftArgs.get(i));
                    TypeClass right = getType(rightArgs.get(i));
                    if (!left.isNull() && !isAliasedCorrectly(left, right)) {
                        return;
                    }

                    Symbol l = symbolTable.get(leftArgs.get(i));
                    if (l != null) {
                        if (l.kind != Symbol.SymbolKind.LOCAL) {
                            ErrorManager.printError("Assignment of non locals not allowed");
                        }
                    }
                    if (right != null) {
                        if (right.baseType == Type.VOID) {
                            ErrorManager.printError("assigning to a void value");
                        }
                    }

                    if (left.isNull()) {
                        if (!newIds.add(getIdName(((AIdExp)leftArgs.get(i)).getIdType()))) {
                            ErrorManager.printError("Cannot redeclare same id twice");
                        }
                        if (isBlankId(((AIdExp)leftArgs.get(i)).getIdType())) {
                            continue;
                        }
                        newDecl = true;
                        Symbol s = symbolTable.get(rightArgs.get(i));
                            if (s != null) {
                                if (s.kind != Symbol.SymbolKind.LOCAL) {
                                ErrorManager.printError("Assignment of only local variable or primitive allowed");
                            }
                        }

                        TypeClass copy = new TypeClass(right);
                        Symbol lhsSymbol = symbolTable.get(leftArgs.get(i));
                        //System.out.println("Setting dynamic type of " + lhsSymbol + " to: " + copy);
                        lhsSymbol.setType(copy);
                        nodeTypes.put(leftArgs.get(i), lhsSymbol.typeClass);
                    } else if (right.baseType != left.baseType) {
                        ErrorManager.printError("Assignment of incompatible types: " + left + ", " + right);
                        return;
                    }
                }
                if (!newDecl) {
                    ErrorManager.printError("Short declarations must contain one new variable");
                }
                return;
            }
        } else {
            if (leftArgs.size() == 1 && rightArgs.size() == 1) {
                TypeClass left = getType(leftArgs.get(0));
                TypeClass right = getType(rightArgs.get(0));
                AOpEqualsExp op = (AOpEqualsExp) operator;
                
                if (leftArgs.get(0) instanceof AIdExp && isBlankId( ((AIdExp)leftArgs.get(0)).getIdType() ))
                {
                    ErrorManager.printError("Expression on left-hand-side is not an lvalue: " + leftArgs.get(0));
                }

                if (op.getOpEquals().getText().equals("+=")) {
                    if (!isComparable(left, right, BinaryOps.NUMORSTRING)) {
                        ErrorManager.printError("Operation of incompatible types: " +
                        left + ", " + right);
                        return;
                    }
                    return;
                }

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

    // These are IDs in expressions
    public void outAIdExp(AIdExp node)
    {
        // System.out.println("Traversing ID: " + node);

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


    //array dimensions List<Dimension> size of all lists of itself and its aliases
    //returning type associated with a certain dimension of the node
    //so i want to be looking at size - levels and retrieve that type
    //i..e [][][] int
    //level levels = 2 [] ->[] []
    //slice = [] int
    //[][] slice
    // levels = 2 -> slice
    //figure out either: a) which alias it belongs to or b) return base type
    // Struct selector
    // Ex: "x.y.z"
    //iterate through and find base type of current index
    public void outAArrayElementExp(AArrayElementExp node)
    {
        int levels = 0;
        Node cur = node;
        while (cur instanceof AArrayElementExp) {
            ((AArrayElementExp) cur).getIndex().apply(this);
            TypeClass i = nodeTypes.get(((AArrayElementExp) cur).getIndex());
            if (i.baseType != Type.INT) {
                ErrorManager.printError("Only integer indexes allowed");
            }
            cur = ((AArrayElementExp) cur).getArray();
            levels++;
        }
        AIdExp id = (AIdExp)cur;
        id.apply(this);
        TypeClass arrayType = getType(id);
        if (levels > arrayType.totalArrayDimension.size()) {
            ErrorManager.printError("Index doesn't exist");
        }
        System.out.println("Array element type is " + arrayType);
        //iterate through aliases
        List<TypeAlias> typeAliases = arrayType.typeAliases;
        //return basetype in this case
        if (typeAliases.size() == 0) {
            TypeClass temp = new TypeClass(arrayType);
            for (int i = 0; i < levels; i++) {
                temp.decrementDimension();
            }
            addType(node, temp);
            return;
        } else {
            //aliased and need to traverse
            Node prevNode = id;
            int prevDim = arrayType.totalArrayDimension.size();
            int totalDim = arrayType.totalArrayDimension.size();
            for (int i = typeAliases.size() - 1; i >= 0; i--) {
                int curDim = typeAliases.get(i).arrayDimensions.size();
                if (prevDim != curDim) {
                    levels = levels - (totalDim - curDim);
                    if (levels <= 0) {
                        TypeClass indexType = symbolTable.get(prevNode).typeClass;
                        System.out.println("Type to insert is" + indexType);
                        addType(node, indexType);
                        return;
                    }
                    prevNode = typeAliases.get(i).node;
                }
                prevDim = curDim;
            }
            TypeClass indexType = symbolTable.get(typeAliases.get(0).node).typeClass;
            TypeClass copy = new TypeClass(indexType);
            copy.totalArrayDimension = new LinkedList<Dimension>();
            addType(node, copy);
            return; 
            //last alias is the one
            
        }
    }

    // Struct selector
    // Ex: "x.y.z"
    public void inAFieldExp(AFieldExp node)
    {
        declareNodeType(node);
    }

    // These are IDs inside declarations
    // Ex: function parameters
    public void outAIdIdType(AIdIdType node)
    {
        declareNodeType(node);
    }

    private void declareNodeType(Node node)
    {
        if (!nodeTypes.containsKey(node))
        {
            Symbol symbol = symbolTable.get(node);
            if (symbol == null) { return; }

            TypeClass type = symbol.typeClass;
            if (type.baseType == Type.INVALID)
            {
                ErrorManager.printError("Identifier \"" + node + "\"has"
                        + " invalid type");
                return;
            }

            addType(node, new TypeClass(type));
        }
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

        if(exprType.baseType != Type.RUNE && exprType.baseType != Type.FLOAT64 && exprType.baseType != Type.INT 
            && exprType.baseType != Type.BOOL) {
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

        if(op == Operator.EXCLAMATION_MARK) {
            if (exprType.baseType == Type.BOOL) {
                return exprType;
            }
            else {
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

    public void outAUnaryMinusExp(AUnaryMinusExp node){
        TypeClass typeClass = unaryOperationType(Operator.MINUS, getType(node.getExp()));
        if(typeClass != null){
            if(typeClass.baseType == Type.INVALID){
                ErrorManager.printError("Unary operator - used on an expression of type: " + getType(node.getExp()) + "(" + node.getExp().toString().trim() + ")." + " A - unary operation can only be used with an int, float64, or rune literal.");
                return;
            }
        }
        addType(node, typeClass.baseType);
    }

    public void outAUnaryXorExp(AUnaryXorExp node){
        TypeClass typeClass = unaryOperationType(Operator.CARET, getType(node.getExp()));
        if(typeClass != null){
            if(typeClass.baseType == Type.INVALID){
                ErrorManager.printError("Unary operator ^ used on an expression of type: " + getType(node.getExp()) + "(" + node.getExp().toString().trim() + ")." + " A ^ unary operation can only be used with an int or a rune literal.");
                return;
            }
        }
        addType(node, typeClass.baseType);
    }

    public void outAUnaryExclamationExp(AUnaryExclamationExp node){
        TypeClass typeClass = unaryOperationType(Operator.EXCLAMATION_MARK, getType(node.getExp()));
        if(typeClass != null){
            if(typeClass.baseType == Type.INVALID){
                ErrorManager.printError("Unary operator ! used on an expression of type: " + getType(node.getExp()) + "(" + node.getExp().toString().trim() + ")." + " A ! unary operation can only be used with a bool literal.");
                return;
            }
        }
        addType(node, typeClass.baseType);
    }    

    // Base Literals Start
    // ----------------------------------------

    public void outAIntExp(AIntExp node)
    {
        addType(node, Type.INT);
    }

    public void outAOctExp(AOctExp node)
    {
        addType(node, Type.INT);
    }

    public void outAHexExp(AHexExp node)
    {
        addType(node, Type.INT);
    }

    public void outAFloat64LiteralExp(AFloat64LiteralExp node){
        //System.out.println("float: " + node);
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
    public void outAPrintExp(APrintExp node){
        LinkedList<PExp> expList = node.getExp();
        if(expList != null){
            int size = expList.size();
            int i = 0;
            while (i < size) {
                Symbol s = symbolTable.get(expList.get(i));
                if (s != null) {
                    if (s.kind == Symbol.SymbolKind.TYPE) {
                        ErrorManager.printError("can't print types");
                    }
                }
                TypeClass typeClass = getType(expList.get(i));
                if (typeClass != null) {
                    if ((typeClass.baseType == Type.INT || typeClass.baseType == Type.FLOAT64 ||
                    typeClass.baseType == Type.BOOL || typeClass.baseType == Type.STRING ||
                    typeClass.baseType == Type.RUNE) && typeClass.totalArrayDimension.size() == 0){
                        i++;
                        continue;
                    } else {
                        ErrorManager.printError("Argument to print at index " + i + " is of type: " + typeClass +". Type must be int, float64, bool, string, or rune.");
                        return;
                    }
                } else {
                    ErrorManager.printError("Argument to print at index " + i + " is of type: " + typeClass +". Type must be int, float64, bool, string, or rune.");
                    return;
                }
            }
        }
            // Iterate over all the list of the expressions
            // if none of them are invalid, then the expression is correct
    }

    // Println stmt
    public void outAPrintlnExp(APrintlnExp node){
        LinkedList<PExp> expList = node.getExp();
        if(expList != null){
            int size = expList.size();
            int i = 0;
            while(i < size) {
                Symbol s = symbolTable.get(expList.get(i));
                if (s != null) {
                    if (s.kind == Symbol.SymbolKind.TYPE) {
                        ErrorManager.printError("can't print types");
                    }
                }
                TypeClass typeClass = getType(expList.get(i));
                if(typeClass != null){
                    if ((typeClass.baseType == Type.INT || typeClass.baseType == Type.FLOAT64 ||
                    typeClass.baseType == Type.BOOL || typeClass.baseType == Type.STRING ||
                    typeClass.baseType == Type.RUNE) && typeClass.totalArrayDimension.size() == 0) {
                        i++;
                        continue;
                    } else {
                        ErrorManager.printError("Argument to print at index " + i + " is of type: " + typeClass +". Type must be int, float64, bool, string, or rune.");
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
        if (!containsAReturn(((ABlockStmt)node.getBlock()).getStmt())) {
            ErrorManager.printError("Program doesn't contain a return statement");
        }
    }

    public boolean containsAReturn(List<PStmt> nodes){
        for (int i = 0; i < nodes.size(); i++){
            PStmt node = nodes.get(i);
            if(node instanceof AReturnStmt){
                return true;
            }

            if (node instanceof AForStmt) {
                AForStmt forStmt = (AForStmt)node;
                if(containsAReturn(((ABlockStmt)forStmt.getBlock()).getStmt())) {
                    PExp exp = forStmt.getCondition();
                    if(exp instanceof AForCondExp){
                         PExp temp = ((AForCondExp) exp).getSecond();
                            if(temp != null){
                                if(getType(temp) != null) {
                                    continue;
                                }
                            }
                    } else if (exp != null){
                        if (getType(exp) != null) {
                            continue;
                        }
                    }
                    return true;
                }
            }
                
            if (node instanceof AIfStmt){
                Node current = node;
                while (current instanceof AIfStmt) {
                    AIfStmt temp = (AIfStmt) current;
                    if(containsAReturn(((ABlockStmt) temp.getBlock()).getStmt())) {
                        PStmt next = temp.getEnd();
                        if (next instanceof AElseIfStmt) {
                        current = (AIfStmt) ((AElseIfStmt) next).getStmt();
                        continue;
                        } else {
                            current = next;
                        }
                    } else {
                        return false;
                    }
                }
                if (current instanceof AElseStmt) {
                        AElseStmt temp = (AElseStmt) current;
                        return containsAReturn(((ABlockStmt) temp.getStmt()).getStmt());
                } 
                // else {
                //         return false;
                // }
            }

            if (node instanceof AElseStmt) {
                AElseStmt current = (AElseStmt) node;
                ABlockStmt block = (ABlockStmt) current.getStmt();
                return containsAReturn(block.getStmt());
            }

            if (node instanceof ASwitchStmt) {
                LinkedList<PStmt> switches = ((ASwitchStmt) node).getCaseStmts();
                boolean containsDefault = false;
                for (Node n : switches) {
                    ACaseStmt cur = (ACaseStmt) n;
                    if (!containsAReturn(cur.getStmtList())) {
                        return false;
                    }
                    if (cur.getCaseExp() instanceof ADefaultExp) {
                        containsDefault = true;
                    }
                }
                if (containsDefault) {
                    return true;
                } else {
                    continue;
                }
            }
        }
        return false;
    }

    public void outAReturnStmt(AReturnStmt node){
        TypeClass return_type = getType(node.getExp());
        if (return_type != null){
            // meaning function signature and actual return type do not match
            if (global_return_type == null) {
                    ErrorManager.printError("Function returns a type: " +  return_type + " that does not match the function signature return type: " + global_return_type  + ".");
                    return;
            }
             // else if (return_type.baseType == Type.INT 
            //     && node.getExp() instanceof AIntExp && ((AIntExp) node.getExp()).getInt().getText().equals("0")) {
            //     ErrorManager.printError("Cannot return 0 with return type int");
            // } 
            else {
                if (isAliasedCorrectly(return_type, global_return_type)) {
                    if (return_type.baseType != global_return_type.baseType) {
                        ErrorManager.printError("Function returns a type: " +  return_type + " that does not match the function signature return type: " + global_return_type  + ".");
                        return;
                    }
                } 
            }
        }
        // return_type == null
        else {
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
            while(i < size && size >= 2){
                PExp singleExp = expList.get(i);
                if(isAliasedCorrectly(getType(singleExp), temp_case_exp_type)) {
                    if(temp_case_exp_type != null 
                        && getType(singleExp).baseType != temp_case_exp_type.baseType) {
                        ErrorManager.printError("The expressions in the case statement are not all of the same type");
                        return;
                    }
                }
                i++;
            }
            if (global_case_exp_type != null) {
                isAliasedCorrectly(global_case_exp_type, temp_case_exp_type);
            } else {
                global_case_exp_type = temp_case_exp_type;
            }
        }
    }

    public void outASwitchStmt(ASwitchStmt node){
        TypeClass typeClass = getType(node.getExp());
        // empty switch statement, assume it is of boolean type
        if (typeClass == null && global_case_exp_type != null){
            if(global_case_exp_type.baseType != Type.BOOL || global_case_exp_type.typeAliases.size() != 0){
                ErrorManager.printError("Expecting BOOL in case statements, provided with: " + global_case_exp_type);
                return;
            }
        }
        if(typeClass != null && global_case_exp_type != null){
            if(isAliasedCorrectly(typeClass, global_case_exp_type)) {
                if (typeClass.baseType != global_case_exp_type.baseType) {
                    ErrorManager.printError("The expression type " + typeClass.baseType.toString().trim() + " does not match the case expression type " + global_case_exp_type.baseType.toString());
                    return;
                } 
            }
        }

        if (typeClass != null) {
            if (typeClass.baseType == Type.VOID) {
                ErrorManager.printError("No void switch expressions allowed");
            }
        }



        global_case_exp_type = null;
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
