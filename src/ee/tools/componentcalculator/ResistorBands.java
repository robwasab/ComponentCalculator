package ee.tools.componentcalculator;

import java.util.LinkedList;

import android.graphics.Color;
import android.graphics.Paint;

public class ResistorBands 
{
	 public Object[] 
			BLACK =  {null, "black" },
			BROWN  = {null, "brown"},
			RED    = {null, "red"}, 
			ORANGE = {null, "orange"},
			YELLOW = {null, "yellow"},
			GREEN  = {null, "green"},
			BLUE   = {null, "blue"},
			VIOLET = {null, "violet"},
			GRAY   = {null, "gray"}, 
			WHITE  = {null, "white"},
			GOLD   = {null, "gold"},
			SILVER = {null, "silver"};
		
	LinkedList<Object[]> number_objects;
	Object tens_power_object;
	
	Paint[] number_colors;
	Paint tens_power_color;
	
	double value;
	
	String string = "";
	
	public ResistorBands(double value) throws ResistorException
	{
		Paint brown = new Paint();
		brown.setColor(Color.rgb(92, 51, 23));
		BROWN[0] = brown;
		
		Paint black = new Paint();
		black.setColor(Color.BLACK);
		BLACK[0] = black;
		
		Paint red = new Paint();
		red.setColor(Color.RED);
		RED[0] = red;
		
		Paint orange = new Paint();
		orange.setColor(Color.rgb(255, 127, 0));
		ORANGE[0] = orange;
		
		
		Paint yellow = new Paint();
		yellow.setColor(Color.YELLOW);
		YELLOW[0] = yellow;
		
		Paint green = new Paint();
		green.setColor(Color.GREEN);
		GREEN[0] = green;
		
		Paint blue = new Paint();
		blue.setColor(Color.BLUE);
		BLUE[0] = blue;
		
		Paint violet = new Paint();
		violet.setColor(Color.rgb(199, 21, 133));
		VIOLET[0] = violet;
		
		Paint gray = new Paint();
		gray.setColor(Color.GRAY);
		GRAY[0] = gray;
		
		Paint white = new Paint();
		white.setColor(Color.WHITE);
		WHITE[0] = white;
		
		Paint gold = new Paint();
		gold.setColor(Color.rgb(184, 134, 11));
		GOLD[0] = gold;
		
		Paint silver = new Paint();
		silver.setColor(Color.rgb(192, 192, 192));
		SILVER[0] = silver;
		
		this.value = value;
		refresh();
	}
	
	@SuppressWarnings("unchecked")
	private void refresh() throws ResistorException
	{
		/*
		 * calculate_band must return an Object[] array...
		 * Element index 0: is an Object[] array composed of Color Objects
		 *    But really, Color Objects are also of type Object[]
		 *    where its element 0 is actually a Color, element 1 is a String
		 * 
		 * Element index 1: is a Color Object
		 *    where its element 0 is a color, element 1 is a String
		 */
		Object[] objects = calculate_bands(value);
		
		number_objects = (LinkedList<Object[]>) objects[0];
		
		tens_power_object = objects[1];
		
		number_colors = new Paint[number_objects.size()];
		
		for (int i = 0; i < number_objects.size(); i++)
		{
			Object[] number_color = (Object[]) number_objects.get(i);
			
			number_colors[i] = (Paint) number_color[0]; 
			
			this.string += (String) number_color[1] + " ";
		}
		
		tens_power_color  = (Paint) ((Object[]) tens_power_object)[0];
		String tens_power_string = (String)((Object[]) tens_power_object)[1];
		
		string += " | " + tens_power_string;
		
	}
	
	public void setValue(double value) throws ResistorException {this.value = value; refresh(); }
	
	public Paint[] getNumberBands() { return number_colors; }
	
	public Paint getTensPowerBand() { return tens_power_color; }
	
	public String toString() { return string; }
	
	public Object[] calculate_bands(double value) throws ResistorException
	{
		//Gaurds
		if (value < .1) 
		{
			String msg = value + " is too small. Cannot make a valid 2 digit integer " +
								 "silver tens power. .1 <= value";
			throw new ResistorException(msg);
		}
		if (value > 1000E6)
		{
			String msg = value + " is too large to represent with blue tens power.";
			throw new ResistorException(msg);
		}
		
		int power = 0;
		boolean has_remainder = true;
		double scratch_value = value;
		
		if ( (value % 1) > 1E-6 )
		{
			do
			{
				power++;
				scratch_value *= 10; 
				//System.out.println(scratch_value + " Mod: " + (scratch_value % 1));
				has_remainder = (scratch_value % 1) > 1E-6;
			} while( has_remainder == true );
			
			power *= -1;
			
			if (power < -2) 
			{
				String msg = "Tens power below -2, cannot represent with resistor. Calculated tens power: " + power;
				throw new ResistorException(msg);
			}
		}
		else
		{
			//stop when there is a digit in the ones place		   
			while( (scratch_value % 10) == 0 )
			{
				power++;
				scratch_value /= 10.0;
			} 
		}
		int whole_part = (int)Math.round(scratch_value);
		
		//if the whole_part is only one digit...
		if (whole_part < 10)
		{
			whole_part *= 10;
			scratch_value *= 10;
			power--;
		}
		
		if (1000 <= whole_part)
		{
			String msg = whole_part + " larger than 3 band numbers";
			throw new ResistorException(msg);
		}
		
		//System.out.println(whole_part + " | " + power);
		
		LinkedList<Object[]> list = new LinkedList<Object[]>();
		
		int divide_by = 10;
		
		int scratch = whole_part;
		
		while(true)
		{			
			int digit = scratch % divide_by;
			
			Object[] band = toColor(digit / (divide_by/10) );
			
			list.push(band);
			
			if (whole_part == (whole_part % divide_by)) break;

			divide_by *= 10;
			
			scratch -= digit;			
		}
		Object[] ret = new Object[2];
		ret[0] = list;
		ret[1] = toColor(power);
		return ret;
	}
	
	public Object[] toColor(int number) throws ResistorException
	{
			switch(number)
			{
			case -2:
				return SILVER;
			case -1:
				return GOLD;
			case 0:
				return BLACK;
			case 1:
				return BROWN;
			case 2:
				return RED;
			case 3:
				return ORANGE;
			case 4:
				return YELLOW;
			case 5:
				return GREEN;
			case 6:
				return BLUE;
			case 7:
				return VIOLET;
			case 8:
				return GRAY;
			case 9:
				return WHITE;
			default:
				throw new ResistorException("Cannot Create a Band Color for " + number);
		}
	}
}
