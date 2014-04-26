/*
 * Parser.java            
 *
 * This parser for a subset of the VC language is intended to 
 *  demonstrate how to create the AST nodes, including (among others): 
 *  [1] a list (of statements)
 *  [2] a function
 *  [3] a statement (which is an expression statement), 
 *  [4] a unary expression
 *  [5] a binary expression
 *  [6] terminals (identifiers, integer literals and operators)
 *
 * In addition, it also demonstrates how to use the two methods start 
 * and finish to determine the position information for the start and 
 * end of a construct (known as a phrase) corresponding an AST node.
 *
 * NOTE THAT THE POSITION INFORMATION WILL NOT BE MARKED. HOWEVER, IT CAN BE
 * USEFUL TO DEBUG YOUR IMPLEMENTATION.
 *
 * (07-April-2014)


program       -> func-decl
func-decl     -> type identifier "(" ")" compound-stmt
type          -> void
identifier    -> ID
// statements
compound-stmt -> "{" stmt* "}" 
stmt          -> expr-stmt
expr-stmt     -> expr? ";"
// expressions 
expr                -> additive-expr
additive-expr       -> multiplicative-expr
                    |  additive-expr "+" multiplicative-expr
                    |  additive-expr "-" multiplicative-expr
multiplicative-expr -> unary-expr
	            	|  multiplicative-expr "*" unary-expr
	            	|  multiplicative-expr "/" unary-expr
unary-expr          -> "-" unary-expr
		    		|  primary-expr

primary-expr        -> identifier
		 		    |  INTLITERAL
				    | "(" expr ")"
 */

package VC.Parser;

import VC.Scanner.Scanner;
import VC.Scanner.SourcePosition;
import VC.Scanner.Token;
import VC.ErrorReporter;
import VC.ASTs.*;

public class Parser {

	private Scanner scanner;
	private ErrorReporter errorReporter;
	private Token currentToken;
	private SourcePosition previousTokenPosition;
	private SourcePosition dummyPos = new SourcePosition();

