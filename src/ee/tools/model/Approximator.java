package ee.tools.model;

import java.util.Arrays;
import java.util.LinkedList;

import ee.tools.componentcalculator.Log;

public class Approximator {

	public static double precision = 0.1; //0.01, 0.001, 0.0001, 0.00001...
	
	public static String last_message = "";
	public static int error = -1;
	
	public static final int EXCEEDED_INVERSE_INVERSE_SUM_DEPTH = 0;
	public static final int EXCEEDED_SUM_DEPTH = 1;
	public static final int EXCEEDED_MAX_LENGTH = 2;
	public static final int INVERSE_INVERSE_SUM_ERROR = 3;
	
	public static final int PREFER_SHORTER = 0, PREFER_ACCURACY = 1;
	public static int preference = PREFER_SHORTER;
	
	public static String recurse_tag = "recuse tag";
	public static String range_tag = "range tag";
	public static String length_tag = "length tag";
	public static String optimize_tag = "optimize";
	public static String fail_tag = "fail tag";
	public static boolean kill = false;
	private static Component static_component = new Component(0);
	
	public static Components approximate(LinkedList<Component> comps, Component target, double percent_error)  
	{
		last_message = "";
		error = 0;
		kill = false;
		Log.blackList.add(recurse_tag);
		Log.blackList.add(range_tag);
		Log.blackList.add(length_tag);
		Log.blackList.add(optimize_tag);
		
		Component[] seq = comps.toArray(new Component[0]);
		Arrays.sort(seq);
		double fractional_error = percent_error/100.0;
		int max_length = -1; //unlimited
		Components ret = sum_recurse(seq, target.getValue(), 0, 0, fractional_error, target.getValue(), max_length, 0);
		
		Log.blackList.removeLast();
		Log.blackList.removeLast();
		Log.blackList.removeLast();	
		Log.blackList.removeLast();
		return ret;
	}	
	
