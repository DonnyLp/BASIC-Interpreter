public class FloatNode extends Node{

    private final float floatNumber;

    public FloatNode(float number){
        this.floatNumber = number;
    }

    @Override
    protected String getValue() {
        return Float.toString(floatNumber);
    }
}
