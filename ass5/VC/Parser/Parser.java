package VC.Parser;

import VC.Scanner.Scanner;
import VC.Scanner.SourcePosition;
import VC.Scanner.Token;
import VC.ErrorReporter;
import VC.ASTs.*;

public class Parser {
	private VC.Scanner.Scanner scanner;
	private VC.ErrorReporter errorReporter;
	private Token currentToken;
	private SourcePosition previousTokenPosition;

	public class TypeAndIdent {
		VC.ASTs.Type tAST;
		VC.ASTs.Ident iAST;

		public TypeAndIdent(VC.ASTs.Type paramType, VC.ASTs.Ident paramIdent) {
			this.tAST = paramType;
			this.iAST = paramIdent;
		}
	}

	private SourcePosition dummyPos = new SourcePosition();

	public Parser(VC.Scanner.Scanner paramScanner,
			VC.ErrorReporter paramErrorReporter) {
		this.scanner = paramScanner;
		this.errorReporter = paramErrorReporter;

		this.previousTokenPosition = new SourcePosition();

		this.currentToken = this.scanner.getToken();
	}

	void match(int paramInt) throws SyntaxError {
		if (this.currentToken.kind == paramInt) {
			this.previousTokenPosition = this.currentToken.position;
			this.currentToken = this.scanner.getToken();
		} else {
			syntacticError("\"%\" expected here", Token.spell(paramInt));
		}
	}

	void accept() {
		this.previousTokenPosition = this.currentToken.position;
		this.currentToken = this.scanner.getToken();
	}

	void syntacticError(String paramString1, String paramString2)
			throws SyntaxError {
		SourcePosition localSourcePosition = this.currentToken.position;
		this.errorReporter.reportError(paramString1, paramString2,
				localSourcePosition);
		throw new SyntaxError();
	}

	void start(SourcePosition paramSourcePosition) {
		paramSourcePosition.lineStart = this.currentToken.position.lineStart;
		paramSourcePosition.charStart = this.currentToken.position.charStart;
	}

	void finish(SourcePosition paramSourcePosition) {
		paramSourcePosition.lineFinish = this.previousTokenPosition.lineFinish;
		paramSourcePosition.charFinish = this.previousTokenPosition.charFinish;
	}

	void copyStart(SourcePosition paramSourcePosition1,
			SourcePosition paramSourcePosition2) {
		paramSourcePosition2.lineStart = paramSourcePosition1.lineStart;
		paramSourcePosition2.charStart = paramSourcePosition1.charStart;
	}

	VC.ASTs.Type cloneType(VC.ASTs.Type paramType) {
		SourcePosition localSourcePosition = paramType.position;
		Type localObject;
		if ((paramType instanceof VC.ASTs.IntType)) {
			localObject = new VC.ASTs.IntType(localSourcePosition);
		} else if ((paramType instanceof VC.ASTs.FloatType)) {
			localObject = new VC.ASTs.FloatType(localSourcePosition);
		} else if ((paramType instanceof VC.ASTs.BooleanType)) {
			localObject = new VC.ASTs.BooleanType(localSourcePosition);
		} else
			localObject = new VC.ASTs.VoidType(localSourcePosition);
		return localObject;
	}

	public VC.ASTs.Program parseProgram() {
		VC.ASTs.Program localProgram = null;

		SourcePosition localSourcePosition = new SourcePosition();
		start(localSourcePosition);
		try {
			VC.ASTs.List localList = parseDeclList();
			finish(localSourcePosition);
			localProgram = new VC.ASTs.Program(localList, localSourcePosition);
			if (this.currentToken.kind != 39) {
				syntacticError("\"%\" unknown type", this.currentToken.spelling);
			}
		} catch (SyntaxError localSyntaxError) {
			return null;
		}
		return localProgram;
	}

	VC.ASTs.List parseDeclList() throws SyntaxError {
		VC.ASTs.Decl localDecl = null;
		List localObject1 = null;
		List localObject2 = null;

		SourcePosition localSourcePosition = new SourcePosition();
		start(localSourcePosition);
		Object localObject3;
		if ((this.currentToken.kind == 9) || (this.currentToken.kind == 0)
				|| (this.currentToken.kind == 7)
				|| (this.currentToken.kind == 4)) {

			localObject3 = parseType();
			VC.ASTs.Ident localIdent = parseIdent();
			if (this.currentToken.kind == 27) {
				localDecl = parseRestFuncDecl((VC.ASTs.Type) localObject3,
						localIdent);
			} else {
				localObject1 = parseRestVarDecl((VC.ASTs.Type) localObject3,
						localIdent, true);
			}
		}
		if ((this.currentToken.kind == 9) || (this.currentToken.kind == 0)
				|| (this.currentToken.kind == 7)
				|| (this.currentToken.kind == 4)) {

			localObject2 = parseDeclList();
		} else {
			localObject2 = new VC.ASTs.EmptyDeclList(this.dummyPos);
		}
		if (localDecl != null) {
			finish(localSourcePosition);
			localObject1 = new VC.ASTs.DeclList(localDecl,
					(VC.ASTs.List) localObject2, localSourcePosition);
		} else if (localObject1 != null) {
			localObject3 = (VC.ASTs.DeclList) localObject1;
			while (!(((VC.ASTs.DeclList) localObject3).DL instanceof VC.ASTs.EmptyDeclList))
				localObject3 = (VC.ASTs.DeclList) ((VC.ASTs.DeclList) localObject3).DL;
			if (!(localObject2 instanceof VC.ASTs.EmptyDeclList))
				((VC.ASTs.DeclList) localObject3).DL = ((VC.ASTs.DeclList) localObject2);
		} else {
			localObject1 = localObject2;
		}
		return localObject1;
	}

	VC.ASTs.Decl parseRestFuncDecl(VC.ASTs.Type paramType,
			VC.ASTs.Ident paramIdent) throws SyntaxError {
		VC.ASTs.FuncDecl localFuncDecl = null;

		SourcePosition localSourcePosition = new SourcePosition();
		localSourcePosition = paramType.position;

		VC.ASTs.List localList = parseParaList();
		VC.ASTs.Stmt localStmt = parseCompoundStmt();
		finish(localSourcePosition);
		localFuncDecl = new VC.ASTs.FuncDecl(paramType, paramIdent, localList,
				localStmt, localSourcePosition);
		return localFuncDecl;
	}

	VC.ASTs.List parseRestVarDecl(VC.ASTs.Type paramType,
			VC.ASTs.Ident paramIdent, boolean paramBoolean) throws SyntaxError {
		List localObject1 = null;
		Object localObject2 = null;

		SourcePosition localSourcePosition1 = new SourcePosition();
		copyStart(paramType.position, localSourcePosition1);
		Object localObject3;
		if (this.currentToken.kind == 29) {
			accept();
			if (this.currentToken.kind == 34) {
				localObject3 = parseExpr();
			} else
				localObject3 = new VC.ASTs.EmptyExpr(this.dummyPos);
			match(30);
			finish(localSourcePosition1);
			paramType = new VC.ASTs.ArrayType(paramType, (Expr) localObject3,
					localSourcePosition1);
		}

		if (this.currentToken.kind == 17) {
			accept();
			localObject3 = parseInitialiser();
		} else {
			localObject3 = new VC.ASTs.EmptyExpr(this.dummyPos);
		}
		SourcePosition localSourcePosition2 = new SourcePosition();
		copyStart(paramIdent.position, localSourcePosition2);
		finish(localSourcePosition2);
		if (paramBoolean) {
			localObject2 = new VC.ASTs.GlobalVarDecl(paramType, paramIdent,
					(Expr) localObject3, localSourcePosition2);
		} else {
			localObject2 = new VC.ASTs.LocalVarDecl(paramType, paramIdent,
					(Expr) localObject3, localSourcePosition2);
		}
		SourcePosition localSourcePosition3 = new SourcePosition();
		copyStart(paramIdent.position, localSourcePosition3);

		if (this.currentToken.kind == 32) {
			accept();
			if ((paramType instanceof VC.ASTs.ArrayType))
				paramType = ((VC.ASTs.ArrayType) paramType).T;
			localObject1 = parseInitDeclaratorList(paramType, paramBoolean);
			finish(localSourcePosition3);
			localObject1 = new VC.ASTs.DeclList((VC.ASTs.Decl) localObject2,
					(VC.ASTs.List) localObject1, localSourcePosition3);
		} else {
			finish(localSourcePosition3);
			localObject1 = new VC.ASTs.DeclList((VC.ASTs.Decl) localObject2,
					new VC.ASTs.EmptyDeclList(this.dummyPos),
					localSourcePosition3);
		}

		match(31);

		return localObject1;
	}

	VC.ASTs.List parseVarDecl() throws SyntaxError {
		VC.ASTs.List localList = null;

		VC.ASTs.Type localType = parseType();
		localList = parseInitDeclaratorList(localType, false);
		match(31);

		return localList;
	}

