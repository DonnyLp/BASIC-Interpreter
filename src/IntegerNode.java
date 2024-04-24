public class IntegerNode extends Node{


    private final int number;

    public IntegerNode(int number){
        this.number = number;
    }

    public IntegerNode(){
        this.number = 0;
    }

    public int getIntegerValue(){
        return this.number;
    }
    @Override
    public String toString() {
        return Integer.toString(number);
    }


}
