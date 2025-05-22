package enemy;

public class Node {
    public int col, row;
    public int gCost, hCost, fCost;
    public Node parent;

    public Node(int col, int row) {
        this.col = col;
        this.row = row;
    }

    public void calculateHeuristic(int goalCol, int goalRow) {
        int dx = Math.abs(goalCol - col);
        int dy = Math.abs(goalRow - row);
        hCost = (dx + dy) * 10;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Node)) return false;
        Node other = (Node) obj;
        return this.col == other.col && this.row == other.row;
    }

    @Override
    public int hashCode() {
        return col * 1000 + row;
    }
}
