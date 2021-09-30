import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Driver for the Lexer, Parser and Interpreter
 * @author Dan Yee
 */
public class Basic { 
	public static void main(String[] args) throws Exception {
		Lexer newLexer;
		Parser newParser;
		Interpreter newInterpreter;
		ArrayList<Token> lexedTokens;                                                   // ArrayList to store the returned tokens from the Lexer
		StatementsNode parsedLine;														// stores the most recently parsed line from the Parser
		ArrayList<Node> AST = new ArrayList<Node>();									// stores all parsed tokens from the Parser to be passed to Interpreter
		ArrayList<Node> initializedAST;
		
		if(args.length == 0)
			throw new Exception("No CommandLine argument found where one is required!");
		else if(args.length > 1)
			throw new Exception("Multiple CommandLine arguments found where only one is allowed!");
		
		System.out.println("File Name: " + args[0]);
		List<String> textLines = Files.readAllLines(Paths.get(args[0]));
		
		for(int i = 0; i < textLines.size(); i++) {
			newLexer = new Lexer(textLines.get(i));
			lexedTokens = newLexer.Lex();
			
			newParser = new Parser(lexedTokens);
			parsedLine = newParser.parse();
			
			if(parsedLine != null)
				AST.addAll(parsedLine.getStatementsNode()); 
		}
		newInterpreter = new Interpreter(AST);
		initializedAST = newInterpreter.initialize();
		
		newInterpreter.interpret((StatementNode)initializedAST.get(0));
	}
}