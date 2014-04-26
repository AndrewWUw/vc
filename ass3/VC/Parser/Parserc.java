package VC.Parser;

import VC.Scanner.Scanner;
import VC.Scanner.SourcePosition;
import VC.Scanner.Token;
import VC.ErrorReporter;
import VC.ASTs.*;

public class Parserc {

	private Scanner scanner;
	private ErrorReporter errorReporter;
	private Token currentToken;
	private SourcePosition previousTokenPosition;
	private SourcePosition dummyPos = new SourcePosition();

	public Parserc(Scanner lexer, ErrorReporter reporter) {
		scanner = lexer;
		errorReporter = reporter;

		previousTokenPosition = new SourcePosition();

		currentToken = scanner.getToken();
	}

	// match checks to see f the current token matches tokenExpected.
	// If so, fetches the next token.
	// If not, reports a syntactic error.

	void match(int tokenExpected) throws SyntaxError {
		if (currentToken.kind == tokenExpected) {
			previousTokenPosition = currentToken.position;
			currentToken = scanner.getToken();
		} else {
			syntacticError("\"%\" expected here", Token.spell(tokenExpected));
		}
	}

	void accept() {
		previousTokenPosition = currentToken.position;
		currentToken = scanner.getToken();
	}

	void syntacticError(String messageTemplate, String tokenQuoted)
			throws SyntaxError {
		SourcePosition pos = currentToken.position;
		errorReporter.reportError(messageTemplate, tokenQuoted, pos);
		throw (new SyntaxError());
	}

	// start records the position of the start of a phrase.
	// This is defined to be the position of the first
	// character of the first token of the phrase.

	void start(SourcePosition position) {
		position.lineStart = currentToken.position.lineStart;
		position.charStart = currentToken.position.charStart;
	}

	// finish records the position of the end of a phrase.
	// This is defined to be the position of the last
	// character of the last token of the phrase.

	void finish(SourcePosition position) {
		position.lineFinish = previousTokenPosition.lineFinish;
		position.charFinish = previousTokenPosition.charFinish;
	}

	void copyStart(SourcePosition from, SourcePosition to) {
		to.lineStart = from.lineStart;
		to.charStart = from.charStart;
	}

	// ========================== PROGRAMS ========================

	public Program parseProgram() {

		Program programAST = null;

		SourcePosition programPos = new SourcePosition();
		start(programPos);

		try {
			List dlAST = null;
			finish(programPos);
			programAST = new Program(dlAST, programPos);
			if (currentToken.kind != Token.EOF) {
				syntacticError("\"%\" unknown type", currentToken.spelling);
			}
		} catch (SyntaxError s) {
			return null;
		}
		return programAST;
	}

	// ========================== DECLARATIONS ========================

	List parseGlobalVarDeclList() throws SyntaxError {

		return null;
	}

	Decl parseGlobalVarDecl() throws SyntaxError {

		Decl gvdAST = null;

		// gvdAST = new GlobalVarDecl(tAST, iAST, eAST, position);

		return gvdAST;
	}

	List parseLocalVarDeclList() throws SyntaxError {

		List dlAST = null;
		SourcePosition declPos = new SourcePosition();
		start(declPos);

		if (currentToken.kind == Token.VOID
				|| currentToken.kind == Token.BOOLEAN
				|| currentToken.kind == Token.INT
				|| currentToken.kind == Token.FLOAT) {
			Decl dAST = parseLocalVarDecl();

			if (currentToken.kind == Token.VOID
					|| currentToken.kind == Token.BOOLEAN
					|| currentToken.kind == Token.INT
					|| currentToken.kind == Token.FLOAT) {
				dlAST = parseLocalVarDeclList();
				finish(declPos);
				dlAST = new DeclList(dAST, dlAST, declPos);
			} else {
				finish(declPos);
				dlAST = new DeclList(dAST, new EmptyDeclList(dummyPos), declPos);
			}
		} else
			dlAST = new EmptyDeclList(dummyPos);

		return dlAST;
	}

