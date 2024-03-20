import java.util.LinkedList;

public class InputNode extends StatementNode implements Addable {

    private final LinkedList<Node> inputList;

    public InputNode(){
        this.inputList = new LinkedList<>();
    }

    @Override
    public void add(Node node){
        inputList.add(node);
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("INPUT");

        for (Node node : inputList){
            builder.append(" " + node.toString() + " ");
        }
        return builder.toString();

    }

}