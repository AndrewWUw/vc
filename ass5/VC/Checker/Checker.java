package VC.Checker;

import VC.ASTs.*;
import VC.Scanner.SourcePosition;
import VC.ErrorReporter;
import VC.StdEnvironment;

public final class Checker implements Visitor {
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

	private static VC.Scanner.SourcePosition dummyPos = new VC.Scanner.SourcePosition();
	private ErrorReporter reporter;
	private int whileLevel = 0;

	public Checker(ErrorReporter paramErrorReporter) {
		this.reporter = paramErrorReporter;
		this.idTable = new SymbolTable();
		establishStdEnvironment();
	}

	private Expr i2f(Expr paramExpr) {
		UnaryExpr localUnaryExpr = new UnaryExpr(new Operator("i2f",
				paramExpr.position), paramExpr, paramExpr.position);
		localUnaryExpr.type = StdEnvironment.floatType;
		localUnaryExpr.parent = paramExpr;
		return localUnaryExpr;
	}

    private Expr checkAssignment(Type paramType, Expr paramExpr,
			String paramString, VC.Scanner.SourcePosition paramSourcePosition) {
		if (!paramType.assignable(paramExpr.type)) {
			this.reporter.reportError(paramString, "", paramSourcePosition);
		} else if (!paramType.equals(paramExpr.type)) {
			return i2f(paramExpr);
		}

		return paramExpr;
	}

	private void declareVariable(Ident paramIdent, Decl paramDecl) {
		IdEntry localIdEntry = this.idTable
				.retrieveOneLevel(paramIdent.spelling);

		if (localIdEntry != null) {

			this.reporter.reportError(this.errMesg[2] + ": %",
					paramIdent.spelling, paramIdent.position);
		}
		this.idTable.insert(paramIdent.spelling, paramDecl);
	}

	private void declareFunction(Ident paramIdent, Decl paramDecl) {
		IdEntry localIdEntry = this.idTable
				.retrieveOneLevel(paramIdent.spelling);

		if (localIdEntry != null) {

			this.reporter.reportError(this.errMesg[2] + ": %",
					paramIdent.spelling, paramIdent.position);
		}

		this.idTable.insert(paramIdent.spelling, paramDecl);
	}

	void reportError(String paramString1, Type paramType, String paramString2,
			VC.Scanner.SourcePosition paramSourcePosition) {
		if (paramType == StdEnvironment.errorType) {
			this.reporter.reportError(paramString1, "", paramSourcePosition);
		} else
			this.reporter.reportError(paramString1 + " (found: " + paramType
					+ ", required: " + paramString2 + ")", "",
					paramSourcePosition);
	}

	public void check(AST paramAST) {
		paramAST.visit(this, null);
	}

	public Object visitProgram(Program ast, Object o) {
		ast.FL.visit(this, null);

		Decl localDecl = this.idTable.retrieve("main");
		if ((localDecl == null) || (!(localDecl instanceof FuncDecl))) {
			this.reporter.reportError(this.errMesg[0], "", ast.position);
		} else if (!StdEnvironment.intType.equals(((FuncDecl) localDecl).T))
			this.reporter.reportError(this.errMesg[1], "", ast.position);
		return null;
	}

	public Object visitIfStmt(IfStmt ast, Object o) {
		Type localType = (Type) ast.E.visit(this, null);
		if (!localType.equals(StdEnvironment.booleanType))
			this.reporter.reportError(this.errMesg[20] + " (found: "
					+ localType.toString() + ")", "", ast.E.position);
		ast.S1.visit(this, o);
		ast.S2.visit(this, o);
		return null;
	}

	public Object visitCompoundStmt(CompoundStmt ast, Object o) {
		this.idTable.openScope();
		if ((o != null) && ((o instanceof FuncDecl))) {

			FuncDecl localFuncDecl = (FuncDecl) o;
			localFuncDecl.PL.visit(this, null);
			ast.DL.visit(this, null);
			ast.SL.visit(this, (Type) localFuncDecl.T.visit(this, null));
		} else {
			ast.DL.visit(this, null);
			ast.SL.visit(this, o);
		}
		this.idTable.closeScope();
		return null;
	}

	public Object visitStmtList(StmtList ast, Object o) {
		ast.S.visit(this, o);
		if (((ast.S instanceof ReturnStmt)) && ((ast.SL instanceof StmtList))) {
			this.reporter.reportError(this.errMesg[30], "", ast.SL.position);
		}
		ast.SL.visit(this, o);
		return null;
	}

