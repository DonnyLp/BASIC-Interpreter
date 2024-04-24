import org.junit.Test;
import java.io.IOException;
import java.util.LinkedList;
import static org.junit.Assert.*;
import java.util.HashMap;

public class InterpreterTest {

    @Test
    public void testFizzBuzz() throws IOException{
        LinkedList<Object> fizzBuzzElements = new LinkedList<>();

        for(int i = 1; i <= 100; i++){
            if(i % 3 == 0 && i % 5 == 0){
                fizzBuzzElements.add("Fizz Buzz");
            }else if(i % 3 == 0){
                fizzBuzzElements.add("Fizz");
            }else if(i % 5 == 0){
                fizzBuzzElements.add("Buzz");
            }else{
                fizzBuzzElements.add(i);
            }
        }

        Lexer lexer = new Lexer("fizzbuzz.txt");
        LinkedList<Token> tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        StatementsNode tree = parser.parse();
        Interpreter interpreter = new Interpreter(tree,true);

        interpreter.interpret(tree.getHead());

        assertEquals(fizzBuzzElements, interpreter.getTestPrintElements());

    }

    @Test
    public void testClassAverage() throws IOException{

        LinkedList<Object>testOuput = new LinkedList<>();
        testOuput.add("Billy");
        testOuput.add(81);
        testOuput.add("Mandy");
        testOuput.add(81);
        testOuput.add("Sarah");
        testOuput.add(85);
        testOuput.add("Bob");
        testOuput.add(92);
        testOuput.add("Chad");
        testOuput.add(95);
        Lexer lexer = new Lexer("test.txt");
        LinkedList<Token> tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        StatementsNode tree = parser.parse();
        Interpreter interpreter = new Interpreter(tree,true);

          interpreter.interpret(tree.getHead());

          assertEquals(testOuput, interpreter.getTestPrintElements());

    }
    @Test
    public void testInterpretForNode() throws IOException {

        LinkedList<Integer>testOuput = new LinkedList<>();
        testOuput.add(2);
        testOuput.add(4);
        testOuput.add(6);
        testOuput.add(8);
        testOuput.add(10);

        Lexer lexer = new Lexer("forlooptest.txt");
        LinkedList<Token> tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        StatementsNode tree = parser.parse();
        Interpreter interpreter = new Interpreter(tree,true);

        interpreter.interpret(tree.getHead());

        assertEquals(testOuput.toString(), interpreter.getTestPrintElements().toString());
    }

    @Test
    public void testInterpretWhileNode() throws IOException {
        LinkedList<Integer>testOuput = new LinkedList<>();
        testOuput.add(1);
        testOuput.add(2);
        testOuput.add(3);
        testOuput.add(4);
        testOuput.add(5);
        Lexer lexer = new Lexer("whiletest.txt");
        LinkedList<Token> tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        StatementsNode tree = parser.parse();
        Interpreter interpreter = new Interpreter(tree,true);
        interpreter.interpret(tree.getHead());
        assertEquals(testOuput,interpreter.getTestPrintElements());
    }

    @Test
    public void testInterpretIfNode() throws IOException {
        Lexer lexer = new Lexer("ifstatement.txt");
        LinkedList<Token> tokens = lexer.lex();
        Parser parser = new Parser(tokens);
        StatementsNode tree = parser.parse();
        Interpreter interpreter = new Interpreter(tree,true);
        interpreter.interpret(tree.getHead());
       assertEquals("[4]", interpreter.getTestPrintElements().toString());
    }

    @Test
    public void testInterpretInput(){

        LinkedList<Token> tokens = new LinkedList<>();

        tokens.add(new Token(Token.TokenType.INPUT, "INPUT"));
        tokens.add(new Token(Token.TokenType.STRINGLITERAL, "\"What is your name and age?\""));
        tokens.add(new Token(Token.TokenType.COMMA, ","));
        tokens.add(new Token(Token.TokenType.WORD, "name$"));
        tokens.add(new Token(Token.TokenType.COMMA, ","));
        tokens.add(new Token(Token.TokenType.WORD, "age"));
        tokens.add(new Token(Token.TokenType.END));

        Parser parser = new Parser(tokens);
        StatementsNode astTree = parser.parse();
        Interpreter interpreter = new Interpreter(astTree,true);
        interpreter.addTestInputElements("dondario");
        interpreter.addTestInputElements("20");
        interpreter.interpret((StatementNode) astTree.getList().peek());

        //checking if test list has been populated with the initial string
        assertEquals("\"What is your name and age?\"",interpreter.getTestInputElements().get(0));

        //verifying that variable storage has been updated after interpret call
        assertEquals("{age=20}",interpreter.getIntegerVariables().toString());
        assertEquals("{name$=dondario}",interpreter.getStringVariables().toString());
    }

