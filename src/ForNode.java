import java.util.LinkedList;

public class ForNode extends StatementNode implements Addable {

    private VariableNode variable;
    private Node startIndex;
    private Node endIndex;

    private int incrementValue;

    private final LinkedList<Node> body;

    private boolean isStepPresent;

    public ForNode(VariableNode variable, Node startIndex, Node endIndex, int incrementValue, boolean isStepPresent){
        this.variable = variable;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.incrementValue = incrementValue;
        this.body = new LinkedList<>();
        this.isStepPresent = isStepPresent;
    }

    public ForNode(){
        this.body = new LinkedList<>();
    }


    @Override
    public void add(Node node){
        body.add(node);
    }

    @Override
    public String toString(){
        StringBuilder forLoopVisualizer = new StringBuilder();
        forLoopVisualizer.append("FOR ")
                .append(this.variable.toString())
                .append(" = ")
                .append(this.startIndex)
                .append(" TO ")
                .append(this.endIndex);

        if(isStepPresent){
            forLoopVisualizer.append(" STEP ").append(this.incrementValue);
        }

        for (Node node : body){
            forLoopVisualizer.append("\n").append(node.toString());
        }
        forLoopVisualizer.append("\n").append("NEXT ").append(this.variable);
        return forLoopVisualizer.toString();
    }
}
