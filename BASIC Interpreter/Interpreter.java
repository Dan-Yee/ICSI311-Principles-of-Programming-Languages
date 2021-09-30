import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;
import java.util.HashMap;

/**
 * Implementation of an Interpreter that will interpret and run the Nodes in the AST created by the Parser
 * @author Dan Yee
 */
public class Interpreter {
	private ArrayList<Node> nodesList;
	// HashMap's for three different data types and labels
	private HashMap<String, Integer> intVars = new HashMap<String, Integer>();
	private HashMap<String, Float> floatVars = new HashMap<String, Float>();
	private HashMap<String, String> strVars = new HashMap<String, String>();
	private HashMap<String, Node> labelStatements = new HashMap<String, Node>();
	// Collection to store contents of DATA statements
	private ArrayList<Object> dataList = new ArrayList<Object>();
	// Scanner for INPUT statements
	private Scanner scanner = new Scanner(System.in);
	
	public Interpreter(ArrayList<Node> nodesList) {
		this.nodesList = nodesList;
	}
	
	/**
	 * Performs preprocessing actions on the AST and prepares it to be interpreted
	 * @return - the modified AST after preprocessing
	 * @throws Exception - method calls may throw an Exception
	 */
	public ArrayList<Node> initialize() throws Exception {
		labelOptimize();
		readOptimize();
		forNextOptimize();
		nextInstruction(); 
		
		return this.nodesList;
	}
	
	/**
	 * Walks the AST and removes and stores LabeledStatementNodes
	 */
	public void labelOptimize() {
		for(int i = 0; i < nodesList.size(); i++) {
			if(nodesList.get(i) instanceof LabeledStatementNode) {
				labelStatements.put(((LabeledStatementNode)nodesList.get(i)).getLabel(), ((LabeledStatementNode)nodesList.get(i)).getStatement());
				nodesList.set(i, ((LabeledStatementNode)nodesList.get(i)).getStatement());
			}
		}
	}
	
	/**
	 * Walks the AST and removes and stores contents of any DATA statements
	 */
	public void readOptimize() {
		for(int i = 0; i < nodesList.size(); i++) {
			if(nodesList.get(i) instanceof DataNode) {
				dataList.addAll(((DataNode)nodesList.remove(i)).getDataList());
				i--;
			}
		}
	}
	
	/**
	 * Walks the AST looking for FOR/NEXT statements and verifies the identifiers match with each other and links them together
	 * @throws Exception - if the identifiers for the pair of FOR/NEXT statements don't match
	 * @throws Exception - if no NEXT statement was found after finding a FOR statement
	 */
	public void forNextOptimize() throws Exception {
		int forIndex = -1;
		int nextIndex = -1;
		
		for(int i = 0; i < nodesList.size(); i++) {
			if(nodesList.get(i) instanceof ForNode) {
				forIndex = i;
				for(int j = i; j < nodesList.size(); j++) {
					if(nodesList.get(j) instanceof NextNode) {
						nextIndex = j;
						if(!(((ForNode)nodesList.get(forIndex)).getVar().getVarName().equals(((NextNode)nodesList.get(nextIndex)).getNextVar().getVarName())))	// compares identifiers for the FOR/NEXT statement
							throw new Exception("Interpreter.forNextOptimize: Variables for FOR and NEXT Statements do not match!");
						break;
					}
				}
				if(nextIndex == -1)
					throw new Exception("Interpreter.forNextOptimize: No NEXT Statement found!");
				((NextNode)nodesList.get(nextIndex)).setNext(nodesList.get(forIndex));
			}
			if(forIndex != -1 && (nextIndex + 1) < nodesList.size()) {
				((ForNode)nodesList.get(forIndex)).setNext(nodesList.get(nextIndex + 1));
				forIndex = -1;
				nextIndex = -1;
			}
		}
	}
	
	/**
	 * Walks the AST and links together each StatementNode such that there is "next" statement to execute
	 */
	public void nextInstruction() {
		for(int i = 0; i < nodesList.size() - 1; i++) {
			((StatementNode)nodesList.get(i)).setNextStatement(((StatementNode)nodesList.get(i + 1)));
		}
	}
	
