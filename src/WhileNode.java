public class WhileNode extends StatementNode {

    private final BooleanExpressionNode  condition;
    private final String label;

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

        return "WHILE " + condition.toString() + " " + this.label;
    }

}
