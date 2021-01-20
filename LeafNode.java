public class LeafNode extends Node {
    public LeafNode() {
        generateCreationNumber();
    }

    @Override
    public boolean isLeaf() {
        return true;
    }
}
