import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;
import edu.princeton.cs.algs4.StdOut;

import javax.naming.directory.SearchControls;
import java.util.HashSet;
import java.util.Scanner;

public class Solver
{
    private Board initial;
    private MinPQ<SearchNode> pq;
    private MinPQ<SearchNode> twinpq;
    private int n;
    private int[][] blocks;
    private Board goal;
    private class SearchNode implements Comparable<SearchNode>
    {
        private int moves;
        private int priority;
        Board board;
        SearchNode prev;
        public SearchNode(Board board, int moves,SearchNode prev)
        {
            this.board = board;
            this.moves = moves;
            this.prev = prev;
            priority = moves + board.manhattan();

        }

        @Override
        public int compareTo(SearchNode o) {
            return (this.priority - o.priority);
        }
    }
    public Solver(Board initial)
    {
        if(initial == null)
        {
            throw new NullPointerException();
        }
        n= initial.dimension();
        pq = new MinPQ<>();
        twinpq = new MinPQ<>();
        pq.insert(new SearchNode(initial,0,null));
        twinpq.insert(new SearchNode(initial.twin(),0,null));
        int k=1;
        blocks = new int[n][n];
        for(int i=0;i<n;i++)
        {
            for(int j=0;j<n;j++)
            {
                blocks[i][j] = k;
                k++;
            }
        }
        blocks[n-1][n-1] = 0;
        goal = new Board(blocks);
        HashSet<Board> hel = new HashSet<>();
        HashSet<Board> heltwin = new HashSet<>();

        while(!pq.min().board.equals(goal) && !twinpq.min().board.equals(goal))
        {
            hel.add(pq.min().board);
            heltwin.add(twinpq.min().board);

            SearchNode min = pq.min();
            pq.delMin();
            SearchNode mintwin = twinpq.min();
            twinpq.delMin();
            for(Board neighbor: min.board.neighbors())
            {
                if(min.moves == 0 || !neighbor.equals(min.board))
                {
                    pq.insert(new SearchNode(neighbor,min.moves+1,min));
                }
            }
            for(Board neighbor: mintwin.board.neighbors())
            {
                if(mintwin.moves == 0 || !neighbor.equals(mintwin.board))
                {
                    twinpq.insert(new SearchNode(neighbor,mintwin.moves+1,mintwin));
                }
            }

        }
    }
    public boolean isSolvable()
    {
        if(pq.min().board.equals(goal))
            return true;
        else if(twinpq.min().board.equals(goal))
            return false;
        return false;
    }
    public int moves()
    {
        if(!isSolvable())
            return -1;
        return pq.min().moves;

    }
    public Iterable<Board> solution()
    {
        if(!isSolvable())
            return null;
        Stack<Board> st = new Stack<>();
        SearchNode current = pq.min();
        while(current.prev != null)
        {
            st.push(current.board);
            current = current.prev;
        }
        st.push(initial);
        return st;
    }
    public static void main(String[] args)
    {
        Scanner in = new Scanner(System.in);

        int n = in.nextInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.nextInt();
        Board initial = new Board(blocks);

        // solve the puzzle
        double st = System.nanoTime();
        Solver solver = new Solver(initial);
        double end = System.nanoTime();
        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board+"\n"+(end -st)/1000000000);

            }
    }
}