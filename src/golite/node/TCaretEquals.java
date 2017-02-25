/* This file was generated by SableCC (http://www.sablecc.org/). */

package golite.node;

import golite.analysis.*;

@SuppressWarnings("nls")
public final class TCaretEquals extends Token
{
    public TCaretEquals()
    {
        super.setText("^=");
    }

    public TCaretEquals(int line, int pos)
    {
        super.setText("^=");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TCaretEquals(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTCaretEquals(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TCaretEquals text.");
    }
}