    @Test
    public void testInterpretPrint(){
        LinkedList<Token> tokens = new LinkedList<>();

        tokens.add(new Token(Token.TokenType.PRINT, "PRINT"));
        tokens.add(new Token(Token.TokenType.STRINGLITERAL, "hello. 5 + 3 ="));
        tokens.add(new Token(Token.TokenType.COMMA, ","));
        tokens.add(new Token(Token.TokenType.NUMBER, "5"));
        tokens.add(new Token(Token.TokenType.PLUS, "+"));
        tokens.add(new Token(Token.TokenType.NUMBER, "3"));
        tokens.add(new Token(Token.TokenType.ENDOFLINE));
        tokens.add(new Token(Token.TokenType.END));

        Parser parser = new Parser(tokens);
        StatementsNode astTree = parser.parse();
        Interpreter interpreter = new Interpreter(astTree,true);

        //Grabs the first statement in the astTree: "PRINT "hello. 5 + 3 =", 5 + 3
        interpreter.interpret((StatementNode) astTree.getList().peek());

        //check that print statement has been added correctly and that
        assertEquals("[hello. 5 + 3 =, 8]", interpreter.getTestPrintElements().toString());
    }
    @Test
    public void testInterpretRead(){
        //READ LIST:

        LinkedList<Token> tokens = new LinkedList<>();

        tokens.add(new Token(Token.TokenType.READ, "READ"));
        tokens.add(new Token(Token.TokenType.WORD, "a$"));
        tokens.add(new Token(Token.TokenType.COMMA, ","));
        tokens.add(new Token(Token.TokenType.WORD, "ab%"));
        tokens.add(new Token(Token.TokenType.COMMA, ","));
        tokens.add(new Token(Token.TokenType.WORD, "a%"));
        tokens.add(new Token(Token.TokenType.COMMA, ","));
        tokens.add(new Token(Token.TokenType.WORD, "a"));
        tokens.add(new Token(Token.TokenType.ENDOFLINE));

        //DATA LIST:

        tokens.add(new Token(Token.TokenType.DATA, "DATA"));
        tokens.add(new Token(Token.TokenType.STRINGLITERAL, "\"Test String Literal\""));
        tokens.add(new Token(Token.TokenType.COMMA, ","));
        tokens.add(new Token(Token.TokenType.NUMBER, "7.2"));
        tokens.add(new Token(Token.TokenType.COMMA, ","));
        tokens.add(new Token(Token.TokenType.NUMBER, "8.9"));
        tokens.add(new Token(Token.TokenType.COMMA, ","));
        tokens.add(new Token(Token.TokenType.NUMBER, "8"));
        tokens.add(new Token(Token.TokenType.ENDOFLINE));
        tokens.add(new Token(Token.TokenType.END));

        Parser parser = new Parser(tokens);

        StatementsNode astTree = parser.parse();
        LinkedList<StatementNode> statements = astTree.getList();
        Interpreter interpreter = new Interpreter(astTree,true);

        //populate the data statements list
        interpreter.optimizeRead(astTree.getHead());

        for(StatementNode statement: statements){
            interpreter.interpret(statement);
        }

        assertEquals("{a=8}", interpreter.getIntegerVariables().toString());
        assertEquals("{a$=\"Test String Literal\"}", interpreter.getStringVariables().toString());
        assertEquals("{a%=8.9, ab%=7.2}", interpreter.getFloatVariables().toString());
    }

    @Test
    public void testEvaluateString(){

        Interpreter interpreter = new Interpreter();

        StringNode testString = new StringNode("Test1");

        String result = interpreter.evaluateString(testString);

        assertEquals("Test1", result);
    }

