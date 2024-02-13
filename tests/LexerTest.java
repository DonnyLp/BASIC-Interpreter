import java.io.IOException;
import java.util.LinkedList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;




public class LexerTest {

    @Test
    public void lexTest() throws IOException {
        Lexer lexer = new Lexer("test.txt");
        LinkedList<Token> tokens = lexer.lex();
        Token token = lexer.nexToken();


        //WORD TEST

        assertEquals(Token.TokenType.WORD, token.getType());
        assertEquals("Test", token.getValue());
        token = lexer.nexToken();


        //SYMBOLS TEST
        assertEquals(Token.TokenType.NOTEQUALS, token.getType());
        assertEquals("<>", token.getValue());
        token = lexer.nexToken();

        assertEquals(Token.TokenType.LESSTHAN, token.getType());
        assertEquals("<", token.getValue());
        token = lexer.nexToken();

        //NUMBERS TEST
        assertEquals(Token.TokenType.NUMBER, token.getType());
        assertEquals("3.1459", token.getValue());
        token = lexer.nexToken();

        //STRING LITERALS TEST

        //empty string literal
        assertEquals(Token.TokenType.STRINGLITERAL, token.getType());
        assertEquals("", token.getValue());
        token = lexer.nexToken();

        //regular
        assertEquals(Token.TokenType.STRINGLITERAL, token.getType());
        assertEquals("Nice Test Here!", token.getValue());
        token = lexer.nexToken();

        //escaped
        assertEquals(Token.TokenType.STRINGLITERAL, token.getType());
        assertEquals("Sung \"DRIP\" WOO", token.getValue());
        token = lexer.nexToken();

        assertEquals(Token.TokenType.ENDOFLINE, token.getType());
        token = lexer.nexToken();

        //KEYWORDS TEST
        assertEquals(Token.TokenType.FOR, token.getType());
        token = lexer.nexToken();

        assertEquals(Token.TokenType.STEP, token.getType());
        token = lexer.nexToken();

        //LABEL TESTING

        assertEquals(Token.TokenType.LABEL, token.getType());
        assertEquals("Method", token.getValue());
        token = lexer.nexToken();

        assertEquals(Token.TokenType.WORD, token.getType());
        assertEquals("var", token.getValue());
        token = lexer.nexToken();

        assertEquals(Token.TokenType.ADD, token.getType());
        assertEquals("+", token.getValue());
        token = lexer.nexToken();

        assertEquals(Token.TokenType.NUMBER, token.getType());
        assertEquals("8", token.getValue());
        token = lexer.nexToken();

        assertEquals(Token.TokenType.ENDOFLINE, token.getType());

    }
}