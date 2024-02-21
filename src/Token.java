public class Token {

    private String value;
    private final TokenType type;
    private int lineNumber;
    private int characterPosition;

    public enum TokenType{
        WORD, NUMBER, IF,DO, STRINGLITERAL, NOTEQUALS, LPAREN, RPAREN,
        EQUALS, LESSTHAN, GREATERTHAN, LESSTHANOREQUAL, GREATERTHANOREQUAL,
        ADD, SUBTRACT, MULTIPLY, DIVIDE,
        LABEL, PRINT, READ, INPUT, DATA, GOSUB,FOR, TO, STEP, NEXT, RETURN, THEN, FUNCTION, WHILE, END,
        COMMA, ENDOFLINE
    }

    public Token(TokenType type, String value){
        this.type = type;
        this.value = value;
    }

    public Token(String value, TokenType type, int lineNumber, int characterPosition){
        this.value = value;
        this.type = type;
        this.lineNumber = lineNumber;
        this.characterPosition = characterPosition;
    }

    public Token(TokenType type, int lineNumber, int characterPosition){
        this.type = type;
        this.lineNumber = lineNumber;
        this.characterPosition = characterPosition;

    }

    public TokenType getType(){ return this.type; }

    public String getValue(){ return this.value; }

    @Override
    public boolean equals(Object obj){

        Token token = (Token) obj;

        if(obj == null || getClass() != obj.getClass()){
            return false;
        }

        return type == token.getType() && value.equals(((Token) obj).getValue());
    }

    @Override
    public String toString() {
        return String.format("%s |%s| - (%d:%d)", type, value, lineNumber, characterPosition);
    }
}
