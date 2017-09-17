package golite.lexer;

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
        return (token instanceof TEol || token instanceof TEndOfFile) &&
               (lastToken instanceof TId ||
                lastToken instanceof TInt ||
                lastToken instanceof TOct ||
                lastToken instanceof THex ||
                lastToken instanceof TFloat64Literal ||
                lastToken instanceof TRuneLiteral ||
                //lastToken instanceof TBoolLiteral ||
                lastToken instanceof TRawStringLit ||
                lastToken instanceof TInterpretedStringLiteral ||
                lastToken instanceof TType ||
                lastToken instanceof TRParen ||
                lastToken instanceof TRBrace ||
                lastToken instanceof TLBrace ||
                lastToken instanceof TRBrack ||
                lastToken instanceof TReturn ||
                lastToken instanceof TPlusPlus ||
                lastToken instanceof TMinusMinus ||
                lastToken instanceof TBreak ||
                lastToken instanceof TContinue);
    }
}
