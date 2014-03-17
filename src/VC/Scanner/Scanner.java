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
		if (currentChar != '\n') {
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
		for (int i = 1; i <= num; i++)
			accept();
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

		// literals
		if (currentChar <= '9' && currentChar >= '0') {
			// accept();
			return digitHandler();
		} else if (currentChar <= 'z' && currentChar >= 'A') {
			accept(1);
			return Token.ID;
		} else {
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
				// identifiers
			case '*':
				accept(1);
				return Token.MULT;
				// keywords
			case '/':
				accept(1);
				return Token.DIV;
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

				// case '\n':
				// accept();
				// break;
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
			// accept(inspectDigit());
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

	// Count the number of digits
	private int inspectDigit() {
		int counter = 1;
		// System.out.println(inspectChar(counter));
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
			// skip space & tab
			skip++;
			while ((inspectChar(skip) == ' ' || inspectChar(skip) == '\t')
					&& currentChar != '\u0000') {
				skip++;
			}
		}
		if (currentChar == '/') {
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

		// skip to the next 'skip' chars
		for (int i = 0; i < skip; i++) {
			currentChar = sourceFile.getNextChar();
		}
		updateSourcePosition(lineOffset, skip, skip);

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
		skipSpaceAndComments();

		currentSpelling = new StringBuffer("");
		// You must record the position of the current token somehow

		kind = nextToken();
		linePos = sourcePos.lineStart;
		charPos = sourcePos.charFinish;

		tok = new Token(kind, currentSpelling.toString(), sourcePos);

		// * do not remove these three lines
		if (debug)
			System.out.println(tok);
		return tok;
	}

	// public StringBuffer getCurrentSpelling() {
	// return currentSpelling;
	// }
	//
	// public void setCurrentSpelling(StringBuffer currentSpelling) {
	// this.currentSpelling = currentSpelling;
	// }
}
