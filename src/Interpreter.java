import java.util.*;

public class Interpreter {


    private LinkedList<Node> dataStatementContents;
    private ArrayList testPrintElements;
    private ArrayList testInputElements;
    private HashMap<String, LabeledStatementNode> labeledStatements;
    private HashMap<String, String> stringVariables;
    private HashMap<String, Integer> integerVariables;
    private HashMap<String, Float> floatVariables;
    Stack<StatementNode> statementStack;
    private StatementsNode astTree;
    private boolean inTestMode;
    private boolean isWhileMode;
    StatementNode nextStatement;

    public Interpreter(StatementsNode tree, boolean inTestMode) {
        this.astTree = tree;
        this.astTree.setNextForAll();
        this.inTestMode = inTestMode;
        this.isWhileMode = false;
        initializeDataStructures();
    }

    public Interpreter() {
        this.astTree = null;
        this.inTestMode = false;
        this.isWhileMode = false;
        initializeDataStructures();
    }


    /*
    Interprets the entire ast tree from the passed in head reference.
    Optimizes the interpretation process by setting saving all DATA statement contents and labeled statements in maps
    If a statement that isn't supported is passed in then it will throw a InterpreterException with a custom message
    @param StatementNode statement - the head of the ast tree
    @return void
     */
    public void interpret(StatementNode currentStatement) {

        //optimize interpretation processing
        optimizeRead(currentStatement);
        optimizeLabels(currentStatement);

        boolean continueLoop = true;

        while (continueLoop) {

            //set the next to the current statement's next
            this.nextStatement = currentStatement.getNext();

            //determine the current statement type and interpret it
            switch (currentStatement) {
                case ReadNode readNode -> {
                    for (Node node : readNode.getList()) {
                        VariableNode variable = (VariableNode) node;
                        String variableName = variable.getName();
                        handleReadAssignment(variableName, dataStatementContents.pop());
                    }
                }

                case AssignmentNode assignment -> {

                    String variableName = assignment.getVariableName().getName();
                    Node value = assignment.getAssignmentValue();

                    //check variables data type
                    if (variableName.contains("%")) {
                        Float tempVar = evaluateFloat(assignment.getAssignmentValue());
                        floatVariables.put(variableName, tempVar);
                    } else if (variableName.contains("$")) {
                        String tempVar = evaluateString(assignment.getAssignmentValue());
                        stringVariables.put(variableName, tempVar);
                    } else {
                        Integer tempVar = evaluateInt(assignment.getAssignmentValue());
                        integerVariables.put(variableName, tempVar);
                    }
                }
                case InputNode inputNode -> interpretInputNode(inputNode);
                case PrintNode printNode -> {

                    LinkedList<Node> printList = printNode.getList();
                    String output = "";
                    for (Node node : printList) {
                       output += interpretPrint(node);
                    }
                    System.out.println(output);
                }
                case DataNode dataNode -> {
                    break;
                }
                case IfNode ifNode -> interpretIfNode(ifNode, currentStatement);
                case GoSubNode goSubNode -> interpretGoSubNode(goSubNode);
                case ReturnNode returnNode -> {
                    this.nextStatement = statementStack.pop();
                }
                case NextNode nextNode -> this.nextStatement = statementStack.pop();
                case EndNode endNode -> continueLoop = false;
                case ForNode forNode -> interpretForNode(forNode, currentStatement);
                case LabeledStatementNode labeledStatement -> {
                    //update if the labeled statement is a WHILE label
                    labeledStatement.setWhileLabelMode(this.isWhileMode);
                    if(labeledStatement.isWhileLabel()){
                        this.nextStatement = statementStack.pop();
                    }else{
                        this.nextStatement = labeledStatement.getNext();
                    }

                }
                case WhileNode whileNode-> interpretWhileNode(whileNode);
                default -> throw new InterpreterException("Statement interpretation not supported: " + currentStatement.toString());
            }
            currentStatement = this.nextStatement;
        }
    }

    /*
    Interprets the whileNode statement given the whileNode
    @param whileNode: current statement that is passed in from the interpret method that is of type WhileNode
    @return void
     */
    public void interpretWhileNode(WhileNode whileNode){

        BooleanExpressionNode condition = whileNode.getCondition();
        String label = whileNode.getLabel();

        boolean isConditionValid = false;

        isConditionValid = processBooleanExpression(condition);

        if(isConditionValid){
            this.isWhileMode = true;
            statementStack.push(whileNode);
        }else{
            this.isWhileMode = false;
            this.nextStatement = this.labeledStatements.get(label);
        }
    }

