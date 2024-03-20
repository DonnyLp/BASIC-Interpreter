public class StringNode extends StatementNode {

    private final String value;

    public StringNode (String value){
        this.value = value;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\"");
        builder.append(this.value);
        builder.append("\"");
        return builder.toString();
    }
}
