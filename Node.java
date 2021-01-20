public abstract class Node {
    private static int numCreated = 0;
    private int creationNumber;

    protected void generateCreationNumber() {
        creationNumber = ++numCreated;
    }

    public boolean isLeaf() {
        return false;
    }

    public boolean isExplicit() {
        return false;
    }

    public boolean isRoot() {
        return false;
    }

    public int getCreationNumber() {
        return creationNumber;
    }

    @Override
    public String toString() {
        return Integer.toString(creationNumber);
    }
}
