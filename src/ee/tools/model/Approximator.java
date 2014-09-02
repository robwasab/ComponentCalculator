package ee.tools.model;

import java.util.Arrays;
import java.util.LinkedList;

import ee.tools.componentcalculator.Log;

public class Approximator {

	public static double precision = 0.1; //0.01, 0.001, 0.0001, 0.00001...
	
	public static double default_tolerance = 1;
	public static int maximum_number_inverse_inverse_components = 5;
	public static String recurse_tag = "recuse tag";
	public static String range_tag = "range tag";
	public static String length_tag = "length tag";
	public static String optimize_tag = "optimize";
	
	public static Components approximate(LinkedList<Component> comps, Component target)
	{
		Log.blackList.add(recurse_tag);
		Log.blackList.add(range_tag);
		Log.blackList.add(length_tag);
		//Log.blackList.add(optimize_tag);
		
		Component[] seq = comps.toArray(new Component[0]);
		Arrays.sort(seq);
		double fractional_error = 0.01;
		int max_length = -1; //unlimited
		Components ret = sum_recurse(seq, target.clone(), 0, 0, fractional_error, target.getValue(), max_length, 0);
		
		Log.blackList.removeLast();
		Log.blackList.removeLast();
		Log.blackList.removeLast();
		//Log.blackList.removeLast();
		return ret;
	}
	
