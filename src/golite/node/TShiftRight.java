/* This file was generated by SableCC (http://www.sablecc.org/). */

package golite.node;

import golite.analysis.*;

@SuppressWarnings("nls")
public final class TShiftRight extends Token
{
    public TShiftRight()
    {
        super.setText(">>");
    }

    public TShiftRight(int line, int pos)
    {
        super.setText(">>");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TShiftRight(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTShiftRight(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TShiftRight text.");
    }
}
