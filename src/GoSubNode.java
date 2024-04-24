public class GoSubNode extends StatementNode{

    private final String label;

    public GoSubNode(String subRoutineName){
        this.label =  subRoutineName;
    }

    public String getLabel(){
        return this.label;
    }

    @Override
    public String toString(){
        return "GOSUB " + this.label;
    }
}
