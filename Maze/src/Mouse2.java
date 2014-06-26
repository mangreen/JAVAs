class Point2 {
    public final int x;
    public final int y;
    Point2(int x, int y) {
        this.x = x;
        this.y = y;
    }
}

class Maze2 {
    private int[][] maze;
    private Point end;
    
    Maze2(int[][] maze, Point end) {
        this.maze = maze;
        this.end = end;
    }
    
    public boolean isArrived() {
        return maze[end.x][end.y] == 1;
    }
    
    public boolean isEmpty(Point p) { 
    	return maze[p.x][p.y] == 0; 
    }
    
    public void step(Point p) { maze[p.x][p.y] = 1; }
    public void empty(Point p) { maze[p.x][p.y] = 0; }
    
    public void print() {
        for(int i = 0; i < maze.length; i++) { 
            for(int j = 0; j < maze[0].length; j++) { 
                if(maze[i][j] == 2) 
                    System.out.print("▉"); 
                else if(maze[i][j] == 1) 
                    System.out.print("◇"); 
                else 
                    System.out.print("　"); 
            } 
            System.out.println(); 
        }
    }
}

public class Mouse2 {
    public static void go(Maze maze, Point p) {
        maze.step(p);
        if(maze.isArrived()) {
            maze.print();
        }
        
        test(maze, new Point(p.x, p.y + 1));
        test(maze, new Point(p.x + 1, p.y));
        test(maze, new Point(p.x, p.y - 1));
        test(maze, new Point(p.x - 1, p.y));
        
        maze.empty(p);
    }
    
    private static void test(Maze maze, Point p) {
        if(maze.isEmpty(p))
            go(maze, p);
    }
    
    public static void main(String[] args) {
        Maze maze = new Maze( 
                      new int[][]{
                        {2, 2, 2, 2, 2, 2, 2, 2, 2},
                        {2, 0, 0, 0, 0, 0, 0, 0, 2},
                        {2, 0, 2, 2, 0, 2, 2, 0, 2},
                        {2, 0, 2, 0, 0, 2, 0, 0, 2},
                        {2, 0, 2, 0, 2, 0, 2, 0, 2},
                        {2, 0, 0, 0, 0, 0, 2, 0, 2},
                        {2, 2, 0, 2, 2, 0, 2, 2, 2},
                        {2, 0, 0, 0, 0, 0, 0, 0, 2},
                        {2, 2, 2, 2, 2, 2, 2, 2, 2}},
                      new Point(7, 7));
                  
        Mouse2.go(maze, new Point(1, 1));
    }
}
