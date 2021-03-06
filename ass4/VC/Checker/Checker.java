/**
 * Checker.java   
 * Sat Apr 26 18:23:13 EST 2014
 **/

package VC.Checker;

import VC.ASTs.*;
import VC.Scanner.SourcePosition;
import VC.ErrorReporter;
import VC.StdEnvironment;

public final class Checker implements Visitor {

    private static final String FuncDecl = null;

    private String errMesg[] = {
            "*0: main function is missing",
            "*1: return type of main is not int",

            // defined occurrences of identifiers
            // for global, local and parameters
            "*2: identifier redeclared",
            "*3: identifier declared void",
            "*4: identifier declared void[]",

            // applied occurrences of identifiers
            "*5: identifier undeclared",

            // assignments
            "*6: incompatible type for =",
            "*7: invalid lvalue in assignment",

            // types for expressions
            "*8: incompatible type for return",
            "*9: incompatible type for this binary operator",
            "*10: incompatible type for this unary operator",

            // scalars
            "*11: attempt to use an array/fuction as a scalar",

            // arrays
            "*12: attempt to use a scalar/function as an array",
            "*13: wrong type for element in array initialiser",
            "*14: invalid initialiser: array initialiser for scalar",
            "*15: invalid initialiser: scalar initialiser for array",
            "*16: excess elements in array initialiser",
            "*17: array subscript is not an integer",
            "*18: array size missing",

            // functions
            "*19: attempt to reference a scalar/array as a function",

            // conditional expressions in if, for and while
            "*20: if conditional is not boolean",
            "*21: for conditional is not boolean",
            "*22: while conditional is not boolean",

            // break and continue
            "*23: break must be in a while/for",
            "*24: continue must be in a while/for",

            // parameters
            "*25: too many actual parameters",
            "*26: too few actual parameters",
            "*27: wrong type for actual parameter",

            // reserved for errors that I may have missed (J. Xue)
            "*28: misc 1", "*29: misc 2",

            // the following two checks are optional
            "*30: statement(s) not reached", "*31: missing return statement", };

    private SymbolTable idTable;
    private static SourcePosition dummyPos = new SourcePosition();
    private ErrorReporter reporter;

    // Checks whether the source program, represented by its AST,
    // satisfies the language's scope rules and type rules.
    // Also decorates the AST as follows:
    // (1) Each applied occurrence of an identifier is linked to
    // the corresponding declaration of that identifier.
    // (2) Each expression and variable is decorated by its type.

    public Checker(ErrorReporter reporter) {
        this.reporter = reporter;
        this.idTable = new SymbolTable();
        establishStdEnvironment();
    }

    public void check(AST ast) {
        ast.visit(this, null);
    }

    // auxiliary methods

    private void declareVariable(Ident ident, Decl decl) {
        IdEntry entry = idTable.retrieveOneLevel(ident.spelling);

        if (entry == null) {
            ; // no problemdeclareVariable
        } else
            reporter.reportError(errMesg[2] + ": %", ident.spelling,
                    ident.position);
        idTable.insert(ident.spelling, decl);
    }

    // Programs

    public Object visitProgram(Program ast, Object o) {
        ast.FL.visit(this, null);

        if (!isMainExist)
            reporter.reportError(errMesg[0], "", ast.position);

        return null;
    }

    /** ------------------------ Statements-------------------------- **/

    public Object visitCompoundStmt(CompoundStmt ast, Object o) {

        if (!(o instanceof FuncDecl))
            idTable.openScope();

        ast.DL.visit(this, o);
        ast.SL.visit(this, o);

        if (!(o instanceof FuncDecl))
            idTable.closeScope();
        return null;
    }

    public Object visitStmtList(StmtList ast, Object o) {
        ast.S.visit(this, o);
        if (ast.S instanceof ReturnStmt && ast.SL instanceof StmtList)
            reporter.reportError(errMesg[30], "", ast.SL.position);
        ast.SL.visit(this, o);
        return null;
    }

    public Object visitExprStmt(ExprStmt ast, Object o) {
        ast.E.visit(this, o);
        return null;
    }

    public Object visitEmptyStmt(EmptyStmt ast, Object o) {
        return null;
    }

    public Object visitEmptyStmtList(EmptyStmtList ast, Object o) {
        return null;
    }

