/* This file was generated by SableCC (http://www.sablecc.org/). */

package golite.node;

import golite.analysis.*;

@SuppressWarnings("nls")
public final class TInterpretedStringLiteral extends Token
{
    public TInterpretedStringLiteral(String text)
    {
        setText(text);
    }

    public TInterpretedStringLiteral(String text, int line, int pos)
    {
        setText(text);
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TInterpretedStringLiteral(getText(), getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTInterpretedStringLiteral(this);
    }
}
