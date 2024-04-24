public abstract class StatementNode extends Node{

    private StatementNode next;

    //construct the internal linked list
    public StatementNode getNext() {
        return next;
    }

    public void setNext(StatementNode next) {
        this.next = next;
    }

    public void accept(Visitor visitor) {
        visitor.visit(this);
    }

    @Override
    public abstract String toString();

}