	Decl parseLocalVarDecl() throws SyntaxError {

		Decl lvarAST = null;
		Type tAST = null;
		Ident iAST = null;
		Expr eAST = null;

		SourcePosition declPos = new SourcePosition();
		start(declPos);

		if (currentToken.kind == Token.VOID
				|| currentToken.kind == Token.BOOLEAN
				|| currentToken.kind == Token.INT
				|| currentToken.kind == Token.FLOAT) {
			tAST = parseType();
			iAST = parseIdent();

			if (currentToken.kind == Token.LBRACKET) {
				// array decl
				accept();
				Expr dAST = parseExpr();
				match(Token.RBRACKET);
				tAST = new ArrayType(tAST, dAST, declPos);

				if (currentToken.kind == Token.EQ) {
					// eAST = parseArrayVarInitList();
					finish(declPos);
					lvarAST = new LocalVarDecl(tAST, iAST, eAST, declPos);
				} else {
					finish(declPos);
					lvarAST = new LocalVarDecl(tAST, iAST, new EmptyExpr(
							dummyPos), declPos);
				}
			} else {
				if (currentToken.kind == Token.EQ) {
					// var init
					eAST = parseVarExprInit();
					finish(declPos);
					lvarAST = new LocalVarDecl(tAST, iAST, eAST, declPos);
				} else {
					lvarAST = new LocalVarDecl(tAST, iAST, new EmptyExpr(
							dummyPos), declPos);
				}
			}

			if (currentToken.kind == Token.COMMA) {
				// mutiple var decl
				accept();
			} else {
				match(Token.SEMICOLON);
			}
		}

		return lvarAST;
	}

	List parseInitDeclList() throws SyntaxError {

		List dlAST = null;
		SourcePosition initDeclPos = new SourcePosition();
		start(initDeclPos);

		parseInitDecl();
		while (currentToken.kind == Token.COMMA) {
			accept();
			parseInitDecl();
		}

		return dlAST;
	}

	void parseInitDecl() throws SyntaxError {
		if (currentToken.kind != Token.EQ) {
			parseDeclarator();
		}
		if (currentToken.kind == Token.EQ) {
			accept();
			// parseInitialiser();
		}
	}

	Decl parseDeclarator() throws SyntaxError {

		Ident I = null;
		SourcePosition dPos = new SourcePosition();
		start(dPos);

		if (currentToken.kind == Token.ID) {
			I = parseIdent();
		} else {
			syntacticError(
					"Illegal declarator expression, identifier expected here",
					currentToken.spelling);
		}
		if (currentToken.kind == Token.LBRACKET) {
			accept();
			if (currentToken.kind == Token.INTLITERAL) {
				accept();
			}
			match(Token.RBRACKET);

		} else {
			// dAST = new
		}

		return null;
	}

	Type parseArrayDecl() throws SyntaxError {
		Type tAST = null;

		return null;
	}

	Expr parseVarExprInit() throws SyntaxError {
		Expr eAST = parseExpr();
		return eAST;
	}

	List parseArrayVarInitList() throws SyntaxError {

		List elAST = null;
		Expr eAST = null;

		SourcePosition arrayInitPos = new SourcePosition();
		eAST = parseExpr();

		if (currentToken.kind == Token.COMMA) {
			accept();
			elAST = parseArrayVarInitList();
			finish(arrayInitPos);
			elAST = new ExprList(eAST, elAST, arrayInitPos);
		} else if (eAST != null) {
			finish(arrayInitPos);
			elAST = new ExprList(eAST, new EmptyExprList(dummyPos),
					arrayInitPos);
		} else {
			elAST = new EmptyExprList(dummyPos);
		}

		return elAST;
	}

	// ======================== TYPES ==========================

	Type parseType() throws SyntaxError {
		Type typeAST = null;
		SourcePosition typePos = new SourcePosition();
		start(typePos);

		switch (currentToken.kind) {
		case Token.VOID:
			match(Token.VOID);
			finish(typePos);
			typeAST = new VoidType(typePos);
			break;
		case Token.BOOLEAN:
			match(Token.BOOLEAN);
			finish(typePos);
			typeAST = new BooleanType(typePos);
			break;
		case Token.INT:
			match(Token.INT);
			finish(typePos);
			typeAST = new IntType(typePos);
			break;
		case Token.FLOAT:
			match(Token.FLOAT);
			finish(typePos);
			typeAST = new FloatType(typePos);
			break;
		default:
			syntacticError("Illegal type declaration", currentToken.spelling);
		}

		return typeAST;
	}

