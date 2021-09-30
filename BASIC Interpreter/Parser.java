import java.util.ArrayList;

/**
 * Class that parses tokens created in Lexer class
 * @author Dan Yee
 */
public class Parser {
	private ArrayList<Token> tokenList;
	
	public Parser(ArrayList<Token> tokenList) {
		this.tokenList = tokenList;
	}
	
	/**
	 * Parses a list of tokens by calling statements()
	 * @return statements() - a instance of a StatementsNode
	 * @throws Exception - statements() is top level of a chain of method calls, some of which may throw an Exception
	 */
	public StatementsNode parse() throws Exception {
		return statements();
	}
	
	/**
	 * continuously calls statement() which checks all 3 types of handles statements to parse valid statements
	 * @return new StatementsNode(statementNodes) - a new instance of a StatementsNode class using an ArrayList
	 * @throws Exception - statement() calls methods that may throw an Exception
	 */
	public StatementsNode statements() throws Exception {
		ArrayList<Node> statementNodes = new ArrayList<Node>();
		Node statement;
		
		while((statement = statement()) != null) {
			statementNodes.add(statement);
		}
		
		if(statementNodes.size() != 0)
			return new StatementsNode(statementNodes);
		return null;
	}
	
	/**
	 * Attempts to parse for each type of possible statement currently supported or returns null
	 * @return statement - an instance of a specific Node type based on where it was returned
	 * @throws Exception - method calls in this body may throw Exception
	 * @throws Exception - line contains multiple types of Statements where only one type can exist
	 */
	public Node statement() throws Exception {
		Node statement;
		Token labelValue;
		boolean isLabel = false;
		
		if((labelValue = matchAndRemove(Token.TokenTypes.label)) != null)
			isLabel = true;
		
		if((statement = assignment()) != null) {
			if(isLabel)
				return new LabeledStatementNode(labelValue.getValue(), statement);
			return statement;
		} else if((statement = printStatement()) != null) {
			if(isLabel)
				return new LabeledStatementNode(labelValue.getValue(), statement);
			return statement;
		} else if((statement = readStatement()) != null) {
			if(isLabel)
				return new LabeledStatementNode(labelValue.getValue(), statement);
			return statement;
		} else if((statement = dataStatement()) != null) {
			if(isLabel)
				return new LabeledStatementNode(labelValue.getValue(), statement);
			return statement;
		} else if((statement = inputStatement()) != null) {
			if(isLabel)
				return new LabeledStatementNode(labelValue.getValue(), statement);
			return statement;
		} else if((statement = forStatement()) != null) {
			if(isLabel)
				return new LabeledStatementNode(labelValue.getValue(), statement);
			return statement;
		} else if((statement = nextStatement()) != null) {
			if(isLabel)
				return new LabeledStatementNode(labelValue.getValue(), statement);
			return statement;
		} else if((statement = goSubStatement()) != null) {
			if(isLabel)
				return new LabeledStatementNode(labelValue.getValue(), statement);
			return statement;
		} else if((statement = returnStatement()) != null) {
			if(isLabel)
				return new LabeledStatementNode(labelValue.getValue(), statement);
			return statement;
		} else if((statement = ifStatement()) != null) {
			if(isLabel)
				return new LabeledStatementNode(labelValue.getValue(), statement);
			return statement;
		}
		return null;
	}
	
	/**
	 * Handles "PRINT" statements
	 * @return new PrintNode(listChecker) - a new instance of a PrintNode that represents things to be printed out
	 * @throws Exception - method call in this body may throw an Exception
	 */
	public StatementNode printStatement() throws Exception {
		ArrayList<Node> listChecker;
		
		if(matchAndRemove(Token.TokenTypes.print) == null)
			return null;
		if((listChecker = printList()) == null)
			return null;
		return new PrintNode(listChecker);
	}
	
