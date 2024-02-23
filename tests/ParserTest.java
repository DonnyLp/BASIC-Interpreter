import org.junit.Test;

import java.util.LinkedList;

import static org.junit.Assert.*;

public class ParserTest{
    @Test
    public void testHandleSeparators() {
        // Create a LinkedList of Tokens
        LinkedList<Token> tokens = new LinkedList<>();
        tokens.add(new Token(Token.TokenType.NUMBER, "123"));

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
    public void testFactor(){
        LinkedList<Token> tokens = new LinkedList<>();
        tokens.add(new Token(Token.TokenType.NUMBER, "3.0"));
        tokens.add(new Token(Token.TokenType.NUMBER, "3"));
        tokens.add(new Token(Token.TokenType.MINUS, "-"));
        tokens.add(new Token(Token.TokenType.NUMBER, "2"));
        tokens.add(new Token(Token.TokenType.NUMBER, "2.0"));

        Parser parser = new Parser(tokens);

        Node node1 =  parser.factor();
        Node node2 =  parser.factor();
        Node node3 =  parser.factor();
        Node node4 =  parser.factor();

        assertEquals("3.0", node1.getValue());
        assertEquals("3", node2.getValue());
        assertEquals("-2", node3.getValue());
        assertEquals("2.0", node4.getValue());
    }
}
