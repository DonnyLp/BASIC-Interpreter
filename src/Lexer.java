import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

public class Lexer {

    private final LinkedList<Token> tokens;
    private final CodeHandler handler;
    private int lineNumber;
    private int characterPosition;
    private int tokenListIndex;
    private int characterOffset;

    private final HashMap <String, Token.TokenType> keyWords;
    private final HashMap <String, Token.TokenType> doubleCharSymbols;
    private final HashMap <String, Token.TokenType> singleCharSymbols;


    private enum state {
        WORD,
        NUMBER,
        IGNORE,
        STRINGLITERAL,
        SYMBOL,
        INVALID,
        ENDOFLINE,

    }


    public Lexer(String fileName) throws IOException {
        handler = new CodeHandler(fileName);
        tokens = new LinkedList<>();
        this.lineNumber = 1;
        this.characterPosition = 1;
        this.characterOffset = 0;
        this.tokenListIndex = 0;
        this.keyWords = new HashMap<>();
        this.singleCharSymbols = new HashMap<>();
        this.doubleCharSymbols = new HashMap<>();
        populateHashMaps();
    }

    public LinkedList<Token> lex() {

        while (!handler.isDone()) {

            char current = handler.peek(0);

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
                case SYMBOL -> tokens.add(processSymbols(handler));
                case ENDOFLINE -> {
                    tokens.add(new Token(Token.TokenType.ENDOFLINE, this.lineNumber, this.characterPosition));
                    this.lineNumber += 1;
                    this.characterPosition = 1;
                    this.characterOffset = handler.getIndex();
                    handler.swallow(1);
                }
                case INVALID -> throw new IllegalArgumentException(invalidCharMessage("Invalid character", handler.peek(1) ,this.lineNumber, this.characterPosition));
            }
        }
        //Add last ENDOFLINE token not handled in while loop
        tokens.add(new Token(Token.TokenType.ENDOFLINE, this.lineNumber, this.characterPosition));
        return tokens;
    }

    //Grabs the next token in the list of tokens
    public Token nexToken(){
        return this.tokens.get(tokenListIndex++);
    }

    //Processes characters forming a word and returns the corresponding token
    private Token processWord(CodeHandler handler) {


        Token token = null;
        StringBuilder tokenValue = new StringBuilder();

        //Loop through characters while it's either a letter, digit, or terminating symbol for string variables
        while (!handler.isDone() && isValidWordChar(handler.peek(0))){
            tokenValue.append(handler.peek(0));
            handler.swallow(1);
        }

        //Handle keywords and makes corresponding token
        if(keyWords.containsKey(tokenValue.toString())){
            token = new Token("",keyWords.get(tokenValue.toString()),this.lineNumber,this.characterPosition);
        }

        //Handle label case
        if (!handler.isDone() && handler.peek(0) == ':'){
            token = new Token(tokenValue.toString(), Token.TokenType.LABEL,this.lineNumber,this.characterPosition);
            handler.swallow(1);
        }
        //Handle case where there's a space in between the semicolon and consume the corresponding characters
        else if (!handler.isDone() && handler.peek(1) == ':'){
            token = new Token(tokenValue.toString(), Token.TokenType.LABEL,this.lineNumber,this.characterPosition);
            handler.swallow(2);
        }

        //Set token to regular word token, if not set in prior cases
        if(token == null) token = new Token(tokenValue.toString(), Token.TokenType.WORD, this.lineNumber, this.characterPosition);

        this.characterPosition = handler.getIndex() - characterOffset;
        return token;
    }

    //Processes characters forming a numbers and returns the corresponding token
    private Token processNumber(CodeHandler handler) {


        Token token;
        boolean isDecimal = false;
        StringBuilder tokenValue = new StringBuilder();

        //Loops through characters while they are digits or at decimal points
        while (!handler.isDone() && (Character.isDigit(handler.peek(0)) || handler.peek(0) == '.')) {


            //Handles the case where there's more than one decimal detected
            if (handler.peek(0) == '.' && isDecimal){
                throw new IllegalArgumentException(invalidCharMessage("Extra Decimal", handler.peek(0), this.lineNumber, handler.getIndex() - characterOffset));
            }

            if (handler.peek(0) == '.') isDecimal = true;

            tokenValue.append(handler.peek(0));
            handler.swallow(1);
        }

        //Handle invalid characters
        if(!handler.isDone() && (isValidNumberChar(handler.peek(0)) || !Character.isWhitespace(handler.peek(0)) && !isValidSymbol(handler.peek(0)))){
            throw new IllegalArgumentException(invalidCharMessage("Invalid character",handler.peek(0),this.lineNumber,handler.getIndex() - characterOffset));
        }

        //Create new token with accumulated string of characters, update the character position and swallow the characters of the recent token
        token = new Token(tokenValue.toString(), Token.TokenType.NUMBER, this.lineNumber, this.characterPosition);
        this.characterPosition = handler.getIndex() - characterOffset;
        return token;
    }

    //Processes characters forming a string literals and returns the corresponding token
    private Token processStringLiteral(CodeHandler handler){

        Token token;
        StringBuilder tokenValue = new StringBuilder();

        //Consume the beginning quotation
        handler.swallow(1);

        //Loop through string literal and create token
        while(!handler.isDone() && handler.peek(0) != '"'){
            //Handle escaped nested in string literal
            if(handler.peek(0) == '\\'){
                tokenValue.append(handler.peek(1));
                handler.swallow(2);
            }
            //accumulate characters
            tokenValue.append(handler.peek(0));
            handler.swallow(1);
        }

        //Consume ending quotation
        handler.swallow(1);

        //Append token to list and update character position
        token = new Token(tokenValue.toString(), Token.TokenType.STRINGLITERAL, this.lineNumber, this.characterPosition);
        this.characterPosition = handler.getIndex() - characterOffset;
        return token;
    }

    //Processes characters forming symbols and returns the corresponding token
    private Token processSymbols(CodeHandler handler){

        Token token = null;
        String singleCharSymbol = Character.toString(handler.peek(0));
        String doubleCharSymbol = "";

        //Handle edge case for string index out of bounds on double character peek ahead
        if((handler.getIndex() + 2)<= handler.getFileLength()){
            doubleCharSymbol = handler.peekString(2);
        }

        //Handle double and single character symbols
        if(this.doubleCharSymbols.containsKey(doubleCharSymbol)){
            token = new Token(doubleCharSymbol,doubleCharSymbols.get(doubleCharSymbol),this.lineNumber,this.characterPosition);
            handler.swallow(2);
        }
        else if(this.singleCharSymbols.containsKey(singleCharSymbol)){
            token = new Token(singleCharSymbol,singleCharSymbols.get(singleCharSymbol), this.lineNumber,this.characterPosition);
            handler.swallow(1);
        }
        this.characterPosition = handler.getIndex() - characterOffset;
        return token ;
    }

    //Matches character to specific state (WORD, NUMBER,etc.)
    private state matchState(char current) {

        state newState = state.INVALID;

        if (Character.isSpaceChar(current) || current == '\t' || current == '\r') newState = state.IGNORE;
        else if (Character.isAlphabetic(current)) newState = state.WORD;
        else if (Character.isDigit(current)) newState = state.NUMBER;
        else if (current == '"') newState = state.STRINGLITERAL;
        else if (current == '\n') newState = state.ENDOFLINE;
        else if (current == '$' || current == '%') newState = state.WORD;
        else if (this.singleCharSymbols.containsKey(Character.toString(current))) newState = state.SYMBOL;
        return newState;
    }

    //Adds corresponding elements to initialized hash maps (SYMBOLS & KEYWORDS)
    private void populateHashMaps(){
        //initialize keywords hash map
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
        this.keyWords.put("RANDOM", Token.TokenType.RANDOM);
        this.keyWords.put("LEFT$", Token.TokenType.LEFT$);
        this.keyWords.put("RIGHT$", Token.TokenType.RIGHT$);
        this.keyWords.put("MID$", Token.TokenType.MID$);
        this.keyWords.put("NUM$", Token.TokenType.NUM$);
        this.keyWords.put("VAL", Token.TokenType.VAL_INT);
        this.keyWords.put("VAL%", Token.TokenType.VAL_FLOAT);

        //initialize double character symbol hashmap
        this.doubleCharSymbols.put("<>", Token.TokenType.NOTEQUALS);
        this.doubleCharSymbols.put("<=",Token.TokenType.LESSTHANOREQUAL);
        this.doubleCharSymbols.put(">=",Token.TokenType.GREATERTHANOREQUAL);

        //initialize single character symbol hashmap
        this.singleCharSymbols.put("=", Token.TokenType.EQUALS);
        this.singleCharSymbols.put("<", Token.TokenType.LESSTHAN);
        this.singleCharSymbols.put(">", Token.TokenType.GREATERTHAN);
        this.singleCharSymbols.put("(", Token.TokenType.LPAREN);
        this.singleCharSymbols.put(")", Token.TokenType.RPAREN);
        this.singleCharSymbols.put("+", Token.TokenType.PLUS);
        this.singleCharSymbols.put("-", Token.TokenType.MINUS);
        this.singleCharSymbols.put("*", Token.TokenType.MULTIPLY);
        this.singleCharSymbols.put("/", Token.TokenType.DIVIDE);
        this.singleCharSymbols.put(",", Token.TokenType.COMMA);
    }

    //Helper to validate characters in processWord
    private boolean isValidWordChar(char c) { return Character.isAlphabetic(c) || Character.isDigit(c) || c == '$' || c == '%'; }
    //Helper to validate characters in processNumber
    private boolean isValidNumberChar(char c) { return Character.isDigit(c) || c == '.'; }
    //Helper to validate symbols
    private boolean isValidSymbol(char c) {return this.singleCharSymbols.containsKey(Character.toString(c));}

    //Creates custom error message for invalid characters
    private String invalidCharMessage(String customMessage,char invalidChar, int lineNumber, int characterPosition){
        return String.format("%s: '%c' at Line: %d, Character %d", customMessage, invalidChar, lineNumber, characterPosition);
    }


}