	/**
	 * Starts with the first statement and interprets each statement according to type
	 * @param statement - the first statement to be interpreted
	 * @throws Exception - method calls to interpret different types of Nodes may throw an Exception
	 */
	public void interpret(StatementNode statement) throws Exception {
		StatementNode nextStatement = statement;
		Stack<Node> goSubStack = new Stack<Node>();
		
		while(nextStatement != null) {
			if(nextStatement instanceof AssignmentNode)
				interpretAssignment(nextStatement);
			else if(nextStatement instanceof PrintNode) {
				interpretPrint(nextStatement);
				System.out.println();
			} else if(nextStatement instanceof InputNode)
				interpretInput(nextStatement);
			else if(nextStatement instanceof ReadNode)
				interpretRead(nextStatement);
			else if(nextStatement instanceof IfNode) {
				if(evaluateBoolOpNode(((IfNode)nextStatement).getOp())) {
					if(labelStatements.get((((IfNode)nextStatement).getIdentifier()).getVarName()) != null)
						nextStatement = (StatementNode)labelStatements.get((((IfNode)nextStatement).getIdentifier()).getVarName());
					continue;
				}
			} else if(nextStatement instanceof ForNode) {
				if(evaluateForCondition(nextStatement))
					nextStatement = nextStatement.getNextStatement();
				else {
					intVars.remove(((ForNode)nextStatement).getVar().getVarName());
					nextStatement = ((StatementNode)((ForNode)nextStatement).getNext());
				}
				continue;
			} else if(nextStatement instanceof NextNode) {
				nextStatement = ((StatementNode)((NextNode)nextStatement).getNext());
				continue;
			} else if(nextStatement instanceof GoSubNode) {
				if(labelStatements.get(((GoSubNode)nextStatement).getGoSub().getVarName()) != null) {
					goSubStack.push(nextStatement.getNextStatement());
					nextStatement = (StatementNode)labelStatements.get(((GoSubNode)nextStatement).getGoSub().getVarName());
					continue;
				} else
					throw new Exception("Unassigned Label Error");
			} else if(nextStatement instanceof ReturnNode) {
				if(!(goSubStack.empty())) {
					nextStatement = (StatementNode)goSubStack.pop();
					continue;
				}
			}
			nextStatement = nextStatement.getNextStatement();
		}
	}
	
	/**
	 * Interprets AssignmentNode's by checking data types and putting them in the correct HashMaps for future reference
	 * @param statement - the assignment statement being interpreted
	 * @throws Exception - if types between identifier and its value don't match
	 */
	public void interpretAssignment(StatementNode statement) throws Exception {
		AssignmentNode assign = (AssignmentNode)statement;
		
		// Handles right side of an assignment statement
		if(assign.getValue() instanceof FunctionNode) {
			interpretAssignment(new AssignmentNode(assign.getVarNode(), interpretFunctions(assign.getValue())));
			return;
		} else if(assign.getValue() instanceof MathOpNode) {
			try {
				interpretAssignment(new AssignmentNode(assign.getVarNode(), evaluateIntMathOp(assign.getValue())));
			} catch(Exception e) {
				interpretAssignment(new AssignmentNode(assign.getVarNode(), evaluateFloatMathOp(assign.getValue())));
			}
			return;
		} else if(assign.getValue() instanceof VariableNode) {
			if(((VariableNode)assign.getValue()).getVarName().indexOf("$") >= 1) {
				if(strVars.get(((VariableNode)assign.getValue()).getVarName()) != null) {
					interpretAssignment(new AssignmentNode(assign.getVarNode(), new StringNode(strVars.get(((VariableNode)assign.getValue()).getVarName()))));
					return;
				} else
					throw new Exception("Unassigned String Variable Error");
			} else if(((VariableNode)assign.getValue()).getVarName().indexOf("%") >= 1) {
				if(floatVars.get(((VariableNode)assign.getValue()).getVarName()) != null) {
					interpretAssignment(new AssignmentNode(assign.getVarNode(), new FloatNode(floatVars.get(((VariableNode)assign.getValue()).getVarName()))));
					return;
				} else
					throw new Exception("Unassigned Float Variable Error");
			} else
				if(intVars.get(((VariableNode)assign.getValue()).getVarName()) != null) {
					interpretAssignment(new AssignmentNode(assign.getVarNode(), new IntegerNode(intVars.get(((VariableNode)assign.getValue()).getVarName()))));
					return;
				} else
					throw new Exception("Unassigned Integer Variable Error");
		}
		
		// Handles assignment statement if right side checks didn't already
		if(assign.getVarNode().getVarName().indexOf("$") >= 1) {
			if(assign.getValue() instanceof StringNode)
				strVars.put(assign.getVarNode().getVarName(), ((StringNode)assign.getValue()).getValue());
			else
				throw new Exception("Type Mismatch: Cannot assign Integer/Float to String");	
		} else if(assign.getVarNode().getVarName().indexOf("%") >= 1) {
			if(assign.getValue() instanceof FloatNode)
				floatVars.put(assign.getVarNode().getVarName(), ((FloatNode)assign.getValue()).getNumValue());
			else
				throw new Exception("Type Mismatch: Cannot assign Integer/String to Float");
		} else
			if(assign.getValue() instanceof IntegerNode)
				intVars.put(assign.getVarNode().getVarName(), ((IntegerNode)assign.getValue()).getNumValue());
			else
				throw new Exception("Type Mismatch: Cannot assign Float/String to Integer");
	}
	
