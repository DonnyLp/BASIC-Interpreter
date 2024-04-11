public class GoSubNode extends StatementNode{

    private final String subRoutineName;

    public GoSubNode(String subRoutineName){
        this.subRoutineName =  subRoutineName;
    }

    @Override
    public String toString(){
        return "GOSUB " + this.subRoutineName;
    }
}
