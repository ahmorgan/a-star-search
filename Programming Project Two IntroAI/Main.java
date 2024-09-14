import java.util.ArrayList;
import java.util.Scanner;
import java.lang.Math;

public class Main {
    
    // creates the state in which A* will run. Defines obstacles randomly, 1 in 10 chance of each node being an obstacle
    // define the start and goal nodes after, as per rubric
    public static Node[][] generateField() {
        Node[][] field = new Node[15][15];
        
        for (int r = 0; r < 15; r++) {
            for (int c = 0; c < 15; c++) {
                int randomNum = (int)(Math.random() * 2); // num from 0 - 9
                if (randomNum == 1) { // choose 9 as my target value to create a 1 in 10 chance
                    field[r][c] = new Node(true, false, r, c, false);
                }
                else {
                    field[r][c] = new Node(false, false, r, c, false);
                }
            }
        }

        return field;
    }

    // uses the manhattan method to calculate an h value for some node
    public static int heuristic(int currR, int currC, int goalR, int goalC) {
        return (Math.abs(goalR - currR) + Math.abs(goalC - currC)) * 10;
    }

    // iterates over all neighbors of current node, skipping over invalid nodes
    // at nodes that are valid, set that node's f and parent node values
    // return an arraylist of every node that was updated
    public static ArrayList<Node> generateNeighbors(Node[][] field, ArrayList<Node> openList, ArrayList<Location> closedList, int currR, int currC, int goalR, int goalC) {
        ArrayList<Node> updatedNodes = new ArrayList<>();
        int r = currR - 1;
        int c = currC - 1;
        boolean skip = false;
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                if (r < 0 || r > 14 || c < 0 || c > 14 || field[r][c].isObstacle() || (r == currR && c == currC)) {
                    // out of bounds or obstacle
                    c++;
                    continue;
                }

                for (Location loc : closedList) {
                    if (loc.getRow() == r && loc.getColumn() == c) {
                        // already visited, skip
                        // skip this iteration of the for loop outside this one
                        skip = true;
                    }
                }

                // Find g cost (up to but not including the node we are looking at)
                int g = field[currR][currC].getG();

                // don't regenerate neighbors already in the open list
                for (Node node : openList) {
                    if (node.getLocation().getRow() == r && node.getLocation().getColumn() == c) {
                        // unless, if when we recalculate the g cost, that g is lower than the node's g,
                        // we've run into an edge case where diagonal movement determined the wrong g cost the first around
                        // (see A* video part 1 on canvas at ~56:00)
                        if (r != currR && c != currC) {
                            g += 14;
                        }
                        else {
                            g += 10;
                        }

                        if (g < node.getG()) {
                            field[r][c].setG(g);
                            field[r][c].calculateF(g, heuristic(r, c, goalR, goalC));
                            field[r][c].setParent(field[currR][currC]);
                            updatedNodes.add(field[r][c]);
                        }

                        skip = true;
                    }
                }
                // I'm using a skip boolean here because I want to continue
                // out of the middle for loop, relative to the for loop that went
                // through the closed list/open list
                if (skip) {
                    skip = false;
                    c++;
                    continue;
                }

                if (r != currR && c != currC) {
                    // diagonal movement
                    g += 14;
                }
                else { 
                    // horizontal/vertical movement
                    g += 10;
                }

                field[r][c].setG(g);
                field[r][c].calculateF(g, heuristic(r, c, goalR, goalC));
                field[r][c].setParent(field[currR][currC]);
                updatedNodes.add(field[r][c]);
                c++;
            }
            c = currC - 1; // reset c
            r++; // next row
        }

        return updatedNodes;
    }

    // iterate through the open list and find the node with the lowest f value,
    // return its index
    public static int findBestNode(ArrayList<Node> openList) {
        int bestIndex = 0;
        int bestF = Integer.MAX_VALUE;
        for (int x = 0; x < openList.size(); x++) {
            if (openList.get(x).getF() < bestF) {
                bestF = openList.get(x).getF();
                bestIndex = x;
            }
        }
        return bestIndex;
    }

    // prints the current state as a field of characters
    public static void displayField(Node[][] field) {
        for (int r = 0; r < 15; r++) {
            for (int c = 0; c < 15; c++) {
                if (field[r][c].isStart()) {
                    System.out.print("S "); // 'S' for Start Node
                }
                else if (field[r][c].isGoal()) {
                    System.out.print("G "); // 'G' for Goal
                }
                else if (field[r][c].isObstacle()) {
                    System.out.print("X "); // 'X' for Obstacle
                }
                else {
                    System.out.print("n "); // 'n' for Navigable Node
                }
            }
            System.out.println();
        }
    }

    // print the path generated by A* by iterating through the parents of each node
    // from the goal node to the start node, putting those Location strings into
    // an arraylist, and reversing the arraylist
    public static void generateAndDisplayPath(int currR, int currC, Node[][] field) {
        System.out.print("\nPath generated by A* is: ");
        Node currNode = field[currR][currC];
        ArrayList<String> path = new ArrayList<>();
        
        while (currNode.getParent() != null) {
            path.add(currNode.getLocation().toString());
            currNode = currNode.getParent();
        }

        for (int x = path.size()-1; x >= 0; x--) {
            if (x == 0) {
                System.out.println(path.get(x));
            }
            else {
                System.out.print(path.get(x) + ", ");
            }
        }
    }

    public static void main(String[] args) {
        // program continues to loop after first search, allowing the user to specify new start/goal nodes
        while (true) {
            // initialize and display state
            Node[][] field = generateField();
            System.out.println("\nCurrent State:");
            displayField(field);

            Scanner scanner = new Scanner(System.in);
            int currR, currC, goalR, goalC;
            
            // get coordinate inputs for start and goal node from user
            // precondition that input be between 0 and 14, be integers only, not be an obstacle and that the current and goal nodes be different
            System.out.println("Enter row of starting position between 0 and 14 (input 'quit' to exit program)");
            String nextLine = scanner.nextLine();
            if (nextLine.equals("quit")) {
                scanner.close();
                break;
            }
            currR = Integer.parseInt(nextLine);
            
            System.out.println("Enter column of starting position between 0 and 14");
            currC = Integer.parseInt(scanner.nextLine());  

            System.out.println("Enter row of goal position between 0 and 14");
            goalR = Integer.parseInt(scanner.nextLine());

            System.out.println("Enter column of goal position between 0 and 14");
            goalC = Integer.parseInt(scanner.nextLine());

            // define start and goal nodes
            field[currR][currC] = new Node(false, false, currR, currC, true);
            field[goalR][goalC] = new Node(false, true, goalR, goalC, false);

            System.out.println("\nNew state with start and goal nodes:");
            displayField(field);
            
            System.out.println("\nSearching..."); 
            
            ArrayList<Node> openList = new ArrayList<>();
            ArrayList<Location> closedList = new ArrayList<>(); // Making this an ArrayList of Locations (node coordinates)
            // instead of Nodes is a little simpler to work with in the generateNeighbors method
            Node currentNode = field[currR][currC];
            boolean solutionExists = true;
            currentNode.setG(0);
            currentNode.calculateF(0, heuristic(currR, currC, goalR, goalC));

            openList.add(currentNode);

            // Main A* loop
            while(true) {
                // if we've removed all nodes from the open list and none were the goal,
                // we've completely explored the possible navigable area and no solution exists
                if (openList.size() == 0) {
                    solutionExists = false;
                    System.out.println("no path could be found");
                    break;
                }
                currentNode = openList.remove(findBestNode(openList)); // findBestNode() returns index of best node

                // we've visited the current node, so it goes in the closed list
                closedList.add(currentNode.getLocation());

                // update local variable that keeps track of the current node
                // Not actually needed, but makes it easier to read
                currR = currentNode.getLocation().getRow();
                currC = currentNode.getLocation().getColumn();

                if (currentNode.isGoal()) {
                    break;
                }
                else {
                    // iterate through updatedNodes and set F and parent variables in the respective Nodes in the current state
                    // I can't just return an updated field 2d array and set field to it because of the way object references work in Java
                    for (Node n : generateNeighbors(field, openList, closedList, currR, currC, goalR, goalC)) {
                        field[n.getLocation().getRow()][n.getLocation().getColumn()].setG(n.getG());
                        field[n.getLocation().getRow()][n.getLocation().getColumn()].setF(n.getF());
                        field[n.getLocation().getRow()][n.getLocation().getColumn()].setParent(n.getParent());
                        
                        openList.add(n);
                    }
                }
            }

            if (solutionExists) {
                generateAndDisplayPath(currR, currC, field);
            }
        }
    }
}