	// ======================= EXPRESSIONS ======================

	Expr parseExpr() throws SyntaxError {
		Expr exprAST = null;
		exprAST = parseAssignExpr();
		return exprAST;
	}

	Expr parseAssignExpr() throws SyntaxError {

		Expr exprAST = null;
		SourcePosition assignStartPos = new SourcePosition();
		start(assignStartPos);

		exprAST = parseCondOrExpr();
		while (currentToken.kind == Token.EQ) {
			Operator opAST = acceptOperator();
			Expr e2AST = parseCondOrExpr();

			SourcePosition assignPos = new SourcePosition();
			copyStart(assignStartPos, assignPos);
			finish(assignPos);
			exprAST = new BinaryExpr(exprAST, opAST, e2AST, assignPos);
		}
		return exprAST;
	}

	Expr parseCondOrExpr() throws SyntaxError {

		Expr exprAST = null;
		SourcePosition condOrStartPos = new SourcePosition();
		start(condOrStartPos);

		exprAST = parseCondAndExpr();
		while (currentToken.kind == Token.OROR) {
			Operator opAST = acceptOperator();
			Expr e2AST = parseCondAndExpr();

			SourcePosition condOrPos = new SourcePosition();
			copyStart(condOrStartPos, condOrPos);
			finish(condOrPos);
			exprAST = new BinaryExpr(exprAST, opAST, e2AST, condOrPos);
		}
		return exprAST;
	}

	Expr parseCondAndExpr() throws SyntaxError {

		Expr exprAST = null;
		SourcePosition condAndStartPos = new SourcePosition();
		start(condAndStartPos);

		exprAST = parseEqualityExpr();
		while (currentToken.kind == Token.ANDAND) {
			Operator opAST = acceptOperator();
			Expr e2AST = parseEqualityExpr();

			SourcePosition condAndPos = new SourcePosition();
			copyStart(condAndStartPos, condAndPos);
			finish(condAndPos);
			exprAST = new BinaryExpr(exprAST, opAST, e2AST, condAndPos);
		}
		return exprAST;
	}

	Expr parseEqualityExpr() throws SyntaxError {

		Expr exprAST = null;
		SourcePosition equStartPos = new SourcePosition();
		start(equStartPos);

		exprAST = parseRealExpr();
		while (currentToken.kind == Token.EQEQ
				|| currentToken.kind == Token.NOTEQ) {
			Operator opAST = acceptOperator();
			Expr e2AST = parseRealExpr();

			SourcePosition equalitylPos = new SourcePosition();
			copyStart(equStartPos, equalitylPos);
			finish(equalitylPos);
			exprAST = new BinaryExpr(exprAST, opAST, e2AST, equalitylPos);
		}
		return exprAST;
	}

	Expr parseRealExpr() throws SyntaxError {

		Expr exprAST = null;
		SourcePosition realStartPos = new SourcePosition();
		start(realStartPos);

		exprAST = parseAdditiveExpr();
		while (currentToken.kind == Token.LT || currentToken.kind == Token.GT
				|| currentToken.kind == Token.LTEQ
				|| currentToken.kind == Token.GTEQ) {
			Operator opAST = acceptOperator();
			Expr e2AST = parseAdditiveExpr();

			SourcePosition realPos = new SourcePosition();
			copyStart(realStartPos, realPos);
			finish(realPos);
			exprAST = new BinaryExpr(exprAST, opAST, e2AST, realPos);
		}
		return exprAST;
	}

	Expr parseAdditiveExpr() throws SyntaxError {
		Expr exprAST = null;

		SourcePosition addStartPos = new SourcePosition();
		start(addStartPos);

		exprAST = parseMultiplicativeExpr();
		while (currentToken.kind == Token.PLUS
				|| currentToken.kind == Token.MINUS) {
			Operator opAST = acceptOperator();
			Expr e2AST = parseMultiplicativeExpr();

			SourcePosition addPos = new SourcePosition();
			copyStart(addStartPos, addPos);
			finish(addPos);
			exprAST = new BinaryExpr(exprAST, opAST, e2AST, addPos);
		}
		return exprAST;
	}