	/**
	 * Handles comma separated expressions following "PRINT" token
	 * @return printExp - arraylist of MathOpNode containing parsed comma separated expressions
	 * @throws Exception - matched a comma that wasn't followed by an expression
	 */
	public ArrayList<Node> printList() throws Exception {
		ArrayList<Node> printExp = new ArrayList<Node>();
		Node expression;
		
		do {
			if((expression = expression()) != null)
				printExp.add(expression);
			else 
				throw new Exception("Parser.printList: Expression expected!");
		} while(matchAndRemove(Token.TokenTypes.comma) != null);
		return printExp;
	}
	
	/**
	 * Handles assignment in the format of VARIABLE EQUALS EXPRESSION
	 * @return new AssignmentNode(new VariableNode(token.getValue()), expression()) - matched an identifier, followed by equals, followed by an expression
	 * @throws Exception - expression() call may throw an exception
	 * @throws Exception - expression() call returned null, incomplete assignment
	 */
	public StatementNode assignment() throws Exception {
		Token token;
		Node exp;
		
		if((token = matchAndRemove(Token.TokenTypes.identifier)) == null)
			return null;
		else
			if(matchAndRemove(Token.TokenTypes.equals) != null)
				if((exp = expression()) == null)
					throw new Exception("Parser.assignment: Expression missing!");
				else
					return new AssignmentNode(new VariableNode(token.getValue()), exp);
			else																						// didn't match equals sign after identifier, replace removed identifier as it can be part of a expression
				this.tokenList.add(0, token);
		return null;
	}
	
	/**
	 * Handles comma separated Identifiers
	 * @return null - if readList() returned null
	 * @return new ReadNode(listChecker) - a new ReadNode using the arraylist returned from readList()
	 */
	public StatementNode readStatement() {
		ArrayList<VariableNode> listChecker;
		
		if(matchAndRemove(Token.TokenTypes.read) == null)
			return null;
		if((listChecker = varList()) == null)
			return null;
		return new ReadNode(listChecker);
	}
	
	/**
	 * Parses and returns a list of VariableNodes to be used for a ReadNode
	 * @return null - if an identifier to be parsed into a VariableNode wasn't found
	 * @return varNodeList - list of VariableNodes to be used for a new ReadNode
	 */
	public ArrayList<VariableNode> varList() {
		ArrayList<VariableNode> varNodeList = new ArrayList<VariableNode>();
		Token identifier;
		
		do {
			if((identifier = matchAndRemove(Token.TokenTypes.identifier)) != null)
				varNodeList.add(new VariableNode(identifier.getValue()));
			else
				return null;
		} while(matchAndRemove(Token.TokenTypes.comma) != null);
		return varNodeList;
	}
	
	/**
	 * Handles comma separated numbers and Strings
	 * @return null - if dataList() returned null
	 * @return new DataNode(listChecker) - a new DataNode using the list returned from dataList()
	 */
	public StatementNode dataStatement() {
		ArrayList<Node> listChecker;
		
		if(matchAndRemove(Token.TokenTypes.data) == null)
			return null;
		if((listChecker = dataList()) == null)
			return null;
		return new DataNode(listChecker);
	}
	
	/**
	 * Parses and returns a list of StringNode, IntegerNode, and FloatNode to be used for a DataNode
	 * @return null - if Token wasn't a valid type for DataNode
	 * @return dataNodeList - list of StringNode, IntegerNode, and FloatNode to be used for a new DataNode
	 */
	public ArrayList<Node> dataList() {
		ArrayList<Node> dataNodeList = new ArrayList<Node>();
		Token data;
		
		do {
			if((data = matchAndRemove(Token.TokenTypes.string)) != null)
				dataNodeList.add(new StringNode(data.getValue()));
			else if((data = matchAndRemove(Token.TokenTypes.number)) != null) {
				if(data.getValue().indexOf(".") > 0)
					dataNodeList.add(new FloatNode(Float.parseFloat(data.getValue())));
				else
					dataNodeList.add(new IntegerNode(Integer.parseInt(data.getValue())));
			} else
				return null;
		} while(matchAndRemove(Token.TokenTypes.comma) != null);
		return dataNodeList;
	}
	
