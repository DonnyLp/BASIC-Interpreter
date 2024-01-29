public class Token {
    
    private String tokenValue;
    private TokenType token;
    private int lineNumber;
    private int characterPos;

    public enum TokenType{
        WORD,
        NUMBER,
        ENDOFLINE
    }


    public Token(String tokenValue, TokenType token, int lineNumber, int characterPos){
        this.tokenValue = tokenValue;
        this.token = token;
        this.lineNumber = lineNumber;
        this.characterPos = characterPos;
    }

    public Token(TokenType token, int lineNumber, int characterPos){
        this.token = token;
        this.lineNumber = lineNumber;
        this.characterPos = characterPos;

    }

    public TokenType getType(){ return this.token; }

    public String getTokenValue(){ return this.tokenValue; }


    @Override
    public String toString(){
        return this.token + " : " + this.tokenValue + " ( " + "Line: " + this.lineNumber + " , " + "Character: " + this.characterPos + " ) ";
    }
}