    @Override
    public Object visitIfStmt(IfStmt ast, Object o) {
        idTable.openScope();

        ast.E.visit(this, o);
        if (!ast.E.type.isBooleanType())
            reporter.reportError(errMesg[20] + "(found: %)",
                    ast.E.type.toString(), ast.E.position);

        ast.S1.visit(this, o);
        ast.S2.visit(this, o);

        idTable.closeScope();
        return null;
    }

    @Override
    public Object visitWhileStmt(WhileStmt ast, Object o) {

        isInLoop = true;
        idTable.openScope();

        ast.E.visit(this, o);
        if (!ast.E.type.isBooleanType())
            reporter.reportError(errMesg[22] + "(found: %)",
                    ast.E.type.toString(), ast.E.position);
        ast.S.visit(this, o);

        idTable.closeScope();
        isInLoop = false;
        return null;
    }

    @Override
    public Object visitForStmt(ForStmt ast, Object o) {

        isInLoop = true;
        idTable.openScope();

        ast.E1.visit(this, o);
        if (!(ast.E1.type.isBooleanType() || ast.E1.type.isErrorType()))
            reporter.reportError(errMesg[21] + "(found: %)",
                    ast.E1.type.toString(), ast.E1.position);

        ast.E2.visit(this, o);
        if (!(ast.E2.type.isBooleanType() || ast.E2.type.isErrorType()))
            reporter.reportError(errMesg[21] + "(found: %)",
                    ast.E2.type.toString(), ast.E2.position);
        ast.E3.visit(this, o);

        if (!(ast.E3.type.isBooleanType() || ast.E3.type.isErrorType()))
            reporter.reportError(errMesg[21] + "(found: %)",
                    ast.E3.type.toString(), ast.E3.position);
        ast.S.visit(this, o);

        idTable.closeScope();

        isInLoop = false;

        return null;
    }

    boolean isInLoop = false;

    @Override
    public Object visitBreakStmt(BreakStmt ast, Object o) {
        if (!isInLoop)
            reporter.reportError(errMesg[23], "", ast.position);

        isInLoop = false;

        // ast.visit(this, o);

        return null;
    }

    @Override
    public Object visitContinueStmt(ContinueStmt ast, Object o) {
        // TODO Auto-generated method stub

        if (!isInLoop)
            reporter.reportError(errMesg[24], "", ast.position);

        // ast.visit(this, o);

        return null;
    }

    @Override
    public Object visitReturnStmt(ReturnStmt ast, Object o) {
        // TODO Auto-generated method stub

        ast.E.visit(this, o);

        FuncDecl fAST = (FuncDecl) o;
        if (!fAST.T.assignable(ast.E.type))
            reporter.reportError(errMesg[8], "", ast.position);
        if (ast.E.isEmptyExpr() && !fAST.T.isVoidType())
            reporter.reportError(errMesg[31], "", ast.position);

        return null;
    }

    @Override
    public Object visitEmptyCompStmt(EmptyCompStmt ast, Object o) {
        return null;
    }

    /** ------------------------ Expressions -------------------------- **/

    // Returns the Type denoting the type of the expression. Does
    // not use the given object.

    @Override
    public Object visitEmptyExprList(EmptyExprList ast, Object o) {
        // TODO Auto-generated method stub
        return null;
    }

    public Object visitEmptyExpr(EmptyExpr ast, Object o) {
        ast.type = StdEnvironment.errorType;
        return ast.type;
    }

    public Object visitBooleanExpr(BooleanExpr ast, Object o) {
        ast.type = StdEnvironment.booleanType;
        return ast.type;
    }

    public Object visitIntExpr(IntExpr ast, Object o) {
        ast.type = StdEnvironment.intType;
        return ast.type;
    }

    public Object visitFloatExpr(FloatExpr ast, Object o) {
        ast.type = StdEnvironment.floatType;
        return ast.type;
    }

    public Object visitStringExpr(StringExpr ast, Object o) {
        ast.type = StdEnvironment.stringType;
        return ast.type;
    }

    public Object visitVarExpr(VarExpr ast, Object o) {
        ast.type = (Type) ast.V.visit(this, null);
        return ast.type;
    }

