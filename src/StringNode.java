public class StringNode extends StatementNode {

    private final String value;

    public StringNode (String value){
        this.value = value;
    }

    @Override
    public String toString() {
        return "[" + this.value + "]";
    }
}