    /*
    Interprets the forNode statement given the forNode statement from the ast tree
    @param forNode - current statement that is passed in from the interpret method that is of type ForNode
    @param currentStatement - instance of the current statement used to iterate through the ast tree
    @return void
     */
    public void interpretForNode(ForNode forNode, StatementNode currentStatement) {

        String loopVariable;
        int currentIndex;
        int endIndex;
        Node startIndexNode = forNode.getStartIndex();
        Node endIndexNode = forNode.getEndIndex();
        int incrementalValue;

        loopVariable = forNode.getVariable().getName();
        incrementalValue = forNode.getIncrementValue();

        //setting the loopVariable in storage if non-existent prior to the FOR call
        if (!integerVariables.containsKey(loopVariable)) {
            currentIndex = evaluateInt(startIndexNode);
            integerVariables.put(loopVariable, currentIndex);
        }

        currentIndex = integerVariables.get(loopVariable);
        endIndex = evaluateInt(endIndexNode);

        //if the variable is past the limit then skip all the way to the next statement
        if (currentIndex >= endIndex) {
            while (!(currentStatement instanceof NextNode) && currentStatement != null) {
                currentStatement = currentStatement.getNext();
            }
            this.nextStatement = currentStatement.getNext();
        } else {
            statementStack.push(forNode);
            currentIndex = integerVariables.get(loopVariable) + incrementalValue;
            integerVariables.put(loopVariable, currentIndex);
        }
    }

    private void interpretGoSubNode(GoSubNode goSubNode) {
        String label = goSubNode.getLabel();
        StatementNode subRoutine = labeledStatements.get(label);
        statementStack.push(goSubNode.getNext());
        this.nextStatement = subRoutine;
    }

    //helper to interpret the ifNode's boolean expression and handle label
    public void interpretIfNode(IfNode ifNode, StatementNode currentStatement) {

        BooleanExpressionNode condition = ifNode.getCondition();
        String label = ifNode.getLabel();
        boolean isConditonValid = false;

        isConditonValid = processBooleanExpression(condition);

        if (isConditonValid) {
            if (labeledStatements.containsKey(label)) {
                this.nextStatement = labeledStatements.get(label);
            } else {
                throw new InterpreterException("The label: " + "\"" + label + "\"" + " does not exist.");
            }
        }
    }

    private void interpretInputNode(InputNode inputNode) {
        //EXAMPLE: INPUT “What is your name and age?”, name$, age
        LinkedList<Node> inputList = inputNode.getList();
        Scanner scan = new Scanner(System.in);
        StringNode initialString = (StringNode) inputList.pop();

        if (inTestMode) {
            testInputElements.add(0, initialString.toString());
        } else {
            System.out.println(initialString);
        }
        for (Node node : inputList) {
            VariableNode currentVariable = (VariableNode) node;
            String currentName = currentVariable.getName();
                        /*
                             *NOTE* for test mode collection manipulation:
                             only removing the item from the first index (after the initial string) ensures
                             that the 1st index will have a test input value for the corresponding variable
                        */
            //Node of type: STRING
            if (currentVariable.getName().contains("$")) {
                if (inTestMode) {
                    stringVariables.put(currentName, (String) testInputElements.remove(1));
                } else {
                    stringVariables.put(currentName, scan.next());
                }
            }
            //Node of type: FLOAT
            else if (currentName.contains("%")) {
                if (inTestMode) {
                    floatVariables.put(currentName, (Float) testInputElements.remove(1));
                } else {
                    floatVariables.put(currentName, scan.nextFloat());
                }
            }
            //Node of type: INTEGER
            else {
                if (inTestMode) {
                    integerVariables.put(currentName, Integer.parseInt((String) testInputElements.remove(1)));
                } else {
                    integerVariables.put(currentName, scan.nextInt());
                }
            }
        }
    }

    public boolean processBooleanExpression(BooleanExpressionNode condition){

        boolean isConditionValid = false;
        Node left = condition.getLeftOperand();
        Node right = condition.getRightOperand();

        switch (condition.getOperator()) {
            case EQUALS -> {
                isConditionValid = handleEquals(left, right);
            }
            case LESSTHAN -> {
                isConditionValid = handleLessThan(left, right);
            }
            case NOTEQUALS -> {
                isConditionValid = handleNotEquals(left, right);
            }
            case GREATERTHAN -> {
                isConditionValid = handleGreaterThan(left, right);
            }
            case LESSTHANOREQUAL -> {
                isConditionValid = handleLessOrEqual(left, right);
            }
            case GREATERTHANOREQUAL -> {
                isConditionValid = handleGreaterOrEqual(left, right);
            }
        }
        return isConditionValid;
    }

