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

	private int LineNum;
	private int CharStart;
	private int CharFinish;

	private int CommentStartChar;
	private int CommentStartLn;

	// =========================================================

	public Scanner(SourceFile source, ErrorReporter reporter) {
		sourceFile = source;
		errorReporter = reporter;
		LineNum = 1;
		CharStart = 0;
		CharFinish = 0;
		currentChar = sourceFile.getNextChar();
		++CharStart;
		++CharFinish;

		debug = false;

		// you may initialise your counters for line and column numbers here

	}

	public void enableDebugging() {
		debug = true;
	}

	// accept gets the next character from the source program.

	private void accept() {
		currentSpelling.append(currentChar);
		currentChar = sourceFile.getNextChar();
		++CharFinish;
		// you may save the lexeme of the current token incrementally here
		// you may also increment your line and column counters here
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
		// Ids
		if ((currentChar >= 'a' && currentChar <= 'z')
				|| (currentChar >= 'A' && currentChar <= 'Z')
				|| currentChar == '_') {
			accept();
			while ((currentChar >= '0' && currentChar <= '9')
					|| (currentChar >= 'a' && currentChar <= 'z')
					|| (currentChar >= 'A' && currentChar <= 'Z')
					|| currentChar == '_') {
				accept();
			}
			if (currentSpelling.toString().equals("true")
					|| currentSpelling.toString().equals("TRUE")
					|| currentSpelling.toString().equals("false")
					|| currentSpelling.toString().equals("FALSE")) {
				--CharFinish;
				return Token.BOOLEANLITERAL;
			}
			--CharFinish;
			return Token.ID;
		} else if (currentChar >= '0' && currentChar <= '9') {// intliteral or
																// floatliteral
			while (currentChar >= '0' && currentChar <= '9') {
				accept();
				if (currentChar == 'E' || currentChar == 'e') {
					char tempchar = inspectChar(1);
					if (tempchar == '+' || tempchar == '-') {
						tempchar = inspectChar(2);
						if (tempchar >= '0' && tempchar <= '9') {
							accept();
							accept();
							while (currentChar >= '0' && currentChar <= '9') {
								accept();
							}
							--CharFinish;
							return Token.FLOATLITERAL;
						} else {
							--CharFinish;
							return Token.INTLITERAL;
						}
					} else if (tempchar >= '0' && tempchar <= '9') {
						accept();
						while (currentChar >= '0' && currentChar <= '9') {
							accept();
						}
						--CharFinish;
						return Token.FLOATLITERAL;
					} else {
						--CharFinish;
						return Token.INTLITERAL;
					}

				}
			}
			if (currentChar != '.') {
				--CharFinish;
				return Token.INTLITERAL;
			}
		} else if (currentChar == '"') {// stringliteral
			currentChar = sourceFile.getNextChar();
			CharFinish++;
			while (currentChar != '"') {
				if (currentChar != '\n') {
					while (currentChar == '\\') {
						char temp = inspectChar(1);
						switch (temp) {
						case 't':
							currentChar = sourceFile.getNextChar();
							currentChar = sourceFile.getNextChar();
							CharFinish += 2;
							currentSpelling.append('\t');
							break;
						case 'n':
							currentChar = sourceFile.getNextChar();
							currentChar = sourceFile.getNextChar();
							CharFinish += 2;
							currentSpelling.append('\n');
							break;
						case 'b':
							currentChar = sourceFile.getNextChar();
							currentChar = sourceFile.getNextChar();
							CharFinish += 2;
							currentSpelling.append('\b');
							break;
						case 'r':
							currentChar = sourceFile.getNextChar();
							currentChar = sourceFile.getNextChar();
							CharFinish += 2;
							currentSpelling.append('\r');
							break;
						case 'f':
							currentChar = sourceFile.getNextChar();
							currentChar = sourceFile.getNextChar();
							CharFinish += 2;
							currentSpelling.append('\f');
							break;
						case '\'':
							currentChar = sourceFile.getNextChar();
							currentChar = sourceFile.getNextChar();
							CharFinish += 2;
							currentSpelling.append('\'');
							break;
						case '"':
							currentChar = sourceFile.getNextChar();
							currentChar = sourceFile.getNextChar();
							CharFinish += 2;
							currentSpelling.append('\"');
							break;
						case '\\':
							currentChar = sourceFile.getNextChar();
							currentChar = sourceFile.getNextChar();
							CharFinish += 2;
							currentSpelling.append("\\");
							break;
						default:
							StringBuffer illegalEC = new StringBuffer("");
							illegalEC.append("\\" + temp);
							errorReporter.reportError(
									"%: illegal escape character", illegalEC
											.toString(), new SourcePosition(
											LineNum, CharStart, CharFinish));
							accept();
							break;
						}
					}
					if (currentChar == '"')
						break;
					accept();
				} else {
					errorReporter.reportError("%: unterminated string",
							currentSpelling.toString(), new SourcePosition(
									LineNum, CharStart, CharFinish));
					break;
				}
			}
			if (currentChar != '"')
				--CharFinish;
			else
				currentChar = sourceFile.getNextChar();
			return Token.STRINGLITERAL;
		}
		switch (currentChar) {
		// separators
		case '{':
			accept();
			--CharFinish;
			return Token.LCURLY;
		case '}':
			accept();
			--CharFinish;
			return Token.RCURLY;
		case '(':
			accept();
			--CharFinish;
			return Token.LPAREN;
		case ')':
			accept();
			--CharFinish;
			return Token.RPAREN;
		case '[':
			accept();
			--CharFinish;
			return Token.LBRACKET;
		case ']':
			accept();
			--CharFinish;
			return Token.RBRACKET;
		case ';':
			accept();
			--CharFinish;
			return Token.SEMICOLON;
		case ',':
			accept();
			--CharFinish;
			return Token.COMMA;
			// operators
		case '+':
			accept();
			--CharFinish;
			return Token.PLUS;
		case '-':
			accept();
			--CharFinish;
			return Token.MINUS;
		case '*':
			accept();
			--CharFinish;
			return Token.MULT;
		case '/':
			accept();
			--CharFinish;
			return Token.DIV;
		case '!':
			accept();
			if (currentChar == '=') {
				accept();
				--CharFinish;
				return Token.NOTEQ;
			} else {
				--CharFinish;
				return Token.NOT;
			}
		case '=':
			accept();
			if (currentChar == '=') {
				accept();
				--CharFinish;
				return Token.EQEQ;
			} else {
				--CharFinish;
				return Token.EQ;
			}
		case '<':
			accept();
			if (currentChar == '=') {
				accept();
				--CharFinish;
				return Token.LTEQ;
			} else {
				--CharFinish;
				return Token.LT;
			}
		case '>':
			accept();
			if (currentChar == '=') {
				accept();
				--CharFinish;
				return Token.GTEQ;
			} else {
				--CharFinish;
				return Token.GT;
			}
		case '&':
			accept();
			if (currentChar == '&') {
				accept();
				--CharFinish;
				return Token.ANDAND;
			} else {
				--CharFinish;
				return Token.ERROR;
			}
		case '|':
			accept();
			if (currentChar == '|') {
				accept();
				--CharFinish;
				return Token.OROR;
			} else {
				--CharFinish;
				return Token.ERROR;
			}
		case '.':
			accept();
			if ((currentChar >= '0' && currentChar <= '9')
					|| currentChar == 'E' || currentChar == 'e') {
				while (currentChar >= '0' && currentChar <= '9') {
					accept();
					if (currentChar == 'E' || currentChar == 'e') {
						char tempchar = inspectChar(1);
						if (tempchar == '+' || tempchar == '-') {
							tempchar = inspectChar(2);
							if (tempchar >= '0' && tempchar <= '9') {
								accept();
								accept();
								while (currentChar >= '0' && currentChar <= '9') {
									accept();
								}
								--CharFinish;
								return Token.FLOATLITERAL;
							} else {
								--CharFinish;
								return Token.FLOATLITERAL;
							}
						} else if (tempchar >= '0' && tempchar <= '9') {
							accept();
							while (currentChar >= '0' && currentChar <= '9') {
								accept();
							}
							--CharFinish;
							return Token.FLOATLITERAL;
						}
					}
				}
				if (currentChar == 'E' || currentChar == 'e') {
					char tempchar = inspectChar(1);
					if (tempchar == '+' || tempchar == '-') {
						tempchar = inspectChar(2);
						if (tempchar >= '0' && tempchar <= '9') {
							accept();
							accept();
							while (currentChar >= '0' && currentChar <= '9') {
								accept();
							}
							--CharFinish;
							return Token.FLOATLITERAL;
						} else {
							--CharFinish;
							return Token.FLOATLITERAL;
						}
					} else if (tempchar >= '0' && tempchar <= '9') {
						accept();
						while (currentChar >= '0' && currentChar <= '9') {
							accept();
						}
						--CharFinish;
						return Token.FLOATLITERAL;
					}
				}
			}
			if (currentSpelling.length() > 1) {
				--CharFinish;
				return Token.FLOATLITERAL;
			} else {
				--CharFinish;
				return Token.ERROR;
			}
		case SourceFile.eof:
			currentSpelling.append(Token.spell(Token.EOF));
			return Token.EOF;
		default:
			break;
		}

		accept();
		--CharFinish;
		return Token.ERROR;
	}

	void skipSpaceAndComments() {
		while (currentChar != SourceFile.eof) {
			if (currentChar == ' ') {
				currentChar = sourceFile.getNextChar();
				++CharStart;
				++CharFinish;
			} else if (currentChar == '\t') {
				currentChar = sourceFile.getNextChar();
				CharFinish += 7;
				CharStart = CharFinish;
			}

			else if (currentChar == '\n') {
				currentChar = sourceFile.getNextChar();
				CharStart = 1;
				CharFinish = 1;
				++LineNum;
			}

			else if (currentChar == '/') {

				char tempChar = inspectChar(1);
				if (tempChar == '/') {// scan the next line
					currentChar = sourceFile.getNextChar();
					++CharStart;
					++CharFinish;
					while (currentChar != '\n' && currentChar != SourceFile.eof) {
						currentChar = sourceFile.getNextChar();
						++CharStart;
						++CharFinish;
					}
					CharStart = 1;
					CharFinish = 1;
					++LineNum;
					currentChar = sourceFile.getNextChar();
				} else if (tempChar == '*') {
					CommentStartLn = LineNum;
					CommentStartChar = CharStart;
					currentChar = sourceFile.getNextChar();
					++CharStart;
					++CharFinish;
					currentChar = sourceFile.getNextChar();
					++CharStart;
					++CharFinish;
					while (currentChar != SourceFile.eof) {
						if (currentChar == '*') {
							tempChar = inspectChar(1);
							if (tempChar == '/') {
								currentChar = sourceFile.getNextChar();
								++CharStart;
								++CharFinish;
								currentChar = sourceFile.getNextChar();
								++CharStart;
								++CharFinish;
								break;
							}
						} else if (currentChar == '\n') {
							CharStart = 0;
							CharFinish = 0;
							++LineNum;
						}
						currentChar = sourceFile.getNextChar();
						++CharStart;
						++CharFinish;
						if (currentChar == SourceFile.eof) {
							errorReporter
									.reportError("%: unterminated comment", "",
											new SourcePosition(CommentStartLn,
													CommentStartChar,
													CommentStartChar));
						}
					}
				} else
					break;
			} else
				break;

		}

	}

	public Token getToken() {
		Token tok;
		int kind;
		CommentStartChar = 0;
		CommentStartLn = 0;
		// skip white space and comments
		skipSpaceAndComments();

		currentSpelling = new StringBuffer("");

		// You must record the position of the current token somehow

		kind = nextToken();
		sourcePos = new SourcePosition(LineNum, CharStart, CharFinish);

		tok = new Token(kind, currentSpelling.toString(), sourcePos);
		++CharFinish;
		CharStart = CharFinish;

		// * do not remove these three lines
		if (debug)
			System.out.println(tok);
		return tok;
	}

}
