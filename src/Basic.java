import java.io.IOException;
import java.util.LinkedList;

public class Basic {
    public static void main(String[] args) throws IOException {

        if (args.length != 1) {
            System.out.println("Error: There should at least or at most, 1 file passed!");
            System.exit(0);
        }

        String fileName = args[0];
        Lexer lexer = new Lexer(fileName);

        LinkedList<Token> output = lexer.lex();

        // TO DO : Print each token out from lex method
        int counter = 0;
        while(!output.isEmpty()){
            System.out.println(output.get(counter).toString());
            counter++;
        }

    }
}