    @Override
    public Object visitUnaryExpr(UnaryExpr ast, Object o) {
        ast.E.visit(this, o);

        if (ast.O.spelling.equals("&&") || ast.O.spelling.equals("||")
                || ast.O.spelling.equals("!")) {
            if (!ast.E.type.isBooleanType())
                reporter.reportError(errMesg[10] + ": %", ast.O.spelling,
                        ast.position);
            ast.O.spelling = "i" + ast.O.spelling;
        } else if ((ast.O.spelling.equals("==") || ast.O.spelling.equals("!="))
                && ast.E.type.isBooleanType()) {
            ast.O.spelling = "i" + ast.O.spelling;
        } else {
            if (ast.E.type.isFloatType())
                ast.O.spelling = "f" + ast.O.spelling;
            else if (ast.E.type.isIntType())
                ast.O.spelling = "i" + ast.O.spelling;
        }

        ast.O.visit(this, o);
        return ast.type;
    }

    @Override
    public Object visitBinaryExpr(BinaryExpr ast, Object o) {

        Type t1AST = (Type) ast.E1.visit(this, o);
        Type t2AST = (Type) ast.E2.visit(this, o);

        if (t1AST == null)
            t1AST = StdEnvironment.errorType;
        if (t2AST == null)
            t2AST = StdEnvironment.errorType;

        if (ast.E1.type == null)
            ast.E1.type = t1AST;
        if (ast.E2.type == null)
            ast.E2.type = t2AST;

        ast.O.visit(this, o);

        if (!(ast.E1.type.isErrorType() || ast.E2.type.isErrorType())) {

            if (ast.E1.type.isIntType() && ast.E2.type.isFloatType()) {
                Operator op = new Operator("i2f", dummyPos);
                UnaryExpr eAST = new UnaryExpr(op, ast.E1, dummyPos);
                eAST.E.type = StdEnvironment.floatType;
                ast.E1 = eAST;
            }

            if (ast.E2.type.isIntType() && ast.E1.type.isFloatType()) {
                Operator op = new Operator("i2f", dummyPos);
                UnaryExpr eAST = new UnaryExpr(op, ast.E2, dummyPos);
                eAST.E.type = StdEnvironment.floatType;
                ast.E2 = eAST;
            }

            if (!(ast.E1.type.isIntType() || ast.E1.type.isFloatType())) {
                if (ast.E1.type.isArrayType())
                    if (ast.E1 instanceof VarExpr)
                        reporter.reportError(errMesg[11] + ": %",
                                ((SimpleVar) ((VarExpr) ast.E1).V).I.spelling,
                                ast.E1.position);

                reporter.reportError(errMesg[9] + ": %", ast.O.spelling,
                        ast.position);
            }
            if (!(ast.E2.type.isIntType() || ast.E2.type.isFloatType())) {
                if (ast.E2.type.isArrayType())
                    if (ast.E2 instanceof VarExpr)
                        reporter.reportError(errMesg[11] + ": %",
                                ((SimpleVar) ((VarExpr) ast.E2).V).I.spelling,
                                ast.E2.position);

                reporter.reportError(errMesg[9] + ": %", ast.O.spelling,
                        ast.position);
            }

        }

        return ast.type;
    }

    @Override
    public Object visitInitExpr(InitExpr ast, Object o) {

        Decl dAST = null;
        if (o instanceof GlobalVarDecl)
            dAST = (GlobalVarDecl) o;
        if (o instanceof LocalVarDecl)
            dAST = (LocalVarDecl) o;

        Type tAST = null;
        if (dAST.T.isArrayType()) {
            ast.IL.visit(this, o);

            checkArrayInitTypes((ArrayType) dAST.T, (ExprList) ast.IL, 0);

            int elemNum = Integer
                    .parseInt(((IntExpr) ((ArrayType) dAST.T).E).IL.spelling);
            int sum = 0;
            ExprList elAST = (ExprList) ast.IL;

            while (!elAST.isEmptyExprList()) {
                sum++;
                if (!elAST.EL.isEmptyExprList()) {
                    elAST = (ExprList) elAST.EL;

                } else
                    break;
            }
            if (elemNum < sum)
                reporter.reportError(errMesg[16] + ": %", dAST.I.spelling,
                        dAST.position);

            ast.type = ((ArrayType) dAST.T).T;
        } else {
            tAST = (Type) ast.IL.visit(this, o);
        }

        if (ast.type == null)
            ast.type = tAST;

        if (!dAST.T.assignable(ast.type) && !dAST.T.isArrayType()) {
            reporter.reportError(errMesg[14], "", ast.IL.position);
        }

        return ast.type;
    }

