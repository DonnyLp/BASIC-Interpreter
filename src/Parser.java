import javax.lang.model.element.VariableElement;
import java.util.*;
import java.util.function.Function;


public class Parser {

    //Member variables
    private final TokenHandler handler;
    private final Set<Token.TokenType> functionSet;
    private boolean isWhileLoopMode;

    //Methods
    public Parser(LinkedList<Token> tokens){
        handler = new TokenHandler(tokens);
        this.functionSet = new HashSet<>();
        this.isWhileLoopMode = false;
        initializeFunctionSet();
    }

    //Parse all existing statements and return StatementsNode
    public StatementsNode parse(){

        StatementNode statement;
        StatementsNode statementsList = new StatementsNode();
        handleSeparators(); // Handle empty lines at the start of the file

        while((statement = parseStatement())!= null){
            statementsList.add(statement);
            handleSeparators();
        }
        return statementsList;
    }

    public StatementNode parseStatement(){

        StatementNode statement = null;
        Optional<Token> nextToken = handler.peek(0);

        if(nextToken.isPresent()){
            switch(nextToken.get().getType()){
                case WORD -> statement = parseAssignment();
                case PRINT -> statement = parsePrintStatement();
                case STRINGLITERAL -> statement = parseStringLiteral();
                case READ -> statement = parseReadStatement();
                case DATA -> statement = parseDataStatement();
                case INPUT -> statement = parseInputStatement();
                case FOR -> statement = parseForStatement();
                case WHILE -> statement = parseWhileStatement();
                case IF -> statement = parseIfStatement();
                case GOSUB -> statement = parseGoSubStatement();
                case RETURN -> statement = parseReturnStatement();
                case RANDOM,LEFT$,MID$,RIGHT$,VAL_FLOAT,VAL_INT,NUM$ -> statement = parseFunctionStatement();
                case LABEL -> statement = parseLabelStatement();
                case END -> statement = parseEndStatement();
                case NEXT -> statement = parseNextStatement();
                case ENDOFLINE -> handleSeparators();
                default -> throw new ParserException("Encountered an invalid token type: " + nextToken.get().getType());
            }
                return statement;
        }
        return null;
    }

    public LabeledStatementNode parseLabelStatement(){

        Token labelToken = extractToken(Token.TokenType.LABEL, "Expected LABEL token");
        LabeledStatementNode labeledStatement = new LabeledStatementNode(labelToken.getValue(),isWhileLoopMode);

        //handle while loop empty label
        if(isWhileLoopMode){
            isWhileLoopMode = false;
            return labeledStatement;
        }

        return labeledStatement;
    }

    public FunctionNode parseFunctionStatement(){

        Optional<Token> currentToken = handler.peek(0);
        FunctionNode functionNode = new FunctionNode();

        if(currentToken.isPresent()){
            String functionName = currentToken.get().getType().name();

            //Convert the function names for "VAL" to the appropriate BASIC function name
            if(functionName.equals("VAL_FLOAT")){
                functionName = "VAL%";
            }
            else if(functionName.equals("VAL_INT")){
                functionName = "VAL";
            }
            functionNode = parseFunctionParameters(functionName);
        }
        return functionNode;
    }

    private FunctionNode parseFunctionParameters(String functionName){

        Node parameter;
        FunctionNode functionNode = new FunctionNode(functionName);

        if(handler.peek(0).isPresent()){
            verifyTokenPresence(handler.peek(0).get().getType(),"Expected function name" + functionName);
        }


        //RANDOM is the only built-in function that doesn't require a comma seperated list
        if(functionName.equals("RANDOM")){
            verifyTokenPresence(Token.TokenType.LPAREN,"Expected left parentheses");
            verifyTokenPresence(Token.TokenType.RPAREN,"Expected right parentheses");
        }
        else {
            verifyTokenPresence(Token.TokenType.LPAREN,"Expected left parentheses");
            do {
                parameter = expression();
                if (parameter == null) {
                    parameter = parseStringLiteral();
                }

                functionNode.add(parameter);

            } while (handler.matchAndRemove(Token.TokenType.COMMA).isPresent());

            verifyTokenPresence(Token.TokenType.RPAREN, "Expected closing parentheses for the function call");
        }

        return functionNode;
    }