    @Test
    public void testEvaluateFloat(){
        FloatNode number = new FloatNode(8.2F);
        FloatNode number1 = new FloatNode(8.5F);

        MathOpNode expression = new MathOpNode(number,Token.TokenType.PLUS, number1);
        MathOpNode expression1 = new MathOpNode(number,Token.TokenType.MULTIPLY, expression);

        //final expression: (8.2+8.5) + (8.2 * (8.2+8.5))

        MathOpNode finalExpression = new MathOpNode(expression,Token.TokenType.PLUS, expression1);

        Interpreter interpreter = new Interpreter();

        Float result = interpreter.evaluateFloat(finalExpression);

        System.out.print(result);

        assertEquals("153.64", result.toString());
    }
    @Test
    public void testEvaluateFloat1(){
        FloatNode number = new FloatNode(8.2F);
        FloatNode number1 = new FloatNode(8.5F);

        MathOpNode expression = new MathOpNode(number,Token.TokenType.PLUS, number1);
        MathOpNode expression1 = new MathOpNode(number,Token.TokenType.MULTIPLY, expression);

        //final expression: (8.2+8.5) / (8.2 * (8.2+8.5))

        MathOpNode finalExpression = new MathOpNode(expression,Token.TokenType.DIVIDE, expression1);

        Interpreter interpreter = new Interpreter();

        Float result = interpreter.evaluateFloat(finalExpression);

        assertEquals("0.12195122", result.toString());
    }
    @Test
    public void testEvaluateFloatFunctionCall(){

        FloatNode number1 = new FloatNode(2.5f);
        FloatNode number2 = new FloatNode(2.5f);

        MathOpNode expression = new MathOpNode(number1,Token.TokenType.PLUS, number2);
        LinkedList<Node> parameters = new LinkedList<>();
        parameters.add(new StringNode("2.5"));
        FunctionNode functionCall = new FunctionNode("VAL%",parameters);

        //final expression: (2.5 + 2.5) + VAL("2.5")

        MathOpNode finalExpression = new MathOpNode(expression,Token.TokenType.PLUS, functionCall);

        Interpreter interpreter = new Interpreter();

        Float result = interpreter.evaluateFloat(finalExpression);

        assertEquals("7.5", result.toString());
    }


    @Test
    public void testEvaluateIntFunctionCall(){

        IntegerNode integer = new IntegerNode(8);
        IntegerNode integer1 = new IntegerNode(8);

        MathOpNode expression = new MathOpNode(integer,Token.TokenType.PLUS, integer1);
        LinkedList<Node> parameters = new LinkedList<>();
        parameters.add(new StringNode("8"));
        FunctionNode functionCall = new FunctionNode("VAL",parameters);

        //final expression: (8+8) + VAL("8")

        MathOpNode finalExpression = new MathOpNode(expression,Token.TokenType.PLUS, functionCall);

        Interpreter interpreter = new Interpreter();

        Integer result = interpreter.evaluateInt(finalExpression);


        assertEquals("24", result.toString());
    }

    @Test
    public void testEvaluateInt(){
        IntegerNode integer = new IntegerNode(8);

        Interpreter interpreter = new Interpreter();

        Integer result = interpreter.evaluateInt(integer);

        System.out.print(result);
    }

    @Test
    public void testEvaluateInt1(){

        IntegerNode integer = new IntegerNode(8);
        IntegerNode integer1 = new IntegerNode(8);

        MathOpNode expression = new MathOpNode(integer,Token.TokenType.PLUS, integer1);
        MathOpNode expression1 = new MathOpNode(integer,Token.TokenType.MULTIPLY, expression);

        //final expression: (8+8) + (8 * (8+8))

        MathOpNode finalExpression = new MathOpNode(expression,Token.TokenType.PLUS, expression1);

        Interpreter interpreter = new Interpreter();

        Integer result = interpreter.evaluateInt(finalExpression);

        System.out.print(result);
    }

    @Test
    public void testEvaluateInt2(){

        IntegerNode integer = new IntegerNode(8);
        IntegerNode integer1 = new IntegerNode(8);

        MathOpNode expression = new MathOpNode(integer,Token.TokenType.PLUS, integer1);
        MathOpNode expression1 = new MathOpNode(expression,Token.TokenType.DIVIDE, integer1);

        //final expression: (8+8) - ((8+8) / 8)

        MathOpNode finalExpression = new MathOpNode(expression,Token.TokenType.MINUS, expression1);

        Interpreter interpreter = new Interpreter();

        Integer result = interpreter.evaluateInt(finalExpression);

        System.out.print(result);
    }

