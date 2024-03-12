import java.util.List;

public class InputNode extends StatementNode {

    private List<Node> inputList;

    public InputNode(List<Node> inputList){
        this.inputList = inputList;
    }

    @Override
    public String toString() {
        return inputList.toString();
    }

}