    private BooleanExpressionNode parseBooleanExpression(){

        Node leftOperand = null;
        Token operator = new Token();
        Node rightOperand = null;

        //Node structure: leftOperand operator rightOperand
        try{
            leftOperand = expression();
        }catch (Exception e){
            throw new ParserException(e.getMessage());
        }

        if (handler.peek(0).isPresent()){
            operator = extractToken(handler.peek(0).get().getType(), "Operator expected after left operand");
        }

        try{
            rightOperand = expression();
        }catch (Exception e){
            throw new ParserException(e.getMessage());
        }

        return new BooleanExpressionNode(leftOperand,operator.getType(),rightOperand);
    }

    public WhileNode parseWhileStatement(){

        BooleanExpressionNode condition = new BooleanExpressionNode();
        Token label = new Token();
        WhileNode whileNode = new WhileNode();

        /*
         WhileNode is of the form:
         WHILE condtion Label
         body
         label
         */

        verifyTokenPresence(Token.TokenType.WHILE,"Expected WHILE token");
        condition = parseBooleanExpression();
        label = extractToken(Token.TokenType.WORD, "Expected label name after condition");
        whileNode = new WhileNode(condition, label.getValue());

        //set the mode of the label method to handle the while variation
        isWhileLoopMode = true;

        return whileNode;
    }
   
    public IfNode parseIfStatement(){

        IfNode ifNode = new IfNode();
        BooleanExpressionNode condition = new BooleanExpressionNode();
        Token label = new Token();

        //IFNode is of the form -> "IF expression THEN LABEL"

        verifyTokenPresence(Token.TokenType.IF,"Expected IF token");
        condition = parseBooleanExpression();
        verifyTokenPresence(Token.TokenType.THEN,"Expected THEN token after the condition");
        label = extractToken(Token.TokenType.WORD, "Expected name of label to direct to after THEN token");
        ifNode = new IfNode(condition,label.getValue());

        return ifNode;
    }

    public EndNode parseEndStatement(){

        //EndNode is of the form -> "END"
        Optional<Token> endToken =  handler.matchAndRemove(Token.TokenType.END);
        endToken.orElseThrow();
        return new EndNode();

    }

    public ForNode parseForStatement(){


        ForNode forNode = new ForNode();
        Node startIndex;
        Node endIndex;
        int incrementValue = 1;

        /*forNode is of the form:
          FOR VARIABLE = START_INDEX TO END_INDEX STEP 2 (Increment value deceleration is optional).
          body
          NEXT VARIABLE
         */
        verifyTokenPresence(
                Token.TokenType.FOR,
                "FOR keyword expected at the start of the loop definition"
        );

        Token loopVariable = extractToken(
                Token.TokenType.WORD,
                "Loop variable expected after the FOR token."
        );


        verifyTokenPresence(Token.TokenType.EQUALS, "Equal sign expected after initial loop variable declaration");


        startIndex = expression();

        verifyTokenPresence(Token.TokenType.TO, "Expected the \"TO\" token after the start index.");

        endIndex = expression();

        //Handle optional incremental value declaration
        Optional<Token> stepToken = handler.matchAndRemove(Token.TokenType.STEP);
        if(stepToken.isPresent()){
            Token incrementValueToken = extractToken(
                    Token.TokenType.NUMBER,
                    "Increment value expected after STEP token"
            );
            incrementValue = Integer.parseInt(incrementValueToken.getValue());
            forNode = new ForNode(new VariableNode(loopVariable.getValue()), startIndex,endIndex,incrementValue,true);
        }
        else{
            forNode = new ForNode(new VariableNode(loopVariable.getValue()),startIndex,endIndex,incrementValue,false);
        }

        return forNode;
    }

    public NextNode parseNextStatement(){

        NextNode next = new NextNode();
        VariableNode nextVariable = new VariableNode();

        verifyTokenPresence(Token.TokenType.NEXT,"Expected a NEXT token");
        Token variable = extractToken(Token.TokenType.WORD,"Excepted a variable after the NEXT token");

        nextVariable = new VariableNode(variable.getValue());
        next = new NextNode(nextVariable);

        return next;
    }

    public ReturnNode parseReturnStatement(){

        //ReturnNode is of the form -> "RETURN"
        verifyTokenPresence(Token.TokenType.RETURN,"Expected a READ token");
        return new ReturnNode();

    }

