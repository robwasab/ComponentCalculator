package ee.tools.componentcalculator.components_toolbox;

import java.util.LinkedList;
import java.util.List;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import ee.tools.componentcalculator.Schematic;
import ee.tools.model.Component;
import ee.tools.model.Components;

public class ComponentsView extends Components implements ComponentViewInterface{
	
	Complex rotation, draw_origin, grab_point, next;
	
	ComponentViewInterface next_comp = null;
	
	double angle = 0;
	String tag, prefix;
	private LinkedList<Integer> serial;
	private Paint p;
	
	/*
	 * Parallel Components Variables:
	 */
	private LinkedList<Complex> p11s, p12s;
	
	private LinkedList<Complex> p21s, p22s;
	
	private final static int padding = 20;
	
	double height = 0.0, width = 0.0;
	
	/*
	 * Series Components Variables:
	 */
	public Complex recommended_origin;
	
	boolean collapse = false;
	
	public static final int COLLAPSED_CAN_SHRINK = 0, CAN_SHRINK = 1;
	
	ComponentView collapseView;
	
	View parent;
	
	public ComponentsView(View parent, LinkedList<Integer> serial, List<Component> comps,
			int operation, int type)
	{
		super(null, operation, type);
		
		this.parent   = parent;
		
		rotation      = new Complex(1, 0);
		
		//not initialized Complex draw_origin
		draw_origin   = new Complex(0, 0);
		
		grab_point    = new Complex(0, 0);
		
		prefix        = this.getClass().toString();
				
		if (serial == null) 
		{
			serial = new LinkedList<Integer>();
			serial.add(0);
		}
		else
		{
			this.setSerialNumber(serial);
		}
		
		if (comps != null)
		{
			for (int i = 0; i < comps.size(); i++)
			{
				LinkedList<Integer> next_serial = this.getSerialNumber();
				
				next_serial.add(i);
				
				Component c = comps.get(i);
				
				Log.d(tag, "TYPE: " +  c.getClass().toString());
				
				if (c instanceof ComponentViewInterface) 
				{
					super.add(c); 
					((ComponentViewInterface)c).setSerialNumber(next_serial);
					
				}
				
				else if (c.getClass() == Component.class)
				{
					//make a new Component View
					
					//what type of body should we add? 
					Body b = null;
					
					if (type == RESISTOR)
					{
						int tolerance = 0;
						try {
							b = new ResistorBody(next_serial, c.getValue(), tolerance);
						} catch (ResistorException e) {
							e.printStackTrace();
						}
					}
					else if (type == CAPACITOR)
					{
					   b = new CapacitorBody(next_serial);
					}
					ComponentView cv = new ComponentView(next_serial, b, c.getValue(), c.getQnty());
					Log.d(tag, "MADE A:");
					Log.d(tag, cv.toString());
					super.add(cv);
					//swap the old Component pointer with a new ComponentView
					//This way, when the user attempts to modify the Component, he will modify
					//the ComponentView instead.
					comps.set(i, cv);
				}
				else if (c.getClass() == Components.class)
				{
					Components foo = (Components) c;
					ComponentsView csv = new ComponentsView(
							parent, next_serial, foo.components, foo.operation, type);
					super.add(csv);
					comps.set(i, csv);
				}
			}
		}
		
		if (super.getOrientation() == PARALLEL)
		{
			Log.d(tag, "Initing parallel...");
			init_parallel();
		}

		else if (super.getOrientation() == SERIES)
		{
			Log.d(tag, "Initing series...");
			init_series();
		}
		
		//init the collapsed view
		Body tapDat = new Tap2SeeMoreBody();
		tag = "ComponentsView";
		collapseView = new ComponentView(this.getSerialNumber(), tapDat, this.getValue(), this.getQnty());
	}
	
	public double getValue()
	{
		double ret = super.getValue();
		if (collapseView != null)
			this.collapseView.setValue(ret);
		return ret;
	}
	
	public String toString() 
	{
	   return this.to_string( 3 * this.getSerialNumber().size() );   
	}
	
