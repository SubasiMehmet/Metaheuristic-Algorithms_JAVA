import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

/*NOTLAR:
 * div deðiþkeni "false" olarak atanýrsa ve initializer_set_size = 0 olarak atanýrsa alt gruplara bölme iþlemi iptal edilir.
 * 
 * extended_Generation_Number = 0 ve extend_Pop_Size = 1 olarak atanýrsa popülasyon geniþletme iþlemi iptal edilir.
 * 
 * Aircraft.java class'ýnda 8. satýr "test_File" deðiþkeni "airland1.txt", "airland2.txt", "airland3.txt", "airland4.txt", "airland5.txt",
 * "airland8.txt", "airland9.txt", "airland10.txt", "airland11.txt", "airland12txt", "airland13.txt"
 * 
 * Okumasý yapýlan data setleri proje içerisinde mevcuttur.
 * 
 */



public class aircraft_landing {
	
		
	public static int[][] Parents;
	public static int[][] OffSprings;
							//Ana algoritma için popülasyon sayýsý
	public static int Population_Size = 50;
	public static int [] Best_Solution;
	public static int [] landing_times;
								
							//Yeni jenerasyona direkt aktarýlacak birey sayýsý
	public static int Parent_Transfer_Number = 5;  
							
	
							//Mutasyon Oraný
	public static double Mutation_Rate = 0.2;
							//Jenerasyon Sayýsý, durma kriteri
	public static int Generation_Number = 10000;
							//Geniþletilmiþ GA için jenerasyon Sayýsý, durma kriteri
	public static int extended_Generation_Number = Generation_Number/100;
	
							//BigM deðeri
	public static int  bigM = 1000000;
							//GA için geniþletme oraný
	public static int extend_Pop_Size = 5;
	
	public static Aircraft aircrafts[];
	
	public static int[] ordered_parent_list;
	
	public static int[][] target_list;
	
							//Ana grubun kaç alt gruba bölündüðünü tutan deðiþken
	public static int division;
	public static boolean div = true;
	
							//Alt grup popülasyonlarý
	public static int[][][] Parent_Divisions;	
	public static int[][][] OffSpring_Divisions;
							//Alt gruplar için popülasyon sayýsý
	public static int initializer_set_size = 20;
							//Alt gruplar için yeni jenerasyona direkt aktarýlacak birey sayýsý
	public static int initilizer_parent_transfer = 2;
							//Alt gruplar için jenerasyon sayýsý, durma kriteri
	public static int initilizer_generation_number =  Generation_Number/10 ;
	


	
	
