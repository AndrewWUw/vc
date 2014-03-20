/**
 **	Scanner.java                        
 **/

package VC.Scanner;

import java.util.ArrayList;
import java.util.List;

import VC.ErrorReporter;

public final class Scanner {

	private SourceFile sourceFile;
	private boolean debug;

	private ErrorReporter errorReporter;
	private StringBuffer currentSpelling;
	private char currentChar;
	private SourcePosition sourcePos;
	private int linePos;
	private int charPos;
	private int charLength;

	// =========================================================
	public Scanner(SourceFile source, ErrorReporter reporter) {
		sourceFile = source;
		errorReporter = reporter;
		currentChar = sourceFile.getNextChar();
		debug = false;

		// you may initialise your counters for line and column numbers here
		linePos = 1;
		charPos = 1;
		charLength = 0;
	}

	public void enableDebugging() {
		debug = true;
	}

	// accept gets the next character from the source program.
	private void accept() {
		// you may save the lexeme of the current token incrementally here
		// you may also increment your line and column counters here

		if (currentChar == '\"') {
			if (inspectChar(1) != '\n') {
				currentChar = sourceFile.getNextChar();
				currentSpelling.append(currentChar);
			}
			charPos++;
		} else if (currentChar != '\n') {
			currentSpelling.append(currentChar);
			// sourcePos.charFinish++;
			charPos++;
		} else {
			sourcePos.lineStart++;
			sourcePos.lineFinish++;
			sourcePos.charStart = 1;
			sourcePos.charFinish = 1;
			linePos++;
			charPos = 1;
		}
		currentChar = sourceFile.getNextChar();

	}

	private void accept(int num) {
		if (num >= 1) {
			for (int i = 1; i <= num; i++)
				accept();
		}
		if (!(sourcePos.charStart == 1 && sourcePos.charFinish == 1))
			sourcePos.charFinish += num;
	}

	private void acceptString(int num) {
		if (num >= 1) {
			for (int i = 1; i <= num; i++) {

			}

		}
		sourcePos.charFinish += num;
	}

	// inspectChar returns the n-th character after currentChar in the input
	// stream.
	//
	// If there are fewer than nthChar characters between currentChar and the
	// end of file marker, SourceFile.eof is returned.
	//
	// Both currentChar and the current position in the input stream are *not*
	// changed. Therefore, a subsequent call to accept() will always return the
	// next char after currentChar.

	private char inspectChar(int nthChar) {
		return sourceFile.inspectChar(nthChar);
	}

	private int nextToken() {
		// Tokens: separators, operators, literals, identifiers and keyworods

		// digit literals
		if (currentChar <= '9' && currentChar >= '0') {
			return digitHandler();
		} else if ((currentChar <= 'z' && currentChar >= 'a')
				|| (currentChar <= 'Z' && currentChar >= 'A')) {
			// identifiers
			return idHandler();
		} else if (currentChar == '"') {
			return stringHandler();
		} else if (currentChar == '/') {
			if (inspectChar(1) == '*' || inspectChar(1) == '/') {
				skipSpaceAndComments();
			} else {
				accept(1);
				return Token.DIV;
			}
		}

		else {
			switch (currentChar) {
			// separators
			case '(':
				accept(1);
				return Token.LPAREN;
			case '{':
				accept(1);
				return Token.LCURLY;
			case '[':
				accept(1);
				return Token.LBRACKET;
			case ')':
				accept(1);
				return Token.RPAREN;
			case '}':
				accept(1);
				return Token.RCURLY;
			case ']':
				accept(1);
				return Token.RBRACKET;
			case ';':
				accept(1);
				return Token.SEMICOLON;
			case ',':
				accept(1);
				return Token.COMMA;
			case '.':
				return digitHandler();

				// operators
			case '+':
				accept(1);
				return Token.PLUS;
			case '-':
				accept(1);
				return Token.MINUS;
			case '*':
				accept(1);
				return Token.MULT;
			case '!':
				accept(1);
				if (currentChar == '=') {
					accept(1);
					return Token.NOTEQ;
				} else {
					return Token.NOT;
				}
			case '=':
				accept(1);
				if (currentChar == '=') {
					accept(1);
					return Token.EQEQ;
				} else {
					return Token.EQ;
				}
			case '<':
				accept(1);
				if (currentChar == '=') {
					accept(1);
					return Token.LTEQ;
				} else {
					return Token.LT;
				}
			case '>':
				accept(1);
				if (currentChar == '=') {
					accept(1);
					return Token.GTEQ;
				} else {
					return Token.GT;
				}
			case '&':
				accept(1);
				if (currentChar == '&') {
					accept(1);
					return Token.ANDAND;
				} else {
					return Token.ERROR;
				}
			case '|':
				accept(1);
				if (currentChar == '|') {
					accept(1);
					return Token.OROR;
				} else {
					return Token.ERROR;
				}
			case ' ':
				skipSpaceAndComments();
				// ....
			case SourceFile.eof:
				currentSpelling.append(Token.spell(Token.EOF));
				return Token.EOF;
			default:
				break;
			}
		}
		accept();
		return Token.ERROR;
	}

