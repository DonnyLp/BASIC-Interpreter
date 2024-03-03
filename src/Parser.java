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

        //Loop while there's still more tokens
        while(handler.moreTokens()){

            handleSeparators();

            //Look for statements and add to list
            nodes.add(statements());

            handleSeparators();
        }
        return new ProgramNode(nodes);
    }


    //Parse all existing statements and return StatementsNode
    public StatementsNode statements(){
        //Call statements while statements doesn't return null
        StatementNode statement;
        List<StatementNode> statementsList = new ArrayList<>();
        while((statement = statement())!= null){   
            statementsList.add(statement);
        }
        return new StatementsNode(statementsList);

    }

    //Handle a singular statement, for now Print Node and Assignment Node
    public StatementNode statement(){

        //Create a statement node to hold the current statement
        StatementNode statement = null;

        //Check if next token exists, find corresponding statement type, process the tokens for the statement
        //Return the corresponding statement
        if(handler.peek(0).isPresent()){
            switch(handler.peek(0).get().getType()){

                case WORD -> statement = assignment();
                case PRINT -> statement = printStatement();
                //Handle the rest of the statements

            }
            return statement;
        }

        return null;
    }

    //Handle assignments and returns an AssignmentNode
    public AssignmentNode assignment(){

        //Define Node for variable name and value
        VariableNode name;
        Node value;

        //Create optionals for the variable name and equals sign for error checking
        Optional<Token> variableName = handler.matchAndRemove(Token.TokenType.WORD);
        Optional<Token> equalSign = handler.matchAndRemove(Token.TokenType.EQUALS);

        //Check if the variable name and equals sign for assignment exists
        if(variableName.isPresent() && equalSign.isPresent()){
             name = new VariableNode(variableName.get().getValue());
             value = expression();
             return new AssignmentNode(name,value);
        }

        return null;
    }

    //Handle print statements and return a PrintNode
    public PrintNode printStatement(){
        //Create an Optional Token for PRINT
        Optional<Token> printToken = handler.matchAndRemove(Token.TokenType.PRINT);

        //If the optional token is empty then return null
        if(printToken.isEmpty()) return null;

        //Call printlist method to obtain comma seperated list
        List<Node> printList = printList();

        return new PrintNode(printList);
    }

    //Handle the comma seperated list of items to print
    private List<Node> printList(){
        //Create a list of nodes to store the print values
        List<Node> printList = new ArrayList<>();

        //Add the first expression to the print list
        printList.add(expression());

        //Loop while the current token is a comma
        while(handler.peek(0).isPresent() && handler.peek(0).get().getType() == Token.TokenType.COMMA){
            handler.matchAndRemove(Token.TokenType.COMMA);
            printList.add(expression());
        }
        
        return printList;
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

    private Node expression(){
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

    private Node term(){
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
    private Node factor(){

        boolean isNegative = false;

        //Create optional token variables for the sign, number and variable
        Optional<Token> signOptional = handler.matchAndRemove(Token.TokenType.MINUS);
        Optional<Token> numberOptional = handler.matchAndRemove(Token.TokenType.NUMBER);
        Optional <Token> variableOptional = handler.matchAndRemove(Token.TokenType.WORD);

        //Handle embedded expression case
        if (numberOptional.isEmpty() && signOptional.isEmpty() && variableOptional.isEmpty()){

            if (handler.matchAndRemove(Token.TokenType.LPAREN).isPresent()){

                Node node = expression();

                if(handler.matchAndRemove(Token.TokenType.RPAREN).isEmpty()){
                    throw new MissingFormatArgumentException("Missing closing parenthesis");
                }

                return node;
            }
            return null;
        }
        //Handle variable case
        if(variableOptional.isPresent()) {
            return new VariableNode(variableOptional.get().getValue());
        }
        //Handle different number cases i.e. Float or Integer
        if (signOptional.isPresent()) isNegative = true;

        if(numberOptional.isEmpty()) return null;

        Token numberToken = numberOptional.get();

        //Check for float or integer and return the appropriate node
        if(numberToken.getValue().contains(".")){

            float floatNumber = Float.parseFloat(numberToken.getValue());

            if(isNegative) floatNumber = -floatNumber;

            return new FloatNode(floatNumber);

        }
        else{
            int integerNumber = Integer.parseInt(numberToken.getValue());

            if(isNegative) integerNumber = -integerNumber;

            return  new IntegerNode(integerNumber);
        } 

    }

    //Helper Method for term operator case
    private boolean isMultiplyOrDivide(Optional<Token> operator){
        if(operator.isPresent() && operator.get().getValue() != null){
            String operatorValue = operator.get().getValue();
            return operatorValue.equals("*") || operatorValue.equals("/");
        }
        return false;
    }

    //Helper Method for expression operator case
    private boolean isAddOrSubtract(Optional<Token> operator){
        if(operator.isPresent() && operator.get().getValue() != null){
            String operatorValue = operator.get().getValue();
            return operatorValue.equals("+") || operatorValue.equals("-");
        }
        return false;
    }
}