	public static void main(String[] args) {
		
				
		
						//Algoritma Süre Baþlangýcý
		long tic =  System.nanoTime();
		 
		
						
		int temp_ts;
		temp_ts = Parent_Transfer_Number;
		Parent_Transfer_Number = initilizer_parent_transfer;
		
						//Data Seti okunur
		Read_Fixed_Information();

		
				
						//Hedef süreleri tutulur
		landing_times = new int [Aircraft.number_of_aircrafts];
		
		int counter = 0;
						//Uçak sayýsý için diziler ve objeler oluþturulur
		System.out.println("Number of Aircrafts: " + Aircraft.number_of_aircrafts);
		System.out.println("Parents Number: " + Population_Size);
		
		Parents = new int [Population_Size+1][Aircraft.number_of_aircrafts+1];
		OffSprings = new int [Population_Size+1][Aircraft.number_of_aircrafts+1];
		Best_Solution = new int [Population_Size+1];
		ordered_parent_list = new int[Aircraft.number_of_aircrafts+1];
		
		target_list = new int[Aircraft.number_of_aircrafts+1][1];
		
		
		

										//Tüm data seti okunur
		aircrafts = new Aircraft[Aircraft.number_of_aircrafts+1];
		
		for(int i=1; i<Aircraft.number_of_aircrafts+1;i++) {	
			aircrafts[i] = new Aircraft();
		}
								
		for (int i=1;i<Aircraft.number_of_aircrafts+1;i++) {
			aircrafts[i].setData(i-1);
		}
		
		for (int i=1;i<Aircraft.number_of_aircrafts+1;i++) {
			aircrafts[i].setDataList(i-1);
		}
		
		
		
		for(int i=1;i<target_list.length;i++) {
			target_list[i][0] = aircrafts[i].target_time;
		}
		
		
										//Uçaklarýn hedef iniþ süreleri sýralanýr
		Arrays.sort(target_list, (a, b) -> a[0] - b[0]);
		
						
										//Hedef iniþ sürelerine göre kaç alt gruba bölüneceðine karar verilir.
		if(div == true)
			division = (int) Aircraft.number_of_aircrafts/10;    
		else
			division = 0;
		
		for(int i=0;i<Aircraft.number_of_aircrafts+1;i++) {
			ordered_parent_list[i] = i;
		}
		
		
		
		int max_target_time = target_list[target_list.length-1][0];			
			
		
								
		ArrayList<ArrayList<Integer>> pr_dv = new ArrayList<>(division);
		
		
		for(int i=0; i < division; i++) {
			pr_dv.add(new ArrayList<Integer>());
		}


			
										//Hedef iniþ sürelerine göre alt grup atamalarý / arraylist atamasý
		for(int j=1;j<division+1;j++) {
			for(int k=1;k<Parents[0].length;k++){
				if( (aircrafts[k].target_time > ((double)(max_target_time)/(double)(division))*(j-1)) && (aircrafts[k].target_time <= ((double)(max_target_time)/(double)(division))*j)) {
					pr_dv.get(j-1).add(k);
				}	
			}
		}
		
		
		
										//Alt grup dizileri
		int [][][] b_parent_div;
		int [][][] b_offspring_div;
		
		
		
		b_parent_div = new int[division+1][initializer_set_size+1][1];
		b_offspring_div = new int[division+1][initializer_set_size+1][1];
		
		
									// Alt grup arrayleri initializion
		for(int j=1;j<b_parent_div.length;j++) {
			for(int k=1; k<b_parent_div[j].length;k++) {
				b_parent_div[j][k] = new int[pr_dv.get(j-1).size()+1];
				b_offspring_div[j][k] = new int[pr_dv.get(j-1).size()+1];
			}
		}
		
		
		
									// Alt grup atamalarý / Arraylist to array
			for(int j=1;j<division+1;j++) {
				for(int i=1;i<b_parent_div[j].length;i++) {
					for(int k=1;k<b_parent_div[j][i].length;k++) {
						b_parent_div[j][i][k] = pr_dv.get(j-1).get(k-1);
					}
				}
			}


			
									// Arraylarin karýlmasý ve uygunluk deðeri hesaplarý
		for(int i=1;i<division+1;i++) {
			for(int j=1; j<b_parent_div[i].length;j++) {
				Calculate_Solution(shuffleArray(b_parent_div[i][j]));
			}
		}
		
									// Uygunluk deðerlerine göre sýralama
		for(int i=1;i<division+1;i++) {
				Arrays.sort(b_parent_div[i], (a, b) -> a[0] - b[0]);
			}

		
									//Alt gruplar için genetik algoritma
		int sub_counter = 0;
		while(sub_counter<initilizer_generation_number) {		
			
			for(int i=1;i<division+1;i++) {
				for(int j=1; j<b_parent_div[i].length;j++) {
					Calculate_Solution(shuffleArray(b_parent_div[i][j]));
				}
			}
			
			for(int i=1;i<division+1;i++) {
				Arrays.sort(b_parent_div[i], (a, b) -> a[0] - b[0]);
			}
			

			
			for(int i=1;i<division+1;i++) {
				CrossOver(b_parent_div[i], b_offspring_div[i]);			//CrossOver
			}
			
			
			for(int i=1;i<division+1;i++) {
				Mutation(b_offspring_div[i]);			//CrossOver
			}
			
			for(int i=1;i<division+1;i++) {
				if(counter != (Generation_Number/100)-1)
					OffSpringstoParents(b_offspring_div[i],b_parent_div[i]);
			}
			
			
			
			
			sub_counter++;
			
			
			
		}			//Alt gruplar için genetik algoritma sonu
		
	
	
								//Alt gruplarýn birleþtirilmesi
		int size_of_div = 0;
		int aux_size = 0;
		for(int i=1;i<b_parent_div.length;i++) {		
			for(int j=1;j<b_parent_div[i].length;j++) {		
				for(int k=1;k<b_parent_div[i][j].length;k++) { 
					Parents[j][(size_of_div)+k] = b_parent_div[i][j][k];
				}
				aux_size = b_parent_div[i][j].length;
			}
			size_of_div += aux_size-1;
		}
		


		
		
		
		Parent_Transfer_Number = temp_ts;
				
									//Ana grup için kalan bireyler random initialize edilir
		Initialize_Parents(Parents);

		
		
		
		
									//Bireylerin uygunluk deðerleri bulunur
		for(int i=1; i<Parents.length;i++) {
			Calculate_Solution(Parents[i]);
		}
		
		
									//Bireyler sýralanýr
		Arrays.sort(Parents, (a, b) -> a[0] - b[0]);
									
		//Display(Parents);
		
									//En iyi birey tutulur
		Best_Solution = Arrays.copyOf(Parents[1], Parents[1].length);
		
		System.out.print("Best Initial Sol'n: ");
		for(int i=0;i<Best_Solution.length;i++) {
			System.out.print(Best_Solution[i] + " ");
		}System.out.println("\n\n");
		

		
		
		int [] solution_list = {-1, 0};
		int solution_counter = 0;
		
									//Ana genetik algoritma kodu
		while(counter < Generation_Number) {	

			
			for(int i=1; i<Parents.length;i++) {
				Calculate_Solution(Parents[i]);
			}
			Arrays.sort(Parents, (a, b) -> a[0] - b[0]);
			
										
									//En iyi çözüm tutulur
			if(Best_Solution[0]>Parents[1][0]) { 
				Best_Solution = Arrays.copyOf(Parents[1], Parents[1].length);
			}
		
			
			
									//En iyi çözümde belirli bir döngüdür iyileþme yoksa jenerasyon sayýsý geniþletilir
									//ayrý bir iç döngü baþlar
									
			if(solution_counter%(Generation_Number/100) == 0) {
				solution_list[0] = solution_list[1];
				solution_list[1] = Best_Solution[0];
			}	
									//Rastgele bireylerle popülasyonun geniþletilmesi ve bireylerin initialize edilmesi
				if(solution_list[0] == solution_list[1]) {
					Population_Size = Population_Size*extend_Pop_Size;
					int[][] aux_Parents = new int [Population_Size+1][Aircraft.number_of_aircrafts+1];
					int[][] aux_OffSprings = new int [Population_Size+1][Aircraft.number_of_aircrafts+1];
					int[] aux_Best_Solution = new int [Population_Size+1];
					
					Arrays.fill(aux_Best_Solution, bigM);
					
					Initialize_Parents(aux_Parents);
					for(int i = 0;i<Parents.length;i++) {
						aux_Parents[i] = Arrays.copyOf(Parents[i], Parents[i].length);
					}
				
									//Geniþlemiþ popülayonda genetik algoritma kodu.
				int counter_inside = 0;
				while(counter_inside < Generation_Number/100) {	//Durma kriteri
		
								
					//Inside
					CrossOver(aux_Parents, aux_OffSprings);			//CrossOver
					
					Mutation(aux_OffSprings);					//Mutation
					
					
					if(counter != Generation_Number-1)
						OffSpringstoParents(aux_OffSprings,aux_Parents);		//Yeni jenerasyon atamasý
						
					
					for(int i=1; i<aux_Parents.length;i++) {
						Calculate_Solution(aux_Parents[i]);						//Uygunluk deðeri hesaplamalarý
					}
					

					Arrays.sort(aux_Parents, (a, b) -> a[0] - b[0]);			//Popülasyon sýralamasý

					
																//Eðer en iyi çözümde bir geliþme olursa döndüden hemen çýkýlýr
																// ve popülasyon eski haline getirilir.
					if(aux_Best_Solution[0]>aux_Parents[1][0]) { 
						aux_Best_Solution = Arrays.copyOf(aux_Parents[1], aux_Parents[1].length);
					}
					
					if(aux_Best_Solution[0]!=solution_list[1]) {

						break;
					}
					
					counter_inside++;
				}						//Geniþletilmiþ popülasyon ile algoritma sonu / döngü sonu
										//Popülasyonun eski haline getirilmesi ve kötü bireylerin popülasyondan atýlmasý
				for(int i=0;i<Parents.length;i++) {
					Parents[i] = Arrays.copyOf(aux_Parents[i], aux_Parents[i].length);
				}
				
										
				Population_Size = Population_Size/extend_Pop_Size;
				solution_list[0] = -1;
				solution_list[1] = 0;
				
			}
				
				
			

													//Ana algoritmaya devam edilir
			CrossOver(Parents, OffSprings);			//CrossOver
			
			Mutation(OffSprings);					//Mutation
			
			
			
			if(counter != Generation_Number-1)		//Yeni jenerasyon atamasý
				OffSpringstoParents(OffSprings,Parents);
			
			counter++;
			solution_counter++;
			
			

		}		//Ana döngü sonu 
		
		
													//Bulunan deðerlerin ekranda gösterilmesi
		System.out.println("\nBest Solution: " +Arrays.toString(Best_Solution));
		Calculate_Solution(Best_Solution);
		System.out.println("\nLanding Times: " +Arrays.toString(landing_times));
		
		long toc = System.nanoTime();
		long tic_toc = toc - tic;
		System.out.println("\n\n\nRunning Time: " + tic_toc*Math.pow(10,-6) + " milliseconds\n\n\n");
		
		System.out.println("\n\n------FINISH------\n\n\n");

		
	}							
	
					
	
