import java.util.LinkedList;

public class WhileNode extends StatementNode implements Addable{

    private BooleanExpressionNode condition;
    private String label;
    private final LinkedList<Node> body;

    public WhileNode(BooleanExpressionNode condition, String label){
        this.condition = condition;
        this.label = label;
        this.body = new LinkedList<>();
    }

    public WhileNode(){
        this.body = new LinkedList<>();
    }

    @Override
    public void add(Node node) {
        body.add(node);
    }

    @Override
    public String toString(){

        StringBuilder whileLoopVisualizer = new StringBuilder();

        whileLoopVisualizer.append("WHILE ").append(condition.toString()).append(" ").append(this.label);

        for (Node node : body){
            whileLoopVisualizer.append("\n").append(node.toString());
        }

        whileLoopVisualizer.append("\n").append(this.label).append(":");

        return whileLoopVisualizer.toString();
    }


}
