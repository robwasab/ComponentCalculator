package ee.tools.componentcalculator;

import java.util.LinkedList;

import ee.tools.model.Component;
import ee.tools.model.Components;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.os.Bundle;

public class SchematicFragment extends Fragment
{
	Context current_context;
	Activity current_activity;
	LinearLayout root_layout;
	String tag = "SchematicFragment";

	LinearLayout settings;
	
	Schematic schematic;
	
	ComponentsView series;
	
	public SchematicFragment() { super(); }
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
		
		View rootView = inflater.inflate(R.layout.schematic_fragment, container, false);
		
		this.getActivity().getActionBar().setDisplayHomeAsUpEnabled(true);
		
		current_context  = this.getActivity();
		current_activity = this.getActivity();
		
		root_layout = (LinearLayout) rootView.findViewById(R.id.schematic_layout);
		
		settings = new LinearLayout(current_context);
		
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		settings.setLayoutParams(params);
				
		schematic = new Schematic(current_context, settings);
		
		if (this.series == null)
		{
			Log.d(tag, "Creating Default Series...");
			
			LinkedList<Component> small_series_ll = new LinkedList<Component>();
		
			small_series_ll.add(new Component(456));
			small_series_ll.add(new Component(789));
		
			LinkedList<Integer> default_serial = new LinkedList<Integer>();
			default_serial.add(0);
		
			ComponentsView small_series = new ComponentsView(
					schematic,
					default_serial,
					small_series_ll,
					Components.SUM,
					Components.RESISTOR);
		
			LinkedList<Component> parallel_ll = new LinkedList<Component>();
		
			parallel_ll.add(small_series);
			parallel_ll.add(new Component(1230000));
			parallel_ll.add(new Component(567000));
		
			ComponentsView parallel = new ComponentsView(
					schematic,
					default_serial,
					parallel_ll,
					Components.INVERSE_INVERSE_SUM,
					Components.RESISTOR);
		
			LinkedList<Component> s = new LinkedList<Component>();
		
			s.add(new Component(123));
			s.add(parallel);
			s.add(new Component(47000));
			s.add(new Component(1000));
		
			//It is very important to start a ComponentsView with the master_serial
			//This serial number defines all of the children's serial numbers.
			//You cannot change this serial number after instantiation, it is immutable
		
			this.series = new ComponentsView(
					schematic,
					default_serial,
					s,
					Components.SUM,
					Components.RESISTOR);
		}
		else
		{
			Log.d(tag, "SchematicFragment was previously set with: " + series.toString());
			//This means that you are a detail view, enable back navigation
			this.current_activity.getActionBar().setDisplayHomeAsUpEnabled(true);
		}
		schematic.setSeries(series);
		
		root_layout.addView(settings);
		
		root_layout.addView(schematic);
		
		if (savedInstanceState != null)
		{
			schematic.restoreInstanceState(savedInstanceState);
		}
		
		schematic.series.setAngle(0);
		schematic.series.setXY(0, 0);
		schematic.invalidate();
		return rootView;
	}
	
	public void setComponentsView(ComponentsView series)
	{
		this.series = series;
		this.series.setCollapse(false);
		if (schematic != null)
		{
			schematic.setSeries(series);
			schematic.series.setAngle(0);
			schematic.series.setXY(0, 0);
			schematic.invalidate();
		}
	}
	
	public void onSaveInstanceState(Bundle state)
	{
		super.onSaveInstanceState(state);
		schematic.saveInstanceState(state);
	}	
}
