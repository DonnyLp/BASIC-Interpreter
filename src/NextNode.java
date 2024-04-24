public class NextNode extends StatementNode{

    private final VariableNode variable;

    public NextNode(VariableNode variable){
        this.variable = variable;
    }

    public NextNode(){
        this.variable = null;
    }

    @Override
    public String toString(){
        return "NEXT " + variable;
    }

}
