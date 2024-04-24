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

    public BooleanExpressionNode(){
        this.leftOperand = null;
        this.operator = null;
        this.rightOperand = null;
    }

    public Node getLeftOperand(){
        return this.leftOperand;
    }

    public Node getRightOperand(){
        return this.rightOperand;
    }

    public BooleanOperator getOperator(){
        return this.operator;
    }

    @Override
    public String toString(){
        return leftOperand.toString() +" "+ this.operator.getOperator() +" "+ rightOperand.toString();
    }

}