	@Override
	public void setSerialNumber(LinkedList<Integer> serial) {
		this.serial = new LinkedList<Integer>();
		for (Integer i : serial)
		{
			this.serial.add(i);
		}
		for (int i = 0; i < size(); i++)
		{
			LinkedList<Integer> next_serial = this.getSerialNumber();
			next_serial.add(i);
			ComponentViewInterface comp = this.getComponentView(i);
			comp.setSerialNumber(next_serial);
		}
	}

	@Override
	public LinkedList<Integer> getSerialNumber() {
		LinkedList<Integer> copy = new LinkedList<Integer>();
		for (Integer i : serial)
		{
			copy.add(i);
		}
		return copy;
	}

	public ComponentViewInterface findBySerialNumber(LinkedList<Integer> search)
	{
		int this_serial_size = this.serial.size();
		int i = 0;
		
		for ( ; i < this_serial_size; i++)
		{
			if (this.serial.get(i).intValue() != search.get(i).intValue() ) return null;
		}
		
		if (this_serial_size == search.size()) 
		{
			return this;
		}
		
		ComponentViewInterface cv = this.getComponentView(search.get(i).intValue());
		ComponentsView c;
		
		if (cv.getClass() == ComponentsView.class)
		{
			c = (ComponentsView)cv;
			return c.findBySerialNumber(search);
		}
		else
		{
			return null;
		}
	}
	public void setXY(float x, float y)
	{
		this.grab_point.re = x; this.grab_point.im = y;
		
		if (collapse) { collapseView.setXY(x,y);  return;}			
		
		if (this.getOrientation() == PARALLEL)
		{
			update_parallel_values();
			rotate_parallel_points();
			update_next();
		}
	}
	
	public void setXY(Complex c)
	{
		this.grab_point = new Complex(c.re, c.im);
		
		if (collapse) { collapseView.setXY(c);  return;}			
		
		if (this.getOrientation() == PARALLEL)
		{
			update_parallel_values();
			rotate_parallel_points();
			update_next();
		}		
	}
	
	@Override
	public void setCollapse(boolean collapse) {	this.collapse = collapse; }
	
	@Override
	public Complex getNextPoint() {
		if (collapse)
		{
			return collapseView.getNextPoint();
		}
		if (this.orientation == SERIES)
		{
			return this.getComponentView(size()-1).getNextPoint();
		}
		return next;
	}

	@Override
	public float getPadding() { return padding; }
	
	public Paint getLinePaint() { return this.p; }
	
	public double getAngle() { return this.angle; }
	
	public String getName() { return "ComponentsView"; }
	
	@Override
	public void setAngle(double radians) {
		this.angle = radians;
		rotation.re = Math.cos(radians);
		rotation.im = Math.sin(radians);
		
		if (collapse)
		{
			collapseView.setAngle(radians);
			return;
		}
		
		if (this.getOrientation() == PARALLEL)
		{
			update_parallel_values();
			rotate_parallel_points();
			update_next();
		}
		else if (this.getOrientation() == SERIES)
		{
			//do nothing, let the code set the rotation field
			//rotation is actually taken care of in the easy_series_arrange helper function
		}
	}

	/* * * * * * * *
	 * DRAW        *
	 * * * * * * * *
	 */
	@Override
	public void draw(Canvas c) {
		
		if (collapse)
		{
			collapseView.draw(c);
			return;
		}
		
		if (super.getOrientation() == PARALLEL)
		{
			draw_parallel(c);
		}
		else if (super.getOrientation() == SERIES)
		{
			if (1 <= size() )
			{
				easy_series_arrange(c);
				ComponentViewInterface first = this.getComponentView(0);
				first.draw(c);
			}
		}
		if (next_comp != null)
		{
			next_comp.draw(c);
			
			RecursiveSeriesDrawingUtility.link(this, next_comp, c);
		}
	}

