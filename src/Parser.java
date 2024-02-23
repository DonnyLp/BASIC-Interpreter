import java.util.LinkedList;
import java.util.MissingFormatArgumentException;
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

    //Handles the factor of the expression
    public Node factor(){

        boolean isNegative = false;

        //Create optional token variables for the sign and value of the number
        Optional<Token> signOptional = handler.matchAndRemove(Token.TokenType.MINUS);
        Optional<Token> valueOptional = handler.matchAndRemove(Token.TokenType.NUMBER);

        //Create regular token variables for the sign and the number
        Token valueToken;

        //Handle embedded expression case
        if (valueOptional.isEmpty() && signOptional.isEmpty()){

            if (handler.matchAndRemove(Token.TokenType.LPAREN).isPresent()){

                Node node = expression();

                if(handler.matchAndRemove(Token.TokenType.RPAREN).isEmpty()){
                    throw new MissingFormatArgumentException("Missing closing parenthesis");
                }

                return node;
            }
            return null;
        }

        //Handle different number cases i.e. Float or Integer
        if (signOptional.isPresent()) isNegative = true;

        if(valueOptional.isEmpty()) return null;

        valueToken = valueOptional.get();

        if(valueToken.getValue().contains(".")){

            float floatNumber = Float.parseFloat(valueToken.getValue());

            if(isNegative) floatNumber = -floatNumber;

            return new FloatNode(floatNumber);

        }
        else{
            int integerNumber = Integer.parseInt(valueToken.getValue());

            if(isNegative) integerNumber = -integerNumber;

            return  new IntegerNode(integerNumber);
        }
    }

}