    @Test
    public void testOptimizeRead() throws IOException {

        LinkedList<Token> tokens = new LinkedList<>();
        //DATA LIST:
        tokens.add(new Token(Token.TokenType.DATA, "DATA"));
        tokens.add(new Token(Token.TokenType.NUMBER, "10"));
        tokens.add(new Token(Token.TokenType.COMMA, ","));
        tokens.add(new Token(Token.TokenType.NUMBER, "22.3"));
        tokens.add(new Token(Token.TokenType.COMMA, ","));
        tokens.add(new Token(Token.TokenType.NUMBER, "33.1"));
        tokens.add(new Token(Token.TokenType.COMMA, ","));
        tokens.add(new Token(Token.TokenType.NUMBER, "55.2"));
        tokens.add(new Token(Token.TokenType.COMMA, ","));
        tokens.add(new Token(Token.TokenType.NUMBER, "44.2"));
        tokens.add(new Token(Token.TokenType.COMMA, ","));
        tokens.add(new Token(Token.TokenType.NUMBER, "17.8"));
        tokens.add(new Token(Token.TokenType.COMMA, ","));
        tokens.add(new Token(Token.TokenType.NUMBER, "66.2"));

        Parser parser = new Parser(tokens);
        StatementsNode astTree =  parser.parse();
        Interpreter interpreter = new Interpreter(astTree,true);

        interpreter.optimizeRead(astTree.getHead());

        LinkedList<Node> dataContents = interpreter.getDataStatementContents();

        assertEquals(
                "[10, 22.3, 33.1, 55.2, 44.2, 17.8, 66.2]",
                dataContents.toString()
        );
    }

    @Test
    public void testOptimizeLabels()throws IOException {
        Lexer lexer = new Lexer("maintest.txt");
        LinkedList<Token> tokens = new LinkedList<>();
        Parser parser = new Parser(lexer.lex());
        StatementsNode astTree =  parser.parse();
        Interpreter interpreter = new Interpreter(astTree,true);

        interpreter.optimizeLabels(astTree.getHead());

        HashMap<String, LabeledStatementNode> labeledStatements = interpreter.getLabeledStatements();

        //check that the labeled statements key is store correctly
        assertTrue(labeledStatements.containsKey("Convert"));
    }

    @Test
    public void testRandom() {
        int random = Interpreter.random();
        System.out.print(random);
    }

    @Test
    public void testLeft() {
        String input = "Hello";
        String left = Interpreter.left(input, 2);
        assertEquals("He", left);
    }

    @Test
    public void testRight() {
        String input = "Hello";
        String right = Interpreter.right(input, 2);
        assertEquals("lo", right);
    }

    @Test
    public void testMid() {
        String input = "Hello";
        String mid = Interpreter.mid(input, 1, 2);
        assertEquals("el", mid);
    }

    @Test
    public void testMidPositionGreaterThanLength() {
        String input = "Hello";
        try {
            Interpreter.mid(input, 6, 2);
            fail("Expected InterpreterException");
        } catch (InterpreterException e) {
            assertEquals("Position is greater than the length of the string", e.getMessage());
        }
    }

    @Test
    public void testRightCountGreaterThanLength() {
        String input = "Hello";
        try {
            Interpreter.right(input, 6);
            fail("Expected InterpreterException");
        } catch (InterpreterException e) {
            assertEquals("Count is greater than the length of the string", e.getMessage());
        }
    }

    @Test
    public void testLeftCountGreaterThanLength() {
        String input = "Hello";
        try {
            Interpreter.left(input, 6);
            fail("Expected InterpreterException");
        } catch (InterpreterException e) {
            assertEquals("Count is greater than the length of the string", e.getMessage());
        }
    }

    @Test
    public void testNumInt() {
        int input = 5;
        String numInt = Interpreter.numInt(input);
        assertEquals("5", numInt);
    }

    @Test
    public void testNumFloat() {
        float input = 5.5f;
        String numFloat = Interpreter.numFloat(input);
        assertEquals("5.5", numFloat);
    }

    @Test
    public void testValInt() {
        String input = "5";
        int valInt = Interpreter.valInt(input);
        assertEquals(5, valInt);
    }

    @Test
    public void testValFloat() {
        String input = "5.5";
        float valFloat = Interpreter.valFloat(input);
        assertEquals(5.5f, valFloat, 0.0);
    }

    @Test
    public void testValIntInvalid() {
        String input = "5.5";
        try {
            Interpreter.valInt(input);
            fail("Expected NumberFormatException");
        } catch (NumberFormatException e) {
            assertEquals("For input string: \"5.5\"", e.getMessage());
        }
    }

    @Test
    public void testValFloatInvalid() {
        String input = "5.5.5";
        try {
            Interpreter.valFloat(input);
            fail("Expected NumberFormatException");
        } catch (NumberFormatException e) {
            assertEquals("multiple points", e.getMessage());
        }
    }

}
