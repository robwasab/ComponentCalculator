package ee.tools.model;

import android.util.Log;

public class EngNot {
	static String[] suffixes = {"f", "p", "n", "u", "m", " ", "k", "M", "G", "T"};
	static int d_places = 3;
	
	public static int zero_index()
	{
	   for (int i = 0; i < suffixes.length; i++)
	   {
	      if (suffixes[i] == " ")
		     return i;
	   }
	   return -1;
	}
			
	private static String rounder(double num, int d_places)
	{
		double multiplier = Math.pow(10.0, (double) d_places);
		   
		double dummy = Math.round(num * multiplier) / multiplier;
				
		return Double.toString(dummy);
	}
	
	public static String toEngNotation(double num)
	{
		if (num == 0) return "0";
		
		double power = (int) Math.floor(Math.log10(Math.abs(num)));
		
		int power_floored = (int) Math.floor(power / 3.0);
			   
		if (power_floored == 0)
		{
			return rounder(num, EngNot.d_places);
		}
			   
	    int power_index = zero_index() + power_floored;
	    	   
	    double div = Math.pow(10.0, power_floored * 3.0);
		
	    num /= div;
	    String ret  = rounder(num, d_places);
	    
	    ret  = ret + suffixes[power_index];
			   
	    return ret;
	}
	
	public static double convert(String engr_not)
	{
		int zero_index = zero_index();
				
		double power = 0;
		
		int suffix_index = 0;
		
		boolean break_outer_loop = false;
		
		for (int i = 0; i < suffixes.length; i++)
		{
			if (i == zero_index) continue;
			
			char suffix = suffixes[i].charAt(0);
			
			for (suffix_index = 0; suffix_index < engr_not.length(); suffix_index++)
			{
			   char c = engr_not.charAt(suffix_index);
			   
			   if (c == suffix)
		   	   {
			      power = i - zero_index;
				  power *= 3;
				  break_outer_loop = true;
				  break;
		   	   }
			}	
			if (break_outer_loop) break;
		}
		
		double multiplier = Math.pow(10.0, power);
		
		String remove_suffix = engr_not.substring(0, suffix_index);
		 		
		double value = Double.parseDouble(remove_suffix);
		
		value *= multiplier;
		
		return value;
	}
	
	public static void main(String[] args) throws Exception
	{
		System.out.println(EngNot.toEngNotation(1000));
		System.out.println(EngNot.toEngNotation(1234));
		System.out.println(EngNot.toEngNotation(10000));
		System.out.println(EngNot.toEngNotation(1000000));
		System.out.println(EngNot.toEngNotation(1234.567));
		System.out.println(EngNot.toEngNotation(1E-6));
		System.out.println(EngNot.toEngNotation(4.7E-9));
		System.out.println(EngNot.toEngNotation(12E-12));
		
		System.out.println(EngNot.convert("1k"));
		System.out.println(EngNot.convert("1.234k"));
		System.out.println(EngNot.convert("1M"));
		System.out.println(EngNot.convert("1m"));
		System.out.println(EngNot.convert("4.8u"));

	}
}