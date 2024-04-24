import org.junit.Test;
import java.io.IOException;
import java.util.LinkedList;

import static org.junit.Assert.*;

public class ParserTest{
    @Test
    public void testHandleSeparators() {
        // Create a LinkedList of Tokens
        LinkedList<Token> tokens = new LinkedList<>();
        tokens.add(new Token(Token.TokenType.ENDOFLINE, "\n"));
        tokens.add(new Token(Token.TokenType.ENDOFLINE, "\n"));
        tokens.add(new Token(Token.TokenType.ENDOFLINE, "\n"));

        // Create a Parser object with the list of Tokens
        Parser parser = new Parser(tokens);

        // Call handleSeparators and check the return value
        assertTrue(parser.handleSeparators());

        // Create a new list of Tokens without ENDOFLINE
        tokens = new LinkedList<>();
        tokens.add(new Token(Token.TokenType.NUMBER, "123"));

        // Create a new Parser object with the new list of Tokens
        parser = new Parser(tokens);

        // Call handleSeparators and check the return value
        assertFalse(parser.handleSeparators());
    }

    //This test exclusively tests input from the test file and tests the statement and statements methods
    @Test
    public void testParse() throws IOException {
        Lexer lexer = new Lexer("test.txt");
        Parser parser = new Parser(lexer.lex());

        StatementsNode astTree =  parser.parse();

        assertEquals("READ count\n" +
                "FOR I = 0 TO count\n" +
                "READ F%\n" +
                "GOSUB Convert\n" +
                "NEXT I\n" +
                "END\n" +
                "DATA 10,22.3,33.1,55.2,44.2,17.8,66.2,47.1,33.2,42.9,17.2\n" +
                "Convert: \n" +
                "C% = ((5 * (F% - 32)) / 9)\n" +
                "PRINT F%,\"DEG F = \",C%,\"DEG C\"\n" +
                "RETURN\n", astTree.toString());
    }

    @Test
    public void testRandom(){
        LinkedList<Token> tokens = new LinkedList<>();
        tokens.add(new Token(Token.TokenType.RANDOM));
        tokens.add(new Token(Token.TokenType.LPAREN));
        tokens.add(new Token(Token.TokenType.RPAREN));

        Parser parser = new Parser(tokens);
        //This expects null because the value for any of the function name's is
        assertEquals("RANDOM()", parser.expression().toString());
    }
    @Test
    public void testLEFT$(){

        LinkedList<Token> tokens = new LinkedList<>();
        tokens.add(new Token(Token.TokenType.LEFT$));
        tokens.add(new Token(Token.TokenType.LPAREN));
        tokens.add(new Token(Token.TokenType.STRINGLITERAL, "Albany"));
        tokens.add(new Token(Token.TokenType.COMMA, ","));
        tokens.add(new Token(Token.TokenType.NUMBER, "6"));
        tokens.add(new Token(Token.TokenType.RPAREN));

        Parser parser = new Parser(tokens);
        assertEquals("LEFT$(Albany,6)", parser.expression().toString());
    }
    
    @Test
    public void testRIGHT$(){
        LinkedList<Token> tokens = new LinkedList<>();
        tokens.add(new Token(Token.TokenType.RIGHT$));
        tokens.add(new Token(Token.TokenType.LPAREN));
        tokens.add(new Token(Token.TokenType.STRINGLITERAL, "Albany"));
        tokens.add(new Token(Token.TokenType.COMMA, ","));
        tokens.add(new Token(Token.TokenType.NUMBER, "4"));
        tokens.add(new Token(Token.TokenType.RPAREN));

        Parser parser = new Parser(tokens);
        assertEquals("RIGHT$(Albany,4)", parser.expression().toString());
    }
    @Test
    public void testMID$(){
        LinkedList<Token> tokens = new LinkedList<>();
        tokens.add(new Token(Token.TokenType.MID$));
        tokens.add(new Token(Token.TokenType.LPAREN));
        tokens.add(new Token(Token.TokenType.STRINGLITERAL, "Albany"));
        tokens.add(new Token(Token.TokenType.COMMA, ","));
        tokens.add(new Token(Token.TokenType.NUMBER, "2"));
        tokens.add(new Token(Token.TokenType.COMMA, ","));
        tokens.add(new Token(Token.TokenType.NUMBER, "3"));
        tokens.add(new Token(Token.TokenType.RPAREN));

        Parser parser = new Parser(tokens);
        assertEquals("MID$(Albany,2,3)", parser.expression().toString());
    }
    @Test
    public void testNUM$(){
        LinkedList<Token> tokens = new LinkedList<>();
        tokens.add(new Token(Token.TokenType.NUM$));
        tokens.add(new Token(Token.TokenType.LPAREN));
        tokens.add(new Token(Token.TokenType.NUMBER, "3"));
        tokens.add(new Token(Token.TokenType.RPAREN));

        Parser parser = new Parser(tokens);
        assertEquals("NUM$(3)", parser.expression().toString());
    }
    @Test
    public void testValInt(){
        LinkedList<Token> tokens = new LinkedList<>();
        tokens.add(new Token(Token.TokenType.VAL_INT));
        tokens.add(new Token(Token.TokenType.LPAREN));
        tokens.add(new Token(Token.TokenType.STRINGLITERAL, "8"));
        tokens.add(new Token(Token.TokenType.RPAREN));

        Parser parser = new Parser(tokens);
        assertEquals("VAL(8)", parser.expression().toString());
    }
    @Test
    public void testValFloat(){
        LinkedList<Token> tokens = new LinkedList<>();
        tokens.add(new Token(Token.TokenType.VAL_FLOAT));
        tokens.add(new Token(Token.TokenType.LPAREN));
        tokens.add(new Token(Token.TokenType.STRINGLITERAL, "3.4"));
        tokens.add(new Token(Token.TokenType.RPAREN));

        Parser parser = new Parser(tokens);
        assertEquals("VAL%(3.4)", parser.expression().toString());
    }

