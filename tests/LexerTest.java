import org.junit.Test;

import java.io.IOException;
import java.util.LinkedList;

import static org.junit.Assert.*;

public class LexerTest {

    @Test
    public void lexTest() throws IOException {
        Lexer lexer = new Lexer("test.txt");
        LinkedList<Token> tokens;
        tokens = lexer.lex();

        assertEquals(9, tokens.size());

        //first token
        Token first = tokens.getFirst();
        assertEquals(Token.TokenType.WORD, first.getType());
        assertEquals("is",first.getTokenValue());

        //second token
        Token second = tokens.get(1);
        assertEquals(Token.TokenType.NUMBER, second.getType());
        assertEquals("123",second.getTokenValue());

        //third token
        Token third = tokens.get(2);
        assertEquals(Token.TokenType.WORD, third.getType());
        assertEquals("and",third.getTokenValue());

        //fourth token
        Token fourth = tokens.get(3);
        assertEquals(Token.TokenType.NUMBER, fourth.getType());
        assertEquals("1.23",fourth.getTokenValue());

        //fifth token
        Token fifth = tokens.get(4);
        assertEquals(Token.TokenType.WORD, fifth.getType());
        assertEquals("numbers",fifth.getTokenValue());

        //fifth token
        Token sixth = tokens.get(5);
        assertEquals(Token.TokenType.ENDOFLINE, sixth.getType());

        //seventh token
        Token seventh = tokens.get(6);
        assertEquals(Token.TokenType.WORD, seventh.getType());
        assertEquals("invalid", seventh.getTokenValue());

        //eighth token
        Token eighth = tokens.get(7);
        assertEquals(Token.TokenType.NUMBER, eighth.getType());
        assertEquals("1.2",eighth.getTokenValue());

       //ninth token
       Token ninth = tokens.get(8);
       assertEquals(Token.TokenType.ENDOFLINE, ninth.getType());





    }


}