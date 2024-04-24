import java.util.LinkedList;

public class PrintNode extends StatementNode implements Addable{
    private final LinkedList<Node> printList;

    public PrintNode(){
        this.printList = new LinkedList<>();
    }

    public LinkedList<Node> getList(){
        return this.printList;
    }

    @Override
    public void add(Node node){
        printList.add(node);
    }

    @Override
    public String toString(){
        StringBuilder builder = new StringBuilder();

        builder.append("PRINT").append(" ");

        int index = 0;
        while (index < printList.size()){

            builder.append(printList.get(index).toString());

            if(index < printList.size() - 1) builder.append(",");
            index++;
        }
        return builder.toString();
    }
}