	/**
	 * Interprets PrintNode's by looping its print list and printing them based on data types
	 * @param statement - the print statement being interpreted
	 * @throws Exception - if identifier was unassigned at time of print
	 */
	public void interpretPrint(StatementNode statement) throws Exception {
		PrintNode print = (PrintNode)statement;
		ArrayList<Node> printFunc = new ArrayList<Node>();																	// used for recursive calls to print MathOp's or Functions
		
		for(int i = 0; i < print.getPrintList().size(); i++) {
			if(print.getPrintList().get(i) instanceof IntegerNode)
				System.out.print(((IntegerNode)print.getPrintList().get(i)).getNumValue());
			else if(print.getPrintList().get(i) instanceof FloatNode)
				System.out.print(((FloatNode)print.getPrintList().get(i)).getNumValue());
			else if(print.getPrintList().get(i) instanceof StringNode)
				System.out.print(((StringNode)print.getPrintList().get(i)).getValue());
			else if(print.getPrintList().get(i) instanceof VariableNode) {
				if(((VariableNode)print.getPrintList().get(i)).getVarName().indexOf("$") >= 1) {
					if(strVars.get(((VariableNode)print.getPrintList().get(i)).getVarName()) != null)
						System.out.print(strVars.get(((VariableNode)print.getPrintList().get(i)).getVarName()));
					else
						throw new Exception("Unassigned String Variable Error");
				} else if(((VariableNode)print.getPrintList().get(i)).getVarName().indexOf("%") >= 1) {
					if(floatVars.get(((VariableNode)print.getPrintList().get(i)).getVarName()) != null)
						System.out.print(floatVars.get(((VariableNode)print.getPrintList().get(i)).getVarName()));
					else
						throw new Exception("Unassigned Float Variable Error");
				} else {
					if(intVars.get(((VariableNode)print.getPrintList().get(i)).getVarName()) != null)
						System.out.print(intVars.get(((VariableNode)print.getPrintList().get(i)).getVarName()));
					else
						throw new Exception("Unassigned Integer Variable Error");
				}
			} else if(print.getPrintList().get(i) instanceof MathOpNode) {
				try {
					printFunc.add(evaluateIntMathOp(print.getPrintList().get(i)));
					interpretPrint(new PrintNode(printFunc));
				} catch(Exception e) {
					printFunc.add(evaluateFloatMathOp(print.getPrintList().get(i)));
					interpretPrint(new PrintNode(printFunc));
				}
				printFunc.clear();
			} else if(print.getPrintList().get(i) instanceof FunctionNode) {
				printFunc.add(interpretFunctions(print.getPrintList().get(i)));
				interpretPrint(new PrintNode(printFunc));
				printFunc.clear();
			}
		}
	}
	
	/**
	 * Interprets InputNode's by printing out the prompt and then accepting and properly storing user inputs based on data types
	 * @param statement - the input statement being interpreted
	 * @throws Exception - method call to interpretAssignment(...) may throw an Exception
	 */
	public void interpretInput(StatementNode statement) throws Exception {
		InputNode input = (InputNode)statement;
		ArrayList<Node> Inputprompt = new ArrayList<Node>();																// used to call interpretPrint to print the prompt
		Object userInput;
		
		Inputprompt.add(input.getFirstValue());
		interpretPrint(new PrintNode(Inputprompt));
		
		for(int i = 0; i < input.getVarList().size(); i++) {
			if(((VariableNode)input.getVarList().get(i)).getVarName().indexOf("$") >= 1) {
				userInput = scanner.nextLine();
				interpretAssignment(new AssignmentNode(input.getVarList().get(i), new StringNode((String)userInput)));
			} else if(((VariableNode)input.getVarList().get(i)).getVarName().indexOf("%") >= 1) {
				userInput = Float.parseFloat(scanner.nextLine());
				interpretAssignment(new AssignmentNode(input.getVarList().get(i), new FloatNode((Float)userInput)));
			} else {
				userInput = Integer.parseInt(scanner.nextLine());
				interpretAssignment(new AssignmentNode(input.getVarList().get(i), new IntegerNode((int)userInput)));
			}
		}
	}
	
