package golite;

import golite.node.*;
import golite.analysis.*;

import java.util.*;

public class Weeder extends DepthFirstAdapter
{
    public void inASwitchStmt(ASwitchStmt node)
    {
        // Check for multiple default statements
        boolean hasDefaultStatement = false;
        for (int i = 0; i < node.getCaseStmts().size(); i++)
        {
            ACaseStmt caseStmt = (ACaseStmt)node.getCaseStmts().get(i);
            
            if (caseStmt.getCaseExp() instanceof ADefaultExp)
            {
                // Throw an exception if the switch-statement has more that
                // one default
                if (hasDefaultStatement)
                {
                    throw new RuntimeException("Two default statements in a switch statement");
                }

                hasDefaultStatement = true;
            }
        }
    }

    public void inANoReturnFuncDecl(ANoReturnFuncDecl node)
    {
        if (hasContinue(((ABlockStmt)node.getBlock()).getStmt()))
        {
            throwContinueError();
        }

        if (hasBreak(((ABlockStmt)node.getBlock()).getStmt()))
        {
            throwBreakError();
        }
    }

    public void inASingleReturnFuncDecl(ASingleReturnFuncDecl node)
    {
        if (hasContinue(((ABlockStmt)node.getBlock()).getStmt()))
        {
            throwContinueError();
        }

        if (hasBreak(((ABlockStmt)node.getBlock()).getStmt()))
        {
            throwBreakError();
        }
    }

    public void inAIfStmt(AIfStmt node)
    {
        if (hasContinue(((ABlockStmt)node.getBlock()).getStmt()))
        {
            throwContinueError();
        }

        if (hasBreak(((ABlockStmt)node.getBlock()).getStmt()))
        {
            throwBreakError();
        }
    }

    public void inAElseStmt(AElseStmt node)
    {
        if (hasContinue(((ABlockStmt)node.getStmt()).getStmt()))
        {
            throwContinueError();
        }

        if (hasBreak(((ABlockStmt)node.getStmt()).getStmt()))
        {
            throwBreakError();
        }
    }

    public void inACaseStmt(ACaseStmt node)
    {
        if (hasContinue(node.getStmtList()))
        {
            throwContinueError();
        }
    }

    public boolean hasContinue(List<PStmt> nodes)
    {
        for (int i = 0; i < nodes.size(); i++)
        {
            PStmt node = nodes.get(i);

            if (node instanceof AContinueStmt)
            {
                return true;
            }
        }

        return false;
    }

    public boolean hasBreak(List<PStmt> nodes)
    {
        for (int i = 0; i < nodes.size(); i++)
        {
            PStmt node = nodes.get(i);

            if (node instanceof ABreakStmt)
            {
                return true;
            }
        }

        return false;
    } 

    public void throwContinueError()
    {
        throw new RuntimeException("Continue must be inside a loop");
    }

    public void throwBreakError()
    {
        throw new RuntimeException("Break must be inside a loop or a switch statement");
    }
}