	@Override
	public void saveInstanceState(Bundle state) {
		LinkedList<Integer> prefix_ints = this.getSerialNumber();
		String prefix = "";
		for (Integer i : prefix_ints)
		{
			prefix += i.toString();
		}
		
		state.putInt(prefix + "size", size());
		state.putInt(prefix + "type", this.type);
		state.putInt(prefix + "orientation", this.orientation);
		state.putInt(prefix + "operation", this.operation);
		state.putString(prefix + "class", this.getClass().toString());
		state.putDouble(prefix + "grab_point_x", grab_point.re);
		state.putDouble(prefix + "grab_point_y", grab_point.im);
		state.putDouble(prefix + "angle", rotation.phase());
		
		Log.d("!!!", "ComponentsView Saving..." + prefix + " " + this.getValue());
		Log.d("!!!", "Saved size: " + size());
		
		for (int i = 0; i < size(); i++)
		{
			this.getComponentView(i).saveInstanceState(state);
		}
	}

	@Override
	public void restoreInstanceState(Bundle saved) {
		LinkedList<Integer> prefix_ints = this.getSerialNumber();
		
		String prefix = "";
		for (Integer i : prefix_ints)
		{
			prefix += i.toString();
		}
		Log.d("!!!", "ComponentsView Restoring..." + prefix);
		
		super.components = new LinkedList<Component>();
		
		int size = saved.getInt(prefix + "size");
		Log.d("!!!", "Got size: " + size);
		
		type = saved.getInt(prefix + "type");
		operation = saved.getInt(prefix+"operation");
		orientation = saved.getInt(prefix + "orientation");
		double grab_point_x = saved.getDouble(prefix + "grab_point_x");
		double grab_point_y = saved.getDouble(prefix + "grab_point_y");
		grab_point = new Complex(grab_point_x, grab_point_y);
		double angle = saved.getDouble(prefix + "angle");
		
		String child_prefix;
		
		for (int i = 0; i < size; i++)
		{
			child_prefix = prefix + Integer.toString(i);
			@SuppressWarnings("unchecked")
			LinkedList<Integer> child_prefix_ll = (LinkedList<Integer>) this.getSerialNumber().clone();
			child_prefix_ll.add(i);
			
			Log.d("!!!", "Retrieving class type: " + child_prefix);
			
			String class_str = saved.getString(child_prefix + "class");
			
			if (class_str.equals(ComponentsView.class.toString()))
			{
				LinkedList<Component> dummi = new LinkedList<Component>();
				
				dummi.add(new Component(456));
				
				ComponentsView cv = new ComponentsView(parent, child_prefix_ll, dummi, operation, type);
				
				cv.restoreInstanceState(saved);
				
				super.components.add(cv);
			}
			else if (class_str.equals(ComponentView.class.toString()))
			{
				ComponentView c = new ComponentView(child_prefix_ll, null);
				c.restoreInstanceState(saved);
				
				super.components.add(c);
			}
			this.collapseView.setValue(this.getValue());
		}
		
		this.setXY(grab_point);
		this.setAngle(angle);
		
		if (super.getOrientation() == PARALLEL)
		{
			init_parallel();
		}

		else if (super.getOrientation() == SERIES)
		{
			init_series();
		}

	}

	public void setNext(ComponentViewInterface cvi) { next_comp = cvi; }
	
	@Override
	public ComponentViewInterface getNext() {
		return next_comp;
	}

	@Override
	public Complex getXY() { return new Complex(grab_point.re, grab_point.im); }	
	
	public float getWidth()
	{
		if (collapse) { 
			return collapseView.getWidth();
		}
		
		if (this.getOrientation() == SERIES) { return this.series_width(); }
		
		return (float)this.width;
	}
	
	public float getHeight() 
	{
		if (collapse) { return collapseView.getHeight(); }

		if (this.getOrientation() == SERIES) { return this.series_height(); }
		return (float)height; 
	}
	
	public float getStrokeWidth() {	return ComponentView.stroke_width; }
	
	public boolean isCollapsed() { return this.collapse; }
	
	public ComponentViewInterface isIn(Complex pnt)
	{
		if (collapse)
		{
			if ( collapseView.isIn(pnt) != null ) { return this; }
			
			return null;
		}
		for (int i = 0; i < size(); i++)
		{
			ComponentViewInterface comp = this.getComponentView(i);
			
			if (comp.isIn(pnt) != null ) { return comp.isIn(pnt); }
		}
		return null;
	}
	
