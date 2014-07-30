package ee.tools.componentcalculator;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;
import ee.tools.componentcalculator.Complex;
import ee.tools.componentcalculator.ComponentViewInterface;


public class RecursiveSeriesDrawingUtility {
	
	public static Complex get_preferred_grab_point(ComponentViewInterface this_comp, 
			Complex starting_grab_point, 
			float screen_width, 
			float screen_height, 
			float highest_y, 
			float lowest_y, 
			boolean rotate180,
			boolean justRotated)
	{	
		boolean enough_horz_room;
		
		if (!rotate180)
		{
			enough_horz_room = (starting_grab_point.re + this_comp.getWidth()) < screen_width;
		}
		else 
		{
			enough_horz_room = (starting_grab_point.re - this_comp.getWidth()) > 0;
		}
		
		boolean enough_vert_room = highest_y <= (starting_grab_point.im - this_comp.getHeight()/2.0);
		
		boolean keep_current_point = false;
		Complex keep_point = null;
		
		if ( enough_vert_room && enough_horz_room )
		{
			this_comp.setXY((float)starting_grab_point.re, (float)starting_grab_point.im);
		}
		else
		{
			if ( !enough_horz_room )
			{
				keep_current_point = true;
				keep_point = new Complex(starting_grab_point.re, starting_grab_point.im);
				//draw a line connector 
				float new_x;
				
				if (rotate180 == false) new_x = screen_width - 10;
				else                    new_x = 10;
				
				int padding = 10;
				
				float new_y = (float) (lowest_y + this_comp.getHeight()/2.0 + padding);
				
				this_comp.setXY(new_x, new_y);
				
				rotate180 ^= true;
				
				highest_y = lowest_y;
				
				//redo the enough_vert_room
				enough_vert_room = highest_y <= (this_comp.getXY().im - this_comp.getHeight()/2.0);
			}
			
			if ( !enough_vert_room )
			{
				double alter_y = highest_y + this_comp.getHeight()/2.0 + 10;
				
				this_comp.setXY((float)starting_grab_point.re, (float)alter_y);
			}
		
		}
		
		if (rotate180) 
		{
			this_comp.setAngle(Math.PI); 
		}
				
		else { this_comp.setAngle(0); }
		
		
		if (this_comp.getNext() != null)
		{			
			Complex suggest_next = new Complex(0,0);
				
			suggest_next.re = this_comp.getNextPoint().re;
			suggest_next.im = this_comp.getNextPoint().im;
			
			if ( lowest_y < this_comp.get_lowest_y() ) { lowest_y = this_comp.get_lowest_y(); }
			
			//suggest_next reference is returned, but altered.
			Complex next_pref = this_comp.getNext().get_preferred_grab_point(suggest_next,
					screen_width, screen_height, highest_y, lowest_y, rotate180, justRotated);
			
			//next_pref and suggest_next should be the same object
			Complex this_pref = next_pref;
			
			if (rotate180) { this_pref.re += this_comp.getWidth(); }
			else           { this_pref.re -= this_comp.getWidth(); }
			
			this_comp.setXY( (float) this_pref.re, (float) this_pref.im);
			
			if (!keep_current_point)
			{
				return this_pref;
			}	
			else 
			{
				return keep_point;
			}
		}
		else
		{
			if (!keep_current_point)
			{
				Complex ret = starting_grab_point;
				ret.re = this_comp.getXY().re;
				ret.im = this_comp.getXY().im;
				return ret;
			}
			else
			{
				return keep_point;
			}
		}
	}
	
	public static void link(ComponentViewInterface this_comp, ComponentViewInterface next_comp, Canvas c)
	{
		Complex p1 = this_comp.getNextPoint();
		Complex p2 = next_comp.getXY();
		Paint p = this_comp.getLinePaint();
		
		boolean rotated = false;
		double dx = p2.re - p1.re;
		
		if (Math.abs(this_comp.getAngle()) > 1) rotated = true;
		
		if (dx < 0.0 && !rotated)
		{
			//draw a vert then horz
			c.drawLine((float) p1.re, (float) p1.im, (float) p1.re, (float) p2.im, p);		
			c.drawLine((float) p1.re, (float) p2.im, (float) p2.re, (float) p2.im, p);
		}
		else if (dx < 0.0 && rotated)
		{
			//horz then vert
			c.drawLine((float) p1.re, (float) p1.im, (float) p2.re, (float) p1.im, p);		
			c.drawLine((float) p2.re, (float) p1.im, (float) p2.re, (float) p2.im, p);	
		}
		else if (dx > 0.0 && !rotated)
		{
			//horz then vert
			c.drawLine((float) p1.re, (float) p1.im, (float) p2.re, (float) p1.im, p);		
			c.drawLine((float) p2.re, (float) p1.im, (float) p2.re, (float) p2.im, p);	
		}
		else if (dx > 0.0 && rotated)
		{
			//vert then horz
			c.drawLine((float) p1.re, (float) p1.im, (float) p1.re, (float) p2.im, p);		
			c.drawLine((float) p1.re, (float) p2.im, (float) p2.re, (float) p2.im, p);
		}
	}
}
