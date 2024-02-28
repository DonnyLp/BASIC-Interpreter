public class FloatNode extends Node{

    private final float floatNumber;

    public FloatNode(float number){
        this.floatNumber = number;
    }

    @Override
    public String toString() {
        return Float.toString(floatNumber);
    }
}
