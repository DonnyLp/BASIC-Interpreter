import java.util.LinkedList;

public class DataNode extends StatementNode implements Addable{

    private final LinkedList<Node> dataList;


    public DataNode(){
        this.dataList = new LinkedList<>();
    }

    public void add(Node node){
        dataList.add(node);
    }

    public LinkedList<Node> getDataList(){
        return dataList;
    }

    @Override
    public LinkedList<Node> getList(){
        return this.dataList;
    }

    @Override
    public String toString(){
        StringBuilder dataBuilder = new StringBuilder();

        dataBuilder.append("DATA").append(" ");

        int index = 0;
        while (index < dataList.size()){

            dataBuilder.append(dataList.get(index).toString());

            if(index < dataList.size() - 1) dataBuilder.append(",");
            index++;
        }
        return dataBuilder.toString();
    }
}
