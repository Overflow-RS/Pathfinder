package src.pathfinder.impl;

import src.pathfinder.core.util.GameRegion;
import src.pathfinder.core.util.Heuristic;
import src.pathfinder.core.util.Structure;
import src.pathfinder.core.util.WalkDirection;
import src.pathfinder.core.wrapper.PathNode;
import src.pathfinder.core.wrapper.TilePath;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.logging.Logger;

/**
 * Author: Tom
 * Date: 03/09/13
 * Time: 00:02
 */
public class Pathfinder {

    private final Heuristic.HeuristicAlgorithm heuristic;

    public Pathfinder(final Heuristic.HeuristicAlgorithm heuristic) {
        this.heuristic = heuristic;
    }

    public Pathfinder(final Heuristic heuristic) {
        this(heuristic.getAlgorithm());
    }

    public Pathfinder() {
        this(Heuristic.MANHATTAN);
    }

    public TilePath findPath(final int tileA, final int tileB, final long maxTime, final EndCondition endCondition, final boolean returnNextBest) {
        if (!GameRegion.init()) {
            Logger.getGlobal().config("[Pathfinder] Unable to find path due to error loading map data");
            return null;
        }
        final HashMap<Integer, PathNode> nodeMap = new HashMap<Integer,PathNode>();
        final PriorityQueue<PathNode> openList = new PriorityQueue<PathNode>();
        openList.add(get(tileA, tileB, nodeMap));
        final long startTime = System.currentTimeMillis();
        final long endTime = (startTime + maxTime);
        PathNode current;
        PathNode best = null;
        while (endTime > System.currentTimeMillis()) {
            if (openList.isEmpty()) {
                break;
            }
            current = openList.poll();
            if (current.isExpanded()) {
                continue;
            }
            if (endCondition.acceptCurrent(tileB, current)) {
                Logger.getGlobal().config("[Pathfinder] Found Path > Took: " + (System.currentTimeMillis() - startTime) + "ms > Expanded Vertex's: " + nodeMap.size());
                return resolvePath(tileA, tileB, (int) (System.currentTimeMillis() - startTime), current);
            } else {
                if (returnNextBest && (best == null || current.getHeuristicCost() < best.getHeuristicCost())) {
                    best = current;
                }
                current.expand();
                for (PathNode node : getValidMoves(current, tileB, nodeMap)) {
                    if (!node.isExpanded()) {
                        openList.add(node);
                    }
                }
            }
        }
        if (returnNextBest && best != null) {
            Logger.getGlobal().config("[Pathfinder] Sub-Optimal Path found > Took: " + (System.currentTimeMillis() - startTime) + "ms > Expanded Vertex's: " + nodeMap.size());
            return resolvePath(tileA, tileB, (int) (System.currentTimeMillis() - startTime), best);
        }
        Logger.getGlobal().config(
                "[Pathfinder] Failed To Find A Path > Took: " + (System.currentTimeMillis() - startTime) + "ms > Expanded Vertex's: " + nodeMap.size());
        return null;
    }

    public TilePath findPath(final int tileA, final int tileB, final long maxTime, final boolean returnNextBest) {
        return findPath(tileA, tileB, maxTime, DEFAULT_END_FILTER, returnNextBest);
    }

    protected LinkedList<PathNode> getValidMoves(final PathNode current, final int target, final HashMap<Integer, PathNode> map) {
        final LinkedList<PathNode> validMoves = new LinkedList<PathNode>();
        final int x = Structure.TILE.getX(current.getHash()), y = Structure.TILE.getY(current.getHash()), z = Structure.TILE.getZ(current.getHash());
        final int flag = GameRegion.getFlag(x, y, z);
        for (WalkDirection direction : directions) {
            PathNode node = null;
            switch (direction.getWalkType(flag)) {
                case WALK:
                    node = get(Structure.TILE.getHash(x + direction.getOffsetX(), y + direction.getOffsetY(), z), target, map);
                    node.examineNode(current, false);
                    break;
                case OBJECT:
                    node = get(Structure.TILE.getHash(x + direction.getOffsetX(), y + direction.getOffsetY(), z), target, map);
                    node.examineNode(current, true);
                    break;
            }
            if (node != null) {
                validMoves.add(node);
            }
        }
        return validMoves;
    }

    public static final WalkDirection[] directions = WalkDirection.values();

    protected PathNode get(final int tile, final int target, final HashMap<Integer, PathNode> map) {
        PathNode node = map.get(tile);
        if (node == null) {
            map.put(tile, node = new PathNode(tile, heuristic.getCost(tile, target)));
        }
        return node;
    }

    protected TilePath resolvePath(final int start, final int target, final int timeTook, PathNode current) {
        final TilePath path = new TilePath(target, timeTook);
        path.add(current);
        while ((current = current.getParent()) != null) {
            path.addFirst(current);
            if (current.getHash() == start) {
                break;
            }
        }
        Logger.getLogger("Pathfinder").config("[Pathfinder] Path Length: " + path.size());
        return path;
    }

    public interface EndCondition {
        public boolean acceptCurrent(final int target, final PathNode current);
    }

    public static final EndCondition DEFAULT_END_FILTER = new EndCondition() {

        @Override
        public boolean acceptCurrent(int target, PathNode current) {
            return target == current.getHash();
        }
    };

}
