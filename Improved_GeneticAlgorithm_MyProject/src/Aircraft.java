import java.io.File;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.util.Scanner;

public class Aircraft {
	
		public static String test_File = "airland8.txt";		//1,2,3,4,5,8,9,10,11,12,13
	
	
		public static int number_of_aircrafts;
		public static int freeze_time;
		

		
		public  int appearence_time;
		public  int earliest_time;
		public  int target_time;
		public  int latest_time;
		public  double penalty_before;
		public  double penalty_after;
		public  int[] separation_time;
		

		public void setData(int counter_call){
			
			int counter = 1;
			
			try {				
			     File myObj = new File(test_File);
			     Scanner my_scan = new Scanner(myObj);
			     
			     my_scan.useLocale(Locale.US);
			     for(int i=0;i<2+((counter_call*6)+(number_of_aircrafts*counter_call));i++) {
			    	 my_scan.nextDouble();
			     }
			     
			     while(counter < 7) {
			    	 
			    	 if(counter == 1)
			    		 appearence_time = my_scan.nextInt();
			    	 else if(counter == 2)
			    		 earliest_time = my_scan.nextInt();
			    	 else if(counter == 3)
			    		 target_time = my_scan.nextInt();
			    	 else if(counter == 4)
			    		 latest_time = my_scan.nextInt();
			    	 else if(counter == 5)
			    		 penalty_before = my_scan.nextDouble();
			    	 else if(counter == 6)
			    		 penalty_after = my_scan.nextDouble();
			    	 
			    	 counter++;
			    	 			
			     }
			     my_scan.close();
			     
			} 
			catch (FileNotFoundException e) {
			   	System.out.println("An error occurred.");
			   	e.printStackTrace();
			}
		
			
		}
		
		public void setDataList(int counter_call) {
			
int counter = 1;
			

			separation_time = new int[number_of_aircrafts+1];
			separation_time[0] = 0;
			
			try {				
			     File myObj = new File(test_File);
			     Scanner my_scan = new Scanner(myObj);
			     my_scan.useLocale(Locale.US);
			     for(int i=0;i<2+(((counter_call+1)*6)+(number_of_aircrafts*counter_call));i++) {
			    	 my_scan.nextDouble();
			     }
			    
			     while(counter < number_of_aircrafts+1) {
			    	 
			    	 separation_time[counter] = my_scan.nextInt();
			    		 
			    	 counter++;
			    	 			
			     }
			     my_scan.close();
			     
			} 
			catch (FileNotFoundException e) {
			   	System.out.println("An error occurred.");
			   	e.printStackTrace();
			}
		
			
		}


	
	

}
