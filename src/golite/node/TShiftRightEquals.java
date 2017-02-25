/* This file was generated by SableCC (http://www.sablecc.org/). */

package golite.node;

import golite.analysis.*;

@SuppressWarnings("nls")
public final class TShiftRightEquals extends Token
{
    public TShiftRightEquals()
    {
        super.setText(">>=");
    }

    public TShiftRightEquals(int line, int pos)
    {
        super.setText(">>=");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TShiftRightEquals(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTShiftRightEquals(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TShiftRightEquals text.");
    }
}