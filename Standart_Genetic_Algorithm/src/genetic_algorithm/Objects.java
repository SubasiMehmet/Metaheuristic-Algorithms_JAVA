package genetic_algorithm;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Objects {
	
	public static String test_File = "END507_HW3_instance.txt";
	
	
	public static int Obj_Number;
	public static int Weight;
	
	public int value;
	public int weight;
	
	public void setData(int counter_call){
		
		try {				
		     File myObj = new File(test_File);
		     Scanner my_scan = new Scanner(myObj);
		    
		     for(int i=1;i<(counter_call*3)+1;i++) {
		    	 my_scan.nextInt();
		     }
		     
		     weight = my_scan.nextInt();
		     value = my_scan.nextInt();
		     
		     my_scan.close();
		     
		} 
		catch (FileNotFoundException e) {
		   	System.out.println("An error occurred.");
		   	e.printStackTrace();
		}
		
		
	}
		

}
