import java.util.LinkedList;
import java.util.List;

public class PrintNode extends StatementNode implements Addable{
    public final LinkedList<Node> printList;

    public PrintNode(){
        this.printList = new LinkedList<>();
    }

    public void add(Node node){
        printList.add(node);
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();

        builder.append("PRINT");

        for (Node node : printList){
            builder.append(" ").append(node.toString()).append(" ");
        }
        return builder.toString();
    }
}

