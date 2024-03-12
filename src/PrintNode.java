import java.util.List;

public class PrintNode extends StatementNode{
    public final List<Node> printList;

    public PrintNode(List<Node> printList){
        this.printList = printList;
    }

    @Override
    public String toString(){
       return printList.toString();
    }
}