	/**
	 * Handles an input statement in the (Lexed) format: INPUT IDENTIFIER | STRING {COMMA IDENTIFIER}
	 * @return new InputNode(firstValue, varList) - a new InputNode with a specific first value and a list of VariableNodes
	 * @throws Exception - if input statement is in invalid format
	 */
	public StatementNode inputStatement() throws Exception {
		ArrayList<VariableNode> varList;
		Token value;
		Node firstValue;
		
		if(matchAndRemove(Token.TokenTypes.input) == null)
			return null;
		
		if((value = matchAndRemove(Token.TokenTypes.string)) != null)
			firstValue = new StringNode(value.getValue());
		else if((value = matchAndRemove(Token.TokenTypes.identifier)) != null)
			firstValue = new VariableNode(value.getValue());
		else 
			throw new Exception("Parser.inputStatement: String or Variable Expected after INPUT");
		matchAndRemove(Token.TokenTypes.comma);															// handling of optional comma following first value
		
		if((varList = varList()) == null)																// reuse readList() because input also has list of VariableNodes
			return null;
		
		return new InputNode(firstValue, varList);
	}
	
	/**
	 * Handles subroutine statements with format "GOSUB IDENTIFIER"
	 * @return new GoSubNode(new VariableNode(identifier.getValue())) - a new node representing a GOSUB token
	 */
	public StatementNode goSubStatement() {
		Token identifier;
		
		if(matchAndRemove(Token.TokenTypes.gosub) == null)
			return null;
		
		if((identifier = matchAndRemove(Token.TokenTypes.identifier)) != null)
			return new GoSubNode(new VariableNode(identifier.getValue()));
		
		return null;
	}
	
	/**
	 * Handles RETURN token
	 * @return new ReturnNode() - a new ReturnNode representing RETURN
	 */
	public StatementNode returnStatement() {
		if(matchAndRemove(Token.TokenTypes.Return) != null)
			return new ReturnNode();
		return null;
	}
	
	/**
	 * Handles for statements with format "FOR ASSIGNMENT TO INT (STEP INT)" with (STEP INT) being optional
	 * @return new ForNode(assign, finalValue, Integer.parseInt(numValue.getValue())) - a new ForNode representing a for loop statement with a specified STEP
	 * @return new ForNode(assign, finalValue) - a new ForNode representing a for loop statement without a specified STEP
	 * @return null - when format of accepted statement isn't correct
	 * @throws Exception - assignment() method call may throw an exception
	 */
	public StatementNode forStatement() {
		VariableNode var;
		IntegerNode startValue;
		IntegerNode finalValue;
		Token tempValue;
																											// comments because this section is a little complicated
		if(matchAndRemove(Token.TokenTypes.For) == null)													// match and remove FOR token
			return null;
		if((tempValue = matchAndRemove(Token.TokenTypes.identifier)) == null)								// match and remove identifier for the variable
			return null;
		var = new VariableNode(tempValue.getValue());
		if(matchAndRemove(Token.TokenTypes.equals) == null)													// match and remove EQUALS token
			return null;
		if((tempValue = matchAndRemove(Token.TokenTypes.number)) == null)									// match and remove NUMBER for the start value
			return null;
		startValue = new IntegerNode(Integer.parseInt(tempValue.getValue()));
		if(matchAndRemove(Token.TokenTypes.to) == null)														// match and remove TO token
			return null;
		if((tempValue = matchAndRemove(Token.TokenTypes.number)) == null)									// match and remove NUMBER for the final value
			return null;
		finalValue = new IntegerNode(Integer.parseInt(tempValue.getValue()));
		if(matchAndRemove(Token.TokenTypes.step) != null) {													// match and remove STEP (optional)
			if((tempValue = matchAndRemove(Token.TokenTypes.number)) != null)								// match and remove NUMBER following STEP for step value
				return new ForNode(var, startValue, finalValue, new IntegerNode(Integer.parseInt(tempValue.getValue())));
			else
				return null;
		} else
			return new ForNode(var, startValue, finalValue);												// if STEP wasn't there, default step value is 1
	}
	
