/***********************************************************************
 *                                                                     *
 *   Recogniser.java             									   *
 *                                                                     *
 **********************************************************************/

/* At this stage, this parser accepts a subset of VC defined	by
 * the following grammar. 
 *
 * You need to modify the supplied parsing methods (if necessary) and 
 * add the missing ones to obtain a parser for the VC language.
 *
 * (23-*-March-*-2014)

 program       -> func-decl

 // declaration
 func-decl     -> void identifier "(" ")" compound-stmt

 identifier    -> ID

 // statements 
 compound-stmt -> "{" stmt* "}" 
 stmt          -> continue-stmt
 			   |  expr-stmt
 continue-stmt -> continue ";"
 expr-stmt     -> expr? ";"

 // expressions 
 expr                -> assignment-expr
 assignment-expr     -> additive-expr
 additive-expr       -> multiplicative-expr
 					 |  additive-expr "+" multiplicative-expr
 multiplicative-expr -> unary-expr
 					 |  multiplicative-expr "*" unary-expr
 unary-expr          -> "-" unary-expr
 					 |  primary-expr

 primary-expr        -> identifier
 					 |  INTLITERAL
 					 | "(" expr ")"
 */

package VC.Recogniser;

import VC.Scanner.Scanner;
import VC.Scanner.SourcePosition;
import VC.Scanner.Token;
import VC.ErrorReporter;

public class Recogniser {

	private Scanner scanner;
	private ErrorReporter errorReporter;
	private Token currentToken;

	public Recogniser(Scanner lexer, ErrorReporter reporter) {
		scanner = lexer;
		errorReporter = reporter;

		currentToken = scanner.getToken();
	}

	// match checks to see f the current token matches tokenExpected.
	// If so, fetches the next token.
	// If not, reports a syntactic error.

	void match(int tokenExpected) throws SyntaxError {
		if (currentToken.kind == tokenExpected) {
			currentToken = scanner.getToken();
		} else {
			syntacticError("\"%\" expected here", Token.spell(tokenExpected));
		}
	}

	// accepts the current token and fetches the next
	void accept() {
		currentToken = scanner.getToken();
	}

	void syntacticError(String messageTemplate, String tokenQuoted)
			throws SyntaxError {
		SourcePosition pos = currentToken.position;
		errorReporter.reportError(messageTemplate, tokenQuoted, pos);
		throw (new SyntaxError());
	}

	// ========================== PROGRAMS ========================

	public void parseProgram() {

		try {
			while (currentToken.kind != Token.EOF) {
				if (currentToken.kind == Token.VOID
						|| currentToken.kind == Token.BOOLEAN
						|| currentToken.kind == Token.INT
						|| currentToken.kind == Token.FLOAT) {
					parseType();
					parseIdent();
					switch (currentToken.kind) {
					case Token.LPAREN:
						parseFuncDecl();
						break;
					case Token.LBRACKET:
					case Token.EQ:
					case Token.COMMA:
						parseVarDecl();
						break;
					case Token.SEMICOLON:
						accept();
						break;
					default:
						syntacticError("Wrong result type for a program",
								currentToken.spelling);
					}
				} else
					break;
			}
			// parseFuncDecl();
			if (currentToken.kind != Token.EOF) {
				syntacticError("\"%\" wrong result type for a function",
						currentToken.spelling);
			}
		} catch (SyntaxError s) {
		}
	}

	// ========================== DECLARATIONS ========================

	void parseFuncDecl() throws SyntaxError {

		if (currentToken.kind == Token.VOID
				|| currentToken.kind == Token.BOOLEAN
				|| currentToken.kind == Token.INT
				|| currentToken.kind == Token.FLOAT) {
			parseType();
			parseIdent();
		}
		parseParaList();
		parseCompoundStmt();
	}

	void parseVarDecl() throws SyntaxError {

		if (currentToken.kind == Token.VOID
				|| currentToken.kind == Token.BOOLEAN
				|| currentToken.kind == Token.INT
				|| currentToken.kind == Token.FLOAT) {
			parseType();
		}
		parseInitDeclList();
		match(Token.SEMICOLON);
	}

	void parseInitDeclList() throws SyntaxError {
		parseInitDecl();
		while (currentToken.kind == Token.COMMA) {
			accept();
			parseInitDecl();
		}
	}

	void parseInitDecl() throws SyntaxError {
		if (currentToken.kind != Token.EQ) {
			parseDeclarator();
		}
		if (currentToken.kind == Token.EQ) {
			accept();
			parseInitialiser();
		}
	}

	void parseDeclarator() throws SyntaxError {
		if (currentToken.kind == Token.ID) {
			parseIdent();
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
		}
	}