				//Bilgilerin okunmasý
public static void Read_Fixed_Information () {
	
	int counter = 0;
	
	try {				
	     File myObj = new File(Aircraft.test_File);
	     Scanner my_scan = new Scanner(myObj);
	    
	     while(counter < 2) {
	    	 if(counter == 0)
	    		Aircraft.number_of_aircrafts = my_scan.nextInt(); 
	    	 else
	    		 Aircraft.freeze_time = my_scan.nextInt();
	     counter++;
	     }
	     my_scan.close();
	     
	} 
	catch (FileNotFoundException e) {
	   	System.out.println("An error occurred.");
	   	e.printStackTrace();
	}
	
	
	
	
	
	
}

				//Alt grup ile atamasý yapýlmayan bireylerin initialize edilmesi
public static void Initialize_Parents(int[][] my_array) {
	
	
	for(int i=initializer_set_size+1;i<my_array.length;i++) {
		my_array[i] = Arrays.copyOf(shuffleArray(ordered_parent_list),ordered_parent_list.length);
	}
		
}
							
					//Uygunluk deðeri hesaplamalarý ve iniþ zamaný atamalarý
public static void Calculate_Solution(int[]my_assignment) {


	int [] landing_time = new int[my_assignment.length];
	double [] fit_values_for_each = new double[my_assignment.length];
	landing_time[0] = 0; 

	
	for(int i=1;i<my_assignment.length;i++) {
		
		landing_time[i] = aircrafts[my_assignment[i]].appearence_time;
		
		
		if(landing_time[i] < landing_time[i-1] + aircrafts[my_assignment[i]].separation_time[i-1]) 
			landing_time[i] = landing_time[i-1] + aircrafts[my_assignment[i]].separation_time[i-1];
		
		

		if(landing_time[i]<aircrafts[my_assignment[i]].target_time) {
			landing_time[i] += (aircrafts[my_assignment[i]].target_time - landing_time[i]);
			
		}
		
		
		
		
		
		if(landing_time[i]>aircrafts[my_assignment[i]].target_time) {
			fit_values_for_each[i] = (landing_time[i] - aircrafts[my_assignment[i]].target_time)*aircrafts[my_assignment[i]].penalty_after;
		}
		else if(landing_time[i]<aircrafts[my_assignment[i]].target_time) {
			fit_values_for_each[i] = (aircrafts[my_assignment[i]].target_time - landing_time[i])*aircrafts[my_assignment[i]].penalty_before;
		}
		else if(landing_time[i]==aircrafts[my_assignment[i]].target_time) {
			fit_values_for_each[i] = 0;
		}
		
		
								
		if(landing_time[i]>aircrafts[my_assignment[i]].latest_time) {
			fit_values_for_each[i] = bigM;
		}
		else if (landing_time[i]<aircrafts[my_assignment[i]].earliest_time) {
			fit_values_for_each[i] = bigM;
		}
		
		
			
			
	}
		
	my_assignment[0] = 0;
	double assign = 0;
	for(int i=1;i<my_assignment.length;i++) {
		assign += fit_values_for_each[i];
	}
	my_assignment[0] = (int) assign;
	
	landing_times = Arrays.copyOf(landing_time, landing_time.length);
	
	
	
}
		

