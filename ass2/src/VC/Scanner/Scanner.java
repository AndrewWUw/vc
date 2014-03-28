package VC.Scanner;

import VC.ErrorReporter;
import java.io.PrintStream;

public final class Scanner {
	private SourceFile sourceFile;
	private ErrorReporter errorReporter;
	private boolean debug;
	private StringBuffer currentSpelling;
	private char currentChar;
	private Token token;
	private boolean jdField_new;
	private SourcePosition sourcePos;
	private int jdField_int;
	private int jdField_char;
	private int jdField_if;
	private char[] jdField_case = { 'b', 'f', 'n', 'r', 't', '\'', '"', '\\' };
	private char[] jdField_else = { '\b', '\f', '\n', '\r', '\t', '\'', '"',
			'\\' };

	public Scanner(SourceFile paramSourceFile, ErrorReporter paramErrorReporter) {
		this.sourceFile = paramSourceFile;
		this.errorReporter = paramErrorReporter;
		this.token = null;
		this.jdField_int = 1;
		this.jdField_char = 0;
		this.debug = false;
		this.jdField_new = false;
		accept();
	}

	public void enableDebugging() {
		this.debug = true;
	}

	public Token inspectNextToken() {
		this.token = getToken();
		return this.token;
	}

	private void accept() {
		if (this.jdField_new)
			this.currentSpelling.append(this.currentChar);
		jdField_for();
	}

	private void jdField_for() {
		// this.jdField_long = this.jdField_try.a();
		this.jdField_if = this.jdField_char;
		if (this.currentChar == '\n') {
			this.jdField_int += 1;
			this.jdField_char = 0;
		} else if (this.currentChar == '\t') {
			this.jdField_char = (8 - this.jdField_char % 8 + this.jdField_char);
		} else {
			this.jdField_char += 1;
		}
	}

	private char jdField_if(int paramInt) {
		return ' ';
		// return this.jdField_try.a(paramInt);
	}

	void a(String paramString1, String paramString2) {
		this.errorReporter.reportError(paramString1, paramString2,
				this.sourcePos);
	}

	private int nextToken() {
		switch (this.currentChar) {
		case '{':
			accept();
			return 25;
		case '}':
			accept();
			return 26;
		case '(':
			accept();
			return 27;
		case ')':
			accept();
			return 28;
		case '[':
			accept();
			return 29;
		case ']':
			accept();
			return 30;
		case ';':
			accept();
			return 31;
		case ',':
			accept();
			return 32;
		case '.':
			if (Character.isDigit(jdField_if(1)))
				return jdField_try();
			accept();
			return 38;
		case '+':
			accept();
			return 11;
		case '-':
			accept();
			return 12;
		case '*':
			accept();
			return 13;
		case '/':
			accept();
			return 14;
		case '!':
			accept();
			if (this.currentChar == '=') {
				accept();
				return 16;
			}
			return 15;
		case '=':
			accept();
			if (this.currentChar == '=') {
				accept();
				return 18;
			}
			return 17;
		case '<':
			accept();
			if (this.currentChar == '=') {
				accept();
				return 20;
			}
			return 19;
		case '>':
			accept();
			if (this.currentChar == '=') {
				accept();
				return 22;
			}
			return 21;
		case '&':
			accept();
			if (this.currentChar == '&') {
				accept();
				return 23;
			}
			return 38;
		case '|':
			accept();
			if (this.currentChar == '|') {
				accept();
				return 24;
			}
			return 38;
		case '"':
			return jdField_new();
		case '\000':
			this.currentSpelling.append(Token.spell(39));
			this.jdField_if = this.jdField_char;
			return 39;
		}
		if (a(this.currentChar))
			return jdField_if();
		if (Character.isDigit(this.currentChar))
			return jdField_byte();
		accept();
		return 38;
	}

	int jdField_byte() {
		while (Character.isDigit(this.currentChar))
			accept();
		if (this.currentChar == '.')
			return jdField_try();
		if ((this.currentChar == 'e') || (this.currentChar == 'E'))
			return a(34);
		return 34;
	}

	int jdField_try() {
		accept();
		while (Character.isDigit(this.currentChar))
			accept();
		if ((this.currentChar == 'e') || (this.currentChar == 'E'))
			return a(35);
		return 35;
	}