	private static String space(int spaces)
	{
		String space = "";
		for (int i = 0; i < spaces; i++) space += " ";
		return space;
	}
	
	
	private static Components sum_recurse(Component[] comps, Component target,
			int depth, int parent_depth, double fractional_error, double original_target, int max_length, int current_length)
	{
		String indent = space(depth + parent_depth);
		
		Log.d(length_tag, indent + "Max Length: " + max_length);
		Log.d(length_tag, indent + "Current Length: " + current_length);
		
		if (max_length < current_length && max_length != -1)
		{
			Log.d(length_tag, indent + "SUM RETURNING");
			return null;
		}
		
		if (50 < depth) 
		{
			Log.d(recurse_tag, indent + "depth too large... returning***");
			return null;
		}
		
		int index = BinarySearch.search(comps, target);
		
		Components optimized = null;
		
		for (int i = index; i < comps.length; i++)
		{
			Component found = comps[i];
			
			if ((target.getValue() * 5.0) < found.getValue()) break;
			
			Log.d(recurse_tag, found.toString(depth+parent_depth));
		
			Component new_target = target.subtract(found);
		
			double range = original_target * fractional_error;
		
			Log.d(range_tag, indent + "sum range: " + range);
		
			if (Math.abs(new_target.getValue()) <= range)
			{
				Log.d(recurse_tag,  indent + "RETURNING");
				Components c = new Components(null, Components.SUM);
				c.add(found);
				return c;
			}
		
			Components c = null;
			
			if (new_target.lessThan(new Component(0.0))) 
			{			
				c = new Components(null, Components.SUM);
				//here's where you should start the parallel approximation
				//make sure you pass the "found" Component to the parallel approximator
				//here, the found Component is guaranteed to overshoot the target value. 
			
				//since target is negative (target has to be negative to get in here)
				//undo subtracting the found value 
				new_target = new_target.add(found);
			
				double new_range = fractional_error * original_target;
				
				int new_parent_depth = depth + parent_depth;
				
				Components inv_inv_sum_res = inverse_inverse_sum_recurse(comps, new_target, found, 1, 
						new_parent_depth, new_range,
							max_length, current_length + 1); //current_length + 1 to account for found component
				
				if (inv_inv_sum_res == null) continue;
				
				inv_inv_sum_res.add(found);
			
				c.add(inv_inv_sum_res);
			}
			else
			{
				c = sum_recurse
					(comps, new_target, depth + 1, parent_depth, fractional_error, original_target,
							max_length, current_length + 1);
		
				if (c == null) continue;
		
				c.add(found);
			}
			
			/*This is where the weeding happens*/
			if (c.getLength() <= max_length || max_length == -1)
			{
				if (max_length == -1)
				{
					optimized  = c;
					max_length = optimized.getLength();
					
					Log.d(optimize_tag, indent + "INITIAL OPTIMIZE LENGTH: " + max_length);
					Log.d(optimize_tag, c.toString(parent_depth + depth));
				}
				else
				{
					if (optimized != null)
					{
						double previous_precision = Math.abs(optimized.getValue() - target.getValue());
						double new_precision = Math.abs(c.getValue() - target.getValue());
						if (new_precision < previous_precision)
						{
							int new_length = c.getLength();
							
							Log.d(optimize_tag, indent + "Better Component...");
							Log.d(optimize_tag, indent + "New Length: " + new_length);
							Log.d(optimize_tag, indent + "previous value: " + optimized.getValue());
							Log.d(optimize_tag, indent + "new value: " + c.getValue());
							Log.d(optimize_tag, c.toString(parent_depth + depth));
							
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
		(Component[] comps, Component target, Component base_comp,
				int depth, int parent_depth, double range, int max_length, int current_length)
	{
		String indent = space(parent_depth + depth);

		Log.d(length_tag, indent + "Max Length: " + max_length);
		Log.d(length_tag, indent + "Current Length: " + current_length);

		if (max_length < current_length && max_length != -1)
		{
			Log.d(length_tag, indent + "INV RETURNING");
			return null;
		}
		
		Component numerator = target.multiply(base_comp);
		
		Component denom     = base_comp.subtract(target);
		
		Component desired   = numerator.divide(denom);
				
		Log.d(range_tag, indent + "inverse range: " + range);	
		
		boolean stop = false;
		
		double desired_threshold 
		= precision/4.0 * (4.0 * base_comp.getValue() * base_comp.getValue() / (precision * precision) - 1 );
		
		if (base_comp.lessThan(target)) stop |= true;
		
		if (desired_threshold < desired.getValue()) stop |= true;
		
		Log.d(recurse_tag, indent + "Desire: " + desired.getValue());
		
		if (stop)
		{
			Log.d(recurse_tag, indent + "INVERSE RETURNING");
			return new Components(null, Components.INVERSE_INVERSE_SUM);
		}
		
		//if the desired component is larger than the smallest one in our inventory, 
		//Then try to approximate it using sum_recurse
		Component found;
	
		if (desired.lessThan(comps[0]))
		{
			//Then it is the smallest one youve got;
			found = comps[0];
			current_length += 1;
		}
		else 
		{
			Log.d(recurse_tag, space(parent_depth + depth) + "Starting SUM dive!");
			Log.d(recurse_tag, space(parent_depth + depth) + "Looking for: " + desired.toString());
			//also try finding the range of acceptable series components
			//we know what we need
			double target_value = target.getValue();
			double desired_value = desired.getValue();
			double base_value = base_comp.getValue();
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
			Log.d(recurse_tag, space(parent_depth + depth)+"fractional_error: " + fractional_error);
			found = sum_recurse
					(comps, desired, 1, parent_depth + depth, 
							fractional_error, desired.getValue(),
								max_length, current_length);
			
			if (found == null)
			{
				Log.d(recurse_tag, space(parent_depth + depth) + "SUM_RECURSE RETURNED NULL");
				return null;
			}
			current_length += found.getLength();
		}
				
		Log.d(recurse_tag, found.toString(parent_depth + depth));
				
		//Calculate the resulting value
		double current_val = base_comp.getValue();
		
		double found_val   = found.getValue();
		
		//Product Over Sum to Calculate New Parallel Resistance
		Component new_base_comp = new Component(current_val * found_val / (current_val + found_val));
		
		/*AM I CLOSE ENOUGH?*/
		if (Math.abs((new_base_comp.getValue() - target.getValue())) <= range)
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
	
	private static double percent_error(Component target, Component compare)
	{
		double tar = target.getValue();
		double com = compare.getValue();
		return (com - tar)/tar * 100.0;
	}
	
	public static void main(String[] args) throws CsvParserException
	{
		LinkedList<Component> ll;
		ll = CsvParser.parseFile("/Users/robwasab/Documents/android/PermutationCalculator/src/parts2.csv");
		Components res = approximate(ll, new Component(9650));
		System.out.println(res);
	}
}
