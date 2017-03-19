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

        if (!containsAReturn(((ABlockStmt)node.getBlock()).getStmt())) {
            throwReturnError();
        }
    } 

    public boolean containsAReturn(List<PStmt> nodes){
        for (int i = 0; i < nodes.size(); i++){
            PStmt node = nodes.get(i);
            if(node instanceof AReturnStmt){
                return true;
            }

            if (node instanceof AForStmt){
                AForStmt forStmt = (AForStmt)node;
                if(containsAReturn(((ABlockStmt)forStmt.getBlock()).getStmt())) {
                    return true;
                }
            }

            if (node instanceof AIfStmt){
                Node current = node;
                while (current instanceof AIfStmt) {
                    AIfStmt temp = (AIfStmt) current;
                    if(containsAReturn(((ABlockStmt) temp.getBlock()).getStmt())) {
                        PStmt next = temp.getEnd();
                        if (next instanceof AElseIfStmt) {
                        current = (AIfStmt) ((AElseIfStmt) next).getStmt();
                        continue;
                        } else {
                            current = next;
                        }
                    } else {
                        return false;
                    }
                }
                if (current instanceof AElseStmt) {
                        AElseStmt temp = (AElseStmt) current;
                        return containsAReturn(((ABlockStmt) temp.getStmt()).getStmt());
                } else {
                        return true;
                }
            }

            if (node instanceof AElseStmt) {
                AElseStmt current = (AElseStmt) node;
                ABlockStmt block = (ABlockStmt) current.getStmt();
                return containsAReturn(block.getStmt());
            }
        }
        return false;
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

    public void caseAAssignListStmt(AAssignListStmt node) {
        inAAssignListStmt(node);
        {
            List<PExp> copy = new ArrayList<PExp>(node.getL());
            for(PExp e : copy)
            {   
                if (e instanceof AIdExp) {
                    AIdExp temp = (AIdExp) e;
                    if (temp.getIdType() instanceof AIdIdType) {
                    AIdIdType newTemp = (AIdIdType) temp.getIdType();
                    if (newTemp.getId().getText().equals("_")) {
                    } else {
                        e.apply(this);
                        }
                    }
                } else {
                    e.apply(this);
                }
            }
        }
        if(node.getOp() != null)
        {
            node.getOp().apply(this);
        }
        {
            List<PExp> copy = new ArrayList<PExp>(node.getR());
            for(PExp e : copy)
            {
                e.apply(this);
            }
        }
        outAAssignListStmt(node);
    }

    public void inAIdExp(AIdExp node) {
        PIdType id = node.getIdType();
        if (id instanceof AIdIdType) {
            AIdIdType temp = (AIdIdType) id;
            if (temp.getId().getText().equals("_")) {
                throwBlankIdError("Trying to evaluate an expression with a blank identifier \n");
            }
        }
    }


    public void inAPackageDecl(APackageDecl node) {
        PIdType id = node.getIdType();
        if (id instanceof AIdIdType) {
            AIdIdType temp = (AIdIdType) id;
            if (temp.getId().getText().equals("_")) {
                throwBlankIdError("Trying to declare a package with a blank identifier \n");
            }
        }
    }

    public void inAStructSelectorExp(AStructSelectorExp node) {
        PExp right = node.getR();
        if (right instanceof AIdExp) {
            AIdExp temp = (AIdExp) right;
            if (temp.getIdType() instanceof AIdIdType) {
                AIdIdType newTemp = (AIdIdType) temp.getIdType();
                if (newTemp.getId().getText().equals("_")) {
                    throwBlankIdError("Trying to evaluate an expression with a blank identifier \n");
                }
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

    public void throwBlankIdError(String message) {
        ErrorManager.printError(message);
    }

    public void throwContinueError()
    {
        ErrorManager.printError("Continue must be inside a loop");
    }

    public void throwBreakError()
    {
        ErrorManager.printError("Break must be inside a loop or a switch statement");
    }

    public void throwReturnError(){
        throw new RuntimeException("Program missing a return statement");
    }
}
