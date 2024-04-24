public class FloatNode extends Node{

    private final float floatNumber;

    public FloatNode(float number){
        this.floatNumber = number;
    }

    public float getFloatValue(){
        return this.floatNumber;
    }

    @Override
    public String toString() {
        return Float.toString(floatNumber);
    }
}
