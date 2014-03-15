/**
 **	Scanner.java                        
 **/

package VC.Scanner;

import VC.ErrorReporter;

public final class Scanner {

	private SourceFile sourceFile;
	private boolean debug;

	private ErrorReporter errorReporter;
	private StringBuffer currentSpelling;
	private char currentChar;
	private SourcePosition sourcePos;

	// =========================================================

	public Scanner(SourceFile source, ErrorReporter reporter) {
		sourceFile = source;
		errorReporter = reporter;
		currentChar = sourceFile.getNextChar();
		debug = false;

		// you may initialise your counters for line and column numbers here
		sourcePos = new SourcePosition();
		sourcePos.lineStart++;
		sourcePos.lineFinish++;
		sourcePos.charStart++;
		sourcePos.charFinish++;
		// System.out.println(sourcePos.toString());
	}

	public void enableDebugging() {
		debug = true;
	}

	// accept gets the next character from the source program.

	private void accept() {

		// you may save the lexeme of the current token incrementally here
		// you may also increment your line and column counters here
		if (currentChar != '\n') {
			currentSpelling.append(currentChar);
			sourcePos.charFinish++;
		} else {
			sourcePos.lineStart++;
			sourcePos.lineFinish++;
			sourcePos.charStart = 1;
			sourcePos.charFinish = 1;
		}
		currentChar = sourceFile.getNextChar();

	}

	private void accept(int num) {
		for (int i = 0; i < num; i++) {
			currentSpelling.append(currentChar);
			currentChar = sourceFile.getNextChar();
			sourcePos.charFinish++;
		}

	}

	// inspectChar returns the n-th character after currentChar
	// in the input stream.
	//
	// If there are fewer than nthChar characters between currentChar
	// and the end of file marker, SourceFile.eof is returned.
	//
	// Both currentChar and the current position in the input stream
	// are *not* changed. Therefore, a subsequent call to accept()
	// will always return the next char after currentChar.

	private char inspectChar(int nthChar) {
		return sourceFile.inspectChar(nthChar);
	}

