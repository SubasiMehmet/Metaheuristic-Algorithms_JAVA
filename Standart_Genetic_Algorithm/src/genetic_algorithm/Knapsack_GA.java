package genetic_algorithm;


import java.io.File;
import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Scanner;

public class Knapsack_GA {
	
	
	// Population Size
	public static int Population_Size = 60;  
	//Number of transmitted parents to directly next generation
	public static int number_of_transmitted_parents = 1; 
	public static double Mutation_Rate = 0.5;
	//Stop criteria
	public static int Generation_Number = 200; 
	
	
	public static Objects[] objects;
	public static int[][] Parents;
	public static int[][] OffSprings;
	public static int[] Best_Solution;
	
	
	public static void main(String[] args) {
		
							//Reading from values from text file
		Read_Knapsack_Information();
		
		objects = new Objects[Objects.Obj_Number+1];
		
		for(int i=1; i<Objects.Obj_Number+1;i++) {	
			objects[i] = new Objects();
		}
		
		for (int i=1;i<Objects.Obj_Number+1;i++) {
			objects[i].setData(i);
		}
		
		Parents = new int[Population_Size+1][Objects.Obj_Number+1];
		OffSprings = new int[Population_Size+1][Objects.Obj_Number+1];
		Best_Solution = new int [Population_Size+1];
		

								//Initializing parents randomly
		for(int i=1;i<Parents.length;i++) {
			Initialize_First_Parents(i);
		}	
								//Calculating fitness values of chromosomes
		for(int i=1;i<Parents.length;i++) {
			Calculate_Value(Parents[i]);
		}
						//Sorting population according to their fitness values
		Arrays.sort(Parents, (a, b) -> a[0] - b[0]);
		Best_Solution = Arrays.copyOf(Parents[Parents.length-1], Parents[Parents.length-1].length);
								
								//Initialize best sol'n
		System.out.print("Best Sol'n: ");
		for(int i=0;i<Best_Solution.length;i++) {
			System.out.print(Best_Solution[i] + " ");
		}System.out.println("\n\n");
		
		
			
		for(int i=1;i<Parents.length;i++) {
			System.out.println(Arrays.toString(Parents[i]));
		}
		
		int counter = 0;
		
		while (counter < Generation_Number) {
									
							//Calculate population's fitness values
			for(int i=1;i<Parents.length;i++) {
				Calculate_Value(Parents[i]);
			}
							//Sorting population according to their fitness values
			Arrays.sort(Parents, (a, b) -> a[0] - b[0]);
			
			
							//Control best sol'n
			if(Best_Solution[0]<Parents[Parents.length-1][0]) {
				Best_Solution = Arrays.copyOf(Parents[Parents.length-1], Parents[Parents.length-1].length);
			}
							
			CrossOver(Parents);		//CrossOver
			Mutation();				//Mutation
									//Rectify the chromosomes if they are not feasible
			Rectification(OffSprings);  
									//Assign the off-spring as next generation
			OffSpringstoParents();
			
			
			counter++;
		}

		
		for(int i=1;i<Parents.length;i++) {
			Calculate_Value(Parents[i]);
		}
						//Displaying
		Display(Parents);
		System.out.println("\nBest Solution: " +Arrays.toString(Best_Solution));
		
	}

	
	
								//Reading information function
	public static void Read_Knapsack_Information(){
		
		
		int counter = 0;
		
		try {				
		     File myObj = new File(Objects.test_File);
		     Scanner my_scan = new Scanner(myObj);
		    
		     while(counter < 2) {
		    	 if(counter == 0)
		    		Objects.Obj_Number = my_scan.nextInt(); 
		    	 else
		    		 Objects.Weight = my_scan.nextInt();
		     counter++;
		     }
		     my_scan.close();
		     
		} 
		catch (FileNotFoundException e) {
		   	System.out.println("An error occurred.");
		   	e.printStackTrace();
		}
				
	}
								
						//The function that initialize first parents randomly
						//this function consider feasibility
	public static void Initialize_First_Parents(int ind) {
		
		int[] rand_list = new int [Objects.Obj_Number+1];
	
		for(int i=1;i<Objects.Obj_Number+1;i++) {
			rand_list[i] = (int) (Math.random()*Objects.Obj_Number+1);
	    	for (int j=1;j<i;j++) {
	            if (rand_list[i] == rand_list[j]) {
	                i--; 
	                break;
	            }
	    	}
	    }
		
		for(int i=1;i<rand_list.length;i++) {
			if(Total_Weight(Parents[ind])<Objects.Weight) {
				Parents[ind][rand_list[i]] = 1;
						if(Total_Weight(Parents[ind])>Objects.Weight) {
							Parents[ind][rand_list[i]] = 0;
						}
			}
			
		}
		
		//Parents[ind][0] = Total_Weight(Parents[ind]);
		
	}

				//Controlling feasibility by checking weight of the knapsack
				//This function returns weight of the knapsack
	public static int Total_Weight(int[] my_array) {
		
		int weight = 0;
			
			for(int i=1;i<Objects.Obj_Number+1;i++) {
				weight += objects[i].weight*my_array[i];
			}
		
		return weight;
	}
	
