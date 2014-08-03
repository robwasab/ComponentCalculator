package ee.tools.componentcalculator;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.util.Log;

class ResistorBody implements Body
{
	static final float body_height = 80, body_width = 200;
	float x, y;
	
	Paint body_paint;
	
	Path body_path;
	
	Complex nw, ne, se, sw, rotate;
	List<Complex> outline;
	
	double value;
	int tolerance;
	
	//An array of an array of Complex points 
	int COMPLEX_PNTS_INDEX = 0, PATH_INDEX = 1;
	int NW = 0, NE = 1, SE = 2, SW = 3;
	Object[][] bands;
	
	Paint[] band_colors;
		
	ResistorBands resistor_bands;

	private int num_bands;

	private int padding;

	private int band_width;

	private String tag = "Resistor Body";
	
	LinkedList<Integer> serial;
	
	public ResistorBody(LinkedList<Integer> serial, double value, int tolerance) throws ResistorException
	{
		this.value = value;
		this.tolerance = tolerance;
		body_paint = new Paint();
		this.serial = serial;
		
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
		
		resistor_bands = new ResistorBands(value);
		
		main_init();
	}

	private void main_init()
	{
		Paint[] number_band_colors = resistor_bands.getNumberBands();
		
		//plus two for tens power and tolerance;
		this.num_bands = number_band_colors.length + 2;
		
		band_colors = new Paint[num_bands];
		
		for (int i = 0; i < number_band_colors.length; i++) band_colors[i] = number_band_colors[i];
		
		band_colors[num_bands-2] = resistor_bands.getTensPowerBand();
		
		band_colors[num_bands-1] = (Paint)resistor_bands.GOLD[0];
		
		if (number_band_colors.length == 2) body_paint.setColor(Color.rgb(189, 183, 107));
		else  body_paint.setColor(Color.rgb(0, 197, 205));
		
		init_bands();
		
		recalculate();
	}
	
	public void setValue(double value)
	{
		try
		{
			resistor_bands.setValue(value);
			this.value = value;			
			main_init();
		}
		catch (ResistorException re) {}
	}
	
	public void setValue(double value, int tolerance) throws ResistorException
	{
		this.tolerance = tolerance;
		this.value = value;
		resistor_bands.setValue(value);
		main_init();
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
		
		recalculate_bands();
	}
	
	private void recalculate_bands()
	{
		for (int i = 0; i < this.num_bands; i++)
		{
			Complex[] rect = (Complex[])(bands[i][COMPLEX_PNTS_INDEX]);
			Path path = (Path)(bands[i][PATH_INDEX]);
			
			rect[NW].re = i * band_width + padding;
			rect[NW].im = 0;
		
			rect[NE].re = (i * band_width) + band_width - padding;
			rect[NE].im = 0;
		
			rect[SE].re = rect[NE].re;
			rect[SE].im = body_height;
		
			rect[SW].re = rect[NW].re;
			rect[SW].im = body_height;
			
			for (int j = 0; j < 4; j++)
			{
				rect[j] = rect[j].times(rotate);
				rect[j].re += x; rect[j].im += y;
			}
			path.rewind();
			path.moveTo((float)rect[NW].re, (float)rect[NW].im);
			path.lineTo((float)rect[NE].re, (float)rect[NE].im);
			path.lineTo((float)rect[SE].re, (float)rect[SE].im);
			path.lineTo((float)rect[SW].re, (float)rect[SW].im);
			path.lineTo((float)rect[NW].re, (float)rect[NW].im);
		}
	}
	
	private void init_bands()
	{
		band_width = (int) ((body_width - (body_width * 0.1)) / num_bands);
		
		padding = band_width / 6;
		
		bands = new Object[num_bands][];
		
		for (int i = 0; i < num_bands; i++)
		{			
			Complex[] pnts = new Complex[4];
			pnts[NW] = new Complex(0,0);
			pnts[NE] = new Complex(0,0);
			pnts[SE] = new Complex(0,0);
			pnts[SW] = new Complex(0,0);
						
			Object[] band = new Object[2];
			band[COMPLEX_PNTS_INDEX] = pnts;
			band[PATH_INDEX] = new Path();
			
			bands[i] = band;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void setSerialNumber(LinkedList<Integer> serial)
	{
		this.serial = (LinkedList<Integer>)serial.clone();
	}
	
	public boolean isIn(Complex pnt)
	{
		return InOrOut.inOrOut(outline, pnt);
	}
	
	public void draw(Canvas c) {
		c.drawPath(body_path, body_paint);
		
		for (int i = 0; i < num_bands; i++)
		{
			Object[] band = bands[i]; 
			Path path = (Path) band[PATH_INDEX];
			if (path == null) Log.d(tag , i + "Path is null");
			Paint paint = band_colors[i];
			if (paint == null) Log.d(tag, i + "Paint is null");
			
			c.drawPath(path, paint);
		}
	}

	@Override
	public void saveInstanceState(Bundle state) {
		LinkedList<Integer> prefix_ints = this.serial;
		String prefix = "";
		for (Integer i : prefix_ints)
		{
			prefix += i.toString();
		}
		Log.d("!!!", "ResistorBody Saving..."+prefix);
		
		state.putFloat(prefix + "x", this.x);
		state.putFloat(prefix + "y", this.y);
		state.putDouble(prefix + "value", this.value);
		state.putDouble(prefix + "rotate.re", this.rotate.re);
		state.putDouble(prefix + "rotate.im", this.rotate.im);
	}

	@Override
	public void restoreInstanceState(Bundle saved) {
		LinkedList<Integer> prefix_ints = this.serial;
		String prefix = "";
		for (Integer i : prefix_ints)
		{
			prefix += i.toString();
		}
		Log.d("!!!", "ResistorBody Restoring..."+prefix);
		
		this.x = saved.getFloat(prefix + "x");
		this.y = saved.getFloat(prefix + "y");
		double value = saved.getDouble(prefix + "value");
		
		this.setValue(value);
		
		this.rotate.re = saved.getDouble(prefix + "rotate.re");
		this.rotate.im = saved.getDouble(prefix + "rotate.im");
		main_init();
	}
}
