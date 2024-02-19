public abstract class Node {


    //Abstract methods
    protected abstract String getType();
    protected abstract String getValue();


    //TO DO : Create to String that looks similar to input
    @Override
    public String toString(){
        return getType() + ":" + getValue();
    }

}

