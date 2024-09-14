public class Node {
    private int f;
    private int g;
    private Node parent;
    private boolean isGoal;
    private boolean isObstacle;
    private Location location; // each Node knows its coordinates in the state
    private boolean isStart; // needed to display field before and after defining start/goal nodes without having
    // to create a new displayField method that takes starting coordinates
    
    public Node(boolean isObstacle, boolean isGoal, int r, int c, boolean isStart) {
        parent = null;
        this.isObstacle = isObstacle;
        this.isGoal = isGoal;
        this.location = new Location(r, c);
        this.isStart = isStart;
    }

    public void calculateF(int g, int h) {
        this.f = g + h;
    }

    public void setG(int g) {
        this.g = g;
    }

    public int getG() {
        return g;
    }

    public void setF(int f) {
        this.f = f;
    }

    public int getF() {
        return f;
    }

    public Location getLocation() {
        return location;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Node getParent() {
        return this.parent;
    }

    public boolean isObstacle() {
        return isObstacle;
    }

    public boolean isGoal() {
        return isGoal;
    }

    public boolean isStart() {
        return isStart;
    }
}