					//Order Cross-Over ile yeni bireylerin yaratýlmasý, Mating Pool'dan bireyler rastgele çekilir
public static void CrossOver(int[][] my_parents, int[][] my_offsprings) {
	
	
	int selected_parents_list[] = Roulette_Wheel(my_parents);
	shuffleArray(selected_parents_list);
	
	
	
	int cutpoint1, cutpoint2;
	ArrayList<Integer> Offspring_Transfer1 = new ArrayList<Integer>();
	ArrayList<Integer> Offspring_Transfer2 = new ArrayList<Integer>();
	Random random = new Random();
	

	
	
	for(int i=1;i<my_parents.length;i++) {
		my_offsprings[i] = Arrays.copyOf(my_parents[i], my_parents[i].length);
		
	}
	
	
	for(int i=Parent_Transfer_Number+1;i<my_parents.length-1;i++) {
		cutpoint1 = 1+ random.nextInt(my_parents[0].length);
		cutpoint2 = 1+ random.nextInt(my_parents[0].length); 
		while(cutpoint2<cutpoint1) {
			cutpoint2 = 1+ random.nextInt(my_parents[0].length);
		}
		
		for(int k=cutpoint1;k<cutpoint2;k++) {
			my_offsprings[i][k] = my_parents[selected_parents_list[i]][k];
			my_offsprings[i+1][k] = my_parents[selected_parents_list[i+1]][k];
		}
		
		for(int k=cutpoint2;k<my_offsprings[0].length;k++) {
			Offspring_Transfer1.add(my_parents[selected_parents_list[i]][k]);
			Offspring_Transfer2.add(my_parents[selected_parents_list[i+1]][k]);
		}
		
		for(int k=1;k<cutpoint2;k++) {
			Offspring_Transfer1.add(my_parents[selected_parents_list[i]][k]);
			Offspring_Transfer2.add(my_parents[selected_parents_list[i+1]][k]);
		}
		
		for(int k=cutpoint1;k<cutpoint2;k++) {
			if(Offspring_Transfer1.indexOf(my_parents[selected_parents_list[i+1]][k])>=0) 
				Offspring_Transfer1.remove(Offspring_Transfer1.indexOf(my_parents[selected_parents_list[i+1]][k]));
			if(Offspring_Transfer2.indexOf(my_parents[selected_parents_list[i]][k])>=0) 
				Offspring_Transfer2.remove(Offspring_Transfer2.indexOf(my_parents[selected_parents_list[i]][k]));
		}
		
		int m = 0;
		for(int k=cutpoint2;k<my_offsprings[0].length;k++) {
			my_offsprings[i][k] = Offspring_Transfer2.get(m);
			my_offsprings[i+1][k] = Offspring_Transfer1.get(m);
			m++;
		}
		
		
		for(int k=1;k<cutpoint1;k++) {
			my_offsprings[i][k] = Offspring_Transfer2.get(m);
			my_offsprings[i+1][k] = Offspring_Transfer1.get(m);
			m++;
		}
		
		Offspring_Transfer1.clear();
		Offspring_Transfer2.clear();
		i++;
		
	}
	
	
	
}


