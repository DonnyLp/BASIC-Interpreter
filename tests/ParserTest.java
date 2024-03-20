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
        LinkedList<Token> tokens = new LinkedList<>();
        Lexer lexer = new Lexer("test.txt");
        Parser parser = new Parser(lexer.lex());

        Node astTree =  parser.parse();

        //This assert includes two assignments and elements of a print call
        assertEquals("""
                x = (3 * 4)
                INPUT "What is your name and age?"  name$  age\s
                PRINT "Hi "  name$  " you are "  age  "years old!"\s
                DATA 10  "testParse"  3.145\s
                READ random  a$  a%\s
                """, astTree.toString());
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
        assertEquals("INPUT \"\"What is your name and age?\"\"  name$  age ", parse.parseInputStatement().toString());
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
        assertEquals("DATA \"\"Test String Literal\"\"  7.2  8.9  8 ", parser.parseDataStatement().toString());
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
        assertEquals("READ a$  a%  a  a$ ", parser.parseReadStatement().toString());
    }
    @Test
    public void testStringLiteral(){
        //StringLiteral tokens
        LinkedList <Token> tokens = new LinkedList<>();
        tokens.add(new Token(Token.TokenType.STRINGLITERAL, "\"Test String Literal\""));

        Parser parser = new Parser(tokens);
        String stringLiteral = parser.parseStringLiteral().toString();
        assertEquals("\"\"Test String Literal\"\"", stringLiteral);
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
        assertEquals("PRINT (1 + 2)  (2 + 3)  x  \"\"Test Literal\"\"  name$ ", printList);
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

    //This collection of tests have no use for this version of the parser
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