	/**
	 * Interprets ReadNode's by using interpretAssignment() to check data types and place them in the correct Hash Maps for future reference
	 * @param statement - the read statement being interpreted
	 * @throws Exception - method call to interpretAssignment(...) may throw an Exception
	 */
	public void interpretRead(StatementNode statement) throws Exception {
		ReadNode read = (ReadNode)statement;
		
		for(int i = 0 ; i < read.getReadList().size(); i++) {
			if(dataList.size() != 0)
				interpretAssignment(new AssignmentNode(read.getReadList().get(i), (Node)dataList.remove(0)));
			else
				throw new Exception("Read Error: No DATA to Read");
		}
	}
	
	/**
	 * Interprets FunctionNode's by using Java functions to evaluate requests and return a Node based on the function call
	 * @param statement - the function being interpreted
	 * @return - a new StringNode or IntegerNode or FloatNode based on the function call
	 * @throws Exception - number of arguments not applicable to specified function call
	 * @throws Exception - data types of arguments not valid for specified function call
	 */
	public Node interpretFunctions(Node statement) throws Exception {
		FunctionNode function = (FunctionNode)statement;
		Node firstParam, secondParam, thirdParam;
		int subIndex, subIndex2;
		
		switch(function.getName()) {
			case left$:
				if(function.getParams().size() != 2)
					throw new Exception("Too many or missing arguments for LEFT$, expected 2");
				
				firstParam = function.getParams().get(0);
				secondParam = function.getParams().get(1);
				
				if(firstParam instanceof StringNode && secondParam instanceof IntegerNode) {
					subIndex = ((IntegerNode)secondParam).getNumValue();
					return new StringNode(((StringNode)firstParam).getValue().substring(0, subIndex));
				} else if(firstParam instanceof VariableNode && secondParam instanceof IntegerNode) {
					subIndex = ((IntegerNode)secondParam).getNumValue();
					if(strVars.get(((VariableNode)firstParam).getVarName()) != null)
						return new StringNode(strVars.get(((VariableNode)firstParam).getVarName()).substring(0, subIndex));
					else
						throw new Exception("Unassigned String Variable Reference Error");
				} else
					throw new Exception("Invalid data type in arguments for LEFT$, expected String, Integer");
			case right$:
				if(function.getParams().size() != 2)
					throw new Exception("Too many or missing arguments for LEFT$, expected 2");
				
				firstParam = function.getParams().get(0);
				secondParam = function.getParams().get(1);
				
				if(firstParam instanceof StringNode && secondParam instanceof IntegerNode) {
					subIndex = ((StringNode)firstParam).getValue().length() - ((IntegerNode)secondParam).getNumValue();
					return new StringNode(((StringNode)firstParam).getValue().substring(subIndex));
				} else if(firstParam instanceof VariableNode && secondParam instanceof IntegerNode) {
					subIndex = ((StringNode)firstParam).getValue().length() - ((IntegerNode)secondParam).getNumValue();
					if(strVars.get(((VariableNode)firstParam).getVarName()) != null)
						return new StringNode(strVars.get(((VariableNode)firstParam).getVarName()).substring(subIndex));
					else
						throw new Exception("Unassigned String Variable Reference Error");
				} else
					throw new Exception("Invalid data type in arguments for LEFT$, expected String, Integer");
			case mid$:
				if(function.getParams().size() != 3)
					throw new Exception("Too many or missing arguments for MID$, expected 3");
				
				firstParam = function.getParams().get(0);
				secondParam = function.getParams().get(1);
				thirdParam = function.getParams().get(2);
				
				if(firstParam instanceof StringNode && secondParam instanceof IntegerNode && thirdParam instanceof IntegerNode) {
					subIndex = ((IntegerNode)secondParam).getNumValue();
					subIndex2 = ((IntegerNode)thirdParam).getNumValue() + ((IntegerNode)secondParam).getNumValue();
					
					return new StringNode(((StringNode)firstParam).getValue().substring(subIndex, subIndex2));
				} else if(firstParam instanceof VariableNode && secondParam instanceof IntegerNode && thirdParam instanceof IntegerNode) {
					subIndex = ((IntegerNode)secondParam).getNumValue();
					subIndex2 = ((IntegerNode)thirdParam).getNumValue() + ((IntegerNode)secondParam).getNumValue();
					
					if(strVars.get(((VariableNode)firstParam).getVarName()) != null)
						return new StringNode(strVars.get(((VariableNode)firstParam).getVarName()).substring(subIndex, subIndex2));
					else
						throw new Exception("Unassigned String Variable Reference Error");
				} else {
					throw new Exception("Invalid data type in arguments for MID$, expected String, Integer, Integer");
				}
			case num$:
				if(function.getParams().size() != 1)
					throw new Exception("Too many or missing arguments for NUM$, expected 1");
				
				firstParam = function.getParams().get(0);
				
				if(firstParam instanceof IntegerNode)
					return new StringNode(Integer.toString(((IntegerNode)firstParam).getNumValue()));
				else if(firstParam instanceof FloatNode)
					return new StringNode(Float.toString(((FloatNode)firstParam).getNumValue()));
				else
					throw new Exception("Invalid data type for argument, expected Integer or Float");
			case random:
				if(function.getParams().size() != 0)
					throw new Exception("Too many or missing arguments for RANDOM(), expected 0");
					
				return new IntegerNode((int)(Math.random() * Integer.MAX_VALUE));
			case val:
				if(function.getParams().size() != 1)
					throw new Exception("Too many or missing arguments for VAL, expected 1");
				
				firstParam = function.getParams().get(0);
				
				if(firstParam instanceof StringNode)
					return new IntegerNode(Integer.parseInt(((StringNode)firstParam).getValue()));
				else
					throw new Exception("Invalid data type in argument for VAL, expected String");
			case valFloat:
				if(function.getParams().size() != 1)
					throw new Exception("Too many or missing arguments for VAL%, expected 1");
				
				firstParam = function.getParams().get(0);
				
				if(firstParam instanceof StringNode)
					return new FloatNode(Float.parseFloat(((StringNode)firstParam).getValue()));
				else
					throw new Exception("Invalid data type in argument for VAL%, expected String");
			default:
				throw new Exception("Unrecognized Function Error");
		}
	}
	
