import java.util.LinkedList;
import java.util.List;

public class DataNode extends StatementNode implements Addable{

    private final LinkedList<Node> dataList;


    public DataNode(){
        this.dataList = new LinkedList<>();
    }

    public void add(Node node){
        dataList.add(node);
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();

        builder.append("DATA");

        for (Node node : dataList){
            builder.append(" " + node.toString() + " ");
        }
        return builder.toString();
    }

}