	Expr parseMultiplicativeExpr() throws SyntaxError {

		Expr exprAST = null;

		SourcePosition multStartPos = new SourcePosition();
		start(multStartPos);

		exprAST = parseUnaryExpr();
		while (currentToken.kind == Token.MULT
				|| currentToken.kind == Token.DIV) {
			Operator opAST = acceptOperator();
			Expr e2AST = parseUnaryExpr();

			SourcePosition multPos = new SourcePosition();
			copyStart(multStartPos, multPos);
			finish(multPos);
			exprAST = new BinaryExpr(exprAST, opAST, e2AST, multPos);
		}

		return exprAST;
	}

	Expr parseUnaryExpr() throws SyntaxError {

		Expr exprAST = null;

		SourcePosition unaryPos = new SourcePosition();
		start(unaryPos);

		switch (currentToken.kind) {
		case Token.MINUS: {
			Operator opAST = acceptOperator();
			Expr e2AST = parseUnaryExpr();
			finish(unaryPos);
			exprAST = new UnaryExpr(opAST, e2AST, unaryPos);
		}
			break;

		case Token.PLUS: {
			Operator opAST = acceptOperator();
			Expr e2AST = parseUnaryExpr();
			finish(unaryPos);
			exprAST = new UnaryExpr(opAST, e2AST, unaryPos);
		}
			break;

		case Token.NOT: {
			Operator opAST = acceptOperator();
			Expr e2AST = parseUnaryExpr();
			finish(unaryPos);
			exprAST = new UnaryExpr(opAST, e2AST, unaryPos);
		}
			break;

		default:
			exprAST = parsePrimaryExpr();
			break;

		}
		return exprAST;
	}

	Expr parsePrimaryExpr() throws SyntaxError {

		Expr exprAST = null;

		SourcePosition primPos = new SourcePosition();
		start(primPos);

		switch (currentToken.kind) {

		case Token.ID:
			Ident iAST = parseIdent();

			if (currentToken.kind == Token.LPAREN) {
				List aplAST = parseArgList();
				finish(primPos);
				exprAST = new CallExpr(iAST, aplAST, primPos);
			} else if (currentToken.kind == Token.LBRACKET) {
				accept();
				Expr indexAST = parseExpr();
				match(Token.RBRACKET);
				finish(primPos);
				Var simVAST = new SimpleVar(iAST, primPos);
				exprAST = new ArrayExpr(simVAST, indexAST, primPos);
			}
			break;

		case Token.LPAREN:
			accept();
			exprAST = parseExpr();
			match(Token.RPAREN);
			break;

		case Token.INTLITERAL:
			IntLiteral ilAST = parseIntLiteral();
			finish(primPos);
			exprAST = new IntExpr(ilAST, primPos);
			break;

		case Token.FLOATLITERAL:
			FloatLiteral flAST = parseFloatLiteral();
			finish(primPos);
			exprAST = new FloatExpr(flAST, primPos);
			break;

		case Token.BOOLEANLITERAL:
			BooleanLiteral blAST = parseBooleanLiteral();
			finish(primPos);
			exprAST = new BooleanExpr(blAST, primPos);
			break;

		case Token.STRINGLITERAL:
			StringLiteral slAST = parseStringLiteral();
			finish(primPos);
			exprAST = new StringExpr(slAST, primPos);
			break;

		default:
			syntacticError("illegal parimary expression", currentToken.spelling);

		}
		return exprAST;
	}

	// ======================= PARAMETERS =======================

	List parseParaList() throws SyntaxError {
		List formalsAST = null;

		SourcePosition formalsPos = new SourcePosition();
		start(formalsPos);

		match(Token.LPAREN);
		if (currentToken.kind != Token.RPAREN) {
			parseProperParaList();
		}

		match(Token.RPAREN);
		finish(formalsPos);
		formalsAST = new EmptyParaList(formalsPos);

		return formalsAST;
	}

