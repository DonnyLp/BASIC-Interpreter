

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
        assertEquals('1', result);
    }

    @Test
    public void peekStringTest() {
        String result = codeHandler.peekString(8);
        assertEquals("is 123 a",result);
    }

    @Test
    public void swallowTest() {
        codeHandler.swallow(12);
        assertEquals(12,codeHandler.getIndex());
    }


    @Test
    public void remainderTest() {
        codeHandler.swallow(25);
        String result = codeHandler.remainder();
        assertEquals("invalid 1.2", result);
    }

    @Test
    public void isDoneTest() {
        assertFalse(codeHandler.isDone());
        codeHandler.swallow(codeHandler.getFileLength() - codeHandler.getIndex());
        assertTrue(codeHandler.isDone());
    }
}