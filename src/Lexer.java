import java.io.IOException;
import java.util.LinkedList;

public class Lexer {

    private final LinkedList<Token> tokens = new LinkedList<>();
    private final CodeHandler handler;
    private int lineNumber;
    private int characterPosition;

    private int characterOffset;

    private enum state {
        WORD,
        NUMBER,
        IGNORE,
        INVALID,
        ENDOFLINE,

    }

    public Lexer(String fileName) throws IOException {
        handler = new CodeHandler(fileName);
        // TO DO: instantiate the line number and character position
        this.lineNumber = 1;
        this.characterPosition = 1;
        this.characterOffset = 0;
    }

    public LinkedList<Token> lex() {

        while (!handler.isDone()) {

            int currentIndex = handler.getIndex();
            char current = handler.peek(currentIndex);

            //State machine like implementation to control each character case
            switch (matchState(current)) {
                case IGNORE -> {
                    if(current == ' '){
                        this.characterPosition++;
                    }
                    if(current == '\t'){
                        this.characterPosition += 4;
                    }

                    handler.swallow(1);
                }
                case WORD -> tokens.add(processWord(handler));
                case NUMBER -> tokens.add(processNumber(handler));
                case ENDOFLINE -> {
                    tokens.add(new Token(Token.TokenType.ENDOFLINE, this.lineNumber, this.characterPosition));
                    this.lineNumber += 1;
                    this.characterPosition = 1;
                    this.characterOffset = handler.getIndex();
                    handler.swallow(1);
                }
                case INVALID -> throw new IllegalArgumentException(invalidCharMessage("Invalid character", this.lineNumber, this.characterPosition));
            }
        }
        return tokens;
    }


    //Accepts letters,numbers, and string terminators for string variables
    private Token processWord(CodeHandler handler) {

        StringBuilder tokenValue = new StringBuilder();
        Token token;
        int currentIndex = handler.getIndex();

        //Loop through characters while it's either a letter, digit, or terminating symbol for string variables
        while (!handler.isDone() && (isValidWordChar(handler.peek(currentIndex)))){
            tokenValue.append(handler.peek(currentIndex));
            currentIndex++;
            handler.swallow(1);
        }

        token = new Token(tokenValue.toString(), Token.TokenType.WORD, this.lineNumber, this.characterPosition);
        this.characterPosition = currentIndex - characterOffset;
        handler.swallow(currentIndex - handler.getIndex());
        return token;
    }

    //Accepts number & decimals
    private Token processNumber(CodeHandler handler) {

        StringBuilder tokenValue = new StringBuilder();
        Token token;
        boolean isDecimal = false;
        int currentIndex = handler.getIndex();

        //Loops through characters while they are digits or at decimal points
        while (!handler.isDone() && (Character.isDigit(handler.peek(currentIndex)) || handler.peek(currentIndex) == '.' || handler.peek(currentIndex) == ' ')) {

            //Handle invalid character exception
            if(isValidNumberChar(handler.peek(currentIndex))){
                throw new IllegalArgumentException(invalidCharMessage("Invalid character", this.lineNumber,this.characterPosition));
            }

            //Handles the case where there's more than one decimal detected
            if (handler.peek(currentIndex) == '.' && isDecimal){
                throw new IllegalArgumentException(invalidCharMessage("Extra Decimal", this.lineNumber, this.characterPosition));
            }

            if (handler.peek(currentIndex) == '.') isDecimal = true;

            tokenValue.append(handler.peek(currentIndex));
            currentIndex++;
            handler.swallow(1);
        }
        //Create new token with accumulated string of characters, update the character position and swallow the characters of the recent token
        token = new Token(tokenValue.toString(), Token.TokenType.NUMBER, this.lineNumber, this.characterPosition);
        this.characterPosition = currentIndex - characterOffset;
        handler.swallow(currentIndex - handler.getIndex());
        return token;
    }

    //Helper method to match character to specific token state (WORD, NUMBER,etc.)
    private state matchState(char current) {

        state newState = state.INVALID;

        if (Character.isSpaceChar(current) || current == '\t' || current == '\r') newState = state.IGNORE;
        else if (Character.isAlphabetic(current)) newState = state.WORD;
        else if (Character.isDigit(current)) newState = state.NUMBER;
        else if (current == '\n') newState = state.ENDOFLINE;

        return newState;
    }

    //Helper for processWord tokenizing
    private boolean isValidWordChar(char c){
        return Character.isAlphabetic(c) || Character.isDigit(c) || c == '$' || c == '%';
    }

    private boolean isValidNumberChar(char c){
        return Character.isDigit(c) || c == '.';
    }

    //Helper method to create custom error message with specifications
    private String invalidCharMessage(String customMessage, int lineNumber, int characterPosition){
        return String.format("%s at Line: %d, Character %d", customMessage, lineNumber, characterPosition);
    }
}