	private int stringHandler() {
		int num = 1;
		boolean endOfString = false;
		char nextChar = inspectChar(num);

		while (nextChar != '\n' && nextChar != Token.EOF && nextChar != '"') {
			if (nextChar != '"') {
				num++;
			}
			nextChar = inspectChar(num);

		}
		nextChar = inspectChar(num + 1);
		switch (nextChar) {
		case '\n':
			num++;
			break;
		case Token.EOF:

		case ' ':

		}
		accept(num);

		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < currentSpelling.length(); i++) {
			char c = currentSpelling.charAt(i);
			if (c == '\\') {
				switch (currentSpelling.charAt(i + 1)) {
				case 'b':
					buffer.append('\b');
					i++;
					break;
				case 'f':
					buffer.append('\f');
					i++;
					break;
				case 'n':
					buffer.append('\n');
					i++;
					break;
				case 'r':
					buffer.append('\r');
					i++;
					break;
				case 't':
					buffer.append('\t');
					i++;
					break;
				case '\'':
					buffer.append('\'');
					i++;
					break;
				case '"':
					buffer.append('\"');
					i++;
					break;
				case '\\':
					buffer.append('\\');
					i++;
					break;
				default:
					buffer.append(c);
					break;
				}
			} else {
				buffer.append(c);
			}
		}
		if (buffer.length() > 0)
			currentSpelling = buffer;
		return Token.STRINGLITERAL;
	}

	private int idHandler() {
		int num = 1;
		while ((inspectChar(num) <= '9' && inspectChar(num) >= '0')
				|| (inspectChar(num) <= 'z' && inspectChar(num) >= 'a')
				|| (inspectChar(num) <= 'Z' && inspectChar(num) >= 'A')
				|| inspectChar(num) == '_') {
			num++;
		}
		accept(num);
		if (currentSpelling.toString().toLowerCase() == "true"
				|| currentSpelling.toString().toLowerCase() == "false") {
			return Token.BOOLEANLITERAL;
		} else {
			return Token.ID;
		}
	}

	private int digitHandler() {
		int num = inspectDigit();
		accept(num);

		// handle exponent
		if (currentChar == '.') {
			accept();
			if (currentChar <= '9' && currentChar >= '0') {
				accept(inspectDigit());
			}
			if (currentChar == 'e' || currentChar == 'E') {
				accept(1);
				// e.g. 1.e+5,1.e5
				if (currentChar == '+' || currentChar == '-') {
					accept(1);
				}
				if (currentChar <= '9' && currentChar >= '0') {
					accept(inspectDigit());
					return Token.FLOATLITERAL;
				} else {
					return Token.ERROR;
				}
			}
		} else if (currentChar == 'e' || currentChar == 'E') {
			// e.g. 1e+5, 1e5
			accept(1);
			if (currentChar == '+' || currentChar == '-') {
				accept(1);
				if (currentChar <= '9' && currentChar >= '0') {
					accept(inspectDigit());
					return Token.FLOATLITERAL;
				} else {
					return Token.ERROR;
				}
			}
		} else {
			return Token.INTLITERAL;
		}
		return Token.ERROR;
	}

	// Count the number of digits
	private int inspectDigit() {
		int counter = 1;
		while (inspectChar(counter) <= '9' && inspectChar(counter) >= '0'
				&& inspectChar(counter) != '\u0000') {
			counter++;
		}
		return counter;
	}

	private int spaceHandler() {
		int skip = 0;
		if (currentChar == ' ' || currentChar == '\t') {
			// skip space & tab
			skip++;
			while ((inspectChar(skip) == ' ' || inspectChar(skip) == '\t')
					&& currentChar != '\u0000') {
				skip++;
			}
		}
		return skip;
	}

	private boolean commentsHandler() {
		int skip = 0;
		char lastChar = 0;

		if (currentChar == '/') {
			int nthChar = 2;
			// handle comments like '//'
			if (inspectChar(1) == '/') {
				while (inspectChar(nthChar) != '\n') {
					skip++;
					nthChar++;
				}
				skip = nthChar;
			} else if (inspectChar(1) == '*') {

				// handle comments like /* ... */
				StringBuffer buffer = new StringBuffer();
				buffer.append(inspectChar(nthChar));
				buffer.append(inspectChar(nthChar + 1));
				while (!buffer.toString().equals("*/")
						&& inspectChar(nthChar) != '\u0000') {
					skip++;
					nthChar++;
					buffer = new StringBuffer();
					buffer.append(inspectChar(nthChar));
					buffer.append(inspectChar(nthChar + 1));
					// System.out.println(buffer.toString());
				}
				if (!buffer.toString().equals("*/")) {
					return false;
				}
				skip = nthChar + 2;
			}
		}

		if (skip > 0) {
			while ((inspectChar(skip) == ' ' || inspectChar(skip) == '\t')
					&& currentChar != '\u0000') {
				skip++;
			}
		}

		// skip to the next 'skip' chars
		for (int i = 0; i < skip; i++) {
			lastChar = currentChar;
			currentChar = sourceFile.getNextChar();
		}
		updateSourcePosition(0, skip, skip);

		return true;
	}

	boolean skipSpaceAndComments() {
		int skip = 0;
		int lineOffset = 0;
		boolean error = false;

		skip += spaceHandler();
		// skip to the next 'skip' chars
		for (int i = 0; i < skip; i++) {
			currentChar = sourceFile.getNextChar();
		}
		updateSourcePosition(lineOffset, skip, skip);
		if (currentChar == '\n')
			accept();

		if (commentsHandler()) {
			boolean e = false;
			while (currentChar == '\n' && inspectChar(1) == '/' && e) {
				accept();
				e = commentsHandler();
			}
			return e;
		} else {
			return false;
		}
	}

	private void updateSourcePosition(int lineNumOffset, int charStartOffset,
			int charFinishOffset) {
		sourcePos.lineStart += lineNumOffset;
		sourcePos.lineFinish += lineNumOffset;
		sourcePos.charStart += charStartOffset;
		sourcePos.charFinish += charFinishOffset;
	}

	public Token getToken() {
		Token tok;
		int kind;

		// Init for new sourcePosition
		sourcePos = new SourcePosition();
		sourcePos.lineStart = linePos;
		sourcePos.lineFinish = linePos;
		sourcePos.charStart = charPos;
		sourcePos.charFinish = charPos;
		// System.out.println(sourcePos.toString());

		// skip white space and comments
		// boolean error = false;
		// while ((currentChar == ' ' || currentChar == '/' || currentChar ==
		// '\n')
		// && !error) {
		// error = skipSpaceAndComments();
		// }
		//
		// if (error) {
		// errorReporter.reportError("unterminated comment", "", sourcePos);
		// return new Token(Token.ERROR, "unterminated comment", sourcePos);
		// }
		// else {

		currentSpelling = new StringBuffer("");
		// You must record the position of the current token somehow
		kind = nextToken();
		linePos = sourcePos.lineStart;
		charPos = sourcePos.charFinish;

		if (currentSpelling.length() == 1) {
			sourcePos.charFinish = sourcePos.charStart;
		}

		tok = new Token(kind, currentSpelling.toString(), sourcePos);

		// * do not remove these three lines
		if (debug)
			System.out.println(tok);
		return tok;
		// }
	}

	// public StringBuffer getCurrentSpelling() {
	// return currentSpelling;
	// }
	//
	// public void setCurrentSpelling(StringBuffer currentSpelling) {
	// this.currentSpelling = currentSpelling;
	// }
}
