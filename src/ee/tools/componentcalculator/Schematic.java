package ee.tools.componentcalculator;

import java.util.LinkedList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.widget.LinearLayout;
import ee.tools.componentcalculator.components_toolbox.Complex;
import ee.tools.componentcalculator.components_toolbox.ComponentViewInterface;
import ee.tools.componentcalculator.components_toolbox.ComponentViewSettings;
import ee.tools.componentcalculator.components_toolbox.ComponentsView;
import ee.tools.model.Component;

/*
 * All a Schematic does is draw a ComponentsView, and direct touch movements to it.
 * It also asks its ComponentsView if there is a Component at an X,Y coord,
 * If there is, then it get that Component and retrieves its Accessory. 
 *   With the accessory, it asks if it is a View or an Intent.
 *   If it is a View, it adds it to the settings_container, which is a view that displays settings.
 *   If it is an Intent, it starts it. 
 *   
 *If a Schematic were to save itself, then it simply passes the Bundle to its ComponentsView.
 *Schematic itself doesn't add any useful information to the saving process. 
 */

public class Schematic extends View implements BackPressedListener
{
	String prefix = "schematic";
	
	Context current_context;
	
	final int INVALID_PNTR_ID = -1;
	int pntr1 = INVALID_PNTR_ID, pntr2 = INVALID_PNTR_ID;
		
	float i_x, i_y, f_x, f_y;
	float pntr2_x, pntr2_y;
		
	ComponentViewInterface series;
		
	LinearLayout settings_container;

	FragmentActivity current_activity;
	
	LinkedList<ComponentViewInterface> component_stack;
	LinkedList<ComponentSettings> settings_stack;

	private boolean fit_component_to_screen;
		
	public Schematic(Context context, LinearLayout settings_container) 
	{
		super(context);
		this.setBackgroundColor(Color.CYAN);
		this.current_context = context;
		this.current_activity = (FragmentActivity)context;
		this.settings_container = settings_container;
		this.settings_stack = new LinkedList<ComponentSettings>();
		invalidate();
	}
	
	public void setSeries(ComponentViewInterface series) 
	{
		if (series == null) return;
		this.series = series;
		this.component_stack = new LinkedList<ComponentViewInterface>();
		this.component_stack.add(series);
	}
	
	public void setFitToScreen(boolean fit)
	{
		fit_component_to_screen = fit;
	}
	
	public ComponentViewInterface getCurrent() 
	{
		if (this.component_stack == null) return null;
		return this.component_stack.get(this.component_stack.size() - 1); 
	}
	
	public void saveInstanceState(Bundle state) 
	{
		if (series != null)
		{
			//save the serial numbers in component_stack
			
			state.putInt(prefix+"component_stack_size", component_stack.size());
			
			for (int i = 0; i < component_stack.size(); i++)
			{
				LinkedList<Integer> serial = this.component_stack.get(i).getSerialNumber();
				
				int[] serial_arr = new int[serial.size()];
				
				for (int j = 0; j < serial.size(); j++)
				{
					serial_arr[j] = serial.get(j).intValue();
				}
				state.putIntArray(prefix+"component_stack_element"+i, serial_arr);
			}
			
			state.putInt(prefix+"settings_stack_size", settings_stack.size());
			
			for (int i = 0; i < settings_stack.size(); i++)
			{
				String settings_prefix = prefix + "ComponentSettings" + i;
				settings_stack.get(i).saveInstanceState(settings_prefix, state);
			}
			series.saveInstanceState(state);
		}
	}

	public void restoreInstanceState(Bundle state)
	{
		if (series != null)
		{
			series.restoreInstanceState(state);
			
			component_stack = new LinkedList<ComponentViewInterface>();
			
			int component_stack_size = state.getInt(prefix + "component_stack_size");
			
			if (1 < component_stack_size)
				this.current_activity.getActionBar().setDisplayHomeAsUpEnabled(true);
			else
				this.current_activity.getActionBar().setDisplayHomeAsUpEnabled(false);
	
			for (int i = 0; i < component_stack_size; i++)
			{
				String key = prefix + "component_stack_element"+i;
				int[] s_array = state.getIntArray(key);
				LinkedList<Integer> search_serial = new LinkedList<Integer>();
				for (int element: s_array) search_serial.add(element);
				
				Log.d(prefix, "Searching for: " + search_serial.toString());
				
				ComponentViewInterface cv = null;
				
				if (series.getClass() == ComponentsView.class)
					cv = ((ComponentsView)series)
						.findBySerialNumber(search_serial);
				
				if (cv != null)
				{
					Log.d(prefix, "Found: " + cv.toString());
				
					component_stack.add(cv);
				}
				else Log.d(prefix, "Got null...");
			}
			
			settings_stack.removeAll(settings_stack);
			
			int settings_stack_size = state.getInt(prefix+"settings_stack_size");
			
			for (int i = 0; i < settings_stack_size; i++)
			{
				String settings_prefix = prefix + "ComponentSettings" + i;
				ComponentSettings setting = new ComponentSettings();
				setting.restoreInstanceState(settings_prefix, state);
				settings_stack.add(setting);
			}
			
			getCurrent().setCollapse(false);
			getCurrent().setAngle(0.0);
			getCurrent().setXY(0,0);
			invalidate();
		}
	}
	
