package enemy;

import main.GamePanel;

import java.util.*;
import java.util.function.BiPredicate;

public class PathFinder {
    GamePanel gp;
    Node[][] grid;
    PriorityQueue<Node> openList;
    boolean[][] closedList;
    boolean allowDiagonal;
    private java.util.function.BiPredicate<Integer, Integer> safeChecker;

    public PathFinder(GamePanel gp) {
        this.gp = gp;
        createGrid();
    }

    public void setSafeChecker(BiPredicate<Integer, Integer> checker) {
        this.safeChecker = checker;
    }

    public void createGrid() {
        grid = new Node[gp.maxWorldCol][gp.maxWorldRow];
        for (int col = 0; col < gp.maxWorldCol; col++) {
            for (int row = 0; row < gp.maxWorldRow; row++) {
                grid[col][row] = new Node(col, row);
            }
        }
    }

    public List<Node> findPath(int startCol, int startRow, int goalCol, int goalRow, boolean allowDiagonal) {
        this.allowDiagonal = allowDiagonal;
        openList = new PriorityQueue<>(Comparator.comparingInt(n -> n.fCost));
        closedList = new boolean[gp.maxWorldCol][gp.maxWorldRow];

        Node startNode = grid[startCol][startRow];
        Node goalNode = grid[goalCol][goalRow];

        for (int col = 0; col < gp.maxWorldCol; col++) {
            for (int row = 0; row < gp.maxWorldRow; row++) {
                if (safeChecker != null && !safeChecker.test(col, row)) {
                    continue;
                }
                Node node = grid[col][row];
                node.gCost = Integer.MAX_VALUE;
                node.calculateHeuristic(goalCol, goalRow);
                node.fCost = node.gCost + node.hCost;
                node.parent = null;
            }
        }
        

        startNode.gCost = 0;
        startNode.fCost = startNode.hCost;
        openList.add(startNode);

        while (!openList.isEmpty()) {
            Node current = openList.poll();

            if (current.equals(goalNode)) {
                return reconstructPath(goalNode);
            }

            closedList[current.col][current.row] = true;

            for (Node neighbor : getNeighbors(current)) {
                if (closedList[neighbor.col][neighbor.row]) continue;

                if (gp.tileM.tile[gp.tileM.mapTileNum[neighbor.col][neighbor.row]].collision) continue;

                int tentativeG = current.gCost + calculateCost(current, neighbor);
                if (tentativeG < neighbor.gCost) {
                    neighbor.parent = current;
                    neighbor.gCost = tentativeG;
                    neighbor.fCost = neighbor.gCost + neighbor.hCost;

                    if (!openList.contains(neighbor)) {
                        openList.add(neighbor);
                    }
                }
            }
        }

        return null; // Không tìm được
    }

    private int calculateCost(Node from, Node to) {
        int dx = Math.abs(from.col - to.col);
        int dy = Math.abs(from.row - to.row);
        return (dx + dy == 2) ? 14 : 10; // 14 = chéo, 10 = thẳng
    }

    private List<Node> getNeighbors(Node node) {
        List<Node> neighbors = new ArrayList<>();

        int[][] directions4 = {
                {0, -1}, // up
                {0, 1},  // down
                {-1, 0}, // left
                {1, 0}   // right
        };

        int[][] directions8 = {
                {-1, -1}, {0, -1}, {1, -1},
                {-1, 0},           {1, 0},
                {-1, 1},  {0, 1},  {1, 1}
        };

        int[][] directions = allowDiagonal ? directions8 : directions4;

        for (int[] d : directions) {
            int newCol = node.col + d[0];
            int newRow = node.row + d[1];
            if (isInBounds(newCol, newRow) && !gp.tileM.tile[gp.tileM.mapTileNum[newCol][newRow]].collision) {
                neighbors.add(grid[newCol][newRow]);
            }
        }
        return neighbors;
    }

    private boolean isInBounds(int col, int row) {
        return col >= 0 && col < gp.maxWorldCol && row >= 0 && row < gp.maxWorldRow;
    }

    private List<Node> reconstructPath(Node goal) {
        List<Node> path = new ArrayList<>();
        Node current = goal;
        while (current != null) {
            path.add(0, current);
            current = current.parent;
        }
        return path;
    }
}
