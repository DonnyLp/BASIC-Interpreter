import java.util.LinkedList;

public class StatementsNode  {
    private final LinkedList<StatementNode> statements;

    public StatementsNode(){
        this.statements = new LinkedList<>();
    }

    public StatementNode getHead(){
        return this.statements.getFirst();
    }


    public void add(StatementNode node) {
        this.statements.add(node);
    }

    public LinkedList<StatementNode> getList() {
        return this.statements;
    }

    public void setNextForAll(){
        Visitor visitor = new NextSetter();
        for(StatementNode node: this.statements){
            node.accept(visitor);
        }
    }

    @Override
    public String toString(){
        StringBuilder statementsBuilder = new StringBuilder();

        for(Node statement: statements){
            statementsBuilder.append(statement).append("\n");
        }
        return statementsBuilder.toString();
    }

}
