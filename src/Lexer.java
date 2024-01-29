import java.io.IOException;
import java.util.LinkedList;

public class Lexer {

    private LinkedList<Token> tokens = new LinkedList<>();
    private CodeHandler handler;
    private Token token;

    private int lineNumber;

    private int characterPosition;

    private int characterOffset;

    private enum state {
        WORD,
        NUMBER,
        IGNORE,
        ENDOFLINE,

    }

    public Lexer(String fileName) throws IOException {
        handler = new CodeHandler(fileName);
        // TO DO: instantiate the linenumber and character position
        this.lineNumber = 1;
        this.characterPosition = 0;
        this.characterOffset = 0;
    }

    // TODO : Finish the remaining requirements for this method
    public LinkedList<Token> lex() {

        // TO DO: Loop While there's still data in the code handler

        /*

            U:Use state machine w/helper method to identify the state
            - We iterate through the file
            - make tokens by the cases defined above
            M:
                - While loop to iterate through the file
                - switch case to account for all cases above
                - handle logic for each case
                - create new token to the LinkedList of tokens
                - move the index with swallow()?
         */
        int i = 0;
        while (!handler.isDone()) {

            i = handler.getIndex();
            char current = handler.peek(i);

            switch (matchState(current)) {
                case IGNORE -> handler.swallow(1);
                case WORD -> tokens.add(processWord(handler));
                case NUMBER -> tokens.add(processNumber(handler));
                case ENDOFLINE -> {
                    tokens.add(new Token(Token.TokenType.ENDOFLINE, this.lineNumber, this.characterPosition));
                    this.lineNumber += 1;
                    this.characterPosition = 0;
                    this.characterOffset = handler.getIndex();
                    handler.swallow(1);
                }
                default -> System.out.println("Unexpected character intercepted");

            }
        }
        return tokens;
    }


    private Token processWord(CodeHandler handler) {

        StringBuilder tokenValue = new StringBuilder();
        Token token = new Token("", Token.TokenType.WORD, this.lineNumber, this.characterPosition);
        int i = handler.getIndex();

        //collect characters that are acceptable for the "WORD" token
        while (!handler.isDone() && (Character.isAlphabetic(handler.peek(i)) || handler.peek(i) == '$' || handler.peek(i) == '%')) {
            tokenValue.append(handler.peek(i));
            handler.swallow(1);
            i++;
        }

        token = new Token(tokenValue.toString(), Token.TokenType.WORD, this.lineNumber, this.characterPosition);
        this.characterPosition = i - characterOffset;
        handler.swallow(i - handler.getIndex());
        return token;
    }
    // TO DO : Create the ProcessNumber method that returns a token and accepts 0 - 9 and one decimal
    /*
    U: process the characters and form a token with numbers and/or decimal number
    M:
    - Loop while: char is number or char is decimal
        - if current char is decimal point and isDecimal is true
        print "Syntax error: too many decimals"
        - token.append(handler.peek(i));
    - return new Token (token, Number, lineNumber, characterPosition)
     */

    // TO DO: Fix logic for single decimal and rest of the method
    private Token processNumber(CodeHandler handler) {
        StringBuilder tokenValue = new StringBuilder();
        Token token = new Token("", Token.TokenType.NUMBER, this.lineNumber, this.characterPosition);
        boolean isDecimal = false;
        int i = handler.getIndex();
        while (Character.isDigit(handler.peek(i)) || handler.peek(i) == '.') {
            //TO DO: Fix logic for single decimal
            //handle logic for single decimal
            if (handler.peek(i) == '.') isDecimal = true;
            if (handler.peek(i) == '.' && isDecimal) {
                System.out.println("Syntax error: too many decimals");
                break;
            }
                tokenValue.append(i);
            }
            return new Token("", Token.TokenType.NUMBER, this.lineNumber, this.characterPosition);
        }

    //helper method to match input char to specific state
    private state matchState(char current) {

        state newState = null;

        if (Character.isSpaceChar(current) || current == '\t' || current == '\r') newState = state.IGNORE;
        else if (Character.isAlphabetic(current)) newState = state.WORD;
        else if (Character.isDigit(current)) newState = state.NUMBER;
        else if (current == '\n') newState = state.ENDOFLINE;

        return newState;
    }
}