	int a(int paramInt) {
		char c1 = jdField_if(1);
		char c2 = jdField_if(2);
		if ((Character.isDigit(c1))
				|| (((c1 == '+') || (c1 == '-')) && (Character.isDigit(c2)))) {
			accept();
			if ((this.currentChar == '+') || (this.currentChar == '-'))
				accept();
			while (Character.isDigit(this.currentChar))
				accept();
			return 35;
		}
		return paramInt;
	}

	int jdField_new() {
		int i = 0;
		jdField_for();
		while ((i != 0) || (this.currentChar != '"')) {
			if (this.currentChar == '\n') {
				this.sourcePos.charFinish = this.sourcePos.charStart;
				a("%: unterminated string", this.currentSpelling.toString());
				return 37;
			}
			if (i != 0) {
				i = 0;
				int j = jdField_if(this.currentChar);
				if (j >= 0) {
					this.currentChar = this.jdField_else[j];
					accept();
				} else {
					StringBuffer localStringBuffer = new StringBuffer("\\");
					localStringBuffer.append(this.currentChar);
					this.sourcePos.charFinish = this.jdField_if;
					a("%: illegal escape character",
							localStringBuffer.toString());
					this.currentSpelling.append('\\');
					accept();
				}
			} else if (this.currentChar == '\\') {
				i = 1;
				jdField_for();
			} else {
				accept();
			}
		}
		jdField_for();
		return 37;
	}

	int jdField_if(char paramChar) {
		for (int i = 0; i < this.jdField_case.length; i++)
			if (this.jdField_case[i] == paramChar)
				return i;
		return -1;
	}

	boolean a(char paramChar) {
		return (paramChar == '_') || ((paramChar >= 'A') && (paramChar <= 'Z'))
				|| ((paramChar >= 'a') && (paramChar <= 'z'));
	}

	boolean jdField_do(char paramChar) {
		return (paramChar == '_') || ((paramChar >= 'A') && (paramChar <= 'Z'))
				|| ((paramChar >= 'a') && (paramChar <= 'z'))
				|| ((paramChar >= '0') && (paramChar <= '9'));
	}

	int jdField_if() {
		while (jdField_do(this.currentChar))
			accept();
		if ((this.currentSpelling.toString().compareTo("true") == 0)
				|| (this.currentSpelling.toString().compareTo("false") == 0))
			return 36;
		return 33;
	}

	void jdField_do() {
		this.jdField_new = false;
		while ((this.currentChar == ' ') || (this.currentChar == '\n')
				|| (this.currentChar == '\r') || (this.currentChar == '\t')
				|| (this.currentChar == '/'))
			if (this.currentChar == '/') {
				if (jdField_if(1) == '/') {
					while (this.currentChar != '\n')
						accept();
				} else {
					if (jdField_if(1) != '*')
						break;
					this.sourcePos = new SourcePosition();
					this.sourcePos.lineStart = (this.sourcePos.lineFinish = this.jdField_int);
					this.sourcePos.charStart = (this.sourcePos.charFinish = this.jdField_char);
					accept();
					do {
						do
							accept();
						while ((this.currentChar != '*')
								&& (this.currentChar != 0));
						if (this.currentChar == 0) {
							a("%: unterminated comment", "");
							break;
						}
						do
							accept();
						while ((this.currentChar == '*')
								&& (this.currentChar != 0));
						if (this.currentChar == 0) {
							a("%: unterminated comment", "");
							break;
						}
					} while (this.currentChar != '/');
					accept();
				}
			} else
				accept();
		this.jdField_new = true;
	}

	public Token getToken() {
		if (this.token != null) {
			Token localToken2 = this.token;
			this.token = null;
			return localToken2;
		}
		jdField_do();
		this.currentSpelling = new StringBuffer("");
		this.sourcePos = new SourcePosition();
		this.sourcePos.lineStart = (this.sourcePos.lineFinish = this.jdField_int);
		this.sourcePos.charStart = this.jdField_char;
		int i = nextToken();
		this.sourcePos.charFinish = this.jdField_if;
		Token localToken1 = new Token(i, this.currentSpelling.toString(),
				this.sourcePos);
		if (this.debug)
			System.out.println(localToken1);
		return localToken1;
	}
}

/*
 * Location: /home/andrew/study/git/a/ Qualified Name: VC.Scanner.Scanner
 * JD-Core Version: 0.6.2
 */