    private boolean handleEquals(Node left, Node right) {
        if (isStringType(left) || isStringType(right)) {
            return evaluateString(left).equals(evaluateString(right));
        } else if (isIntegerType(left) || isIntegerType(right)) {
            return evaluateInt(left) == evaluateInt(right);
        } else if (isFloatType(left) || isFloatType(right)) {
            return evaluateFloat(left) == evaluateFloat(right);
        } else {
            throw new InterpreterException("Operand comparison type mismatch");
        }
    }

    private boolean handleLessThan(Node left, Node right) {
        boolean isTrue = false;
        if (isIntegerType(left) || isIntegerType(right)) {
            return evaluateInt(left) < evaluateInt(right);
        } else if (isFloatType(left) || isFloatType(right)) {
            return evaluateFloat(left) < evaluateFloat(right);
        } else {
            throw new InterpreterException("Operand comparison type mismatch");
        }
    }

    private boolean handleGreaterThan(Node left, Node right) {
        if (isIntegerType(left) || isIntegerType(right)) {
            return evaluateInt(left) > evaluateInt(right);
        } else if (isFloatType(left) || isFloatType(right)) {
            return evaluateFloat(left) > evaluateFloat(right);
        } else {
            throw new InterpreterException("Operand comparison type mismatch");
        }
    }

    private boolean handleNotEquals(Node left, Node right) {
        if (isStringType(left) || isStringType(right)) {
            return !(evaluateString(left).equals(evaluateString(right)));
        } else if (isIntegerType(left) || isIntegerType(right)) {
            return evaluateInt(left) != evaluateInt(right);
        } else if (isFloatType(left) || isFloatType(right)) {
            return evaluateFloat(left) != evaluateFloat(right);
        } else {
            throw new InterpreterException("Operand comparison type mismatch");
        }
    }

    private boolean handleLessOrEqual(Node left, Node right) {
        if (isIntegerType(left) || isIntegerType(right)) {
            return evaluateInt(left) <= evaluateInt(right);
        } else if (isFloatType(left) || isFloatType(right)) {
            return evaluateFloat(left) <= evaluateFloat(right);
        } else {
            throw new InterpreterException("Operand comparison type mismatch");
        }
    }

    private boolean handleGreaterOrEqual(Node left, Node right) {
        if (isIntegerType(left) || isIntegerType(right)) {
            return evaluateInt(left) >= evaluateInt(right);
        } else if (isFloatType(left) || isFloatType(right)) {
            return evaluateFloat(left) >= evaluateFloat(right);
        } else {
            throw new InterpreterException("Operand comparison type mismatch");
        }
    }

    //helper to interpret the print statement's list
    private String interpretPrint(Node node) {
        String output = "";
        switch (node) {
            case StringNode string -> {
                if (inTestMode) {
                    testPrintElements.add(string.toString());
                } else {
                    output += string.toString();
                }
            }
            case VariableNode variable -> {
                if (inTestMode) {
                    testPrintElements.add(lookUpVariable(variable.getName()));
                } else {
                    output += lookUpVariable(variable.getName());
                }
            }
            case MathOpNode expression -> {
                Object evaluatedExpression;

                //the expression can either be between floats or integers
                if (isIntegerExpression(expression)) {
                    evaluatedExpression = evaluateInt(expression);
                } else {
                    evaluatedExpression = evaluateFloat(expression);
                }

                if (inTestMode) {
                    testPrintElements.add(evaluatedExpression.toString());
                } else {
                    output += evaluatedExpression.toString();
                }
            }
            default -> throw new InterpreterException("Unexpected value: " + node.toString());
        }
        return output;
    }

    //helper to identify the expression's type
    private boolean isIntegerExpression(MathOpNode expression) {
        return expression.getLeft() instanceof IntegerNode;
    }

