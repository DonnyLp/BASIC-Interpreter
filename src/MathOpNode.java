public class MathOpNode extends Node {

    //Member variables
    private final Node left;
    private final Node right;
    private final MathOperation operation;

    public MathOpNode(Node left, Token.TokenType operator, Node right){
        this.left = left;
        this.operation = MathOperation.valueOf(operator.name());
        this.right = right;
    }

    public enum MathOperation{
        PLUS('+'),
        MINUS('-'),
        MULTIPLY('*'),
        DIVIDE('/');

        private final char symbol;

        MathOperation(char symbol){
            this.symbol = symbol;
        }
        public char getSymbol(){
            return this.symbol;
        }
    }

    public Node getLeft(){
        return this.left;
    }

    public MathOperation getOperation(){
        return this.operation;
    }

    public Node getRight(){
        return this.right;
    }

    @Override
    public String toString() {
        return "(" +this.left + " " + this.operation.getSymbol() + " " +  this.right + ")";

    }
}
