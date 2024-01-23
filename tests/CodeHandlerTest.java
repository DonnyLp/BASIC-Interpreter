

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class CodeHandlerTest {
    private CodeHandler codeHandler;

    @Before
    public void initialize() throws IOException {
        codeHandler = new CodeHandler("test.txt");
    }

    @Test
    public void peekTest() {
        char result = codeHandler.peek(11);
        assertEquals('b', result);
    }

    @Test
    public void peekStringTest() {
        String result = codeHandler.peekString(8);
        assertEquals("Learning",result);
    }

    @Test
    public void swallowTest() {
        codeHandler.swallow(12);
        assertEquals(12,codeHandler.getIndex());
    }


    @Test
    public void remainderTest() {
        codeHandler.swallow(12);
        String result = codeHandler.remainder();
        assertEquals(" development, compiler, data, and theoritical math.", result);
    }

    @Test
    public void isDoneTest() {
        assertFalse(codeHandler.isDone());
        codeHandler.swallow(codeHandler.getFileLength() - codeHandler.getIndex());
        assertTrue(codeHandler.isDone());
    }
}