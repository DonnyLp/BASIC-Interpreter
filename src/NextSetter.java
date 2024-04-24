public class NextSetter implements Visitor{

    private StatementNode currentNode;

    public NextSetter(){
        this.currentNode = null;
    }

    @Override
    public void visit(StatementNode node) {
        if(currentNode != null){
            currentNode.setNext(node);
        }
        currentNode = node;
    }
}