	private static Components sum_recurse(Component[] comps, double target,
			int depth, int parent_depth, double fractional_error, double original_target, int max_length, int current_length)  
	{		
		if (Thread.interrupted()) 
		{
			last_message = "Killed...";
			kill = true;
		}
		//String indent = space(depth + parent_depth);
	/*
		Log.d(recurse_tag, indent + "SUM");	
		Log.d(length_tag, indent + "Max Length: " + max_length);
		Log.d(length_tag, indent + "Current Length: " + current_length);
	*/
		
		if (max_length < current_length && max_length != -1)
		{
		/*	Log.d(length_tag, indent + "SUM RETURNING");
			last_message = "Sum exceeded max length\n";
			last_message += "Max Length: " + max_length + "\n";
		*/
			error = EXCEEDED_MAX_LENGTH;
			return null;
		}
		
		if (20 < depth) 
		{
		 	/* Log.d(recurse_tag, indent + "depth too large... returning***"); */
			error = Approximator.EXCEEDED_SUM_DEPTH;
			last_message = "Sum Recurse Exceeded Depth: " + depth + "\n";
			last_message += "Parent Depth: " + parent_depth + "\n";
			last_message += "Original Target: " + original_target + "\n";
			last_message += "Current Search Value: " + target;
			last_message += "Recommend Increasing % Error\n";
			return null;
		}
		
		static_component.setValue(target);
		
		int index = BinarySearch.search(comps, static_component);
		
		Components optimized = null;
		
		int fails = 0;
		
		for (int i = (index - 1 < 0) ? 0 : index - 1; i < comps.length && kill != true; i++)
		{
			Component found = comps[i];
			
			if (1 < fails) 
			{
				Log.d(fail_tag, "fails exceeded..");
				break;
			}
			
			//if ((target.getValue() * 5.0) < found.getValue()) break;
			
		/*Log.d(recurse_tag, found.toString(depth+parent_depth)); */
		
			double new_target = target - found.getValue();
		
			double range = original_target * fractional_error;
		
		/*	Log.d(range_tag, indent + "sum range: " + range);*/
		
			if (Math.abs(new_target) <= range)
			{
			/*	Log.d(recurse_tag,  indent + "RETURNING");*/
				Components c = new Components(null, Components.SUM);
				c.add(found);
				return c;
			}
		
			Components c = null;
			
			//Log.d(recurse_tag, indent + "New Target: " + new_target);
			
			if (new_target < 0.0) 
			{			
				c = new Components(null, Components.SUM);
				//here's where you should start the parallel approximation
				//make sure you pass the "found" Component to the parallel approximator
				//here, the found Component is guaranteed to overshoot the target value. 
			
				//since target is negative (target has to be negative to get in here)
				//undo subtracting the found value 
				new_target = new_target + found.getValue();
			
				double new_range = fractional_error * original_target;
				
				int new_parent_depth = depth + parent_depth;
				
				Components inv_inv_sum_res = inverse_inverse_sum_recurse(comps, new_target, found.getValue(), 1, 
						new_parent_depth, new_range,
							max_length, current_length + 1); //current_length + 1 to account for found component
				
				if (inv_inv_sum_res == null)
				{
					if (max_length != -1) fails++;
					continue;
				}
				
				inv_inv_sum_res.add(found);
			
				c.add(inv_inv_sum_res);
			}
			else
			{
				c = sum_recurse
					(comps, new_target, depth + 1, parent_depth, fractional_error, original_target,
							max_length, current_length + 1);
		
				if (c == null) 
				{
					if (max_length == -1) return null;
					fails++;
					continue;
				}
		
				c.add(found);
			}
			
			/*This is where the weeding happens*/
			boolean evaluate = false;
			
			if (Approximator.preference == PREFER_SHORTER)
				evaluate = (c.getLength() <  max_length || max_length == -1);
			
			else if (Approximator.preference == PREFER_ACCURACY)
				evaluate = (c.getLength() <= max_length || max_length == -1);
			
			else 
				evaluate = (c.getLength() <= max_length || max_length == -1);
			
			if (evaluate)
			{
				if (max_length == -1)
				{
					optimized  = c;
					max_length = optimized.getLength();
				/*
					Log.d(optimize_tag, indent + "INITIAL OPTIMIZE LENGTH: " + max_length);
					Log.d(optimize_tag, c.toString(parent_depth + depth));
				*/
				}
				else
				{
					if (optimized != null)
					{
						boolean swap = false;
						double previous_precision = Math.abs(optimized.getValue() - target);
						double new_precision = Math.abs(c.getValue() - target);
						swap |= new_precision < previous_precision;
						
						swap |= c.getLength() < optimized.getLength();
						
						if (swap)
						{
						/*
							Log.d(optimize_tag, indent + "Better Component...");
							Log.d(optimize_tag, indent + "New Length: " + new_length);
							Log.d(optimize_tag, indent + "previous value: " + optimized.getValue());
							Log.d(optimize_tag, indent + "new value: " + c.getValue());
							Log.d(optimize_tag, c.toString(parent_depth + depth));
						*/
							optimized  = c;
							max_length = optimized.getLength();
						}
					}
				}
			}
		}
		
		return optimized;
	}
	