	/**
	 * Evaluates and returns an IntegerNode based on an arithmetic operation specified in a MathOpNode
	 * @param statement - the Integer based MathOpNode being evaluated
	 * @return - a new IntegerNode based on the evaluated value of the MathOpNode
	 * @throws Exception - when an unassigned variable is referenced or the two data types between the operands don't match
	 */
	public Node evaluateIntMathOp(Node statement) throws Exception {
		MathOpNode mathOp = (MathOpNode)statement;
		Node leftOperand = mathOp.getLeftOperand();
		Node rightOperand = mathOp.getRightOperand();
		int left = 0, right = 0;
		
		if(leftOperand instanceof MathOpNode)
			leftOperand = evaluateIntMathOp(leftOperand);
		if(rightOperand instanceof MathOpNode)
			rightOperand = evaluateIntMathOp(rightOperand);
		
		if(leftOperand instanceof IntegerNode)
			left = ((IntegerNode)leftOperand).getNumValue();
		else if(leftOperand instanceof VariableNode)
			if(intVars.get(((VariableNode)leftOperand).getVarName()) != null)
				left = intVars.get(((VariableNode)leftOperand).getVarName());
			else
				if(floatVars.get(((VariableNode)leftOperand).getVarName()) != null)
					throw new Exception("Cannot perform arithmetic operation on two different data types, expected Integers");
				else
					throw new Exception("Unassigned Integer Variable Error");
		else
			throw new Exception("Cannot perform arithmetic operation on two different data types, expected Integers");
		
		if(rightOperand instanceof IntegerNode)
			right = ((IntegerNode)rightOperand).getNumValue();
		else if(rightOperand instanceof VariableNode)
			if(intVars.get(((VariableNode)rightOperand).getVarName()) != null)
				right = intVars.get(((VariableNode)rightOperand).getVarName());
			else
				if(floatVars.get(((VariableNode)leftOperand).getVarName()) != null)
					throw new Exception("Cannot perform arithmetic operation on two different data types, expected Integers");
				else
					throw new Exception("Unassigned Integer Variable Error");
		else
			throw new Exception("Cannot perform arithmetic operation on two different data types, expected Integers");
		
		switch(mathOp.getOperator()) {
			case plus:
				return new IntegerNode((left + right));
			case minus:
				return new IntegerNode((left - right));
			case multiply:
				return new IntegerNode((left * right));
			case divide:
				return new IntegerNode((left / right));
			default:
				throw new Exception("Error Evaluating Integer MathOpNode");
		}
	}
	
