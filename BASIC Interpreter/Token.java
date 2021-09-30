/**
 * Enum tokens for Lexer Class
 * @author Dan Yee
 */
public class Token {
	public enum TokenTypes {
		number, plus, minus, multiply, divide, 																// basic mathematical operators
		equals, lessThan, lessThanEquals, greaterThan, greaterThanEquals, notequals, lparen, rparen, 		// inequality operators and parenthesis
		print, string, identifier, label, comma, read, data, input, gosub, Return, 							// general keywords
		For, to, step, next,																				// keywords for for loop
		If, then,																							// keywords for if statements
		random, left$, right$, mid$, num$, val,																// keywords for built-in functions
		valFloat {																							// special toString because '%' not valid in variable/enum declaration
			@Override
			public String toString() {
				return "VAL%";
			}
		},
		exception,
		endofline {
			@Override
			public String toString() {
				return "EndOfLine";
			}
		};
		
		@Override
		public String toString() {
			return this.name().toUpperCase();
		}
	}
	
	private String value;
	private TokenTypes newToken;
	
	public Token(TokenTypes newToken, String value) {
		this.newToken = newToken;
		this.value = value;
	}
	
	public Token(TokenTypes newToken) {
		this.newToken = newToken;
	}
	
	/**
	 * Returns the value of the Token
	 * @return this.value - reference to the value of the Token
	 */
	public String getValue() {
		return this.value;
	}
	
	/**
	 * Changes the value of the Token
	 * @param value the new value of the Token
	 */
	public void setValue(String value) {
		this.value = value;
	}
	
	/**
	 * Accessor for the specific Token
	 * @return this.newToken - reference to the Token type
	 */
	public TokenTypes getToken() {
		return this.newToken;
	}
	
	/**
	 * If value is "null", Token is an operator/parenthesis and doesn't have a value, otherwise Token is a number
	 */
	public String toString() {
		if(value == null)
			return this.newToken.toString();
		else
			return this.newToken.toString() + "(" + this.getValue() + ")";
	}
}