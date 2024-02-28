
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

import static org.junit.Assert.*;

public class TokenHandlerTest {

    private TokenHandler tokenHandler;
    private final Lexer lexer;


    //Initialize lexer with test file
    public TokenHandlerTest() throws IOException {
        lexer = new Lexer("test.txt");
    }


    //Initialize all token handler objects with lexed tokens
   @Before
    public void initialize() throws IOException{
       tokenHandler = new TokenHandler(lexer.lex());
   }


   @Test
    public void peek(){
       Optional<Token> expectedToken = Optional.of(new Token(Token.TokenType.WORD, "handler"));
       Optional<Token> resultToken = tokenHandler.peek(2);
       assertEquals(expectedToken,resultToken);
   }

   @Test
    public void matchAndRemove(){
        Optional<Token>expectedToken = Optional.of(new Token(Token.TokenType.NUMBER, "2"));
        Optional<Token> resultToken = tokenHandler.matchAndRemove(expectedToken.get().getType());
        assertEquals(expectedToken.get().getType(),resultToken.get().getType());
   }


    @Test
    public void moreTokens(){

       assertTrue(tokenHandler.moreTokens());


       assertFalse(tokenHandler.moreTokens());

    }



}