	private float distance(float i_x, float i_y, float f_x, float f_y) 
	{
		float x = f_x - i_x;
		float y = f_y - i_y;
		return (float) Math.sqrt(y * y + x * x);
	}
		
	public boolean onTouchEvent(MotionEvent me)
	{
		if (!this.isEnabled()) return false;
		
		super.onTouchEvent(me);
		
		if (this.component_stack.size() < 1) return false;
		
		if (this.getCurrent() == null) return false;

		int pntr_count = me.getPointerCount();
		
		int action = me.getActionMasked();
		
		ComponentViewInterface CURRENT = this.getCurrent();
		
		switch(action)
		{
		case MotionEvent.ACTION_DOWN:
			i_x = me.getX();
			i_y = me.getY();
			pntr1 = me.getPointerId(0);
			
			Complex pnt = new Complex(i_x, i_y);
			ComponentViewInterface comp = CURRENT.isIn(pnt);
			
			if (comp != null)
			{
				Object accessory = comp.getAccessory(this);
				
				if (accessory != null)
				{
					if (accessory.getClass() == ComponentViewSettings.class)
					{
						settings_container.removeAllViews();
				
						settings_container.addView((ComponentViewSettings)accessory);
				
						Log.d("Schematic", ">>>" + ((Component)comp).toString());
					}
					else if (accessory instanceof ComponentViewInterface)
					{
						ComponentViewInterface detail_comp = (ComponentViewInterface) accessory;
						component_stack.add(detail_comp);
						
						ComponentSettings detail_settings = new ComponentSettings();
						detail_settings.collapsed = detail_comp.isCollapsed();
						detail_settings.rotation  = detail_comp.getAngle();
						detail_settings.xy        = detail_comp.getXY();
						
						settings_stack.add(detail_settings);
						
						detail_comp.setCollapse(false);
						detail_comp.setXY(0,0);
						detail_comp.setAngle(0.0);
						
						this.current_activity.getActionBar().setDisplayHomeAsUpEnabled(true);

						invalidate();
					}
				}
			}
			else
			{
				settings_container.removeAllViews();
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
				
					CURRENT.setXY((float)move.re, (float)move.im);
					
					i_x = f_x;
					i_y = f_y;
					invalidate();
				}
			}
			if (pntr2 != INVALID_PNTR_ID)
			{
				pntr2_x = me.getX(pntr2);
				pntr2_y = me.getY(pntr2);
			}
			break;
			
			case MotionEvent.ACTION_UP:
				Log.d(prefix, CURRENT.toString());
				Log.d("!!!", "X: " + f_x +" Y: " + f_y);
				
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
		ComponentDrawingUtility.draw(getCurrent(), c, this.fit_component_to_screen);
	}

	@Override
	public boolean backPressedAction() {
		if (this.component_stack.size() > 1)
		{
			ComponentSettings restore = settings_stack.getLast();
			getCurrent().setXY(restore.xy);
			getCurrent().setAngle(restore.rotation);
			getCurrent().setCollapse(restore.collapsed);
			this.component_stack.removeLast();
			this.settings_stack.removeLast();
			
			getCurrent().setCollapse(false);
			getCurrent().setAngle(0.0);
			getCurrent().setXY(0,0);

			if (this.component_stack.size() == 1)
				this.current_activity.getActionBar().setDisplayHomeAsUpEnabled(false);

			invalidate();
			return true;
		}
		return false;
	}
	
	class ComponentSettings
	{
		boolean collapsed;
		Complex xy;
		double rotation;
		
		void saveInstanceState(String prefix, Bundle b)
		{
			b.putBoolean(prefix+"collapsed", collapsed);
			b.putDouble(prefix+"x", xy.re);
			b.putDouble(prefix+"y", xy.im);
			b.putDouble(prefix+"rotation", rotation);
		}
		
		void restoreInstanceState(String prefix, Bundle b)
		{
			collapsed = b.getBoolean(prefix+"collapsed");
			double x = b.getDouble(prefix+"x");
			double y = b.getDouble(prefix+"y");
			xy = new Complex(x,y);
			rotation = b.getDouble(prefix+"rotation");
		}
	}	
}
