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

            //Look for statements and add to list
            nodes.add(statements());

        }
        return new ProgramNode(nodes);
    }


    //Parse all existing statements and return StatementsNode
    public StatementsNode statements(){
        //Call statements while statements doesn't return null
        StatementNode statement;

        List<StatementNode> statementsList = new ArrayList<>();
        while((statement = statement())!= null && handler.moreTokens()){
            statementsList.add(statement);
            handleSeparators();
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
                case STRINGLITERAL -> statement = stringLiteral();
                case READ -> statement = readStatement();
                case DATA -> statement = dataStatement();
                case INPUT -> statement = inputStatement();
                //Handle rest of the statements

            }
            return statement;
        }
        return null;
    }

    //Handle input statement
    public InputNode inputStatement(){
        Optional<Token> inputToken = handler.matchAndRemove(Token.TokenType.INPUT);

        if (inputToken.isEmpty()) return null;

        return new InputNode(parseInputList());
    }
    //Helper for input statement
    private List<Node> parseInputList(){

        List<Node> inputList = new ArrayList<>();

        //Handle required stringliteral
        Optional<Token> stringLiteral = handler.matchAndRemove(Token.TokenType.STRINGLITERAL);

        if(stringLiteral.isEmpty()) return null;

        inputList.add(new StringNode(stringLiteral.get().getValue()));

       // Append the variables and handle the commas
        while(handler.peek(0).isPresent() && handler.peek(0).get().getType() == Token.TokenType.COMMA){
            Optional<Token> commaOptional = handler.matchAndRemove(Token.TokenType.COMMA);
            if (commaOptional.isEmpty()) return null;

            Optional<Token> variable = handler.peek(0);

            if(variable.isEmpty()) return null;

            inputList.add(new VariableNode(variable.get().getValue()));

        }
        return inputList;
    }

    //Handle data statements
    public DataNode dataStatement(){
        Optional<Token> dataToken = handler.matchAndRemove(Token.TokenType.DATA);

        if(dataToken.isEmpty()) return null;

        return new DataNode(parseDataList());
    }

    //Helper for data statements
    private List<Node> parseDataList(){

        List<Node> dataList = new ArrayList<>();

        //Handle first expression or number
        Optional<Token> stringLiteral = handler.matchAndRemove(Token.TokenType.STRINGLITERAL);

        if(stringLiteral.isPresent()) {
            dataList.add(new StringNode(stringLiteral.get().getValue()));
        }
        else{
            dataList.add(expression());
        }

        while(handler.peek(0).isPresent() && handler.peek(0).get().getType() == Token.TokenType.COMMA){
            Optional<Token> commaOptional = handler.matchAndRemove(Token.TokenType.COMMA);
            if (commaOptional.isEmpty()) return null;

            stringLiteral = handler.matchAndRemove(Token.TokenType.STRINGLITERAL);

            if(stringLiteral.isPresent()) {
                dataList.add(new StringNode(stringLiteral.get().getValue()));
            }
            else{
                dataList.add(expression());
            }

        }
        return dataList;
    }
    //Handle read statements
    public ReadNode readStatement(){

        Optional<Token> readToken = handler.matchAndRemove(Token.TokenType.READ);

        if(readToken.isEmpty()) return null;

        return new ReadNode(parseReadList());

    }
    //Helper for processing comma seperated read statements
    private List<VariableNode> parseReadList(){

        List<VariableNode> readList = new ArrayList<>();

        //Add the first variable Node to the list
        Optional<Token> variable = handler.matchAndRemove(Token.TokenType.WORD);

        if(variable.isEmpty()) return null;

        readList.add(new VariableNode(variable.get().getValue()));

        //Append the following variables in the comma seperated list
        while(handler.peek(0).isPresent() && handler.peek(0).get().getType() == Token.TokenType.COMMA){
            //Remove the comma from the token list and handle exceptions
            Optional<Token> commaOptional = handler.matchAndRemove(Token.TokenType.COMMA);
            if (commaOptional.isEmpty()) return null;

            //Append the next variable node to the list
            variable = handler.matchAndRemove(Token.TokenType.WORD);

            if(variable.isEmpty()) return null;

            readList.add(new VariableNode(variable.get().getValue()));

        }
        return readList;
    }

    //Handle string literals
    public StringNode stringLiteral(){
        //Define node for value of string literal
        String stringValue;

        //Create optional for the stringLiteral node
        Optional<Token> stringOptional = handler.matchAndRemove(Token.TokenType.STRINGLITERAL);

        if (stringOptional.isPresent()){
            stringValue = stringOptional.get().getValue();
            return new StringNode(stringValue);
        }
        return null;
    }
    //Handle assignments and returns an AssignmentNode
    public AssignmentNode assignment(){

        //Define Nodes for the variable name and value
        VariableNode name;
        Node value;

        //Create optionals for the variable name and equals sign for error checking
        Optional<Token> variableName = handler.matchAndRemove(Token.TokenType.WORD);
        Optional<Token> equalSign = handler.matchAndRemove(Token.TokenType.EQUALS);

        //Verify that the variable name and equals sign for assignment exists
        if(variableName.isPresent() && equalSign.isPresent()){
             name = new VariableNode(variableName.get().getValue());
             value = expression();
             return new AssignmentNode(name,value);
        }

        return null;
    }

    //Handle print statement
    public PrintNode printStatement(){

        Optional<Token> printToken = handler.matchAndRemove(Token.TokenType.PRINT);

        if(printToken.isEmpty()) return null;

        return new PrintNode(printList());
    }

    //Handle the comma seperated list of items to print
    private List<Node> printList(){
        //Create a list of nodes to store the print values
        List<Node> printList = new ArrayList<>();

        //Add the first expression or string literal to list
        if(handler.peek(0).isPresent() && handler.peek(0).get().getType() == Token.TokenType.STRINGLITERAL){
            printList.add(stringLiteral());
        }
        else{
            printList.add(expression());
        }

        //Append the following elements in the print statement and handle the exisiting commas
        while(handler.peek(0).isPresent() && handler.peek(0).get().getType() == Token.TokenType.COMMA){
            //Remove the comma from the token list and handle exceptions
            Optional<Token> commaOptional = handler.matchAndRemove(Token.TokenType.COMMA);
            if (commaOptional.isEmpty()) return null;

            //Append the correct type of element to the output list
            if(handler.peek(0).isPresent() && handler.peek(0).get().getType() == Token.TokenType.STRINGLITERAL){
                printList.add(stringLiteral());
            }
            else{
                printList.add(expression());
            }
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