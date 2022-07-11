package tabu_search;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Quadratic_Assignment {

							//Matrix of Location Distance
							//It does not start from zero for convenience
 public static int[][] matrix_distance = new int [21][21];	
		 													
 							//Matrix of Department Flow
 							//It does not start from zero for convenience
 public static int [][] matrix_flow = new int[21][21];		
			  												
 
 public static ArrayList<String> Tabu_List = new ArrayList<String>();															
 							//Candidate List						
 public static int[][] all_next_solutions = new int[190][3]; 
 							//Incumbent Solution
 							//Last element is reserved for solution value
 public static int[] Incumbent_Solution = new int[22]; 		
 											
 public static String next_move = null;
 
 public static int Best_Solution;

	
	public static void main(String[] args) {
		
		Read_Matrix_Flow();
		Read_Matrix_Distance();
		int tabu_tenure = 13; 							//Tenure_length
		int stop_condition = 10000 ;						//Iteration Number
		
		
		int[] Best_Locations = new int[22];				//Best Solution
							//Last element is reserved for solution value
		
		for(int i=0;i<tabu_tenure;i++) {	//Initializing tabu_list
			Tabu_List.add("0");
		}
		
		for(int i=0;i<21;) {				//initial sol'n
			
			Incumbent_Solution[i] = i;	
			i++;;
		}
			
		
											//calculate solution value
		Incumbent_Solution[21] = Calculate_Solution(Incumbent_Solution);
		
											//display function
		diplay_Result(Incumbent_Solution, "Initial");
							//Firstly, Best and initial sol'n are same
		Best_Solution = Incumbent_Solution[21];	
			
			int counter = 0;				//Iteration Counter
			while(counter<stop_condition) {
				
				Best_Moves();	//Find all possible next moves
								//Cheking the tabu list and decide next move
								
								//Decided to change neighbors
				int [] next_move_array = new int[2];  
				decide_Next_Move(tabu_tenure, next_move_array);
												
				
				
										//Move
				do_Swap(next_move_array[0], next_move_array[1]);
										
							//Accept or not Incumbent Sol'n as Best Sol'n
				if(Incumbent_Solution[21] < Best_Solution) {
					System.arraycopy(Incumbent_Solution, 0, Best_Locations, 0, Incumbent_Solution.length);
					Best_Solution = Best_Locations[21];
				}
				
				
				
			counter++;
			}		
			diplay_Result(Best_Locations, "Best");
			
}
	
						//Solution value calculating
	public static int Calculate_Solution(int[] my_assignment) {
		
		int solution = 0;
		
		for(int i=1; i<21; i++) {
			for(int k=1;k<21;k++) {
				solution += matrix_distance[i][k] * matrix_flow[my_assignment[i]][my_assignment[k]];
			}
		}
		solution = solution/2;
		
		return solution;
	}
						//Reading Flow Matrix
	public static void Read_Matrix_Flow () {
		
		try {						//reading flow_matrix
		     File myObj = new File("Flow_Matrix.txt");
		     Scanner my_scan = new Scanner(myObj);
		     int i = 1, k = 1;
		     
		     while (my_scan.hasNextInt()) {
		       matrix_flow[i][k] = my_scan.nextInt();
		       if(k%20==0) {
		       	i++;
		       	k = 1; 
		       }
		       else {
		       	k++; 	
		       }
		     }
		     my_scan.close();
		     
		} 
		catch (FileNotFoundException e) {
		   	System.out.println("An error occurred.");
		   	e.printStackTrace();
		}
		
	}
						//Reading Distance Matrix
	public static void Read_Matrix_Distance () {
		try {
		      File myObj = new File("Distance_Matrix.txt");
		      Scanner my_scan = new Scanner(myObj);
		      int i = 1, k = 1;
		      
		      while (my_scan.hasNextInt()) {
		        matrix_distance[i][k] = my_scan.nextInt();
		        if(k%20==0) {
		        	i++;
		        	k = 1; 
		        }
		        else {
		        	k++; 	
		        }
		      }
		      my_scan.close();
		      
		} 
		catch (FileNotFoundException e) {
		    	System.out.println("An error occurred.");
		    	e.printStackTrace();
		}	
		
	}
						//Find all next moves and sort them DESC
	public static void Best_Moves() {
		
		int count = 0;
		int[] Incumbent = new int[22];
		System.arraycopy(Incumbent_Solution, 0, Incumbent, 0, Incumbent_Solution.length);
		for(int i=1;i<21;i++) {
			for(int k=i+1;k<21;k++) {	
				Swap_Moves(i,k,Incumbent,count);
				count++;
			}	
		}
		
		Arrays.sort(all_next_solutions, (a, b) -> a[0] - b[0]);
		
		
	}
						//Swap 2-point and calculate new sol'n value as virtual
	public static void Swap_Moves(int i, int k, int[] Incumbent, int count) {
		
		 int[] Incumbent1 = new int[22];
		 System.arraycopy(Incumbent, 0, Incumbent1, 0, Incumbent.length);
		 int aux;
		 aux = Incumbent1[i];
		 Incumbent1[i] = Incumbent1[k];
		 Incumbent1[k] = aux;
		 
		 all_next_solutions[count][0] = Calculate_Solution(Incumbent1) - Incumbent_Solution[21];
		 all_next_solutions[count][1] = i;
		 all_next_solutions[count][2] = k;
		 
	}
						//Decide next move by searching tabu_list
	public static void decide_Next_Move(int tabu_tenure, int [] next_move_array) {
		
		int tabu_counter = 0;
		int candidate_iterator = 0;
		
		while(tabu_counter<tabu_tenure) {
			
			next_move = all_next_solutions[candidate_iterator][1] + ", " + all_next_solutions[candidate_iterator][2];
										
			for(int i = tabu_tenure-1;i>-1;i--) {
				
				if(next_move.equals(Tabu_List.get(i))) {
														//Aspiration Criteria
					int [] trial_next_solution = new int[22];
					System.arraycopy(Incumbent_Solution, 0, trial_next_solution, 0, Incumbent_Solution.length);
					int aux;
					aux = trial_next_solution[all_next_solutions[candidate_iterator][1]];
					trial_next_solution[all_next_solutions[candidate_iterator][1]] = trial_next_solution[all_next_solutions[candidate_iterator][2]];
					trial_next_solution[all_next_solutions[candidate_iterator][2]] = aux; 
					trial_next_solution[21] = Calculate_Solution(trial_next_solution);
										
					if(Best_Solution > trial_next_solution[21]) {
						tabu_counter = tabu_tenure+1;
						break;
					}			
					
					candidate_iterator++;
					
					tabu_counter = 0;
					break;
				}
				tabu_counter++;	
			}
			
		}
		
		Pattern p = Pattern.compile("\\d+");
		   Matcher m = p.matcher(next_move);
		   int l = 0;
		   while(m.find()) {       
		        next_move_array[l] = Integer.parseInt(m.group());
		        l++;
		   }
		
		
	}
						//Execute move
	public static void do_Swap(int i, int k){
		
		int aux;
		aux = Incumbent_Solution[i];
		Incumbent_Solution[i] = Incumbent_Solution[k];
		Incumbent_Solution[k] = aux; 
		Incumbent_Solution[21] = Calculate_Solution(Incumbent_Solution);
		
		Tabu_List.add(i+", " + k);
		Tabu_List.remove(0);
		
		
	}
						//Displaying present solution
	public static void diplay_Result(int [] my_array, String array_name) {
		
		System.out.print(array_name + " Locations: ");
		for(int i=1;i<21;i++) {
			System.out.print(my_array[i] + " ");
		}
		System.out.println("");
		System.out.println(array_name + "_Solution: " + my_array[21]);
		
	}

	
}
	