    @Test
    public void testEndStatement(){
        LinkedList<Token> tokens = new LinkedList<>();
        tokens.add(new Token(Token.TokenType.END));

        Parser parser = new Parser(tokens);
        assertEquals("END", parser.parseEndStatement().toString());
    }
    @Test
    public void testGoSubStatement(){
        LinkedList<Token> tokens = new LinkedList<>();
        tokens.add(new Token(Token.TokenType.GOSUB));
        tokens.add(new Token(Token.TokenType.WORD, "FtoC"));

        Parser parser = new Parser(tokens);
        assertEquals("GOSUB FtoC", parser.parseGoSubStatement().toString());
    }

    @Test
    public void testWhileStatement(){
        LinkedList<Token> tokens = new LinkedList<>();

        tokens.add(new Token(Token.TokenType.WHILE));
        tokens.add(new Token(Token.TokenType.WORD, "x"));
        tokens.add(new Token(Token.TokenType.LESSTHAN, "<"));
        tokens.add(new Token(Token.TokenType.NUMBER, "5"));
        tokens.add(new Token(Token.TokenType.WORD, "endWhileLabel"));
        tokens.add(new Token(Token.TokenType.ENDOFLINE));
        tokens.add(new Token(Token.TokenType.WORD, "x"));
        tokens.add(new Token(Token.TokenType.EQUALS, "="));
        tokens.add(new Token(Token.TokenType.WORD, "x"));
        tokens.add(new Token(Token.TokenType.PLUS, "+"));
        tokens.add(new Token(Token.TokenType.NUMBER, "1"));
        tokens.add(new Token(Token.TokenType.ENDOFLINE));
        tokens.add(new Token(Token.TokenType.LABEL, "endWhileLabel"));
        tokens.add(new Token(Token.TokenType.ENDOFLINE));

        Parser parser = new Parser(tokens);
        assertEquals(
                "WHILE x<5 endWhileLabel\n" +
                        "x = (x + 1)\n" +
                        "endWhileLabel:", parser.parseWhileStatement().toString());
    }

    @Test
    public void testIfStatement(){
        LinkedList<Token> tokens = new LinkedList<>();

        tokens.add(new Token(Token.TokenType.IF));
        tokens.add(new Token(Token.TokenType.WORD, "x"));
        tokens.add(new Token(Token.TokenType.LESSTHAN, "<"));
        tokens.add(new Token(Token.TokenType.NUMBER, "5"));
        tokens.add(new Token(Token.TokenType.THEN));
        tokens.add(new Token(Token.TokenType.WORD, "xIsSmall"));

        Parser parser = new Parser(tokens);

        assertEquals("IF x<5 THEN xIsSmall", parser.parseIfStatement().toString());
    }

    @Test
    public void testForStatementWithStep(){

        LinkedList<Token> tokens = new LinkedList<>();

        tokens.add(new Token(Token.TokenType.FOR));
        tokens.add(new Token(Token.TokenType.WORD, "A"));
        tokens.add(new Token(Token.TokenType.EQUALS, "="));
        tokens.add(new Token(Token.TokenType.NUMBER, "0"));
        tokens.add(new Token(Token.TokenType.TO));
        tokens.add(new Token(Token.TokenType.NUMBER, "10"));
        tokens.add(new Token(Token.TokenType.STEP));
        tokens.add(new Token(Token.TokenType.NUMBER, "2"));
        tokens.add(new Token(Token.TokenType.ENDOFLINE));
        tokens.add(new Token(Token.TokenType.PRINT));
        tokens.add(new Token(Token.TokenType.WORD,"A"));
        tokens.add(new Token(Token.TokenType.ENDOFLINE));
        tokens.add(new Token(Token.TokenType.NEXT));
        tokens.add(new Token(Token.TokenType.WORD, "A"));
        tokens.add(new Token(Token.TokenType.ENDOFLINE));

        Parser parser = new Parser(tokens);
        assertEquals(
                "FOR A = 0 TO 10 STEP 2\n" +
                "PRINT A\n" +
                "NEXT A", parser.parseForStatement().toString());
    }