	/**
	 * Evaluates and returns a FloatNode based on an arithmetic operation specified in a MathOpNode
	 * @param statement - the Float based MathOpNode being evaluated
	 * @return - a new FloatNode based on the value of the evaluated MathOpNode expression
	 * @throws Exception - if the data types of the two operands don't match or if a variable was unassigned
	 */
	public Node evaluateFloatMathOp(Node statement) throws Exception {
		MathOpNode mathOp = (MathOpNode)statement;
		Node leftOperand = mathOp.getLeftOperand();
		Node rightOperand = mathOp.getRightOperand();
		float left = 0, right = 0;
		
		if(leftOperand instanceof MathOpNode)
			leftOperand = evaluateFloatMathOp(leftOperand);
		if(rightOperand instanceof MathOpNode)
			rightOperand = evaluateFloatMathOp(rightOperand);
		
		if(leftOperand instanceof FloatNode)
			left = ((FloatNode)leftOperand).getNumValue();
		else if(leftOperand instanceof VariableNode)
			if(floatVars.get(((VariableNode)leftOperand).getVarName()) != null)
				left = floatVars.get(((VariableNode)leftOperand).getVarName());
			else 
				if(intVars.get(((VariableNode)leftOperand).getVarName()) != null)
					throw new Exception("Cannot perform arithmetic operation on two different data types, expected Floats");
				else
					throw new Exception("Unassigned Float Variable Error");
		else
			throw new Exception("Cannot perform arithmetic operation on two different data types, expected Floats");
		
		if(rightOperand instanceof FloatNode)
			right = ((FloatNode)rightOperand).getNumValue();
		else if(rightOperand instanceof VariableNode)
			if(floatVars.get(((VariableNode)rightOperand).getVarName()) != null)
				right = floatVars.get(((VariableNode)rightOperand).getVarName());
			else 
				if(intVars.get(((VariableNode)leftOperand).getVarName()) != null)
					throw new Exception("Cannot perform arithmetic operation on two different data types, expected Floats");
				else
					throw new Exception("Unassigned Float Variable Error");
		else
			throw new Exception("Cannot perform arithmetic operation on two different data types, expected Floats");
		
		switch(mathOp.getOperator()) {
			case plus:
				return new FloatNode((left + right));
			case minus:
				return new FloatNode((left - right));
			case multiply:
				return new FloatNode((left * right));
			case divide:
				return new FloatNode((left / right));
			default:
				throw new Exception("Error Evaluating Float MathOpNode");
		}
	}
	