	public Object visitForStmt(ForStmt ast, Object o) {
		this.whileLevel += 1;
		ast.E1.visit(this, null);
		Type localType = (Type) ast.E2.visit(this, null);
		if ((!ast.E2.isEmptyExpr())
				&& (!localType.equals(StdEnvironment.booleanType)))
			this.reporter.reportError(this.errMesg[21] + " (found: "
					+ localType.toString() + ")", "", ast.E2.position);
		ast.E3.visit(this, null);
		ast.S.visit(this, o);
		this.whileLevel -= 1;
		return null;
	}

	public Object visitWhileStmt(WhileStmt ast, Object o) {
		this.whileLevel += 1;
		Type localType = (Type) ast.E.visit(this, null);
		if (!localType.equals(StdEnvironment.booleanType))
			this.reporter.reportError(this.errMesg[22] + " (found: "
					+ localType.toString() + ")", "", ast.E.position);
		ast.S.visit(this, o);
		this.whileLevel -= 1;
		return null;
	}

	public Object visitBreakStmt(BreakStmt ast, Object o) {
		if (this.whileLevel < 1)
			this.reporter.reportError(this.errMesg[23], "", ast.position);
		return null;
	}

	public Object visitContinueStmt(ContinueStmt ast, Object o) {
		if (this.whileLevel < 1)
			this.reporter.reportError(this.errMesg[24], "", ast.position);
		return null;
	}

	public Object visitReturnStmt(ReturnStmt ast, Object o) {
		Type localType = (Type) o;
		ast.E.visit(this, o);
		ast.E = checkAssignment(localType, ast.E, this.errMesg[8], ast.position);
		return null;
	}

	public Object visitExprStmt(ExprStmt ast, Object o) {
		ast.E.visit(this, o);
		return null;
	}

	public Object visitEmptyCompStmt(EmptyCompStmt ast, Object o) {
		this.idTable.openScope();
		if ((o != null) && ((o instanceof FuncDecl))) {

			FuncDecl localFuncDecl = (FuncDecl) o;
			localFuncDecl.PL.visit(this, null);
		}
		this.idTable.closeScope();
		return null;
	}

	public Object visitEmptyStmt(EmptyStmt ast, Object o) {
		return null;
	}

	public Object visitEmptyStmtList(EmptyStmtList ast, Object o) {
		return null;
	}

	public Object visitAssignExpr(AssignExpr ast, Object o) {
		ast.E1.visit(this, o);
		ast.E2.visit(this, null);

		if ((!(ast.E1 instanceof VarExpr)) && (!(ast.E1 instanceof ArrayExpr))) {
			this.reporter.reportError(this.errMesg[7], "", ast.position);
		} else if ((ast.E1 instanceof VarExpr)) {
			SimpleVar localSimpleVar = (SimpleVar) ((VarExpr) ast.E1).V;
			Decl localDecl = (Decl) localSimpleVar.I.decl;
			if ((localDecl instanceof FuncDecl)) {
				this.reporter.reportError(this.errMesg[7] + ": %",
						localSimpleVar.I.spelling, ast.position);
			}
		}

		ast.E2 = checkAssignment(ast.E1.type, ast.E2, this.errMesg[6],
				ast.position);

		ast.type = ast.E2.type;
		return ast.type;
	}

	public Object visitBinaryExpr(BinaryExpr ast, Object o) {
		Type localType1 = (Type) ast.E1.visit(this, o);
		Type localType2 = (Type) ast.E2.visit(this, o);
		Type localType3 = localType1;
		String str = ast.O.spelling;
		int i = 0;

		int j = (str.equals("&&")) || (str.equals("||")) ? 1 : 0;
		int k = (str.equals("==")) || (str.equals("!=")) ? 1 : 0;
		int m = (str.equals("<=")) || (str.equals(">=")) || (str.equals("<"))
				|| (str.equals(">")) ? 1 : 0;

		if ((localType1.isErrorType()) || (localType2.isErrorType())) {
			localType3 = StdEnvironment.errorType;
		} else if ((localType1.isVoidType()) || (localType2.isVoidType())) {
			i = 1;
		} else if ((localType1.isStringType()) || (localType2.isStringType())) {
			i = 1;
		} else if ((localType1.isArrayType()) || (localType2.isArrayType())) {
			i = 1;
		} else if ((localType1.isBooleanType()) || (localType2.isBooleanType())) {
			if ((!localType1.equals(localType2)) || ((j == 0) && (k == 0)))
				i = 1;
			ast.O.spelling = ("i" + ast.O.spelling);
		} else if (j != 0) {
			i = 1;
		} else if (!localType1.equals(localType2)) {
			localType3 = StdEnvironment.floatType;
			ast.O.spelling = ("f" + ast.O.spelling);
			if (!localType3.equals(localType1)) {
				ast.E1 = i2f(ast.E1);
			} else {
				ast.E2 = i2f(ast.E2);
			}
		} else if (localType1.isFloatType()) {
			ast.O.spelling = ("f" + ast.O.spelling);
		} else {
			ast.O.spelling = ("i" + ast.O.spelling);
		}

		if (i != 0) {
			this.reporter.reportError(this.errMesg[9] + ": %", str,
					ast.position);
			localType3 = StdEnvironment.errorType;
		}

		ast.type = ((k != 0) || (m != 0) ? StdEnvironment.booleanType
				: localType3);
		return ast.type;
	}

