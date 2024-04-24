public class IfNode extends StatementNode {

    private final BooleanExpressionNode condition;
    private final String label;


    public IfNode(BooleanExpressionNode condition, String label){
        this.condition = condition;
        this.label = label;
    }

    public IfNode(){
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
        return "IF " + condition.toString() + " THEN " + label;
    }
}
