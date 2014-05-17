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
    private boolean flag1;
    private boolean flag2;
    private boolean flag3;
    private Token temptoken;
    private Type temptype;

    public Parser(Scanner lexer, ErrorReporter reporter) {
        scanner = lexer;
        errorReporter = reporter;

        previousTokenPosition = new SourcePosition();

        currentToken = scanner.getToken();
        flag1 = false;
        flag2 = false;
        flag3 = false;
        temptype = null;
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
            List dlAST = parseFuncDeclList();
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

    List parseFuncDeclList() throws SyntaxError {
        List dlAST = null;
        Decl dAST = null;

        SourcePosition funcPos = new SourcePosition();
        start(funcPos);
        if (currentToken.kind == Token.EOF) {
            finish(funcPos);
            return new EmptyDeclList(dummyPos);
        }

        dAST = parseFuncDecl();

        if (currentToken.kind == Token.VOID || currentToken.kind == Token.INT
                || currentToken.kind == Token.FLOAT
                || currentToken.kind == Token.BOOLEAN
                || (currentToken.kind == Token.COMMA && flag1 == true)) {
            dlAST = parseFuncDeclList();
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

    Decl parseFuncDecl() throws SyntaxError {// global variable included here

        Decl fAST = null;
        flag3 = true;
        SourcePosition funcPos = new SourcePosition();
        start(funcPos);
        if (currentToken.kind == Token.INT || currentToken.kind == Token.VOID
                || currentToken.kind == Token.FLOAT
                || currentToken.kind == Token.BOOLEAN) {
            Type tAST = parseType();
            temptype = tAST;
            // if(currentToken.kind != Token.VOID) {
            temptoken = scanner.getToken();
            flag2 = true;
            if (temptoken.kind == Token.LPAREN) {
                Ident iAST = parseIdent();
                List fplAST = parseParaList();
                Stmt cAST = parseCompoundStmt();
                finish(funcPos);
                fAST = new FuncDecl(tAST, iAST, fplAST, cAST, funcPos);
            } else {
                Ident iAST = parseIdent();
                Expr eAST = null;
                if (currentToken.kind == Token.LBRACKET)
                    fAST = parseArray(tAST, iAST, eAST, funcPos);

                else if (currentToken.kind == Token.EQ) {
                    accept();
                    if (currentToken.kind == Token.LCURLY) {
                        accept();
                        eAST = parseInitExpr();
                        match(Token.RCURLY);
                        finish(funcPos);
                        fAST = new GlobalVarDecl(tAST, iAST, eAST, funcPos);
                    } else {
                        eAST = parseExpr();
                        finish(funcPos);
                        fAST = new GlobalVarDecl(tAST, iAST, eAST, funcPos);
                    }
                } else {
                    finish(funcPos);
                    fAST = new GlobalVarDecl(tAST, iAST,
                            new EmptyExpr(dummyPos), funcPos);
                }
                if (currentToken.kind == Token.COMMA) {
                    flag1 = true;
                } else {
                    match(Token.SEMICOLON);

                }

            }

            // }
            /*
             * else { Ident iAST = parseIdent(); List fplAST = parseParaList();
             * Stmt cAST = parseCompoundStmt(); finish(funcPos); fAST = new
             * FuncDecl(tAST, iAST, fplAST, cAST, funcPos); }
             */
        } else if (currentToken.kind == Token.COMMA && flag1 == true) {
            accept();
            Type tAST = temptype;
            Ident iAST = parseIdent();
            Expr eAST = null;
            if (currentToken.kind == Token.LBRACKET)
                fAST = parseArray(tAST, iAST, eAST, funcPos);
            else if (currentToken.kind == Token.EQ) {
                accept();
                if (currentToken.kind == Token.LCURLY) {
                    accept();
                    eAST = parseInitExpr();
                    match(Token.RCURLY);
                    finish(funcPos);
                    fAST = new GlobalVarDecl(tAST, iAST, eAST, funcPos);
                } else {
                    eAST = parseExpr();
                    finish(funcPos);
                    fAST = new GlobalVarDecl(tAST, iAST, eAST, funcPos);
                }
            } else {
                finish(funcPos);
                fAST = new GlobalVarDecl(tAST, iAST, new EmptyExpr(dummyPos),
                        funcPos);
            }
            if (currentToken.kind == Token.COMMA) {
                flag1 = true;
            } else {
                match(Token.SEMICOLON);
                flag1 = false;
            }

        } else
            syntacticError("type expected here", currentToken.spelling);
        flag3 = false;
        return fAST;
    }

    Decl parseArray(Type tAST, Ident iAST, Expr eAST, SourcePosition funcPos)
            throws SyntaxError {
        Decl fAST = null;
        accept();
        ArrayType atAST = null;
        Expr ieAST = null;
        IntLiteral ilAST = null;
        if (currentToken.kind == Token.INTLITERAL)
            ilAST = parseIntLiteral();
        match(Token.RBRACKET);
        if (ilAST == null)
            ieAST = new EmptyExpr(dummyPos);
        else
            ieAST = new IntExpr(ilAST, currentToken.position);
        atAST = new ArrayType(tAST, ieAST, tAST.position);
        if (currentToken.kind == Token.EQ) {
            accept();
            if (currentToken.kind == Token.LCURLY) {
                accept();
                eAST = parseInitExpr();
                match(Token.RCURLY);
                finish(funcPos);
                if (flag3 == true)
                    fAST = new GlobalVarDecl(atAST, iAST, eAST, funcPos);
                else
                    fAST = new LocalVarDecl(atAST, iAST, eAST, funcPos);
            } else {
                eAST = parseExpr();
                finish(funcPos);
                if (flag3 == true)
                    fAST = new GlobalVarDecl(atAST, iAST, eAST, funcPos);
                else
                    fAST = new LocalVarDecl(atAST, iAST, eAST, funcPos);
            }
        } else {
            finish(funcPos);
            if (flag3 == true)
                fAST = new GlobalVarDecl(atAST, iAST, new EmptyExpr(dummyPos),
                        funcPos);
            else
                fAST = new LocalVarDecl(atAST, iAST, new EmptyExpr(dummyPos),
                        funcPos);
        }

        return fAST;
    }

    List parseVarDeclList() throws SyntaxError {
        List dlAST = null;
        Decl dAST = null;
        SourcePosition funcPos = new SourcePosition();
        start(funcPos);
        if (currentToken.kind == Token.INT
                || currentToken.kind == Token.BOOLEAN
                || currentToken.kind == Token.FLOAT
                || (currentToken.kind == Token.COMMA && flag1 == true))
            dAST = parseVarDecl();
        if (currentToken.kind == Token.INT
                || currentToken.kind == Token.BOOLEAN
                || currentToken.kind == Token.FLOAT
                || (currentToken.kind == Token.COMMA && flag1 == true)) {
            dlAST = parseVarDeclList();
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

    Decl parseVarDecl() throws SyntaxError {
        Decl dAST = null;
        SourcePosition funcPos = new SourcePosition();
        start(funcPos);
        if (currentToken.kind != Token.COMMA) {
            Type tAST = parseType();
            Ident iAST = parseIdent();
            temptype = tAST;
            Expr eAST = null;
            if (currentToken.kind == Token.LBRACKET)
                dAST = parseArray(tAST, iAST, eAST, funcPos);

            else if (currentToken.kind == Token.EQ) {
                accept();
                if (currentToken.kind == Token.LCURLY) {
                    accept();
                    eAST = parseInitExpr();
                    match(Token.RCURLY);
                    finish(funcPos);
                    dAST = new LocalVarDecl(tAST, iAST, eAST, funcPos);
                } else {
                    eAST = parseExpr();
                    finish(funcPos);
                    dAST = new LocalVarDecl(tAST, iAST, eAST, funcPos);
                }
            } else {
                finish(funcPos);
                dAST = new LocalVarDecl(tAST, iAST, new EmptyExpr(dummyPos),
                        funcPos);
            }
            if (currentToken.kind == Token.COMMA) {
                flag1 = true;
            } else {
                match(Token.SEMICOLON);

            }
        } else {
            accept();
            Type tAST = temptype;
            Ident iAST = parseIdent();
            Expr eAST = null;
            if (currentToken.kind == Token.LBRACKET)
                dAST = parseArray(tAST, iAST, eAST, funcPos);
            else if (currentToken.kind == Token.EQ) {
                accept();
                eAST = parseExpr();
                finish(funcPos);
                dAST = new LocalVarDecl(tAST, iAST, eAST, funcPos);
            } else {
                finish(funcPos);
                dAST = new LocalVarDecl(tAST, iAST, new EmptyExpr(dummyPos),
                        funcPos);
            }
            if (currentToken.kind == Token.COMMA) {
                flag1 = true;
            } else {
                match(Token.SEMICOLON);
                flag1 = false;
            }
        }
        return dAST;
    }

    // ======================== TYPES ==========================

    Type parseType() throws SyntaxError {
        Type typeAST = null;

        SourcePosition typePos = new SourcePosition();
        start(typePos);
        switch (currentToken.kind) {
        case Token.INT:
            accept();
            finish(typePos);
            typeAST = new IntType(typePos);
            break;
        case Token.VOID:
            accept();
            finish(typePos);
            typeAST = new VoidType(typePos);
            break;
        case Token.FLOAT:
            accept();
            finish(typePos);
            typeAST = new FloatType(typePos);
            break;
        case Token.BOOLEAN:
            accept();
            finish(typePos);
            typeAST = new BooleanType(typePos);
            break;
        default:
            syntacticError("type expected here", currentToken.spelling);
            break;
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
        List dlAST = null;
        List slAST = null;
        if (currentToken.kind != Token.RCURLY)
            dlAST = parseVarDeclList();
        if (currentToken.kind != Token.RCURLY)
            slAST = parseStmtList();
        match(Token.RCURLY);
        finish(stmtPos);

        /*
         * In the subset of the VC grammar, no variable declarations are
         * allowed. Therefore, a block is empty if it has no statements.
         */
        if (dlAST == null && slAST == null) {
            cAST = new EmptyCompStmt(stmtPos);
            return cAST;
        }
        if (dlAST == null)
            dlAST = new EmptyDeclList(dummyPos);
        if (slAST == null)
            slAST = new EmptyStmtList(dummyPos);

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
        Stmt sAST = new EmptyStmt(dummyPos);
        switch (currentToken.kind) {

        case Token.CONTINUE:
            sAST = parseContinueStmt();
            break;
        case Token.IF:
            sAST = parseIfStmt();
            break;
        case Token.FOR:
            sAST = parseForStmt();
            break;
        case Token.WHILE:
            sAST = parseWhileStmt();
            break;
        case Token.BREAK:
            sAST = parseBreakStmt();
            break;
        case Token.RETURN:
            sAST = parseRetrunStmt();
            break;
        case Token.LCURLY:
            sAST = parseCompoundStmt();
            break;
        case Token.RCURLY:
            break;

        default:

            sAST = parseExprStmt();
            break;
        }

        return sAST;
    }

    Stmt parseExprStmt() throws SyntaxError {
        Stmt sAST = null;
        Expr eAST = new EmptyExpr(dummyPos);
        SourcePosition stmtPos = new SourcePosition();
        start(stmtPos);
        if (currentToken.kind != Token.SEMICOLON)
            eAST = parseExpr();
        match(Token.SEMICOLON);
        finish(stmtPos);
        sAST = new ExprStmt(eAST, stmtPos);
        return sAST;
    }

    Stmt parseIfStmt() throws SyntaxError {
        Stmt sAST = null;
        Expr eAST = null;
        Stmt sAST1 = null;
        SourcePosition stmtPos = new SourcePosition();
        start(stmtPos);
        accept();
        match(Token.LPAREN);
        eAST = parseExpr();
        match(Token.RPAREN);
        sAST1 = parseStmt();
        if (currentToken.kind == Token.ELSE) {
            accept();
            Stmt sAST2 = parseStmt();
            finish(stmtPos);
            sAST = new IfStmt(eAST, sAST1, sAST2, stmtPos);
            return sAST;
        }
        finish(stmtPos);
        sAST = new IfStmt(eAST, sAST1, stmtPos);
        return sAST;

    }

    Stmt parseWhileStmt() throws SyntaxError {
        Stmt sAST = null;
        Stmt sAST1 = null;
        Expr eAST = null;
        SourcePosition stmtPos = new SourcePosition();
        start(stmtPos);
        accept();
        match(Token.LPAREN);
        eAST = parseExpr();
        match(Token.RPAREN);
        sAST1 = parseStmt();
        finish(stmtPos);
        sAST = new WhileStmt(eAST, sAST1, stmtPos);
        return sAST;
    }

    Stmt parseBreakStmt() throws SyntaxError {
        Stmt sAST = null;
        SourcePosition stmtPos = new SourcePosition();
        start(stmtPos);
        accept();
        finish(stmtPos);
        match(Token.SEMICOLON);
        sAST = new BreakStmt(stmtPos);
        return sAST;
    }

    Stmt parseRetrunStmt() throws SyntaxError {
        Stmt sAST = null;
        Expr eAST = new EmptyExpr(dummyPos);
        SourcePosition stmtPos = new SourcePosition();
        start(stmtPos);
        accept();
        if (currentToken.kind != Token.SEMICOLON)
            eAST = parseExpr();
        match(Token.SEMICOLON);
        finish(stmtPos);
        sAST = new ReturnStmt(eAST, stmtPos);
        return sAST;

    }

    Stmt parseForStmt() throws SyntaxError {
        Stmt sAST = null;
        Expr eAST1 = new EmptyExpr(dummyPos);
        Expr eAST2 = new EmptyExpr(dummyPos);
        Expr eAST3 = new EmptyExpr(dummyPos);
        Stmt sAST1 = null;
        SourcePosition stmtPos = new SourcePosition();
        start(stmtPos);
        accept();
        match(Token.LPAREN);
        if (currentToken.kind != Token.SEMICOLON)
            eAST1 = parseExpr();
        match(Token.SEMICOLON);
        if (currentToken.kind != Token.SEMICOLON)
            eAST2 = parseExpr();
        match(Token.SEMICOLON);
        if (currentToken.kind != Token.RPAREN)
            eAST3 = parseExpr();
        match(Token.RPAREN);
        sAST1 = parseStmt();
        finish(stmtPos);
        sAST = new ForStmt(eAST1, eAST2, eAST3, sAST1, stmtPos);
        return sAST;
    }

    Stmt parseContinueStmt() throws SyntaxError {
        Stmt sAST = null;
        SourcePosition stmtPos = new SourcePosition();
        start(stmtPos);
        accept();
        match(Token.SEMICOLON);
        finish(stmtPos);
        sAST = new ContinueStmt(stmtPos);
        return sAST;

    }

    // ======================= PARAMETERS =======================

    List parseParaList() throws SyntaxError {
        List formalsAST = null;

        SourcePosition formalsPos = new SourcePosition();
        start(formalsPos);
        match(Token.LPAREN);
        if (currentToken.kind != Token.RPAREN) {
            formalsAST = parseProperParaList();
            finish(formalsPos);
            match(Token.RPAREN);

        } else {
            match(Token.RPAREN);
            finish(formalsPos);
            formalsAST = new EmptyParaList(formalsPos);
        }

        return formalsAST;
    }

    List parseProperParaList() throws SyntaxError {
        List formalsAST = null;
        ParaDecl dAST = null;
        SourcePosition exprPos = new SourcePosition();
        start(exprPos);
        dAST = parseParaDecl();
        if (currentToken.kind == Token.COMMA) {
            accept();
            List lAST1 = parseProperParaList();
            finish(exprPos);
            formalsAST = new ParaList(dAST, lAST1, exprPos);
        } else {
            finish(exprPos);
            formalsAST = new ParaList(dAST, new EmptyParaList(dummyPos),
                    exprPos);
        }
        return formalsAST;
    }

    ParaDecl parseParaDecl() throws SyntaxError {
        ParaDecl dAST = null;
        SourcePosition paraPos = new SourcePosition();
        start(paraPos);
        if (currentToken.kind == Token.VOID || currentToken.kind == Token.INT
                || currentToken.kind == Token.FLOAT
                || currentToken.kind == Token.BOOLEAN) {
            Type tAST = parseType();
            Ident iAST = parseIdent();
            if (currentToken.kind == Token.LBRACKET) {
                accept();
                Expr ieAST = null;
                IntLiteral ilAST = null;
                if (currentToken.kind == Token.INTLITERAL) {
                    ilAST = parseIntLiteral();
                    match(Token.RBRACKET);
                    finish(paraPos);
                    ieAST = new IntExpr(ilAST, currentToken.position);
                    tAST = new ArrayType(tAST, ieAST, tAST.position);
                } else {
                    tAST = new ArrayType(tAST, new EmptyExpr(dummyPos),
                            tAST.position);
                    match(Token.RBRACKET);
                    finish(paraPos);
                }
            }
            dAST = new ParaDecl(tAST, iAST, paraPos);
        } else
            syntacticError("type expected here", currentToken.spelling);
        return dAST;
    }

    List parseArgList() throws SyntaxError {
        List lAST = null;
        match(Token.LPAREN);
        if (currentToken.kind != Token.RPAREN)
            lAST = parseProperArgList();
        else
            lAST = new EmptyArgList(dummyPos);
        match(Token.RPAREN);
        return lAST;
    }

    List parseProperArgList() throws SyntaxError {
        List lAST = null;
        List lAST1 = null;
        Arg aAST1 = null;
        SourcePosition paraPos = new SourcePosition();
        start(paraPos);
        aAST1 = parseArg();
        if (currentToken.kind == Token.COMMA) {
            accept();
            lAST1 = parseProperArgList();
        }
        finish(paraPos);
        if (lAST1 == null)
            lAST = new ArgList(aAST1, new EmptyArgList(dummyPos), paraPos);
        else
            lAST = new ArgList(aAST1, lAST1, paraPos);
        return lAST;
    }

    Arg parseArg() throws SyntaxError {
        Arg aAST = null;
        Expr eAST = null;
        SourcePosition paraPos = new SourcePosition();
        start(paraPos);
        eAST = parseExpr();
        finish(paraPos);
        if (eAST == null)
            eAST = new EmptyExpr(dummyPos);
        aAST = new Arg(eAST, paraPos);
        return aAST;
    }

    // ======================= EXPRESSIONS ======================

    Expr parseExpr() throws SyntaxError {
        Expr exprAST = null;
        exprAST = parseAssignExpr();
        if (exprAST == null)
            exprAST = new EmptyExpr(dummyPos);
        return exprAST;
    }

    Expr parseAssignExpr() throws SyntaxError {
        Expr aeAST = null;
        SourcePosition exprPos = new SourcePosition();
        start(exprPos);
        aeAST = parseCondOrExpr();
        if (currentToken.kind == Token.EQ) {
            accept();
            Expr eAST2 = parseAssignExpr();
            SourcePosition newPos = new SourcePosition();
            copyStart(exprPos, newPos);
            finish(newPos);
            if (eAST2 == null)
                eAST2 = new EmptyExpr(dummyPos);
            aeAST = new AssignExpr(aeAST, eAST2, newPos);
        }
        return aeAST;
    }

    Expr parseCondOrExpr() throws SyntaxError {
        Expr coAST = null;
        SourcePosition exprPos = new SourcePosition();
        start(exprPos);
        coAST = parseCondAndExpr();
        while (currentToken.kind == Token.OROR) {
            Operator oAST = acceptOperator();
            Expr bAST2 = parseCondAndExpr();
            SourcePosition newPos = new SourcePosition();
            copyStart(exprPos, newPos);
            finish(newPos);
            coAST = new BinaryExpr(coAST, oAST, bAST2, newPos);
        }
        return coAST;
    }

    Expr parseCondAndExpr() throws SyntaxError {
        Expr caAST = null;
        SourcePosition exprPos = new SourcePosition();
        start(exprPos);
        caAST = parseEqualityExpr();
        while (currentToken.kind == Token.ANDAND) {
            Operator oAST = acceptOperator();
            Expr bAST2 = parseEqualityExpr();
            SourcePosition newPos = new SourcePosition();
            copyStart(exprPos, newPos);
            finish(newPos);
            caAST = new BinaryExpr(caAST, oAST, bAST2, newPos);
        }
        return caAST;

    }

    Expr parseEqualityExpr() throws SyntaxError {
        Expr caAST = null;
        SourcePosition exprPos = new SourcePosition();
        start(exprPos);
        caAST = parseRelExpr();
        while (currentToken.kind == Token.EQEQ
                || currentToken.kind == Token.NOTEQ) {
            Operator oAST = acceptOperator();
            Expr bAST2 = parseRelExpr();
            SourcePosition newPos = new SourcePosition();
            copyStart(exprPos, newPos);
            finish(newPos);
            caAST = new BinaryExpr(caAST, oAST, bAST2, newPos);
        }
        return caAST;
    }

    Expr parseRelExpr() throws SyntaxError {
        Expr caAST = null;
        SourcePosition exprPos = new SourcePosition();
        start(exprPos);
        caAST = parseAdditiveExpr();
        while (currentToken.kind == Token.LT || currentToken.kind == Token.LTEQ
                || currentToken.kind == Token.GT
                || currentToken.kind == Token.GTEQ) {
            Operator oAST = acceptOperator();
            Expr bAST2 = parseAdditiveExpr();
            SourcePosition newPos = new SourcePosition();
            copyStart(exprPos, newPos);
            finish(newPos);
            caAST = new BinaryExpr(caAST, oAST, bAST2, newPos);
        }
        return caAST;
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
            Var simVAST = new SimpleVar(iAST, primPos);
            if (currentToken.kind == Token.LBRACKET) {
                accept();
                if (currentToken.kind != Token.RBRACKET) {
                    Expr eAST = parseExpr();
                    finish(primPos);
                    exprAST = new ArrayExpr(simVAST, eAST, primPos);
                    match(Token.RBRACKET);
                } else {
                    match(Token.RBRACKET);
                    finish(primPos);
                    exprAST = new ArrayExpr(simVAST, new EmptyExpr(dummyPos),
                            primPos);
                }
            } else if (currentToken.kind == Token.LPAREN) {
                List lAST = parseArgList();
                finish(primPos);
                exprAST = new CallExpr(iAST, lAST, primPos);
            } else {
                finish(primPos);
                exprAST = new VarExpr(simVAST, primPos);
            }
            break;

        case Token.LPAREN: {
            accept();
            exprAST = parseExpr();
            match(Token.RPAREN);
        }
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

    Expr parseInitExpr() throws SyntaxError {
        Expr exprAST = null;
        SourcePosition InitExprpos = new SourcePosition();
        start(InitExprpos);
        List ilAST = parseExprList();
        finish(InitExprpos);
        exprAST = new InitExpr(ilAST, InitExprpos);
        return exprAST;
    }

    List parseExprList() throws SyntaxError {
        List elAST = null;
        Expr exprAST = null;
        List ilAST = null;
        SourcePosition ExprListpos = new SourcePosition();
        start(ExprListpos);
        if (currentToken.kind != Token.RCURLY) {
            exprAST = parseExpr();
            if (currentToken.kind == Token.COMMA) {
                accept();
                ilAST = parseExprList();
                finish(ExprListpos);
                elAST = new ExprList(exprAST, ilAST, ExprListpos);
            } else {
                finish(ExprListpos);
                elAST = new ExprList(exprAST, new EmptyExprList(dummyPos),
                        ExprListpos);
            }
        } else {
            elAST = new EmptyExprList(dummyPos);
        }
        return elAST;
    }

    // ========================== ID, OPERATOR and LITERALS
    // ========================

    Ident parseIdent() throws SyntaxError {

        Ident I = null;
        if (flag2 == true) {
            if (currentToken.kind == Token.ID) {
                previousTokenPosition = currentToken.position;
                String spelling = currentToken.spelling;
                I = new Ident(spelling, previousTokenPosition);
                currentToken = temptoken;
                flag2 = false;
            } else
                syntacticError("identifier expected here", "");
        } else {
            if (currentToken.kind == Token.ID) {
                previousTokenPosition = currentToken.position;
                String spelling = currentToken.spelling;
                I = new Ident(spelling, previousTokenPosition);
                currentToken = scanner.getToken();
            } else
                syntacticError("identifier expected here", "");

        }
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
            syntacticError("integer literal expected here", "");
        return SL;
    }

}
