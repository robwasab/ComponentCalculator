package ee.tools.model;

import java.util.Arrays;

public class BinarySearch {
	
	public static int search(Component[] seq, Component target)
	{
		   int mini = 0;
		   int maxi = seq.length - 1;
		   while(true)
		   {
		      int m = (int) ((float)(mini + maxi) / 2.0);

		      if (maxi < mini)
		      {
		         return wrap_up(seq, m, target);
		      }
		      
		      if (seq[m].lessThan(target) ) { mini = m + 1; }
		      
		      else if (seq[m].greaterThan(target)) { maxi = m - 1; }
		      
		      else return m;
		   }
	}
		 
	private static double diff(Component x, Component y) { return Math.abs(x.getValue() - y.getValue());}
	
	private static int wrap_up(Component[] seq, int m, Component target)
	{
		//check tolerances 
		int    min_diff_index = m;
		double min_diff = diff(target, seq[m]);
		   
		   for (int i = m-1; i <= m+1; i++)
		   {
		      try
		      {
		         if (diff(target, seq[i]) < min_diff)
		         {
		            min_diff = diff(target, seq[i]);
		            min_diff_index = i;
		         }
		      }
		      catch (ArrayIndexOutOfBoundsException foo) { continue; }
		   }   
		   return min_diff_index;
	}
	
	public static void main(String[] args)
	{
		Component[] things = new Component[50];
		
		for (int i = 0; i < 50; i++)
		{
		   things[i] = new Component( (double) (50 - i));
		}
		
		Arrays.sort(things);
		
		System.out.println(things[BinarySearch.search(things, new Component(-1))]);

		System.out.println(things[BinarySearch.search(things, new Component(25.6))]);
		
	}

}