	private int nextToken() {
		// Tokens: separators, operators, literals, identifiers and keyworods

		// literals

		if (currentChar <= '9' && currentChar >= '0') {
			// accept();
			return digitHandler();
		} else if (currentChar <= 'z' && currentChar >= 'A') {
			accept();
			return Token.ID;
		} else {
			switch (currentChar) {
			// separators
			case '(':
				accept();
				return Token.LPAREN;
			case '{':
				accept();
				return Token.LCURLY;
			case '[':
				accept();
				return Token.LBRACKET;
			case ')':
				accept();
				return Token.RPAREN;
			case '}':
				accept();
				return Token.RCURLY;
			case ']':
				accept();
				return Token.RBRACKET;
			case ';':
				accept();
				return Token.SEMICOLON;
			case ',':
				accept();
				return Token.COMMA;

			case '.':
				accept(inspectDigit());

				return Token.FLOATLITERAL;

				// operators
			case '+':
				accept();
				return Token.PLUS;
			case '-':
				accept();
				return Token.MINUS;
				// identifiers
			case '*':
				accept();
				return Token.MULT;
				// keywords
			case '/':
				accept();
				return Token.DIV;
			case '!':
				accept();
				if (currentChar == '=') {
					accept();
					return Token.NOTEQ;
				} else {
					return Token.NOT;
				}
			case '=':
				accept();
				if (currentChar == '=') {
					accept();
					return Token.EQEQ;
				} else {
					return Token.EQ;
				}
			case '<':
				accept();
				if (currentChar == '=') {
					accept();
					return Token.LTEQ;
				} else {
					return Token.LT;
				}
			case '>':
				accept();
				if (currentChar == '=') {
					accept();
					return Token.GTEQ;
				} else {
					return Token.GT;
				}
			case '&':
				accept();
				if (currentChar == '&') {
					accept();
					return Token.ANDAND;
				} else {
					return Token.ERROR;
				}
			case '|':
				accept();
				if (currentChar == '|') {
					accept();
					return Token.OROR;
				} else {
					return Token.ERROR;
				}

				// case '\n':
				// accept();
				// return Token.ERROR;

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

	private int digitHandler() {
		int num = inspectDigit();
		accept(num);

		// handle exponent
		if (currentChar == '.' || currentChar == 'e' || currentChar == 'E') {
			accept();
			accept(inspectDigit());
			if (currentChar == 'e' || currentChar == 'E') {
				accept();
				// e.g. 1.e+5,1.e5
				if (currentChar == '+' || currentChar == '-') {
					accept();
					if (currentChar <= '9' && currentChar >= '0') {
						accept();
						return Token.FLOATLITERAL;
					} else {
						return Token.ERROR;
					}
				}
			}
			accept(inspectDigit());
			return Token.FLOATLITERAL;
		} else if (currentChar == 'e' || currentChar == 'E') {
			// e.g. 1e+5, 1e5
			accept();
			if (currentChar == '+' || currentChar == '-') {
				accept();
				if (currentChar <= '9' && currentChar >= '0') {
					accept();
					return Token.FLOATLITERAL;
				} else {
					return Token.ERROR;
				}
			}
		} else {
			return Token.INTLITERAL;
		}
		return Token.ERROR;
		// switch (currentChar) {
		// case '.':
		// accept(inspectDigit() + 1);
		// return Token.FLOATLITERAL;
		// case 'e':
		// case 'E':
		// accept();
		// if (currentChar == '+' || currentChar == '-') {
		// accept();
		// if (currentChar <= '9' && currentChar >= '0') {
		// accept(inspectDigit() + 1);
		// return Token.FLOATLITERAL;
		// } else {
		// return Token.ERROR;
		// }
		//
		// }
		// }
	}

	private int inspectDigit() {
		int counter = 1;
		// accept();
		char c = inspectChar(counter);
		while (inspectChar(counter) <= '9' && inspectChar(counter) >= '0'
				&& inspectChar(counter) != '\u0000') {
			counter++;
		}

		return counter;
	}

	void skipSpaceAndComments() {
		int skip = 0;
		int lineOffset = 0;
		// System.out.println("CurrentChar: " + currentChar);
		if (currentChar == ' ' || currentChar == '\t') {
			skip++;
		} else if (currentChar == '/') {
			int nthChar = 2;
			System.out.println("nextChar: " + inspectChar(1));
			if (inspectChar(1) == '/') {
				System.out.println("nextNChar: " + inspectChar(nthChar));

				while (inspectChar(nthChar) != '\n') {
					skip++;
					nthChar++;
				}
				lineOffset++;
				// updateSourcePosition(1, 0, 0);
			} else if (inspectChar(1) == '*') {
				while (inspectChar(nthChar) != '*'
						&& inspectChar(nthChar + 1) != '/') {
					System.out.println("nextNChar: " + inspectChar(nthChar)
							+ " n= " + nthChar);

					if (inspectChar(nthChar) == '\n') {
						lineOffset++;
						// updateSourcePosition(1, 0, 0);
					}
					skip++;
					nthChar++;
				}
				skip = skip + 2;
			}
		}

		// skip the next 'skip' chars
		for (int i = 0; i < skip; i++) {
			sourceFile.getNextChar();
		}
		updateSourcePosition(lineOffset, 0, skip);

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

		// skip white space and comments

		skipSpaceAndComments();

		currentSpelling = new StringBuffer("");

		// You must record the position of the current token somehow

		kind = nextToken();

		tok = new Token(kind, currentSpelling.toString(), sourcePos);

		// * do not remove these three lines
		if (debug)
			System.out.println(tok);
		return tok;
	}

	private void setLineStart(int lineStart) {
		sourcePos.lineStart = lineStart;
	}

	private void setLineFinish(int lineFinish) {
		sourcePos.lineFinish = lineFinish;
	}

	private void setCharStart(int charStart) {
		sourcePos.charStart = charStart;
	}

	private void setCharFinish(int charFinish) {
		sourcePos.charFinish = charFinish;
	}

	public StringBuffer getCurrentSpelling() {
		return currentSpelling;
	}

	public void setCurrentSpelling(StringBuffer currentSpelling) {
		this.currentSpelling = currentSpelling;
	}
}
