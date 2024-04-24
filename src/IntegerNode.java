public class IntegerNode extends Node{


    private final int number;

    public IntegerNode(int number){
        this.number = number;
    }

    public int getIntegerValue(){
        return this.number;
    }
    @Override
    public String toString() {
        return Integer.toString(number);
    }


}
