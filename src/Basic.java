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

        LinkedList<Token> output = lexer.lex();
        Token [] tokens = output.toArray(new Token[0]);
        parser = new Parser(output);

        //Print tokens
//       for(Token token: tokens){
//           System.out.println(token);
//       }

       //Print AST
        System.out.println(parser.parse());

    }
}
