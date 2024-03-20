import java.util.LinkedList;
public class ReadNode extends StatementNode implements Addable{

    private final LinkedList<Node> readList;

    public ReadNode(){
        this.readList = new LinkedList<>();
    }


    @Override
    public void add(Node node){
        readList.add(node);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("READ");

        for (Node node : readList){
            builder.append(" " + node.toString() + " ");
        }
        return builder.toString();
    }
}