	VC.ASTs.List parseInitDeclaratorList(VC.ASTs.Type paramType,
			boolean paramBoolean) throws SyntaxError {
		List localObject = null;

		SourcePosition localSourcePosition = new SourcePosition();
		start(localSourcePosition);

		paramType = cloneType(paramType);

		VC.ASTs.Decl localDecl = parseInitDeclarator(paramType, paramBoolean);
		if (this.currentToken.kind == 32) {
			accept();
			localObject = parseInitDeclaratorList(paramType, paramBoolean);
			finish(localSourcePosition);
			localObject = new VC.ASTs.DeclList(localDecl,
					(VC.ASTs.List) localObject, localSourcePosition);
		} else {
			finish(localSourcePosition);
			localObject = new VC.ASTs.DeclList(localDecl,
					new VC.ASTs.EmptyDeclList(this.dummyPos),
					localSourcePosition);
		}

		return localObject;
	}

	VC.ASTs.Decl parseInitDeclarator(VC.ASTs.Type paramType,
			boolean paramBoolean) throws SyntaxError {
		Decl localObject1 = null;

		SourcePosition localSourcePosition = new SourcePosition();
		start(localSourcePosition);

		TypeAndIdent localTypeAndIdent = parseDeclarator(paramType);
		Object localObject2 = null;
		if (this.currentToken.kind == 17) {
			accept();
			localObject2 = parseInitialiser();
		} else {
			localObject2 = new VC.ASTs.EmptyExpr(this.dummyPos);
		}
		finish(localSourcePosition);
		if (paramBoolean) {
			localObject1 = new VC.ASTs.GlobalVarDecl(localTypeAndIdent.tAST,
					localTypeAndIdent.iAST, (Expr) localObject2,
					localSourcePosition);
		} else {
			localObject1 = new VC.ASTs.LocalVarDecl(localTypeAndIdent.tAST,
					localTypeAndIdent.iAST, (Expr) localObject2,
					localSourcePosition);
		}
		return localObject1;
	}

	TypeAndIdent parseDeclarator(VC.ASTs.Type paramType) throws SyntaxError {
		Object localObject = null;

		SourcePosition localSourcePosition1 = new SourcePosition();
		copyStart(paramType.position, localSourcePosition1);

		VC.ASTs.Ident localIdent = parseIdent();
		if (this.currentToken.kind == 29) {
			accept();
			if (this.currentToken.kind == 34) {
				SourcePosition localSourcePosition2 = new SourcePosition();
				start(localSourcePosition2);
				VC.ASTs.IntLiteral localIntLiteral = parseIntLiteral();
				finish(localSourcePosition2);
				localObject = new VC.ASTs.IntExpr(localIntLiteral,
						localSourcePosition2);
			} else {
				localObject = new VC.ASTs.EmptyExpr(this.dummyPos);
			}
			match(30);
			finish(localSourcePosition1);
			paramType = new VC.ASTs.ArrayType(paramType, (Expr) localObject,
					localSourcePosition1);
		}

		return new TypeAndIdent(paramType, localIdent);
	}

	VC.ASTs.List parseInitExpr() throws SyntaxError {
		SourcePosition localSourcePosition = new SourcePosition();
		start(localSourcePosition);

		List localObject = null;

		Expr localExpr = parseExpr();
		if (this.currentToken.kind == 32) {
			accept();
			localObject = parseInitExpr();
			finish(localSourcePosition);
			localObject = new VC.ASTs.ExprList(localExpr,
					(VC.ASTs.List) localObject, localSourcePosition);
		} else {
			finish(localSourcePosition);
			localObject = new VC.ASTs.ExprList(localExpr,
					new VC.ASTs.EmptyExprList(this.dummyPos),
					localSourcePosition);
		}
		return localObject;
	}

	Expr parseInitialiser() throws SyntaxError {
		Expr localObject = null;

		SourcePosition localSourcePosition = new SourcePosition();
		start(localSourcePosition);

		if (this.currentToken.kind == 25) {
			accept();
			VC.ASTs.List localList = parseInitExpr();
			match(26);
			finish(localSourcePosition);
			localObject = new VC.ASTs.InitExpr(localList, localSourcePosition);
		} else {
			localObject = parseExpr();
		}
		return localObject;
	}

	VC.ASTs.Type parseType() throws SyntaxError {
		Type localObject = null;

		SourcePosition localSourcePosition = new SourcePosition();
		start(localSourcePosition);

		if (this.currentToken.kind == 9) {
			accept();
			finish(localSourcePosition);
			localObject = new VC.ASTs.VoidType(localSourcePosition);
		} else if (this.currentToken.kind == 0) {
			accept();
			finish(localSourcePosition);
			localObject = new VC.ASTs.BooleanType(localSourcePosition);
		} else if (this.currentToken.kind == 7) {
			accept();
			finish(localSourcePosition);
			localObject = new VC.ASTs.IntType(localSourcePosition);
		} else if (this.currentToken.kind == 4) {
			accept();
			finish(localSourcePosition);
			localObject = new VC.ASTs.FloatType(localSourcePosition);
		} else {
			syntacticError(
					"\"%\" illegal type (must be one of void, int, float and boolean)",
					this.currentToken.spelling);
		}
		return localObject;
	}

	VC.ASTs.Stmt parseCompoundStmt() throws SyntaxError {
		Stmt localObject = null;

		SourcePosition localSourcePosition = new SourcePosition();
		start(localSourcePosition);

		match(25);
		VC.ASTs.List localList1 = parseDeclStmtList();
		VC.ASTs.List localList2 = parseStmtList();
		match(26);
		finish(localSourcePosition);
		if (((localList1 instanceof VC.ASTs.EmptyDeclList))
				&& ((localList2 instanceof VC.ASTs.EmptyStmtList))) {
			localObject = new VC.ASTs.EmptyCompStmt(localSourcePosition);
		} else {
			finish(localSourcePosition);
			localObject = new VC.ASTs.CompoundStmt(localList1, localList2,
					localSourcePosition);
		}
		return localObject;
	}

	VC.ASTs.List parseDeclStmtList() throws SyntaxError {
		List localObject = null;

		SourcePosition localSourcePosition = new SourcePosition();
		start(localSourcePosition);

		if ((this.currentToken.kind == 7) || (this.currentToken.kind == 4)
				|| (this.currentToken.kind == 0)
				|| (this.currentToken.kind == 9)) {

			localObject = parseVarDecl();
		}

		while ((this.currentToken.kind == 7) || (this.currentToken.kind == 4)
				|| (this.currentToken.kind == 0)
				|| (this.currentToken.kind == 9)) {
			VC.ASTs.List localList = parseVarDecl();
			VC.ASTs.DeclList localDeclList = (VC.ASTs.DeclList) localObject;
			while (!(localDeclList.DL instanceof VC.ASTs.EmptyDeclList))
				localDeclList = (VC.ASTs.DeclList) localDeclList.DL;
			localDeclList.DL = ((VC.ASTs.DeclList) localList);
		}

		if (localObject == null) {
			localObject = new VC.ASTs.EmptyDeclList(this.dummyPos);
		}
		return localObject;
	}

	VC.ASTs.List parseStmtList() throws SyntaxError {
		List localObject = null;

		SourcePosition localSourcePosition = new SourcePosition();
		start(localSourcePosition);

		if (this.currentToken.kind != 26) {
			VC.ASTs.Stmt localStmt = parseStmt();

			if (this.currentToken.kind != 26) {
				localObject = parseStmtList();
				finish(localSourcePosition);
				localObject = new VC.ASTs.StmtList(localStmt,
						(VC.ASTs.List) localObject, localSourcePosition);
			} else {
				finish(localSourcePosition);
				localObject = new VC.ASTs.StmtList(localStmt,
						new VC.ASTs.EmptyStmtList(this.dummyPos),
						localSourcePosition);
			}
		} else {
			localObject = new VC.ASTs.EmptyStmtList(this.dummyPos);
		}
		return localObject;
	}

	VC.ASTs.Stmt parseStmt() throws SyntaxError {
		VC.ASTs.Stmt localStmt = null;

		switch (this.currentToken.kind) {
		case 25:
			localStmt = parseCompoundStmt();
			break;

		case 6:
			localStmt = parseIfStmt();
			break;

		case 5:
			localStmt = parseForStmt();
			break;

		case 10:
			localStmt = parseWhileStmt();
			break;

		case 1:
			localStmt = parseBreakStmt();
			break;

		case 2:
			localStmt = parseContinueStmt();
			break;

		case 8:
			localStmt = parseReturnStmt();
			break;
		case 3:
		case 4:
		case 7:
		case 9:
		case 11:
		case 12:
		case 13:
		case 14:
		case 15:
		case 16:
		case 17:
		case 18:
		case 19:
		case 20:
		case 21:
		case 22:
		case 23:
		case 24:
		default:
			localStmt = parseExprStmt();
		}

		return localStmt;
	}

	VC.ASTs.Stmt parseIfStmt() throws SyntaxError {
		VC.ASTs.IfStmt localIfStmt = null;

		SourcePosition localSourcePosition = new SourcePosition();
		start(localSourcePosition);

		match(6);
		match(27);
		Expr localExpr = parseExpr();
		match(28);
		VC.ASTs.Stmt localStmt1 = parseStmt();
		if (this.currentToken.kind == 3) {
			accept();
			VC.ASTs.Stmt localStmt2 = parseStmt();
			finish(localSourcePosition);
			localIfStmt = new VC.ASTs.IfStmt(localExpr, localStmt1, localStmt2,
					localSourcePosition);
		} else {
			finish(localSourcePosition);
			localIfStmt = new VC.ASTs.IfStmt(localExpr, localStmt1,
					localSourcePosition);
		}
		return localIfStmt;
	}

