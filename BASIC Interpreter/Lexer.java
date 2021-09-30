import java.util.ArrayList;
import java.util.HashMap;
/**
 * Breaks up a string into its value and value
 * @author Dan Yee
 */
public class Lexer {
	private String tokenString;
	
	public Lexer(String tokenString) {
		this.tokenString = tokenString;
	}
	
	/**
	 * Method that takes string member of class instance and lexes it into tokens and values
	 * @return an ArrayList with the lexed tokens and values
	 * @throws Exception when multiple decimal points are found or unknown characters are found
	 */
	public ArrayList<Token> Lex() throws Exception {
		HashMap<String, Token.TokenTypes> knownTokens = new HashMap<String, Token.TokenTypes>();
		knownTokens.put("PRINT", Token.TokenTypes.print);
		knownTokens.put("DATA", Token.TokenTypes.data);
		knownTokens.put("READ", Token.TokenTypes.read);
		knownTokens.put("INPUT", Token.TokenTypes.input);
		knownTokens.put("GOSUB", Token.TokenTypes.gosub);
		knownTokens.put("FOR", Token.TokenTypes.For);
		knownTokens.put("TO", Token.TokenTypes.to);
		knownTokens.put("STEP", Token.TokenTypes.step);
		knownTokens.put("NEXT", Token.TokenTypes.next);
		knownTokens.put("RETURN", Token.TokenTypes.Return);
		knownTokens.put("IF", Token.TokenTypes.If);
		knownTokens.put("THEN", Token.TokenTypes.then);
		
		// hashmap keys for built-in functions
		knownTokens.put("RANDOM", Token.TokenTypes.random);
		knownTokens.put("LEFT$", Token.TokenTypes.left$);
		knownTokens.put("RIGHT$", Token.TokenTypes.right$);
		knownTokens.put("MID$", Token.TokenTypes.mid$);
		knownTokens.put("NUM$", Token.TokenTypes.num$);
		knownTokens.put("VAL", Token.TokenTypes.val);
		knownTokens.put("VAL%", Token.TokenTypes.valFloat);
		
		ArrayList<Token> lexedTokens = new ArrayList<Token>();
		int currentState = 1;                                                                                                                   // stores the current state in the state machine
		int currentPosition;                       																								// stores the current position in the loop - used to check future char's
		boolean hasDecimalPoint = false;                                                                                                        // limits number of decimal points in a value
		String value = "";																														// stores multi-char value values
		
		for(int i = 0;i < this.tokenString.length();i++) {                                                                                      // loops through each character of the String
			currentPosition = i;
			if(Character.isDigit(this.tokenString.charAt(i)))                                                                                   // if the character being looked at is a numerical digit
				currentState = 1;
			else if(this.tokenString.charAt(i) == ' ' || this.tokenString.charAt(i) == '\t')
				continue;
			else if(this.tokenString.charAt(i) == '-')																							// minus sign/negative numbers
				currentState = 2;
			else if(this.tokenString.charAt(i) == '"')                                                                                          
				currentState = 4;
			else if(this.tokenString.charAt(i) == '<' || this.tokenString.charAt(i) == '>')
				currentState = 5;
			else																																// other non-numerical characters except minus sign
				currentState = 3;
			
			switch(currentState) {
				case 1:                                                                                                                         // handles numerical characters
					try {
						value+=this.tokenString.charAt(i);
						if((currentPosition+1) < this.tokenString.length() && this.tokenString.charAt(currentPosition+1) == '.') {              // checks for decimal point as next character
							if(hasDecimalPoint == false)                                                                                        // if decimal value string doesn't already have a decimal point
								continue;             
							else                                                																// throw exception if multiple decimal points
								throw new Exception("Multiple Decimal Points Exception!");
						} else if((currentPosition+1) < this.tokenString.length() && Character.isDigit(this.tokenString.charAt(currentPosition+1))) {     // handles multi-digit numbers
							continue;
						} else {                                                                                                                
							lexedTokens.add(new Token(Token.TokenTypes.number, value));
							value = "";
							hasDecimalPoint = false;
						}
					} catch(Exception e) { 																										// catches any exception thrown and returns to move on to next String
						System.out.println(e+" Moving to Next Line!");
						lexedTokens.add(new Token(Token.TokenTypes.exception));
						return lexedTokens;
					}
					break;
				case 2:																															// handles minus sign/negative numbers
					if((currentPosition-1) >= 0) {																								// checks previous characters before minus sign
						while(this.tokenString.charAt(currentPosition-1) == ' ' || this.tokenString.charAt(currentPosition-1) == '\t')
							currentPosition--;
						if(Character.isDigit(this.tokenString.charAt(currentPosition-1)) || Character.isLetter(this.tokenString.charAt(currentPosition-1)))
							if(lexedTokens.get(lexedTokens.size() - 1).getValue() != null)
								lexedTokens.add(new Token(Token.TokenTypes.minus));
							else
								value+=this.tokenString.charAt(i);
						else
							value+=this.tokenString.charAt(i);
						continue;
					}
					if((currentPosition+1) < this.tokenString.length()) {																		// checks future characters after minus sign
						if(Character.isDigit(this.tokenString.charAt(currentPosition+1)))
							value+=this.tokenString.charAt(i);
						else
							lexedTokens.add(new Token(Token.TokenTypes.minus));
					}
					break;
				case 3:																															// handles non-numerical characters
					try {
						switch(this.tokenString.charAt(i)) {
							case '.':
								if(hasDecimalPoint == false) {																					// if there already isn't a decimal point, add it in and set limiter to true
									value += this.tokenString.charAt(i);
									hasDecimalPoint = true;
								}
								break;
							case '+':
								lexedTokens.add(new Token(Token.TokenTypes.plus));
								break;
							case '*':
								lexedTokens.add(new Token(Token.TokenTypes.multiply));
								break;
							case '/':
								lexedTokens.add(new Token(Token.TokenTypes.divide));
								break;
							case '=':
								lexedTokens.add(new Token(Token.TokenTypes.equals));
								break;
							case '(':
								lexedTokens.add(new Token(Token.TokenTypes.lparen));
								break;
							case ')':
								lexedTokens.add(new Token(Token.TokenTypes.rparen));
								break;
							case ',':
								lexedTokens.add(new Token(Token.TokenTypes.comma));
								break;
							default:																											// handles keywords and other characters
								for(int a = i; a < this.tokenString.length(); a++) {
									// if char is a space OR char is last char of String OR char is a divider
									// Divider tells the program when its time to look back on value String and check its contents
									if(a == this.tokenString.length() - 1 || isDivider(this.tokenString.charAt(a))) {
										if(a == this.tokenString.length() - 1 && this.tokenString.charAt(a) != ' ' && !isDivider(this.tokenString.charAt(a))) {
											value += this.tokenString.charAt(a);
										}
										if(knownTokens.get(value) != null) {
											lexedTokens.add(new Token(knownTokens.get(value.toUpperCase())));
											value = "";
											if(isDivider(this.tokenString.charAt(a)))															// makes sure the operator that the loop stopped at isn't ignored after
												i = a - 1;
											else
												i = a;
											break;
										} else {
											if(value.charAt(value.length() - 1) == '$' || value.charAt(value.length() - 1) == '%')
												if(value.charAt(value.length() - 2) == '$' || value.charAt(value.length() - 2) == '%')
													throw new Exception("Identifier can only contain one of \"$\" or \"%\" is allowed!");
											lexedTokens.add(new Token(Token.TokenTypes.identifier, value));										// handles identifiers
											i += value.length() - 1;
											value = "";
											break;
										}
									} else if(this.tokenString.charAt(a) == ':') {																// handles LABELS										
										lexedTokens.add(new Token(Token.TokenTypes.label, value));
										value = "";
										i = a;																									// updates loop position to after label
										break;
									} else																										// concatenate char's to string otherwise
										value += this.tokenString.charAt(a);
								}
								break;
							}
					} catch(Exception e) {																										// catches exception and returns for next String
						System.out.println(e+" Moving to Next Line!");
						lexedTokens.add(new Token(Token.TokenTypes.exception));
						return lexedTokens;
					}
					break;
				case 4:																															// handles characters surrounded by quotation marks
					value = "";
					for(int a = i+1;a < this.tokenString.length();a++) {
						if(this.tokenString.charAt(a) == '"') {																					// concatenates all char's as STRING until close quote is found
							i = a;
							break;
						} else
							value += this.tokenString.charAt(a);
					}
					lexedTokens.add(new Token(Token.TokenTypes.string, value));
					value="";
					break;
				case 5:																															// handles <, <=, >, >=, <>
					if((currentPosition+1) > this.tokenString.length())
						break;
					
					if(this.tokenString.charAt(i) == '<') {																						// check next char after < for = or >
						switch(this.tokenString.charAt(currentPosition+1)) {
							case '=':
								lexedTokens.add(new Token(Token.TokenTypes.lessThanEquals));
								i++;																											// skip 1 char ahead to account for =
								break;
							case '>':
								lexedTokens.add(new Token(Token.TokenTypes.notequals));
								i++;																											// skip 1 char ahead to account for >
								break;
							default:																											// no recognized char following <
								lexedTokens.add(new Token(Token.TokenTypes.lessThan));
						}
					} else if(this.tokenString.charAt(i) == '>') {																				// check next char after > for =
						switch(this.tokenString.charAt(currentPosition+1)) {
							case '=':
								lexedTokens.add(new Token(Token.TokenTypes.greaterThanEquals));																
								i++;																											// skip 1 char ahead to account for =
								break;
							default:																											// no recognized char following >
								lexedTokens.add(new Token(Token.TokenTypes.greaterThan));
						}
					}
					break;
			}
		}
		lexedTokens.add(new Token(Token.TokenTypes.endofline));																					// adds EndOfLine value to end of lexed string
		return lexedTokens;																														// returns ArrayList of lexed value Strings
	}
	
	/**
	 * boolean to check if current character is a operator or comma
	 * @param value - char being compared
	 * @return - true or false based on the character
	 */
	public boolean isDivider(char value) {
		switch(value) {
			case '+':
			case '-':
			case '*':
			case '/':
			case '=':
			case ',':
			case ' ':
			case '<':
			case '>':
			case '(':
			case ')':
			case '"':
				return true;
		}
		return false;
	}
}