	void parseInitialiser() throws SyntaxError {
		if (currentToken.kind == Token.LCURLY) {
			accept();
			parseExpr();
			while (currentToken.kind == Token.COMMA) {
				accept();
				parseExpr();
			}
			match(Token.RCURLY);
		} else {
			parseExpr();
		}
	}

	// ==================== PRIMITIVE TYPES ============================

	void parseType() throws SyntaxError {
		switch (currentToken.kind) {
		case Token.VOID:
			match(Token.VOID);
			break;
		case Token.BOOLEAN:
			match(Token.BOOLEAN);
			break;
		case Token.INT:
			match(Token.INT);
			break;
		case Token.FLOAT:
			match(Token.FLOAT);
			break;
		default:
			syntacticError("Illegal type declaration", currentToken.spelling);
		}
	}

	// ======================= STATEMENTS ==============================

	void parseCompoundStmt() throws SyntaxError {

		match(Token.LCURLY);
		parseVarDeclList();
		parseStmtList();
		match(Token.RCURLY);
	}

	void parseVarDeclList() throws SyntaxError {

		while (currentToken.kind == Token.VOID
				|| currentToken.kind == Token.BOOLEAN
				|| currentToken.kind == Token.INT
				|| currentToken.kind == Token.FLOAT)
			parseVarDecl();
	}

	// Here, a new nontermial has been introduced to define { stmt } *
	void parseStmtList() throws SyntaxError {

		while (currentToken.kind != Token.RCURLY)
			parseStmt();
	}

	void parseStmt() throws SyntaxError {

		switch (currentToken.kind) {
		case Token.IF:
			parseIFStmt();
			break;
		case Token.FOR:
			parseFORStmt();
			break;
		case Token.WHILE:
			parseWHILEStmt();
			break;
		case Token.BREAK:
			parseBreakStmt();
			break;
		case Token.CONTINUE:
			parseContinueStmt();
			break;
		case Token.RETURN:
			parseReturnStmt();
			break;
		case Token.LCURLY:
			parseCompoundStmt();
			break;
		default:
			parseExprStmt();
			break;
		}
	}

	void parseIFStmt() throws SyntaxError {

		accept();
		match(Token.LPAREN);
		parseExpr();
		match(Token.RPAREN);
		parseStmt();
		if (currentToken.kind == Token.ELSE) {
			accept();
			parseStmt();
		}
	}

	void parseFORStmt() throws SyntaxError {

		accept();
		match(Token.LPAREN);
		if (currentToken.kind != Token.SEMICOLON) {
			parseExpr();
		}

		match(Token.SEMICOLON);

		if (currentToken.kind != Token.SEMICOLON) {
			parseExpr();
		}

		match(Token.SEMICOLON);

		if (currentToken.kind != Token.RPAREN) {
			parseExpr();
		}

		match(Token.RPAREN);
		parseStmt();
	}

	void parseWHILEStmt() throws SyntaxError {

		accept();
		match(Token.LPAREN);
		parseExpr();
		match(Token.RPAREN);
		parseStmt();
	}

	void parseBreakStmt() throws SyntaxError {

		match(Token.BREAK);
		match(Token.SEMICOLON);
	}

	void parseContinueStmt() throws SyntaxError {

		match(Token.CONTINUE);
		match(Token.SEMICOLON);
	}

	void parseReturnStmt() throws SyntaxError {

		match(Token.RETURN);
		if (currentToken.kind != Token.SEMICOLON) {
			parseExpr();
		}
		match(Token.SEMICOLON);
	}

	void parseExprStmt() throws SyntaxError {

		if (currentToken.kind != Token.SEMICOLON) {
			parseExpr();
		}
		match(Token.SEMICOLON);
	}

	// ======================= IDENTIFIERS ======================

	// Call parseIdent rather than match(Token.ID).
	// In Assignment 3, an Identifier node will be constructed in here.

	void parseIdent() throws SyntaxError {

		if (currentToken.kind == Token.ID) {
			currentToken = scanner.getToken();
		} else
			syntacticError("identifier expected here", "");
	}

	// ======================= OPERATORS ======================

	// Call acceptOperator rather than accept().
	// In Assignment 3, an Operator Node will be constructed in here.

	void acceptOperator() throws SyntaxError {

		currentToken = scanner.getToken();
	}

	// ======================= EXPRESSIONS ======================

	void parseExpr() throws SyntaxError {
		parseAssignExpr();
	}

	void parseAssignExpr() throws SyntaxError {

		// parseAdditiveExpr();
		parseCondOrExpr();
		while (currentToken.kind == Token.EQ) {
			acceptOperator();
			parseCondOrExpr();
		}
	}

	void parseCondOrExpr() throws SyntaxError {

		parseCondAndExpr();
		while (currentToken.kind == Token.OROR) {
			accept();
			parseCondAndExpr();
		}
	}