	VC.ASTs.Stmt parseForStmt() throws SyntaxError {
		Stmt localObject1 = null;
		Object localObject2 = null;
		Object localObject3 = null;
		Object localObject4 = null;

		SourcePosition localSourcePosition = new SourcePosition();
		start(localSourcePosition);

		match(5);
		match(27);
		if (this.currentToken.kind != 31) {
			localObject2 = parseExpr();
		} else
			localObject2 = new VC.ASTs.EmptyExpr(this.dummyPos);
		match(31);
		if (this.currentToken.kind != 31) {
			localObject3 = parseExpr();
		} else
			localObject3 = new VC.ASTs.EmptyExpr(this.dummyPos);
		match(31);
		if (this.currentToken.kind != 28) {
			localObject4 = parseExpr();
		} else
			localObject4 = new VC.ASTs.EmptyExpr(this.dummyPos);
		match(28);
		localObject1 = parseStmt();
		finish(localSourcePosition);
		localObject1 = new VC.ASTs.ForStmt((Expr) localObject2,
				(Expr) localObject3, (Expr) localObject4,
				(VC.ASTs.Stmt) localObject1, localSourcePosition);
		return localObject1;
	}

	VC.ASTs.Stmt parseWhileStmt() throws SyntaxError {
		Stmt localObject = null;

		SourcePosition localSourcePosition = new SourcePosition();
		start(localSourcePosition);

		match(10);
		match(27);
		Expr localExpr = parseExpr();
		match(28);
		localObject = parseStmt();
		finish(localSourcePosition);
		localObject = new VC.ASTs.WhileStmt(localExpr,
				(VC.ASTs.Stmt) localObject, localSourcePosition);
		return localObject;
	}

	VC.ASTs.Stmt parseBreakStmt() throws SyntaxError {
		VC.ASTs.BreakStmt localBreakStmt = null;

		SourcePosition localSourcePosition = new SourcePosition();
		start(localSourcePosition);

		match(1);
		match(31);
		finish(localSourcePosition);

		localBreakStmt = new VC.ASTs.BreakStmt(localSourcePosition);
		return localBreakStmt;
	}

	VC.ASTs.Stmt parseContinueStmt() throws SyntaxError {
		VC.ASTs.ContinueStmt localContinueStmt = null;

		SourcePosition localSourcePosition = new SourcePosition();
		start(localSourcePosition);

		match(2);
		match(31);
		finish(localSourcePosition);

		localContinueStmt = new VC.ASTs.ContinueStmt(localSourcePosition);
		return localContinueStmt;
	}

	VC.ASTs.Stmt parseReturnStmt() throws SyntaxError {
		VC.ASTs.ReturnStmt localReturnStmt = null;

		SourcePosition localSourcePosition = new SourcePosition();
		start(localSourcePosition);

		match(8);
		Object localObject;
		if (this.currentToken.kind != 31) {
			localObject = parseExpr();
		} else
			localObject = new VC.ASTs.EmptyExpr(this.dummyPos);
		match(31);
		finish(localSourcePosition);
		localReturnStmt = new VC.ASTs.ReturnStmt((Expr) localObject,
				localSourcePosition);
		return localReturnStmt;
	}

	VC.ASTs.Stmt parseExprStmt() throws SyntaxError {
		VC.ASTs.ExprStmt localExprStmt = null;

		SourcePosition localSourcePosition = new SourcePosition();
		start(localSourcePosition);

		if ((this.currentToken.kind == 33) || (this.currentToken.kind == 15)
				|| (this.currentToken.kind == 11)
				|| (this.currentToken.kind == 12)
				|| (this.currentToken.kind == 15)
				|| (this.currentToken.kind == 34)
				|| (this.currentToken.kind == 35)
				|| (this.currentToken.kind == 36)
				|| (this.currentToken.kind == 37)
				|| (this.currentToken.kind == 27)) {

			Expr localExpr = parseExpr();
			match(31);
			finish(localSourcePosition);
			localExprStmt = new VC.ASTs.ExprStmt(localExpr, localSourcePosition);
		} else {
			match(31);
			finish(localSourcePosition);
			localExprStmt = new VC.ASTs.ExprStmt(new VC.ASTs.EmptyExpr(
					this.dummyPos), localSourcePosition);
		}
		return localExprStmt;
	}

	VC.ASTs.List parseParaList() throws SyntaxError {
		List localObject = null;

		SourcePosition localSourcePosition = new SourcePosition();
		start(localSourcePosition);

		match(27);

		if (this.currentToken.kind == 28) {
			accept();
			finish(localSourcePosition);
			localObject = new VC.ASTs.EmptyParaList(localSourcePosition);
		} else {
			localObject = parseProperParaList();
			match(28);
		}
		return localObject;
	}

	VC.ASTs.List parseProperParaList() throws SyntaxError {
		List localObject = null;

		SourcePosition localSourcePosition = new SourcePosition();
		start(localSourcePosition);

		VC.ASTs.ParaDecl localParaDecl = parseParaDecl();
		if (this.currentToken.kind == 32) {
			accept();
			localObject = parseProperParaList();
			finish(localSourcePosition);
			localObject = new VC.ASTs.ParaList(localParaDecl,
					(VC.ASTs.List) localObject, localSourcePosition);
		} else {
			finish(localSourcePosition);
			localObject = new VC.ASTs.ParaList(localParaDecl,
					new VC.ASTs.EmptyParaList(this.dummyPos),
					localSourcePosition);
		}
		return localObject;
	}

	VC.ASTs.ParaDecl parseParaDecl() throws SyntaxError {
		VC.ASTs.ParaDecl localParaDecl = null;

		VC.ASTs.Type localType = parseType();

		SourcePosition localSourcePosition = new SourcePosition();
		start(localSourcePosition);

		TypeAndIdent localTypeAndIdent = parseDeclarator(localType);

		finish(localSourcePosition);
		localParaDecl = new VC.ASTs.ParaDecl(localTypeAndIdent.tAST,
				localTypeAndIdent.iAST, localSourcePosition);

		return localParaDecl;
	}

	VC.ASTs.List parseArgList() throws SyntaxError {
		List localObject = null;

		SourcePosition localSourcePosition = new SourcePosition();
		start(localSourcePosition);

		match(27);
		if (this.currentToken.kind == 28) {
			match(28);
			finish(localSourcePosition);
			localObject = new VC.ASTs.EmptyArgList(localSourcePosition);
		} else {
			localObject = parseProperArgList();
			match(28);
		}

		return localObject;
	}

	VC.ASTs.List parseProperArgList() throws SyntaxError {
		List localObject = null;

		SourcePosition localSourcePosition = new SourcePosition();
		start(localSourcePosition);

		VC.ASTs.Arg localArg = parseArg();
		if (this.currentToken.kind == 32) {
			accept();
			localObject = parseProperArgList();
			finish(localSourcePosition);
			localObject = new VC.ASTs.ArgList(localArg,
					(VC.ASTs.List) localObject, localSourcePosition);
		} else {
			finish(localSourcePosition);
			localObject = new VC.ASTs.ArgList(localArg,
					new VC.ASTs.EmptyArgList(localSourcePosition),
					localSourcePosition);
		}

		return localObject;
	}

	VC.ASTs.Arg parseArg() throws SyntaxError {
		VC.ASTs.Arg localArg = null;

		SourcePosition localSourcePosition = new SourcePosition();
		start(localSourcePosition);

		Expr localExpr = parseExpr();
		finish(localSourcePosition);
		localArg = new VC.ASTs.Arg(localExpr, localSourcePosition);
		return localArg;
	}

	VC.ASTs.Ident parseIdent() throws SyntaxError {
		VC.ASTs.Ident localIdent = null;

		if (this.currentToken.kind == 33) {
			String str = this.currentToken.spelling;
			accept();
			localIdent = new VC.ASTs.Ident(str, this.previousTokenPosition);
		} else {
			syntacticError("identifier expected here", "");
		}
		return localIdent;
	}

	VC.ASTs.Operator acceptOperator() throws SyntaxError {
		VC.ASTs.Operator localOperator = null;

		String str = this.currentToken.spelling;
		accept();
		localOperator = new VC.ASTs.Operator(str, this.previousTokenPosition);
		return localOperator;
	}

	public Expr parseExpr() throws SyntaxError {
		Expr localExpr = null;
		localExpr = parseAssignExpr();
		return localExpr;
	}

	public Expr parseAssignExpr() throws SyntaxError {
		Expr localObject = null;

		SourcePosition localSourcePosition = new SourcePosition();
		start(localSourcePosition);

		localObject = parseCondOrExpr();
		if (this.currentToken.kind == 17) {
			acceptOperator();
			Expr localExpr = parseAssignExpr();
			finish(localSourcePosition);
			localObject = new VC.ASTs.AssignExpr((Expr) localObject, localExpr,
					localSourcePosition);
		}

		return localObject;
	}

