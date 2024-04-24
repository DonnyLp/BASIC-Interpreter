public class VariableNode extends Node{
    
    private final String name;

    public VariableNode(String name){
        this.name = name;
    }
    public VariableNode(){this.name = "";}

    public String getName(){return this.name;}

    @Override
    public String toString() {
        return this.name;
    }
}
