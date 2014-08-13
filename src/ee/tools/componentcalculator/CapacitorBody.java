package ee.tools.componentcalculator;

import java.util.ArrayList;
import java.util.LinkedList;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.Log;

public class CapacitorBody implements Body
{
	double body_height = 80;
	double body_width  = 50;
	static final double W_OVER_H = 50.0/80.0;

	float x, y;
	
	Paint body_paint;
	
	Path left_plate, right_plate;
		
	static final int SW = 0, NW = 1, NE = 2, SE = 3;
	
	Complex rotate;
	
	Complex[] left_plate_pnts, right_plate_pnts;
	
	LinkedList<Integer> serial;
	
	public CapacitorBody(LinkedList<Integer> serial, double height)
	{
		this.serial = serial;
		body_width = W_OVER_H * height;
		body_height = height;
		init();
	}

	public CapacitorBody(LinkedList<Integer> serial)
	{
		this.serial = serial;
		init();
	}

	private void init()
	{
		left_plate_pnts = new Complex[4];
		right_plate_pnts = new Complex[4];
		
		for (int i = 0; i < left_plate_pnts.length; i++)
		{
			left_plate_pnts[i]  = new Complex(0,0);
			right_plate_pnts[i] = new Complex(0,0);
		}
		
		body_paint = new Paint();
		body_paint.setColor(Color.GRAY);
	
		this.x = 0;
		this.y = 0;
		
		rotate = new Complex(1,0);
		
		left_plate  = new Path();
		right_plate = new Path();
		
		recalculate();
	}
	
	public float getWidth() { return (float)body_width; }

	public float getHeight() { return (float)body_height; }

	public void setX(float x) 
	{
		if (x != this.x)
		{
			this.x = x;
			recalculate();
		}
	}

	public void setY(float y) 
	{
		if (y != this.y)
		{
			this.y = y;
			recalculate();
		}
	}

	public void setOrigin(Complex c)
	{
		this.x = (float)c.re;
		this.y = (float)c.im;
		recalculate();
	}

	public void setValue(double val) {}
	
	public void setAngle(double angle)
	{
		rotate.re = Math.cos(angle);
		rotate.im = Math.sin(angle);
		recalculate();
	}
	
	public void setSerialNumber(LinkedList<Integer> serial ) {}
	
	private void recalculate()
	{
		//the distance between plates
		double plate_width = body_width/4;
		
		left_plate_pnts[SW].re = 0;           left_plate_pnts[SW].im = 0;
		
		left_plate_pnts[NW].re = 0;           left_plate_pnts[NW].im = body_height;
		
		left_plate_pnts[NE].re = plate_width; left_plate_pnts[NE].im = body_height;

		left_plate_pnts[SE].re = plate_width; left_plate_pnts[SE].im = 0;

	    right_plate_pnts[SW].re = body_width - plate_width;
	    
	    right_plate_pnts[SW].im = 0;
	    
	    right_plate_pnts[NW].re = body_width - plate_width;
	    
	    right_plate_pnts[NW].im = body_height;
	    
	    right_plate_pnts[NE].re = body_width; right_plate_pnts[NE].im = body_height;
	    
	    right_plate_pnts[SE].re = body_width; right_plate_pnts[SE].im = 0;
	    
	    for (int i = 0; i < right_plate_pnts.length; i++)
	    {
	    	right_plate_pnts[i] = right_plate_pnts[i].times(rotate);
	    	 left_plate_pnts[i] =  left_plate_pnts[i].times(rotate);
	    	
	    	right_plate_pnts[i].re += x; right_plate_pnts[i].im += y;
	    	 left_plate_pnts[i].re += x;  left_plate_pnts[i].im += y;
	    }
	    
	    left_plate.rewind();
	    right_plate.rewind();
	    
	    float xl = (float) left_plate_pnts[SW].re;
	    float yl = (float) left_plate_pnts[SW].im;
	    
	    float xr = (float) right_plate_pnts[SW].re;
	    float yr = (float) right_plate_pnts[SW].im;
	    
	    left_plate.moveTo(xl, yl);
	    right_plate.moveTo(xr, yr);
	    
	    for (int i = 1; i < 5; i++)
	    {
	    	xl = (float) left_plate_pnts[i%4].re;
		    yl = (float) left_plate_pnts[i%4].im;
		    xr = (float) right_plate_pnts[i%4].re;
		    yr = (float) right_plate_pnts[i%4].im;
		    
		    left_plate.lineTo(xl, yl);
		    right_plate.lineTo(xr, yr);
	    }	    
	}

	public boolean isIn(Complex pnt)
	{
		ArrayList<Complex> l_arr, r_arr;
		l_arr = new ArrayList<Complex>();
		r_arr = new ArrayList<Complex>();
		for (int i = 0; i < left_plate_pnts.length; i++)
		{
			l_arr.add(left_plate_pnts[i]);
			r_arr.add(right_plate_pnts[i]);
		}
		return InOrOut.inOrOut(l_arr, pnt) || InOrOut.inOrOut(r_arr, pnt);
	}
	
	@Override
	public void draw(Canvas c) {
		c.drawPath(left_plate, body_paint);		
		c.drawPath(right_plate, body_paint);		
	}

	@Override
	public void saveInstanceState(Bundle state) {
		LinkedList<Integer> prefix_ints = this.serial;
		String prefix = "Body";
		for (Integer i : prefix_ints)
		{
			prefix += i.toString();
		}
		state.putFloat(prefix + "x", this.x);
		state.putFloat(prefix + "y", this.y);
		state.putDouble(prefix + "rotate.re", this.rotate.re);
		state.putDouble(prefix + "rotate.im", this.rotate.im);	
	}

	@Override
	public void restoreInstanceState(Bundle saved) {
		LinkedList<Integer> prefix_ints = this.serial;
		String prefix = "Body";
		for (Integer i : prefix_ints)
		{
			prefix += i.toString();
		}
		
		this.x = saved.getFloat(prefix + "x");
		this.y = saved.getFloat(prefix + "y");
		
		this.rotate.re = saved.getDouble(prefix + "rotate.re");
		this.rotate.im = saved.getDouble(prefix + "rotate.im");
	}
}