	private static Components inverse_inverse_sum_recurse
		(Component[] comps, double target, double base_comp,
				int depth, int parent_depth, double range, int max_length, int current_length)
	{
		if (kill) return null;

		//String indent = space(parent_depth + depth);
	/*
		Log.d(recurse_tag, indent + "INVERSE");
		Log.d(length_tag, indent + "Max Length: " + max_length);
		Log.d(length_tag, indent + "Current Length: " + current_length);
	*/
		if (max_length != -1)
		{
			if (max_length < current_length)
			{
				//Log.d(length_tag, indent + "INV RETURNING");
				last_message = "Inverse inverse sum exceeded max_length";
				last_message += "Max Length: " + max_length + "\n";
				error = EXCEEDED_MAX_LENGTH;
				return null;
			}
		}
		
		double numerator = target * base_comp;
		
		double denom     = base_comp - target;
		
		double desired   = numerator / denom;
				
		//Log.d(range_tag, indent + "inverse range: " + range);	
		
		boolean stop = false;
		
		double desired_threshold 
		= precision/4.0 * (4.0 * base_comp * base_comp / (precision * precision) - 1 );
		
		if (base_comp < target) stop |= true;
		
		if (desired_threshold < desired) stop |= true;
		
		//Log.d(recurse_tag, indent + "Desire: " + desired);
		
		if (stop)
		{
			//Log.d(recurse_tag, indent + "INVERSE RETURNING");
			return new Components(null, Components.INVERSE_INVERSE_SUM);
		}
		
		//if the desired component is larger than the smallest one in our inventory, 
		//Then try to approximate it using sum_recurse
		Component found;
	
		if (desired < comps[0].getValue())
		{
			//Then it is the smallest one youve got;
			found = comps[0];
			current_length += 1;
		}
		else 
		{
		/*
			Log.d(recurse_tag, space(parent_depth + depth) + "Starting SUM dive!");
			Log.d(recurse_tag, space(parent_depth + depth) + "Looking for: " + desired);
		*/
			//also try finding the range of acceptable series components
			//we know what we need
			double target_value = target;
			double desired_value = desired;
			double base_value = base_comp;
			double low, high, low_res, high_res, low_diff, high_diff;
			double new_range = 0;
			do
			{
				low = desired_value - new_range;
				high = desired_value + new_range;
				
				low_res  = base_value * low / (base_value + low);
				high_res = base_value * high / (base_value + high);
			/*
				Log.d(tag, indent+"   Desired Value: " + desired_value);
				Log.d(tag, indent+"             LOW: " + low);
				Log.d(tag, indent+"            HIGH: " + high);
				Log.d(tag, indent+"          TARGET: " + target_value);
				Log.d(tag, indent+"         LOW_RES: " + low_res);
				Log.d(tag, indent+"        HIGH_RES: " + high_res);
			*/
				high_diff = Math.abs(high_res - target_value);
				low_diff  = Math.abs(target_value - low_res );
			/*
				Log.d(tag, indent+" HIGH DIFFERENCE: " + high_diff);
				Log.d(tag, indent+"  LOW DIFFERENCE: " + high_diff);
				Log.d(tag, indent+"           RANGE: " + range);
				Log.d(tag, indent+"       NEW RANGE: " + new_range);
			*/
				if ( high_diff > range)
					break;
				if ( low_diff > range)
					break;
				new_range += 0.01;
			} while(true);
			
			double fractional_error = new_range/desired_value;
			
			//Log.d(recurse_tag, space(parent_depth + depth)+"fractional_error: " + fractional_error);
			
			found = sum_recurse
					(comps, desired, 1, parent_depth + depth, 
							fractional_error, desired,
								max_length, current_length);
			
			if (found == null)
			{
				//Log.d(recurse_tag, space(parent_depth + depth) + "SUM_RECURSE RETURNED NULL");
				
				if (error == Approximator.EXCEEDED_SUM_DEPTH)
				{
					error = Approximator.INVERSE_INVERSE_SUM_ERROR;
					last_message += "Call to Sum Within Inverse Inverse Sum exceeded depth...\n";
					last_message += "Desired: " + desired + "\n";
				}
				else if (error == Approximator.EXCEEDED_MAX_LENGTH)
				{
					//last_message += "Call to Sum Within Inverse Inverse Sum exceeded max length\n";
					//last_message += "Max Length: " + max_length + "\n";
					//last_message += "Desired: " + desired + "\n";
				}
				return null;
			}
			current_length += found.getLength();
		}
				
		//Log.d(recurse_tag, found.toString(parent_depth + depth));
				
		//Calculate the resulting value
		double current_val = base_comp;
		
		double found_val   = found.getValue();
		
		//Product Over Sum to Calculate New Parallel Resistance
		double new_base_comp = current_val * found_val / (current_val + found_val);
		
		/*AM I CLOSE ENOUGH?*/
		if (Math.abs(new_base_comp - target) <= range)
		{
			Components ret = new Components(null, Components.INVERSE_INVERSE_SUM);
			ret.add(found);
			return ret;
		}
		
		Components c = inverse_inverse_sum_recurse
				(comps, target, new_base_comp, 
						depth + 1, parent_depth, range,
							max_length, current_length);
		
		if (c == null) return null;
		
		c.add(found);
		
		return c;
	}
	
	public static void main(String[] args) throws CsvParserException, KillThreadException
	{
		LinkedList<Component> ll;
		ll = CsvParser.parseFile("/Users/robwasab/Documents/android/PermutationCalculator/src/parts2.csv");
		Components res = approximate(ll, new Component(9650), 10.0);
		System.out.println(res);
	}
}