	/**
	 * Compares and returns a boolean based on the operator and operands of a BooleanOperationNode
	 * @param statement - the BooleanOperationNode being evaluated
	 * @return true - if the boolean statement evaluates to true
	 * @return false - if the boolean statement evaluates to false
	 * @throws Exception - if the two types being compared don't match
	 */
	public boolean evaluateBoolOpNode(Node statement) throws Exception {
		BooleanOperationNode boolOpNode = (BooleanOperationNode)statement;
		Node leftOperand = boolOpNode.getLeftExp();
		Node rightOperand = boolOpNode.getRightExp();
		Number left = 0, right = 0;
		
		if(leftOperand instanceof MathOpNode) {
			try {
				leftOperand = evaluateIntMathOp(leftOperand);
			} catch(Exception e) {
				leftOperand = evaluateFloatMathOp(leftOperand);
			}
		} else if(leftOperand instanceof VariableNode) {
			if(((VariableNode)leftOperand).getVarName().indexOf("%") >= 1) {
				if(floatVars.get(((VariableNode)leftOperand).getVarName()) != null)
					left = floatVars.get(((VariableNode)leftOperand).getVarName());
				else
					throw new Exception("BooleanOperationNode: Unassigned Float Error");
			} else {
				if(intVars.get(((VariableNode)leftOperand).getVarName()) != null)
					left = intVars.get(((VariableNode)leftOperand).getVarName());
				else
					throw new Exception("BooleanOperationNode: Unassigned Integer Error");
			}
		}
		
		if(leftOperand instanceof IntegerNode)
			left = ((IntegerNode)leftOperand).getNumValue();
		else if(leftOperand instanceof FloatNode)
			left = ((FloatNode)leftOperand).getNumValue();
		
		if(rightOperand instanceof MathOpNode) {
			try {
				rightOperand = evaluateIntMathOp(rightOperand);
			} catch(Exception e) {
				rightOperand = evaluateFloatMathOp(rightOperand);
			}
		} else if(rightOperand instanceof VariableNode) {
			if(((VariableNode)rightOperand).getVarName().indexOf("%") >= 1) {
				if(floatVars.get(((VariableNode)rightOperand).getVarName()) != null)
					right = floatVars.get(((VariableNode)rightOperand).getVarName());
				else
					throw new Exception("BooleanOperationNode: Unassigned Float Error");
			} else {
				if(intVars.get(((VariableNode)rightOperand).getVarName()) != null)
					right = intVars.get(((VariableNode)rightOperand).getVarName());
				else
					throw new Exception("BooleanOperationNode: Unassigned Integer Error");
			}
		}
		
		if(rightOperand instanceof IntegerNode)
			right = ((IntegerNode)rightOperand).getNumValue();
		else if(rightOperand instanceof FloatNode)
			right = ((FloatNode)rightOperand).getNumValue();
		
		if(left instanceof Integer && right instanceof Integer) {
			switch(boolOpNode.getOp()) {
				case lessThan:
					return left.intValue() < right.intValue();
				case lessThanEquals:
					return left.intValue() <= right.intValue();
				case greaterThan:
					return left.intValue() > right.intValue();
				case greaterThanEquals:
					return left.intValue() >= right.intValue();
				case notEquals:
					return left.intValue() != right.intValue();
				case equals:
					return left.intValue() == right.intValue();
				default:
					throw new Exception("BooleanOperationNode: Invalid Operator Error");
			}
		} else if(left instanceof Float && right instanceof Float) {
			switch(boolOpNode.getOp()) {
				case lessThan:
					return left.floatValue() < right.floatValue();
				case lessThanEquals:
					return left.floatValue() <= right.floatValue();
				case greaterThan:
					return left.floatValue() > right.floatValue();
				case greaterThanEquals:
					return left.floatValue() >= right.floatValue();
				case notEquals:
					return left.floatValue() != right.floatValue();
				case equals:
					return left.floatValue() == right.floatValue();
				default:
					throw new Exception("BooleanOperationNode: Invalid Operator Error");
			}
		} else
			throw new Exception("Cannot Compare two different data types, expected (Integer v. Integer) or (Float v. Float)");
	}
	
	/**
	 * Evaluates the condition of the For statement and returns true or false based on whether or not it is still valid
	 * @param statement - the for statement being evaluated
	 * @return - true if the condition of the for statement is still valid
	 * @return false - if the condition of the for statement is invalid
	 * @throws Exception - when Integer's aren't used 
	 */
	public boolean evaluateForCondition(StatementNode statement) throws Exception {
		ForNode fNode = (ForNode)statement;
		
		if(fNode.getVar().getVarName().indexOf("$") >= 1 || fNode.getVar().getVarName().indexOf("%") >= 1)
			throw new Exception("For Statement only supports Integers");
		
		if(intVars.get(fNode.getVar().getVarName()) == null)
			interpretAssignment(new AssignmentNode(fNode.getVar(), fNode.getStartValue()));
		else
			interpretAssignment(new AssignmentNode(fNode.getVar(), new IntegerNode(intVars.get(fNode.getVar().getVarName()) + fNode.getStepValue().getNumValue())));
		
		if(fNode.getStepValue().getNumValue() > 0)
			return intVars.get(fNode.getVar().getVarName()) <= fNode.getFinalValue().getNumValue();
		else if(fNode.getStepValue().getNumValue() < 0)
			return intVars.get(fNode.getVar().getVarName()) >= fNode.getFinalValue().getNumValue();
		else
			throw new Exception("Error Evaluating For Condition");
	}
}