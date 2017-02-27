package golite;

import golite.lexer.*;
import golite.node.*;

import java.io.*;

public class GoliteLexer extends Lexer
{
    private Token lastToken = null;
    private static int lineNumber = 1;

    public GoliteLexer(PushbackReader in)
    {
        super(in);
    }

    protected void filter() 
    {
        if (needsSemicolon())
            token = new TSemicolon();
        // Update the token last seen if it's not a blank
        if (!(token instanceof TBlank || token instanceof TComment
               || token instanceof TBlockComment))
            lastToken = token;
    }

    private boolean needsSemicolon()
    {
        if (token instanceof TEol)
        {
            //System.out.println(lineNumber++ + ": " + lastToken);
        }
        return token instanceof TEol &&
               (lastToken instanceof TId ||
                lastToken instanceof TInt ||
                lastToken instanceof TFloat64Literal ||
                lastToken instanceof TRuneLiteral ||
                //lastToken instanceof TBoolLiteral ||
                lastToken instanceof TRawStringLit ||
                lastToken instanceof TInterpretedStringLiteral ||
                lastToken instanceof TType ||
                lastToken instanceof TRParen ||
                lastToken instanceof TRBrace ||
                // lastToken instanceof TLBrace ||
                lastToken instanceof TRBrack ||
                lastToken instanceof TReturn ||
                lastToken instanceof TPlusPlus ||
                lastToken instanceof TMinusMinus ||
                lastToken instanceof TBreak ||
                lastToken instanceof TContinue); //||
                //(token instanceof TElse && lastToken instanceof TRBrace);
    }
}
