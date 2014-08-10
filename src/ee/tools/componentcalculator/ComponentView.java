package ee.tools.componentcalculator;
import java.util.LinkedList;

import ee.tools.model.EngNot;
import ee.tools.model.Component;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class ComponentView extends Component implements ComponentViewInterface {
	
	ComponentViewInterface next_comp = null;
	
	/* View Stuff */
	static float axial_length = 350;
	static float stroke_width = 8;
	
	double angle = 0;
	
	Body body;
	float origin_x = 0, origin_y;
	
	Complex p11, p12, p21, p22, rotate, body_origin;
	
	Paint line_paint;
	Paint text_paint;
	
	String tag;
	
	boolean draw_value = true;
	private LinkedList<Integer> serial;

	private float text_width;
	/* End View Stuff */
	
	public ComponentView(LinkedList<Integer> serial, Body b) 
	{
		super(0);
		this.setSerialNumber(serial);
		body = b;
		init();
		move_body();
	}

	public ComponentView(LinkedList<Integer> serial, Body b, double value)
	{
		super(value);
		this.setSerialNumber(serial);
		body = b;
		init();
		move_body();		
	}
	
	public ComponentView(LinkedList<Integer> serial, Body b, double value, int qnty)
	{
		super(value, qnty);
		this.setSerialNumber(serial);
		body = b;
		init();
		move_body();
	}
	
	private void move_body() {
		if (body != null )
		{
			body.setX( (float)(origin_x + axial_length/2.0 - body.getWidth()/2.0) );
			body.setY( (float)(origin_y - body.getHeight()/2.0) );			
		
			body_origin.re = axial_length/2.0 - body.getWidth()/2.0;
			body_origin.im = -1 * body.getHeight()/2.0;
		
			body_origin = body_origin.times(rotate);
		
			body_origin.re += origin_x;
			body_origin.im += origin_y;
		
			body.setOrigin(body_origin);
		}
	}
	
	//Do nothing... Already Collapsed enough
	public void setCollapse(boolean c) { };
	
	public void setX(float x) 
	{
		this.origin_x = x;
		recalculate();
		move_body();
	}
	
	public void setY(float y) 
	{
		this.origin_y = y;
		recalculate();
		move_body();
	}
	
	public void setXY(float x, float y)
	{
		this.origin_x = x;
		this.origin_y = y;
		recalculate();
		move_body();
	}
	
	public void setXY(Complex c)
	{
		this.origin_x = (float)c.re;
		this.origin_y = (float)c.im;
		recalculate();
		move_body();		
	}
	
	public float getWidth() 
	{
		return (float)axial_length;
	}
	
	public float getHeight() 
	{
		if (body != null )
		{
			return (float)body.getHeight(); 
		}
		return (float)0.0;
	}
	
	public Complex getNextPoint() { return p22; }
		
	public Complex getRotation() { return rotate; }
	
	public Paint getLinePaint() { return this.line_paint; }
	
	public double getAngle() { return angle; }

	public String getName() { return "SINGLE"; }
	
	public float getStrokeWidth() {
		return stroke_width;
	}
	
	public void setAngle(double angle)
	{
		this.angle = angle;
		rotate.re = Math.cos(angle);
		rotate.im = Math.sin(angle);
		recalculate();
		move_body();
		if (body != null) body.setAngle(angle);
	}
	
	private void init() {
		if (body != null)
		{
			origin_y = (float) (body.getHeight() / 2.0);
		}
		else
		{
			origin_y = 0;
		}
		line_paint = new Paint();
		line_paint.setColor(Color.BLACK);
		line_paint.setStrokeWidth(stroke_width);
		text_paint = new Paint();
		text_paint.setColor(Color.RED);
		text_paint.setTextSize(30);
		text_width = text_paint.measureText("XXX.X");
		p11 = new Complex(0,0);
		p12 = new Complex(0,0);
		p21 = new Complex(0,0);
		p22 = new Complex(0,0);
		rotate = new Complex(1,0);
		body_origin = new Complex(0,0);
		tag = "Body";
		recalculate();
	}
	
	private void recalculate()
	{
		p11.re  = 0;
		
		float body_width = 0;
		//float body_height = 0;
		
		if (body != null)
		{
			body_width = body.getWidth();
			//body_height = body.getHeight();
			axial_length = body.getWidth() + 2 * this.text_width;
		}
		p12.re  = (float) (axial_length/2.0 - body_width/2.0);
		p11.im  = 0;
		p12.im  = 0;
		
		p21.re = (float) (axial_length/2.0 + body_width/2.0);
		p22.re = (float) (axial_length);
		p21.im = 0;
		p22.im = 0;
		
		p11 = p11.times(rotate);
		p12 = p12.times(rotate);
		p21 = p21.times(rotate);
		p22 = p22.times(rotate);
		
		p11.re += origin_x;
		p11.im += origin_y;
		p12.re += origin_x;
		p12.im += origin_y;
		p21.re += origin_x;
		p21.im += origin_y;
		p22.re += origin_x;
		p22.im += origin_y;	
	}
	
	public void draw(Canvas c)
	{
		c.drawLine((float)p11.re, (float)p11.im, (float)p12.re, (float)p12.im, line_paint);
		
		c.drawLine((float)p21.re, (float)p21.im, (float)p22.re, (float)p22.im, line_paint);
		
		String value = EngNot.toEngNotation(super.getValue());
		float x;
		float y;
		
		//if not rotated...
		if (this.angle  <  1)
		{
			y = (float) (this.origin_y - stroke_width/2.0);
			x = this.origin_x;	
		}
		else
		{
			y = (float) (this.origin_y - stroke_width/2.0);
			x = this.origin_x - this.text_width;				
		}
		
		if (body != null) 
		{
			body.draw(c); 
		}
		c.drawText(value, x, y, text_paint);		
		
		if (next_comp != null)
		{
			next_comp.draw(c);
			
			RecursiveSeriesDrawingUtility.link(this, next_comp, c);
		}
	}
	
	public void saveInstanceState(Bundle state)
	{
		LinkedList<Integer> prefix_ints = this.getSerialNumber();
		String prefix = "";
		for (Integer i : prefix_ints)
		{
			prefix += i.toString();
		}
		
		Log.d("!!!", "ComponentView Saving..."+prefix + " " + this.getValue());
		
		state.putFloat(prefix+"origin_x", origin_x);
		state.putFloat(prefix+"origin_y", origin_y);
		state.putDouble(prefix+"rotate_re", rotate.re);
		state.putDouble(prefix+"rotate_im", rotate.im);
		state.putString(prefix+"class", this.getClass().toString());
		state.putDouble(prefix+"value", super.getValue());
		state.putInt(prefix+"qnty", super.getQnty());
		
		if (body != null)
		{
			state.putString(prefix+"type", body.getClass().toString());
			body.saveInstanceState(state);
		}	
	}
	
	public void restoreInstanceState(Bundle saved)
	{
		LinkedList<Integer> prefix_ints = this.getSerialNumber();
		String prefix = "";
		for (Integer i : prefix_ints)
		{
			prefix += i.toString();
		}
		Log.d("!!!", "ComponentView Restoring..."+prefix);
		
		this.origin_x = saved.getFloat(prefix + "origin_x");
		this.origin_y = saved.getFloat(prefix + "origin_y");
		this.rotate.re = saved.getDouble(prefix + "rotate_re");
		this.rotate.im = saved.getDouble(prefix + "rotate_im");
		String type = saved.getString(prefix+"type");
		super.setValue(saved.getDouble(prefix+"value"));
		super.setQnty(saved.getInt(prefix+"qnty"));
		if (type != null)
		{
			if (type.equals(ResistorBody.class.toString()))
			{
				try {
					this.body = new ResistorBody(this.getSerialNumber(), 123, 0);
					this.body.restoreInstanceState(saved);
				} catch (ResistorException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		recalculate();
	}
	
	public void setSerialNumber(LinkedList<Integer> serial) {
		this.serial = new LinkedList<Integer>();
		for (Integer i : serial)
		{
			this.serial.add(i);
		}
		if (body != null) body.setSerialNumber(serial);
	}	
	
	@Override
	public void setValue(double value)
	{
		super.setValue(value);
		if (body != null) body.setValue(value);
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
	
	//Class dependant implementation
	public float get_lowest_y()
	{
		return (float)(this.p11.im + this.getHeight()/2.0);
	}
		
	public Complex get_preferred_grab_point(Complex starting_grab_point, float screen_width, float screen_height, 
			float highest_y, float lowest_y, boolean rotate180, boolean justRotated)
	{
		return RecursiveSeriesDrawingUtility.get_preferred_grab_point(
				this,
				starting_grab_point,
				screen_width,
				screen_height,
				highest_y,
				lowest_y,
				rotate180,
				justRotated);
	}

	public float getPadding()
	{
		return this.text_width;
	}
	
	@Override
	public void setNext(ComponentViewInterface cvi) {
		this.next_comp = cvi;
	}

	@Override
	public ComponentViewInterface getNext() {
		return next_comp;
	}

	@Override
	public Complex getXY() {
		
		return new Complex(p11.re, p11.im);
	}		
	
	@Override
	public ComponentViewInterface isIn(Complex pnt)
	{
		if (body != null)
		{
			if (body.isIn(pnt))
			{
				return this;
			}
		}
		return null;
	}
	
	@Override
	public Object getAccessory(Schematic call_back)
	{
		return new ComponentViewSettings(call_back, this);
	}
}
