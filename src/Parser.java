import java.util.LinkedList;
import java.util.Optional;


public class Parser {

    //Member variables
    private final TokenHandler handler;


    //Methods
    public Parser(LinkedList<Token> tokens){
        handler = new TokenHandler(tokens);
    }


    //Calls expression and returns node of type, "ProgramNode"
    public Node parse(){
        return null;
    }


    //Handles new line/empty line tokens and returns boolean
    public boolean handleSeparators(){

        boolean foundSeperators = false;

        //Loop while the current isn't null and is an END-OF-LINE token
        while(handler.peek(0).isPresent() && handler.peek(0).get().getType() == Token.TokenType.ENDOFLINE ){
            handler.matchAndRemove(Token.TokenType.ENDOFLINE);
            foundSeperators = true;
        }
        return foundSeperators;

    }

    public Node expression(){

        return null;
    }

    public Node term(){
        return null;
    }

    /*
    Understand: Creates an appropriate Node either FloatNode, IntegerNode, or Node from calling Expression
    Match: If cases
    Plan:
     */
    public Node factor(){


        //Create optional token variables for the sign and value of the number
        Optional<Token> minusOptional = handler.matchAndRemove(Token.TokenType.MINUS);
        Optional<Token> valueOptional = handler.matchAndRemove(Token.TokenType.NUMBER);

        //Create regular token variables for the sign and the number
        Token signToken;
        Token valueToken;


        if(minusOptional.isEmpty() && valueOptional.isEmpty()) return null;

        //

        //Check for negative


        //Check for either integer node or float node and return either


        //Return Expression
        return null;
    }

}
