import java.io.IOException;
import java.util.LinkedList;

public class Basic {
    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.out.println("Error: There should at least or at most, 1 file passed");
            System.exit(0);
        }

        String fileName = args[0];
        Lexer lexer = new Lexer(fileName);
        Parser parser;
        Interpreter interpreter;

        LinkedList<Token> output = lexer.lex();

        Token [] tokens = output.toArray(new Token[0]);

        // Print the lexed tokens
         System.out.println("Tokens:");
       for(Token token: tokens){
           System.out.println(token);
       }
       System.out.println("\n");

       // Parse the linked list of tokens and print the AST
        parser = new Parser(output);
        StatementsNode astTree = parser.parse();
        System.out.println("AST:");
        System.out.println(astTree);

        //Process the AST
        interpreter = new Interpreter(astTree,false);

        System.out.println("Converting Fahrenheit to Celsius: ");
        interpreter.interpret(astTree.getHead());

    }
}