    public GoSubNode parseGoSubStatement(){

        //GoSubNode is of the form -> "GOSUB LABEL"
        handler.matchAndRemove(Token.TokenType.GOSUB);
        Optional<Token> labelToken = handler.matchAndRemove(Token.TokenType.WORD);
        labelToken.orElseThrow(() -> new NoSuchElementException("Missing label name for GOSUB call"));
        return new GoSubNode(labelToken.get().getValue());
    }

    public InputNode parseInputStatement(){

        //InputNode is of the form -> "INPUT STRINGLITERAL, VARIABLE, VARIABLE,....."
        Optional<Token> inputToken = handler.matchAndRemove(Token.TokenType.INPUT);
        InputNode inputList = new InputNode();

        if (inputToken.isEmpty()) throw new IllegalStateException("Missing identifier: \"INPUT\"");

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

        if(dataToken.isEmpty()) throw new IllegalStateException("Missing identifier: \"DATA\"");

        parseCommaList(dataList, handler);

        return dataList;
    }

    public ReadNode parseReadStatement(){

        ReadNode readList = new ReadNode();
        Optional<Token> readToken = handler.matchAndRemove(Token.TokenType.READ);

        if(readToken.isEmpty()) throw new IllegalStateException("Missing identifier: \"READ\"");

        parseCommaList(readList, handler);

        return readList;

    }

    public PrintNode parsePrintStatement(){

        PrintNode printList = new PrintNode();
        Optional<Token> printToken = handler.matchAndRemove(Token.TokenType.PRINT);

        if(printToken.isEmpty()) throw new IllegalStateException("Missing identifier: \"PRINT\"");

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

    //Helps parse the statement nodes that consist of a comma seperated list (READ, PRINT,etc.)
    private void parseCommaList(Addable node, TokenHandler handler){

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

    public Node expression(){
        //Set the left node to the result of factor
        Node left = term();
        //Loop while the next token is either a, "+" or "-",
        //Create MathOpNode with right node being the result of term
        while (handler.peek(0).isPresent() && isAddOrSubtract(handler.peek(0).get())){
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
        while (handler.peek(0).isPresent() && isMultiplyOrDivide(handler.peek(0).get())){
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
        Optional <Token> variableOptional = Optional.empty();

        //Ensure that a variable is consumed only when there isn't a number 
        if(numberOptional.isEmpty() && handler.peek(0).isPresent()){
            if(functionSet.contains(handler.peek(0).get().getType())){
               return parseFunctionStatement();
            }else{
                variableOptional = handler.matchAndRemove(Token.TokenType.WORD);
            }
        }

        
        //Handle embedded expression case
        if (numberOptional.isEmpty() && signOptional.isEmpty() && variableOptional.isEmpty()){

            if (handler.matchAndRemove(Token.TokenType.LPAREN).isPresent()){

                Node node = expression();

                if(handler.matchAndRemove(Token.TokenType.RPAREN).isEmpty()){
                    throw new ParserException("Missing closing parenthesis");
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

        if(numberOptional.isEmpty()) throw new ParserException("Number expected");

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

    private void initializeFunctionSet(){
        this.functionSet.add(Token.TokenType.RANDOM);
        this.functionSet.add(Token.TokenType.LEFT$);
        this.functionSet.add(Token.TokenType.RIGHT$);
        this.functionSet.add(Token.TokenType.MID$);
        this.functionSet.add(Token.TokenType.NUM$);
        this.functionSet.add(Token.TokenType.VAL_INT);
        this.functionSet.add(Token.TokenType.VAL_FLOAT);
    }

    private Token extractToken(Token.TokenType type, String errorMessage){
        return handler.matchAndRemove(type).orElseThrow(() -> new ParserException(errorMessage));
    }
    private void verifyTokenPresence(Token.TokenType type, String errorMessage){
        if(handler.matchAndRemove(type).isEmpty()){
            throw new ParserException(errorMessage);
        }
    }
    //Helper Method for term operator case
    private boolean isMultiplyOrDivide(Token operator){
        if(operator != null && operator.getValue() != null){
            String operatorValue = operator.getValue();
            return operatorValue.equals("*") || operatorValue.equals("/");
        }
        return false;
    }

    //Helper Method for expression operator case
    private boolean isAddOrSubtract(Token operator){
        if(operator != null && operator.getValue() != null){
            String operatorValue = operator.getValue();
            return operatorValue.equals("+") || operatorValue.equals("-");
        }
        return false;
    }
}