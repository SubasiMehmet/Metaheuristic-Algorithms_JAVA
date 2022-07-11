package simulated_anneling;



public class Quadratic_Assignment {

	
 public static int[][] matrix_distance = {		//Matrix of Location Distance
			  {},								//It does not start from zero for convenience
			  {0, 0, 1, 1, 2, 3},
			  {0, 1, 0, 2, 1, 2},
			  {0, 1, 2, 0, 1, 2},
			  {0, 2, 1, 1, 0, 1},
			  {0, 3, 2, 2, 1, 0}};
	
 public static int[][] matrix_flow = {			//Matrix of Department Flow
			  {},								//It does not start from zero for convenience
			  {0, 0, 5, 2, 4, 1},
			  {0, 5, 0, 3, 0, 2},
			  {0, 2, 3, 0, 0, 0},
			  {0, 4, 0, 0, 0, 5},
			  {0, 1, 2, 0, 5, 0}};
 

	
	public static void main(String[] args) {
		
		double starting_temperature = 10;
		double temperature = 10;				// Starting temperature value
		double cooling_Rate = 0.98;		
												// Cooling Schedule (temperature = temperature*cooling_Rate)
		
		

		int[] Incumbent_Solution = {0, 4, 2, 3, 1, 5};		//Initial Sol'n
		int[] Next_Solution = new int[6];					//Neighbour of incumbent sol'n
		int[] Best_Solution = new int[6];					//Memory of best Sol'n
		int sub_counter = 0;		
											//The value allowing the number of operations before cooling
		
											//Assignment of initial best and next sol'n 
		for(int i=1;i<6;i++) {							
			Next_Solution[i] = Incumbent_Solution[i];
			Best_Solution[i] = Incumbent_Solution[i];
		}
		
											//As a neihbourhood Relationship, 2-point swap method is used. 
											//So that 2 different random variables are selected between 1-5.
		int Rand1, Rand2;								
		int auxiliary_var;
		
		int Best_Solution_Value = Calculate_Solution(Best_Solution); 
											//initialize best_solution_value
		
											//Printing initial solution
		System.out.println("Initial Solution Set: ");
		
		for(int i=1;i<6;i++) {
			System.out.println("for Location " + i + " -> Deparment " + Best_Solution[i] + " ");
		}
		System.out.println("Solution Value: " + Best_Solution_Value + "\n");
		
		
		
		while (temperature >= 1) {			//stop condition is temperature < 1
									
											//selecting 2 different random variable between 1-5
			Rand1 = (int) (Math.random()*5+1); 
			do {							
				Rand2 = (int) (Math.random()*5+1);
			}while (Rand1==Rand2);			
			
			
									//Find neighbour (next solution) by swapping 2-point on incumbent sol'n
			auxiliary_var = Next_Solution[Rand2]; 
			Next_Solution[Rand2] = Next_Solution[Rand1];
			Next_Solution[Rand1] = auxiliary_var;
			
								//Print steps to follow
/*			System.out.println("Next Sol'n: " + Calculate_Solution(Next_Solution) + "   Incumbent Sol'n: " +  Calculate_Solution(Incumbent_Solution)
							  +"   Best Sol'n: " +  Calculate_Solution(Best_Solution));
*/			
			
								// Decide if change incumbent to next sol'n
								//If next sol'n is better, change incumbent sol'n
			if(Calculate_Solution(Next_Solution)<Calculate_Solution(Incumbent_Solution)){
				for(int i=1;i<6;i++) {
					Incumbent_Solution[i] = Next_Solution[i]; 
			}
				
		//If Incumbent sol'n is better than  all past memory, change best sol'n
				
				if(Calculate_Solution(Next_Solution)<Calculate_Solution(Best_Solution)) {
					Best_Solution_Value = Calculate_Solution(Incumbent_Solution);
					for(int i=1;i<6;i++) {
						Best_Solution[i] = Incumbent_Solution[i];
					}
					
				}
				
				
			}
			else{								//Control the temperature and decide if accept uphill move 
				if (temperature < Math.random()*starting_temperature+1) {

				}
				else {
					
					for(int i=1;i<6;i++) {
						Incumbent_Solution[i] = Next_Solution[i];
					}
				}
			}
			
												//allow 5 operations before cooling 
			if (sub_counter == 5) {
				temperature = temperature *cooling_Rate;  
				sub_counter = 0;
			}
												//temperature cooling after 5 iterations
			
			sub_counter++;
		}
		

												//Printing sol'n
		System.out.println("Solution Set: ");
		
		for(int i=1;i<6;i++) {
			System.out.println("for Location " + i + " -> Deparment " + Best_Solution[i] + " ");
		}
		System.out.println("Solution Value: " + Best_Solution_Value);
	

}
	
												//Solution value calculating
	public static int Calculate_Solution(int[] my_assignment) {
		
		int solution = 0;
		
		for(int i=1; i<6; i++) {
			for(int k=1;k<6;k++) {
				solution += matrix_distance[i][k] * matrix_flow[my_assignment[i]][my_assignment[k]];
			}
		}
		solution = solution/2;
		
		return solution;
	}
	
	
	
}