	void parseCondAndExpr() throws SyntaxError {

		parseEqualityExpr();
		while (currentToken.kind == Token.ANDAND) {
			acceptOperator();
			parseEqualityExpr();
		}
	}

	void parseEqualityExpr() throws SyntaxError {

		parseRealExpr();
		while (currentToken.kind == Token.EQEQ
				|| currentToken.kind == Token.NOTEQ) {
			acceptOperator();
			parseRealExpr();
		}
	}

	void parseRealExpr() throws SyntaxError {

		parseAdditiveExpr();
		while (currentToken.kind == Token.LT || currentToken.kind == Token.GT
				|| currentToken.kind == Token.LTEQ
				|| currentToken.kind == Token.GTEQ) {
			acceptOperator();
			parseAdditiveExpr();
		}
	}

	void parseAdditiveExpr() throws SyntaxError {

		parseMultiplicativeExpr();
		while (currentToken.kind == Token.PLUS
				|| currentToken.kind == Token.MINUS) {
			acceptOperator();
			parseMultiplicativeExpr();
		}
	}

	void parseMultiplicativeExpr() throws SyntaxError {

		parseUnaryExpr();
		while (currentToken.kind == Token.MULT
				|| currentToken.kind == Token.DIV) {
			acceptOperator();
			parseUnaryExpr();
		}
	}

	void parseUnaryExpr() throws SyntaxError {

		switch (currentToken.kind) {
		case Token.MINUS: {
			acceptOperator();
			parseUnaryExpr();
		}
			break;

		case Token.PLUS: {
			acceptOperator();
			parseUnaryExpr();
		}
			break;

		case Token.NOT: {
			acceptOperator();
			parseUnaryExpr();
		}
			break;

		default:
			parsePrimaryExpr();
			break;

		}
	}

	void parsePrimaryExpr() throws SyntaxError {

		switch (currentToken.kind) {

		case Token.ID:
			parseIdent();
			if (currentToken.kind == Token.LPAREN) {
				parseArgList();
			} else if (currentToken.kind == Token.LBRACKET) {
				accept();
				parseExpr();
				match(Token.RBRACKET);
			}
			break;

		case Token.LPAREN: {
			accept();
			parseExpr();
			match(Token.RPAREN);
		}
			break;

		case Token.INTLITERAL:
			parseIntLiteral();
			break;

		case Token.FLOATLITERAL:
			parseFloatLiteral();
			break;

		case Token.BOOLEANLITERAL:
			parseBooleanLiteral();
			break;

		case Token.STRINGLITERAL:
			parseStringLiteral();
			break;

		default:
			syntacticError("illegal parimary expression", currentToken.spelling);
		}
	}

	// ======================== PARAMETERS ========================

	void parseParaList() throws SyntaxError {

		match(Token.LPAREN);
		if (currentToken.kind != Token.RPAREN) {
			parseProperParaList();
		}
		match(Token.RPAREN);
	}

	void parseProperParaList() throws SyntaxError {

		parseParaDecl();
		while (currentToken.kind == Token.COMMA) {
			accept();
			parseParaDecl();
		}
	}

	void parseParaDecl() throws SyntaxError {

		parseType();
		parseDeclarator();
	}

	void parseArgList() throws SyntaxError {

		match(Token.LPAREN);
		if (currentToken.kind != Token.RPAREN) {
			parseProperArgList();
		}

		if (currentToken.kind == Token.RPAREN) {
			accept();
		} else {
			syntacticError("Illegal arg list expression", currentToken.spelling);
		}

	}

	void parseProperArgList() throws SyntaxError {

		parseArg();
		while (currentToken.kind == Token.COMMA) {
			accept();
			parseArg();
		}
	}

	void parseArg() throws SyntaxError {

		parseExpr();
	}

	// ========================== LITERALS ========================

	// Call these methods rather than accept(). In Assignment 3,
	// literal AST nodes will be constructed inside these methods.

	void parseIntLiteral() throws SyntaxError {

		if (currentToken.kind == Token.INTLITERAL) {
			currentToken = scanner.getToken();
		} else
			syntacticError("integer literal expected here", "");
	}

	void parseFloatLiteral() throws SyntaxError {

		if (currentToken.kind == Token.FLOATLITERAL) {
			currentToken = scanner.getToken();
		} else
			syntacticError("float literal expected here", "");
	}

	void parseBooleanLiteral() throws SyntaxError {

		if (currentToken.kind == Token.BOOLEANLITERAL) {
			currentToken = scanner.getToken();
		} else
			syntacticError("boolean literal expected here", "");
	}

	void parseStringLiteral() throws SyntaxError {

		if (currentToken.kind == Token.STRINGLITERAL) {
			currentToken = scanner.getToken();
		} else
			syntacticError("String literal expected here", "");
	}
}