				//The function calculate fitness value.
				//Fitness value is directly equal to value of the knapsack
	public static void Calculate_Value(int[] my_array) {
		
		int value = 0;
		
		for(int i=1;i<my_array.length;i++) {
			value+= objects[i].value*my_array[i];
		}
		
		my_array[0] = value;
		
	}

	
				//Cross-over function
	//This function selects two parants randomly from mating pool and
	//create random cross-over mask. And than cross them by using this mask.
	//Note: Mating pool is created by using Roulette-Whell
public static void CrossOver(int[][] my_parents) {
	
		int[] crossOver_Mask = new int [Objects.Obj_Number+1];
		
		int selected_parents_list[] = Roulette_Wheel(my_parents);
		ArrayList<Integer> selected_parents = new ArrayList<Integer>();
		
		
		for(int i=1;i<selected_parents_list.length;i++) {
			selected_parents.add(selected_parents_list[i]);
		}
		

		
		Collections.shuffle(selected_parents);
		
	//	System.out.println(Arrays.toString(selected_parents.toArray()));
		
		for(int i=1;i<crossOver_Mask.length;i++) {
			crossOver_Mask[i] =  (int) Math.round( Math.random());
		}
		

		if(Population_Size<6) {
			OffSprings[OffSprings.length-1] = Arrays.copyOf(Parents[Parents.length-1], Parents[Parents.length-1].length);
		}	
		else{
			for(int i=1;i<1+number_of_transmitted_parents;i++) {
				OffSprings[OffSprings.length-i] = Arrays.copyOf(Parents[Parents.length-i], Parents[Parents.length-i].length);
			}
		}
		

		if(Population_Size>=6) {
			for(int i=1;i<OffSprings.length-number_of_transmitted_parents;i++) {
				for(int k=1;k<OffSprings[i].length;k++) {
					OffSprings[i][k] = Parents[selected_parents.get(2*i-1)][k]*crossOver_Mask[k] + Parents[selected_parents.get(2*i)][k]*(1-crossOver_Mask[k]);
				}
			}
		}
		
		else {
			for(int i=1;i<OffSprings.length-1;i++) {
				for(int k=1;k<OffSprings[0].length;k++) {
					OffSprings[i][k] = Parents[selected_parents.get(2*i-1)][k]*crossOver_Mask[k] + Parents[selected_parents.get(2*i)][k]*(1-crossOver_Mask[k]);
				}
			}
		}
		
		
		
		
		
}	
	
				//Mutation function
	//Mutation is applied every gene of the chromosomes
	//by a specific ratio.
	//However, best chromosome of the generation is not subjected to mutation
	public static void Mutation() {
		
		double rand_mutation_rate = Math.random();
		
		for(int i=Population_Size-2;i>0;i--) {  
			for(int k=1;k<10;k++) {
				if(rand_mutation_rate<Mutation_Rate) {
					OffSprings[i][k] = 1 - OffSprings[i][k];
				}
			}
		}
		
	}
	
				//Roulette-Wheel

	//For mating pool, 2*N parents are selected. This selection
	//is executed by Roulette-Wheel. To select parents, Roulette-Wheel
	//is turned 2*N times. Selection ratio is directly proportional to
	//fitness values of the chromosomes.

	public static int[] Roulette_Wheel(int[][] my_array) {
		
	double [] ratio_array = new double [my_array.length];
	
	double sum = 0;
		for(int i=1;i<my_array.length;i++) {
			sum += my_array[i][0];
		}
		
		for(int i=1;i<ratio_array.length;i++) {
			ratio_array[i] = (1/sum)*my_array[i][0];
		}
		
		
		
		double [] Roulette_array = new double [Population_Size+1];
		for(int i=1;i<ratio_array.length;i++) {
			double m = 0;
			for(int j=1;j<i;j++) {
				m += ratio_array[j];
			}
			Roulette_array[i] = ratio_array[i] + m;
		}
		
		
		//
		
		int[] selection_array = new int[my_array.length*2+1];
		
		
		for(int i=1;i<selection_array.length;i++) {
			double selection_cursor = Math.random();
			for(int k=1;k<Roulette_array.length;k++) {
				if(selection_cursor>Roulette_array[k]) {}
				else if(selection_cursor<Roulette_array[k]) {
					selection_array[i] = k;
					break;
				}
			}
		}
		
		//System.out.println("\n" + Arrays.toString(selection_array));
		
		return selection_array;
		
		
	}

				//Rectify the chromosomes if they are not feasible
				//This function takes out one object randomly
				//and control feasibility. If it is not feasible still,
				//if take out one more object randomly. It continues...
	public static void Rectification(int[][] my_pop) {
		
		for(int i=1;i<my_pop.length;i++) {
			if(Total_Weight(my_pop[i])<Objects.Weight) {		
			}
			else {
				my_pop[i][(int) (Math.random()*Objects.Obj_Number+1)] = 0;
				i--;
			}
		}
	}
	
				//This function just diplay the arrays
	public static void Display(int[][] my_pop) {
		
		Arrays.sort(my_pop, (a, b) -> a[0] - b[0]);
		System.out.println("\n");
		for(int i=1;i<my_pop.length;i++) {
			System.out.println(Arrays.toString(my_pop[i]));
		}
		System.out.println("\n");
		
		
	}
	
				//The function that assigns off-springs as next generation
	public static void OffSpringstoParents() {
		
		for(int i=0;i<OffSprings.length;i++) {
			Parents[i] = Arrays.copyOf(OffSprings[i], OffSprings[i].length);
			Arrays.fill(OffSprings[i], 0);
		}
		
		
		
		
	}
		
}
