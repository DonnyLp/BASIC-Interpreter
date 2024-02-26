import java.util.List;

public class ProgramNode extends Node{

    private List<Node> nodes;

    public ProgramNode(List<Node> nodes){
        this.nodes = nodes;
    }

    @Override
    public String toString() {
        StringBuilder astTree = new StringBuilder();
        astTree.append("ProgramNode: \n");
        for (Node node : nodes) {
            astTree.append(node.toString());
            astTree.append("\n");
        }
        return astTree.toString();
    }
}