    private void handleReadAssignment(String variableName, Node dataContent) {

        //handle each type of variable available in READ
        switch (checkVariableType(variableName)) {
            case "String" -> {
                if (!isStringType(dataContent)) {
                    throw new InterpreterException("READ variable and DATA content type mismatch");
                }
                StringNode value = (StringNode) dataContent;
                stringVariables.put(variableName, value.toString());
            }
            case "Integer" -> {
                if (!isIntegerType(dataContent)) {
                    throw new InterpreterException("READ variable and DATA content type mismatch");
                }
                IntegerNode value = (IntegerNode) dataContent;
                integerVariables.put(variableName, value.getIntegerValue());
            }
            case "Float" -> {
                if (!isFloatType(dataContent)) {
                    throw new InterpreterException("READ variable and DATA content type mismatch");
                }
                FloatNode value = (FloatNode) dataContent;
                floatVariables.put(variableName, value.getFloatValue());
            }
        }
    }

    private String checkVariableType(String name) {
        if (name.contains("$")) {
            return "String";
        } else if (name.contains("%")) {
            return "Float";
        } else {
            return "Integer";
        }
    }

    private boolean isIntegerType(Node node) {
        return node instanceof IntegerNode;
    }

    private boolean isFloatType(Node node) {
        return node instanceof FloatNode;
    }

    private boolean isStringType(Node node) {
        return node instanceof StringNode;
    }

    public String evaluateString(Node node) {

        String result = "";

        switch (node) {
            case StringNode string -> {
                result = string.toString();
            }
            case VariableNode variable -> {
                result = stringVariables.get(variable.getName());
            }
            case FunctionNode function -> {
                String functionName = function.getName();
                //handle all built-in functions that return a string value
                switch (functionName) {
                    case "LEFT$" -> {
                        //LEFT$(string, int)
                        String target = function.get(0).toString();
                        IntegerNode count = (IntegerNode) function.get(1);
                        result = left(target, count.getIntegerValue());
                    }
                    case "RIGHT$" -> {
                        //RIGHT$(string, int)
                        String target = function.get(0).toString();
                        IntegerNode count = (IntegerNode) function.get(1);
                        result = right(target, count.getIntegerValue());
                    }
                    case "NUM$" -> {
                        //NUM$(int or float)
                        if (function.peek() instanceof IntegerNode number) {
                            result = numInt(number.getIntegerValue());
                        } else if (function.peek() instanceof FloatNode number) {
                            result = numFloat(number.getFloatValue());
                        }
                    }
                    case "MID$" -> {
                        //MID$(string,int, int)
                        String target = function.get(0).toString();
                        IntegerNode startIndex = (IntegerNode) function.get(1);
                        IntegerNode endIndex = (IntegerNode) function.get(2);
                        result = mid(target, startIndex.getIntegerValue(), endIndex.getIntegerValue());

                    }
                    default -> throw new InterpreterException("Invalid function call for type string");
                }
            }
            default -> throw new InterpreterException("Expected a Variable or String Node");
        }
        return result;
    }

    public int evaluateInt(Node node) {

        Integer result = 0;

        switch (node) {
            case VariableNode variableNode -> {
                String variableName = variableNode.getName();

                if (integerVariables.containsKey(variableName)) {
                    result = integerVariables.get(variableName);
                } else {
                    throw new InterpreterException("Variable: " + "\"" + variableName + "\"" + " does not exist.");
                }
            }
            case IntegerNode number -> result = number.getIntegerValue();
            case FunctionNode function -> {
                if (function.getName().equals("RANDOM")) {
                    result = random();
                } else if (function.getName().equals("VAL")) {
                    //grabbing the string for conversion
                    String stringParameter = function.getList().pop().toString();
                    result = valInt(stringParameter);
                }
            }
            case MathOpNode expression -> {
                switch (expression.getOperation()) {
                    case PLUS -> result = evaluateInt(expression.getLeft()) + evaluateInt(expression.getRight());
                    case MULTIPLY -> result = evaluateInt(expression.getLeft()) * evaluateInt(expression.getRight());
                    case MINUS -> result = evaluateInt(expression.getLeft()) - evaluateInt(expression.getRight());
                    case DIVIDE -> result = evaluateInt(expression.getLeft()) / evaluateInt(expression.getRight());
                    default ->
                            throw new InterpreterException("Invalid operation: " + expression.getOperation().toString());
                }
            }
            default -> throw new InterpreterException("Expected a node of type Integer");
        }
        return result;
    }

