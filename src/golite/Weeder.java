package golite;

import golite.node.*;
import golite.analysis.*;

import java.util.*;

public class Weeder extends DepthFirstAdapter
{
    //short decls
    public void inAAssignListStmt(AAssignListStmt node)
    {
        // Stores true if all the lvalues are identifiers
        boolean allIds = true;
        boolean allLValues = true;
        // Check size 
        int lsize = node.getL().size();
        int rsize = node.getR().size();

        if (lsize != rsize)
        {
            throw new RuntimeException("Number of ids and expressions do not match in assignment statement");
        }
        // Checks if all lvalues are identifiers or array elements 
        List<PExp> lvalues = (List<PExp>)node.getL();
        for (int i = 0; i < lvalues.size(); i++)
        {   

            if (!(lvalues.get(i) instanceof AIdExp)) {
                allIds = false;
                if (!(lvalues.get(i) instanceof AArrayElementExp)) {
                    allLValues = false;
                }
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

        if (node.getOp() instanceof AOpEqualsExp && !allLValues)
        {
            throw new RuntimeException("Operation assignment (+=,-=,etc) can only " +
                "be used with proper LValues ");
        }

    }

    //check expr is id or arraySelector
    public void inAIncrementStmt(AIncrementStmt node) {
        if (!(node.getExp() instanceof AIdExp || node.getExp() instanceof AArrayElementExp)) {
            throw new RuntimeException("Increment assignment ++ can only " +
                "be used with proper LValues ");
        }
    }

    public void inADecrementStmt(ADecrementStmt node) {
        if (!(node.getExp() instanceof AIdExp || node.getExp() instanceof AArrayElementExp)) {
            throw new RuntimeException("Increment assignment -- can only " +
                "be used with proper LValues ");
        }
    }

    public boolean isId(PExp node)
    {
        if (node instanceof AIdExp) return true;

        return false;
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
    public void inAFieldExp(AFieldExp node) {
        Node id = node.getIdType();
        if (id instanceof AIdIdType) {
            AIdIdType newTemp = (AIdIdType) id;
            if (newTemp.getId().getText().equals("_")) {
                throwBlankIdError("Trying to evaluate a struct selector with a blank identifier \n");
            }
        }
    }

    public void caseAAssignListStmt(AAssignListStmt node) {
        inAAssignListStmt(node);
        {
            List<PExp> copy = new ArrayList<PExp>(node.getL());
                for(PExp e : copy) {   
                    if (e instanceof AIdExp) {
                        AIdExp temp = (AIdExp) e;
                        if (temp.getIdType() instanceof AIdIdType) {
                        AIdIdType newTemp = (AIdIdType) temp.getIdType();
                        if (newTemp.getId().getText().equals("_") && copy.size() > 1) {
                            //nothing
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

    public void caseAIdExp(AIdExp node) {
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

    // public void inAStructSelectorExp(AStructSelectorExp node) {
    //     PExp right = node.getR();
    //     if (right instanceof AIdExp) {
    //         AIdExp temp = (AIdExp) right;
    //         if (temp.getIdType() instanceof AIdIdType) {
    //             AIdIdType newTemp = (AIdIdType) temp.getIdType();
    //             if (newTemp.getId().getText().equals("_")) {
    //                 throwBlankIdError("Trying to evaluate an expression with a blank identifier \n");
    //             }
    //         }
    //     }
    // }

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