    @Test
    public void testForStatementWithoutStep(){

        LinkedList<Token> tokens = new LinkedList<>();

        tokens.add(new Token(Token.TokenType.FOR));
        tokens.add(new Token(Token.TokenType.WORD, "A"));
        tokens.add(new Token(Token.TokenType.EQUALS, "="));
        tokens.add(new Token(Token.TokenType.NUMBER, "0"));
        tokens.add(new Token(Token.TokenType.TO));
        tokens.add(new Token(Token.TokenType.NUMBER, "10"));
        tokens.add(new Token(Token.TokenType.ENDOFLINE));
        tokens.add(new Token(Token.TokenType.PRINT));
        tokens.add(new Token(Token.TokenType.WORD,"A"));
        tokens.add(new Token(Token.TokenType.ENDOFLINE));
        tokens.add(new Token(Token.TokenType.NEXT));
        tokens.add(new Token(Token.TokenType.WORD, "A"));
        tokens.add(new Token(Token.TokenType.ENDOFLINE));

        Parser parser = new Parser(tokens);
        assertEquals(
                "FOR A = 0 TO 10\n" +
                        "PRINT A\n" +
                        "NEXT A", parser.parseForStatement().toString());

    }

    @Test
    public void testInput(){
        LinkedList<Token> tokens = new LinkedList<>();

        tokens.add(new Token(Token.TokenType.INPUT, "INPUT"));
        tokens.add(new Token(Token.TokenType.STRINGLITERAL, "\"What is your name and age?\""));
        tokens.add(new Token(Token.TokenType.COMMA, ","));
        tokens.add(new Token(Token.TokenType.WORD, "name$"));
        tokens.add(new Token(Token.TokenType.COMMA, ","));
        tokens.add(new Token(Token.TokenType.WORD, "age"));

        Parser parse = new Parser(tokens);
        assertEquals("INPUT \"What is your name and age?\",name$,age", parse.parseInputStatement().toString());
    }
    @Test
    public void testData(){
        LinkedList<Token> tokens = new LinkedList<>();

        tokens.add(new Token(Token.TokenType.DATA, "DATA"));
        tokens.add(new Token(Token.TokenType.STRINGLITERAL, "\"Test String Literal\""));
        tokens.add(new Token(Token.TokenType.COMMA, ","));
        tokens.add(new Token(Token.TokenType.NUMBER, "7.2"));
        tokens.add(new Token(Token.TokenType.COMMA, ","));
        tokens.add(new Token(Token.TokenType.NUMBER, "8.9"));
        tokens.add(new Token(Token.TokenType.COMMA, ","));
        tokens.add(new Token(Token.TokenType.NUMBER, "8"));

        Parser parser = new Parser(tokens);
        assertEquals("DATA \"Test String Literal\",7.2,8.9,8", parser.parseDataStatement().toString());
    }
    @Test
    public void testRead(){
        LinkedList<Token> tokens = new LinkedList<>();

        tokens.add(new Token(Token.TokenType.READ, "READ"));
        tokens.add(new Token(Token.TokenType.WORD, "a$"));
        tokens.add(new Token(Token.TokenType.COMMA, ","));
        tokens.add(new Token(Token.TokenType.WORD, "a%"));
        tokens.add(new Token(Token.TokenType.COMMA, ","));
        tokens.add(new Token(Token.TokenType.WORD, "a"));
        tokens.add(new Token(Token.TokenType.COMMA, ","));
        tokens.add(new Token(Token.TokenType.WORD, "a$"));

        Parser parser = new Parser(tokens);
        assertEquals("READ a$,a%,a,a$", parser.parseReadStatement().toString());
    }
    @Test
    public void testStringLiteral(){
        //StringLiteral tokens
        LinkedList <Token> tokens = new LinkedList<>();
        tokens.add(new Token(Token.TokenType.STRINGLITERAL, "\"Test String Literal\""));

        Parser parser = new Parser(tokens);
        String stringLiteral = parser.parseStringLiteral().toString();
        assertEquals("\"Test String Literal\"", stringLiteral);
    }
    @Test
    public void testPrint(){
        LinkedList<Token> tokens = new LinkedList<>();

        //Print
        tokens.add(new Token(Token.TokenType.PRINT, "PRINT"));
        tokens.add(new Token(Token.TokenType.NUMBER, "1"));
        tokens.add(new Token(Token.TokenType.PLUS, "+"));
        tokens.add(new Token(Token.TokenType.NUMBER, "2"));
        tokens.add(new Token(Token.TokenType.COMMA, ","));
        tokens.add(new Token(Token.TokenType.NUMBER, "2"));
        tokens.add(new Token(Token.TokenType.PLUS, "+"));
        tokens.add(new Token(Token.TokenType.NUMBER, "3"));
        tokens.add(new Token(Token.TokenType.COMMA, ","));
        tokens.add(new Token(Token.TokenType.WORD, "x"));
        tokens.add(new Token(Token.TokenType.COMMA, ","));
        tokens.add(new Token(Token.TokenType.STRINGLITERAL, "\"Test Literal\""));
        tokens.add(new Token(Token.TokenType.COMMA, ","));
        tokens.add(new Token(Token.TokenType.WORD, "name$"));

        Parser parser = new Parser(tokens);
        String printList = parser.parsePrintStatement().toString();
        assertEquals("PRINT (1 + 2),(2 + 3),x,\"Test Literal\",name$", printList);
    }

