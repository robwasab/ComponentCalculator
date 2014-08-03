package ee.tools.componentcalculator;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;

public class Tap2SeeMoreBody implements Body{

	static final float body_height = 80, body_width = 200;
	float x, y;
	
	Paint body_paint;
	
	Path body_path;
	
	Complex nw, ne, se, sw, rotate;
	List<Complex> outline;
	
	private Paint text_paint;
	private String text;
	private float text_width;

	public Tap2SeeMoreBody()
	{
		body_paint = new Paint();
		body_paint.setColor(Color.GRAY);
		text_paint = new Paint();
		text_paint.setColor(Color.MAGENTA);
		text_paint.setTextSize(30);
		text = "Tap to Expand";
		text_width = text_paint.measureText(text);
		
		this.x = 0;
		this.y = 0;
		nw = new Complex(0,0);
		ne = new Complex(0,0);
		se = new Complex(0,0);
		sw = new Complex(0,0);
		outline = new ArrayList<Complex>();
		outline.add(nw);
		outline.add(ne);
		outline.add(se);
		outline.add(sw);
		
		rotate = new Complex(1,0);
		
		body_path = new Path();
		
		recalculate();
	}
	
	public float getWidth() { return body_width; }

	public float getHeight() { return body_height; }

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

	public void setAngle(double angle)
	{
		rotate.re = Math.cos(angle);
		rotate.im = Math.sin(angle);
		recalculate();
	}
	
	private void recalculate()
	{
		nw.re = 0; nw.im = 0;
		ne.re = 0 + body_width; ne.im = 0;
		se.re = 0 + body_width; se.im = 0 + body_height;
		sw.re = 0; sw.im = 0 + body_height;
		
		nw = nw.times(rotate);
		ne = ne.times(rotate);
		se = se.times(rotate);
		sw = sw.times(rotate);
		
		nw.re += x; nw.im += y;
		ne.re += x; ne.im += y;
		se.re += x; se.im += y;
		sw.re += x; sw.im += y;
		
		body_path.rewind();
		body_path.moveTo((float)nw.re, (float)nw.im);
		body_path.lineTo((float)ne.re, (float)ne.im);
		body_path.lineTo((float)se.re, (float)se.im);
		body_path.lineTo((float)sw.re, (float)sw.im);
		body_path.lineTo((float)nw.re, (float)nw.im);
		
		outline.set(0, nw);
		outline.set(1, ne);
		outline.set(2, se);
		outline.set(3, sw);
	}

	public boolean isIn(Complex pnt)
	{
		return InOrOut.inOrOut(outline, pnt);
	}
	
	@Override
	public void draw(Canvas c) {
		c.drawPath(body_path, body_paint);		
		double x_text = (ne.re + nw.re + sw.re + se.re) / 4.0;
		double y_text = (ne.im + nw.im + sw.im + se.im) / 4.0;
		
		x_text -= text_width/2.0;
		
		c.drawText(text, (float)x_text, (float)y_text, text_paint);
	}

	@Override
	public void saveInstanceState(Bundle state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void restoreInstanceState(Bundle saved) {
		// TODO Auto-generated method stub
		
	}

}
