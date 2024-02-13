public class Token {
    
    private String value;
    private final TokenType token;
    private int lineNumber;
    private int characterPosition;

    public enum TokenType{
        WORD, NUMBER, IF,DO, STRINGLITERAL, NOTEQUALS, LPAREN, RPAREN,
        EQUALS, LESSTHAN, GREATERTHAN, LESSTHANOREQUAL, GREATERTHANOREQUAL,
        ADD, SUBTRACT, MULTIPLY, DIVIDE,
        LABEL, PRINT, READ, INPUT, DATA, GOSUB,FOR, TO, STEP, NEXT, RETURN, THEN, FUNCTION, WHILE, END,
        COMMA, ENDOFLINE
    }

    public Token(TokenType token, String value){
        this.token = token;
        this.value = value;
    }

    public Token(String value, TokenType token, int lineNumber, int characterPosition){
        this.value = value;
        this.token = token;
        this.lineNumber = lineNumber;
        this.characterPosition = characterPosition;
    }

    public Token(TokenType token, int lineNumber, int characterPosition){
        this.token = token;
        this.lineNumber = lineNumber;
        this.characterPosition = characterPosition;

    }

    public TokenType getType(){ return this.token; }

    public String getValue(){ return this.value; }


    @Override
    public String toString() {
        return String.format("%s <%s> - (%d:%d)", token, value, lineNumber, characterPosition);
    }
}
