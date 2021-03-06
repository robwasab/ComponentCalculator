package ee.tools.componentcalculator;

import java.util.LinkedList;

import ee.tools.componentcalculator.components_toolbox.ComponentViewInterface;
import ee.tools.componentcalculator.components_toolbox.ComponentsView;
import ee.tools.model.Component;
import ee.tools.model.Components;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.os.Bundle;

/*
 * This Must Keep Track of the Master Serial Number!
 */
public class SchematicFragment extends Fragment implements BackPressedListener
{
	Context current_context;
	Activity current_activity;
	LinearLayout root_layout;
	String tag = "SchematicFragment";
	
	LinearLayout settings;
	
	Schematic schematic;
	
	TextView fit, reset, value;
	private CalculatorFragment calculator_fragment;
	private InventoryFragment inventory_fragment ;
	
	public SchematicFragment() 
	{ super(); }
	
	public void onActivityCreated(Bundle state)
	{
		super.onActivityCreated(state);
	}
	
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		super.onCreateView(inflater, container, savedInstanceState);
				
		current_context = this.getActivity();
		
		current_activity = this.getActivity();
		
		View rootView = inflater.inflate(R.layout.schematic_fragment, container, false);
				
		root_layout = (LinearLayout) rootView.findViewById(R.id.schematic_layout);
		
		settings = new LinearLayout(current_context);
		
		fit = (TextView) rootView.findViewById(R.id.fit_text_view);
		
		reset = (TextView) rootView.findViewById(R.id.reset_text_view);
		
		value = (TextView) rootView.findViewById(R.id.schematic_text_view_value);
		
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		settings.setLayoutParams(params);
				
		schematic = new Schematic(current_context, settings) 
		{
			public void setSeries(ComponentViewInterface cvi)
			{
				super.setSeries(cvi);
				
				if (schematic.getCurrent() != null)
					value.setText(ee.tools.model.EngNot.toEngNotation(schematic.getCurrent().getValue()));
			}
			
			public void invalidate()
			{
				super.invalidate();
				if (schematic != null && schematic.getCurrent() != null)
					value.setText(ee.tools.model.EngNot.toEngNotation(schematic.getCurrent().getValue()));				
			}
		};
		
		LinkedList<Integer> base_serial = new LinkedList<Integer>();
		base_serial.add(0);
		
		LinkedList<Component> series_1 = new LinkedList<Component>();
		series_1.add(new Component(123));
		series_1.add(new Component(456));
		series_1.add(new Component(789));
		
		Components series_11 = new Components(series_1, Components.SUM);
		
		LinkedList<Component> parallel_1 = new LinkedList<Component>();
		
		parallel_1.add(new Component(12300000));
		parallel_1.add(new Component(45600000));
		parallel_1.add(new Component(78900000));
		parallel_1.add(series_11);
		
		Components parallel_11 = new Components(parallel_1, Components.INVERSE_INVERSE_SUM);
		
		LinkedList<Component> small_series_ll = new LinkedList<Component>();
		
		small_series_ll.add(new Component(789));
		small_series_ll.add(parallel_11);
		small_series_ll.add(new Component(456));
		
		ComponentsView small_series = new ComponentsView(
				schematic,
				base_serial,
				small_series_ll,
				Components.SUM,
				Components.RESISTOR);
		
		LinkedList<Component> parallel_ll = new LinkedList<Component>();
	
		parallel_ll.add(new Component(1230000));
		parallel_ll.add(small_series);
		parallel_ll.add(new Component(567000));
		
		ComponentsView parallel = new ComponentsView(
				schematic,
				base_serial,
				parallel_ll,
				Components.INVERSE_INVERSE_SUM,
				Components.RESISTOR);
		
		LinkedList<Component> s = new LinkedList<Component>();
		s.add(parallel);
		s.add(new Component(123));
		s.add(new Component(47000));
		s.add(new Component(1000));
		
		//It is very important to start a ComponentsView with the master_serial
		//This serial number defines all of the children's serial numbers.
		//You cannot change this serial number after instantiation, it is immutable
		
		ComponentsView first_comp = new ComponentsView(
				schematic,
				base_serial,
				s,
				Components.SUM,
				Components.RESISTOR);
        /*
		LinkedList<Component> ll_s0 = new LinkedList<Component>();
		ll_s0.add(new Component(4E-3));
		ll_s0.add(new Component(5E-3));
		ll_s0.add(new Component(6E-3));
		ll_s0.add(new Component(7E-3));
	
		ComponentsView s0 = new ComponentsView(
				schematic,
				base_serial,
				ll_s0,
				Components.INVERSE_INVERSE_SUM,
				Components.CAPACITOR);
				
		LinkedList<Component> ll_p1 = new LinkedList<Component>();		
		ll_p1.add(new Component(1E-6));
		ll_p1.add(new Component(2E-6));
		ll_p1.add(s0);
		ll_p1.add(new Component(3E-6));
		
		ComponentsView p1 = new ComponentsView(
				schematic,
				base_serial,
				ll_p1,
				Components.SUM,
				Components.CAPACITOR);
		
		LinkedList<Component> ll_s1 = new LinkedList<Component>();
		
		ll_s1.add(new Component(.01E-6));
		ll_s1.add(new Component(.1E-6));
		ll_s1.add(new Component(1));
		ll_s1.add(p1);
		ll_s1.add(new Component(12E-12));
		
		ComponentsView first_comp = new ComponentsView(
				schematic,
				base_serial,
				ll_s1,
				Components.INVERSE_INVERSE_SUM,
				Components.CAPACITOR);
		*/
		schematic.setSeries(first_comp);
		
		if (savedInstanceState != null) 
		{
			schematic.restoreInstanceState(savedInstanceState);
		}
				
		root_layout.addView(settings);
		
		schematic.setLayoutParams(params);
		
		root_layout.addView(schematic);
		
		init_text_view_listeners();
		
		return rootView;
	}

	private void init_text_view_listeners() {
		fit.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (schematic.getCurrent() != null)
				{
					schematic.compress();
				}
			}	
		});
		
		reset.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (schematic.getCurrent() != null)
				{
					schematic.getCurrent().resetShrink();
					schematic.invalidate();
				}
			}	
		});	
	}

	public void setFragments(InventoryFragment iF, CalculatorFragment cF)
	{
		this.calculator_fragment = cF;
		this.inventory_fragment  = iF;
	}
	
	public void onSaveInstanceState(Bundle save)
	{
		super.onSaveInstanceState(save);
		schematic.saveInstanceState(save);
	}
	
	@Override
	public boolean backPressedAction() {
		return schematic.backPressedAction();
	}	
}
