import java.util.LinkedList;

public class WhileNode extends StatementNode {

    private BooleanExpressionNode condition;
    private String label;

    public WhileNode(BooleanExpressionNode condition, String label){
        this.condition = condition;
        this.label = label;
    }

    public WhileNode(){
        this.condition = null;
        this.label = "";
    }

    public BooleanExpressionNode getCondition(){
        return this.condition;
    }

    public String getLabel(){
        return this.label;
    }

    @Override
    public String toString(){

        StringBuilder whileLoopVisualizer = new StringBuilder();

        whileLoopVisualizer.append("WHILE ").append(condition.toString()).append(" ").append(this.label);

        return whileLoopVisualizer.toString();
    }

}
