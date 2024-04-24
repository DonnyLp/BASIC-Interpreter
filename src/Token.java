public class Token {

    private String value;
    private TokenType type;
    private int lineNumber;
    private int characterPosition;

    public enum TokenType{
        WORD, NUMBER, STRINGLITERAL,
        EQUALS, LESSTHAN, GREATERTHAN, LESSTHANOREQUAL, GREATERTHANOREQUAL,
        NOTEQUALS, LPAREN, RPAREN,COMMA,
        PLUS, MINUS, MULTIPLY, DIVIDE,
        LABEL, PRINT, READ, INPUT, DATA, GOSUB,FOR, TO, STEP, NEXT, RETURN,
        THEN, FUNCTION, WHILE, END, IF,DO,
        RANDOM,LEFT$,RIGHT$,MID$,NUM$,VAL_INT,VAL_FLOAT,
        ENDOFLINE
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
    public Token(TokenType type){
        this.type = type;
    }

    public Token(){
        this.type = null;
        this.value = null;
        this.lineNumber = 0;
        this.characterPosition = 0;
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