					//Roulette Wheel seçimi
public static int[] Roulette_Wheel(int[][] my_array) {
	
	double [] ratio_array = new double [my_array.length];
	
	double sum = 0;
	
					//Feasible olmayan bireylerin uygunluk deðerlerinde iyileþtirme yapýlarak roulette çarkýna eklenmesi
	for(int i=1;i<my_array.length;i++) {
			if(my_array[i][0] < bigM)
				sum += my_array[i][0];
			else 
				sum += my_array[i][0]/(100);
			
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
		
		
		return selection_array;
		
		
}

				//2-point swap yöntem ile mutasyon
public static void Mutation (int[][] my_array) {
	

	Random random = new Random();
	int rand1, rand2;
	int temp_var;
	
	
	for(int i=Parent_Transfer_Number+1;i<my_array.length;i++) {
		double rand_mutation_rate = Math.random();
		if(rand_mutation_rate < Mutation_Rate) {
			rand1 = 1+random.nextInt(my_array[1].length-1);
			rand2 = 1+random.nextInt(my_array[1].length-1);
			temp_var = my_array[i][rand1];
			my_array[i][rand1] = my_array[i][rand2];
			my_array[i][rand2] = temp_var;
			
		}
	}

	
}
					

				//2 boyutlu bir diziyi ekrana yazdýrma metodu
public static void Display(int[][] my_pop) {
	
	System.out.println();
	for(int i=1;i<my_pop.length;i++) {
		System.out.println(Arrays.toString(my_pop[i]));
	}
	System.out.println();	
	
}


				//Yeni jenerasyonun ebeveyn olarak atanmasý 
public static void OffSpringstoParents(int[][] my_array1, int[][] my_array2  ) {
	
	for(int i=0;i<my_array1.length;i++) {
		my_array2[i] = Arrays.copyOf(my_array1[i], my_array1[i].length);
		Arrays.fill(my_array1[i], 0);
	}
	
		
}


				// "Fisher–Yates shuffle" yöntemi ile bir dizinin karýlmasý metodu
private static int[] shuffleArray(int[] my_array)
{
    int index;
    Random random = new Random();
    for (int i = my_array.length - 1; i > 0; i--)
    {
        index = 1 + random.nextInt(i);
        if (index != i)
        {
            my_array[index] ^= my_array[i];
            my_array[i] ^= my_array[index];
            my_array[index] ^= my_array[i];
        }
    }
    
    return my_array;
}




}





