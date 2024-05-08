public class GoSubStatement {
    private String name;
    private StatementNode statement;


    public GoSubStatement(String name, StatementNode statement){
        this.name = name;
        this.statement = statement;
    }

    public String getName(){
        return this.name;
    }

    public StatementNode getStatement(){
        return this.statement;
    }

    public String toString(){
        return this.name + this.statement;
    }
}
