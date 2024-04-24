import java.util.LinkedList;

public class ForNode extends StatementNode {

    private VariableNode variable;
    private Node startIndex;
    private Node endIndex;

    private int incrementValue;

    private boolean isStepPresent;

    public ForNode(VariableNode variable, Node startIndex, Node endIndex, int incrementValue, boolean isStepPresent){
        this.variable = variable;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.incrementValue = incrementValue;
        this.isStepPresent = isStepPresent;
    }
    public ForNode(){
        this.variable = null;
        this.startIndex = null;
        this.endIndex = null;
        this.incrementValue = 0;
        this.isStepPresent = false;
    }

    public VariableNode getVariable() {
        return variable;
    }

    public int getIncrementValue(){
        return incrementValue;
    }

    public Node getStartIndex(){
        return this.startIndex;
    }

    public Node getEndIndex(){
        return this.endIndex;
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

        return forLoopVisualizer.toString();
    }
}
