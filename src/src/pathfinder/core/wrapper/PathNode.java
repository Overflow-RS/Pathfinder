package src.pathfinder.core.wrapper;

/**
 * Author: Tom
 * Date: 03/09/13
 * Time: 00:18
 */
public class PathNode implements Comparable<PathNode> {

    private final int hash;
    private final double heuristicCost;

    private boolean expanded;

    private PathNode parent;
    private boolean door = false;
    private double currentCost;
    private double totalCost;

    public PathNode(final int hash, final double heuristicCost) {
        this.hash = hash;
        this.heuristicCost = heuristicCost;
    }

    public void examineNode(final PathNode pathNode, final boolean door) {
        if (parent == null || currentCost > pathNode.currentCost) {
            this.parent = pathNode;
            this.door = door;
            this.currentCost = parent.currentCost + 1;
            this.totalCost = heuristicCost + currentCost;
        }
    }

    public PathNode getParent() {
        return parent;
    }

    public int getHash() {
        return hash;
    }

    public boolean isDoor() {
        return door;
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void expand() {
        this.expanded = true;
    }

    public double getHeuristicCost() {
        return heuristicCost;
    }

    @Override
    public int compareTo(PathNode o) {
        return Double.compare(totalCost, o.totalCost);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PathNode pathNode = (PathNode) o;
        if (hash != pathNode.hash) return false;
        return true;
    }

    @Override
    public int hashCode() {
        return hash;
    }
}