	void parseProperParaList() throws SyntaxError {

		parseParaDecl();
		while (currentToken.kind == Token.COMMA) {
			accept();
			parseParaDecl();
		}
	}

	void parseParaDecl() throws SyntaxError {

		ParaDecl pdAST = null;
		SourcePosition pdPos = new SourcePosition();

		start(pdPos);
		Type tAST = parseType();
		Decl dAST = parseDeclarator();
		finish(pdPos);
		// pdAST = new ParaDecl(tAST, idAST, pdPos);

	}

	List parseArgList() throws SyntaxError {

		ArgList alAST = null;
		SourcePosition alPos = new SourcePosition();

		start(alPos);
		match(Token.LPAREN);
		if (currentToken.kind != Token.RPAREN) {
			alAST = (ArgList) parseProperArgList();
		}
		if (currentToken.kind == Token.RPAREN) {
			accept();
		} else {
			syntacticError("Illegal arg list expression", currentToken.spelling);
		}
		return alAST;
	}

	List parseProperArgList() throws SyntaxError {

		List alAST = null;
		SourcePosition palPosition = new SourcePosition();

		start(palPosition);
		Arg aAST = parseArg();

		if (currentToken.kind == Token.COMMA) {
			accept();
			if (currentToken.kind == Token.COMMA) {
				alAST = parseProperArgList();
				finish(palPosition);
				alAST = new ArgList(aAST, alAST, palPosition);
			} else {
				finish(palPosition);
				alAST = new ArgList(aAST, new EmptyArgList(palPosition),
						palPosition);
			}
		} else {
			alAST = new EmptyArgList(palPosition);
		}

		return alAST;
	}

	Arg parseArg() throws SyntaxError {
		Arg aAST = null;
		SourcePosition argPos = new SourcePosition();

		start(argPos);
		Expr eAST = parseExpr();
		finish(argPos);
		aAST = new Arg(eAST, argPos);

		return aAST;
	}

	// ======================== ID, OPERATOR and LITERALS ======================

	Ident parseIdent() throws SyntaxError {

		Ident I = null;

		if (currentToken.kind == Token.ID) {
			previousTokenPosition = currentToken.position;
			String spelling = currentToken.spelling;
			I = new Ident(spelling, previousTokenPosition);
			currentToken = scanner.getToken();
		} else
			syntacticError("identifier expected here", "");
		return I;
	}

	// acceptOperator parses an operator, and constructs a leaf AST for it

	Operator acceptOperator() throws SyntaxError {
		Operator O = null;

		previousTokenPosition = currentToken.position;
		String spelling = currentToken.spelling;
		O = new Operator(spelling, previousTokenPosition);
		currentToken = scanner.getToken();
		return O;
	}

	IntLiteral parseIntLiteral() throws SyntaxError {
		IntLiteral IL = null;

		if (currentToken.kind == Token.INTLITERAL) {
			String spelling = currentToken.spelling;
			accept();
			IL = new IntLiteral(spelling, previousTokenPosition);
		} else
			syntacticError("integer literal expected here", "");
		return IL;
	}

	FloatLiteral parseFloatLiteral() throws SyntaxError {
		FloatLiteral FL = null;

		if (currentToken.kind == Token.FLOATLITERAL) {
			String spelling = currentToken.spelling;
			accept();
			FL = new FloatLiteral(spelling, previousTokenPosition);
		} else
			syntacticError("float literal expected here", "");
		return FL;
	}

	BooleanLiteral parseBooleanLiteral() throws SyntaxError {
		BooleanLiteral BL = null;

		if (currentToken.kind == Token.BOOLEANLITERAL) {
			String spelling = currentToken.spelling;
			accept();
			BL = new BooleanLiteral(spelling, previousTokenPosition);
		} else
			syntacticError("boolean literal expected here", "");
		return BL;
	}

	StringLiteral parseStringLiteral() throws SyntaxError {

		StringLiteral SL = null;

		if (currentToken.kind == Token.STRINGLITERAL) {
			String spelling = currentToken.spelling;
			accept();
			SL = new StringLiteral(spelling, previousTokenPosition);
		} else
			syntacticError("String literal expected here", "");
		return SL;
	}
}
