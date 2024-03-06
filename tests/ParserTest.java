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
        assertEquals(" b = (18 / (3 + 3))  x = ((9 * 10) + b)  [(9 * 10)][x][(10 / 9)][b] ", astTree.toString());

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
        String assignment = parser.assignment().toString();
        System.out.print(assignment);
        assertEquals("x = (x * 11)", assignment);
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

        Parser parser = new Parser(tokens);
        String printList = parser.printStatement().toString();
        assertEquals("[(1 + 2)][(2 + 3)][x]", printList);
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