	/*Helper Functions*/
	private int size() { return super.components.size(); }
	
	private ComponentViewInterface getComponentView(int i)
	{
		return (ComponentViewInterface) super.components.get(i);
	}
	
	private Complex[] get_p12_p21_p22(Complex p11, ComponentViewInterface item, double maximum_width)
	{
		double space = maximum_width - item.getWidth();		
		
		Complex p21 = new Complex(p11.re + maximum_width - space/2.0, p11.im);
		
		Complex p12 = new Complex(p11.re + space/2.0, p11.im);
		Complex p22 = new Complex(p11.re + maximum_width, p11.im);
						
		Complex[] tuple = new Complex[3];
		
		tuple[0] = p12;
		tuple[1] = p21;
		tuple[2] = p22;
		
		return tuple;
	}
	
	/*~~~~~~~~~~~~~~~~~~~~~~PARALLEL DRAWING STUFF~~~~~~~~~~~~~~~~~~~~~~~~*/
	private void init_parallel()
	{
		p = new Paint();
		p.setColor(Color.BLACK);
		p.setStrokeWidth(ComponentView.stroke_width);
		next = new Complex(0,0);
		if (size() < 1) return;
		update_parallel_values();
		rotate_parallel_points();
		update_next();		
	}
		
	private void update_parallel_values()
	{
		this.height = 0.0;
		this.width  = 0.0;
		
		//Calculate dimensions
		for (int i = 0; i < size(); i++)
		{
			ComponentViewInterface cv = getComponentView(i);
				
			height += cv.getHeight();
			
			//if this isn't the last element, then add the padding
			if (i != (super.components.size() - 1)) { height += padding; }
			
			cv.setCollapse(false);
			//test to see if this component's width, in addition to the current grab_point.re
			//is within the boundary
			boolean should_collapse;
			if (0 <= this.rotation.re)
			{
				//Log.d(tag, "Orientation ->");
				should_collapse = (this.parent.getWidth() < (this.grab_point.re + cv.getWidth()) );	
				//Log.d(tag, this.parent.getWidth() + " < " + (this.grab_point.re + cv.getWidth()));				
			}
			else
			{
				//Log.d(tag, "Orientation <-");
				should_collapse = ( this.grab_point.re < cv.getWidth() );
				//Log.d(tag, this.grab_point.re + " < " + cv.getWidth());
			}
			/*
			Log.d(tag, "Should Collapse: " + should_collapse);
			Log.d(tag, "Parent Width   : " + this.parent.getWidth());
			Log.d(tag, "Grab Point RE  : " + this.grab_point.re);
			Log.d(tag, "Component Width: " + cv.getWidth());
			Log.d(tag,  cv.getSerialNumber().toString());
			*/
			cv.setCollapse(should_collapse);
		 	
			if (cv.getWidth() > width) 
			{
				width = cv.getWidth(); 
			}
		}
		
		//calculate the drawing origin with no rotation 
		draw_origin.re = this.grab_point.re;
		draw_origin.im = this.grab_point.im - this.height/2.0 + getComponentView(0).getHeight()/2.0;
		
		//Can't do this with zero components....
		//Have to expect that components might be zero size...
		if (size() < 1) return;
		
		p11s = new LinkedList<Complex>();
		
		p12s = new LinkedList<Complex>();
		
		p21s = new LinkedList<Complex>();
		
		p22s = new LinkedList<Complex>();
		
		ComponentViewInterface first = this.getComponentView(0);
		
		Complex p11 = new Complex(draw_origin.re, draw_origin.im);
		
		Complex p12, p21, p22;
		
		Complex[] p12_p21_p22 = get_p12_p21_p22(p11, first, width);
		
		p12 = p12_p21_p22[0];
		
		p21 = p12_p21_p22[1];
		
		p22 = p12_p21_p22[2];
		
		p11s.add(p11);
		
		p12s.add(p12);
		
		p21s.add(p21);
		
		p22s.add(p22);
		
		//Layout the points 
		for (int i = 1; i < size(); i++)
		{
			Complex p11_prev = p11s.get(i-1);
			
			//Complex p22_prev = p22s.get(i-1);
			
			ComponentViewInterface prev_comp = getComponentView(i - 1);
			
			ComponentViewInterface this_comp = getComponentView(i);
			
			Complex p11_next, p12_next, p21_next, p22_next;
			
			p11_next = new Complex(p11_prev.re, p11_prev.im);
			
			p11_next.im += padding + prev_comp.getHeight()/2.0 + this_comp.getHeight()/2.0;
			
			p12_p21_p22 = get_p12_p21_p22(p11_next, this_comp, width);
			
			p12_next = p12_p21_p22[0];
			
			p21_next = p12_p21_p22[1];
			
			p22_next = p12_p21_p22[2];
			
			p11s.add(p11_next);
			
			p22s.add(p22_next);
			
			p12s.add(p12_next);
			
			p21s.add(p21_next);
		}
	}
	
