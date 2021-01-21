import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class Main {
    public static void main(String[] args) {
        try (InputStreamReader in = new InputStreamReader(System.in);
             BufferedReader br = new BufferedReader(in)) {
            final String text = br.readLine() + "$";
            SuffixTree st = new SuffixTree(text);
          //  System.out.println(st);
            System.out.println(st.getInternalNodeDepth());
        } catch (IOException e) {
            System.out.println("Error reading input");
            System.out.println(e.getMessage());
        }
    }

    public static abstract class Node {
        private int creationNumber;
        private static int numCreated = 0;

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

    public static class Edge {
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

    public static class InternalNode extends Node {
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

    public static class RootNode extends InternalNode {
        public RootNode() {
            super();
        }

        @Override
        public boolean isRoot() {
            return true;
        }
    }

    public static class LeafNode extends Node {
        public LeafNode() {
            generateCreationNumber();
        }

        @Override
        public boolean isLeaf() {
            return true;
        }
    }

    public static class SuffixTree {
        private String source;
        private RootNode root;
        private InternalNode activeNode;
        private Character activeEdge;
        private int activeLength;
        private int remainder;
        private AtomicInteger endMarker;

        private LinkedHashMap<Integer, Integer> suffixLinks;

        //Builds a suffix tree using Ukkonen's Algorithm
        public SuffixTree(String source) {
            suffixLinks = new LinkedHashMap<>();
            this.source = source;
            root = new RootNode();
            activeNode = root;
            activeEdge = null;
            activeLength = 0;
            remainder = 0;
            endMarker = new AtomicInteger(0);

            while (endMarker.get() != source.length()) {
                ++remainder;

                //First: update all terminal edges
                int i = endMarker.getAndIncrement();

                //Second: determine if insert is necessary, or update active points
                update(i, null);

/*            System.out.println("=====STAGE " + endMarker.get() + "=====");
            System.out.println("Suffix: " + this.source.substring(0, endMarker.get()));
            System.out.println("Active Node: " + activeNode.getCreationNumber());
            System.out.println("Active Edge: " + activeEdge);
            System.out.println("Active Length: " + activeLength);
            System.out.println("Remainder: " + remainder);
            System.out.println(this);
            System.out.println("=====STAGE " + endMarker.get() + "=====");*/
            }
        }

        public int getNDistinctSubstrings() {
            ArrayDeque<Edge> edgeStack = new ArrayDeque<>();
            for (Edge e : root.getEdgesOut().values())
                edgeStack.push(e);
            if (edgeStack.isEmpty())
                return 1;
            int sum = 0;
            while (!edgeStack.isEmpty()) {
                Edge top = edgeStack.pop();
                sum += top.getLength();
                if (top.getTo().isExplicit()) {
                    InternalNode iNode = (InternalNode) top.getTo();
                    for (Edge e : iNode.getEdgesOut().values())
                        edgeStack.push(e);
                }
            }
            return sum + 1;
        }

        public int getInternalNodeDepth() {
            return getDeepestInternalNodeImpl(root);
        }

        private int getDeepestInternalNodeImpl(InternalNode iNode) {
            return iNode.getEdgesOut().values().stream()
                    .filter(e -> e.getTo().isExplicit())
                    .mapToInt(e -> e.getLength() + getDeepestInternalNodeImpl((InternalNode) e.getTo()))
                    .max()
                    .orElseGet(() -> 0);
        }

        private void update(int index, InternalNode previousNode) {/*
        System.out.println("===UPDATE CALLED===");
        System.out.println("Suffix: " + this.source.substring(0, endMarker.get()));
        System.out.println("Active Node: " + activeNode.getCreationNumber());
        System.out.println("Active Edge: " + activeEdge);
        System.out.println("Active Length: " + activeLength);
        System.out.println("Remainder: " + remainder);
        System.out.println("Index: " + index);
        System.out.println("previousNode: " + previousNode);
        System.out.println(this);*/

            char lastChar = source.charAt(index);

            //Update rule lambdas
            Runnable rule1 = () -> {
                if (activeNode.isRoot() && activeLength > 0) {
                    --activeLength;
                    activeEdge = source.charAt(index - activeLength);
                }
            };

            Consumer<InternalNode> rule2 = iNode -> {
                if (previousNode != null) {
                    previousNode.setSuffixLink(iNode);
                    suffixLinks.put(previousNode.getCreationNumber(), iNode.getCreationNumber());
                }
            };

            Runnable rule3 = () -> {
                if (!activeNode.isRoot()) {
                    if (activeNode.hasSuffixLink())
                        activeNode = activeNode.getSuffixLink();
                    else
                        activeNode = root;
                }
            };

            Consumer<InternalNode> allRules = iNode -> {
                rule1.run();
                rule2.accept(iNode);
                rule3.run();
            };

            //Position validation lambda
            Runnable checkPos = () -> {
                if (activeEdge != null && activeNode.hasEdge(activeEdge)) {
                    Edge aEdge = activeNode.getEdge(activeEdge);
                    while (aEdge != null && activeLength >= aEdge.getLength()) {
                        activeNode = (InternalNode) aEdge.getTo();
                        activeLength -= aEdge.getLength();
                        activeEdge = source.charAt(index - activeLength);
                        aEdge = activeNode.getEdge(activeEdge);
                    }
                }
            };

            Consumer<InternalNode> jumpLink = iNode -> {
              iNode.setSuffixLink(activeNode);
              suffixLinks.put(iNode.getCreationNumber(), activeNode.getCreationNumber());
            };

            if (activeEdge == null || activeLength == 0) {//do update directly at current node
                //Case 1: lastChar is already present in this node, so just update active point
                if (activeNode.hasEdge(lastChar)) {
                    if (previousNode != null)
                        jumpLink.accept(previousNode);
                    ++activeLength;
                    activeEdge = lastChar;
                    checkPos.run();
                } else { //Case 2: directly insert new edge into node, terminating in a leaf
                    Edge edge = new Edge(activeNode, new LeafNode(), index, endMarker);
                    activeNode.addEdgeOut(lastChar, edge);
                    InternalNode fromNode = activeNode;
                    --remainder;
                    allRules.accept(activeNode);
                    if (remainder > 0) {
                        if (fromNode.isRoot())
                            update(index, null);
                        else
                            update(index, fromNode);
                    }
                }
            } else { //activePoint points to a point along an existing edge
                Edge aEdge = activeNode.getEdge(activeEdge);
                int pos = aEdge.getStart() + activeLength;
                char activePointChar = source.charAt(aEdge.getStart() + activeLength);
                //Case 1: this next point matches lastChar, so just update activeLength
                if (activePointChar == lastChar) {
                    if (previousNode != null)
                        jumpLink.accept(previousNode);
                    ++activeLength;
                    checkPos.run();
                } else { //Case 2: this edge needs to be split at this point
                    InternalNode iNode = new InternalNode();
                    iNode.addEdgeOut(activePointChar, new Edge(iNode, aEdge.getTo(), pos, aEdge.getEnd()));
                    iNode.addEdgeOut(lastChar, new Edge(iNode, new LeafNode(), index, endMarker));
                    aEdge.setEnd(new AtomicInteger(pos));
                    aEdge.setTo(iNode);
                    --remainder;
                    allRules.accept(iNode);
                    checkPos.run();
                    if (remainder > 0)
                        update(index, iNode);
                }
            }
        }

        //Print function based on the one found in this article:
        //https://www.baeldung.com/java-print-binary-tree-diagram
        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            traverseNode(sb, "", "", root, false);
            sb.append(suffixLinks);
            return sb.toString();
        }

        private void traverseNode(StringBuilder sb, String padding, String pointer, Node node, boolean nodeAfter) {
            if (node != null) {
                sb.append(padding);
                sb.append(pointer);
                if (node == activeNode)
                    sb.append('(');
                else
                    sb.append('[');
                sb.append(node.getCreationNumber());
                if (node == activeNode)
                    sb.append(')');
                else
                    sb.append(']');
                sb.append('\n');

                String terminalPointer = "└──";
                String linkPointer = "├──";

                Consumer<String> nextCall = s -> {
                    InternalNode iNode = (InternalNode) node;
                    List<Edge> edgeList = new ArrayList<>(iNode.getEdgesOut().values());
                    for (int i = 0; i < edgeList.size(); ++i) {
                        Edge edge = edgeList.get(i);
                        String edgePointer = "<" + source.substring(edge.getStart(), edge.getEnd().get()) + ">──";
                        if (i == edgeList.size() - 1)
                            traverseNode(sb, s, terminalPointer + edgePointer, edge.getTo(), false);
                        else
                            traverseNode(sb, s, linkPointer + edgePointer, edge.getTo(), true);
                    }
                };


                if (node.isRoot()) {
                    nextCall.accept("");
                } else if (node.isExplicit()) {
                    int length = pointer.length() + Integer.toString(node.getCreationNumber()).length();
                    StringBuilder pb = new StringBuilder(padding);
                    if (nodeAfter)
                        pb.append("|" + " ".repeat(length));
                    else
                        pb.append(" " + " ".repeat(length));
                    nextCall.accept(pb.toString());
                }
            }
        }
    }

}