    public void checkArrayInitTypes(ArrayType tAST, ExprList elAST, int pos) {

        Type ast = tAST.T;

        if (!ast.assignable(elAST.E.type))
            reporter.reportError(errMesg[13] + ": at position %", "" + pos,
                    elAST.E.position);
        if (!elAST.EL.isEmptyExprList())
            checkArrayInitTypes(tAST, (ExprList) elAST.EL, ++pos);

    }

    @Override
    public Object visitExprList(ExprList ast, Object o) {
        ast.E.visit(this, o);
        ast.EL.visit(this, ast);

        return ast.E.type;
    }

    @Override
    public Object visitArrayExpr(ArrayExpr ast, Object o) {

        Type tAST = (Type) ast.V.visit(this, ast);
        if (!tAST.isArrayType())
            reporter.reportError(errMesg[14], "", ast.V.position);
        if (!(ast.E instanceof IntExpr))
            reporter.reportError(errMesg[17], "", ast.position);
        ast.E.visit(this, ast);

        return ast.type;
    }

    @Override
    public Object visitCallExpr(CallExpr ast, Object o) {
        // TODO Auto-generated method stub

        Decl decl = idTable.retrieve(ast.I.spelling);
        if (decl == null) {
            reporter.reportError(errMesg[5] + ": %", ast.I.spelling,
                    ast.position);
        } else if (!decl.isFuncDecl()) {
            reporter.reportError(errMesg[11] + ": %", decl.I.spelling,
                    ast.I.position);
        }

        if (ast.type == null)
            if (decl != null && decl.isFuncDecl()) {
                ast.type = decl.T;

                ast.I.visit(this, o);
                ast.AL.visit(this, o);

                FuncDecl dAST = (VC.ASTs.FuncDecl) decl;
                comparArgListToParaList((ArgList) ast.AL, (ParaList) dAST.PL);
            } else
                ast.type = StdEnvironment.errorType;

        return ast.type;
    }

    public void comparArgListToParaList(ArgList aL, ParaList pL) {

        if (!pL.P.T.isArrayType()) {
            if (pL.isEmptyParaList() && !aL.isEmptyArgList())
                reporter.reportError(errMesg[25], "", aL.A.position);

            if (!pL.isEmptyParaList() && aL.isEmptyArgList())
                reporter.reportError(errMesg[26], "", aL.position);

            if (!pL.P.T.assignable(aL.A.type))
                reporter.reportError(errMesg[27] + ": %", pL.P.I.spelling,
                        aL.A.position);

            if (!aL.AL.isEmptyArgList() && !pL.PL.isEmptyParaList()) {
                comparArgListToParaList((ArgList) aL.AL, (ParaList) pL.PL);
            } else if (aL.AL.isEmptyArgList() && !pL.PL.isEmptyParaList()) {
                reporter.reportError(errMesg[26], "", aL.A.position);
            } else if (!aL.AL.isEmptyArgList() && pL.PL.isEmptyParaList()) {
                reporter.reportError(errMesg[25], "", aL.AL.position);
            }
        } else {
            if (!aL.A.type.isArrayType()) {
                reporter.reportError(errMesg[27] + ": %", pL.P.I.spelling,
                        aL.A.position);
            } else {
                ArrayType platAST = (ArrayType) pL.P.T;
                ArrayType alatAST = (ArrayType) aL.A.type;

                if (!platAST.T.assignable(alatAST.T))
                    reporter.reportError(errMesg[27] + ": %", pL.P.I.spelling,
                            aL.A.position);

                if (!aL.AL.isEmptyArgList() && !pL.PL.isEmptyParaList()) {
                    comparArgListToParaList((ArgList) aL.AL, (ParaList) pL.PL);
                } else if (aL.AL.isEmptyArgList() && !pL.PL.isEmptyParaList()) {
                    reporter.reportError(errMesg[26], "", aL.A.position);
                } else if (!aL.AL.isEmptyArgList() && pL.PL.isEmptyParaList()) {
                    reporter.reportError(errMesg[25], "", aL.AL.position);
                }
            }
        }

    }

