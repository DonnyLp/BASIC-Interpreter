import java.io.IOException;
import java.util.LinkedList;

public class Lexer {

    private LinkedList<Token> tokens = new LinkedList<>();
    private CodeHandler handler;
    private Token token;

    private enum state{
        WORD,
        NUMBER,
        IGNORE,
        ENDOFLINE,

    }
    public Lexer(String fileName) throws IOException {
       handler = new CodeHandler(fileName);
       // TO DO: instantiate the linenumber and character position

    }
    // TODO : Finish the remaining requirements for this method
    public LinkedList<Token> lex(){

        // TO DO: Loop While there's still data in the codehandler

        /*

            U:Use state machine w/helper method to indentify the state
            - We iterate through the file
            - make tokens by the cases defined above
            M:
                - While loop to iterate through the file
                - switch case to account for all cases above
                - handle logic for each case
                - create new token to the LinkedList of tokens
                - move the index with swallow()?
         */

        while(!handler.isDone()){

            switch(matchState(handler.peek(handler.getIndex()))){

                // TO DO: if space or tab ignore

                // TO DO: if linefeed create ENDOFLINE TOKEN and append to list, Increment the line number and set char position to 0

                // TO DO: if carriage return (\r), ignore

                // TO DO: if character is letter, call ProcessWord and append to list

                // TO DO: if character is digit, call ProcessDigit and append to list

                //TO DO: handle exception for unknown character
                default -> System.out.println("This is an unknown character");
            }


        }
        return tokens; 
    }



    // TO DO: Create the ProcessWord method that returns a token and accepts letters,digits, and underscores.

    // TO DO : Create the ProcessNumber method that returns a token and accepts 0 - 9 and one decimal

    //helper method to match input char to specific state
    private state matchState(char input){

        state newState = null;

        if(Character.isSpaceChar(input) || input == '\t') newState = state.IGNORE;
        else if(Character.isAlphabetic(input)) newState = state.WORD;
        else if(Character.isDigit(input)) newState = state.NUMBER;
        else if(input == '\n') newState = state.ENDOFLINE;

        return newState;
    }
}
