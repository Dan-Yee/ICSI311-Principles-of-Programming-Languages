import java.util.ArrayList;

/**
 * Specifications of an function invocation statement
 * @author Dan Yee
 */
public class FunctionNode extends Node {
	private Token.TokenTypes funcName;
	private ArrayList<Node> paramList;
	
	public FunctionNode(Token.TokenTypes funcName, ArrayList<Node> paramList) {
		this.funcName = funcName;
		this.paramList = paramList;
	}
	
	/**
	 * Accessor to get the name of this function
	 * @return this.funcName - a reference to the keyword enum representing the name of this function
	 */
	public Token.TokenTypes getName() {
		return this.funcName;
	}
	
	/**
	 * Accessor to get the parameter list of this function
	 * @return this.paramList - a reference to the ArrayList of parameters for this function
	 */
	public ArrayList<Node> getParams() {
		return this.paramList;
	}
	
	/**
	 * toString() override to display the FunctionNode
	 */
	public String toString() {
		return "FunctionNode(" + this.funcName + ", " + this.paramList.toString() + ")";
	}
}