    public void compareArrListToArrayParaList() {

    }

    @Override
    public Object visitAssignExpr(AssignExpr ast, Object o) {
        // TODO Auto-generated method stub

        if (!(ast.E1 instanceof VarExpr)) {
            reporter.reportError(errMesg[7], "", ast.position);
        } else {

            Type t1AST = (Type) ast.E1.visit(this, ast);
            if (t1AST == null)
                t1AST = StdEnvironment.errorType;
            if (ast.E1.type == null)
                ast.E1.type = t1AST;

            VarExpr veAST = (VarExpr) ast.E1;
            if (veAST.V instanceof SimpleVar) {
                SimpleVar svAST = (SimpleVar) veAST.V;
                if (svAST.I.decl instanceof FuncDecl)
                    reporter.reportError(errMesg[11] + ": %", svAST.I.spelling,
                            svAST.I.position);
            }

            Type t2AST = (Type) ast.E2.visit(this, ast);
            if (t2AST == null)
                t2AST = StdEnvironment.errorType;
            if (ast.E2.type == null)
                ast.E2.type = t2AST;

            if (!ast.E1.type.assignable(ast.E2.type))
                reporter.reportError(errMesg[6], "", ast.position);

            if (veAST.V instanceof SimpleVar) {
                SimpleVar svAST = (SimpleVar) veAST.V;
                if (ast.E1.type.isErrorType() || ast.E2.type.isErrorType())
                    reporter.reportError(errMesg[7] + ": %", svAST.I.spelling,
                            ast.position);
            }

            if (ast.E1.type.isFloatType() && ast.E2.type.isIntType()) {
                Operator op = new Operator("i2f", dummyPos);
                UnaryExpr eAST = new UnaryExpr(op, ast.E2, dummyPos);
                eAST.type = StdEnvironment.floatType;
                ast.E2 = eAST;
            }

        }
        return ast.type;
    }

    /** ------------------------ Declarations-------------------------- **/

    // Always returns null. Does not use the given object.

    boolean isMainExist = false;

    public Object visitFuncDecl(FuncDecl ast, Object o) {

        if (idTable.retrieveOneLevel(ast.I.spelling) != null)
            reporter.reportError(errMesg[2] + ": %", ast.I.spelling,
                    ast.I.position);

        idTable.insert(ast.I.spelling, ast);

        // TODO

        // HINT
        // Pass ast as the 2nd argument (as done below) so that the
        // formal parameters of the function an be extracted from ast when the
        // function body is later visited

        idTable.openScope();
        ast.PL.visit(this, ast);

        ast.S.visit(this, ast);
        idTable.closeScope();
        if (!ast.T.isVoidType() && ast.S.isEmptyCompStmt())
            reporter.reportError(errMesg[31], "", ast.position);

        if (ast.I.spelling.equals("main")) {
            isMainExist = true;
            if (!ast.T.isIntType())
                reporter.reportError(errMesg[1], "", ast.position);
        }

        return ast.T;
    }

    public Object visitDeclList(DeclList ast, Object o) {
        Type t = (Type) ast.D.visit(this, null);
        if (ast.D.T == null)
            ast.D.T = t;
        ast.DL.visit(this, null);
        return null;
    }

    public Object visitEmptyDeclList(EmptyDeclList ast, Object o) {
        return null;
    }

    public Object visitGlobalVarDecl(GlobalVarDecl ast, Object o) {
        declareVariable(ast.I, ast);

        if (ast.T.isVoidType()) {
            reporter.reportError(errMesg[3] + ": %", ast.I.spelling,
                    ast.I.position);
        } else if (ast.T.isArrayType()) {
            if (((ArrayType) ast.T).T.isVoidType())
                reporter.reportError(errMesg[4] + ": %", ast.I.spelling,
                        ast.I.position);
            if (!ast.E.isEmptyExpr() && !(ast.E instanceof InitExpr))
                reporter.reportError(errMesg[15] + ": %", ast.I.spelling,
                        ast.position);
        }

        ast.I.visit(this, ast);
        ast.E.visit(this, ast);
        ast.T.visit(this, ast);

        return ast.T;
    }