	public Parser(Scanner lexer, ErrorReporter reporter) {
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
			List dlAST = parseDeclList();
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

	List parseDeclList() throws SyntaxError {
		List dlAST = null;
		Type tAST = null;
		Ident iAST = null;
		Decl dAST = null;
		// SourcePosition declPos = new SourcePosition();
		if (currentToken.kind == Token.VOID
				|| currentToken.kind == Token.BOOLEAN
				|| currentToken.kind == Token.INT
				|| currentToken.kind == Token.FLOAT) {
			tAST = parseType();
			iAST = parseIdent();
			switch (currentToken.kind) {
			case Token.LPAREN:
				// dlAST = parseFuncDeclList(tAST, iAST);
				SourcePosition funcPos = new SourcePosition();
				start(funcPos);

				dAST = parseFuncDecl(tAST, iAST, funcPos);

				if (currentToken.kind == Token.VOID
						|| currentToken.kind == Token.BOOLEAN
						|| currentToken.kind == Token.INT
						|| currentToken.kind == Token.FLOAT) {
					// tAST = parseType();
					// iAST = parseIdent();
					dlAST = parseDeclList();
					finish(funcPos);
					dlAST = new DeclList(dAST, dlAST, funcPos);
				} else if (dAST != null) {
					finish(funcPos);
					dlAST = new DeclList(dAST, new EmptyDeclList(dummyPos),
							funcPos);
				}

				break;
			case Token.LBRACKET:
			case Token.EQ:
			case Token.COMMA:
				// dlAST = parseGlobalVarDeclList();
				SourcePosition declVarPos = new SourcePosition();
				start(declVarPos);

				dAST = parseGlobalVarDecl(tAST, iAST, declVarPos);

				if (currentToken.kind == Token.VOID
						|| currentToken.kind == Token.BOOLEAN
						|| currentToken.kind == Token.INT
						|| currentToken.kind == Token.FLOAT) {
					// tAST = parseType();
					// iAST = parseIdent();
					dlAST = parseDeclList();
					finish(declVarPos);
					dlAST = new DeclList(dAST, dlAST, declVarPos);
				} else if (currentToken.kind == Token.COMMA && dAST != null) {
					dlAST = parseMultiGlobalVarDecl(dAST.T);
					finish(declVarPos);
					dlAST = new DeclList(dAST, dlAST, declVarPos);
				} else {
					finish(declVarPos);
					dlAST = new DeclList(dAST, new EmptyDeclList(dummyPos),
							declVarPos);
				}

				break;
			case Token.SEMICOLON:
				accept();
				break;
			default:
				syntacticError("Wrong result type for a program",
						currentToken.spelling);
			}
		} else {
			dlAST = new EmptyDeclList(dummyPos);
		}

		return dlAST;
	}

	List parseFuncDeclList(Type tAST, Ident iAST) throws SyntaxError {
		List dlAST = null;
		Decl dAST = null;

		SourcePosition funcPos = new SourcePosition();
		start(funcPos);

		dAST = parseFuncDecl(tAST, iAST, funcPos);

		if (currentToken.kind == Token.VOID
				|| currentToken.kind == Token.BOOLEAN
				|| currentToken.kind == Token.INT
				|| currentToken.kind == Token.FLOAT) {
			tAST = parseType();
			iAST = parseIdent();
			dlAST = parseFuncDeclList(tAST, iAST);
			finish(funcPos);
			dlAST = new DeclList(dAST, dlAST, funcPos);
		} else if (dAST != null) {
			finish(funcPos);
			dlAST = new DeclList(dAST, new EmptyDeclList(dummyPos), funcPos);
		}
		if (dlAST == null)
			dlAST = new EmptyDeclList(dummyPos);

		return dlAST;
	}

	Decl parseFuncDecl(Type tAST, Ident iAST, SourcePosition pos)
			throws SyntaxError {

		Decl fAST = null;

		SourcePosition funcPos = new SourcePosition();
		copyStart(pos, funcPos);
		// start(funcPos);

		// Type tAST = parseType();
		// Ident iAST = parseIdent();
		List fplAST = parseParaList();
		Stmt cAST = parseCompoundStmt();
		finish(funcPos);
		fAST = new FuncDecl(tAST, iAST, fplAST, cAST, funcPos);
		return fAST;
	}

	List parseGlobalVarDeclList(Type tAST, Ident iAST) throws SyntaxError {

		List dlAST = null;
		SourcePosition declPos = new SourcePosition();
		start(declPos);

		if (currentToken.kind == Token.VOID
				|| currentToken.kind == Token.BOOLEAN
				|| currentToken.kind == Token.INT
				|| currentToken.kind == Token.FLOAT) {
			Decl dAST = parseGlobalVarDecl(tAST, iAST, declPos);

			if (currentToken.kind == Token.VOID
					|| currentToken.kind == Token.BOOLEAN
					|| currentToken.kind == Token.INT
					|| currentToken.kind == Token.FLOAT) {
				tAST = parseType();
				iAST = parseIdent();
				dlAST = parseGlobalVarDeclList(tAST, iAST);
				finish(declPos);
				dlAST = new DeclList(dAST, dlAST, declPos);
			} else if (currentToken.kind == Token.COMMA && dAST != null) {
				dlAST = parseMultiGlobalVarDecl(dAST.T);
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

	Decl parseGlobalVarDecl(Type tAST, Ident iAST, SourcePosition pos)
			throws SyntaxError {

		Decl gvarAST = null;
		// Type tAST = null;
		// Ident iAST = null;
		Expr eAST = null;

		SourcePosition declPos = new SourcePosition();
		copyStart(pos, declPos);
		// start(declPos);
		// tAST = parseType();
		// iAST = parseIdent();

		if (currentToken.kind == Token.LBRACKET) {
			// array decl
			accept();
			Expr dAST = parseExpr();
			match(Token.RBRACKET);
			tAST = new ArrayType(tAST, dAST, declPos);

			if (currentToken.kind == Token.EQ) {
				List exprlAST = parseArrayVarInitList();
				eAST = new InitExpr(exprlAST, declPos);
				finish(declPos);
				gvarAST = new GlobalVarDecl(tAST, iAST, eAST, declPos);
			} else {
				finish(declPos);
				gvarAST = new GlobalVarDecl(tAST, iAST,
						new EmptyExpr(dummyPos), declPos);
			}
		} else {
			if (currentToken.kind == Token.EQ) {
				// var init
				eAST = parseVarExprInit();
				finish(declPos);
				gvarAST = new GlobalVarDecl(tAST, iAST, eAST, declPos);
			} else {
				gvarAST = new GlobalVarDecl(tAST, iAST,
						new EmptyExpr(dummyPos), declPos);
			}
		}

		if (currentToken.kind == Token.SEMICOLON) {
			// single var decl
			match(Token.SEMICOLON);
		}
		return gvarAST;
	}

	Decl parseGlobalVarDecl(Type tAST) throws SyntaxError {

		Decl gvarAST = null;
		Ident iAST = null;
		Expr eAST = null;

		if (tAST != null) {
			SourcePosition declPos = new SourcePosition();
			start(declPos);

			iAST = parseIdent();

			if (currentToken.kind == Token.LBRACKET) {
				// array decl
				accept();
				Expr dAST = parseExpr();
				match(Token.RBRACKET);
				tAST = new ArrayType(tAST, dAST, declPos);

				if (currentToken.kind == Token.EQ) {
					List exprlAST = parseArrayVarInitList();
					eAST = new InitExpr(exprlAST, declPos);
					finish(declPos);
					gvarAST = new GlobalVarDecl(tAST, iAST, eAST, declPos);
				} else {
					finish(declPos);
					gvarAST = new GlobalVarDecl(tAST, iAST, new EmptyExpr(
							dummyPos), declPos);
				}
			} else {
				if (currentToken.kind == Token.EQ) {
					// var init
					eAST = parseVarExprInit();
					finish(declPos);
					gvarAST = new GlobalVarDecl(tAST, iAST, eAST, declPos);
				} else {
					gvarAST = new GlobalVarDecl(tAST, iAST, new EmptyExpr(
							dummyPos), declPos);
				}
			}

			if (currentToken.kind == Token.SEMICOLON) {
				// single var decl
				match(Token.SEMICOLON);
			}
			return gvarAST;
		}
		return null;
	}

	List parseMultiGlobalVarDecl(Type tAST) throws SyntaxError {

		List dlAST = null;
		SourcePosition declPos = new SourcePosition();

		if (currentToken.kind == Token.COMMA && tAST != null) {
			accept();
			start(declPos);
			Decl dAST = parseGlobalVarDecl(tAST);

			if (currentToken.kind == Token.COMMA) {
				dlAST = parseMultiGlobalVarDecl(tAST);
				finish(declPos);
				dlAST = new DeclList(dAST, dlAST, declPos);
			} else {
				finish(declPos);
				dlAST = new DeclList(dAST, dlAST, declPos);
			}
		} else {
			dlAST = new EmptyDeclList(declPos);
		}

		return dlAST;
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
			} else if (currentToken.kind == Token.COMMA && dAST != null) {
				dlAST = parseMultiLocalVarDecl(dAST.T);
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

	List parseMultiLocalVarDecl(Type tAST) throws SyntaxError {

		List dlAST = null;
		SourcePosition declPos = new SourcePosition();

		if (currentToken.kind == Token.COMMA && tAST != null) {
			accept();
			start(declPos);
			Decl dAST = parseLocalVarDecl(tAST);

			if (currentToken.kind == Token.COMMA) {
				dlAST = parseMultiLocalVarDecl(tAST);
				finish(declPos);
				dlAST = new DeclList(dAST, dlAST, declPos);
			} else {
				finish(declPos);
				dlAST = new DeclList(dAST, dlAST, declPos);
			}
		} else {
			dlAST = new EmptyDeclList(declPos);
		}

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
					List exprlAST = parseArrayVarInitList();
					eAST = new InitExpr(exprlAST, declPos);
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

			if (currentToken.kind == Token.SEMICOLON) {
				// single var decl
				match(Token.SEMICOLON);
			}
			return lvarAST;
		}
		return null;
	}

	Decl parseLocalVarDecl(Type tAST) throws SyntaxError {
		Decl lvarAST = null;
		Ident iAST = null;
		Expr eAST = null;

		if (tAST != null) {
			SourcePosition declPos = new SourcePosition();
			start(declPos);

			iAST = parseIdent();

			if (currentToken.kind == Token.LBRACKET) {
				// array decl
				accept();
				Expr dAST = parseExpr();
				match(Token.RBRACKET);
				tAST = new ArrayType(tAST, dAST, declPos);

				if (currentToken.kind == Token.EQ) {
					List exprlAST = parseArrayVarInitList();
					eAST = new InitExpr(exprlAST, declPos);
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

			if (currentToken.kind == Token.SEMICOLON) {
				// single var decl
				match(Token.SEMICOLON);
			}
			return lvarAST;
		}
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

	// List parseInitDeclList() throws SyntaxError {
	//
	// List dlAST = null;
	// SourcePosition initDeclPos = new SourcePosition();
	// start(initDeclPos);
	//
	// parseInitDecl();
	// while (currentToken.kind == Token.COMMA) {
	// accept();
	// parseInitDecl();
	// }
	//
	// return dlAST;
	// }
	//
	// void parseInitDecl() throws SyntaxError {
	// if (currentToken.kind != Token.EQ) {
	// parseDeclarator();
	// }
	// if (currentToken.kind == Token.EQ) {
	// accept();
	// parseInitialiser();
	// }
	// }

	//
	// Expr parseInitialiser() throws SyntaxError {
	//
	// Expr iAST = null;
	//
	// if (currentToken.kind == Token.LCURLY) {
	//
	// } else {
	// iAST = parseExpr();
	// }
	//
	// return iAST;
	// }
	//
	// Expr parseInitExprList() throws SyntaxError {
	// ExprList iAST = null;
	// Expr eAST = null;
	//
	// SourcePosition ePos = new SourcePosition();
	// start(ePos);
	//
	// accept();
	// eAST = parseExpr();
	//
	// List elAST = parseExprList();
	//
	// match(Token.RCURLY);
	//
	// iAST = new ExprList(eAST, elAST, ePos);
	//
	// return null;
	// // return iAST;
	// }
	//
	// List parseExprList() throws SyntaxError {
	//
	// List elAST = null;
	// Expr eAST = null;
	//
	// SourcePosition exprPos = new SourcePosition();
	// start(exprPos);
	//
	// eAST = parseExpr();
	//
	// if (currentToken.kind == Token.COMMA) {
	//
	// } else if (eAST != null) {
	// finish(exprPos);
	// elAST = new ExprList(eAST, new EmptyExprList(dummyPos), exprPos);
	// } else {
	// elAST = new EmptyExprList(dummyPos);
	// }
	//
	// return elAST;
	// }

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

	// ======================= STATEMENTS ==============================

	Stmt parseCompoundStmt() throws SyntaxError {
		Stmt cAST = null;

		SourcePosition stmtPos = new SourcePosition();
		start(stmtPos);

		match(Token.LCURLY);

		// Insert code here to build a DeclList node for variable declarations
		List dlAST = parseLocalVarDeclList();
		List slAST = parseStmtList();
		match(Token.RCURLY);
		finish(stmtPos);

		/*
		 * In the subset of the VC grammar, no variable declarations are
		 * allowed. Therefore, a block is empty iff it has no statements.
		 */
		if (slAST instanceof EmptyStmtList)
			cAST = new EmptyCompStmt(stmtPos);
		else
			cAST = new CompoundStmt(dlAST, slAST, stmtPos);
		return cAST;
	}

	List parseStmtList() throws SyntaxError {
		List slAST = null;

		SourcePosition stmtPos = new SourcePosition();
		start(stmtPos);

		if (currentToken.kind != Token.RCURLY) {
			Stmt sAST = parseStmt();
			{
				if (currentToken.kind != Token.RCURLY) {
					slAST = parseStmtList();
					finish(stmtPos);
					slAST = new StmtList(sAST, slAST, stmtPos);
				} else {
					finish(stmtPos);
					slAST = new StmtList(sAST, new EmptyStmtList(dummyPos),
							stmtPos);
				}
			}
		} else
			slAST = new EmptyStmtList(dummyPos);

		return slAST;
	}

	Stmt parseStmt() throws SyntaxError {
		Stmt sAST = null;

		switch (currentToken.kind) {
		case Token.IF:
			sAST = parseIFStmt();
			break;
		case Token.FOR:
			sAST = parseFORStmt();
			break;
		case Token.WHILE:
			sAST = parseWHILEStmt();
			break;
		case Token.BREAK:
			sAST = parseBreakStmt();
			break;
		case Token.CONTINUE:
			sAST = parseContinueStmt();
			break;
		case Token.RETURN:
			sAST = parseReturnStmt();
			break;
		case Token.LCURLY:
			sAST = parseCompoundStmt();
			break;
		default:
			sAST = parseExprStmt();
			break;
		}

		return sAST;
	}

	Stmt parseIFStmt() throws SyntaxError {
		Stmt sAST = null;
		Expr eAST = null;
		Stmt s1AST = null;
		Stmt s2AST = null;
		SourcePosition stmtPos = new SourcePosition();

		start(stmtPos);
		accept();
		match(Token.LPAREN);
		eAST = parseExpr();
		match(Token.RPAREN);
		s1AST = parseStmt();
		if (currentToken.kind == Token.ELSE) {
			accept();
			s2AST = parseStmt();
			finish(stmtPos);
			sAST = new IfStmt(eAST, s1AST, s2AST, stmtPos);
		} else {
			finish(stmtPos);
			sAST = new IfStmt(eAST, s1AST, stmtPos);
		}

		return sAST;
	}

	Stmt parseFORStmt() throws SyntaxError {
		Stmt sAST = null;
		Expr e1AST = null;
		Expr e2AST = null;
		Expr e3AST = null;
		Stmt ssAST = null;
		SourcePosition stmtPos = new SourcePosition();

		start(stmtPos);
		accept();
		match(Token.LPAREN);
		if (currentToken.kind != Token.SEMICOLON) {
			e1AST = parseExpr();
		} else {
			e1AST = new EmptyExpr(stmtPos);
		}

		match(Token.SEMICOLON);

		if (currentToken.kind != Token.SEMICOLON) {
			e2AST = parseExpr();
		} else {
			e2AST = new EmptyExpr(stmtPos);
		}

		match(Token.SEMICOLON);

		if (currentToken.kind != Token.RPAREN) {
			e3AST = parseExpr();
		} else {
			e3AST = new EmptyExpr(stmtPos);
		}

		match(Token.RPAREN);
		ssAST = parseStmt();

		finish(stmtPos);
		sAST = new ForStmt(e1AST, e2AST, e3AST, ssAST, stmtPos);

		return sAST;
	}

	Stmt parseWHILEStmt() throws SyntaxError {
		Stmt sAST = null;

		SourcePosition stmtPos = new SourcePosition();
		start(stmtPos);

		accept();
		match(Token.LPAREN);
		Expr eAST = parseExpr();
		match(Token.RPAREN);
		Stmt ssAST = parseStmt();

		finish(stmtPos);
		sAST = new WhileStmt(eAST, ssAST, stmtPos);

		return sAST;
	}

	Stmt parseBreakStmt() throws SyntaxError {
		Stmt sAST = null;

		SourcePosition stmtPos = new SourcePosition();
		start(stmtPos);

		match(Token.BREAK);
		match(Token.SEMICOLON);
		finish(stmtPos);
		sAST = new BreakStmt(stmtPos);

		return sAST;
	}

	Stmt parseContinueStmt() throws SyntaxError {
		Stmt sAST = null;

		SourcePosition stmtPos = new SourcePosition();
		start(stmtPos);
		match(Token.CONTINUE);
		match(Token.SEMICOLON);
		finish(stmtPos);
		sAST = new ContinueStmt(stmtPos);
		return sAST;
	}

	Stmt parseReturnStmt() throws SyntaxError {

		Stmt sAST = null;

		SourcePosition stmtPos = new SourcePosition();
		start(stmtPos);

		match(Token.RETURN);
		if (currentToken.kind != Token.SEMICOLON) {
			Expr eAST = parseExpr();
			match(Token.SEMICOLON);
			finish(stmtPos);
			sAST = new ReturnStmt(eAST, stmtPos);
		} else {
			match(Token.SEMICOLON);
			finish(stmtPos);
			sAST = new ReturnStmt(new EmptyExpr(dummyPos), stmtPos);
		}

		return sAST;
	}

	Stmt parseExprStmt() throws SyntaxError {
		Stmt sAST = null;

		SourcePosition stmtPos = new SourcePosition();
		start(stmtPos);

		if (currentToken.kind == Token.ID
				|| currentToken.kind == Token.INTLITERAL
				|| currentToken.kind == Token.LPAREN) {
			Expr eAST = parseExpr();
			match(Token.SEMICOLON);
			finish(stmtPos);
			sAST = new ExprStmt(eAST, stmtPos);
		} else {
			match(Token.SEMICOLON);
			finish(stmtPos);
			sAST = new ExprStmt(new EmptyExpr(dummyPos), stmtPos);
		}
		return sAST;
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
		List plAST = null;

		match(Token.LPAREN);
		if (currentToken.kind != Token.RPAREN) {
			plAST = parseProperParaList();
		}

		match(Token.RPAREN);

		return plAST;
	}

	List parseProperParaList() throws SyntaxError {

		List plAST = null;
		ParaDecl pAST = null;
		SourcePosition paraPos = new SourcePosition();
		start(paraPos);
		pAST = parseParaDecl();

		if (currentToken.kind == Token.COMMA) {
			accept();
			plAST = parseProperParaList();
			finish(paraPos);
			plAST = new ParaList(pAST, plAST, paraPos);
		} else if (pAST != null) {
			finish(paraPos);
			plAST = new ParaList(pAST, new EmptyParaList(dummyPos), paraPos);
		}

		if (plAST == null) {
			plAST = new EmptyParaList(dummyPos);
		}
		return plAST;
	}

	ParaDecl parseParaDecl() throws SyntaxError {

		ParaDecl paraAST = null;
		Ident iAST = null;
		Type tAST = null;
		SourcePosition paraPos = new SourcePosition();

		start(paraPos);
		tAST = parseType();

		if (currentToken.kind == Token.ID) {
			iAST = parseIdent();
		} else {
			syntacticError(
					"Illegal declarator expression, identifier expected here",
					currentToken.spelling);
		}

		if (currentToken.kind == Token.LBRACKET) {
			// array decl
			accept();
			Expr dAST = parseExpr();
			match(Token.RBRACKET);
			finish(paraPos);
			tAST = new ArrayType(tAST, dAST, paraPos);
			paraAST = new ParaDecl(tAST, iAST, paraPos);
		} else {
			finish(paraPos);
			paraAST = new ParaDecl(tAST, iAST, paraPos);
		}

		return paraAST;
	}

	List parseArgList() throws SyntaxError {

		List alAST = null;

		match(Token.LPAREN);
		if (currentToken.kind != Token.RPAREN) {
			alAST = parseProperArgList();
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