	Expr parseCondOrExpr() throws SyntaxError {
		Expr localObject = null;

		SourcePosition localSourcePosition1 = new SourcePosition();
		start(localSourcePosition1);

		localObject = parseCondAndExpr();
		while (this.currentToken.kind == 24) {
			VC.ASTs.Operator localOperator = acceptOperator();
			Expr localExpr = parseCondAndExpr();
			SourcePosition localSourcePosition2 = new SourcePosition();
			copyStart(localSourcePosition1, localSourcePosition2);
			finish(localSourcePosition2);
			localObject = new VC.ASTs.BinaryExpr((Expr) localObject,
					localOperator, localExpr, localSourcePosition2);
		}
		return localObject;
	}

	Expr parseCondAndExpr() throws SyntaxError {
		Expr localObject = null;

		SourcePosition localSourcePosition1 = new SourcePosition();
		start(localSourcePosition1);

		localObject = parseEqualityExpr();
		while (this.currentToken.kind == 23) {
			VC.ASTs.Operator localOperator = acceptOperator();
			Expr localExpr = parseEqualityExpr();
			SourcePosition localSourcePosition2 = new SourcePosition();
			copyStart(localSourcePosition1, localSourcePosition2);
			finish(localSourcePosition2);
			localObject = new VC.ASTs.BinaryExpr((Expr) localObject,
					localOperator, localExpr, localSourcePosition2);
		}
		return localObject;
	}

	Expr parseEqualityExpr() throws SyntaxError {
		Expr localObject = null;

		SourcePosition localSourcePosition1 = new SourcePosition();
		start(localSourcePosition1);

		localObject = parseRelExpr();

		while ((this.currentToken.kind == 18) || (this.currentToken.kind == 16)) {
			VC.ASTs.Operator localOperator = acceptOperator();
			Expr localExpr = parseRelExpr();
			SourcePosition localSourcePosition2 = new SourcePosition();
			copyStart(localSourcePosition1, localSourcePosition2);
			finish(localSourcePosition2);
			localObject = new VC.ASTs.BinaryExpr((Expr) localObject,
					localOperator, localExpr, localSourcePosition2);
		}
		return localObject;
	}

	Expr parseRelExpr() throws SyntaxError {
		Expr localObject = null;

		SourcePosition localSourcePosition1 = new SourcePosition();
		start(localSourcePosition1);

		localObject = parseAdditiveExpr();

		while ((this.currentToken.kind == 19) || (this.currentToken.kind == 20)
				|| (this.currentToken.kind == 21)
				|| (this.currentToken.kind == 22)) {
			VC.ASTs.Operator localOperator = acceptOperator();
			Expr localExpr = parseAdditiveExpr();
			SourcePosition localSourcePosition2 = new SourcePosition();
			copyStart(localSourcePosition1, localSourcePosition2);
			finish(localSourcePosition2);
			localObject = new VC.ASTs.BinaryExpr((Expr) localObject,
					localOperator, localExpr, localSourcePosition2);
		}
		return localObject;
	}

	Expr parseAdditiveExpr() throws SyntaxError {
		Expr localObject = null;

		SourcePosition localSourcePosition1 = new SourcePosition();
		start(localSourcePosition1);

		localObject = parseMultiplicativeExpr();

		while ((this.currentToken.kind == 11) || (this.currentToken.kind == 12)) {
			VC.ASTs.Operator localOperator = acceptOperator();
			Expr localExpr = parseMultiplicativeExpr();

			SourcePosition localSourcePosition2 = new SourcePosition();
			copyStart(localSourcePosition1, localSourcePosition2);
			finish(localSourcePosition2);
			localObject = new VC.ASTs.BinaryExpr((Expr) localObject,
					localOperator, localExpr, localSourcePosition2);
		}
		return localObject;
	}

	Expr parseMultiplicativeExpr() throws SyntaxError {
		Expr localObject = null;

		SourcePosition localSourcePosition1 = new SourcePosition();
		start(localSourcePosition1);

		localObject = parseUnaryExpr();

		while ((this.currentToken.kind == 13) || (this.currentToken.kind == 14)) {
			VC.ASTs.Operator localOperator = acceptOperator();
			Expr localExpr = parseUnaryExpr();
			SourcePosition localSourcePosition2 = new SourcePosition();
			copyStart(localSourcePosition1, localSourcePosition2);
			finish(localSourcePosition2);
			localObject = new VC.ASTs.BinaryExpr((Expr) localObject,
					localOperator, localExpr, localSourcePosition2);
		}
		return localObject;
	}

	Expr parseUnaryExpr() throws SyntaxError {
		Expr localObject = null;

		SourcePosition localSourcePosition = new SourcePosition();
		start(localSourcePosition);

		switch (this.currentToken.kind) {
		case 11:
		case 12:
		case 15:
			VC.ASTs.Operator localOperator = acceptOperator();
			Expr localExpr = parseUnaryExpr();
			finish(localSourcePosition);
			localObject = new VC.ASTs.UnaryExpr(localOperator, localExpr,
					localSourcePosition);

			break;
		case 13:
		case 14:
		default:
			localObject = parsePrimaryExpr();
		}

		return localObject;
	}

	Expr parsePrimaryExpr() throws SyntaxError {
		Expr localObject1 = null;

		SourcePosition localSourcePosition = new SourcePosition();
		start(localSourcePosition);
		Object localObject2;
		Object localObject3;
		switch (this.currentToken.kind) {
		case 33:
			VC.ASTs.Ident localIdent = parseIdent();
			if (this.currentToken.kind == 27) {
				localObject2 = parseArgList();
				finish(localSourcePosition);
				localObject1 = new VC.ASTs.CallExpr(localIdent,
						(VC.ASTs.List) localObject2, localSourcePosition);
			} else if (this.currentToken.kind == 29) {
				finish(localSourcePosition);
				localObject2 = new VC.ASTs.SimpleVar(localIdent,
						localSourcePosition);
				accept();
				localObject3 = parseExpr();
				finish(localSourcePosition);
				localObject1 = new VC.ASTs.ArrayExpr(
						(VC.ASTs.Var) localObject2, (Expr) localObject3,
						localSourcePosition);
				match(30);
			} else {
				finish(localSourcePosition);
				localObject2 = new VC.ASTs.SimpleVar(localIdent,
						localSourcePosition);
				localObject1 = new VC.ASTs.VarExpr((VC.ASTs.Var) localObject2,
						localSourcePosition);
			}
			break;

		case 27:
			accept();
			localObject1 = parseExpr();
			match(28);

			break;

		case 34:
			localObject2 = parseIntLiteral();
			finish(localSourcePosition);
			localObject1 = new VC.ASTs.IntExpr(
					(VC.ASTs.IntLiteral) localObject2, localSourcePosition);
			break;

		case 35:
			localObject3 = parseFloatLiteral();
			finish(localSourcePosition);
			localObject1 = new VC.ASTs.FloatExpr(
					(VC.ASTs.FloatLiteral) localObject3, localSourcePosition);
			break;

		case 36:
			VC.ASTs.BooleanLiteral localBooleanLiteral = parseBooleanLiteral();
			finish(localSourcePosition);
			localObject1 = new VC.ASTs.BooleanExpr(localBooleanLiteral,
					localSourcePosition);
			break;

		case 37:
			VC.ASTs.StringLiteral localStringLiteral = parseStringLiteral();
			finish(localSourcePosition);
			localObject1 = new VC.ASTs.StringExpr(localStringLiteral,
					localSourcePosition);
			break;
		case 28:
		case 29:
		case 30:
		case 31:
		case 32:
		default:
			syntacticError("illegal parimary expression",
					this.currentToken.spelling);
		}

		return localObject1;
	}

	VC.ASTs.IntLiteral parseIntLiteral() throws SyntaxError {
		VC.ASTs.IntLiteral localIntLiteral = null;

		if (this.currentToken.kind == 34) {
			String str = this.currentToken.spelling;
			accept();
			localIntLiteral = new VC.ASTs.IntLiteral(str,
					this.previousTokenPosition);
		} else {
			syntacticError("integer literal expected here", "");
		}
		return localIntLiteral;
	}

	VC.ASTs.FloatLiteral parseFloatLiteral() throws SyntaxError {
		VC.ASTs.FloatLiteral localFloatLiteral = null;

		if (this.currentToken.kind == 35) {
			String str = this.currentToken.spelling;
			accept();
			localFloatLiteral = new VC.ASTs.FloatLiteral(str,
					this.previousTokenPosition);
		} else {
			syntacticError("float literal expected here", "");
		}
		return localFloatLiteral;
	}

	VC.ASTs.BooleanLiteral parseBooleanLiteral() throws SyntaxError {
		VC.ASTs.BooleanLiteral localBooleanLiteral = null;

		if (this.currentToken.kind == 36) {
			String str = this.currentToken.spelling;
			accept();
			localBooleanLiteral = new VC.ASTs.BooleanLiteral(str,
					this.previousTokenPosition);
		} else {
			syntacticError("string literal expected here", "");
		}
		return localBooleanLiteral;
	}

