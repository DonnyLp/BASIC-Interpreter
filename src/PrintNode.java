import java.util.List;

public class PrintNode extends StatementNode{
    public final List<Node> printList;

    public PrintNode(List<Node> printList){
        this.printList = printList;
    }

    @Override
    public String toString(){
       StringBuilder printString = new StringBuilder();
        for (Node printElement : printList){
            printString.append("|");
           printString.append(printElement.toString());
           printString.append("|");
       }
       return printString.toString();
    }
}
