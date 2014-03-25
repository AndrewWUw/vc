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
	private boolean accept() {
		// you may save the lexeme of the current token incrementally here
		// you may also increment your line and column counters here
		boolean isSkipped = false;
		if (currentChar == '"') {
			if (inspectChar(1) != '\n') {
				currentChar = sourceFile.getNextChar();
				if (currentChar == '\\') {
					currentSpelling.append(currentChar);
					currentChar = sourceFile.getNextChar();
					currentSpelling.append(currentChar);
					isSkipped = true;
					charPos++;
				} else {
					currentSpelling.append(currentChar);
				}
			}
			charPos++;
		} else if (currentChar == '\\') {
			currentSpelling.append(currentChar);
			currentChar = sourceFile.getNextChar();
			currentSpelling.append(currentChar);
			isSkipped = true;
			charPos++;
		} else if (currentChar == '\n') {
			// sourcePos.lineStart++;
			// sourcePos.lineFinish++;
			sourcePos.charStart = 1;
			sourcePos.charFinish = 1;
			linePos++;
			charPos = 1;
		} else if (currentChar != '\n') {
			currentSpelling.append(currentChar);
			// sourcePos.charFinish++;
			charPos++;
		}
		currentChar = sourceFile.getNextChar();

		return isSkipped;
	}

	private void accept(int num) {
		int sum = num;
		if (num >= 1) {
			for (int i = 1; i <= num; i++) {
				boolean isSkipped = accept();
				if (isSkipped)
					num--;
			}
		}
		// if (!(sourcePos.charStart == 1 && sourcePos.charFinish == 1))
		sourcePos.charFinish += sum;
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
				if (inspectDigit() <= 1) {
					accept(1);
					return Token.ERROR;
				} else {
					return digitHandler();
				}
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

		// "comp9102
		while (nextChar != '\n' && nextChar != '\u0000' && nextChar != '"') {
			if (nextChar == '\\')
				num++;
			num++;
			nextChar = inspectChar(num);
		}

		// string ends without closing quote
		if (nextChar == '\n' || nextChar == '\u0000') {
			if (inspectChar(num - 1) != '"') {
				updateSourcePosition();
				errorReporter.reportError(currentSpelling.toString()
						+ " unterminated string", "", sourcePos);
			}
		}
		accept(num);

		if (inspectChar(num + 1) == '\n')
			accept(1);

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
					updateSourcePosition();
					sourcePos.charFinish++;
					errorReporter.reportError(
							"\\" + currentSpelling.charAt(i + 1)
									+ " illegal escape character", "",
							sourcePos);
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

	/**
	 * Skip space & tab
	 */
	private void spaceHandler() {
		int skip = 0;
		int tabOffset = 0;
		if (currentChar == ' ' || currentChar == '\t') {
			if (currentChar == '\t') {
				tabOffset = 8 - skip % 8 - 1;
			}
			skip++;
			while ((inspectChar(skip) == ' ' || inspectChar(skip) == '\t')
					&& currentChar != '\u0000') {
				if (currentChar == '\t') {
					tabOffset = 8 - skip % 8 - 1;
				}
				skip++;
			}

		}

		// skip the next 'skip' chars
		for (int i = 0; i < skip; i++) {
			currentChar = sourceFile.getNextChar();
		}

		updateSourcePosition(0, skip + tabOffset, skip + tabOffset);
	}

	/**
	 * @return 0: if currentChar is comment;
	 * 
	 *         -1: if the comments ends wrongly
	 * 
	 *         1: if it's not a comment,
	 */
	private int commentsHandler() {
		int skip = 0;

		if (currentChar == '/') {
			int nthChar = 2;
			// handle comments like '//'
			if (inspectChar(1) == '/') {
				while (inspectChar(nthChar) != '\n') {
					skip++;
					nthChar++;
				}
				skip = nthChar;
			}
			// handle comments like /* ... */
			else if (inspectChar(1) == '*') {
				StringBuffer buffer = new StringBuffer();
				buffer.append(inspectChar(nthChar));
				buffer.append(inspectChar(nthChar + 1));
				while (inspectChar(nthChar + 1) != '\u0000'
						&& !buffer.toString().equals("*/")) {
					buffer = new StringBuffer();
					buffer.append(inspectChar(nthChar));
					buffer.append(inspectChar(nthChar + 1));
					skip++;
					nthChar++;
				}
				// comments ends wrongly, return -1
				if (!buffer.toString().equals("*/")) {
					return -1;
				}
				skip = nthChar + 2;
			} else {
				// it's not a comment, return 1
				return 1;
			}
		} else {
			return 1;
		}

		// check if there is spaces or tabs behind the comments
		if (skip > 0) {
			while ((inspectChar(skip) == ' ' || inspectChar(skip) == '\t')
					&& currentChar != '\u0000') {
				skip++;
			}
		}
		// skip to the next 'skip' chars
		// for (int i = 0; i < skip; i++) {
		// currentChar = sourceFile.getNextChar();
		// }

		accept(skip);

		updateSourcePosition(0, skip, skip);

		return 0;
	}

	/**
	 * Skip space and comments from input stream
	 * 
	 * @return 0: space and comments have been processed;
	 * 
	 *         -1: the comments ends wrongly
	 * 
	 *         1: it's not a comment
	 */
	private int skipSpaceAndComments() {
		int skip = 0;
		int lineOffset = 0;
		int isComment = -1;

		// skip += spaceHandler();
		spaceHandler();
		while (currentChar == '\n') {
			// if (currentChar == '\n') {
			accept();
			sourcePos.lineStart++;
			sourcePos.lineFinish++;
			spaceHandler();
		}

		isComment = commentsHandler();
		// check if the next line also is comment
		if (isComment == 0 && currentChar != '\u0000') {
			while (currentChar == '\n' && isComment == 0) {
				// only if the current char is \n and the next line starts
				// with '/' and the previous line is also comment
				accept();
				sourcePos.lineStart++;
				sourcePos.lineFinish++;
				if (currentChar == ' ') {
					spaceHandler();
				}
				isComment = commentsHandler();
				if (currentChar == '\n')
					isComment = 0;
			}
			return isComment;
		} else {
			return isComment; // fail to process the comments & spaces
		}
	}

	private boolean skipSpaceAndComments(int i) {
		int skip = 0;
		int lineOffset = 0;
		int isComment = -1;

		while ((currentChar == ' ' || currentChar == '\t' || currentChar == '/' || currentChar == '\n')
				&& currentChar != '\u0000') {
			switch (currentChar) {
			case ' ':
				spaceHandler();
				break;
			case '\n':
				newLineHandler();
				break;
			case '\t':
				tabHandler();
				break;
			case '/':
				commentsHandler();
				break;
			}
		}
		return false;
	}

	private void tabHandler() {
		int skip = 0;
		while (currentChar == '\t') {
			skip = 8 - sourcePos.charFinish % 8 - 1;
			accept(1);
		}
	}

	private void newLineHandler() {
		int skip = 0;
		while (currentChar == '\n') {
			accept(1);
			sourcePos.lineStart++;
			sourcePos.lineFinish++;
		}
	}

	public Token getToken(int i) {
		Token tok;
		int kind;

		// Init for new sourcePosition
		sourcePos = new SourcePosition();
		sourcePos.lineStart = linePos;
		sourcePos.lineFinish = linePos;
		sourcePos.charStart = charPos;
		sourcePos.charFinish = charPos;
		currentSpelling = new StringBuffer("");

		int isSucceed = 0;
		// skip white space and comments process space and comments
		// only continue if the previous line is processed successfully
		if (currentChar != '\u0000') {
			while ((currentChar == ' ' || currentChar == '\t'
					|| currentChar == '/' || currentChar == '\n')
					&& isSucceed == 0 && currentChar != '\u0000') {
				isSucceed = skipSpaceAndComments();
			}
		}

		if (isSucceed == -1) {
			errorReporter.reportError("unterminated comment", "", sourcePos);
			accept(1);
			return new Token(Token.ERROR, "unterminated comment", sourcePos);
		} else {
			// You must record the position of the current token somehow
			kind = nextToken();
			// linePos = sourcePos.lineStart;
			// charPos = sourcePos.charFinish;

			if (currentSpelling.length() == 1) {
				sourcePos.charFinish = sourcePos.charStart;
				charPos = sourcePos.charFinish;
			} else if (currentSpelling.length() > 1) {
				sourcePos.charFinish = sourcePos.charStart
						+ currentSpelling.length() - 1;
				charPos = sourcePos.charFinish;
			}

			tok = new Token(kind, currentSpelling.toString(), sourcePos);

			// * do not remove these three lines
			if (debug)
				System.out.println(tok);
			return tok;
		}
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
		currentSpelling = new StringBuffer("");

		int isSucceed = 0;
		// skip white space and comments process space and comments
		// only continue if the previous line is processed successfully
		while ((currentChar == ' ' || currentChar == '\t' || currentChar == '/' || currentChar == '\n')
				&& currentChar != '\u0000' && isSucceed == 0) {
			isSucceed = skipSpaceAndComments();
		}

		if (isSucceed == -1) {
			errorReporter.reportError("unterminated comment", "", sourcePos);
			accept(1);
			return new Token(Token.ERROR, "unterminated comment", sourcePos);
		} else {
			// You must record the position of the current token somehow
			kind = nextToken();
			// linePos = sourcePos.lineStart;
			// charPos = sourcePos.charFinish;

			if (currentSpelling.length() == 1) {
				sourcePos.charFinish = sourcePos.charStart;
				charPos = sourcePos.charFinish;
			} else if (currentSpelling.length() > 1) {
				sourcePos.charFinish = sourcePos.charStart
						+ currentSpelling.length() - 1;
				charPos = sourcePos.charFinish;
			}

			tok = new Token(kind, currentSpelling.toString(), sourcePos);

			// * do not remove these three lines
			if (debug)
				System.out.println(tok);
			return tok;
		}
	}

	private void updateSourcePosition() {
		if (sourcePos.charFinish > sourcePos.charStart)
			sourcePos.charFinish--;
	}

	private void updateSourcePosition(int lineNumOffset, int charStartOffset,
			int charFinishOffset) {
		sourcePos.lineStart += lineNumOffset;
		sourcePos.lineFinish += lineNumOffset;
		sourcePos.charStart += charStartOffset;
		sourcePos.charFinish += charFinishOffset;
	}
}
