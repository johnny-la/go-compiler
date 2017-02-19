/* This file was generated by SableCC (http://www.sablecc.org/). */

package golite.node;

import java.util.*;
import golite.analysis.*;

@SuppressWarnings("nls")
public final class AProgram extends PProgram
{
    private final LinkedList<PDecl> _decl_ = new LinkedList<PDecl>();
    private final LinkedList<PStmt> _stmt_ = new LinkedList<PStmt>();

    public AProgram()
    {
        // Constructor
    }

    public AProgram(
        @SuppressWarnings("hiding") List<?> _decl_,
        @SuppressWarnings("hiding") List<?> _stmt_)
    {
        // Constructor
        setDecl(_decl_);

        setStmt(_stmt_);

    }

    @Override
    public Object clone()
    {
        return new AProgram(
            cloneList(this._decl_),
            cloneList(this._stmt_));
    }

    @Override
    public void apply(Switch sw)
    {
        ((Analysis) sw).caseAProgram(this);
    }

    public LinkedList<PDecl> getDecl()
    {
        return this._decl_;
    }

    public void setDecl(List<?> list)
    {
        for(PDecl e : this._decl_)
        {
            e.parent(null);
        }
        this._decl_.clear();

        for(Object obj_e : list)
        {
            PDecl e = (PDecl) obj_e;
            if(e.parent() != null)
            {
                e.parent().removeChild(e);
            }

            e.parent(this);
            this._decl_.add(e);
        }
    }

    public LinkedList<PStmt> getStmt()
    {
        return this._stmt_;
    }

    public void setStmt(List<?> list)
    {
        for(PStmt e : this._stmt_)
        {
            e.parent(null);
        }
        this._stmt_.clear();

        for(Object obj_e : list)
        {
            PStmt e = (PStmt) obj_e;
            if(e.parent() != null)
            {
                e.parent().removeChild(e);
            }

            e.parent(this);
            this._stmt_.add(e);
        }
    }

    @Override
    public String toString()
    {
        return ""
            + toString(this._decl_)
            + toString(this._stmt_);
    }

    @Override
    void removeChild(@SuppressWarnings("unused") Node child)
    {
        // Remove child
        if(this._decl_.remove(child))
        {
            return;
        }

        if(this._stmt_.remove(child))
        {
            return;
        }

        throw new RuntimeException("Not a child.");
    }

    @Override
    void replaceChild(@SuppressWarnings("unused") Node oldChild, @SuppressWarnings("unused") Node newChild)
    {
        // Replace child
        for(ListIterator<PDecl> i = this._decl_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set((PDecl) newChild);
                    newChild.parent(this);
                    oldChild.parent(null);
                    return;
                }

                i.remove();
                oldChild.parent(null);
                return;
            }
        }

        for(ListIterator<PStmt> i = this._stmt_.listIterator(); i.hasNext();)
        {
            if(i.next() == oldChild)
            {
                if(newChild != null)
                {
                    i.set((PStmt) newChild);
                    newChild.parent(this);
                    oldChild.parent(null);
                    return;
                }

                i.remove();
                oldChild.parent(null);
                return;
            }
        }

        throw new RuntimeException("Not a child.");
    }
}