	public Object visitUnaryExpr(UnaryExpr ast, Object o) {
		Type localType = (Type) ast.E.visit(this, o);
		String str = ast.O.spelling;
		int i = 0;

		if (localType.isErrorType()) {
			localType = StdEnvironment.errorType;
		} else if ((localType.isVoidType()) || (localType.isStringType())
				|| (localType.isArrayType())) {

			i = 1;
		} else if (((str.equals("!")) && (!localType.isBooleanType()))
				|| ((!str.equals("!")) && (localType.isBooleanType()))) {

			i = 1;
		}

		if (i != 0) {
			this.reporter.reportError(this.errMesg[10] + ": %", str,
					ast.position);
			localType = StdEnvironment.errorType;
		} else if (localType.isFloatType()) {
			ast.O.spelling = ("f" + ast.O.spelling);
		} else {
			ast.O.spelling = ("i" + ast.O.spelling);
		}

		ast.type = localType;
		return ast.type;
	}

	public Object visitCallExpr(CallExpr ast, Object o) {
		Decl localDecl = (Decl) ast.I.visit(this, null);
		if (localDecl == null) {
			this.reporter.reportError(this.errMesg[5] + ": %", ast.I.spelling,
					ast.position);
			ast.type = StdEnvironment.errorType;
		} else if ((localDecl instanceof FuncDecl)) {
			ast.AL.visit(this, ((FuncDecl) localDecl).PL);
			ast.type = ((FuncDecl) localDecl).T;
		} else {
			this.reporter.reportError(this.errMesg[19] + ": %", ast.I.spelling,
					ast.I.position);
			ast.type = StdEnvironment.errorType;
		}
		return ast.type;
	}

	public Object visitArrayExpr(ArrayExpr ast, Object o) {
		Type localType1 = (Type) ast.V.visit(this, o);
		if (localType1.isArrayType()) {
			localType1 = ((ArrayType) localType1).T;
		} else if (!localType1.isErrorType()) {
			this.reporter.reportError(this.errMesg[12], "", ast.position);
			localType1 = StdEnvironment.errorType;
		}

		Type localType2 = (Type) ast.E.visit(this, o);
		if ((!localType2.isIntType()) && (!localType2.isErrorType())) {
			this.reporter.reportError(this.errMesg[17], "", ast.position);
		}
		ast.type = localType1;
		return localType1;
	}

	public Object visitInitExpr(InitExpr ast, Object o) {
		Type localType = (Type) o;
		if (!localType.isArrayType()) {
			this.reporter.reportError(this.errMesg[14], " ", ast.position);
			ast.type = StdEnvironment.errorType;
			return ast.type;
		}
		return ast.IL.visit(this, ((ArrayType) localType).T);
	}

	public Object visitExprList(ExprList ast, Object o) {
		Type localType = (Type) o;
		ast.E.visit(this, o);
		ast.E = checkAssignment(localType, ast.E, this.errMesg[13]
				+ ": at position " + ast.index, ast.E.position);

		if ((ast.EL instanceof ExprList)) {
			((ExprList) ast.EL).index = (ast.index + 1);
			return ast.EL.visit(this, o);
		}
		return new Integer(ast.index + 1);
	}

	public Object visitEmptyExprList(EmptyExprList ast, Object o) {
		return null;
	}