    public Object visitLocalVarDecl(LocalVarDecl ast, Object o) {
        declareVariable(ast.I, ast);

        if (ast.T.isVoidType()) {
            reporter.reportError(errMesg[3] + ": %", ast.I.spelling,
                    ast.I.position);
        } else if (ast.T.isArrayType()) {
            if (((ArrayType) ast.T).T.isVoidType())
                reporter.reportError(errMesg[4] + ": %", ast.I.spelling,
                        ast.I.position);
            if (!ast.E.isEmptyExpr() && !(ast.E instanceof InitExpr))
                reporter.reportError(errMesg[15] + ": %", ast.I.spelling,
                        ast.position);
        }

        ast.I.visit(this, ast);
        ast.E.visit(this, ast);
        ast.T.visit(this, ast);

        return ast.T;
    }

    /** ------------------------ Parameters-------------------------- **/

    // Always returns null. Does not use the given object.

    public Object visitEmptyParaList(EmptyParaList ast, Object o) {
        return null;
    }

    public Object visitParaList(ParaList ast, Object o) {
        ast.P.T = (Type) ast.P.visit(this, ast);
        ast.PL.visit(this, ast);
        return null;
    }

    public Object visitParaDecl(ParaDecl ast, Object o) {
        declareVariable(ast.I, ast);

        if (ast.T.isVoidType()) {
            reporter.reportError(errMesg[3] + ": %", ast.I.spelling,
                    ast.I.position);
        } else if (ast.T.isArrayType()) {
            if (((ArrayType) ast.T).T.isVoidType())
                reporter.reportError(errMesg[4] + ": %", ast.I.spelling,
                        ast.I.position);
        }

        ast.I.visit(this, ast);
        ast.T.visit(this, ast);
        return ast.T;
    }

    /** ------------------------ Arguments-------------------------- **/

    @Override
    public Object visitEmptyArgList(EmptyArgList ast, Object o) {
        return null;
    }

    @Override
    public Object visitArgList(ArgList ast, Object o) {
        // TODO Auto-generated method stub

        ast.A.visit(this, o);
        ast.AL.visit(this, o);
        return null;
    }

    @Override
    public Object visitArg(Arg ast, Object o) {
        // TODO Auto-generated method stub

        Type tAST = (Type) ast.E.visit(this, o);

        if (ast.type == null)
            ast.type = tAST;

        return ast.type;
    }

    /** ------------------------ Types-------------------------- **/

    // Returns the type predefined in the standard environment.

    public Object visitErrorType(ErrorType ast, Object o) {
        return StdEnvironment.errorType;
    }

    public Object visitBooleanType(BooleanType ast, Object o) {
        return StdEnvironment.booleanType;
    }

    public Object visitIntType(IntType ast, Object o) {
        return StdEnvironment.intType;
    }

    public Object visitFloatType(FloatType ast, Object o) {
        return StdEnvironment.floatType;
    }

    public Object visitStringType(StringType ast, Object o) {
        return StdEnvironment.stringType;
    }

    public Object visitVoidType(VoidType ast, Object o) {
        return StdEnvironment.voidType;
    }

    @Override
    public Object visitArrayType(ArrayType ast, Object o) {
        Decl vAST = null;

        if (o instanceof GlobalVarDecl)
            vAST = (GlobalVarDecl) o;
        else if (o instanceof LocalVarDecl)
            vAST = (LocalVarDecl) o;

        if (ast.E.isEmptyExpr() && vAST != null)
            reporter.reportError(errMesg[18] + ": %", vAST.I.spelling,
                    ast.position);
        return null;
    }

    /** -----------------Literals, Identifiers and Operators----------------- **/

    public Object visitIdent(Ident I, Object o) {
        Decl binding = idTable.retrieve(I.spelling);
        if (binding != null)
            I.decl = binding;
        return binding;
    }

    public Object visitBooleanLiteral(BooleanLiteral SL, Object o) {
        return StdEnvironment.booleanType;
    }

    public Object visitIntLiteral(IntLiteral IL, Object o) {
        return StdEnvironment.intType;
    }

    public Object visitFloatLiteral(FloatLiteral IL, Object o) {
        return StdEnvironment.floatType;
    }

    public Object visitStringLiteral(StringLiteral IL, Object o) {
        return StdEnvironment.stringType;
    }

    public Object visitOperator(Operator O, Object o) {
        return null;
    }