	/**
	 * Handles statement in the format of NEXT IDENTIFIER
	 * @return new NextNode(new VariableNode(identifier.getValue())) - a new NextNode representing a next statement
	 * @return null - if NEXT token wasn't matched (not a Next Statement)
	 * @return null - if next statement was invalid
	 */
	public StatementNode nextStatement() {
		Token identifier;
		if(matchAndRemove(Token.TokenTypes.next) == null)
			return null;
		if((identifier = matchAndRemove(Token.TokenTypes.identifier)) != null)
			return new NextNode(new VariableNode(identifier.getValue()));
		return null;
	}
	
	/**
	 * Handles a statement in the format IF BooleanExpression THEN IDENTIFIER
	 * @return return new IfNode(boolExp, new VariableNode(temp.getValue())) - a new IfNode based on the boolean expression and identifier in the statement
	 * @return null - if IF token wasn't matched (not an IF statement)
	 * @throws Exception - if the format of the IF Statement after IF token is invalid
	 */
	public StatementNode ifStatement() throws Exception {
		BooleanOperationNode boolExp;
		Token temp;
		
		if(matchAndRemove(Token.TokenTypes.If) == null)
			return null;
		if((boolExp = booleanExpression()) == null)
			throw new Exception("Boolean Expression expected, but not found!");
		if(matchAndRemove(Token.TokenTypes.then) == null)
			throw new Exception("\"THEN\" expected, but not found!");
		if((temp = matchAndRemove(Token.TokenTypes.identifier)) == null)
			throw new Exception("Label name expected, but not found!");
		return new IfNode(boolExp, new VariableNode(temp.getValue()));
	}
	
	/**
	 * Handles boolean operation statements in the format of EXPRESSION BOOLEAN_OPERATOR EXPRESSION
	 * @return return new BooleanOperationNode(leftExpression, operator, rightExpression) - a new BooleanOperationNode based on 2 expressions and an operator
	 * @return null - if leftExpression is null
	 * @throws Exception - if a boolean operator wasn't found
	 */
	public BooleanOperationNode booleanExpression() throws Exception {
		Node leftExpression;
		Node rightExpression;
		BooleanOperationNode.boolOperators operator = null;

		if((leftExpression = expression()) == null)
			return null;
		if(this.tokenList.size() != 0) {
			switch(this.tokenList.remove(0).getToken()) {
				case lessThan:
					operator = BooleanOperationNode.boolOperators.lessThan;
					break;
				case lessThanEquals:
					operator = BooleanOperationNode.boolOperators.lessThanEquals;
					break;
				case greaterThan:
					operator = BooleanOperationNode.boolOperators.greaterThan;
					break;
				case greaterThanEquals:
					operator = BooleanOperationNode.boolOperators.greaterThanEquals;
					break;
				case notequals:
					operator = BooleanOperationNode.boolOperators.notEquals;
					break;
				case equals:
					operator = BooleanOperationNode.boolOperators.equals;
					break;
				default:
					throw new Exception("Boolean Operator expected, but not found!");
			}
		}
		if((rightExpression = expression()) == null)
			throw new Exception("Parser.booleanExpression: Right Expression missing!");
		if(operator != null)
			return new BooleanOperationNode(leftExpression, operator, rightExpression);
		return null;
	}
	
