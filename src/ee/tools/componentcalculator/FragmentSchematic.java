package ee.tools.componentcalculator;

import java.util.LinkedList;

import ee.tools.model.Component;
import ee.tools.model.Components;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.os.Bundle;

public class FragmentSchematic extends Fragment implements OnKeyListener
{
	Context current_context;
	Activity current_activity;
	LinearLayout root_layout;
	String tag = "FragmentSchematic";

	EditText value_entry;
	
	Schematic schematic;
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		View rootView = inflater.inflate(R.layout.fragment_schematic, container,
				false);
		
		
		current_context  = inflater.getContext();
		current_activity = this.getActivity();
		
		schematic = new Schematic(current_context);
		
		if (savedInstanceState != null)
		{
		}
		
		root_layout = (LinearLayout) rootView.findViewById(R.id.schematic_layout);
		root_layout.addView(schematic);
		
		value_entry = (EditText) rootView.findViewById(R.id.value_entry);
		init_listeners();
		return rootView;
	}
	
	private void init_listeners()
	{
		value_entry.setOnKeyListener(this);
	}
	
	private void close_key_board(EditText edit_text)
	{
		InputMethodManager imm = (InputMethodManager) current_activity.getSystemService(current_activity.INPUT_METHOD_SERVICE);

		imm.hideSoftInputFromWindow(edit_text.getWindowToken(), 0);
	}

	public void onSaveInstanceState(Bundle state)
	{
	}
	
	class Schematic extends SurfaceView
	{
		Context current_context;
		
		final int INVALID_PNTR_ID = -1;
		int pntr1 = INVALID_PNTR_ID, pntr2 = INVALID_PNTR_ID;
		
		float i_x, i_y, f_x, f_y;
		float pntr2_x, pntr2_y;
		
		ComponentsView parallel, series;
		
		public Schematic(Context context) 
		{
			super(context);
			this.setBackgroundColor(Color.CYAN);
			
			/*TEST ComponentsView */
			
			//Serial number
			LinkedList<Integer> serial = new LinkedList<Integer>();
			serial.add(420);
			
			LinkedList<Component> raw2 = new LinkedList<Component>();
			
			raw2.add(new Component(456));
			raw2.add(new Component(789));
			
			ComponentsView series_test = new ComponentsView(serial, raw2, Components.SUM, Components.RESISTOR);
			
			
			LinkedList<Component> raw = new LinkedList<Component>();
			
			raw.add(series_test);
			raw.add(new Component(567));
			raw.add(new Component(890));
			/*
			raw.add(new Component(1230));
			raw.add(new Component(56700));
			raw.add(new Component(890000));
			*/
			
			parallel = new ComponentsView(serial, raw, Components.INVERSE_INVERSE_SUM, Components.RESISTOR);
			
			LinkedList<Component> s = new LinkedList<Component>();
			
			s.add(new Component(123));
			//s.add(new Component(456));
			//s.add(new Component(789));
			s.add(parallel);
			s.add(new Component(123000));
			//s.add(new Component(456000));
			//s.add(new Component(789000));
			
			series   = new ComponentsView(serial, s, Components.SUM, Components.RESISTOR);
			
			invalidate();
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
			int action = me.getActionMasked();
			
			switch(action)
			{
			case MotionEvent.ACTION_DOWN:
				i_x = me.getX();
				i_y = me.getY();
				pntr1 = me.getPointerId(0);
				break;
					
			case MotionEvent.ACTION_MOVE:
				if (pntr1 != INVALID_PNTR_ID)
				{
					f_x = me.getX(pntr1);
					f_y = me.getY(pntr1);
					float d = distance(i_x, i_y, f_x, f_y);
					if (d > 15)
					{
						
						//parallel.setXY(me.getX(pntr1), me.getY(pntr1) ) ;
						Complex move = new Complex(me.getX(pntr1), me.getY(pntr1));
						
						Log.d("!!!", "STARTING!");
						/*
						Complex xy = series.get_preferred_grab_point(move, this.getWidth(), this.getHeight(), 
								0, 0, false, false);
						*/
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
					float dy = pntr2_y - i_y;
					float dx = pntr2_x - i_x;
					double angle = Math.atan(dy/dx);
						
					if (Math.abs(parallel.rotation.phase() - angle) > 0.02)
					{
						if (dx < 0) angle += Math.PI;
							
						//parallel.setAngle(angle);
						//Log.d(tag, Double.toString(angle));
					}
				}
				break;
				
				case MotionEvent.ACTION_UP:
					pntr1 = INVALID_PNTR_ID;
					break;
				case MotionEvent.ACTION_POINTER_DOWN:
					pntr2 = me.getPointerId(me.getActionIndex());
					//Log.d(tag, "Pointer 2 Down");
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
			//parallel.draw(c);
			series.draw(c);
		}
		
	}
	/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~LISTENER IMPLEMENTATION~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
	@Override
	public boolean onKey(View v, int new_key, KeyEvent event) {
		//Log.d(v.toString() , "Key Code " + new_key);
		//Log.d(v.toString(), event.toString() + " " + event.getAction());
		
		//Log.d(tag, Integer.toString(new_key));
		if (new_key != 66) return false;
		if (event.getAction() == KeyEvent.ACTION_UP)
		{
		   this.close_key_board(value_entry);
		   String value = value_entry.getText().toString();
		   try
		   {
			   Double d = Double.valueOf(value);
			   //schematic.resistor.body.setValue(d);
			   schematic.invalidate();
		   }
		   catch (Exception e)
		   {
			   value_entry.setText("Invalid Entry");
		   }
		   return true;
		}
		
		return false;
	}

}
