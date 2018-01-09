import java.util.*;

public class Node
{
        private int g, h;	// g-value of current node, kostos metavashs
                        	// h-value of current node, upoloipomenh apostash, ektimatai apo euristikh
        public int f;    	// f = g + h
                       			
        private Node parent, original_parent;
        private Node r_sibling; //Pointer to Right Sibling
        private Node child1;    //Pointer to Leftmost Child
        private State state;    //State of problem -> coordinates and steps
        private int next;   //0 for CLOSED , 1 for OPEN, -1 for undecided yet

        public Node(int xValue, int yValue, int gValue, int hValue, Node parent, Node r_sibling, int step)
        {
                this.state = new State(xValue, yValue, step);
                this.parent = parent;
                this.original_parent = parent;
                this.r_sibling = r_sibling;
                this.child1 = null;
                this.next = -1;

                this.g = gValue;
                this.h = hValue;
                this.f = gValue + hValue;
        }


        public List<Node> find_children(char[][] map, int xdestination, int ydestination)
        {
        	List<Node> res = new LinkedList<Node>();
        	int current_x, current_y, nx, ny, current_step;
        	Node child = null;

        	current_x = this.state.getx();
        	current_y = this.state.gety();
        	current_step = this.state.getstep();

        	//top child
        	nx = current_x - 1;
        	ny = current_y;
        	if(map[nx][ny]!='X')
        	{
        		child = new Node(nx, ny, this.g + 1, heuristic(nx, ny, xdestination, ydestination), this,  child, current_step + 1);
                res.add(child);
        	}
        	

        	//bot child
			nx = current_x + 1;
        	ny = current_y;
        	if(map[nx][ny]!='X')
        	{
        		child = new Node(nx, ny, this.g + 1, heuristic(nx, ny, xdestination, ydestination), this,  child, current_step + 1);
                res.add(child);
        	}
        	

        	//right child
			nx = current_x;
        	ny = current_y + 1;
        	if(map[nx][ny]!='X')
        	{
        		child = new Node(nx, ny, this.g + 1, heuristic(nx, ny, xdestination, ydestination), this,  child, current_step + 1);
                res.add(child);
        	}
        	

        	//left child
			nx = current_x;
        	ny = current_y - 1;
        	if(map[nx][ny]!='X')
        	{
        		child = new Node(nx, ny, this.g + 1, heuristic(nx, ny, xdestination, ydestination), this,  child, current_step + 1);
                res.add(child);
        	}
        	

        	if(child==null)
        	{
        		this.next = 0;
        		this.child1 = null;
        	}
        	else this.child1 = child;

        	return res;
        }

        public State getState()
        {
        	return this.state;
        }

        public Node getParent()
        {
        	return this.parent;
        }

        public Node getOriginalParent()
        {
        	return this.original_parent;
        }

        public int getG()
        {
        	return this.g;
        }

        public int getH()
        {
            return this.h;
        }

        public Node getR_sibling()
        {
        	return this.r_sibling;
        }

        public Node getChild1()
        {
        	return this.child1;
        }

        public void setChild1(Node child)
        {
        	this.child1 = child;
        	return;
        }

        public void setNext(int value)
        {
        	this.next = value;
        	return;
        }

        public void setOriginalParent(Node original_parent)
        {
            this.original_parent = original_parent;
            return;
        }

        //admissible heuristic = manhattan distance
        public int heuristic(int xnode, int ynode, int xdestination, int ydestination)
    	{
        	return (Math.abs(xdestination - xnode) + Math.abs(ydestination - ynode));
    	}

        //non-admissible heuristic = 2*(manhattan distance)
        //public int heuristic(int xnode, int ynode, int xdestination, int ydestination)
        //{
            //return (2*(Math.abs(xdestination - xnode) + Math.abs(ydestination - ynode)));
        //}

    	@Override
		public boolean equals(Object other)
		{
    		if (other == null) return false;
    		if (other == this) return true;

    		Node otherNode = (Node)other;
    		
    		if (otherNode.getState().getx() == this.state.getx() && otherNode.getState().gety() == this.state.gety()) return true;
    		else return false;
		}

        @Override
        public int hashCode() 
        {
            return this.state.getx()*10 + this.state.gety();
        }



        public String toString()
        {
            return "(" + Integer.toString(this.state.getx()) + "," + Integer.toString(this.state.gety()) + ")";
        }


        class State
        {
        	private int x, y, step;

        	public State(int xValue, int yValue, int step)
        	{
        		this.x = xValue;
        		this.y = yValue;
        		this.step = step;
        	}

        	public int getx()
        	{
        		return this.x;
        	}

        	public int gety()
        	{
        		return this.y;
        	}

        	public int getstep()
        	{
        		return this.step;
        	}

            public void setx(int x)
            {
                this.x = x;
                return;
            }

            public void sety(int y)
            {
                this.y = y;
                return;
            }
        }
}