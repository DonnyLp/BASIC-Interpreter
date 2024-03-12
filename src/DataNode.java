import java.util.List;

public class DataNode extends StatementNode{

    private final List<Node> dataList;


    public DataNode(List<Node> dataList){
        this.dataList = dataList;
    }

    @Override
    public String toString(){
        return dataList.toString();
    }

}
