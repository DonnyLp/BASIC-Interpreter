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

        ProgramNode programList = new ProgramNode();

        //Loop while there's still more tokens
        while(handler.moreTokens()){

            //Look for statements and add to list
            programList.add(statements());

        }
        return programList;
    }


    //Parse all existing statements and return StatementsNode
    public StatementsNode statements(){

        StatementNode statement;
        StatementsNode statementsList = new StatementsNode();
        handleSeparators(); // Handle empty lines at the start of the file

        while((statement = parseStatement())!= null){
            statementsList.add(statement);
            handleSeparators();
        }
        return statementsList;
    }

    //Handle a singular statement, for now Print Node and Assignment Node
    public StatementNode parseStatement(){

        //Create a statement node to hold the current statement
        StatementNode statement = null;

        //Check if next token exists, find corresponding statement type, process the tokens for the statement
        //Return the corresponding statement
        if(handler.peek(0).isPresent()){
            switch(handler.peek(0).get().getType()){
                case WORD -> statement = parseAssignment();
                case PRINT -> statement = parsePrintStatement();
                case STRINGLITERAL -> statement = parseStringLiteral();
                case READ -> statement = parseReadStatement();
                case DATA -> statement = parseDataStatement();
                case INPUT -> statement = parseInputStatement();
                //Handle rest of the statements
            }
            return statement;
        }
        return null;
    }

    public InputNode parseInputStatement(){

        Optional<Token> inputToken = handler.matchAndRemove(Token.TokenType.INPUT);
        InputNode inputList = new InputNode();

        if (inputToken.isEmpty()) return null;

        //Handle the required initial string literal
        Optional<Token> stringLiteral = handler.matchAndRemove(Token.TokenType.STRINGLITERAL);

        if(stringLiteral.isEmpty()) throw new IllegalStateException("Missing string literal");

        inputList.add(new StringNode(stringLiteral.get().getValue()));
        handler.matchAndRemove(Token.TokenType.COMMA);

        //Parse the remaining variable elements
        parseCommaList(inputList, handler);

        return inputList;
    }

    public DataNode parseDataStatement(){

        DataNode dataList = new DataNode();
        Optional<Token> dataToken = handler.matchAndRemove(Token.TokenType.DATA);

        if(dataToken.isEmpty()) return null;

        parseCommaList(dataList, handler);

        return dataList;
    }

    public ReadNode parseReadStatement(){

        ReadNode readList = new ReadNode();
        Optional<Token> readToken = handler.matchAndRemove(Token.TokenType.READ);

        if(readToken.isEmpty()) return null;

        parseCommaList(readList, handler);

        return readList;

    }

    public PrintNode parsePrintStatement(){

        PrintNode printList = new PrintNode();
        Optional<Token> printToken = handler.matchAndRemove(Token.TokenType.PRINT);

        if(printToken.isEmpty()) return null;

        parseCommaList(printList, handler);

        return printList;
    }

    public StringNode parseStringLiteral(){
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

    public AssignmentNode parseAssignment(){

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

    public void parseCommaList(Addable node, TokenHandler handler){

        Optional<Token> token;

        do{
            //Update the token to the next token in the list
            if(handler.peek(0).isPresent()){
                token = Optional.of(handler.peek(0).get());
            }
            else{
                token = Optional.empty();
            }


            if(token.isPresent()){
                //PrintNode and DataNode are parsed similarly i.e. eiter a string literal or an expression
                if (node instanceof PrintNode || node instanceof DataNode){
                    if (token.get().getType() == Token.TokenType.STRINGLITERAL){
                        node.add(new StringNode(token.get().getValue()));
                        handler.matchAndRemove(Token.TokenType.STRINGLITERAL);
                    }
                    else{
                        node.add(expression());
                    }
                }
                //ReadNode and InputNode are parsed similarly i.e. a variable
                //In the case of InputNode, the first item in the list must be a string literal
                else if (node instanceof ReadNode || node instanceof InputNode){
                    node.add(new VariableNode(token.get().getValue()));
                    handler.matchAndRemove(Token.TokenType.WORD);
                }
            }
            else{
                throw new IllegalStateException("Token not present");
            }

        }while(handler.matchAndRemove(Token.TokenType.COMMA).isPresent());

    }

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