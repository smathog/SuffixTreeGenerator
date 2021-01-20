import java.util.concurrent.atomic.AtomicInteger;

public class Edge {
    private Node from;
    private Node to;
    private int start;
    private AtomicInteger end;

    public Edge(Node from, Node to, int start, AtomicInteger end) {
        this.from = from;
        this.to = to;
        this.start = start;
        this.end = end;
    }

    public Node getTo() {
        return to;
    }

    public int getStart() {
        return start;
    }

    public AtomicInteger getEnd() {
        return end;
    }

    public void setFrom(Node from) {
        this.from = from;
    }

    public void setTo(Node to) {
        this.to = to;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public void setEnd(AtomicInteger end) {
        this.end = end;
    }

    public int getLength() {
        return end.get() - start;
    }
}
