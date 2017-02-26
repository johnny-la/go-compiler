/* This file was generated by SableCC (http://www.sablecc.org/). */

package golite.analysis;

import java.util.*;
import golite.node.*;

public class AnalysisAdapter implements Analysis
{
    private Hashtable<Node,Object> in;
    private Hashtable<Node,Object> out;

    @Override
    public Object getIn(Node node)
    {
        if(this.in == null)
        {
            return null;
        }

        return this.in.get(node);
    }

    @Override
    public void setIn(Node node, Object o)
    {
        if(this.in == null)
        {
            this.in = new Hashtable<Node,Object>(1);
        }

        if(o != null)
        {
            this.in.put(node, o);
        }
        else
        {
            this.in.remove(node);
        }
    }

    @Override
    public Object getOut(Node node)
    {
        if(this.out == null)
        {
            return null;
        }

        return this.out.get(node);
    }

    @Override
    public void setOut(Node node, Object o)
    {
        if(this.out == null)
        {
            this.out = new Hashtable<Node,Object>(1);
        }

        if(o != null)
        {
            this.out.put(node, o);
        }
        else
        {
            this.out.remove(node);
        }
    }

    @Override
    public void caseStart(Start node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAProgram(AProgram node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAFuncDeclAstDecl(AFuncDeclAstDecl node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAVarDeclAstDecl(AVarDeclAstDecl node)
    {
        defaultCase(node);
    }

    @Override
    public void caseATypeDeclAstDecl(ATypeDeclAstDecl node)
    {
        defaultCase(node);
    }

    @Override
    public void caseANoReturnFuncDecl(ANoReturnFuncDecl node)
    {
        defaultCase(node);
    }

    @Override
    public void caseASingleReturnFuncDecl(ASingleReturnFuncDecl node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAFuncDecl(AFuncDecl node)
    {
        defaultCase(node);
    }

    @Override
    public void caseASingleIdToTypeSignature(ASingleIdToTypeSignature node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAManyIdToTypeSignature(AManyIdToTypeSignature node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAMultipleTypesSignature(AMultipleTypesSignature node)
    {
        defaultCase(node);
    }

    @Override
    public void caseATypeAliasTypeDecl(ATypeAliasTypeDecl node)
    {
        defaultCase(node);
    }

    @Override
    public void caseATypeAliasBaseTypeDecl(ATypeAliasBaseTypeDecl node)
    {
        defaultCase(node);
    }

    @Override
    public void caseATypeWithManyIdsTypeDecl(ATypeWithManyIdsTypeDecl node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAStructWithIdTypeDecl(AStructWithIdTypeDecl node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAMultilineListTypeDecl(AMultilineListTypeDecl node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAVarWithTypeVarDecl(AVarWithTypeVarDecl node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAVarWithOnlyExpVarDecl(AVarWithOnlyExpVarDecl node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAVarWithTypeAndExpVarDecl(AVarWithTypeAndExpVarDecl node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAInlineListNoExpVarDecl(AInlineListNoExpVarDecl node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAInlineListWithExpVarDecl(AInlineListWithExpVarDecl node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAMultilineListVarDecl(AMultilineListVarDecl node)
    {
        defaultCase(node);
    }

    @Override
    public void caseABaseTypeVarType(ABaseTypeVarType node)
    {
        defaultCase(node);
    }

    @Override
    public void caseASliceVarType(ASliceVarType node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAArrayVarType(AArrayVarType node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAStructVarType(AStructVarType node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAReadStmt(AReadStmt node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAPrintStmt(APrintStmt node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAPrintlnStmt(APrintlnStmt node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAReturnStmt(AReturnStmt node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAIncrementStmt(AIncrementStmt node)
    {
        defaultCase(node);
    }

    @Override
    public void caseADecrementStmt(ADecrementStmt node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAAssignStmt(AAssignStmt node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAAssignOpStmt(AAssignOpStmt node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAAssignListStmt(AAssignListStmt node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAShortDeclStmt(AShortDeclStmt node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAShortDeclListStmt(AShortDeclListStmt node)
    {
        defaultCase(node);
    }

    @Override
    public void caseABlockStmt(ABlockStmt node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAWhileStmt(AWhileStmt node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAIfStmt(AIfStmt node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAElseifStmt(AElseifStmt node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAElseStmt(AElseStmt node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAForStmt(AForStmt node)
    {
        defaultCase(node);
    }

    @Override
    public void caseABreakStmt(ABreakStmt node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAContinueStmt(AContinueStmt node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAEmptyStmt(AEmptyStmt node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAPlusExp(APlusExp node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAMinusExp(AMinusExp node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAMultExp(AMultExp node)
    {
        defaultCase(node);
    }

    @Override
    public void caseADivideExp(ADivideExp node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAIdExp(AIdExp node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAFloat64LiteralExp(AFloat64LiteralExp node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAIntExp(AIntExp node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAUnaryPlusExp(AUnaryPlusExp node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAUnaryMinusExp(AUnaryMinusExp node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAUnaryXorExp(AUnaryXorExp node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAUnaryExclamationExp(AUnaryExclamationExp node)
    {
        defaultCase(node);
    }

    @Override
    public void caseARuneLiteralExp(ARuneLiteralExp node)
    {
        defaultCase(node);
    }

    @Override
    public void caseARawStringLitExp(ARawStringLitExp node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAInterpretedStringLiteralExp(AInterpretedStringLiteralExp node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAArrayIndexExp(AArrayIndexExp node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAStructSelectorExp(AStructSelectorExp node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAListExp(AListExp node)
    {
        defaultCase(node);
    }

    @Override
    public void caseAForCondExp(AForCondExp node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTIf(TIf node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTElse(TElse node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTBreak(TBreak node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTCase(TCase node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTChan(TChan node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTConst(TConst node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTContinue(TContinue node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTDefault(TDefault node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTDefer(TDefer node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTFallthrough(TFallthrough node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTFor(TFor node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTFunc(TFunc node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTGo(TGo node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTGoto(TGoto node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTVar(TVar node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTImport(TImport node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTInterface(TInterface node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTMap(TMap node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTPackage(TPackage node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTRange(TRange node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTReturn(TReturn node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTSelect(TSelect node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTStruct(TStruct node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTSwitch(TSwitch node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTOpEquals(TOpEquals node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTType(TType node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTTypeKeyword(TTypeKeyword node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTPlus(TPlus node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTMinus(TMinus node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTStar(TStar node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTSlash(TSlash node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTModulo(TModulo node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTAmpersand(TAmpersand node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTPipe(TPipe node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTCaret(TCaret node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTShiftLeft(TShiftLeft node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTShiftRight(TShiftRight node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTAmpersandCaret(TAmpersandCaret node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTLogicalAnd(TLogicalAnd node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTLogicalOr(TLogicalOr node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTLeftArrow(TLeftArrow node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTPlusPlus(TPlusPlus node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTMinusMinus(TMinusMinus node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTEqualsEquals(TEqualsEquals node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTLess(TLess node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTGreater(TGreater node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTEquals(TEquals node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTExclamation(TExclamation node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTExclamationEquals(TExclamationEquals node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTLessEquals(TLessEquals node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTGreaterEquals(TGreaterEquals node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTColonEquals(TColonEquals node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTDotDotDot(TDotDotDot node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTLParen(TLParen node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTRParen(TRParen node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTLBrack(TLBrack node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTRBrack(TRBrack node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTLBrace(TLBrace node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTRBrace(TRBrace node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTComma(TComma node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTSemicolon(TSemicolon node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTColon(TColon node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTDot(TDot node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTPrint(TPrint node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTPrintln(TPrintln node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTAppend(TAppend node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTComment(TComment node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTBlockComment(TBlockComment node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTInt(TInt node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTOct(TOct node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTHex(THex node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTFloat64Literal(TFloat64Literal node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTFloat64(TFloat64 node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTBool(TBool node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTRune(TRune node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTRuneLiteral(TRuneLiteral node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTRawStringLit(TRawStringLit node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTInterpretedStringLiteral(TInterpretedStringLiteral node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTId(TId node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTEol(TEol node)
    {
        defaultCase(node);
    }

    @Override
    public void caseTBlank(TBlank node)
    {
        defaultCase(node);
    }

    @Override
    public void caseEOF(EOF node)
    {
        defaultCase(node);
    }

    @Override
    public void caseInvalidToken(InvalidToken node)
    {
        defaultCase(node);
    }

    public void defaultCase(@SuppressWarnings("unused") Node node)
    {
        // do nothing
    }
}
