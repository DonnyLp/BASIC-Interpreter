import javax.lang.model.element.VariableElement;

public class AssignmentNode extends StatementNode{

    private final Node assignmentValue;
    private final VariableNode variableName;
    
    public AssignmentNode(VariableNode variableName, Node value){
        this.variableName = variableName;
        this.assignmentValue = value;
    }

    public VariableNode getVariableName(){
        return this.variableName;
    }

    public Node getAssignmentValue(){
        return this.assignmentValue;
    }

    @Override
    public String toString() {
        return this.variableName.toString() + " = " + this.assignmentValue.toString();
    }

}