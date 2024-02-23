public class MathOpNode extends Node {

    //Member variables
    private Node left;
    private Node right;
    private MathOperation operation;

    public MathOpNode(Node left, MathOperation operation, Node right){
        this.left = left;
        this.operation = operation;
        this.right = right;
    }

    public enum MathOperation{
        ADD('+'),
        SUBTRACT('-'),
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

    @Override
    protected String getValue() {
        return this.left + " " + this.operation.getSymbol() + " " + this.right;

    }
}
