import java.util.LinkedList;
public class ReadNode extends StatementNode implements Addable{

    private final LinkedList<Node> readList;

    public ReadNode(){
        this.readList = new LinkedList<>();
    }

    @Override
    public LinkedList<Node> getList(){
        return this.readList;
    }

    @Override
    public void add(Node node){
        readList.add(node);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("READ").append(" ");

        int index = 0;
        while (index < readList.size()){

            builder.append(readList.get(index).toString());

            if(index < readList.size() - 1) builder.append(",");
            index++;
        }
        return builder.toString();
    }
}