	private void rotate_parallel_points()
	{
		for (int i = 0; i < size(); i++)
		{
			Complex p11 = p11s.get(i);
			Complex p22 = p22s.get(i);
			Complex p12 = p12s.get(i);
			Complex p21 = p21s.get(i);
			
			p11 = p11.minus(grab_point);
			p22 = p22.minus(grab_point);
			p12 = p12.minus(grab_point);
			p21 = p21.minus(grab_point);
			
			p11 = p11.times(rotation);
			p22 = p22.times(rotation);
			p12 = p12.times(rotation);
			p21 = p21.times(rotation);
			
			p11 = p11.plus(grab_point);
			p22 = p22.plus(grab_point);
			p12 = p12.plus(grab_point);
			p21 = p21.plus(grab_point);
			
			p11s.set(i, p11);
			p12s.set(i, p12);
			p21s.set(i, p21);
			p22s.set(i, p22);
			
			getComponentView(i).setXY( (float)p12.re,(float) p12.im);
			getComponentView(i).setAngle(angle);
		}
	}
	
	private void update_next()
	{
		next.re = this.getWidth();
		next.im = 0;
		next = next.times(rotation);
		next.re += this.grab_point.re;
		next.im += this.grab_point.im;
	}
	
	private void draw_parallel(Canvas c)
	{
		update_parallel_values();
		rotate_parallel_points();
		update_next();
		
		ComponentViewInterface first_view = getComponentView(0);
		
		first_view.draw(c);
		
		float i1_x = (float) p11s.get(0).re;
		float i1_y = (float)(p11s.get(0).im - first_view.getStrokeWidth()/2.0);
		
		float i2_x = (float) p22s.get(0).re;
		float i2_y = (float)(p22s.get(0).im - first_view.getStrokeWidth()/2.0);
		
		c.drawLine((float)p11s.get(0).re, (float)p11s.get(0).im, (float)p12s.get(0).re, (float)p12s.get(0).im, p); 
		c.drawLine((float)p21s.get(0).re, (float)p21s.get(0).im, (float)p22s.get(0).re, (float)p22s.get(0).im, p); 
		
		if (size() <= 1) return;
		
		for (int i = 1; i < size(); i++)
		{
			Complex p1 = p11s.get(i);
			float f1_x = (float) p1.re;
			float f1_y = (float) p1.im;

			Complex p2 = p22s.get(i);
			float f2_x = (float) p2.re;
			float f2_y = (float) p2.im;
			
			if (i == size() - 1)
			{
				f1_y += first_view.getStrokeWidth()/2.0;
				f2_y += first_view.getStrokeWidth()/2.0;				
			}
			
			//This draws the vertical lines
			c.drawLine(i1_x, i1_y, f1_x, f1_y, p);

			c.drawLine(i2_x, i2_y, f2_x, f2_y, p);

			i1_x = (float) p1.re; i1_y = (float) p1.im;
			i2_x = (float) p2.re; i2_y = (float) p2.im;
			
			//Draw the horizontal lines
			//From p11 to p12
			//From p21 to p22
			c.drawLine((float)p11s.get(i).re, (float)p11s.get(i).im, (float)p12s.get(i).re, (float)p12s.get(i).im, p); 
			
			c.drawLine((float)p21s.get(i).re, (float)p21s.get(i).im, (float)p22s.get(i).re, (float)p22s.get(i).im, p); 
			
			getComponentView(i).draw(c);
		}
	}
	
