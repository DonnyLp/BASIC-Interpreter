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

    @Test
    public void testParse() throws IOException {
        LinkedList<Token> tokens = new LinkedList<>();
        Lexer lexer = new Lexer("test.txt");
        Parser parser = new Parser(lexer.lex());

        Node astTree =  parser.parse();

        assertEquals("((9 - ((2 * 10) / 2)) + ((3 / 4) * 10))\n", astTree.toString());

    }

    @Test
    public void testAddition(){
        LinkedList<Token> tokens = new LinkedList<>();

        tokens.add(new Token(Token.TokenType.NUMBER,"8"));
        tokens.add(new Token(Token.TokenType.PLUS,"+"));
        tokens.add(new Token(Token.TokenType.NUMBER,"9"));
        Parser parser = new Parser(tokens);
        Node astTree = parser.parse();

        assertEquals("(8 + 9)\n", astTree.toString());

    }

    @Test
    public void testMinus(){
        LinkedList<Token> tokens = new LinkedList<>();

        tokens.add(new Token(Token.TokenType.NUMBER, "11"));
        tokens.add(new Token(Token.TokenType.MINUS, "-"));
        tokens.add(new Token(Token.TokenType.NUMBER, "9"));
       Parser parser = new Parser(tokens);
       Node astTree = parser.parse();

        assertEquals("(11 - 9)\n", astTree.toString());

    }
    @Test
    public void testParenthesis(){

        LinkedList<Token> tokens = new LinkedList<>();

        tokens.add(new Token(Token.TokenType.NUMBER, "11"));
        tokens.add(new Token(Token.TokenType.MULTIPLY, "*"));
        tokens.add(new Token(Token.TokenType.LPAREN, "("));
        tokens.add(new Token(Token.TokenType.NUMBER, "10"));
        tokens.add(new Token(Token.TokenType.DIVIDE, "/"));
        tokens.add(new Token(Token.TokenType.NUMBER, "3"));
        tokens.add(new Token(Token.TokenType.RPAREN, ")"));
        tokens.add(new Token(Token.TokenType.PLUS, "+"));
        tokens.add(new Token(Token.TokenType.NUMBER, "2"));

        Parser parser = new Parser(tokens);
        Node astTree = parser.parse();

        assertEquals("((11 * (10 / 3)) + 2)\n", astTree.toString());
    }

    @Test
    public void testPEMDAS(){

        LinkedList<Token> tokens = new LinkedList<>();

        //PEMDAS
        tokens.add(new Token(Token.TokenType.NUMBER, "10"));
        tokens.add(new Token(Token.TokenType.MINUS, "-"));
        tokens.add(new Token(Token.TokenType.NUMBER, "3"));
        tokens.add(new Token(Token.TokenType.MULTIPLY, "*"));
        tokens.add(new Token(Token.TokenType.NUMBER, "11"));
        tokens.add(new Token(Token.TokenType.PLUS, "+"));
        tokens.add(new Token(Token.TokenType.NUMBER, "2"));

        Parser parser = new Parser(tokens);
        Node astTree = parser.parse();

        assertEquals("((10 - (3 * 11)) + 2)\n",astTree.toString());
        tokens.clear();
    }

}
