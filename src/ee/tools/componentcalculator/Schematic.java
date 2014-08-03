package ee.tools.componentcalculator;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import ee.tools.model.Component;

class Schematic extends SurfaceView
{
	Context current_context;
	
	final int INVALID_PNTR_ID = -1;
	int pntr1 = INVALID_PNTR_ID, pntr2 = INVALID_PNTR_ID;
		
	float i_x, i_y, f_x, f_y;
	float pntr2_x, pntr2_y;
		
	ComponentsView series;
		
	LinearLayout settings_container;
		
	public Schematic(Context context, LinearLayout settings_container) 
	{
		super(context);
		this.setBackgroundColor(Color.CYAN);
		this.current_context = context;
		this.settings_container = settings_container;
		
		invalidate();
	}
	
	public void setSeries(ComponentsView series) { this.series = series; }
	
	public ComponentsView getSeries() { return series; }
	
	public void saveInstanceState(Bundle state) 
	{
		if (series != null)
			series.saveInstanceState(state);
	}

	public void restoreInstanceState(Bundle state)
	{
		if (series != null)
			series.restoreInstanceState(state);
	}
	
	private float distance(float i_x, float i_y, float f_x, float f_y) 
	{
		float x = f_x - i_x;
		float y = f_y - i_y;
		return (float) Math.sqrt(y * y + x * x);
	}
		
	public boolean onTouchEvent(MotionEvent me)
	{
		super.onTouchEvent(me);

		if (series == null) return false;

		int action = me.getActionMasked();
		
		switch(action)
		{
		case MotionEvent.ACTION_DOWN:
			i_x = me.getX();
			i_y = me.getY();
			pntr1 = me.getPointerId(0);
			
			Complex pnt = new Complex(i_x, i_y);
			ComponentViewInterface comp = series.isIn(pnt);
			if (comp != null)
			{
				View settings = comp.getSettingsView(this);
				
				if (settings != null)
				{
					settings_container.removeAllViews();
				
					settings_container.addView(settings);
				
					//double val = ((Component)comp).getValue();
					Log.d("Schematic", ">>>" + ((Component)comp).toString());
				}
			}
			break;
					
		case MotionEvent.ACTION_MOVE:
			if (pntr1 != INVALID_PNTR_ID)
			{
				f_x = me.getX(pntr1);
				f_y = me.getY(pntr1);
				float d = distance(i_x, i_y, f_x, f_y);
				if (d > 15)
				{
					Complex move = new Complex(me.getX(pntr1), me.getY(pntr1));
					
					Log.d("!!!", "STARTING!");
				
					series.setXY((float)move.re, (float)move.im);
					
					i_x = f_x;
					i_y = f_y;
					invalidate();
				}
			}
			if (pntr2 != INVALID_PNTR_ID)
			{
				pntr2_x = me.getX(pntr2);
				pntr2_y = me.getY(pntr2);
				//float dy = pntr2_y - i_y;
				//float dx = pntr2_x - i_x;
				//double angle = Math.atan(dy/dx);
				
				/*
				if (Math.abs(parallel.rotation.phase() - angle) > 0.02)
				{
					if (dx < 0) angle += Math.PI;
						
					//parallel.setAngle(angle);
					//Log.d(tag, Double.toString(angle));
				}
				*/
			}
			break;
			
			case MotionEvent.ACTION_UP:
				pntr1 = INVALID_PNTR_ID;
				break;
			case MotionEvent.ACTION_POINTER_DOWN:
				pntr2 = me.getPointerId(me.getActionIndex());
				break;
			case MotionEvent.ACTION_POINTER_UP:
				pntr2 = INVALID_PNTR_ID;
				pntr1 = INVALID_PNTR_ID;
				break;
		}
		return true;
	}
				
	public void onDraw(Canvas c)
	{
		super.onDraw(c);
		if (series != null)
			series.draw(c);
	}
}
