import java.util.LinkedList;

public class LabeledStatementNode extends StatementNode implements Addable {

    private final String label;
    private final LinkedList<StatementNode> statements;

    public LabeledStatementNode(String label) {
        this.label = label;
        this.statements = new LinkedList<>();
    }

    @Override
    public void add(Node node) {
        this.statements.add((StatementNode) node);
    }

    public String getLabel() {
        return label;
    }

    public LinkedList<StatementNode> getStatements() {
        return this.statements;
    }

    @Override
    public String toString() {
        StringBuilder statementsBuilder = new StringBuilder();
        statementsBuilder.append(this.label).append(":\n");
        for (StatementNode statement : statements){
            statementsBuilder.append(statement.toString());
            statementsBuilder.append("\n");
        }
        return statementsBuilder.toString();
    }

}
