import java.util.LinkedList;

public class FunctionNode extends StatementNode implements Addable{

    private String functionName;
    private final LinkedList<Node> parameters;

    public FunctionNode(String functionName){
        this.functionName = functionName;
        this.parameters = new LinkedList<>();
    }
    public FunctionNode(String functionName, LinkedList<Node> parameters){
        this.functionName = functionName;
        this.parameters = parameters;
    }

    public FunctionNode(){
        this.parameters = new LinkedList<>();
    }

    public String getName(){
        return this.functionName;
    }

    public Node get(int index){
        return this.parameters.get(index);
    }

    public Node peek(){
        return this.parameters.peek();
    }

    @Override
    public void add(Node node){
        this.parameters.add(node);
    }

    @Override
    public LinkedList<Node>getList(){
        return this.parameters;
    }

    @Override
    public String toString(){

        StringBuilder functionBuilder = new StringBuilder();

        functionBuilder.append(this.functionName).append("(");

        int index = 0;
        while (index < parameters.size()){

            functionBuilder.append(parameters.get(index).toString());

            if(index < parameters.size() - 1) functionBuilder.append(",");
            index++;
        }

        functionBuilder.append(")");

        return functionBuilder.toString();
    }
}