	public Object visitEmptyExpr(EmptyExpr ast, Object o) {
		if ((ast.parent instanceof ReturnStmt)) {
			ast.type = StdEnvironment.voidType;
		} else
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

	public Object visitVarExpr(VarExpr ast, Object o) {
		ast.type = ((Type) ast.V.visit(this, null));
		return ast.type;
	}

	public Object visitStringExpr(StringExpr ast, Object o) {
		ast.type = StdEnvironment.stringType;
		return ast.type;
	}

	public Object visitFuncDecl(FuncDecl ast, Object o) {
		declareFunction(ast.I, ast);

		if ((ast.S.isEmptyCompStmt())
				&& (!ast.T.equals(StdEnvironment.voidType))) {
			this.reporter.reportError(this.errMesg[31], "", ast.position);
		}

		ast.S.visit(this, ast);

		return null;
	}

	public Object visitDeclList(DeclList ast, Object o) {
		ast.D.visit(this, null);
		ast.DL.visit(this, null);
		return null;
	}

	public Object visitEmptyDeclList(EmptyDeclList ast, Object o) {
		return null;
	}

	public Object visitGlobalVarDecl(GlobalVarDecl ast, Object o) {
		declareVariable(ast.I, ast);

		if (ast.T.isVoidType()) {
			this.reporter.reportError(this.errMesg[3] + ": %", ast.I.spelling,
					ast.I.position);
		} else if (ast.T.isArrayType()) {
			if (((ArrayType) ast.T).T.isVoidType())
				this.reporter.reportError(this.errMesg[4] + ": %",
						ast.I.spelling, ast.I.position);
			if ((((ArrayType) ast.T).E.isEmptyExpr())
					&& (!(ast.E instanceof InitExpr))) {
				this.reporter.reportError(this.errMesg[18] + ": %",
						ast.I.spelling, ast.I.position);
			}
		}
		Object localObject = ast.E.visit(this, ast.T);

		if (ast.T.isArrayType()) {
			if ((ast.E instanceof InitExpr)) {
				Integer localInteger = (Integer) localObject;
				ArrayType localArrayType = (ArrayType) ast.T;
				if (localArrayType.E.isEmptyExpr()) {
					localArrayType.E = new IntExpr(new IntLiteral(
							localInteger.toString(), dummyPos), dummyPos);
				} else {
					int i = Integer
							.parseInt(((IntExpr) localArrayType.E).IL.spelling);
					int j = localInteger.intValue();
					if (i < j)
						this.reporter.reportError(this.errMesg[16] + ": %",
								ast.I.spelling, ast.position);
				}
			} else if (!ast.E.isEmptyExpr()) {
				this.reporter.reportError(this.errMesg[15] + ": %",
						ast.I.spelling, ast.position);
			}
		} else {
			ast.E = checkAssignment(ast.T, ast.E, this.errMesg[6], ast.position);
		}
		return null;
	}

	public Object visitLocalVarDecl(LocalVarDecl ast, Object o) {
		declareVariable(ast.I, ast);

		if (ast.T.isVoidType()) {
			this.reporter.reportError(this.errMesg[3] + ": %", ast.I.spelling,
					ast.I.position);
		} else if (ast.T.isArrayType()) {
			if (((ArrayType) ast.T).T.isVoidType())
				this.reporter.reportError(this.errMesg[4] + ": %",
						ast.I.spelling, ast.I.position);
			if ((((ArrayType) ast.T).E.isEmptyExpr())
					&& (!(ast.E instanceof InitExpr))) {
				this.reporter.reportError(this.errMesg[18] + ": %",
						ast.I.spelling, ast.I.position);
			}
		}
		Object eAST = ast.E.visit(this, ast.T);

		if (ast.T.isArrayType()) {
			if ((ast.E instanceof InitExpr)) {
				Integer localInteger = (Integer) eAST;
				ArrayType tAST = (ArrayType) ast.T;
				if (tAST.E.isEmptyExpr()) {
					tAST.E = new IntExpr(new IntLiteral(
							localInteger.toString(), dummyPos), dummyPos);
				} else {
					int i = Integer
							.parseInt(((IntExpr) tAST.E).IL.spelling);
					int j = localInteger.intValue();
					if (i < j)
						this.reporter.reportError(this.errMesg[16] + ": %",
								ast.I.spelling, ast.position);
				}
			} else if (!ast.E.isEmptyExpr()) {
				this.reporter.reportError(this.errMesg[15] + ": %",
						ast.I.spelling, ast.position);
			}
		} else {
			ast.E = checkAssignment(ast.T, ast.E, this.errMesg[6], ast.position);
		}
		return null;
	}

	public Object visitParaList(ParaList ast, Object o) {
		ast.P.visit(this, null);
		ast.PL.visit(this, null);
		return null;
	}

	public Object visitParaDecl(ParaDecl ast, Object o) {
		declareVariable(ast.I, ast);

		if (ast.T.isVoidType()) {
			this.reporter.reportError(this.errMesg[3] + ": %", ast.I.spelling,
					ast.I.position);
		} else if ((ast.T.isArrayType())
				&& (((ArrayType) ast.T).T.isVoidType())) {
			this.reporter.reportError(this.errMesg[4] + ": %", ast.I.spelling,
					ast.I.position);
		}
		return null;
	}

	public Object visitEmptyParaList(EmptyParaList ast, Object o) {
		return null;
	}

	public Object visitEmptyArgList(EmptyArgList ast, Object o) {
		List localList = (List) o;
		if (!localList.isEmptyParaList())
			this.reporter.reportError(this.errMesg[26], "", ast.position);
		return null;
	}

	public Object visitArgList(ArgList ast, Object o) {
		List localList = (List) o;

		if (localList.isEmptyParaList()) {
			this.reporter.reportError(this.errMesg[25], "", ast.position);
		} else {
			ast.A.visit(this, ((ParaList) localList).P);
			ast.AL.visit(this, ((ParaList) localList).PL);
		}
		return null;
	}

	public Object visitArg(Arg ast, Object o) {
		ParaDecl localParaDecl = (ParaDecl) o;
		Type localType1 = (Type) ast.E.visit(this, null);

		int i = 0;

		Type localType2 = localParaDecl.T;
		if (localType2.isArrayType()) {
			if (!localType1.isArrayType()) {
				i = 1;
			} else {
				Type localType3 = ((ArrayType) localType2).T;
				Type localType4 = ((ArrayType) localType1).T;
				if (!localType3.assignable(localType4))
					i = 1;
			}
		} else if (!localParaDecl.T.assignable(localType1)) {
			i = 1;
		}

		if (i != 0)
			this.reporter.reportError(this.errMesg[27] + ": %",
					localParaDecl.I.spelling, ast.E.position);
		if ((localParaDecl.T.equals(StdEnvironment.floatType))
				&& (localType1.equals(StdEnvironment.intType))) {
			ast.E = i2f(ast.E);
		}
		return null;
	}

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

	public Object visitArrayType(ArrayType ast, Object o) {
		return ast;
	}

	public Object visitIdent(Ident ast, Object o) {
		Decl localDecl = this.idTable.retrieve(ast.spelling);
		if (localDecl != null)
			ast.decl = localDecl;
		return localDecl;
	}

	public Object visitBooleanLiteral(BooleanLiteral ast, Object o) {
		return StdEnvironment.booleanType;
	}

	public Object visitIntLiteral(IntLiteral ast, Object o) {
		return StdEnvironment.intType;
	}

	public Object visitFloatLiteral(FloatLiteral ast, Object o) {
		return StdEnvironment.floatType;
	}

	public Object visitStringLiteral(StringLiteral ast, Object o) {
		return StdEnvironment.stringType;
	}

	public Object visitOperator(Operator ast, Object o) {
		return null;
	}

	public Object visitSimpleVar(SimpleVar ast, Object o) {
		ast.type = StdEnvironment.errorType;
		Decl localDecl = (Decl) ast.I.visit(this, null);
		if (localDecl == null) {
			this.reporter.reportError(this.errMesg[5] + ": %", ast.I.spelling,
					ast.position);
		} else if ((localDecl instanceof FuncDecl)) {
			this.reporter.reportError(this.errMesg[11] + ": %", ast.I.spelling,
					ast.I.position);
		} else {
			ast.type = localDecl.T;
		}

		if ((ast.type.isArrayType()) && ((ast.parent instanceof VarExpr))
				&& (!(ast.parent.parent instanceof Arg))) {
			this.reporter.reportError(this.errMesg[11] + ": %", ast.I.spelling,
					ast.I.position);
		}

		return ast.type;
	}

	private FuncDecl declareStdFunc(Type paramType, String paramString,
			List paramList) {
		FuncDecl localFuncDecl = new FuncDecl(paramType, new Ident(paramString,
				dummyPos), paramList, new EmptyStmt(dummyPos), dummyPos);

		this.idTable.insert(paramString, localFuncDecl);
		return localFuncDecl;
	}

	private static final Ident dummyI = new Ident("x", dummyPos);

	private void establishStdEnvironment() {
		StdEnvironment.booleanType = new BooleanType(dummyPos);
		StdEnvironment.intType = new IntType(dummyPos);
		StdEnvironment.floatType = new FloatType(dummyPos);
		StdEnvironment.stringType = new StringType(dummyPos);
		StdEnvironment.voidType = new VoidType(dummyPos);
		StdEnvironment.errorType = new ErrorType(dummyPos);

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