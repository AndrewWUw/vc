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

		currentChar = sourceFile.getNextChar();

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

		switch (currentChar) {
		// separators
		case '(':
			accept();
			return Token.LPAREN;
		case '.':
			// attempting to recognise a float

		case '|':
			accept();
			if (currentChar == '|') {
				accept();
				return Token.OROR;
			} else {
				return Token.ERROR;
			}

			// ....
		case SourceFile.eof:
			currentSpelling.append(Token.spell(Token.EOF));
			return Token.EOF;
		default:
			break;
		}

		accept();
		return Token.ERROR;
	}

	
	void skipSpaceAndComments() {
		int skip = 0;
		int lineOffset = 0;
		System.out.println("CurrentChar: " + currentChar);
		if (currentChar == ' ') {
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

}
