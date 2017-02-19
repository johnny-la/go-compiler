/* This file was generated by SableCC (http://www.sablecc.org/). */

package golite.analysis;

import java.util.*;
import golite.node.*;

public class ReversedDepthFirstAdapter extends AnalysisAdapter
{
    public void inStart(Start node)
    {
        defaultIn(node);
    }

    public void outStart(Start node)
    {
        defaultOut(node);
    }

    public void defaultIn(@SuppressWarnings("unused") Node node)
    {
        // Do nothing
    }

    public void defaultOut(@SuppressWarnings("unused") Node node)
    {
        // Do nothing
    }

    @Override
    public void caseStart(Start node)
    {
        inStart(node);
        node.getEOF().apply(this);
        node.getPProgram().apply(this);
        outStart(node);
    }

    public void inAProgram(AProgram node)
    {
        defaultIn(node);
    }

    public void outAProgram(AProgram node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAProgram(AProgram node)
    {
        inAProgram(node);
        {
            List<PStmt> copy = new ArrayList<PStmt>(node.getStmt());
            Collections.reverse(copy);
            for(PStmt e : copy)
            {
                e.apply(this);
            }
        }
        {
            List<PDecl> copy = new ArrayList<PDecl>(node.getDecl());
            Collections.reverse(copy);
            for(PDecl e : copy)
            {
                e.apply(this);
            }
        }
        outAProgram(node);
    }

    public void inAVarDecl(AVarDecl node)
    {
        defaultIn(node);
    }

    public void outAVarDecl(AVarDecl node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAVarDecl(AVarDecl node)
    {
        inAVarDecl(node);
        if(node.getType() != null)
        {
            node.getType().apply(this);
        }
        if(node.getId() != null)
        {
            node.getId().apply(this);
        }
        outAVarDecl(node);
    }

    public void inAReadStmt(AReadStmt node)
    {
        defaultIn(node);
    }

    public void outAReadStmt(AReadStmt node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAReadStmt(AReadStmt node)
    {
        inAReadStmt(node);
        if(node.getId() != null)
        {
            node.getId().apply(this);
        }
        outAReadStmt(node);
    }

    public void inAPrintStmt(APrintStmt node)
    {
        defaultIn(node);
    }

    public void outAPrintStmt(APrintStmt node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAPrintStmt(APrintStmt node)
    {
        inAPrintStmt(node);
        if(node.getExp() != null)
        {
            node.getExp().apply(this);
        }
        outAPrintStmt(node);
    }

    public void inAAssignStmt(AAssignStmt node)
    {
        defaultIn(node);
    }

    public void outAAssignStmt(AAssignStmt node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAAssignStmt(AAssignStmt node)
    {
        inAAssignStmt(node);
        if(node.getExp() != null)
        {
            node.getExp().apply(this);
        }
        if(node.getId() != null)
        {
            node.getId().apply(this);
        }
        outAAssignStmt(node);
    }

    public void inAWhileStmt(AWhileStmt node)
    {
        defaultIn(node);
    }

    public void outAWhileStmt(AWhileStmt node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAWhileStmt(AWhileStmt node)
    {
        inAWhileStmt(node);
        {
            List<PStmt> copy = new ArrayList<PStmt>(node.getStmt());
            Collections.reverse(copy);
            for(PStmt e : copy)
            {
                e.apply(this);
            }
        }
        if(node.getExp() != null)
        {
            node.getExp().apply(this);
        }
        outAWhileStmt(node);
    }

    public void inAIfStmt(AIfStmt node)
    {
        defaultIn(node);
    }

    public void outAIfStmt(AIfStmt node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAIfStmt(AIfStmt node)
    {
        inAIfStmt(node);
        if(node.getEnd() != null)
        {
            node.getEnd().apply(this);
        }
        {
            List<PStmt> copy = new ArrayList<PStmt>(node.getStmt());
            Collections.reverse(copy);
            for(PStmt e : copy)
            {
                e.apply(this);
            }
        }
        if(node.getExp() != null)
        {
            node.getExp().apply(this);
        }
        outAIfStmt(node);
    }

    public void inAElseStmt(AElseStmt node)
    {
        defaultIn(node);
    }

    public void outAElseStmt(AElseStmt node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAElseStmt(AElseStmt node)
    {
        inAElseStmt(node);
        {
            List<PStmt> copy = new ArrayList<PStmt>(node.getStmt());
            Collections.reverse(copy);
            for(PStmt e : copy)
            {
                e.apply(this);
            }
        }
        outAElseStmt(node);
    }

    public void inAEmptyStmt(AEmptyStmt node)
    {
        defaultIn(node);
    }

    public void outAEmptyStmt(AEmptyStmt node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAEmptyStmt(AEmptyStmt node)
    {
        inAEmptyStmt(node);
        outAEmptyStmt(node);
    }

    public void inAPlusExp(APlusExp node)
    {
        defaultIn(node);
    }

    public void outAPlusExp(APlusExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAPlusExp(APlusExp node)
    {
        inAPlusExp(node);
        if(node.getR() != null)
        {
            node.getR().apply(this);
        }
        if(node.getL() != null)
        {
            node.getL().apply(this);
        }
        outAPlusExp(node);
    }

    public void inAMinusExp(AMinusExp node)
    {
        defaultIn(node);
    }

    public void outAMinusExp(AMinusExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAMinusExp(AMinusExp node)
    {
        inAMinusExp(node);
        if(node.getR() != null)
        {
            node.getR().apply(this);
        }
        if(node.getL() != null)
        {
            node.getL().apply(this);
        }
        outAMinusExp(node);
    }

    public void inAMultExp(AMultExp node)
    {
        defaultIn(node);
    }

    public void outAMultExp(AMultExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAMultExp(AMultExp node)
    {
        inAMultExp(node);
        if(node.getR() != null)
        {
            node.getR().apply(this);
        }
        if(node.getL() != null)
        {
            node.getL().apply(this);
        }
        outAMultExp(node);
    }

    public void inADivideExp(ADivideExp node)
    {
        defaultIn(node);
    }

    public void outADivideExp(ADivideExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseADivideExp(ADivideExp node)
    {
        inADivideExp(node);
        if(node.getR() != null)
        {
            node.getR().apply(this);
        }
        if(node.getL() != null)
        {
            node.getL().apply(this);
        }
        outADivideExp(node);
    }

    public void inAUminusExp(AUminusExp node)
    {
        defaultIn(node);
    }

    public void outAUminusExp(AUminusExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAUminusExp(AUminusExp node)
    {
        inAUminusExp(node);
        if(node.getExp() != null)
        {
            node.getExp().apply(this);
        }
        outAUminusExp(node);
    }

    public void inAIdExp(AIdExp node)
    {
        defaultIn(node);
    }

    public void outAIdExp(AIdExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAIdExp(AIdExp node)
    {
        inAIdExp(node);
        if(node.getId() != null)
        {
            node.getId().apply(this);
        }
        outAIdExp(node);
    }

    public void inAFloatExp(AFloatExp node)
    {
        defaultIn(node);
    }

    public void outAFloatExp(AFloatExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAFloatExp(AFloatExp node)
    {
        inAFloatExp(node);
        if(node.getFloat() != null)
        {
            node.getFloat().apply(this);
        }
        outAFloatExp(node);
    }

    public void inAIntExp(AIntExp node)
    {
        defaultIn(node);
    }

    public void outAIntExp(AIntExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAIntExp(AIntExp node)
    {
        inAIntExp(node);
        if(node.getInt() != null)
        {
            node.getInt().apply(this);
        }
        outAIntExp(node);
    }

    public void inAStringExp(AStringExp node)
    {
        defaultIn(node);
    }

    public void outAStringExp(AStringExp node)
    {
        defaultOut(node);
    }

    @Override
    public void caseAStringExp(AStringExp node)
    {
        inAStringExp(node);
        if(node.getString() != null)
        {
            node.getString().apply(this);
        }
        outAStringExp(node);
    }
}