    @Test
    public void testAssignment(){
        LinkedList<Token> tokens = new LinkedList<>();

        //Assignment
        tokens.add(new Token(Token.TokenType.WORD, "x"));
        tokens.add(new Token(Token.TokenType.EQUALS, "="));
        tokens.add(new Token(Token.TokenType.WORD, "x"));
        tokens.add(new Token(Token.TokenType.MULTIPLY, "*"));
        tokens.add(new Token(Token.TokenType.NUMBER, "11"));

        Parser parser = new Parser(tokens);
        String assignment = parser.parseAssignment().toString();
        System.out.print(assignment);
        assertEquals("x = (x * 11)", assignment);
    }

    //These collection of test have no use in any versions of the parser after, parser 2
//    @Test
//    public void testAddition(){
//        LinkedList<Token> tokens = new LinkedList<>();
//
//        tokens.add(new Token(Token.TokenType.NUMBER,"8"));
//        tokens.add(new Token(Token.TokenType.PLUS,"+"));
//        tokens.add(new Token(Token.TokenType.NUMBER,"9"));
//        Parser parser = new Parser(tokens);
//        Node astTree = parser.parse();
//
//        assertEquals("(8 + 9)\n", astTree.toString());
//
//    }
//
//    @Test
//    public void testMinus(){
//        LinkedList<Token> tokens = new LinkedList<>();
//
//        tokens.add(new Token(Token.TokenType.NUMBER, "11"));
//        tokens.add(new Token(Token.TokenType.MINUS, "-"));
//        tokens.add(new Token(Token.TokenType.NUMBER, "9"));
//       Parser parser = new Parser(tokens);
//       Node astTree = parser.parse();
//
//        assertEquals("(11 - 9)\n", astTree.toString());
//
//    }
//    @Test
//    public void testParenthesis(){
//
//        LinkedList<Token> tokens = new LinkedList<>();
//
//        tokens.add(new Token(Token.TokenType.NUMBER, "11"));
//        tokens.add(new Token(Token.TokenType.MULTIPLY, "*"));
//        tokens.add(new Token(Token.TokenType.LPAREN, "("));
//        tokens.add(new Token(Token.TokenType.NUMBER, "10"));
//        tokens.add(new Token(Token.TokenType.DIVIDE, "/"));
//        tokens.add(new Token(Token.TokenType.NUMBER, "3"));
//        tokens.add(new Token(Token.TokenType.RPAREN, ")"));
//        tokens.add(new Token(Token.TokenType.PLUS, "+"));
//        tokens.add(new Token(Token.TokenType.NUMBER, "2"));
//
//        Parser parser = new Parser(tokens);
//        Node astTree = parser.parse();
//
//        assertEquals("((11 * (10 / 3)) + 2)\n", astTree.toString());
//    }
//
//    @Test
//    public void testPEMDAS(){
//
//        LinkedList<Token> tokens = new LinkedList<>();
//
//        //PEMDAS
//        tokens.add(new Token(Token.TokenType.NUMBER, "10"));
//        tokens.add(new Token(Token.TokenType.MINUS, "-"));
//        tokens.add(new Token(Token.TokenType.NUMBER, "3"));
//        tokens.add(new Token(Token.TokenType.MULTIPLY, "*"));
//        tokens.add(new Token(Token.TokenType.NUMBER, "11"));
//        tokens.add(new Token(Token.TokenType.PLUS, "+"));
//        tokens.add(new Token(Token.TokenType.NUMBER, "2"));
//
//        Parser parser = new Parser(tokens);
//        Node astTree = parser.parse();
//
//        assertEquals("((10 - (3 * 11)) + 2)\n",astTree.toString());
//        tokens.clear();
//    }

}
