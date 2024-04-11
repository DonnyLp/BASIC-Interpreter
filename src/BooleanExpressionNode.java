import org.junit.Test;

public class BooleanExpressionNode extends StatementNode {
    private Node leftOperand;
    private BooleanOperator operator;
    private Node rightOperand;


    public enum BooleanOperator{
        GREATERTHAN(">"),
        LESSTHAN("<"),
        LESSTHANOREQUAL("<="),
        GREATERTHANOREQUAL(">="),
        NOTEQUALS("<>"),
        EQUALS("=");

        private final String operator;

        BooleanOperator(String operator){
            this.operator = operator;
        }
        public String getOperator(){
            return this.operator;
        }
    }
    
    public BooleanExpressionNode(Node leftOperand, Token.TokenType operator, Node rightOperand){
        this.leftOperand = leftOperand;
        this.operator = BooleanOperator.valueOf(operator.name());
        this.rightOperand = rightOperand;
    }
    public BooleanExpressionNode(){}

    @Override
    public String toString(){
        return leftOperand.toString() + this.operator.getOperator() + rightOperand.toString();
    }

}
