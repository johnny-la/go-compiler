/* This file was generated by SableCC (http://www.sablecc.org/). */

package golite.node;

import golite.analysis.*;

@SuppressWarnings("nls")
public final class TDone extends Token
{
    public TDone()
    {
        super.setText("done");
    }

    public TDone(int line, int pos)
    {
        super.setText("done");
        setLine(line);
        setPos(pos);
    }

    @Override
    public Object clone()
    {
      return new TDone(getLine(), getPos());
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseTDone(this);
    }

    @Override
    public void setText(@SuppressWarnings("unused") String text)
    {
        throw new RuntimeException("Cannot change TDone text.");
    }
}
