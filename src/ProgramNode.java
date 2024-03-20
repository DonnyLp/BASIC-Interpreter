import java.util.LinkedList;
import java.util.List;

public class ProgramNode extends Node implements Addable{

    private final LinkedList<Node> nodes;

    public ProgramNode(){
        this.nodes = new LinkedList<>();
    }

    public void add(Node node){
        nodes.add(node);
    }
    @Override
    public String toString() {
        StringBuilder astTree = new StringBuilder();
        for (Node node : nodes) {
            astTree.append(node.toString());
        }
        return astTree.toString();
    }


}
