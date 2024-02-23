public abstract class Node {


    //Abstract methods
    protected abstract String getValue();

    @Override
    public String toString() { return getValue(); }

}