	/**
	 * Handles built-in functions in the format FUNCTION_NAME ParameterList
	 * @return new FunctionNode(funcName, paramList) - a new FunctionNode based on the function name recognized and a list of parameters
	 * @return null if format for FunctionNode isn't followed
	 * @throws Exception - Method call to paramList() can throw an Exception
	 */
	public Node functionInvocation() throws Exception {
		Token temp;
		Token.TokenTypes funcName = null;
		ArrayList<Node> paramList;
		
		if(this.tokenList.size() != 0) {
			temp = this.tokenList.remove(0);
			switch(temp.getToken()) {
				case random:
					funcName = Token.TokenTypes.random;
					break;
				case left$:
					funcName = Token.TokenTypes.left$;
					break;
				case right$:
					funcName = Token.TokenTypes.right$;
					break;
				case mid$:
					funcName = Token.TokenTypes.mid$;
					break;
				case num$:
					funcName = Token.TokenTypes.num$;
					break;
				case val:
					funcName = Token.TokenTypes.val;
					break;
				case valFloat:
					funcName = Token.TokenTypes.valFloat;
					break;
				default:																					// not a built-in function, replace the removed Token back onto the list and return null
					this.tokenList.add(0, temp);		
					return null;
			}
		}
		paramList = paramList();
		if(paramList != null)
			return new FunctionNode(funcName, paramList);
		return null;
	}
	
	/**
	 * Parses and returns a list of MathOpNodes and/or StringNodes to be used as the parameter list for FunctionNode
	 * @return paramList - the ArrayList of MathOpNodes and/or StringNodes
	 * @throws Exception - Method call to expression() may throw an exception
	 */
	public ArrayList<Node> paramList() throws Exception {
		Node expParam;
		Token stringParam;
		ArrayList<Node> paramList = new ArrayList<Node>();
		
		if(matchAndRemove(Token.TokenTypes.lparen) == null)
			throw new Exception("Parser.paramList: Left Parentheses missing from function header!");
		do {
			if((expParam = expression()) != null)
				paramList.add(expParam);
			else if((stringParam = matchAndRemove(Token.TokenTypes.string)) != null)
				paramList.add(new StringNode(stringParam.getValue()));
		} while(matchAndRemove(Token.TokenTypes.comma) != null);
		if(matchAndRemove(Token.TokenTypes.rparen) == null)
			throw new Exception("Parser.paramList: Right Parentheses missing to end parameter list!");
		return paramList;
	}
	
	/**
	 * Handles two numbers being added or subtracted from each other
	 * @return new MathOpNode(leftOperand, operator, rightOperand) - AST node based on the list of tokens invoked on
	 * @return leftOperand - if no operator was matched after the leftOperand
	 * @return funcInvod - if functionInvocation is not null (function has higher priorty than expression)
	 * @return null - if leftOperand is null
	 * @throws Exception - regular expression cannot be properly created due to missing operands
	 */
	public Node expression() throws Exception {
		Node leftOperand;
		Node rightOperand;
		MathOpNode.Operators operator;
		Node funcInvoc;
		
		if((funcInvoc = functionInvocation()) != null) {
			return funcInvoc;
		}
		
		leftOperand = term();
		if(leftOperand == null)
			return null;
		if(matchAndRemove(Token.TokenTypes.plus) != null)
			operator = MathOpNode.Operators.plus;
		else {
			if(matchAndRemove(Token.TokenTypes.minus) != null)
				operator = MathOpNode.Operators.minus;
			else
				return leftOperand;
		}
		
		rightOperand = term();
		if(rightOperand == null)
			throw new Exception("Parser.expression: Right Operand Missing");
		
		while(matchAndRemove(Token.TokenTypes.endofline) == null) {
			leftOperand = new MathOpNode(leftOperand, operator, rightOperand);
			
			if(matchAndRemove(Token.TokenTypes.plus) != null)
				operator = MathOpNode.Operators.plus;
			else {
				if(matchAndRemove(Token.TokenTypes.minus) != null)
					operator = MathOpNode.Operators.minus;
				else
					return leftOperand;
			}
			
			rightOperand = term();
			if(rightOperand == null)
				throw new Exception("Parser.expression: Right Operand Missing");
		}
		return new MathOpNode(leftOperand, operator, rightOperand);
	}
	
