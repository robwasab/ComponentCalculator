package ee.tools.componentcalculator;

import java.util.ArrayList;
import java.util.LinkedList;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TabWidget;
import android.os.Build;
import android.os.Bundle;

public class FragmentDrawing extends Fragment{
	
	LinearLayout root_layout;
	SurfaceViewSchematic drawing;
	
	Context current_context;
	Activity current_activity;
	String tag = "FragmentDrawing";
	
	public FragmentDrawing() {
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{	
		super.onCreateView(inflater, container, savedInstanceState);
		
		View rootView = inflater.inflate(R.layout.fragment_drawing, container,
				false);
		
		current_context  = inflater.getContext();
		current_activity = this.getActivity();	
		
		root_layout = (LinearLayout) rootView.findViewById(R.id.drawing_layout);
		
		drawing = new SurfaceViewSchematic(current_context);
		
		root_layout.addView(drawing);
		root_layout.setBackgroundColor(Color.GREEN);
		
		return rootView;
	}

	public void onStop() { super.onStop(); android.util.Log.d(tag, "Stopping Drawing Fragment"); }
	
	public void onDestroyView() { super.onDestroyView(); android.util.Log.d(tag, "Destroying Drawing Fragment"); }
	
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		android.util.Log.d(tag, "Saving...");
	}
	
	class SurfaceViewSchematic extends SurfaceView
	{
		float i_x = 0, i_y = 0, f_x = 0, f_y = 0, last_x, last_y;
		
		//this is for measuring the length of a line drawn on the screen
		//when a finger touches down on the screen, these get inited with 
		//the finger coords.
		//As the finger moves around, just find the current distance relative
		//to i_x_ruler, i_y_ruler
		//report the distance to visual text to you can see how long the line is
		float i_x_ruler, i_y_ruler, ruler_length, f_x_ruler, f_y_ruler;
		
		Paint paint, ruler_paint, text_paint;
		
		boolean draw_ruler = false;
		
		LinkedList<Float> lines;
		float[] f_lines;
		
        Context current_context;
        
		public SurfaceViewSchematic(Context context) {
			super(context);
			current_context = context;
			this.setBackgroundColor(Color.MAGENTA);
			paint = new Paint();
			paint.setColor(Color.BLUE);
			paint.setStrokeWidth(10);
			
			ruler_paint = new Paint();
			ruler_paint.setColor(Color.GREEN);
			ruler_paint.setStrokeWidth(10);
			
			text_paint = new Paint();
			text_paint.setColor(Color.BLACK);
			text_paint.setTextSize(50);
			
			lines = new LinkedList<Float>();
		}
		
		private void remove_last_draw() { lines.removeLast(); lines.removeLast(); lines.removeLast(); lines.removeLast(); }
		
		private float distance(float i_x, float i_y, float f_x, float f_y) 
		{
			float x = f_x - i_x;
			float y = f_y - i_y;
			return (float) Math.sqrt(y * y + x * x);
		}
		
		public boolean onTouchEvent(MotionEvent me)
		{
			super.onTouchEvent(me);
			
			if (me.getPointerCount() > 1) { f_lines = null; lines.removeAll(lines); invalidate(); }
			int action = me.getAction();
			switch(action)
			{
			case MotionEvent.ACTION_DOWN:
				i_x = me.getX();
				i_y = me.getY();
				
				i_x_ruler = i_x;
				i_y_ruler = i_y;
				
				draw_ruler = true;
				break;
				
			case MotionEvent.ACTION_MOVE:
				f_x = me.getX();
				f_y = me.getY();
				
				if (distance(i_x, i_y, f_x, f_y) > 10)
				{
					lines.add(i_x);
					lines.add(i_y);
					lines.add(f_x);
					lines.add(f_y);
					f_lines = convert_linked_list();
					
					i_x = f_x;
					i_y = f_y;
					invalidate();
				}
				
				f_x_ruler = f_x;
				f_y_ruler = f_y;
				
				ruler_length = distance(i_x_ruler, i_y_ruler, f_x_ruler, f_y_ruler);
				break;
				
			case MotionEvent.ACTION_UP:
				draw_ruler = false;
				invalidate();
				break;
			}
			return true;
		}
		
		private float[] convert_linked_list()
		{
			int size = lines.size();
			float[] ret = new float[size];
			for(int i = 0; i < size; i++)
			{
				ret[i] = lines.get(i).floatValue();
			}
			return ret;
		}
		
		public void draw(Canvas c)
		{
			super.draw(c);
			if (f_lines != null) {
			   c.drawLines(f_lines, paint);
			}
			
			c.drawLine(i_x, i_y, f_x, f_y, paint);
			if (draw_ruler)
			{
				c.drawLine(i_x_ruler, i_y_ruler, f_x_ruler, f_y_ruler, ruler_paint);
				c.drawText(Float.toString(ruler_length), i_x_ruler, i_y_ruler, text_paint);
			}
		}
	}
}
