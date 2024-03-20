import java.util.LinkedList;

public class StatementsNode extends StatementNode implements Addable {
    private final LinkedList<StatementNode> statements;

    public StatementsNode(){
        this.statements = new LinkedList<>();
    }

    @Override
    public void add(Node node) {
        this.statements.add((StatementNode) node);
    }

    @Override
    public String toString(){
        StringBuilder statementsBuilder = new StringBuilder();
        for (StatementNode statement : statements){
            statementsBuilder.append(statement.toString());
            statementsBuilder.append("\n");
        }
        return statementsBuilder.toString();
    }


}
