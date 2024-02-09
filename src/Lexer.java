import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

public class Lexer {

    private final LinkedList<Token> tokens = new LinkedList<>();
    private final CodeHandler handler;
    private int lineNumber;
    private int characterPosition;

    private int characterOffset;

    private final HashMap <String, Token.TokenType> keyWords;

    private enum state {
        WORD,
        NUMBER,
        IGNORE,
        STRINGLITERAL,
        INVALID,
        ENDOFLINE,

    }


    public Lexer(String fileName) throws IOException {
        handler = new CodeHandler(fileName);
        // TO DO: instantiate the line number and character position
        this.lineNumber = 1;
        this.characterPosition = 1;
        this.characterOffset = 0;
        this.keyWords = new HashMap<>();
        populateHashMap();
    }

    public LinkedList<Token> lex() {

        while (!handler.isDone()) {

            int currentIndex = handler.getIndex();
            char current = handler.peek(currentIndex);

            //State machine like implementation to control each character case
            switch (matchState(current)) {
                case IGNORE -> {
                    if(current == ' ') this.characterPosition++;
                    if(current == '\t') this.characterPosition += 4;
                    handler.swallow(1);
                }
                case WORD -> tokens.add(processWord(handler));
                case NUMBER -> tokens.add(processNumber(handler));
                case STRINGLITERAL -> tokens.add(processStringLiteral(handler));
                case ENDOFLINE -> {
                    tokens.add(new Token(Token.TokenType.ENDOFLINE, this.lineNumber, this.characterPosition));
                    this.lineNumber += 1;
                    this.characterPosition = 1;
                    this.characterOffset = handler.getIndex();
                    handler.swallow(1);
                }
                case INVALID -> throw new IllegalArgumentException(invalidCharMessage("Invalid character", handler.peek(currentIndex) ,this.lineNumber, this.characterPosition));
            }
        }
        //Add last ENDOFLINE token not handled in while loop
        tokens.add(new Token(Token.TokenType.ENDOFLINE, this.lineNumber, this.characterPosition));
        return tokens;
    }


    //Accepts letters,numbers, and string terminators for string variables
    private Token processWord(CodeHandler handler) {

        StringBuilder tokenValue = new StringBuilder();
        Token token = null;
        int currentIndex = handler.getIndex();

        //Loop through characters while it's either a letter, digit, or terminating symbol for string variables
        while (!handler.isDone() && (isValidWordChar(handler.peek(currentIndex)))){
            tokenValue.append(handler.peek(currentIndex));
            currentIndex++;
            handler.swallow(1);
        }

        if(keyWords.containsKey(tokenValue.toString())){
            token = new Token(keyWords.get(tokenValue.toString()),this.lineNumber,this.characterPosition);
        }
        if(token == null) token = new Token(tokenValue.toString(), Token.TokenType.WORD, this.lineNumber, this.characterPosition);

        this.characterPosition = currentIndex - characterOffset;
        return token;
    }

    //Accepts number & decimals
    private Token processNumber(CodeHandler handler) {

        StringBuilder tokenValue = new StringBuilder();
        Token token;
        boolean isDecimal = false;
        int currentIndex = handler.getIndex();

        //Loops through characters while they are digits or at decimal points
        while (!handler.isDone() && (Character.isDigit(handler.peek(currentIndex)) || handler.peek(currentIndex) == '.')) {


            //Handles the case where there's more than one decimal detected
            if (handler.peek(currentIndex) == '.' && isDecimal){
                throw new IllegalArgumentException(invalidCharMessage("Extra Decimal", handler.peek(currentIndex), this.lineNumber, currentIndex - characterOffset));
            }

            if (handler.peek(currentIndex) == '.') isDecimal = true;

            tokenValue.append(handler.peek(currentIndex));
            currentIndex++;
            handler.swallow(1);
        }

        //Handle invalid characters
        if(!handler.isDone() && (isValidNumberChar(handler.peek(currentIndex)) || !Character.isWhitespace(handler.peek(currentIndex)))){
            throw new IllegalArgumentException(invalidCharMessage("Invalid character",handler.peek(currentIndex),this.lineNumber,currentIndex - characterOffset));
        }

        //Create new token with accumulated string of characters, update the character position and swallow the characters of the recent token
        token = new Token(tokenValue.toString(), Token.TokenType.NUMBER, this.lineNumber, this.characterPosition);
        this.characterPosition = currentIndex - characterOffset;
        return token;
    }

