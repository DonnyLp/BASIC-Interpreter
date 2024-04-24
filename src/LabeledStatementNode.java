import java.util.LinkedList;

public class LabeledStatementNode extends StatementNode implements Addable {

    private final String labelName;
    private boolean isWhileLabel;
    private final LinkedList<StatementNode> statements;

    public LabeledStatementNode(String labelName, boolean isWhileLabel) {
        this.labelName = labelName;
        this.isWhileLabel = isWhileLabel;
        this.statements = new LinkedList<>();
    }

    public String getLabelName(){
        return this.labelName;
    }

    public boolean isWhileLabel(){
        return this.isWhileLabel;
    }

    public void setWhileLabelMode(boolean isWhileMode){
        this.isWhileLabel = isWhileMode;
    }

    @Override
    public LinkedList getList() {
        return this.statements;
    }

    @Override
    public void add(Node node) {
        this.statements.add((StatementNode) node);
    }


    public String getLabel() {
        return labelName;
    }

    public LinkedList<StatementNode> getStatements() {
        return this.statements;
    }

    @Override
    public String toString() {
        StringBuilder statementsBuilder = new StringBuilder();
        statementsBuilder.append(this.labelName).append(":\n");
        for (StatementNode statement : statements){
            statementsBuilder.append(statement.toString());
            statementsBuilder.append("\n");
        }
        return statementsBuilder.toString();
    }

}
