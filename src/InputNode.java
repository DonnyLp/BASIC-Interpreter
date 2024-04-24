import java.util.LinkedList;

public class InputNode extends StatementNode implements Addable {

    private final LinkedList<Node> inputList;

    public InputNode(){
        this.inputList = new LinkedList<>();
    }

    public LinkedList<Node> getList(){
        return this.inputList;
    }

    @Override
    public void add(Node node){
        inputList.add(node);
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();

        builder.append("INPUT").append(" ");

        int index = 0;
        while (index < inputList.size()){

            builder.append(inputList.get(index).toString());

            if(index < inputList.size() - 1) builder.append(",");
            index++;
        }
        return builder.toString();

    }

}