    //Processes any string literals
    public Token processStringLiteral(CodeHandler handler){
        StringBuilder tokenValue = new StringBuilder();
        Token token;
        int currentIndex = handler.getIndex();

        //Add first set of quotations to token value
        tokenValue.append(handler.peek(currentIndex));
        currentIndex++;
        handler.swallow(1);

        //Loop through string literal and create token
        while(handler.peek(currentIndex) != '"'){
            if(handler.peek(currentIndex) == '\\'){
                tokenValue.append(handler.peek(currentIndex));
                tokenValue.append(handler.peek(currentIndex + 1));
                currentIndex+=2;
                handler.swallow(2);
            }
            tokenValue.append(handler.peek(currentIndex));
            currentIndex++;
            handler.swallow(1);
        }
        //Handle ending quotation
        tokenValue.append(handler.peek(currentIndex));
        currentIndex++;
        handler.swallow(1);

        //Append token to list and update character position
        token = new Token(tokenValue.toString(), Token.TokenType.STRINGLITERAL, this.lineNumber, this.characterPosition);
        this.characterPosition = currentIndex - characterOffset;
        return token;
    }

    //Helper method to match character to specific token state (WORD, NUMBER,etc.)
    private state matchState(char current) {

        state newState = state.INVALID;

        if (Character.isSpaceChar(current) || current == '\t' || current == '\r') newState = state.IGNORE;
        else if (Character.isAlphabetic(current)) newState = state.WORD;
        else if (Character.isDigit(current)) newState = state.NUMBER;
        else if (current == '"') newState = state.STRINGLITERAL;
        else if (current == '\n') newState = state.ENDOFLINE;

        return newState;
    }

    //Helper to populate hashmap
    public void populateHashMap(){
        this.keyWords.put("FOR", Token.TokenType.FOR);
        this.keyWords.put("DO", Token.TokenType.DO);
        this.keyWords.put("NEXT", Token.TokenType.NEXT);
        this.keyWords.put("IF", Token.TokenType.IF);
        this.keyWords.put("THEN", Token.TokenType.THEN);
        this.keyWords.put("PRINT", Token.TokenType.PRINT);
        this.keyWords.put("DATA", Token.TokenType.DATA);
        this.keyWords.put("INPUT", Token.TokenType.INPUT);
        this.keyWords.put("END", Token.TokenType.END);
        this.keyWords.put("GOSUB", Token.TokenType.GOSUB);
        this.keyWords.put("RETURN", Token.TokenType.RETURN);
        this.keyWords.put("WHILE", Token.TokenType.WHILE);
        this.keyWords.put("FUNCTION", Token.TokenType.FUNCTION);
        this.keyWords.put("TO", Token.TokenType.TO);
        this.keyWords.put("READ", Token.TokenType.READ);
        this.keyWords.put("STEP", Token.TokenType.STEP);

    }

    //Helper for processWord tokenizing
    private boolean isValidWordChar(char c){
        return Character.isAlphabetic(c) || Character.isDigit(c) || c == '$' || c == '%';
    }

    private boolean isValidNumberChar(char c){
        return Character.isDigit(c) || c == '.';
    }

    //Helper method to create custom error message with specifications
    private String invalidCharMessage(String customMessage,char invalidChar, int lineNumber, int characterPosition){
        return String.format("%s: '%c' at Line: %d, Character %d", customMessage, invalidChar, lineNumber, characterPosition);
    }
}