package golite;

import golite.node.*;
import golite.analysis.*;

import java.util.*;

public class Weeder extends DepthFirstAdapter
{
    // The number of switch statements/loops enterred
    private int inALoop, inASwitchStmt;

    // Short decls
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
            // Expressions cannot be print statements
            weedPrintStatement(node.getR().get(i));

            if (!(lvalues.get(i) instanceof AIdExp)) 
            {
                allIds = false;
                if (!(lvalues.get(i) instanceof AArrayElementExp) &&
                    !(lvalues.get(i) instanceof AFieldExp)) 
                {
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

        if ((node.getOp() instanceof AOpEqualsExp || node.getOp() instanceof AEqualsExp) 
            && !allLValues)
        {
            throw new RuntimeException("Assignment (=,+=,-=,etc) can only " +
                "be used with LValues ");
        }

    }

    public void inAVarWithOnlyExpVarDecl(AVarWithOnlyExpVarDecl node)
    {
        weedPrintExpression(node.getExp());
    }

    public void inAVarWithTypeAndExpVarDecl(AVarWithTypeAndExpVarDecl node)
    {   
        weedPrintExpression(node.getExp());
    }

    public void inAInlineListWithExpVarDecl(AInlineListWithExpVarDecl node)
    {
        weedPrintExpression(node.getExp());
    }

    public void weedPrintExpression(Node node)
    {
        if (node instanceof APrintExp || node instanceof APrintlnExp)
                ErrorManager.printError("Invalid print/println expression found");
    }

    public void weedPrintStatement(Node node)
    {
        if (node == null) 
            return;

        if (node instanceof AExpStmt)
        {
            PExp exp = ((AExpStmt)node).getExp();

            if (exp instanceof APrintExp || exp instanceof APrintlnExp)
                ErrorManager.printError("Invalid print/println statement found");
        }
    }

    public void weedForLoopPost(Node node)
    {
        if (node == null) 
            return;

        if (node instanceof AAssignListStmt)
            ErrorManager.printError("Post condition in for-loop cannot be an assignment statement");
    }

    //check expr is id or arraySelector
    public void inAIncrementStmt(AIncrementStmt node) 
    {
        if (!(node.getExp() instanceof AIdExp || node.getExp() instanceof AArrayElementExp)) 
        {
            throw new RuntimeException("Increment assignment ++ can only " +
                "be used with proper LValues ");
        }
    }

    public void inADecrementStmt(ADecrementStmt node) 
    {
        if (!(node.getExp() instanceof AIdExp || node.getExp() instanceof AArrayElementExp)) 
        {
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
        inASwitchStmt++;

        weedPrintStatement(node.getSimpleStmt());

        // Check for multiple default statements
        boolean hasDefaultStatement = false;
        for (int i = 0; i < node.getCaseStmts().size(); i++)
        {
            ACaseStmt caseStmt = (ACaseStmt)node.getCaseStmts().get(i);
            
            if (caseStmt.getCaseExp() instanceof ADefaultExp)
            {
                // Throw an exception if the switch-statement has > 1 default
                if (hasDefaultStatement)
                {
                    throw new RuntimeException("Two default statements in a switch statement");
                }

                hasDefaultStatement = true;
            }
        }
    }

    public void outASwitchStmt(ASwitchStmt node)
    {
        inASwitchStmt--;
    }

    public void inAForStmt(AForStmt node)
    {
        inALoop++;

        // Simple statement cannot be a print statement
        if (node.getCondition() != null && node.getCondition() instanceof AForCondExp)
        {
            weedPrintStatement(((AForCondExp)node.getCondition()).getFirst());
            weedPrintStatement(((AForCondExp)node.getCondition()).getThird());
            weedForLoopPost(((AForCondExp)node.getCondition()).getThird());
        }
    }

    public void outAForStmt(AForStmt node)
    {
        inALoop--;
    }

    public void inABreakStmt(ABreakStmt node)
    {
        if (inALoop <= 0 && inASwitchStmt <= 0)
        {
            throwBreakError();
        }
    }

    public void inAContinueStmt(AContinueStmt node)
    {
        if (inALoop <= 0)
        {
            throwContinueError();
        }
    }

    public void inAExpStmt(AExpStmt node)
    {
        if (node.getExp() instanceof AAppendedExprExp)
            ErrorManager.printError("Cannot have append() as a statement");
    }

    public void inAIfStmt(AIfStmt node)
    {
        weedPrintStatement(node.getSimpleStmt());
    }

    public void inAFieldExp(AFieldExp node) 
    {
        Node id = node.getIdType();
        if (id instanceof AIdIdType) 
        {
            AIdIdType newTemp = (AIdIdType) id;
            if (newTemp.getId().getText().equals("_"))
                throwBlankIdError("Trying to evaluate a struct selector with a blank identifier \n");
        }
    }

    public void caseAAssignListStmt(AAssignListStmt node) 
    {
        inAAssignListStmt(node);
        {
            List<PExp> copy = new ArrayList<PExp>(node.getL());
            for(PExp e : copy) 
            { 
                if (e instanceof AIdExp)
                {
                    AIdExp temp = (AIdExp) e;
                    if (temp.getIdType() instanceof AIdIdType) 
                    {
                        AIdIdType newTemp = (AIdIdType) temp.getIdType();
                        if (newTemp.getId().getText().equals("_") && copy.size() > 1) {
                            //nothing
                        } else {
                            e.apply(this);
                        }
                    }
                } 
                else 
                {
                    e.apply(this);
                }
            }
        }
        if(node.getOp() != null)
        {
            node.getOp().apply(this);
        }

        List<PExp> copy = new ArrayList<PExp>(node.getR());
        for(PExp e : copy)
        {
            e.apply(this);
        }

        outAAssignListStmt(node);
    }

    public void caseAIdExp(AIdExp node) 
    {
        PIdType id = node.getIdType();
        if (id instanceof AIdIdType) 
        {
            AIdIdType temp = (AIdIdType) id;
            if (temp.getId().getText().equals("_")) 
                throwBlankIdError("Trying to evaluate an expression with a blank identifier \n");
        }
    }


    public void inAPackageDecl(APackageDecl node)
    {
        PIdType id = node.getIdType();
        if (id instanceof AIdIdType) 
        {
            AIdIdType temp = (AIdIdType) id;
            if (temp.getId().getText().equals("_")) 
                throwBlankIdError("Trying to declare a package with a blank identifier \n");
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

    public void throwBlankIdError(String message) 
    {
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

    public void throwReturnError()
    {
        throw new RuntimeException("Program missing a return statement");
    }
}