    @Override
    public Object visitSimpleVar(SimpleVar ast, Object o) {
        // TODO Auto-generated method stub
        Decl decl = idTable.retrieve(ast.I.spelling);

        if (decl == null) {
            reporter.reportError(errMesg[5] + ": %", ast.I.spelling,
                    ast.I.position);
            ast.type = StdEnvironment.errorType;
        } else {
            ast.type = decl.T;
        }
        ast.I.visit(this, o);

        return ast.type;
    }

    // Creates a small AST to represent the "declaration" of each built-in
    // function, and enters it in the symbol table.

    private FuncDecl declareStdFunc(Type resultType, String id, List pl) {

        FuncDecl binding;

        binding = new FuncDecl(resultType, new Ident(id, dummyPos), pl,
                new EmptyStmt(dummyPos), dummyPos);
        idTable.insert(id, binding);
        return binding;
    }

    // Creates small ASTs to represent "declarations" of all
    // build-in functions.
    // Inserts these "declarations" into the symbol table.

    private final static Ident dummyI = new Ident("x", dummyPos);

    private void establishStdEnvironment() {

        // Define four primitive types
        // errorType is assigned to ill-typed expressions

        StdEnvironment.booleanType = new BooleanType(dummyPos);
        StdEnvironment.intType = new IntType(dummyPos);
        StdEnvironment.floatType = new FloatType(dummyPos);
        StdEnvironment.stringType = new StringType(dummyPos);
        StdEnvironment.voidType = new VoidType(dummyPos);
        StdEnvironment.errorType = new ErrorType(dummyPos);

        // enter into the declarations for built-in functions into the table

        StdEnvironment.getIntDecl = declareStdFunc(StdEnvironment.intType,
                "getInt", new EmptyParaList(dummyPos));
        StdEnvironment.putIntDecl = declareStdFunc(StdEnvironment.voidType,
                "putInt", new ParaList(new ParaDecl(StdEnvironment.intType,
                        dummyI, dummyPos), new EmptyParaList(dummyPos),
                        dummyPos));
        StdEnvironment.putIntLnDecl = declareStdFunc(StdEnvironment.voidType,
                "putIntLn", new ParaList(new ParaDecl(StdEnvironment.intType,
                        dummyI, dummyPos), new EmptyParaList(dummyPos),
                        dummyPos));
        StdEnvironment.getFloatDecl = declareStdFunc(StdEnvironment.floatType,
                "getFloat", new EmptyParaList(dummyPos));
        StdEnvironment.putFloatDecl = declareStdFunc(StdEnvironment.voidType,
                "putFloat", new ParaList(new ParaDecl(StdEnvironment.floatType,
                        dummyI, dummyPos), new EmptyParaList(dummyPos),
                        dummyPos));
        StdEnvironment.putFloatLnDecl = declareStdFunc(StdEnvironment.voidType,
                "putFloatLn", new ParaList(new ParaDecl(
                        StdEnvironment.floatType, dummyI, dummyPos),
                        new EmptyParaList(dummyPos), dummyPos));
        StdEnvironment.putBoolDecl = declareStdFunc(StdEnvironment.voidType,
                "putBool", new ParaList(new ParaDecl(
                        StdEnvironment.booleanType, dummyI, dummyPos),
                        new EmptyParaList(dummyPos), dummyPos));
        StdEnvironment.putBoolLnDecl = declareStdFunc(StdEnvironment.voidType,
                "putBoolLn", new ParaList(new ParaDecl(
                        StdEnvironment.booleanType, dummyI, dummyPos),
                        new EmptyParaList(dummyPos), dummyPos));

        StdEnvironment.putStringLnDecl = declareStdFunc(
                StdEnvironment.voidType, "putStringLn", new ParaList(
                        new ParaDecl(StdEnvironment.stringType, dummyI,
                                dummyPos), new EmptyParaList(dummyPos),
                        dummyPos));

        StdEnvironment.putStringDecl = declareStdFunc(StdEnvironment.voidType,
                "putString", new ParaList(new ParaDecl(
                        StdEnvironment.stringType, dummyI, dummyPos),
                        new EmptyParaList(dummyPos), dummyPos));

        StdEnvironment.putLnDecl = declareStdFunc(StdEnvironment.voidType,
                "putLn", new EmptyParaList(dummyPos));

    }

}
