import java.util.List;

public class StatementsNode extends StatementNode {
    private final List<StatementNode> statements;

    public StatementsNode(List<StatementNode> statements){
        this.statements = statements;
    }

    @Override
    public String toString(){
        StringBuilder statementsBuilder = new StringBuilder();
        for (StatementNode statement : statements){
            statementsBuilder.append(" ");
            statementsBuilder.append(statement.toString());
            statementsBuilder.append(" ");
        }
        return statementsBuilder.toString();
    }
}
