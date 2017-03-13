package golite;

import golite.node.*;
import golite.analysis.*;

import java.util.*;

public class Weeder extends DepthFirstAdapter
{
    public void inAAssignListStmt(AAssignListStmt node)
    {
        // Stores true if all the lvalues are identifiers
        boolean allIds = true;

        // Checks if all lvalues are identifiers
        List<PExp> lvalues = (List<PExp>)node.getL();
        for (int i = 0; i < lvalues.size(); i++)
        {
            if (!(lvalues.get(i) instanceof AIdExp))
            {
                allIds = false;
            }
        }

        // Throw an exception if non-identifiers are used with ":="        
        if (!allIds && node.getOp() instanceof AColonEqualsExp)
        {
            throw new RuntimeException("Short assignment (:=) can only "+
                   "be used with ids"); 
        }

        // Op-equals assignments (+=,-=,etc) can only be performed on one identifier
        if (node.getOp() instanceof AOpEqualsExp && lvalues.size() > 1)
        {
            throw new RuntimeException("Operation assignment (+=,-=,etc) can only " +
                "be used with one identifier");
        }

        int lsize = node.getL().size();
        int rsize = node.getR().size();

        if (lsize != rsize)
        {
            throw new RuntimeException("Number of ids and expressions do not match in assignment statement");
        }
    }

    public boolean isId(PExp node)
    {
        if (node instanceof AIdExp) return true;

        return false;
    }

    public void inADecrementStmt(ADecrementStmt node)
    {
        //if (isBlankId())
    }

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

    public void inAIdExp(AIdExp node) {
        PIdType id = node.getIdType();
        if (id instanceof AIdIdType) {
            AIdIdType temp = (AIdIdType) id;
            if (temp.getId().getText().equals("_")) {
                throwBlankIdError();
            }
        }
    }

    // public void inAStructWithIdTypeDecl(AStructWithIdTypeDecl node) {
    //     PVarType current = node.getVarType();
    //     while (current instanceof ASliceVarType || current instanceOf AArrayVarType) {
    //         current = current.getVarType();
    //     }

    //     if (current instanceof AStructType) {
    //         if (((AStructVarType) current).getId().getText().equals("struct")) {
    //             return;
    //         }
    //     }

    //     throwStructError();
    // }

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

    public void throwBlankIdError() {
        ErrorManager.printError("Trying to evaluate an expression with a blank identifier \n");
    }

    public void throwContinueError()
    {
        ErrorManager.printError("Continue must be inside a loop");
    }

    public void throwBreakError()
    {
        ErrorManager.printError("Break must be inside a loop or a switch statement");
    }
}
