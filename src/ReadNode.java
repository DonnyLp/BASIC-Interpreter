import java.util.List;
public class ReadNode extends StatementNode {

    private final List<VariableNode> readList;

    public ReadNode(List<VariableNode> readList){
        this.readList = readList;
    }

    @Override
    public String toString() {
        return readList.toString();
    }
}
