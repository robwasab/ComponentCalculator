package ee.tools.componentcalculator.components_toolbox;

import java.util.LinkedList;

import ee.tools.componentcalculator.Schematic;
import ee.tools.model.Component;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;

public interface ComponentViewInterface extends Comparable<Component>{

	public void setSerialNumber(LinkedList<Integer> serial);
	
	public LinkedList<Integer> getSerialNumber();
	
	/*
	 * set methods must immediately update how the component will be drawn
	 * Calling code assumes changes are immediate!
	 */
	
	public void setCollapse(boolean collapse);
	
	public void setXY(float x, float y);
	
	public void setXY(Complex c);
	
	public boolean setShrink(int new_text_size);
	
	public int shrink();
	
	public void resetShrink();
	
	public void setNext(ComponentViewInterface cvi);
	
	public ComponentViewInterface getNext();
	
	public Complex getNextPoint();

	public float getWidth();
	
	public float getHeight();
	
	public float get_lowest_y();
	
	public float getPadding();
	
	public Complex getXY();
	
	public void setAngle(double angle);
	
	public double getAngle();
	
	public float getStrokeWidth();
	
	public void draw(Canvas c);

	public Paint getLinePaint();
	
	public String getName();
	
	public double getValue();
	
	public void saveInstanceState(Bundle state);

	public void restoreInstanceState(Bundle saved);
	
	//return the component that surrounds the pnt
	//else return null
	public ComponentViewInterface isIn(Complex pnt);
	
	public boolean isCollapsed();
	
	public Object getAccessory(Schematic call_back);
	
	//Recursive method
	//See RecursiveSeriesDrawingUtility
	//Both ComponentsView and ComponentView use this function
	public Complex get_preferred_grab_point(
			Complex starting_grab_point,
			float screen_width,
			float screen_height, 
			float highest_y,
			float lowest_y, 
			boolean rotate180,
			boolean justRotated);

}
