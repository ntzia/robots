import java.util.*;
import java.io.*;

public class Astar
{
    PriorityQueue<Node> queue;
    Set<Node> explored;
    Node source, destination;
    char[][] map;

    public Astar(Node source, Node destination, char[][] map)
    {
        explored = new HashSet<Node>();

        this.source = source;
        this.destination = destination;
        this.map = map;

        Comparator<Node> comp = new Comparator<Node>(){
            //override compare method
            public int compare(Node i, Node j)
            {
                if(i.f > j.f) return 1;
                else if (i.f < j.f) return -1;
                else return 0;       
            }
        };
        queue = new PriorityQueue<Node>(20, comp);

        queue.add(source);
    }

    public Node search(LinkedList<Node> route_of_other, PrintWriter writer, long[] node_counter)
    {
        boolean in_queue;
        Node current;
        List<Node> children;
        int len_of_route_of_other = route_of_other.size();

        while(!queue.isEmpty())
        {
            //the node in having the lowest f value
            current = queue.poll();

            if((route_of_other.isEmpty())) writer.println("Robot 1 considering position " + current + " at step " + current.getState().getstep());
            else writer.println("Robot 2 considering position " + current + " at step " + current.getState().getstep());
            explored.add(current);


            //Robot 1 has an empty route_of_other
            //for Robot 2 avoid any conflict until Robot1 arrives at the final goal
            if(!(route_of_other.isEmpty()) && current.getState().getstep() < len_of_route_of_other-1)
            {
            	//for any node except the source check for conflicts
            	if(!current.equals(source))
            	{
                	if(check_for_conflict(current, route_of_other, queue, explored, writer)) continue;
            	}
            }

            //goal found
            if(current.equals(destination))
            {
            	writer.println();
            	return current;
            } 
            
            children = current.find_children(map, destination.getState().getx(), destination.getState().gety());

            //check every child of current node
            for(Node child : children)
            {
                //if child node has been evaluated skip       
                if((explored.contains(child))) continue;     

                //else if child node is not in queue or newer g is lower

                in_queue = false;
                for(Node queue_elem : queue)
                {
                    if (queue_elem.equals(child))
                    {
                        int temp_g = queue_elem.getG();
                        in_queue = true;
                        if(temp_g>child.getG())
                        {
                            queue.remove(queue_elem);
                            queue.add(child);
                        }
                        break;
                    }
                }
                if(!in_queue)
                {
                    queue.add(child);
                    node_counter[0]++;	//increment the counter for every new node in queue
                }
            }   
        }

        //in the case of failure return null
        return null;
    }

    public boolean check_for_conflict(Node current, LinkedList<Node> route_of_other, PriorityQueue<Node> queue, Set<Node> explored, PrintWriter writer)
    {
        int current_step = current.getState().getstep();

        //Conflict case #1
        //Robot 2 intends to move to the same position as Robot 1 at the same time
        //solution: we stall in the previous position with low possibility
        if(current.equals(route_of_other.get(current_step)))
        {
        	writer.println("***Conflict case 1 in position " + current + " at step " + current.getState().getstep());

            int stall_x = current.getOriginalParent().getState().getx();
            int stall_y = current.getOriginalParent().getState().gety();
            Node stall_original_parent = current.getOriginalParent().getOriginalParent();
                
            //we might want to visit this position again later
            explored.remove(current);	

            //No construction of new node needed
            //Instead we change the current one with a stall at the previous position
            current.getState().setx(stall_x);
            current.getState().sety(stall_y);
            current.setOriginalParent(stall_original_parent);
            current.f = Integer.MAX_VALUE;

            queue.add(current);

            return true;
        }

        //Conflict case #2
        //Robot 1 considers moving to the prev position of Robot 2 and
        //Robot 2 considers moving to the prev position of Robot 1
        //solution: replace this position with a step back with low possibility to be chosen
        else if(current.equals(route_of_other.get(current_step-1))  &&  current.getParent().equals(route_of_other.get(current_step)))
        {
        	writer.println("***Conflict case 2 in position " + current + " at step " + current.getState().getstep());

            if(current.getOriginalParent().getOriginalParent()!=null)
            {
                int go_back_x = current.getOriginalParent().getOriginalParent().getState().getx();
                int go_back_y = current.getOriginalParent().getOriginalParent().getState().gety();
                Node go_back_original_parent = current.getOriginalParent().getOriginalParent().getOriginalParent();
                
                //we might want to visit these positions again later
                explored.remove(current);
                List<Node> children = current.getOriginalParent().getOriginalParent().find_children(map, destination.getState().getx(), destination.getState().gety());
            	for(Node child : children)
            	{
            		explored.remove(child);
   				}
 

                //No construction of new node needed
                //Instead we change the current one with a step back
                current.getState().setx(go_back_x);
                current.getState().sety(go_back_y);
                current.setOriginalParent(go_back_original_parent);
                current.f = Integer.MAX_VALUE;

                queue.add(current);
            }
            //Special case: current node cant be replaced with a step back
            //Unavoidable collision 
            //Just close the path
            else
            {
            	//we might want to visit this position again later
                explored.remove(current);

                current.setNext(0);
                current.setChild1(null);
            }
            
            return true;
        }

        return false;

    }

}
