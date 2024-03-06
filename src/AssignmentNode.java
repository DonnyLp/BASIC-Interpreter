public class AssignmentNode extends StatementNode{

    private final Node assignmentValue;
    private final VariableNode variableName;
    
    public AssignmentNode(VariableNode variableName, Node value){
        this.variableName = variableName;
        this.assignmentValue = value;
    }

    @Override
    public String toString() {
        return this.variableName.toString() + " = " + this.assignmentValue.toString();
    }
}