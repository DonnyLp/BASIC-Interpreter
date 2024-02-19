import java.util.LinkedList;

public class Parser {

    //Member variables
    private TokenHandler handler;


    //Methods
    public Parser(LinkedList<Token> tokens){
        handler = new TokenHandler(tokens);
    }


    //TO DO: Create Parse Method


    //TO DO: EndOfLine eater helper method
    public boolean handleSeperators(){
        return true;
    }

    //TO DO: Create Express, Term, and Factor Methods
}
