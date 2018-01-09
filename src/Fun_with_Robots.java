import java.util.*;
import java.io.*;

public class Fun_with_Robots
{
	public static void main(String args[]) 
	{
		PrintWriter writer;
		long startTime = System.nanoTime();
		try
		{
			writer = new PrintWriter(args[1], "UTF-8");
		}
		catch(FileNotFoundException e) 
		{
			return;
		}
		catch(UnsupportedEncodingException t)
		{
			return;
		}
		long[] node_counter = {0};

		writer.println("**Team:  Rissaki Agapi - Nikos Tziavelis**");
		writer.println("**  Results of input file: " + args[0] + "   **\n");
		Node init_state1, init_state2, init_node, stall, home1, home2, temp;
		LinkedList<Node> targets = new LinkedList<Node>(); //the last element of the list is the final target
		Astar astar;
		LinkedList<Node> route1 = null, route2 = null;

		init_state1 = new Node(0, 0, 0, 0, null, null, 0);
		init_state2 = new Node(0, 0, 0, 0, null, null, 0);
		char[][] map = input(args[0], init_state1, init_state2, targets);

		route1 = new LinkedList<Node>();
		route2 = new LinkedList<Node>();
	
		//for robot 1
		init_node = init_state1;
		for(Node current_target : targets)
		{
			astar = new Astar(init_node, current_target, map);
			if(!init_node.equals(current_target)) 
			{
				init_node = astar.search(route2, writer, node_counter);
				if(init_node==null)
				{
					writer.println("Robot 1 failed to reach target: " + current_target);
					writer.close();
					return;
				}
			}
			else continue;
		}
		route1 = construct_path(init_node);

		
		//for robot 2
		init_node = init_state2;
		for(Node current_target : targets)
		{
			astar = new Astar(init_node, current_target, map);
			if(!init_node.equals(current_target)) 
			{
				init_node = astar.search(route1, writer, node_counter);
				if(init_node==null)
				{
					writer.println("Robot 2 failed to reach target: " + current_target);
					writer.close();
					return;
				}
			}
			else continue;
		}
		//now init_node points at the final target = meeting point
		route2 = construct_path(init_node);

		//arrange meeting
		int len1 = route1.size();
		int len2 = route2.size();
		int stall_x = init_node.getState().getx(), stall_y = init_node.getState().gety(), stall_step;
		int stall_g, stall_h;


		if(len1 > len2)
		{
			//Robot2 arrives first->Stall until Robot1 arrives next to the final position
			//Careful! Robot1 stops next to the final position so we remove the last node from route1
			route1.removeLast();
			stall = route2.getLast();
			stall_step = stall.getState().getstep();
			stall_g = stall.getG();
			stall_h = stall.getH();
			while(len2 < len1 - 1)
			{
				stall_step++;
				stall = new Node(stall_x, stall_y, stall_g, stall_h, stall, null, stall_step);
				route2.addLast(stall);
				len2++;
			}
		}
		else if(len1==len2)
		{
			//Both Robots arrive at the same time
			//All we have to do is make Robot1 wait next to the final position
			route1.removeLast();
			stall = route1.getLast();
			stall_step = stall.getState().getstep();
			stall_g = stall.getG();
			stall_h = stall.getH();
			stall = new Node(stall_x, stall_y, stall_g, stall_h, stall, null, stall_step+1);
			route1.addLast(stall);
		}
		else
		{
			//Robot1 arrives first->Stall until Robot2 arrives next to the final position
			//Careful! Robot2 stops next to the final position so we remove the last node from route2
			route2.removeLast();
			stall = route1.getLast();
			stall_step = stall.getState().getstep();
			stall_g = stall.getG();
			stall_h = stall.getH();
			while(len1 < len2 - 1)
			{
				stall_step++;
				stall = new Node(stall_x, stall_y, stall_g, stall_h, stall, null, stall_step);
				route1.addLast(stall);
				len1++;
			}
		}



		writer.println("\n**Now Returning Home**");


		//A* for returning home
		LinkedList<Node> empty = new LinkedList<Node>(); //Robot 1 searches freely
		astar = new Astar(route1.getLast(), init_state1, map);
		if(!route1.getLast().equals(init_state1)) 
		{
			home1 = astar.search(empty, writer, node_counter);
			route1 = construct_path(home1);
		}

		astar = new Astar(route2.getLast(), init_state2, map);
		if(!route2.getLast().equals(init_state2)) 
		{
			home2 = astar.search(route1, writer, node_counter);
			route2 = construct_path(home2);
		}


		//print final routes
		writer.println("\n");
		writer.println("Final Path of Robot 1:");
		temp = null;
		for(Node to_print : route1)
		{
			if (temp!=null && temp.equals(to_print)) writer.println("Robot1 stalls at " + to_print + " at step " + to_print.getState().getstep() );
			else writer.println("Robot1 moves at " + to_print + " at step " + to_print.getState().getstep() );
			temp = to_print;
		}

		writer.println("\n");
		writer.println("\nFinal Path of Robot 2:");
		temp = null;
		for(Node to_print : route2)
		{
			if (temp!=null && temp.equals(to_print)) writer.println("Robot2 stalls at " + to_print + " at step " + to_print.getState().getstep() );
			else writer.println("Robot2 moves at " + to_print + " at step " + to_print.getState().getstep() );
			temp = to_print;
		}


		writer.println("\n");
		writer.println("# of Nodes opened during Search = " + node_counter[0]);

		long endTime = System.nanoTime();
		long duration = (endTime - startTime)/1000000;	//time in ms
		writer.println("Terminated after " + duration + "msecs");

		writer.close();

		return;
	}



