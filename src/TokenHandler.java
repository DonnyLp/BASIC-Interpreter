import java.util.LinkedList;
import java.util.Optional;

public class TokenHandler {

    //Member Variables
    LinkedList<Token> tokens = new LinkedList<>();

    //Method declerations
    public TokenHandler(LinkedList<Token> tokens){
        this.tokens = tokens;
    }


    //Gets token "i" characters ahead in list
    Optional<Token> peek(int i){
        if(i < 0 || i >= tokens.size()){
            return Optional.empty();
        }
        return Optional.of(tokens.get(i));
    }

    //Check if there's more tokens
    public boolean moreTokens(){
        return tokens.isEmpty();
    }

    Optional<Token> matchAndRemove(Token.TokenType type){

        //Grab the head of the list
        Token head = tokens.getFirst();

        //Check if passed in token is same type as head of list
        if(type != head.getType()){
            return Optional.empty();
        }

        //Returns and removes the head of the list
        return Optional.of(tokens.remove());

    }
}