	VC.ASTs.StringLiteral parseStringLiteral() throws SyntaxError {
		VC.ASTs.StringLiteral localStringLiteral = null;

		if (this.currentToken.kind == 37) {
			this.previousTokenPosition = this.currentToken.position;
			String str = this.currentToken.spelling;
			localStringLiteral = new VC.ASTs.StringLiteral(str,
					this.previousTokenPosition);
			this.currentToken = this.scanner.getToken();
		} else {
			syntacticError("string literal expected here", "");
		}
		return localStringLiteral;
	}
}

/*
 * Location: /home/andrew/study/git/vc/Sols Qualified Name: VC.Parser.Parser
 * Java Class Version: 5 (49.0) JD-Core Version: 0.7.0.1
 */

// /*
// * Parser.java
// *
// * This parser for a subset of the VC language is intended to
// * demonstrate how to create the AST nodes, including (among others):
// * [1] a list (of statements)
// * [2] a function
// * [3] a statement (which is an expression statement),
// * [4] a unary expression
// * [5] a binary expression
// * [6] terminals (identifiers, integer literals and operators)
// *
// * In addition, it also demonstrates how to use the two methods start
// * and finish to determine the position information for the start and
// * end of a construct (known as a phrase) corresponding an AST node.
// *
// * NOTE THAT THE POSITION INFORMATION WILL NOT BE MARKED. HOWEVER, IT CAN BE
// * USEFUL TO DEBUG YOUR IMPLEMENTATION.
// *
// * (07-April-2014)
//
//
// program -> func-decl
// func-decl -> type identifier "(" ")" compound-stmt
// type -> void
// identifier -> ID
// // statements
// compound-stmt -> "{" stmt* "}"
// stmt -> expr-stmt
// expr-stmt -> expr? ";"
// // expressions
// expr -> additive-expr
// additive-expr -> multiplicative-expr
// | additive-expr "+" multiplicative-expr
// | additive-expr "-" multiplicative-expr
// multiplicative-expr -> unary-expr
// | multiplicative-expr "*" unary-expr
// | multiplicative-expr "/" unary-expr
// unary-expr -> "-" unary-expr
// | primary-expr
//
// primary-expr -> identifier
// | INTLITERAL
// | "(" expr ")"
// */
//
// package VC.Parser;
//
// import VC.Scanner.Scanner;
// import VC.Scanner.SourcePosition;
// import VC.Scanner.Token;
// import VC.ErrorReporter;
// import VC.ASTs.*;
//
// public class Parser {
//
// private Scanner scanner;
// private ErrorReporter errorReporter;
// private Token currentToken;
// private SourcePosition previousTokenPosition;
// private SourcePosition dummyPos = new SourcePosition();
// private boolean flag1;
// private boolean flag2;
// private boolean flag3;
// private Token temptoken;
// private Type temptype;
//
// public Parser(Scanner lexer, ErrorReporter reporter) {
// scanner = lexer;
// errorReporter = reporter;
//
// previousTokenPosition = new SourcePosition();
//
// currentToken = scanner.getToken();
// flag1 = false;
// flag2 = false;
// flag3 = false;
// temptype = null;
// }
//
// // match checks to see f the current token matches tokenExpected.
// // If so, fetches the next token.
// // If not, reports a syntactic error.
//
// void match(int tokenExpected) throws SyntaxError {
// if (currentToken.kind == tokenExpected) {
// previousTokenPosition = currentToken.position;
// currentToken = scanner.getToken();
// } else {
// syntacticError("\"%\" expected here", Token.spell(tokenExpected));
// }
// }
//
// void accept() {
// previousTokenPosition = currentToken.position;
// currentToken = scanner.getToken();
// }
//
// void syntacticError(String messageTemplate, String tokenQuoted)
// throws SyntaxError {
// SourcePosition pos = currentToken.position;
// errorReporter.reportError(messageTemplate, tokenQuoted, pos);
// throw (new SyntaxError());
// }
//
// // start records the position of the start of a phrase.
// // This is defined to be the position of the first
// // character of the first token of the phrase.
//
// void start(SourcePosition position) {
// position.lineStart = currentToken.position.lineStart;
// position.charStart = currentToken.position.charStart;
// }
//
// // finish records the position of the end of a phrase.
// // This is defined to be the position of the last
// // character of the last token of the phrase.
//
// void finish(SourcePosition position) {
// position.lineFinish = previousTokenPosition.lineFinish;
// position.charFinish = previousTokenPosition.charFinish;
// }
//
// void copyStart(SourcePosition from, SourcePosition to) {
// to.lineStart = from.lineStart;
// to.charStart = from.charStart;
// }
//
// // ========================== PROGRAMS ========================
//
// public Program parseProgram() {
//
// Program programAST = null;
//
// SourcePosition programPos = new SourcePosition();
// start(programPos);
//
// try {
// List dlAST = parseFuncDeclList();
// finish(programPos);
// programAST = new Program(dlAST, programPos);
// if (currentToken.kind != Token.EOF) {
// syntacticError("\"%\" unknown type", currentToken.spelling);
// }
// } catch (SyntaxError s) {
// return null;
// }
// return programAST;
// }
//
// // ========================== DECLARATIONS ========================
//
// List parseFuncDeclList() throws SyntaxError {
// List dlAST = null;
// Decl dAST = null;
//
// SourcePosition funcPos = new SourcePosition();
// start(funcPos);
// if (currentToken.kind == Token.EOF) {
// finish(funcPos);
// return new EmptyDeclList(dummyPos);
// }
//
// dAST = parseFuncDecl();
//
// if (currentToken.kind == Token.VOID || currentToken.kind == Token.INT
// || currentToken.kind == Token.FLOAT
// || currentToken.kind == Token.BOOLEAN
// || (currentToken.kind == Token.COMMA && flag1 == true)) {
// dlAST = parseFuncDeclList();
// finish(funcPos);
// dlAST = new DeclList(dAST, dlAST, funcPos);
// } else if (dAST != null) {
// finish(funcPos);
// dlAST = new DeclList(dAST, new EmptyDeclList(dummyPos), funcPos);
// }
// if (dlAST == null)
// dlAST = new EmptyDeclList(dummyPos);
//
// return dlAST;
// }
//
// Decl parseFuncDecl() throws SyntaxError {// global variable included here
//
// Decl fAST = null;
// flag3 = true;
// SourcePosition funcPos = new SourcePosition();
// start(funcPos);
// if (currentToken.kind == Token.INT || currentToken.kind == Token.VOID
// || currentToken.kind == Token.FLOAT
// || currentToken.kind == Token.BOOLEAN) {
// Type tAST = parseType();
// temptype = tAST;
// // if(currentToken.kind != Token.VOID) {
// temptoken = scanner.getToken();
// flag2 = true;
// if (temptoken.kind == Token.LPAREN) {
// Ident iAST = parseIdent();
// List fplAST = parseParaList();
// Stmt cAST = parseCompoundStmt();
// finish(funcPos);
// fAST = new FuncDecl(tAST, iAST, fplAST, cAST, funcPos);
// } else {
// Ident iAST = parseIdent();
// Expr eAST = null;
// if (currentToken.kind == Token.LBRACKET)
// fAST = parseArray(tAST, iAST, eAST, funcPos);
//
// else if (currentToken.kind == Token.EQ) {
// accept();
// if (currentToken.kind == Token.LCURLY) {
// accept();
// eAST = parseInitExpr();
// match(Token.RCURLY);
// finish(funcPos);
// fAST = new GlobalVarDecl(tAST, iAST, eAST, funcPos);
// } else {
// eAST = parseExpr();
// finish(funcPos);
// fAST = new GlobalVarDecl(tAST, iAST, eAST, funcPos);
// }
// } else {
// finish(funcPos);
// fAST = new GlobalVarDecl(tAST, iAST,
// new EmptyExpr(dummyPos), funcPos);
// }
// if (currentToken.kind == Token.COMMA) {
// flag1 = true;
// } else {
// match(Token.SEMICOLON);
//
// }
//
// }
//
// // }
// /*
// * else { Ident iAST = parseIdent(); List fplAST = parseParaList();
// * Stmt cAST = parseCompoundStmt(); finish(funcPos); fAST = new
// * FuncDecl(tAST, iAST, fplAST, cAST, funcPos); }
// */
// } else if (currentToken.kind == Token.COMMA && flag1 == true) {
// accept();
// Type tAST = temptype;
// Ident iAST = parseIdent();
// Expr eAST = null;
// if (currentToken.kind == Token.LBRACKET)
// fAST = parseArray(tAST, iAST, eAST, funcPos);
// else if (currentToken.kind == Token.EQ) {
// accept();
// if (currentToken.kind == Token.LCURLY) {
// accept();
// eAST = parseInitExpr();
// match(Token.RCURLY);
// finish(funcPos);
// fAST = new GlobalVarDecl(tAST, iAST, eAST, funcPos);
// } else {
// eAST = parseExpr();
// finish(funcPos);
// fAST = new GlobalVarDecl(tAST, iAST, eAST, funcPos);
// }
// } else {
// finish(funcPos);
// fAST = new GlobalVarDecl(tAST, iAST, new EmptyExpr(dummyPos),
// funcPos);
// }
// if (currentToken.kind == Token.COMMA) {
// flag1 = true;
// } else {
// match(Token.SEMICOLON);
// flag1 = false;
// }
//
// } else
// syntacticError("type expected here", currentToken.spelling);
// flag3 = false;
// return fAST;
// }
//
// Decl parseArray(Type tAST, Ident iAST, Expr eAST, SourcePosition funcPos)
// throws SyntaxError {
// Decl fAST = null;
// accept();
// ArrayType atAST = null;
// Expr ieAST = null;
// IntLiteral ilAST = null;
// if (currentToken.kind == Token.INTLITERAL)
// ilAST = parseIntLiteral();
// match(Token.RBRACKET);
// if (ilAST == null)
// ieAST = new EmptyExpr(dummyPos);
// else
// ieAST = new IntExpr(ilAST, currentToken.position);
// atAST = new ArrayType(tAST, ieAST, tAST.position);
// if (currentToken.kind == Token.EQ) {
// accept();
// if (currentToken.kind == Token.LCURLY) {
// accept();
// eAST = parseInitExpr();
// match(Token.RCURLY);
// finish(funcPos);
// if (flag3 == true)
// fAST = new GlobalVarDecl(atAST, iAST, eAST, funcPos);
// else
// fAST = new LocalVarDecl(atAST, iAST, eAST, funcPos);
// } else {
// eAST = parseExpr();
// finish(funcPos);
// if (flag3 == true)
// fAST = new GlobalVarDecl(atAST, iAST, eAST, funcPos);
// else
// fAST = new LocalVarDecl(atAST, iAST, eAST, funcPos);
// }
// } else {
// finish(funcPos);
// if (flag3 == true)
// fAST = new GlobalVarDecl(atAST, iAST, new EmptyExpr(dummyPos),
// funcPos);
// else
// fAST = new LocalVarDecl(atAST, iAST, new EmptyExpr(dummyPos),
// funcPos);
// }
//
// return fAST;
// }
//
// List parseVarDeclList() throws SyntaxError {
// List dlAST = null;
// Decl dAST = null;
// SourcePosition funcPos = new SourcePosition();
// start(funcPos);
// if (currentToken.kind == Token.INT
// || currentToken.kind == Token.BOOLEAN
// || currentToken.kind == Token.FLOAT
// || (currentToken.kind == Token.COMMA && flag1 == true))
// dAST = parseVarDecl();
// if (currentToken.kind == Token.INT
// || currentToken.kind == Token.BOOLEAN
// || currentToken.kind == Token.FLOAT
// || (currentToken.kind == Token.COMMA && flag1 == true)) {
// dlAST = parseVarDeclList();
// finish(funcPos);
// dlAST = new DeclList(dAST, dlAST, funcPos);
// } else if (dAST != null) {
// finish(funcPos);
// dlAST = new DeclList(dAST, new EmptyDeclList(dummyPos), funcPos);
// }
// if (dlAST == null)
// dlAST = new EmptyDeclList(dummyPos);
// return dlAST;
// }
//
// Decl parseVarDecl() throws SyntaxError {
// Decl dAST = null;
// SourcePosition funcPos = new SourcePosition();
// start(funcPos);
// if (currentToken.kind != Token.COMMA) {
// Type tAST = parseType();
// Ident iAST = parseIdent();
// temptype = tAST;
// Expr eAST = null;
// if (currentToken.kind == Token.LBRACKET)
// dAST = parseArray(tAST, iAST, eAST, funcPos);
//
// else if (currentToken.kind == Token.EQ) {
// accept();
// if (currentToken.kind == Token.LCURLY) {
// accept();
// eAST = parseInitExpr();
// match(Token.RCURLY);
// finish(funcPos);
// dAST = new LocalVarDecl(tAST, iAST, eAST, funcPos);
// } else {
// eAST = parseExpr();
// finish(funcPos);
// dAST = new LocalVarDecl(tAST, iAST, eAST, funcPos);
// }
// } else {
// finish(funcPos);
// dAST = new LocalVarDecl(tAST, iAST, new EmptyExpr(dummyPos),
// funcPos);
// }
// if (currentToken.kind == Token.COMMA) {
// flag1 = true;
// } else {
// match(Token.SEMICOLON);
//
// }
// } else {
// accept();
// Type tAST = temptype;
// Ident iAST = parseIdent();
// Expr eAST = null;
// if (currentToken.kind == Token.LBRACKET)
// dAST = parseArray(tAST, iAST, eAST, funcPos);
// else if (currentToken.kind == Token.EQ) {
// accept();
// eAST = parseExpr();
// finish(funcPos);
// dAST = new LocalVarDecl(tAST, iAST, eAST, funcPos);
// } else {
// finish(funcPos);
// dAST = new LocalVarDecl(tAST, iAST, new EmptyExpr(dummyPos),
// funcPos);
// }
// if (currentToken.kind == Token.COMMA) {
// flag1 = true;
// } else {
// match(Token.SEMICOLON);
// flag1 = false;
// }
// }
// return dAST;
// }
//
// // ======================== TYPES ==========================
//
// Type parseType() throws SyntaxError {
// Type typeAST = null;
//
// SourcePosition typePos = new SourcePosition();
// start(typePos);
// switch (currentToken.kind) {
// case Token.INT:
// accept();
// finish(typePos);
// typeAST = new IntType(typePos);
// break;
// case Token.VOID:
// accept();
// finish(typePos);
// typeAST = new VoidType(typePos);
// break;
// case Token.FLOAT:
// accept();
// finish(typePos);
// typeAST = new FloatType(typePos);
// break;
// case Token.BOOLEAN:
// accept();
// finish(typePos);
// typeAST = new BooleanType(typePos);
// break;
// default:
// syntacticError("type expected here", currentToken.spelling);
// break;
// }
//
// return typeAST;
// }
//
// // ======================= STATEMENTS ==============================
//
// Stmt parseCompoundStmt() throws SyntaxError {
// Stmt cAST = null;
//
// SourcePosition stmtPos = new SourcePosition();
// start(stmtPos);
//
// match(Token.LCURLY);
//
// // Insert code here to build a DeclList node for variable declarations
// List dlAST = null;
// List slAST = null;
// if (currentToken.kind != Token.RCURLY)
// dlAST = parseVarDeclList();
// if (currentToken.kind != Token.RCURLY)
// slAST = parseStmtList();
// match(Token.RCURLY);
// finish(stmtPos);
//
// /*
// * In the subset of the VC grammar, no variable declarations are
// * allowed. Therefore, a block is empty if it has no statements.
// */
// if (dlAST == null && slAST == null) {
// cAST = new EmptyCompStmt(stmtPos);
// return cAST;
// }
// if (dlAST == null)
// dlAST = new EmptyDeclList(dummyPos);
// if (slAST == null)
// slAST = new EmptyStmtList(dummyPos);
//
// cAST = new CompoundStmt(dlAST, slAST, stmtPos);
// return cAST;
// }
//
// List parseStmtList() throws SyntaxError {
// List slAST = null;
//
// SourcePosition stmtPos = new SourcePosition();
// start(stmtPos);
//
// if (currentToken.kind != Token.RCURLY) {
// Stmt sAST = parseStmt();
// {
// if (currentToken.kind != Token.RCURLY) {
// slAST = parseStmtList();
// finish(stmtPos);
// slAST = new StmtList(sAST, slAST, stmtPos);
// } else {
// finish(stmtPos);
// slAST = new StmtList(sAST, new EmptyStmtList(dummyPos),
// stmtPos);
// }
// }
// } else
// slAST = new EmptyStmtList(dummyPos);
//
// return slAST;
// }
//
// Stmt parseStmt() throws SyntaxError {
// Stmt sAST = new EmptyStmt(dummyPos);
// switch (currentToken.kind) {
//
// case Token.CONTINUE:
// sAST = parseContinueStmt();
// break;
// case Token.IF:
// sAST = parseIfStmt();
// break;
// case Token.FOR:
// sAST = parseForStmt();
// break;
// case Token.WHILE:
// sAST = parseWhileStmt();
// break;
// case Token.BREAK:
// sAST = parseBreakStmt();
// break;
// case Token.RETURN:
// sAST = parseRetrunStmt();
// break;
// case Token.LCURLY:
// sAST = parseCompoundStmt();
// break;
// case Token.RCURLY:
// break;
//
// default:
//
// sAST = parseExprStmt();
// break;
// }
//
// return sAST;
// }
//
// Stmt parseExprStmt() throws SyntaxError {
// Stmt sAST = null;
// Expr eAST = new EmptyExpr(dummyPos);
// SourcePosition stmtPos = new SourcePosition();
// start(stmtPos);
// if (currentToken.kind != Token.SEMICOLON)
// eAST = parseExpr();
// match(Token.SEMICOLON);
// finish(stmtPos);
// sAST = new ExprStmt(eAST, stmtPos);
// return sAST;
// }
//
// Stmt parseIfStmt() throws SyntaxError {
// Stmt sAST = null;
// Expr eAST = null;
// Stmt sAST1 = null;
// SourcePosition stmtPos = new SourcePosition();
// start(stmtPos);
// accept();
// match(Token.LPAREN);
// eAST = parseExpr();
// match(Token.RPAREN);
// sAST1 = parseStmt();
// if (currentToken.kind == Token.ELSE) {
// accept();
// Stmt sAST2 = parseStmt();
// finish(stmtPos);
// sAST = new IfStmt(eAST, sAST1, sAST2, stmtPos);
// return sAST;
// }
// finish(stmtPos);
// sAST = new IfStmt(eAST, sAST1, stmtPos);
// return sAST;
//
// }
//
// Stmt parseWhileStmt() throws SyntaxError {
// Stmt sAST = null;
// Stmt sAST1 = null;
// Expr eAST = null;
// SourcePosition stmtPos = new SourcePosition();
// start(stmtPos);
// accept();
// match(Token.LPAREN);
// eAST = parseExpr();
// match(Token.RPAREN);
// sAST1 = parseStmt();
// finish(stmtPos);
// sAST = new WhileStmt(eAST, sAST1, stmtPos);
// return sAST;
// }
//
// Stmt parseBreakStmt() throws SyntaxError {
// Stmt sAST = null;
// SourcePosition stmtPos = new SourcePosition();
// start(stmtPos);
// accept();
// finish(stmtPos);
// match(Token.SEMICOLON);
// sAST = new BreakStmt(stmtPos);
// return sAST;
// }
//
// Stmt parseRetrunStmt() throws SyntaxError {
// Stmt sAST = null;
// Expr eAST = new EmptyExpr(dummyPos);
// SourcePosition stmtPos = new SourcePosition();
// start(stmtPos);
// accept();
// if (currentToken.kind != Token.SEMICOLON)
// eAST = parseExpr();
// match(Token.SEMICOLON);
// finish(stmtPos);
// sAST = new ReturnStmt(eAST, stmtPos);
// return sAST;
//
// }
//
// Stmt parseForStmt() throws SyntaxError {
// Stmt sAST = null;
// Expr eAST1 = new EmptyExpr(dummyPos);
// Expr eAST2 = new EmptyExpr(dummyPos);
// Expr eAST3 = new EmptyExpr(dummyPos);
// Stmt sAST1 = null;
// SourcePosition stmtPos = new SourcePosition();
// start(stmtPos);
// accept();
// match(Token.LPAREN);
// if (currentToken.kind != Token.SEMICOLON)
// eAST1 = parseExpr();
// match(Token.SEMICOLON);
// if (currentToken.kind != Token.SEMICOLON)
// eAST2 = parseExpr();
// match(Token.SEMICOLON);
// if (currentToken.kind != Token.RPAREN)
// eAST3 = parseExpr();
// match(Token.RPAREN);
// sAST1 = parseStmt();
// finish(stmtPos);
// sAST = new ForStmt(eAST1, eAST2, eAST3, sAST1, stmtPos);
// return sAST;
// }
//
// Stmt parseContinueStmt() throws SyntaxError {
// Stmt sAST = null;
// SourcePosition stmtPos = new SourcePosition();
// start(stmtPos);
// accept();
// match(Token.SEMICOLON);
// finish(stmtPos);
// sAST = new ContinueStmt(stmtPos);
// return sAST;
//
// }
//
// // ======================= PARAMETERS =======================
//
// List parseParaList() throws SyntaxError {
// List formalsAST = null;
//
// SourcePosition formalsPos = new SourcePosition();
// start(formalsPos);
// match(Token.LPAREN);
// if (currentToken.kind != Token.RPAREN) {
// formalsAST = parseProperParaList();
// finish(formalsPos);
// match(Token.RPAREN);
//
// } else {
// match(Token.RPAREN);
// finish(formalsPos);
// formalsAST = new EmptyParaList(formalsPos);
// }
//
// return formalsAST;
// }
//
// List parseProperParaList() throws SyntaxError {
// List formalsAST = null;
// ParaDecl dAST = null;
// SourcePosition exprPos = new SourcePosition();
// start(exprPos);
// dAST = parseParaDecl();
// if (currentToken.kind == Token.COMMA) {
// accept();
// List lAST1 = parseProperParaList();
// finish(exprPos);
// formalsAST = new ParaList(dAST, lAST1, exprPos);
// } else {
// finish(exprPos);
// formalsAST = new ParaList(dAST, new EmptyParaList(dummyPos),
// exprPos);
// }
// return formalsAST;
// }
//
// ParaDecl parseParaDecl() throws SyntaxError {
// ParaDecl dAST = null;
// SourcePosition paraPos = new SourcePosition();
// start(paraPos);
// if (currentToken.kind == Token.VOID || currentToken.kind == Token.INT
// || currentToken.kind == Token.FLOAT
// || currentToken.kind == Token.BOOLEAN) {
// Type tAST = parseType();
// Ident iAST = parseIdent();
// if (currentToken.kind == Token.LBRACKET) {
// accept();
// Expr ieAST = null;
// IntLiteral ilAST = null;
// if (currentToken.kind == Token.INTLITERAL) {
// ilAST = parseIntLiteral();
// match(Token.RBRACKET);
// finish(paraPos);
// ieAST = new IntExpr(ilAST, currentToken.position);
// tAST = new ArrayType(tAST, ieAST, tAST.position);
// } else {
// tAST = new ArrayType(tAST, new EmptyExpr(dummyPos),
// tAST.position);
// match(Token.RBRACKET);
// finish(paraPos);
// }
// }
// dAST = new ParaDecl(tAST, iAST, paraPos);
// } else
// syntacticError("type expected here", currentToken.spelling);
// return dAST;
// }
//
// List parseArgList() throws SyntaxError {
// List lAST = null;
// match(Token.LPAREN);
// if (currentToken.kind != Token.RPAREN)
// lAST = parseProperArgList();
// else
// lAST = new EmptyArgList(dummyPos);
// match(Token.RPAREN);
// return lAST;
// }
//
// List parseProperArgList() throws SyntaxError {
// List lAST = null;
// List lAST1 = null;
// Arg aAST1 = null;
// SourcePosition paraPos = new SourcePosition();
// start(paraPos);
// aAST1 = parseArg();
// if (currentToken.kind == Token.COMMA) {
// accept();
// lAST1 = parseProperArgList();
// }
// finish(paraPos);
// if (lAST1 == null)
// lAST = new ArgList(aAST1, new EmptyArgList(dummyPos), paraPos);
// else
// lAST = new ArgList(aAST1, lAST1, paraPos);
// return lAST;
// }
//
// Arg parseArg() throws SyntaxError {
// Arg aAST = null;
// Expr eAST = null;
// SourcePosition paraPos = new SourcePosition();
// start(paraPos);
// eAST = parseExpr();
// finish(paraPos);
// if (eAST == null)
// eAST = new EmptyExpr(dummyPos);
// aAST = new Arg(eAST, paraPos);
// return aAST;
// }
//
// // ======================= EXPRESSIONS ======================
//
// Expr parseExpr() throws SyntaxError {
// Expr exprAST = null;
// exprAST = parseAssignExpr();
// if (exprAST == null)
// exprAST = new EmptyExpr(dummyPos);
// return exprAST;
// }
//
// Expr parseAssignExpr() throws SyntaxError {
// Expr aeAST = null;
// SourcePosition exprPos = new SourcePosition();
// start(exprPos);
// aeAST = parseCondOrExpr();
// if (currentToken.kind == Token.EQ) {
// accept();
// Expr eAST2 = parseAssignExpr();
// SourcePosition newPos = new SourcePosition();
// copyStart(exprPos, newPos);
// finish(newPos);
// if (eAST2 == null)
// eAST2 = new EmptyExpr(dummyPos);
// aeAST = new AssignExpr(aeAST, eAST2, newPos);
// }
// return aeAST;
// }
//
// Expr parseCondOrExpr() throws SyntaxError {
// Expr coAST = null;
// SourcePosition exprPos = new SourcePosition();
// start(exprPos);
// coAST = parseCondAndExpr();
// while (currentToken.kind == Token.OROR) {
// Operator oAST = acceptOperator();
// Expr bAST2 = parseCondAndExpr();
// SourcePosition newPos = new SourcePosition();
// copyStart(exprPos, newPos);
// finish(newPos);
// coAST = new BinaryExpr(coAST, oAST, bAST2, newPos);
// }
// return coAST;
// }
//
// Expr parseCondAndExpr() throws SyntaxError {
// Expr caAST = null;
// SourcePosition exprPos = new SourcePosition();
// start(exprPos);
// caAST = parseEqualityExpr();
// while (currentToken.kind == Token.ANDAND) {
// Operator oAST = acceptOperator();
// Expr bAST2 = parseEqualityExpr();
// SourcePosition newPos = new SourcePosition();
// copyStart(exprPos, newPos);
// finish(newPos);
// caAST = new BinaryExpr(caAST, oAST, bAST2, newPos);
// }
// return caAST;
//
// }
//
// Expr parseEqualityExpr() throws SyntaxError {
// Expr caAST = null;
// SourcePosition exprPos = new SourcePosition();
// start(exprPos);
// caAST = parseRelExpr();
// while (currentToken.kind == Token.EQEQ
// || currentToken.kind == Token.NOTEQ) {
// Operator oAST = acceptOperator();
// Expr bAST2 = parseRelExpr();
// SourcePosition newPos = new SourcePosition();
// copyStart(exprPos, newPos);
// finish(newPos);
// caAST = new BinaryExpr(caAST, oAST, bAST2, newPos);
// }
// return caAST;
// }
//
// Expr parseRelExpr() throws SyntaxError {
// Expr caAST = null;
// SourcePosition exprPos = new SourcePosition();
// start(exprPos);
// caAST = parseAdditiveExpr();
// while (currentToken.kind == Token.LT || currentToken.kind == Token.LTEQ
// || currentToken.kind == Token.GT
// || currentToken.kind == Token.GTEQ) {
// Operator oAST = acceptOperator();
// Expr bAST2 = parseAdditiveExpr();
// SourcePosition newPos = new SourcePosition();
// copyStart(exprPos, newPos);
// finish(newPos);
// caAST = new BinaryExpr(caAST, oAST, bAST2, newPos);
// }
// return caAST;
// }
//
// Expr parseAdditiveExpr() throws SyntaxError {
// Expr exprAST = null;
//
// SourcePosition addStartPos = new SourcePosition();
// start(addStartPos);
//
// exprAST = parseMultiplicativeExpr();
// while (currentToken.kind == Token.PLUS
// || currentToken.kind == Token.MINUS) {
// Operator opAST = acceptOperator();
// Expr e2AST = parseMultiplicativeExpr();
//
// SourcePosition addPos = new SourcePosition();
// copyStart(addStartPos, addPos);
// finish(addPos);
// exprAST = new BinaryExpr(exprAST, opAST, e2AST, addPos);
// }
// return exprAST;
// }
//
// Expr parseMultiplicativeExpr() throws SyntaxError {
//
// Expr exprAST = null;
//
// SourcePosition multStartPos = new SourcePosition();
// start(multStartPos);
//
// exprAST = parseUnaryExpr();
// while (currentToken.kind == Token.MULT
// || currentToken.kind == Token.DIV) {
// Operator opAST = acceptOperator();
// Expr e2AST = parseUnaryExpr();
// SourcePosition multPos = new SourcePosition();
// copyStart(multStartPos, multPos);
// finish(multPos);
// exprAST = new BinaryExpr(exprAST, opAST, e2AST, multPos);
// }
// return exprAST;
// }
//
// Expr parseUnaryExpr() throws SyntaxError {
//
// Expr exprAST = null;
//
// SourcePosition unaryPos = new SourcePosition();
// start(unaryPos);
//
// switch (currentToken.kind) {
// case Token.MINUS: {
// Operator opAST = acceptOperator();
// Expr e2AST = parseUnaryExpr();
// finish(unaryPos);
// exprAST = new UnaryExpr(opAST, e2AST, unaryPos);
// }
// break;
// case Token.PLUS: {
// Operator opAST = acceptOperator();
// Expr e2AST = parseUnaryExpr();
// finish(unaryPos);
// exprAST = new UnaryExpr(opAST, e2AST, unaryPos);
// }
// break;
// case Token.NOT: {
// Operator opAST = acceptOperator();
// Expr e2AST = parseUnaryExpr();
// finish(unaryPos);
// exprAST = new UnaryExpr(opAST, e2AST, unaryPos);
// }
// break;
//
// default:
// exprAST = parsePrimaryExpr();
// break;
//
// }
// return exprAST;
// }
//
// Expr parsePrimaryExpr() throws SyntaxError {
//
// Expr exprAST = null;
//
// SourcePosition primPos = new SourcePosition();
// start(primPos);
//
// switch (currentToken.kind) {
//
// case Token.ID:
// Ident iAST = parseIdent();
// Var simVAST = new SimpleVar(iAST, primPos);
// if (currentToken.kind == Token.LBRACKET) {
// accept();
// if (currentToken.kind != Token.RBRACKET) {
// Expr eAST = parseExpr();
// finish(primPos);
// exprAST = new ArrayExpr(simVAST, eAST, primPos);
// match(Token.RBRACKET);
// } else {
// match(Token.RBRACKET);
// finish(primPos);
// exprAST = new ArrayExpr(simVAST, new EmptyExpr(dummyPos),
// primPos);
// }
// } else if (currentToken.kind == Token.LPAREN) {
// List lAST = parseArgList();
// finish(primPos);
// exprAST = new CallExpr(iAST, lAST, primPos);
// } else {
// finish(primPos);
// exprAST = new VarExpr(simVAST, primPos);
// }
// break;
//
// case Token.LPAREN: {
// accept();
// exprAST = parseExpr();
// match(Token.RPAREN);
// }
// break;
// case Token.INTLITERAL:
// IntLiteral ilAST = parseIntLiteral();
// finish(primPos);
// exprAST = new IntExpr(ilAST, primPos);
// break;
//
// case Token.FLOATLITERAL:
// FloatLiteral flAST = parseFloatLiteral();
// finish(primPos);
// exprAST = new FloatExpr(flAST, primPos);
// break;
// case Token.BOOLEANLITERAL:
// BooleanLiteral blAST = parseBooleanLiteral();
// finish(primPos);
// exprAST = new BooleanExpr(blAST, primPos);
// break;
// case Token.STRINGLITERAL:
// StringLiteral slAST = parseStringLiteral();
// finish(primPos);
// exprAST = new StringExpr(slAST, primPos);
// break;
//
// default:
// syntacticError("illegal parimary expression", currentToken.spelling);
//
// }
// return exprAST;
// }
//
// Expr parseInitExpr() throws SyntaxError {
// Expr exprAST = null;
// SourcePosition InitExprpos = new SourcePosition();
// start(InitExprpos);
// List ilAST = parseExprList();
// finish(InitExprpos);
// exprAST = new InitExpr(ilAST, InitExprpos);
// return exprAST;
// }
//
// List parseExprList() throws SyntaxError {
// List elAST = null;
// Expr exprAST = null;
// List ilAST = null;
// SourcePosition ExprListpos = new SourcePosition();
// start(ExprListpos);
// if (currentToken.kind != Token.RCURLY) {
// exprAST = parseExpr();
// if (currentToken.kind == Token.COMMA) {
// accept();
// ilAST = parseExprList();
// finish(ExprListpos);
// elAST = new ExprList(exprAST, ilAST, ExprListpos);
// } else {
// finish(ExprListpos);
// elAST = new ExprList(exprAST, new EmptyExprList(dummyPos),
// ExprListpos);
// }
// } else {
// elAST = new EmptyExprList(dummyPos);
// }
// return elAST;
// }
//
// // ========================== ID, OPERATOR and LITERALS
// // ========================
//
// Ident parseIdent() throws SyntaxError {
//
// Ident I = null;
// if (flag2 == true) {
// if (currentToken.kind == Token.ID) {
// previousTokenPosition = currentToken.position;
// String spelling = currentToken.spelling;
// I = new Ident(spelling, previousTokenPosition);
// currentToken = temptoken;
// flag2 = false;
// } else
// syntacticError("identifier expected here", "");
// } else {
// if (currentToken.kind == Token.ID) {
// previousTokenPosition = currentToken.position;
// String spelling = currentToken.spelling;
// I = new Ident(spelling, previousTokenPosition);
// currentToken = scanner.getToken();
// } else
// syntacticError("identifier expected here", "");
//
// }
// return I;
// }
//
// // acceptOperator parses an operator, and constructs a leaf AST for it
//
// Operator acceptOperator() throws SyntaxError {
// Operator O = null;
//
// previousTokenPosition = currentToken.position;
// String spelling = currentToken.spelling;
// O = new Operator(spelling, previousTokenPosition);
// currentToken = scanner.getToken();
// return O;
// }
//
// IntLiteral parseIntLiteral() throws SyntaxError {
// IntLiteral IL = null;
//
// if (currentToken.kind == Token.INTLITERAL) {
// String spelling = currentToken.spelling;
// accept();
// IL = new IntLiteral(spelling, previousTokenPosition);
// } else
// syntacticError("integer literal expected here", "");
// return IL;
// }
//
// FloatLiteral parseFloatLiteral() throws SyntaxError {
// FloatLiteral FL = null;
//
// if (currentToken.kind == Token.FLOATLITERAL) {
// String spelling = currentToken.spelling;
// accept();
// FL = new FloatLiteral(spelling, previousTokenPosition);
// } else
// syntacticError("float literal expected here", "");
// return FL;
// }
//
// BooleanLiteral parseBooleanLiteral() throws SyntaxError {
// BooleanLiteral BL = null;
//
// if (currentToken.kind == Token.BOOLEANLITERAL) {
// String spelling = currentToken.spelling;
// accept();
// BL = new BooleanLiteral(spelling, previousTokenPosition);
// } else
// syntacticError("boolean literal expected here", "");
// return BL;
// }
//
// StringLiteral parseStringLiteral() throws SyntaxError {
// StringLiteral SL = null;
//
// if (currentToken.kind == Token.STRINGLITERAL) {
// String spelling = currentToken.spelling;
// accept();
// SL = new StringLiteral(spelling, previousTokenPosition);
// } else
// syntacticError("integer literal expected here", "");
// return SL;
// }
//
// }