	public static LinkedList<Node> construct_path(Node current)
    {
        LinkedList<Node> path = new LinkedList<Node>();

        path.addFirst(current);

        while(current.getParent()!=null)
        {
            current = current.getParent();
            path.addFirst(current);
        }

        return path;
    }



	private static char[][] input(String filename, Node init_state1, Node init_state2, LinkedList<Node> targets)
	{
		try
		{
			int mapX, mapY, x, y, n;
			Node temp_target;

			//read map dimensions
			BufferedReader in = new BufferedReader ( new FileReader (filename));
			String line = in.readLine ();
			String [] a = line.split (" ");
			mapX = Integer.parseInt(a[0]);
			mapY = Integer.parseInt(a[1]);

			char[][] map = new char[mapX+2][mapY+2];

			//read initial position of robot 1
			line = in.readLine ();
			a = line.split (" ");
			x = Integer.parseInt(a[0]);
			y = Integer.parseInt(a[1]);
			init_state1.getState().setx(x);
			init_state1.getState().sety(y);

			//read initial position of robot 2
			line = in.readLine ();
			a = line.split (" ");
			x = Integer.parseInt(a[0]);
			y = Integer.parseInt(a[1]);
			init_state2.getState().setx(x);
			init_state2.getState().sety(y);

			//read final target
			line = in.readLine ();
			a = line.split (" ");
			x = Integer.parseInt(a[0]);
			y = Integer.parseInt(a[1]);
			temp_target = new Node(x, y, 0, 0, null, null, 0);
			targets.addFirst(temp_target);

			//read number of intermediate targets
			line = in.readLine ();
			n = Integer.parseInt(line);

			//read the rest of the targets and add them to list
			for (int i = 1; i <= n; i++)
			{
				line = in.readLine ();
				a = line.split (" ");
				x = Integer.parseInt(a[0]);
				y = Integer.parseInt(a[1]);
				temp_target = new Node(x, y, 0, 0, null, null, 0);
				targets.addFirst(temp_target);	
			}


			//mark end of map with 'X'
			for (int i = 0; i <= mapX+1; i++)
			{
				map[i][0] = 'X';
				map[i][mapY+1] = 'X';
			}
			for (int i = 0; i <= mapY+1; i++)
			{
				map[0][i]= 'X';
				map[mapX+1][i] = 'X';
			}

			for(int i = 1; i<=mapX; i++)
			{
				line = in.readLine ();
				for(int j = 1; j<=mapY; j++)
				{
					map[i][j] = line.charAt(j-1);
				}
			}

			in. close ();
			return map;
		}
		catch ( IOException e) 
		{
			e.printStackTrace ();
			return new char [1][1];
		}
	}


}