	/*~~~~~~~~~~~~~~~~~~~~~~SERIES DRAWING STUFF~~~~~~~~~~~~~~~~~~*/
	private void init_series()
	{
		p = new Paint();
		p.setColor(Color.BLACK);
		p.setStrokeWidth(ComponentView.stroke_width);
		next = new Complex(0,0);	
		recommended_origin = new Complex(0,0);
		
		if (size() < 2) return;
		
		for (int i = 1; i < size(); i++)
		{
			ComponentViewInterface prev = this.getComponentView(i-1);
			ComponentViewInterface next = this.getComponentView(i);
			prev.setNext(next);
		}
	}
	
	public float series_width()
	{
		this.width = 0.0;
		for (int i = 0; i < size(); i++)
		{
			width += this.getComponentView(i).getWidth();
		}
		return (float)width;
	}
	
	public float series_height()
	{
		ComponentViewInterface c = this.getComponentView(size()-1);
		
		float height  = c.getHeight();
		double starting_y = c.getXY().im;
		
		for (int i = size() - 2; 0 <= i; i--)
		{
			c = this.getComponentView(i);
			
			if (0.01 < Math.abs(c.getXY().im() - starting_y) ) break;
			
			if (height < c.getHeight())
			{
				height = c.getHeight();
			}
		}
		return height;
	}
	
	//Class dependant implementation
	public float get_lowest_y()
	{
		return (float)(this.grab_point.im + this.getHeight()/2.0);
	}
	
	public void easy_series_arrange(Canvas c)
	{		
		float width  = c.getWidth();
		float height = c.getHeight();
		
		boolean rotate180 = false;
		
		if ( rotation.re < 0.0 ) 
		{
			rotate180 = true;
		}
		
		Complex xy = this.get_preferred_grab_point(this.getXY(), width, height, 
				0, 0, rotate180, false);
		
		this.setXY((float)xy.re, (float)xy.im);
	}
	
	public Complex get_preferred_grab_point(Complex starting_grab_point, float screen_width, float screen_height, 
			float highest_y, float lowest_y, boolean rotate180, boolean justRotated)
	{		
		//The case where it fits horizontally but not vertically
		//The case where it doesn't fit horizontally and vertically doesn't matter
		//The case where it's chillin'
		if (this.orientation == SERIES)
		{
			ComponentViewInterface first = this.getComponentView(0);
			Complex ret =  first.get_preferred_grab_point(starting_grab_point, 
					screen_width, screen_height, highest_y, lowest_y, rotate180, justRotated);
			first.setXY((float)ret.re, (float)ret.im);
			
			return ret;
		}
		return RecursiveSeriesDrawingUtility.get_preferred_grab_point
				(this, 
				starting_grab_point, 
				screen_width, 
				screen_height, 
				highest_y,
				lowest_y,
				rotate180,
				justRotated);
	}
	
	@Override
	public Object getAccessory(Schematic call_back)
	{		
		return this;
	}

	@Override
	public boolean setShrink(int new_text_size) 
	{
		boolean result = true;
		
		result &= this.collapseView.setShrink(new_text_size);
		
		for (int i = 0; i < size(); i++)
		{
			result &= this.getComponentView(i).setShrink(new_text_size);
		}
		return result;
	}
	
	@Override
	public int shrink() 
	{
		int result = 0;
	
		boolean canCollapseStillShrink = true;
		
		int depth = this.serial.size();
		
		for (int j = 0; j < depth; j++)
		{
			canCollapseStillShrink &= 0 < collapseView.shrink() ? true : false ;
			
			for (int i = 0; i < size(); i++)
			{
				result |= this.getComponentView(i).shrink();
			}	
		}
	
		if (this.collapse) { result |= canCollapseStillShrink ? (1 << COLLAPSED_CAN_SHRINK) : 0; }
		return result;
	}

	@Override
	public void resetShrink() 
	{
		this.collapseView.resetShrink();
		
		for (int i = 0; i < size(); i++)
		{
			this.getComponentView(i).resetShrink();
		}
	}
}
