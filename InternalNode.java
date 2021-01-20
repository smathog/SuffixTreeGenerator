import java.util.LinkedHashMap;

public class InternalNode extends Node {
    private LinkedHashMap<Character, Edge> edgesOut;
    private InternalNode suffixLink = null;

    public InternalNode() {
        generateCreationNumber();
        edgesOut = new LinkedHashMap<>();
    }

    public boolean hasSuffixLink() {
        return suffixLink != null;
    }

    public void setSuffixLink(InternalNode other) {
        suffixLink = other;
    }

    public InternalNode getSuffixLink() {
        return suffixLink;
    }

    @Override
    public boolean isExplicit() {
        return true;
    }

    public LinkedHashMap<Character, Edge> getEdgesOut() {
        return edgesOut;
    }

    public boolean hasEdge(char c) {
        return edgesOut.containsKey(c);
    }

    public Edge getEdge(char c) {
        return edgesOut.get(c);
    }

    public void addEdgeOut(char c, Edge edge) {
        edgesOut.put(c, edge);
    }
}
