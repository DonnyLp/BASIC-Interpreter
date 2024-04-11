public class IfNode extends StatementNode {

    private BooleanExpressionNode condition;
    private String label;


    public IfNode(BooleanExpressionNode condition, String label){
        this.condition = condition;
        this.label = label;
    }

    public IfNode(){}

    @Override
    public String toString(){
        return "IF " + condition.toString() + " THEN " + label;
    }
}
