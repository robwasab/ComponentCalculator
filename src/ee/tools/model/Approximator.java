package ee.tools.model;

import java.util.Arrays;
import java.util.LinkedList;

public class Approximator {

	public static int precision_ones_place = 5;
	
	public static Components approximate(LinkedList<Component> comps, Component target)
	{
		Component[] seq = comps.toArray(new Component[0]);
		Arrays.sort(seq);
		return sum_recurse(seq, target.clone());
	}
	
	private static Components sum_recurse(Component[] comps, Component target)
	{
		int index = BinarySearch.search(comps, target);
		
		Component found = comps[index];
		
		target = target.subtract(found);
		
		if (target.lessThan(new Component(0.0))) 
		{
			Components c = new Components(null, Components.SUM);
			//here's where you should start the parallel approximation
			//make sure you pass the "found" Component to the parallel approximator
			//here, the found Component is guaranteed to overshoot the target value. 
			
			//since target is negative (target has to be negative to get in here)
			//undo subtracting the found value 
			target = target.add(found);
			
			Components inv_inv_sum_res = inverse_inverse_sum_recurse(comps, target, found);
			
			inv_inv_sum_res.add(found);
			
			c.add(inv_inv_sum_res);
			
			//If the inverse_inverse_sum under approximates target
			//You can still add series resistors to get closer to the target
			//Recursively call sum_recurse again
			//Asking for the new marginal value
			
			if(inv_inv_sum_res.lessThan(target) && (Math.abs(target.getValue() - inv_inv_sum_res.getValue()) > precision_ones_place))
			{
				Component new_target = new Component( target.getValue() - inv_inv_sum_res.getValue() );
				
				Components another_result = sum_recurse(comps, new_target);
				c.add(another_result);
			}		
			return c;
		}
		Components c = sum_recurse(comps, target);
		c.add(found);
		return c;
	}
	
	private static Components inverse_inverse_sum_recurse(Component[] comps, Component target, Component base_comp)
	{
		Component numerator = target.multiply(base_comp);
		Component denom     = base_comp.subtract(target);
		Component desired   = numerator.divide(denom);
		
		//System.out.println("GOING FOR: " + desired);
		
		int index = BinarySearch.search(comps, desired);
		
		Component found = comps[index];
		
		//System.out.println("FOUND: " + found);
		
		//Calculate the resulting value
		double current_val = base_comp.getValue();
		double found_val   = found.getValue();
		
		//Product Over Sum to Calculate New Parallel Resistance
		Component new_base_comp = new Component(current_val * found_val / (current_val + found_val));
		
		//System.out.println("RESULTING RESISTANCE: " + new_base_comp);
		
		if (new_base_comp.lessThan(target))
		{
			/*Here is when to stop the recursion
			  Here you have a choice:
			  -Do you return found?
			  -Or do you neglect found?
			*/
			double original_p_error = percent_error(target, base_comp);
			double new_p_error      = percent_error(target, new_base_comp);
			
			if (Math.abs(original_p_error) < Math.abs(new_p_error))
			{
				//Don't include the found component
				return new Components(null, Components.INVERSE_INVERSE_SUM);
			}
			else
			{
				//Include the found component
				Components ret = new Components(null, Components.INVERSE_INVERSE_SUM);
				ret.add(found);
				return ret;
			}
		}
		
		//System.out.println(found + " " + new_base_comp + " TARGET: " + target);
		
		Components c = inverse_inverse_sum_recurse(comps, target, new_base_comp);
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