	/**
	 * Handles two numbers being multiplied or divided by each other
	 * @return new MathOpNode(leftOperand, operator, rightOperand) - AST node based on list of tokens invoked on
	 * @return leftOperand - if no operator was matched after left operand
	 * @return null - if leftOperand is null
	 * @throws Exception - regular expression cannot be formed due to missing operands
	 */
	public Node term() throws Exception {
		Node leftOperand;
		Node rightOperand;
		MathOpNode.Operators operator;
		
		leftOperand = factor();
		if(leftOperand == null)
			return null;
		if(matchAndRemove(Token.TokenTypes.multiply) != null)
			operator = MathOpNode.Operators.multiply;
		else {
			if(matchAndRemove(Token.TokenTypes.divide) != null)
				operator = MathOpNode.Operators.divide;
			else
				return leftOperand;
		}
		
		rightOperand = factor();
		if(rightOperand == null)
			throw new Exception("Parser.term: Right Operand Missing");
		
		while(matchAndRemove(Token.TokenTypes.endofline) == null) {
			leftOperand = new MathOpNode(leftOperand, operator, rightOperand);
			
			if(matchAndRemove(Token.TokenTypes.multiply) != null)
				operator = MathOpNode.Operators.multiply;
			else {
				if(matchAndRemove(Token.TokenTypes.divide) != null)
					operator = MathOpNode.Operators.divide;
				else
					return leftOperand;
			}
			
			rightOperand = term();
			if(rightOperand == null)
				throw new Exception("Parser.term: Right Operand Missing");
		}
		return new MathOpNode(leftOperand, operator, rightOperand);
	}
	
	/**
	 * Handles parenthesis and determines if a number is an Integer or Float
	 * @return new FloatNode(Float.parseFloat(number.getValue())) - number is a Float
	 * @return new IntegerNode(Integer.parseInt(number.getValue())) - number is an Integer
	 * @return new VariableNode(tokValue.getValue()) - Token is a variable
	 * @return new StringNode(tokValue.getValue()) - Token is a string
	 * @return null - Token is not a number or left parenthesis
	 * @throws Exception - expression() method call may throw an Exception
	 */
	public Node factor() throws Exception {
		Node temp;
		Token tokValue;
		
		if((tokValue = matchAndRemove(Token.TokenTypes.number)) != null) {
			if(tokValue.getValue().indexOf(".") > 0)
				return new FloatNode(Float.parseFloat(tokValue.getValue()));
			else
				return new IntegerNode(Integer.parseInt(tokValue.getValue()));
		} else if(matchAndRemove(Token.TokenTypes.lparen) != null) {
			temp = expression();
			if(matchAndRemove(Token.TokenTypes.rparen) == null)											// force match a right parenthesis to keep expression complete
				throw new Exception("Parser.factor: Right Parenthesis Missing from Expression!");
			return temp;
		} else if((tokValue = matchAndRemove(Token.TokenTypes.identifier)) != null) 					// handles identifiers (variables)
			return new VariableNode(tokValue.getValue());
		else if((tokValue = matchAndRemove(Token.TokenTypes.string)) != null)
			return new StringNode(tokValue.getValue());
		return null;
	}
	
	/**
	 * Compares two enum of TokenTypes to see if they match
	 * @param matchToThis - reference to the TokenType to match to
	 * @return this.tokenList.remove(0) - reference to the element that was matched
	 * @return null - if the token didn't match
	 */
	public Token matchAndRemove(Token.TokenTypes matchToThis) {
		if(this.tokenList.size() != 0 && this.tokenList.get(0).getToken() == matchToThis)
			return this.tokenList.remove(0);
		else
			return null;
	}
}