    public float evaluateFloat(Node node) {

        Float result = 0.0F;

        switch (node) {
            case VariableNode variableNode -> {
                String variableName = variableNode.getName();

                if (floatVariables.containsKey(variableName)) {
                    result = floatVariables.get(variableName);
                } else {
                    throw new InterpreterException("Variable: " + "\"" + variableName + "\"" + " does not exist.");
                }
            }
            case FloatNode number -> result = number.getFloatValue();
            case FunctionNode function -> {

                if (function.getName().equals("RANDOM")) {
                    result = randomFloat();
                } else if (function.getName().equals("VAL%")) {
                    //grabbing the string for conversion
                    String stringParameter = function.getList().pop().toString();
                    result = valFloat(stringParameter);
                }
            }
            case MathOpNode expression -> {
                switch (expression.getOperation()) {
                    case PLUS -> result = evaluateFloat(expression.getLeft()) + evaluateFloat(expression.getRight());
                    case MULTIPLY ->
                            result = evaluateFloat(expression.getLeft()) * evaluateFloat(expression.getRight());
                    case MINUS -> result = evaluateFloat(expression.getLeft()) - evaluateFloat(expression.getRight());
                    case DIVIDE -> result = evaluateFloat(expression.getLeft()) / evaluateFloat(expression.getRight());
                    default ->
                            throw new InterpreterException("Invalid operation: " + expression.getOperation().toString());
                }
            }
            default -> throw new InterpreterException("Expected a node of type Float");
        }
        return result;
    }

    public Object lookUpVariable(String variableName) {
        //lookup the passed in variable name in all maps and return the value

        Object result = null;

        if (stringVariables.containsKey(variableName)) {
            result = stringVariables.get(variableName);
        } else if (integerVariables.containsKey(variableName)) {
            result = integerVariables.get(variableName);
        } else if (floatVariables.containsKey(variableName)) {
            result = floatVariables.get(variableName);
        }
        return result;
    }

    //Preprocess the AST tree for labeled statements
    public void optimizeLabels(StatementNode current) {

        while (current != null) {
            if (current instanceof LabeledStatementNode labeledStatementNode) {
                labeledStatements.put(labeledStatementNode.getLabel(), labeledStatementNode);
            }
            current = current.getNext();
        }

    }

    //Preprocess the AST tree for data statements
    public void optimizeRead(StatementNode current) {
        while (current != null) {
            if (current instanceof DataNode dataNode) {
                dataStatementContents.addAll(dataNode.getDataList());
            }
            current = current.getNext();
        }
    }

    //returns a random integer
    public static int random(){
        Random rand = new Random();
        return rand.nextInt();
    }

    public static float randomFloat(){
        Random rand = new Random();
        return rand.nextFloat();
    }

    //returns the leftmost n characters from the string
    public static String left(String input, int count){

        if(count > input.length()){
            throw new InterpreterException("Count is greater than the length of the string");
        }

        return input.substring(0,count);
    }

    //returns the rightmost N characters from the string
    public static String right(String input, int count){

        if(count > input.length()){
            throw new InterpreterException("Count is greater than the length of the string");
        }

        return input.substring(input.length() - count);
    }

    //returns the characters of the string starting from the 2nd arg and 3rd arg as count
    public static String mid(String input, int position, int count){

        if(position > input.length()){
            throw new InterpreterException("Position is greater than the length of the string");
        }

        return input.substring(position, position + count);
    }
    //converts an integer to a string
    public static String numInt(int input){
        return Integer.toString(input);
    }

    //converts a float to a string
    public static String numFloat(float input){
        return Float.toString(input);
    }
    //converts the string into an integer
    public static int valInt(String input){
        return Integer.parseInt(input);
    }
    //converts the string into a float
    public static float valFloat(String input){
        return Float.parseFloat(input);
    }

    //Accessors and Mutators for Collections
    public LinkedList<Node> getDataStatementContents() {
        return dataStatementContents;
    }
    public HashMap<String,LabeledStatementNode> getLabeledStatements() {
        return labeledStatements;
    }
    public HashMap<String,String> getStringVariables(){
        return this.stringVariables;
    }
    public void addTestInputElements(String input){
        this.testInputElements.add(input);
    }
    public ArrayList<String> getTestPrintElements(){ return this.testPrintElements; }
    public ArrayList<String> getTestInputElements(){ return this.testInputElements; }
    public HashMap<String,Integer> getIntegerVariables(){ return this.integerVariables; }
    public HashMap<String,Float> getFloatVariables(){ return this.floatVariables; }

    private void initializeDataStructures(){
        this.dataStatementContents = new LinkedList<>();
        this.testInputElements = new ArrayList<>();
        this.testPrintElements = new ArrayList<>();
        this.labeledStatements = new HashMap<>();
        this.stringVariables = new HashMap<>();
        this.integerVariables = new HashMap<>();
        this.floatVariables = new HashMap<>();
        this.statementStack = new Stack<>();
    }

}
