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
    public void testParse(){

    }
}
