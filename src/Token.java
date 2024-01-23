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

    public Token(String tokenValue){
        this.tokenValue = tokenValue;
    }

    public Token(TokenType token, int lineNumber, int characterPos){
        this.token = token;
        this.lineNumber = lineNumber;
        this.characterPos = characterPos;

    }   


    public String toString(){
        return this.token + " : " + this.tokenValue 
        + " ( " + "Line: " + this.lineNumber + ", " + "Position: " + this.characterPos + " ) ";
    }
}
