import java.util.*;


public class Parser {

    //Member variables
    private final TokenHandler handler;


    //Methods
    public Parser(LinkedList<Token> tokens){
        handler = new TokenHandler(tokens);
    }


    //Calls expression and returns node of type, "ProgramNode"
    public Node parse(){
        //Create list of tokens to pass into ProgramNode
        List<Node> nodes = new ArrayList<>();

        //Loop while there's still more tokens and look for expressions
        while(handler.moreTokens()){

            handleSeparators();

            //Look for expressions and add to list

            nodes.add(expression());

            handleSeparators();
        }
        return new ProgramNode(nodes);
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
        //Set the left node to the result of factor
        Node left = term();
        //Loop while the next token is either a, "+" or "-",
        //Create MathOpNode with right node being the result of term
        while (handler.peek(0).isPresent() && isAddOrSubtract(handler.peek(0))){
            Token operator = handler.peek(0).get();
            handler.matchAndRemove(operator.getType());
            left = new MathOpNode(left, operator.getType(), term());
        }
        return left;
    }

    public Node term(){
        //Set the left node to the result of factor
        Node left = factor();
        //Loop while the next token is either a, "*" or "/"
        //Create MathOpNode and set the right node to the result of factor
        while (handler.peek(0).isPresent() && isMultiplyOrDivide(handler.peek(0))){
            Token operator = handler.peek(0).get();
            handler.matchAndRemove(operator.getType());
            left = new MathOpNode(left, operator.getType(), factor());
        }

        return left;
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

    //Helper Method for term operator case
    public boolean isMultiplyOrDivide(Optional<Token> operator){
        if(operator.isPresent() && operator.get().getValue() != null){
            String operatorValue = operator.get().getValue();
            return operatorValue.equals("*") || operatorValue.equals("/");
        }
        return false;
    }

    //Helper Method for expression operator case
    public boolean isAddOrSubtract(Optional<Token> operator){
        if(operator.isPresent() && operator.get().getValue() != null){
            String operatorValue = operator.get().getValue();
            return operatorValue.equals("+") || operatorValue.equals("-");
        }
        return false;
    }
}