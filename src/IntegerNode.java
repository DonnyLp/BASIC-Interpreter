public class IntegerNode extends Node{


    private final int number;

    public IntegerNode(int number){
        this.number = number;
    }

    @Override
    protected String getType() {
        return "IntegerNode";
    }

    @Override
    protected String getValue() {
        return Integer.toString(number);
    }
}
